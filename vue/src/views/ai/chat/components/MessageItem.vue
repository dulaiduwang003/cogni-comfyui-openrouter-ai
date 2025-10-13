<template>
  <div class="message-item" :class="{ 'is-user': message.isUser, 'is-ai': !message.isUser }">
    <!-- 用户消息：简约显示，只显示内容 -->
    <div v-if="message.isUser" class="user-message-content">
      <div class="message-text">
        {{ message.content }}
      </div>
      <div v-if="(message.imageUrls && message.imageUrls.length) || (message.pdfFiles && message.pdfFiles.length) || (message.audioFiles && message.audioFiles.length)" class="attachments">
        <div class="attachments-list">
          <div class="attachment-item image" v-for="(url, idx) in message.imageUrls || []" :key="'uimg-' + idx">
            <a class="image-link" :href="url" target="_blank" rel="noopener">
              <img :src="url" alt="" class="thumb" />
            </a>
          </div>
          <div class="attachment-item file" :class="'ext-' + file.ext" v-for="(file, idx) in fileTiles" :key="'ufile-' + idx">
            <a class="file-tile" :href="file.url" target="_blank" rel="noopener" :title="file.filename">
              <span class="file-badge" :class="'ext-' + file.ext">{{ file.ext.toUpperCase() }}</span>
              <svg class="file-icon" viewBox="0 0 24 24" aria-hidden="true">
                <path fill="currentColor" d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8l-6-6z"></path>
                <path fill="currentColor" d="M13 3.5L18.5 9H13z"></path>
              </svg>
              <span class="file-name">{{ file.filename }}</span>
            </a>
          </div>
        </div>
      </div>
    </div>
    
    <!-- AI消息：保持原有的完整显示 -->
    <template v-else>
      <div class="message-content">
        <div class="message-header">
          <span class="message-sender">{{ message.aiModel || 'AI' }}</span>
          <span v-if="props.isTyping && isLastMessage" class="message-time">{{ t('chat.message.generating') }}</span>
          <span v-else class="message-time">{{ formatTime(message.timestamp) }}</span>
          <span v-if="props.isTyping && isLastMessage" class="spinner"></span>
        </div>
        
        <div v-if="shouldShowTextBlock" class="message-text">
          <!-- 推理内容（如果有且开启显示） -->
          <div v-if="message.reasoningContent && props.showReasoning" class="reasoning-content">
            <button class="reasoning-toggle" @click="toggleReasoning" :aria-expanded="isReasoningOpen">
              <svg :class="['chevron', { open: isReasoningOpen }]" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polyline points="6 9 12 15 18 9"></polyline>
              </svg>
              <span class="reasoning-label">{{ t('chat.message.reasoningProcess') }}</span>
            </button>
            <div v-show="isReasoningOpen" class="reasoning-text">
              <MarkdownRenderer :content="message.reasoningContent" />
            </div>
          </div>
          
          <!-- 正在输入占位动画：仅在最后一条AI消息且内容为空时显示 -->
          <div v-if="showTypingPlaceholder" class="typing-dots" :aria-label="t('chat.message.typing')">
            <span></span><span></span><span></span>
          </div>
          
          <!-- 正式回复内容 -->
          <MarkdownRenderer v-else :content="message.content" />
        </div>

        <div v-if="message.imageUrls && message.imageUrls.length" class="ai-images">
          <div class="ai-images-grid">
            <div 
              v-for="(url, idx) in message.imageUrls" 
              :key="'aiimg-' + idx" 
              class="ai-image-wrap"
              @click="openViewer(idx)"
            >
              <img :src="url" alt="" class="ai-image" />
              <div class="ai-image-badge" aria-hidden="true">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M1 12s4-7 11-7 11 7 11 7-4 7-11 7-11-7-11-7z"></path>
                  <circle cx="12" cy="12" r="3"></circle>
                </svg>
              </div>
            </div>
          </div>
        </div>

        <div v-if="message.citations && message.citations.length" class="citations">
          <button class="citations-toggle" @click="toggleCitations" :aria-expanded="isCitationsOpen">
            <svg :class="['chevron', { open: isCitationsOpen }]" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="6 9 12 15 18 9"></polyline>
            </svg>
            <svg class="citation-icon" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"></path>
              <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"></path>
            </svg>
            <span class="citations-title">
              {{ t('chat.message.references') }}
              <span class="citations-count">({{ message.citations.length }})</span>
            </span>
          </button>
          <div v-show="isCitationsOpen" class="citations-content">
            <ol class="citations-grid">
              <li v-for="(c, i) in message.citations" :key="i" class="citation-card">
                <a :href="c.url" target="_blank" rel="noopener" class="citation-link">
                  <span class="badge">{{ i + 1 }}</span>
                  <img v-if="getFaviconUrl(c.url)" :src="getFaviconUrl(c.url)" class="favicon" alt="" />
                  <div class="meta">
                    <div class="title" :title="c.title || c.url">{{ c.title || c.url }}</div>
                    <div v-if="getDomainFromUrl(c.url)" class="domain">{{ getDomainFromUrl(c.url) }}</div>
                  </div>
                  <svg class="external" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M18 13v6a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6"></path>
                    <path d="M15 3h6v6"></path>
                    <path d="M10 14L21 3"></path>
                  </svg>
                </a>
              </li>
            </ol>
          </div>
        </div>
        
        <div class="message-actions">
          <el-tooltip :content="t('chat.message.copyMessage')" placement="top">
            <button class="action-btn" @click="copyMessage" :aria-label="t('chat.message.copyMessage')">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <rect x="9" y="9" width="13" height="13" rx="2" ry="2"></rect>
                <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"></path>
              </svg>
            </button>
          </el-tooltip>
          <el-tooltip :content="likeTooltip" placement="top">
            <button 
              class="action-btn" 
              :class="{ 'active': likeStatus === 'liked' }"
              @click="handleLike"
              :aria-label="t('chat.message.like')"
            >
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M14 9V5a3 3 0 0 0-3-3l-4 9v11h11.28a2 2 0 0 0 2-1.7l1.38-9a2 2 0 0 0-2-2.3zM7 22H4a2 2 0 0 1-2-2v-7a2 2 0 0 1 2-2h3"></path>
              </svg>
            </button>
          </el-tooltip>
          <el-tooltip :content="dislikeTooltip" placement="top">
            <button 
              class="action-btn" 
              :class="{ 'active': likeStatus === 'disliked' }"
              @click="handleDislike"
              :aria-label="t('chat.message.dislike')"
            >
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M10 15v4a3 3 0 0 0 3 3l4-9V2H5.72a2 2 0 0 0-2 1.7l-1.38 9a2 2 0 0 0 2 2.3zm7-13h2.67A2.31 2.31 0 0 1 22 4v7a2.31 2.31 0 0 1-2.33 2H17"></path>
              </svg>
            </button>
          </el-tooltip>
          <el-tooltip v-if="props.isLastMessage && !props.isTyping" :content="t('chat.message.regenerate')" placement="top">
            <button 
              class="action-btn" 
              @click="handleRegenerate"
              :disabled="isRegenerating"
              :aria-label="t('chat.message.regenerate')"
            >
              <svg 
                width="16" 
                height="16" 
                viewBox="0 0 24 24" 
                fill="none" 
                stroke="currentColor" 
                stroke-width="2"
                :class="{ 'rotating': isRegenerating }"
              >
                <polyline points="23 4 23 10 17 10"></polyline>
                <polyline points="1 20 1 14 7 14"></polyline>
                <path d="m3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"></path>
              </svg>
            </button>
          </el-tooltip>
        </div>

        <AiImageViewer 
          v-model="viewerVisible" 
          :urls="message.imageUrls || []" 
          :start-index="viewerStartIndex"
          @closed="onViewerClosed"
        />
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElNotification } from 'element-plus'
import MarkdownRenderer from '@/components/common/MarkdownRenderer.vue'
import type { Message } from '../types'
import AiImageViewer from './AiImageViewer.vue'

