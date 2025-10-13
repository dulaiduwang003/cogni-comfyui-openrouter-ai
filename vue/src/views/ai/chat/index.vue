<template>
<div class="chat-root page-container">
  <div class="page" :class="{ 'sidebar-collapsed': sidebarCollapsed }">
    <ChatSidebar 
      class="sidebar-enter"
      :selected-model="selectedModel"
      :collapsed="sidebarCollapsed"
      :is-typing="isTyping"
      @toggle-sidebar="handleSidebarToggle" 
      @model-selected="selectModel"
      @chat-delete="handleChatDelete"
      @interrupt-ai="handleInterruptAi"
    />

    <main class="content content-enter" :class="{ 'has-messages': messages.length > 0 }">
      <div class="content-inner">
        <!-- 显示欢迎页面、模型选择卡片或聊天消息 -->
        <WelcomeView 
          v-if="messages.length === 0" 
          :web-search-enabled="enableWebSearch"
          :image-gen-enabled="enableImageGen"
          :selected-model="selectedModel"
          @websearch-change="enableWebSearch = $event"
          @imagegen-change="enableImageGen = $event"
          @send-message="onSendMessage"
        />
        <ModelCards v-else-if="showModelCards" />
        <ChatMessages 
          v-else 
          ref="chatMessagesRef" 
          :messages="messages"
          :is-typing="isTyping"
          :show-reasoning="showReasoning"
          :selected-model="selectedModel"
          @regenerate="handleRegenerate"
        />
    </div>

      <!-- 只有在有消息时才显示底部输入框 -->
      <MessageComposer 
        v-if="messages.length > 0"
        :chat-mode="true"
        :disabled="isTyping"
        :is-typing="isTyping"
        :web-search-enabled="enableWebSearch"
        :image-gen-enabled="enableImageGen"
        :selected-model="selectedModel"
        @send-message="onSendMessage"
        @stop-reply="stopReply"
        @websearch-change="enableWebSearch = $event"
        @imagegen-change="enableImageGen = $event"
      />
    </main>

    <FabRail 
      class="fab-enter"
      :sidebar-collapsed="sidebarCollapsed"
      :pinned-top="isPinnedTop"
      :is-fullscreen="isFullscreen"
      @toggle-sidebar="handleSidebarToggle(!sidebarCollapsed)"
      @clear-session="handleClearSession"
      @toggle-top-bottom="handleToggleTopBottom"
      @toggle-fullscreen="handleToggleFullscreen"
    />
  </div>  
</div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { useI18n } from 'vue-i18n'
// 导入组件
import ChatSidebar from './components/ChatSidebar.vue'
import ModelCards from './components/ModelCards.vue'
import ChatMessages from './components/ChatMessages.vue'
import MessageComposer from './components/MessageComposer.vue'
import FabRail from './components/FabRail.vue'
import WelcomeView from './components/WelcomeView.vue'

import { useChatStream } from './composable/useChatStream'
import { useChatStore } from '@/stores/modules/chat'
import type { ChatItem } from './components/ChatHistory.vue'
import { chatApi } from '@/api/chat/chat'

const { t } = useI18n()


// 联网搜索开关
const enableWebSearch = ref(false)
// 生成图片开关
const enableImageGen = ref(false)

// 通过 composable 管理聊天与 SSE
const {
  messages,
  isTyping,
  showReasoning,
  selectedModel,

  handleModelSelected: selectModel,

  closeSseConnection,
  handleSendMessage: sendMessage,
  stopReply,

} = useChatStream({ enableWebSearch, enableImageGen })

const chatStore = useChatStore()
const currentUser = ref('') 

// 控制显示状态
const showModelCards = ref(false) // 默认显示聊天界面

// 侧边栏状态管理
const sidebarCollapsed = ref(false)

// ChatMessages ref
const chatMessagesRef = ref<InstanceType<typeof ChatMessages> | null>(null)

