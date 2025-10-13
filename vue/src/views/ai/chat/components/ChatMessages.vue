<template>
  <div class="chat-messages">
    <div class="messages-container" ref="messagesContainer">
      <!-- 聊天消息列表 -->
      <div class="messages-list">
        <MessageItem 
          v-for="(message, index) in props.messages" 
          :key="message.id" 
          :message="message" 
          :show-reasoning="props.showReasoning"
          :is-typing="props.isTyping"
          :is-last-message="index === props.messages.length - 1"
          @regenerate="handleRegenerate"
        />
        

      </div>
    </div>
    
    <!-- 置底按钮（当用户上滚时显示） -->
    <button v-show="showScrollToBottom" class="scroll-to-bottom" @click="resumeAutoScroll" title="回到底部">
      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <polyline points="6 9 12 15 18 9"></polyline>
      </svg>
    </button>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick, watch, computed, onMounted, onUnmounted, onUpdated } from 'vue'
import MessageItem from './MessageItem.vue'

import type { Message } from '../types'
import type { ModelItem } from '@/api/chat/types'

interface Props {
  messages: Message[]
  isTyping?: boolean
  showReasoning?: boolean // 是否显示推理内容
  selectedModel?: ModelItem | null
}

const props = withDefaults(defineProps<Props>(), {
  isTyping: false,
  showReasoning: false
})

// 定义 emits
const emit = defineEmits<{
  regenerate: [messageId: string]
}>()

const messagesContainer = ref<HTMLElement>()

// 改进的自动滚动控制
const autoScrollEnabled = ref(true)
const showScrollToBottom = ref(false)
const isUserScrolling = ref(false) // 用户是否正在手动滚动
const userScrollPaused = ref(false) // 用户是否主动暂停了自动滚动

// 防抖定时器
let scrollDebounceTimer: number | null = null
let userScrollDetectionTimer: number | null = null
let lastScrollTop = 0
let lastScrollTime = 0

// 处理重新生成事件
const handleRegenerate = (messageId: string) => {
  emit('regenerate', messageId)
}

// 跟踪最后一条消息变动（ID + 内容长度），用于流式更新时触发
const lastMessageSignature = computed(() => {
  const list = props.messages
  const last = list[list.length - 1]
  if (!last) return ''
  return `${last.id}:${last.content ? last.content.length : 0}`
})

const isNearBottom = () => {
  const el = messagesContainer.value
  if (!el) return true
  // 增加阈值，让用户更容易触发"接近底部"
  const threshold = 80
  return el.scrollHeight - el.scrollTop - el.clientHeight <= threshold
}

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    const el = messagesContainer.value
    if (!el) return
    // 等待布局完成后滚动
    requestAnimationFrame(() => {
      el.scrollTop = el.scrollHeight
    })
  })
}

// 防抖滚动到底部 - 减少滚动频率
const debouncedScrollToBottom = () => {
  if (scrollDebounceTimer) {
    clearTimeout(scrollDebounceTimer)
  }
  scrollDebounceTimer = window.setTimeout(() => {
    scrollToBottom()
    scrollDebounceTimer = null
  }, 50) // 50ms 防抖延迟
}

// 滚动到顶部
const scrollToTop = () => {
  nextTick(() => {
    const el = messagesContainer.value
    if (!el) return
    requestAnimationFrame(() => {
      el.scrollTop = 0
    })
  })
}