const { t } = useI18n()

interface Props {
  message: Message
  showReasoning?: boolean // 是否显示推理内容
  isTyping?: boolean // 是否正在输入
  isLastMessage?: boolean // 是否是最后一条消息
}

const props = withDefaults(defineProps<Props>(), {
  showReasoning: true,
  isTyping: false,
  isLastMessage: false
})

// 推理内容折叠状态
const isReasoningOpen = ref(true)

// 参考来源折叠状态
const isCitationsOpen = ref(false)

const toggleReasoning = () => {
  isReasoningOpen.value = !isReasoningOpen.value
}

// 切换参考来源折叠状态
const toggleCitations = () => {
  isCitationsOpen.value = !isCitationsOpen.value
}

// 图片查看器
const viewerVisible = ref(false)
const viewerStartIndex = ref(0)
const openViewer = (index: number) => {
  viewerStartIndex.value = index
  viewerVisible.value = true
}
const onViewerClosed = () => {
  // no-op for now
}

// 点赞/点踩状态管理
type LikeStatus = 'none' | 'liked' | 'disliked'
const likeStatus = ref<LikeStatus>('none')
const likeTooltip = computed(() => likeStatus.value === 'liked' ? t('chat.message.liked') : t('chat.message.like'))
const dislikeTooltip = computed(() => likeStatus.value === 'disliked' ? t('chat.message.disliked') : t('chat.message.dislike'))