// 置顶/置底状态
const isPinnedTop = ref(false)

// 全屏状态
const isFullscreen = ref(false)

// 处理侧边栏切换
const handleSidebarToggle = (collapsed: boolean) => {
  sidebarCollapsed.value = collapsed
}

// 切换置顶/置底
const handleToggleTopBottom = () => {
  isPinnedTop.value = !isPinnedTop.value
  const api = chatMessagesRef.value
  if (!api) return
  if (isPinnedTop.value) {
    api.pauseAutoScroll && api.pauseAutoScroll()
    api.scrollToTop()
          } else {
    api.resumeAutoScroll && api.resumeAutoScroll()
    api.scrollToBottom()
  }
}

// 切换全屏
const handleToggleFullscreen = () => {
  if (!document.fullscreenElement) {
    // 进入全屏
    document.documentElement.requestFullscreen().then(() => {
      isFullscreen.value = true
    }).catch((err) => {
      console.error(t('chat.error.enterFullscreenFailed'), err)
    })
  } else {
    // 退出全屏
    document.exitFullscreen().then(() => {
      isFullscreen.value = false
    }).catch((err) => {
      console.error(t('chat.error.exitFullscreenFailed'), err)
    })
  }
}

// 监听全屏状态变化
const handleFullscreenChange = () => {
  isFullscreen.value = !!document.fullscreenElement
}


// 发送消息 - 协调多个组件状态
const onSendMessage = (contentOrPayload: string | { content: string; imageUrls?: string[]; pdfFiles?: { url: string; filename: string }[]; audioFiles?: { url: string }[] }) => {
  if (showModelCards.value) {
    showModelCards.value = false
  }
  const payload = typeof contentOrPayload === 'string' 
    ? { content: contentOrPayload }
    : contentOrPayload
  sendMessage(payload, currentUser.value)
  
  // 发送消息后智能滚动到底部
  nextTick(() => {
    if (chatMessagesRef.value?.scrollToBottom) {
      // 发送新消息时，强制恢复自动滚动并滚动到底部
      if (chatMessagesRef.value?.forceScrollToBottom) {
        chatMessagesRef.value.forceScrollToBottom()
      } else if (chatMessagesRef.value?.resumeAutoScroll) {
        chatMessagesRef.value.resumeAutoScroll()
      } else {
        chatMessagesRef.value.scrollToBottom()
      }
    }
  })
}

// 清空当前会话消息 - 全局操作
const handleClearSession = async () => {
  if (chatStore.activeSessionId) {
    try {
      await chatApi.reqDeleteChatSession({ sessionId: chatStore.activeSessionId })
      chatStore.clearSessionMessages(chatStore.activeSessionId)
    } catch (error) {
      console.error(t('chat.error.clearSessionFailed'), error)
    }
  }
}

// 处理删除会话 - 复杂的跨组件状态管理
const handleChatDelete = async (chat: ChatItem) => {
  // 如果删除的是当前活跃会话，需要先中断AI聊天
  const onBeforeDelete = () => {
    // 停止正在进行的回复
    if (isTyping.value) {
      stopReply()
    }
    // 关闭SSE连接
    closeSseConnection()
  }

  if (chatStore.activeSessionId === chat.id) {
    onBeforeDelete()
  }

  try {
    await chatApi.reqDeleteChatSession({ sessionId: chat.id })
    // 接口成功后再删除本地会话
    chatStore.deleteSession(chat.id)
  } catch (error) {
    console.error(t('chat.error.deleteSessionFailed'), error)
  }
}

// 处理中断AI输出（新建会话时）- 协调SSE和状态
const handleInterruptAi = () => {
  // 停止正在进行的回复
  if (isTyping.value) {
    stopReply()
  }
  // 关闭SSE连接
  closeSseConnection()
}