// 改进的滚动事件处理
const handleScroll = () => {
  const el = messagesContainer.value
  if (!el) return
  
  const currentScrollTop = el.scrollTop
  const currentTime = Date.now()
  const isScrollingUp = currentScrollTop < lastScrollTop
  const scrollDistance = Math.abs(currentScrollTop - lastScrollTop)
  const scrollSpeed = scrollDistance / Math.max(currentTime - lastScrollTime, 1)
  const near = isNearBottom()
  
  // 检测用户是否在快速滚动
  if (scrollDistance > 3) {
    isUserScrolling.value = true
    
    // 清除之前的检测定时器
    if (userScrollDetectionTimer) {
      clearTimeout(userScrollDetectionTimer)
    }
    
    // 根据滚动速度调整检测延迟
    const delay = scrollSpeed > 0.5 ? 800 : 500
    userScrollDetectionTimer = window.setTimeout(() => {
      isUserScrolling.value = false
    }, delay)
  }
  
  lastScrollTop = currentScrollTop
  lastScrollTime = currentTime
  
  // 更新UI状态
  showScrollToBottom.value = !near
  
  // 滚动控制逻辑
  if (near) {
    // 用户回到底部 恢复自动滚动
    userScrollPaused.value = false
    autoScrollEnabled.value = true
  } else if (isScrollingUp && scrollDistance > 20) {
    // 向上滚动超过阈值 暂停自动滚动
    userScrollPaused.value = true
    autoScrollEnabled.value = false
  }
}

// 改进的自动滚动恢复
const resumeAutoScroll = () => {
  userScrollPaused.value = false
  autoScrollEnabled.value = true
  showScrollToBottom.value = false
  scrollToBottom()
}

// 暂停自动滚动
const pauseAutoScroll = () => {
  userScrollPaused.value = true
  autoScrollEnabled.value = false
}

// 强制滚动到底部（用于发送新消息时）
const forceScrollToBottom = () => {
  userScrollPaused.value = false
  autoScrollEnabled.value = true
  showScrollToBottom.value = false
  scrollToBottom()
}

// 智能滚动函数
const shouldAutoScroll = () => {
  // 用户暂停了自动滚动
  if (userScrollPaused.value) return false
  
  // 如果用户正在手动滚动 不滚动
  if (isUserScrolling.value) return false
  
  // 只有在启用自动滚动时才滚动
  return autoScrollEnabled.value
}

onMounted(() => {
  if (messagesContainer.value) {
    messagesContainer.value.addEventListener('scroll', handleScroll, { passive: true })
    lastScrollTop = messagesContainer.value.scrollTop
  }
  // 初始化滚动到底部
  scrollToBottom()
})

onUpdated(() => {
  // 组件更新后，使用智能判断是否需要滚动
  if (shouldAutoScroll()) {
    scrollToBottom()
  }
})

onUnmounted(() => {
  // 清理防抖定时器
  if (scrollDebounceTimer) {
    clearTimeout(scrollDebounceTimer)
  }
  
  if (userScrollDetectionTimer) {
    clearTimeout(userScrollDetectionTimer)
  }
  
  if (messagesContainer.value) {
    messagesContainer.value.removeEventListener('scroll', handleScroll)
  }
})

// 监听消息变化，使用改进的滚动判断
watch(() => props.messages, () => {
  if (shouldAutoScroll()) {
    scrollToBottom()
  }
}, { deep: true })

// 监听消息条数变化（新增消息时）
watch(() => props.messages.length, () => {
  if (shouldAutoScroll()) {
    scrollToBottom()
  }
})

// 监听最后一条消息的内容长度变化（流式追加时）- 使用防抖和改进判断
watch(lastMessageSignature, () => {
  if (shouldAutoScroll()) {
    debouncedScrollToBottom()
  }
})

// 监听输入状态变化，使用改进的滚动判断
watch(() => props.isTyping, (val) => {
  if (val && shouldAutoScroll()) {
    scrollToBottom()
  }
})

// 导出方法供父组件使用
defineExpose({
  scrollToBottom,
  scrollToTop,
  resumeAutoScroll,
  pauseAutoScroll,
  forceScrollToBottom
})
</script>

<style scoped>
.chat-messages {
  height: 100%;
  width: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  position: relative;
}

.messages-container {
  flex: 1;
  width: 100%;
  overflow-y: auto;
  padding: 0;
  scroll-behavior: smooth;
  /* 改进滚动体验 */
  overscroll-behavior-y: contain;
  scroll-padding-bottom: 20px;
}

/* 置底按钮 */
.scroll-to-bottom {
  position: absolute;
  right: 16px;
  bottom: 120px;
  width: 36px;
  height: 36px;
  border-radius: 18px;
  border: 1px solid var(--el-border-color-lighter);
  background: var(--el-bg-color);
  color: var(--el-text-color-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: var(--el-box-shadow-light);
  cursor: pointer;
  z-index: 10;
}

.scroll-to-bottom:hover {
  background: var(--el-fill-color);
}

.welcome-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  padding: 40px 20px;
  text-align: center;
}