// 重新生成状态
const isRegenerating = ref(false)

// 复制消息内容
const copyMessage = async () => {
  try {
    const textToCopy = props.message.reasoningContent && props.showReasoning && isReasoningOpen.value
      ? `${t('chat.message.reasoningProcess')}：\n${props.message.reasoningContent}\n\n${t('chat.message.copyMessage')}：\n${props.message.content}`
      : props.message.content
    await navigator.clipboard.writeText(textToCopy)
    ElNotification.success(t('chat.message.copySuccess'))
  } catch (error) {
    console.error('复制失败:', error)
    ElNotification.error(t('chat.message.copyFailed'))
  }
}

// 点赞处理
const handleLike = () => {
  if (likeStatus.value === 'liked') {
    likeStatus.value = 'none'
  } else {
    likeStatus.value = 'liked'
  }
}

// 点踩处理
const handleDislike = () => {
  if (likeStatus.value === 'disliked') {
    likeStatus.value = 'none'
  } else {
    likeStatus.value = 'disliked'
  }
}

// 定义 emits
const emit = defineEmits<{ 
  regenerate: [messageId: string]
}>()

// 重新生成处理
const handleRegenerate = async () => {
  if (isRegenerating.value) return
  // 仅允许最后一条消息且非输入中时触发
  if (!props.isLastMessage || props.isTyping) return
  isRegenerating.value = true
  try {
    // 发出重新生成事件给父组件
    emit('regenerate', props.message.id)
  } catch (error) {
    console.error('重新生成失败:', error)
    ElNotification.error(t('chat.message.regenerating'))
  } finally {
    // 延迟重置状态，让用户看到加载效果
    setTimeout(() => {
      isRegenerating.value = false
    }, 1000)
  }
}

const formatTime = (timestamp: number) => {
  const date = new Date(timestamp)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  
  if (diff < 60000) { // 小于1分钟
    return '刚刚'
  } else if (diff < 3600000) { // 小于1小时
    return `${Math.floor(diff / 60000)}分钟前`
  } else if (date.toDateString() === now.toDateString()) { // 今天
    return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  } else {
    return date.toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' })
  }
}

const getDomainFromUrl = (url?: string) => {
  try {
    return url ? new URL(url).hostname : ''
  } catch {
    return ''
  }
}

const getFaviconUrl = (url?: string) => {
  if (!url) return ''
  return `https://www.google.com/s2/favicons?sz=32&domain_url=${encodeURIComponent(url)}`
}

const getFileExtension = (filename?: string, url?: string) => {
  const source = filename || url || ''
  const clean = source.split('?')[0].split('#')[0]
  const parts = clean.split('.')
  if (parts.length <= 1) return 'file'
  return (parts.pop() || 'file').toLowerCase()
}