// 处理重新生成 - 核心业务逻辑，涉及消息重构
const handleRegenerate = (messageId: string) => {
  // 找到要重新生成的消息
  const targetMessage = messages.value.find(msg => msg.id === messageId)
  if (!targetMessage || targetMessage.isUser) {
    return
  }
  
  // 找到这条AI消息对应的用户消息
  const messageIndex = messages.value.findIndex(msg => msg.id === messageId)
  if (messageIndex <= 0) return
  
  const userMessage = messages.value[messageIndex - 1]
  if (!userMessage || !userMessage.isUser) return
  
  // 删除从该AI消息开始的所有后续消息，但保留用户消息
  const newMessages = messages.value.slice(0, messageIndex)
  
  // 更新消息列表
  if (chatStore.activeSessionId) {
    chatStore.messagesBySession[chatStore.activeSessionId] = newMessages
    // 更新会话的消息数量
    const session = chatStore.activeSession
    if (session) {
      session.messageCount = newMessages.length
    }
  }
  
  // 使用已存在的用户消息内容和原附件重新生成AI回复（不添加新的用户消息）
  sendMessage({
    content: userMessage.content,
    imageUrls: userMessage.imageUrls,
    pdfFiles: userMessage.pdfFiles,
    audioFiles: userMessage.audioFiles
  }, currentUser.value, false)
}


onMounted(async () => {
  // 确保有活跃会话
  chatStore.ensureActiveSessionOnEnter(null)
  
  // 添加全屏状态监听
  document.addEventListener('fullscreenchange', handleFullscreenChange)
})

// 选择模型时确保有活跃会话
watch(selectedModel, (m) => {
  if (!chatStore.activeSessionId && m) {
    chatStore.ensureActiveSessionOnEnter(null)
  }
})

// 组件卸载时清理SSE连接
onUnmounted(() => {
  closeSseConnection()
  
  // 移除全屏状态监听
  document.removeEventListener('fullscreenchange', handleFullscreenChange)
})
</script>

<style scoped>
.chat-root {
  height: 100%;
}

.page {
  width: 100%;
  height: 100%;
  background-color: var(--el-bg-color-page);
  display: grid;
  grid-template-columns: 260px 1fr;
  grid-template-rows: 1fr;
  position: relative;
  min-height: 0;
  transition: grid-template-columns 0.3s ease;
}

/* 侧边栏收起状态 */
.page.sidebar-collapsed {
  grid-template-columns: 60px 1fr;
}

.content {
  position: relative;
  padding: 20px 16px 0;
  overflow: hidden;
  height: 100%;
  min-height: 0;
  overscroll-behavior: contain;
  display: flex;
  flex-direction: column;
}

.content-inner {
  max-width: 1000px;
  width: 100%;
  margin: 0 auto;
  height: 100%;
  position: relative;
  flex: 1;
  display: flex;
  flex-direction: column;
  /* 调试用边框 - 可以临时看到实际宽度 */
  /* border: 1px solid red; */
}

/* MessageComposer 定位样式 */
.content {
  position: relative;
}

/* 确保有消息时的布局正确 */
.content.has-messages {
  padding-bottom: 120px;
}

/* 大屏幕确保最大宽度 */
@media (min-width: 1200px) {
  .content-inner {
    max-width: 1000px;
  }
}

@media (max-width: 960px) {
  .page {
    grid-template-columns: 220px 1fr;
  }
  
  .page.sidebar-collapsed {
    grid-template-columns: 60px 1fr;
  }
  
  .content {
    padding: 16px 12px 0;
}

  .content.has-messages {
    padding-bottom: 80px;
  }
  
  .content-inner {
    max-width: 100%;
  }
}

@media (max-width: 720px) {
  .page {
    grid-template-columns: 1fr;
  }
  .sidebar {
    display: none;
  }
  
  .content {
    padding: 12px 12px 0;
  }
  
  .content.has-messages {
    padding-bottom: 70px;
}
}
</style> 