.welcome-avatar {
  margin-bottom: 24px;
}

.ai-logo {
  width: 64px;
  height: 64px;
  background: var(--el-color-primary-light-8);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  margin: 0 auto;
}

.welcome-title {
  font-size: 24px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  margin-bottom: 12px;
}

.welcome-desc {
  font-size: 16px;
  color: var(--el-text-color-secondary);
  line-height: 1.6;
  max-width: 500px;
}

.messages-list {
  padding: 20px 0 80px 0;
}

.typing-indicator {
  padding: 0 8px;
}

.typing-indicator .message-item {
  margin-bottom: 24px;
}

/* 头像相关样式已移除 */

/* 更柔和的思考气泡，带轻微高亮与阴影 */
.typing-bubble {
  margin-top: 10px;
  position: relative;
  background: var(--el-bg-color);
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 16px 16px 16px 4px;
  padding: 14px 18px;
  display: inline-flex;
  align-items: center;
  gap: 10px;
  max-width: fit-content;
  box-shadow: 0 2px 10px rgba(0,0,0,0.04);
  overflow: hidden;
}

/* 轻微的流光效果，低对比度避免干扰 */
.typing-bubble::after {
  content: "";
  position: absolute;
  top: 0;
  left: -40%;
  width: 40%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.06), transparent);
  animation: shimmer 2.2s infinite;
}

@keyframes shimmer {
  0% { left: -40%; }
  100% { left: 120%; }
}

/* 打字点更细腻，轻缓呼吸 */
.typing-dots {
  display: inline-flex;
  gap: 6px;
  align-items: center;
}

.typing-dots span {
  width: 6px;
  height: 6px;
  background: var(--el-color-primary);
  opacity: 0.8;
  border-radius: 50%;
  animation: typing 1.4s infinite ease-in-out;
}

.typing-dots span:nth-child(1) {
  animation-delay: 0s;
}

.typing-dots span:nth-child(2) {
  animation-delay: 0.18s;
}

.typing-dots span:nth-child(3) {
  animation-delay: 0.36s;
}

@keyframes typing {
  0%, 60%, 100% {
    opacity: 0.45;
    transform: translateY(0) scale(0.85);
  }
  30% {
    opacity: 1;
    transform: translateY(-2px) scale(1);
  }
}

/* 正在思考文案旁的轻量旋转图标 */
.message-header .spinner {
  width: 14px;
  height: 14px;
  border: 2px solid var(--el-border-color-lighter);
  border-top-color: var(--el-color-primary);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-left: 6px;
}

/* 调小“正在思考...”字号 */
.typing-indicator .message-header .message-time {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  padding-left: 10px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 隐藏滚动条 */
.messages-container::-webkit-scrollbar {
  display: none;
}

.messages-container {
  -ms-overflow-style: none;  /* IE and Edge */
  scrollbar-width: none;  /* Firefox */
}

@media (max-width: 960px) {
  .welcome-section {
    padding: 20px 16px;
  }
  
  .welcome-title {
    font-size: 20px;
  }
  
  .welcome-desc {
    font-size: 14px;
  }
  
  .messages-list {
    padding: 16px 0 80px 0;
  }
  
  .typing-indicator {
    padding: 0 16px;
  }
  
  .typing-bubble {
    padding: 12px 16px;
  }
  
  .scroll-to-bottom {
    bottom: 110px;
  }
}

@media (max-width: 720px) {
  .scroll-to-bottom {
    bottom: 100px;
  }
}

@media (max-width: 768px) {
  .welcome-section {
    padding: 20px 16px;
  }
  
  .welcome-title {
    font-size: 20px;
  }
  
  .welcome-desc {
    font-size: 14px;
  }
  
  .messages-list {
    padding: 16px 0 70px 0;
  }
  
  .typing-indicator {
    padding: 0 16px;
  }
  
  .typing-bubble {
    padding: 12px 16px;
  }
}
</style> 