const resolveFilename = (url?: string, filename?: string) => {
  if (filename && filename.trim()) return filename
  if (!url) return 'file'
  try {
    const u = new URL(url)
    const path = u.pathname.split('/').filter(Boolean).pop() || 'file'
    return decodeURIComponent(path)
  } catch {
    const path = url.split('?')[0].split('#')[0]
    const last = path.split('/').filter(Boolean).pop() || 'file'
    return decodeURIComponent(last)
  }
}

const fileTiles = computed(() => {
  const items: Array<{ url: string; filename: string; ext: string }> = []
  ;(props.message.pdfFiles || []).forEach((f: any) => {
    const name = resolveFilename(f.url, f.filename)
    items.push({ url: f.url, filename: name, ext: getFileExtension(name, f.url) })
  })
  ;(props.message.audioFiles || []).forEach((f: any) => {
    const name = resolveFilename(f.url, (f as any).filename)
    items.push({ url: f.url, filename: name, ext: getFileExtension(name, f.url) })
  })
  return items
})

// 仅含图片的AI消息：在生成完毕且无文本时隐藏文本气泡
const hasTextContent = computed(() => !!(props.message.content && props.message.content.trim()))
const hasReasoningContent = computed(() => !!(props.message.reasoningContent && props.showReasoning))
const showTypingPlaceholder = computed(() => props.isTyping && props.isLastMessage && !hasTextContent.value)
const shouldShowTextBlock = computed(() => hasReasoningContent.value || showTypingPlaceholder.value || hasTextContent.value)

// 移除旧的格式化函数，现在使用MarkdownRenderer组件
</script>

<style scoped>
.message-item {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
  padding: 0 8px;
  max-width: 100%;
  width: 100%;
}

/* 头像相关样式已移除 */

.message-content {
  flex: 1;
  min-width: 0;
  width: 100%;
}

.message-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}

