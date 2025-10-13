import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Message } from '@/views/ai/chat/types'

export interface ChatSession {
  id: string
  title: string
  createdAt: number
  modelId: string | null
  messageCount: number
  archived?: boolean
}

function generateUniqueTimestampId(existingIds: Set<string>): string {
  let id = Date.now().toString()
  while (existingIds.has(id)) {
    id = (Number(id) + 1).toString()
  }
  return id
}

function summarizeTitleFromContent(content: string): string {
  const clean = (content || '').replace(/\s+/g, ' ').trim()
  if (!clean) return '新会话'
  return clean.slice(0, 20)
}

const SESSIONS_KEY = 'chat:sessions'
const ACTIVE_KEY = 'chat:active'
const MESSAGES_KEY_PREFIX = 'chat:messages:'

export const useChatStore = defineStore('chat', () => {
  const sessions = ref<ChatSession[]>([])
  const activeSessionId = ref<string | null>(null)
  const messagesBySession = ref<Record<string, Message[]>>({})

  const activeSession = computed(() => sessions.value.find(s => s.id === activeSessionId.value) || null)
  const activeMessages = computed(() => activeSessionId.value ? (messagesBySession.value[activeSessionId.value] || []) : [])
  const latestSession = computed(() => {
    if (sessions.value.length === 0) return null
    return [...sessions.value].sort((a, b) => b.createdAt - a.createdAt)[0]
  })

  const persistSessions = () => {
    const payload = JSON.stringify(sessions.value)
    localStorage.setItem(SESSIONS_KEY, payload)
    if (activeSessionId.value) {
      localStorage.setItem(ACTIVE_KEY, activeSessionId.value)
    } else {
      localStorage.removeItem(ACTIVE_KEY)
    }
  }

  const persistMessages = (sessionId: string) => {
    const key = MESSAGES_KEY_PREFIX + sessionId
    const payload = JSON.stringify(messagesBySession.value[sessionId] || [])
    localStorage.setItem(key, payload)
  }

  const loadMessages = (sessionId: string) => {
    if (messagesBySession.value[sessionId]) return
    const key = MESSAGES_KEY_PREFIX + sessionId
    try {
      const raw = localStorage.getItem(key)
      const messages: Message[] = raw ? JSON.parse(raw) : []
      
      // 检测并修复未完成的AI消息
      let hasIncompleteAI = false
      for (let i = 0; i < messages.length; i++) {
        const msg = messages[i]
        // 如果是AI消息且内容为空（或只有空白字符），且没有推理内容，认为是未完成的消息
        if (!msg.isUser && 
            (!msg.content || msg.content.trim() === '') && 
            (!msg.reasoningContent || msg.reasoningContent.trim() === '')) {
          msg.content = '[消息异常中断]'
          hasIncompleteAI = true
        }
      }
      
      messagesBySession.value[sessionId] = messages
      
      // 修复后立即保存
      if (hasIncompleteAI) {
        persistMessages(sessionId)
      }
    } catch {
      messagesBySession.value[sessionId] = []
    }
  }

  const rehydrate = () => {
    try {
      const raw = localStorage.getItem(SESSIONS_KEY)
      sessions.value = raw ? JSON.parse(raw) : []
    } catch {
      sessions.value = []
    }

    try {
      const rawActive = localStorage.getItem(ACTIVE_KEY)
      activeSessionId.value = rawActive || null
    } catch {
      activeSessionId.value = null
    }

    // 回填缺失的 messageCount（兼容旧数据）
    const ids = new Set<string>()
    sessions.value.forEach(s => {
      ids.add(s.id)
      if (typeof s.messageCount !== 'number') {
        loadMessages(s.id)
        s.messageCount = (messagesBySession.value[s.id] || []).length
      }
    })
    
    // 加载活跃会话消息
    if (activeSessionId.value && sessions.value.find(s => s.id === activeSessionId.value)) {
      loadMessages(activeSessionId.value)
    }

    // 清理重复 id（极端情况）
    if (ids.size !== sessions.value.length) {
      const unique: Record<string, ChatSession> = {}
      sessions.value.forEach(s => { unique[s.id] = s })
      sessions.value = Object.values(unique).sort((a, b) => a.createdAt - b.createdAt)
      persistSessions()
    }

    // 如果 active 不存在则清空
    if (activeSessionId.value && !sessions.value.find(s => s.id === activeSessionId.value)) {
      activeSessionId.value = null
      localStorage.removeItem(ACTIVE_KEY)
    }
  }

  const createSession = (initialModelId?: string | null): string => {
    const idSet = new Set(sessions.value.map(s => s.id))
    const id = generateUniqueTimestampId(idSet)
    const createdAt = Date.now()
    const newSession: ChatSession = {
      id,
      title: '新会话',
      createdAt,
      modelId: initialModelId ?? null,
      messageCount: 0
    }
    sessions.value.push(newSession)
    messagesBySession.value[id] = []
    persistSessions()
    persistMessages(id)
    return id
  }

  const setActiveSession = (id: string) => {
    if (!sessions.value.find(s => s.id === id)) return
    activeSessionId.value = id
    loadMessages(id)
    localStorage.setItem(ACTIVE_KEY, id)
  }

  const renameSession = (id: string, title: string) => {
    const s = sessions.value.find(x => x.id === id)
    if (!s) return
    s.title = (title || '').trim() || s.title
    persistSessions()
  }

  const deleteSession = (id: string, onBeforeDelete?: () => void) => {
    // 删除活跃会话前执行清理回调
    if (activeSessionId.value === id && onBeforeDelete) {
      onBeforeDelete()
    }
    
    sessions.value = sessions.value.filter(s => s.id !== id)
    delete messagesBySession.value[id]
    localStorage.removeItem(MESSAGES_KEY_PREFIX + id)

    if (activeSessionId.value === id) {
      const next = sessions.value.length > 0 ? sessions.value[sessions.value.length - 1].id : null
      activeSessionId.value = next
      if (next) localStorage.setItem(ACTIVE_KEY, next)
      else localStorage.removeItem(ACTIVE_KEY)
    }
    persistSessions()
  }

  const appendMessage = (message: Message & { sessionId?: string }) => {
    const sid = message.sessionId || activeSessionId.value
    if (!sid) return
    if (!messagesBySession.value[sid]) messagesBySession.value[sid] = []
    messagesBySession.value[sid].push({ ...message, id: message.id || Date.now().toString() })

    const s = sessions.value.find(x => x.id === sid)
    if (s) {
      s.messageCount = (messagesBySession.value[sid] || []).length
      if (message.isUser && s.title.startsWith('新会话')) {
        s.title = summarizeTitleFromContent(message.content)
      }
      persistSessions()
    }
    persistMessages(sid)
  }

  const patchLastAssistantMessage = (sessionId: string, partial: Partial<Message>) => {
    const list = messagesBySession.value[sessionId]
    if (!list || list.length === 0) return
    for (let i = list.length - 1; i >= 0; i--) {
      if (!list[i].isUser) {
        const merged: Message = { ...list[i], ...partial }
        list[i] = merged
        break
      }
    }
    persistMessages(sessionId)
  }

  const clearSessionMessages = (id: string) => {
    messagesBySession.value[id] = []
    const s = sessions.value.find(x => x.id === id)
    if (s) {
      s.messageCount = 0
      persistSessions()
    }
    persistMessages(id)
  }

  const updateActiveSessionModel = (modelId: string | null) => {
    const s = activeSession.value
    if (!s) return
    s.modelId = modelId
    persistSessions()
  }

  const ensureActiveSessionOnEnter = (initialModelId?: string | null) => {
    rehydrate()

    // 保持已有有效 active
    if (activeSession.value) {
      loadMessages(activeSession.value.id)
      return activeSession.value.id
    }

    if (sessions.value.length === 0) {
      const id = createSession(initialModelId)
      setActiveSession(id)
      return id
    }

    const latest = latestSession.value!
    if ((latest?.messageCount || 0) === 0) {
      setActiveSession(latest.id)
      return latest.id
    }

    const id = createSession(initialModelId)
    setActiveSession(id)
    return id
  }

  return {
    // state
    sessions,
    activeSessionId,
    messagesBySession,

    // getters
    activeSession,
    activeMessages,
    latestSession,

    // actions
    rehydrate,
    ensureActiveSessionOnEnter,
    createSession,
    setActiveSession,
    renameSession,
    deleteSession,
    appendMessage,
    patchLastAssistantMessage,
    clearSessionMessages,
    updateActiveSessionModel
  }
}) 