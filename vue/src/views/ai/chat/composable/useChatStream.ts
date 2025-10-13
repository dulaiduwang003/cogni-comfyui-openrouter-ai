import { ref, computed, watch, onMounted, type Ref } from 'vue'
import type {  ModelItem } from '@/api/chat/types'
import { chatApi } from '@/api/chat/chat'
import type { Message } from '../types'
import { useChatStore } from '@/stores/modules/chat'

export function useChatStream(options?: { enableWebSearch?: Ref<boolean>; enableImageGen?: Ref<boolean> }) {
  const chatStore = useChatStore()
  const messages = computed(() => chatStore.activeMessages)
  const isTyping = ref(false)
  const showReasoning = ref(true)
  const selectedModel = ref<ModelItem | null>(null)
  const isUserStopped = ref(false) // 用户是否主动停止了回复

  const sseConnection = ref<EventSource | null>(null)
  const hasReceivedData = ref(false) // 本次流是否已经收到任何数据
  const sessionId = computed(() => chatStore.activeSession?.id || '')
  const isReplyCompleted = ref(false)
  const currentStreamingAiMessage = ref<Message | null>(null)

  // 将服务端可能发送的“全量快照”与“增量片段”做去重合并
  const mergeStreamText = (previous: string, incoming: string): string => {
    const prev = previous || ''
    const inc = incoming || ''
    if (!prev) return inc
    if (!inc) return prev
    if (inc === prev) return prev

    const normalize = (s: string) => (s || '')
      .replace(/[\s\u00A0]+/g, ' ')
      .replace(/[，。,．\.！!？\?；;：:、]/g, '')
      .trim()

    const prevNorm = normalize(prev)
    const incNorm = normalize(inc)
    if (prevNorm === incNorm) return inc.length >= prev.length ? inc : prev

    if (inc.startsWith(prev)) return inc
    if (prev.startsWith(inc)) return prev

    // 若任一包含另一方，采用更长者
    if (inc.includes(prev)) return inc
    if (prev.includes(inc)) return prev

    // 计算共同前缀/后缀长度
    const commonPrefixLen = (a: string, b: string) => {
      const max = Math.min(a.length, b.length)
      let i = 0
      while (i < max && a[i] === b[i]) i++
      return i
    }
    const commonSuffixLen = (a: string, b: string) => {
      const max = Math.min(a.length, b.length)
      let i = 0
      while (i < max && a[a.length - 1 - i] === b[b.length - 1 - i]) i++
      return i
    }

    const minLen = Math.min(prev.length, inc.length)
    const pref = commonPrefixLen(prev, inc)
    const suff = commonSuffixLen(prev, inc)

    // 相似度高时采用更长者
    if ((pref + suff) >= minLen * 0.8) {
      return inc.length >= prev.length ? inc : prev
    }

    // 重叠拼接并去重
    const dedupeJoin = (a: string, b: string): string => {
      const joined = a + b
      // 以空行分段，去除重复段落
      const parts = joined.split(/\n{2,}/)
      const seen = new Set<string>()
      const out: string[] = []
      for (const p of parts) {
        const norm = p.replace(/[\s\u00A0]+/g, ' ').trim()
        if (!norm) continue
        if (seen.has(norm)) continue
        seen.add(norm)
        out.push(p)
      }
      return out.join('\n\n')
    }

    const maxOverlap = Math.min(prev.length, inc.length)
    for (let i = maxOverlap; i > 0; i--) {
      if (prev.endsWith(inc.slice(0, i))) {
        return dedupeJoin(prev, inc.slice(i))
      }
    }
    return dedupeJoin(prev, inc)
  }



  // 简单的模型初始化函数
  const initDefaultModel = async () => {
    if (selectedModel.value) return // 已有模型就不重复获取
    
    try {
      const defaultModel = await chatApi.reqGetDefaultModel()
      if (defaultModel?.id) {
        selectedModel.value = defaultModel
        // 如果有活跃会话且没有模型，更新会话的模型
        if (chatStore.activeSessionId && !chatStore.activeSession?.modelId) {
          chatStore.updateActiveSessionModel(defaultModel.id)
        }
      }
    } catch (error) {
      console.warn('获取默认模型失败:', error)
    }
  }

  const handleModelSelected = (model: ModelItem | null) => {
    selectedModel.value = model
    chatStore.updateActiveSessionModel(model ? model.id : null)
    closeSseConnection()
  }

  const setupSseConnection = () => {
    const activeId = chatStore.ensureActiveSessionOnEnter(null)
    const modelId = selectedModel.value?.id || null
    if (!activeId) return

    try {
      sseConnection.value = chatApi.reqChatStream({
        sessionId: activeId,
        // omit modelId when undefined; backend defaults to Auto
        ...(modelId ? { modelId } : {} as any),
        enableWebSearch: options?.enableWebSearch?.value,
        generateImages: options?.enableImageGen?.value
      })

      hasReceivedData.value = false
      isReplyCompleted.value = false

      sseConnection.value.onopen = () => {}

      sseConnection.value.addEventListener('reasoning', (event) => {
        try {
          const data = JSON.parse(event.data)
          hasReceivedData.value = true
          handleSseEventMessage({ type: 'reasoning', data }, data)
        } catch {}
      })

      sseConnection.value.addEventListener('citations', (event) => {
        try {
          const data = JSON.parse(event.data)
          hasReceivedData.value = true
          handleSseEventMessage({ type: 'citations', data }, data)
        } catch {}
      })

      // 接收图片事件
      sseConnection.value.addEventListener('images', (event) => {
        try {
          const data = JSON.parse(event.data)
          hasReceivedData.value = true
          handleSseEventMessage({ type: 'images', data }, data)
        } catch {}
      })

      sseConnection.value.addEventListener('done', (event: MessageEvent) => {
        try {
          const data = JSON.parse(event.data)
          hasReceivedData.value = true
          handleSseEventMessage({ type: 'done', data }, data)
        } catch {
          const raw = typeof event.data === 'string' ? event.data.trim() : ''
          hasReceivedData.value = true
          handleSseEventMessage({ type: 'done', data: raw && raw !== '[DONE]' ? { content: raw } : {} }, raw && raw !== '[DONE]' ? { content: raw } : {})
        }
      })

      // 监听服务端自定义 'error' 事件（与网络级 onerror 区分）
      sseConnection.value.addEventListener('error', (event: MessageEvent) => {
        const hasPayload = typeof (event as any).data !== 'undefined' && (event as any).data !== null
        if (!hasPayload) {
          // 没有 data 的 error 通常是网络级错误，交给 onerror 统一处理
          return
        }
        try {
          const data = JSON.parse((event as any).data)
          hasReceivedData.value = true
          handleSseEventMessage({ type: 'error', data }, data)
        } catch {
          const raw = typeof (event as any).data === 'string' ? String((event as any).data).trim() : ''
          hasReceivedData.value = true
          handleSseEventMessage({ type: 'error', data: raw ? { error: raw } : { error: '未知错误' } }, raw || '未知错误')
        }
      })

      // 连接错误（网络/中断）统一用 onerror 处理

      sseConnection.value.onmessage = (event) => {
        try {
          if (event.data === '[DONE]') {
            hasReceivedData.value = true
            isTyping.value = false
            // 等待后端发送 'done' 事件
            return
          }

          const data = JSON.parse(event.data)
          hasReceivedData.value = true
          let eventType = 'message'
          const hasErrorPayload = (
            (typeof data?.error === 'string' && data.error.trim().length > 0) ||
            (data?.error && typeof data.error === 'object') ||
            data?.type === 'error'
          )
          if (hasErrorPayload) {
            eventType = 'error'
          } else if (data?.type === 'images' || Array.isArray(data?.images)) {
            eventType = 'images'
          } else if (data?.type === 'citations' || Array.isArray(data?.citations)) {
            eventType = 'citations'
          } else if (data.reasoning_content && !data.content) {
            eventType = 'reasoning'
          } else if (data.content || data.images || data.citations) {
            if (data.content === '[DONE]') {
              eventType = 'done'
            } else {
              eventType = 'message'
            }
          }
          handleSseEventMessage({ type: eventType, data }, data)
        } catch {
          if (typeof event.data === 'string' && event.data.trim()) {
            hasReceivedData.value = true
            handleSseMessage({ content: event.data, type: 'text' })
          }
        }
      }

      sseConnection.value.onerror = () => {
        // 如果之前已经收到任何数据，则视为正常结束，不展示错误
        if (hasReceivedData.value || isReplyCompleted.value || sseConnection.value?.readyState === EventSource.CLOSED) {
          isTyping.value = false
          isReplyCompleted.value = true
          setTimeout(() => closeSseConnection(), 50)
          return
        }
        const errorMessage = '与服务器的连接中断。这可能是因为服务器端发生错误，返回了非预期的“text/plain”响应类型，而不是流式数据“text/event-stream”。请检查后台日志以获取详细信息。'
        handleSseEventMessage(
          { type: 'error', data: { error: errorMessage } },
          { error: errorMessage }
        )
      }

      sseConnection.value.addEventListener('close', () => {
        isTyping.value = false
      })
    } catch {
      isTyping.value = false
      handleSseEventMessage(
        { type: 'error', data: { error: '无法建立 SSE 连接，请重试。' } },
        { error: '无法建立 SSE 连接，请重试。' }
      )
    }
  }

  const handleSseMessage = (data: any) => {
    if (data.type === 'start') {
      if (!currentStreamingAiMessage.value) {
        isTyping.value = true
        const newAi: Message = {
          id: Date.now().toString(),
          content: '',
          isUser: false,
          timestamp: Date.now(),
          aiModel: selectedModel.value?.name || '',
          aiModelIcon: selectedModel.value?.icon,
        }
        chatStore.appendMessage(newAi)
        currentStreamingAiMessage.value = newAi
      }
    } else if (data.type === 'content' || data.type === 'text') {
      const sid = chatStore.activeSessionId as string
      const list = chatStore.messagesBySession[sid] || []
      for (let i = list.length - 1; i >= 0; i--) {
        if (!list[i].isUser) {
          const prev = list[i].content || ''
          const merged = mergeStreamText(prev, (data.content || data.text || ''))
          chatStore.patchLastAssistantMessage(sid, { content: merged })
          break
        }
      }
    } else if (data.type === 'end') {
      isTyping.value = false
    } else if (data.type === 'error') {
      isTyping.value = false
    }
  }

  const handleSseEventMessage = (eventInfo: { type: string; data: any }, data: any) => {
    let currentAiMessage = currentStreamingAiMessage.value
    const sid = chatStore.activeSessionId as string

    // Ensure an AI message exists when the first chunk arrives
    const shouldCreateAiMessage =
      eventInfo.type === 'reasoning' ||
      eventInfo.type === 'message' ||
      eventInfo.type === 'citations' ||
      eventInfo.type === 'error' ||
      eventInfo.type === 'images' ||
      (eventInfo.type === 'done' && (Boolean(data?.content) || Array.isArray(data?.images) || Array.isArray(data?.citations)))
    if (!currentAiMessage && shouldCreateAiMessage) {
      const newAi: Message = {
        id: Date.now().toString(),
        content: '',
        isUser: false,
        timestamp: Date.now(),
        aiModel: selectedModel.value?.name || '',
        aiModelIcon: selectedModel.value?.icon,
        reasoningContent: ''
      }
      chatStore.appendMessage(newAi)
      currentStreamingAiMessage.value = newAi
      isTyping.value = true
    } else if (!currentAiMessage) {
      return
    }

    // If done payload also includes images/citations/content, treat them here
    if (eventInfo.type === 'done') {
      if (Array.isArray(data?.images) && data.images.length) {
        handleSseEventMessage({ type: 'images', data }, data)
      }
      if (Array.isArray(data?.citations) && data.citations.length) {
        handleSseEventMessage({ type: 'citations', data }, data)
      }
      if (data?.content) {
        handleSseEventMessage({ type: 'message', data }, data)
      }
    }

    if (eventInfo.type === 'reasoning') {
      if (data.reasoning_content) {
        const list = chatStore.messagesBySession[sid] || []
        for (let i = list.length - 1; i >= 0; i--) {
          if (!list[i].isUser) {
            const prev = list[i].reasoningContent || ''
            const merged = mergeStreamText(prev, data.reasoning_content)
            chatStore.patchLastAssistantMessage(sid, { reasoningContent: merged })
            break
          }
        }
      }
    } else if (eventInfo.type === 'message') {
      if (data.content) {
        const list = chatStore.messagesBySession[sid] || []
        for (let i = list.length - 1; i >= 0; i--) {
          if (!list[i].isUser) {
            const prev = list[i].content || ''
            const merged = mergeStreamText(prev, data.content)
            chatStore.patchLastAssistantMessage(sid, { content: merged })
            break
          }
        }
      }
    } else if (eventInfo.type === 'citations') {
      try {
        const rawList = Array.isArray(data) ? data : (Array.isArray(data?.citations) ? data.citations : [])
        const normalized = rawList.map((item: any) => {
          const c = item?.url_citation || item
          return {
            title: c?.title || '',
            url: c?.url || '',
            content: c?.content || '',
            startIndex: c?.start_index,
            endIndex: c?.end_index
          }
        })
        if (normalized.length > 0) {
          chatStore.patchLastAssistantMessage(sid, { citations: normalized } as any)
        }
      } catch {}
    } else if (eventInfo.type === 'images') {
      try {
        const rawList = Array.isArray(data) ? data : (Array.isArray(data?.images) ? data.images : [])
        const urls: string[] = []
        rawList.forEach((item: any) => {
          const u = typeof item === 'string' ? item : (item?.image_url?.url || item?.url || '')
          if (!u) return
          urls.push(u)
        })
        if (urls.length > 0) {
          const list = chatStore.messagesBySession[sid] || []
          for (let i = list.length - 1; i >= 0; i--) {
            if (!list[i].isUser) {
              const prev = list[i].imageUrls || []
              const merged = Array.from(new Set([...prev, ...urls]))
              chatStore.patchLastAssistantMessage(sid, { imageUrls: merged })
              break
            }
          }
        }
      } catch {}
    } else if (eventInfo.type === 'done') {
      const hasAnyPayload = Boolean(data?.error) || Boolean(data?.content) || (Array.isArray(data?.images) && data.images.length > 0) || (Array.isArray(data?.citations) && data.citations.length > 0)

      if (!hasAnyPayload) {
        // 空 done 事件，忽略
        isTyping.value = false
        return
      }

      isTyping.value = false
      isReplyCompleted.value = true

      const list = chatStore.messagesBySession[sid] || []
      // 定位最后一条助手消息
      let idx = -1
      for (let i = list.length - 1; i >= 0; i--) {
        if (!list[i].isUser) { idx = i; break }
      }

      if (idx >= 0) {
        // 用后端快照完全替换
        const cleanContent = (data.content || '').replace(/^\n+/, '')
        const next = {
          ...list[idx],
          content: data.content ? cleanContent : (list[idx].content || ''),
          reasoningContent: typeof data.reasoning_content === 'string' ? data.reasoning_content : list[idx].reasoningContent,
          imageUrls: Array.isArray(data.images) ? data.images.map((it: any) => typeof it === 'string' ? it : (it?.image_url?.url || it?.url || '')).filter(Boolean) : list[idx].imageUrls,
          citations: Array.isArray(data.citations) ? data.citations.map((c: any) => {
            // 处理嵌套的 url_citation 结构
            const citation = c?.url_citation || c;
            return {
              title: citation?.title || c?.title || '',
              url: citation?.url || c?.url || '',
              content: citation?.content || c?.content || '',
              startIndex: citation?.start_index || c?.start_index,
              endIndex: citation?.end_index || c?.end_index
            };
          }) : (list[idx] as any).citations
        } as any

        chatStore.patchLastAssistantMessage(sid, next)
      }

      if (data.error) {
        const raw = data.error?.message || data.error
        const msg = typeof raw === 'string' ? raw : JSON.stringify(raw)
        if (idx >= 0) {
          const after = (chatStore.messagesBySession[sid] || [])[idx]
          chatStore.patchLastAssistantMessage(sid, { content: `${(after.content || '').trim()}\n\n[错误] ${msg}` })
        }
      }

      currentStreamingAiMessage.value = null
      setTimeout(() => {
        if (sseConnection.value) {
          sseConnection.value.onerror = null
        }
        closeSseConnection()
      }, 200)
    } else if (eventInfo.type === 'error') {
      isTyping.value = false
      isReplyCompleted.value = true
      const raw = data?.error ?? data?.message ?? data
      const msg = typeof raw === 'string' && raw.trim() ? raw.trim() : '抱歉，处理您的请求时出现错误，请稍后重试。'
      const list = chatStore.messagesBySession[sid] || []
      for (let i = list.length - 1; i >= 0; i--) {
        if (!list[i].isUser) {
          const content = (list[i].content || '').trim()
          if (!content) {
            chatStore.patchLastAssistantMessage(sid, { content: `抱歉，发生错误：${msg}` })
          } else {
            chatStore.patchLastAssistantMessage(sid, { content: `${list[i].content}\n\n[错误] ${msg}` })
          }
          break
        }
      }
      currentStreamingAiMessage.value = null
      setTimeout(() => {
        if (sseConnection.value) {
          sseConnection.value.onerror = null
        }
        closeSseConnection()
      }, 100)
    }

    if (data.type === 'start') {
      isTyping.value = true
    } else if (data.type === 'end' || data.type === 'error') {
      isTyping.value = false
    }
  }

  const closeSseConnection = () => {
    if (sseConnection.value && sseConnection.value.readyState !== EventSource.CLOSED) {
      sseConnection.value.onmessage = null
      sseConnection.value.onerror = null
      sseConnection.value.onopen = null
      sseConnection.value.removeEventListener('reasoning', () => {})
      sseConnection.value.removeEventListener('citations', () => {})
      sseConnection.value.removeEventListener('done', () => {})
      sseConnection.value.removeEventListener('error', () => {})
      sseConnection.value.removeEventListener('close', () => {})
      sseConnection.value.close()
      sseConnection.value = null
      currentStreamingAiMessage.value = null
      hasReceivedData.value = false
    } else if (sseConnection.value?.readyState === EventSource.CLOSED) {
      sseConnection.value = null
      currentStreamingAiMessage.value = null
      hasReceivedData.value = false
    }
  }

  // 用户主动停止回复
  const stopReply = () => {
    if (isTyping.value && currentStreamingAiMessage.value) {
      isUserStopped.value = true
      
      // 在当前AI消息末尾添加中断标识
      const currentMessage = currentStreamingAiMessage.value
      if (currentMessage && sessionId.value) {
        const updatedContent = currentMessage.content + '\n\n[用户中断了回复]'
        chatStore.patchLastAssistantMessage(sessionId.value, {
          content: updatedContent,
          timestamp: Date.now()
        })
      }
      
      // 关闭SSE连接
      closeSseConnection()
      isTyping.value = false
      isReplyCompleted.value = true
      
      // 重置状态
      setTimeout(() => {
        isUserStopped.value = false
      }, 100)
    }
  }

  const handleSendMessage = async (
    contentOrPayload: string | { content: string; imageUrls?: string[]; pdfFiles?: { url: string; filename: string }[]; audioFiles?: { url: string }[] },
    currentUser?: string,
    addUserMessage: boolean = true
  ) => {

    isReplyCompleted.value = false
    currentStreamingAiMessage.value = null
    hasReceivedData.value = false

    const sid = chatStore.ensureActiveSessionOnEnter(null)
    
    // 只有在需要添加用户消息时才添加
    if (addUserMessage) {
      const textContent = typeof contentOrPayload === 'string' ? contentOrPayload : contentOrPayload.content
      const payloadObj = typeof contentOrPayload === 'string' ? undefined : contentOrPayload
      const userMessage: Message = {
        id: Date.now().toString(),
        content: textContent,
        isUser: true,
        timestamp: Date.now(),
        userName: currentUser,
        imageUrls: payloadObj?.imageUrls,
        pdfFiles: payloadObj?.pdfFiles,
        audioFiles: payloadObj?.audioFiles
      }
      chatStore.appendMessage({ ...userMessage, sessionId: sid })
    }

    // 插入AI占位消息
    const aiPlaceholder: Message = {
      id: (Date.now() + 1).toString(),
      content: '',
      isUser: false,
      timestamp: Date.now(),
      aiModel: selectedModel.value?.name || '',
      aiModelIcon: selectedModel.value?.icon,
      reasoningContent: ''
    }
    chatStore.appendMessage(aiPlaceholder)
    currentStreamingAiMessage.value = aiPlaceholder
    isTyping.value = true

    try {
      if (typeof contentOrPayload === 'string') {
        await chatApi.reqSubmitMessage({
          sessionId: sid,
          text: contentOrPayload
        })
      } else {
        await chatApi.reqSubmitMessage({
          sessionId: sid,
          text: contentOrPayload.content,
          imageUrls: contentOrPayload.imageUrls,
          pdfFiles: contentOrPayload.pdfFiles,
          audioFiles: contentOrPayload.audioFiles
        })
      }

      if (!sseConnection.value) {
        setupSseConnection()
      }
    } catch {
      // 请求失败时覆盖占位消息为错误提示
      chatStore.patchLastAssistantMessage(sid, {
        content: '抱歉，消息发送失败，请稍后重试。'
      })
      isTyping.value = false
      isReplyCompleted.value = true
      currentStreamingAiMessage.value = null
    }
  }

  // 监听会话切换：如果需要就初始化模型
  watch(() => chatStore.activeSessionId, () => {
    initDefaultModel()
  })

  onMounted(async () => {
    // 逻辑已移至 watch 中 immediate: true
  })

  return {
    messages,
    isTyping,
    showReasoning,
    selectedModel,

    handleModelSelected,

    closeSseConnection,
    handleSendMessage,
    stopReply,
  }
} 