.message-sender {
  font-size: 14px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.message-time {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.message-text {
  color: var(--el-text-color-primary);
  line-height: 1.6;
  font-size: 14px;
  word-wrap: break-word;
  white-space: normal;
}

.message-text :deep(code) {
  background: var(--el-fill-color-light);
  padding: 2px 4px;
  border-radius: 4px;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 13px;
}

.message-text :deep(strong) {
  font-weight: 600;
}

.message-text :deep(em) {
  font-style: italic;
}

.message-actions {
  display: flex;
  gap: 4px;
  margin-top: 8px;
  opacity: 0;
  transition: opacity 0.2s ease;
}

.message-item:hover .message-actions {
  opacity: 1;
}

.action-btn {
  width: 28px;
  height: 28px;
  border: none;
  background: transparent;
  color: var(--el-text-color-secondary);
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
}

.action-btn:hover {
  background: var(--el-fill-color);
  color: var(--el-text-color-primary);
}

/* 激活状态样式 */
.action-btn.active {
  background: var(--el-color-primary-light-9);
  color: var(--el-color-primary);
}

.action-btn.active:hover {
  background: var(--el-color-primary-light-8);
  color: var(--el-color-primary);
}

/* 禁用状态 */
.action-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.action-btn:disabled:hover {
  background: transparent;
  color: var(--el-text-color-secondary);
}

/* 旋转动画 */
@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.rotating {
  animation: rotate 1s linear infinite;
}

/* 加载spinner样式 */
.message-header .spinner {
  width: 14px;
  height: 14px;
  border: 2px solid var(--el-border-color-lighter);
  border-top-color: var(--el-color-primary);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-left: 6px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 气泡内打字点动画 */
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

.typing-dots span:nth-child(1) { animation-delay: 0s; }
.typing-dots span:nth-child(2) { animation-delay: 0.18s; }
.typing-dots span:nth-child(3) { animation-delay: 0.36s; }

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

/* 用户消息简约样式 */
.is-user {
  justify-content: flex-end;
}

.user-message-content {
  max-width: 70%;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.is-user .message-text {
  background: var(--el-color-primary);
  color: white;
  padding: 12px 16px;
  border-radius: 16px 16px 4px 16px;
  display: inline-block;
  max-width: fit-content;
  word-wrap: break-word;
  white-space: pre-wrap;
  line-height: 1.5;
  font-size: 14px;
}

/* AI消息样式保持不变 */
.is-ai {
  display: flex;
  gap: 12px;
}

.is-ai .message-text {
  background: var(--el-bg-color);
  padding: 12px 16px;
  border-radius: 16px 16px 16px 4px;
  border: 1px solid var(--el-border-color-lighter);
  display: inline-block;

  white-space: pre-wrap;
}

@media (max-width: 768px) {
  .message-item {
    padding: 0 16px;
  }
  
  .message-actions {
    opacity: 1;
  }

}

/* 推理内容样式 */
.reasoning-content {
  background: var(--el-fill-color-extra-light);
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 8px;
  padding: 16px 12px 12px;
  margin-bottom: 12px;
  font-size: 13px;
}

.reasoning-toggle {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 6px;
  background: transparent;
  border: none;
  color: var(--el-text-color-secondary);
  padding: 0 0 6px 0;
  border-bottom: 1px solid var(--el-border-color-lighter);
  cursor: pointer;
}

.chevron {
  transition: transform 0.2s ease;
}

.chevron.open {
  transform: rotate(180deg);
}

.reasoning-header {
  margin-bottom: 8px;
  padding-bottom: 6px;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.reasoning-label {
  font-weight: 600;
  color: var(--el-text-color-secondary);
  font-size: 12px;
}

.reasoning-text {
  padding-top: 12px;
  color: var(--el-text-color-regular);
  line-height: 1.5;
  white-space: normal;
  word-wrap: break-word;
}

.citations {
  margin-top: 10px;
}

.citations-toggle {
  display: flex;
  align-items: center;
  gap: 6px;
  background: none;
  border: none;
  color: var(--el-text-color-secondary);
  cursor: pointer;
  padding: 6px 0;
  margin-bottom: 8px;
  transition: color 0.2s ease;
  font-size: 12px;
  font-weight: 600;
}

.citations-toggle:hover {
  color: var(--el-text-color-primary);
}

.citations-toggle .chevron {
  transition: transform 0.2s ease;
  color: var(--el-text-color-placeholder);
}

.citations-toggle .chevron.open {
  transform: rotate(180deg);
}

.citation-icon {
  color: var(--el-color-primary);
  flex-shrink: 0;
}

.citations-title {
  font-size: 12px;
  color: inherit;
  font-weight: 600;
}

.citations-count {
  margin-left: 4px;
  font-weight: 400;
  color: var(--el-text-color-placeholder);
}

.citations-content {
  overflow: hidden;
  transition: all 0.3s ease;
  max-width: 70%;
}

.citations-grid {
  list-style: none;
  padding: 0;
  margin: 0;
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 8px;
}



.citation-link {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 10px;
  background: var(--el-fill-color);
  text-decoration: none;
  color: inherit;
  transition: background 0.15s ease, border-color 0.15s ease, transform 0.15s ease;
}

.citation-link:hover {
  background: var(--el-fill-color-light);
  border-color: var(--el-color-primary-light-7);
}

.badge {
  width: 20px;
  height: 20px;
  border-radius: 6px;
  display: grid;
  place-items: center;
  font-size: 12px;
  font-weight: 600;
  color: var(--el-color-primary);
  background: var(--el-color-primary-light-9);
  flex: 0 0 auto;
}

.favicon {
  width: 16px;
  height: 16px;
  border-radius: 4px;
  flex: 0 0 auto;
}

.meta {
  min-width: 0;
  display: grid;
  gap: 2px;
}

.meta .title {
  font-size: 13px;
  color: var(--el-text-color-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.meta .domain {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.external {
  margin-left: auto;
  color: var(--el-text-color-secondary);
  flex: 0 0 auto;
}

@media (max-width: 768px) {
  .citations-grid {
    grid-template-columns: 1fr;
  }
}

.attachments {
  margin-top: 8px;
  width: 100%;
  display: flex;
  justify-content: flex-end;
}

.attachments-list {
  display: inline-flex;
  flex-wrap: wrap;
  gap: 8px;
  max-width: 100%;
}

.attachment-item.image .thumb {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 8px;
  border: 1px solid var(--el-border-color);
  cursor: pointer;
}

.attachment-item.file .file-tile {
  width: 88px;
  height: 96px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: var(--el-fill-color-light);
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 10px;
  text-decoration: none;
  color: var(--el-text-color-primary);
  position: relative;
  padding: 8px 6px;
  box-sizing: border-box;
  gap: 6px;
  transition: border-color 0.15s ease, box-shadow 0.15s ease, transform 0.12s ease;
}

.attachment-item.file .file-tile:hover {
  border-color: var(--el-color-primary-light-6);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transform: translateY(-1px);
}

.attachment-item.file .file-icon {
  width: 34px;
  height: 34px;
  color: var(--el-text-color-placeholder);
}

.attachment-item.file .file-badge {
  position: absolute;
  top: 6px;
  left: 6px;
  height: 18px;
  padding: 0 6px;
  font-size: 11px;
  border-radius: 999px;
  background: var(--el-fill-color);
  color: var(--el-text-color-secondary);
  border: 1px solid var(--el-border-color-lighter);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  letter-spacing: 0.3px;
  box-shadow: 0 1px 2px rgba(0,0,0,0.04);
}

.attachment-item.file .file-name {
  width: 100%;
  text-align: center;
  font-size: 12px;
  line-height: 1.25;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  word-break: break-all;
  color: var(--el-text-color-regular);
  font-weight: 500;
}

.attachment-item.file .file-badge.ext-pdf {
  background: var(--el-color-primary-light-9);
  color: var(--el-color-primary);
}

.attachment-item.file .file-badge.ext-wav,
.attachment-item.file .file-badge.ext-mp3,
.attachment-item.file .file-badge.ext-flac,
.attachment-item.file .file-badge.ext-m4a {
  background: var(--el-color-primary-light-9);
  color: var(--el-color-primary);
}

.attachment-item.file .file-badge.ext-file {
  background: var(--el-color-primary-light-9);
  color: var(--el-color-primary);
}

/* Container accent styles */
.attachment-item.file.ext-pdf .file-tile {
  border-color: var(--el-color-primary-light-7);
  background: var(--el-color-primary-light-9);
  height: 80px;
  width: auto;
  max-width: 260px;
  padding: 8px 10px;
}

.attachment-item.file.ext-pdf .file-name {
  -webkit-line-clamp: 1;
  line-clamp: 1;
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
}

.attachment-item.file.ext-wav .file-tile,
.attachment-item.file.ext-mp3 .file-tile,
.attachment-item.file.ext-flac .file-tile,
.attachment-item.file.ext-m4a .file-tile {
  border-color: var(--el-color-primary-light-7);
  background: var(--el-color-primary-light-9);
}

.attachment-item.file .badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  background: var(--el-fill-color);
  color: var(--el-text-color-primary);
  padding: 0 6px;
  height: 20px;
  border-radius: 4px;
  margin-right: 6px;
}

.attachment-item.pdf .name {
  color: var(--el-text-color-primary);
  font-size: 12px;
  text-decoration: none;
}

.attachment-item.audio .audio-player {
  height: 28px;
}

.ai-images {
  margin-top: 10px;
}
.ai-images-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 8px;
}
.ai-image-wrap {
  position: relative;
  cursor: pointer;
}
.ai-image {
  width: 100%;
  height: auto;
  border-radius: 8px;
  border: 1px solid var(--el-border-color-lighter);
  background: var(--el-fill-color);
  display: block;
}
.ai-image-badge {
  position: absolute;
  right: 6px;
  bottom: 6px;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: rgba(0,0,0,0.55);
  color: #fff;
  display: grid;
  place-items: center;
  box-shadow: 0 2px 6px rgba(0,0,0,0.25);
  opacity: 0;
  transform: translateY(4px);
  transition: opacity 0.15s ease, transform 0.15s ease;
  pointer-events: none;
}
.ai-image-wrap:hover .ai-image-badge {
  opacity: 1;
  transform: translateY(0);
}

.ai-images {
  margin-top: 10px;
}
.ai-images-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 8px;
}
.ai-image {
  width: 100%;
  height: auto;
  border-radius: 8px;
  border: 1px solid var(--el-border-color-lighter);
  background: var(--el-fill-color);
}
</style> 
