<template>
  <div class="chat-history" @click="closeMenu">
    <div 
      v-for="section in filteredSections" 
      :key="section.id"
      class="history-section"
    >
      <div class="section-header">
        <h3 class="section-title">{{ section.title }}</h3>
      </div>
      <ul class="chat-list">
        <li 
          v-for="chat in section.chats" 
          :key="chat.id"
          class="chat-item"
          :class="{ 'active': chat.id === activeChat }"
          @click="handleChatClick(chat)"
        >
          <div class="chat-content">
            <span class="chat-title">{{ chat.title }}</span>
            <span v-if="chat.lastMessage" class="chat-preview">{{ chat.lastMessage }}</span>
            <span v-if="chat.timestamp" class="chat-time">{{ formatTime(chat.timestamp) }}</span>
          </div>
          <div class="chat-actions">
            <div class="menu-wrapper">
              <button 
                class="chat-menu"
                @click.stop="handleMenuClick(chat)"
                :title="`${chat.title} ${t('chat.sidebar.menu')}`"
                :class="{ 'active': activeMenuId === chat.id }"
              >
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
                  <circle cx="12" cy="12" r="1" fill="currentColor"/>
                  <circle cx="19" cy="12" r="1" fill="currentColor"/>
                  <circle cx="5" cy="12" r="1" fill="currentColor"/>
                </svg>
              </button>
              
              <!-- 下拉菜单 -->
              <div 
                v-if="activeMenuId === chat.id"
                class="dropdown-menu"
                @click.stop
              >
                <button 
                  class="menu-item delete-item"
                  @click="handleDeleteClick(chat, $event)"
                  :title="t('chat.sidebar.deleteSession')"
                >
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none">
                    <path d="M3 6h18M8 6V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <line x1="10" y1="11" x2="10" y2="17" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                    <line x1="14" y1="11" x2="14" y2="17" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                  </svg>
                  {{ t('chat.sidebar.delete') }}
                </button>
              </div>
            </div>
          </div>
        </li>
      </ul>
    </div>
    
    <!-- 空状态 -->
    <div v-if="isEmpty" class="empty-state">
      <svg width="48" height="48" viewBox="0 0 24 24" fill="none" class="empty-icon">
        <path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
      <p class="empty-text">{{ props.emptyText || t('chat.history.empty') }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

export interface ChatItem {
  id: string
  title: string
  lastMessage?: string
  timestamp?: Date
  tags?: string[]
}

export interface ChatSection {
  id: string
  title: string
  chats: ChatItem[]
}

interface Props {
  sections?: ChatSection[]
  activeChat?: string
  searchQuery?: string
  emptyText?: string
}

const props = withDefaults(defineProps<Props>(), {
  sections: () => [],
  activeChat: '',
  searchQuery: '',
  emptyText: ''
})

const emit = defineEmits<{
  'chat-select': [chat: ChatItem]
  'chat-menu': [chat: ChatItem]
  'chat-delete': [chat: ChatItem]
}>()

// 过滤后的聊天记录
const filteredSections = computed(() => {
  if (!props.searchQuery) {
    return props.sections
  }
  
  const query = props.searchQuery.toLowerCase()
  return props.sections.map(section => ({
    ...section,
    chats: section.chats.filter(chat => 
      chat.title.toLowerCase().includes(query) ||
      chat.lastMessage?.toLowerCase().includes(query)
    )
  })).filter(section => section.chats.length > 0)
})

// 是否为空状态
const isEmpty = computed(() => {
  return filteredSections.value.length === 0 || 
         filteredSections.value.every(section => section.chats.length === 0)
})

// 处理聊天点击
const handleChatClick = (chat: ChatItem) => {
  emit('chat-select', chat)
}

// 菜单状态管理
const activeMenuId = ref<string | null>(null)

// 处理菜单点击
const handleMenuClick = (chat: ChatItem) => {
  activeMenuId.value = activeMenuId.value === chat.id ? null : chat.id
  emit('chat-menu', chat)
}

// 处理删除点击
const handleDeleteClick = (chat: ChatItem, event: Event) => {
  event.stopPropagation()
  activeMenuId.value = null
  emit('chat-delete', chat)
}

// 关闭菜单
const closeMenu = () => {
  activeMenuId.value = null
}

// 格式化时间
const formatTime = (timestamp: Date) => {
  const now = new Date()
  const diff = now.getTime() - timestamp.getTime()
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)
  
  if (minutes < 1) return t('chat.history.justNow')
  if (minutes < 60) return `${minutes}${t('chat.history.minutesAgo')}`
  if (hours < 24) return `${hours}${t('chat.history.hoursAgo')}`
  if (days < 7) return `${days}${t('chat.history.daysAgo')}`
  
  return timestamp.toLocaleDateString('zh-CN', {
    month: 'short',
    day: 'numeric'
  })
}
</script>

<style scoped>
.chat-history {
  flex: 1;
  overflow-y: auto;
  padding: 16px 0;
}

.history-section {
  padding: 0 12px;
  margin-bottom: 24px;
}

.history-section:last-child {
  margin-bottom: 0;
}

.section-header {
  background: var(--el-bg-color);
  margin-bottom: 12px;
  padding: 6px 0;
}

.section-title {
  font-size: 11px;
  font-weight: 600;
  color: var(--el-text-color-secondary);
  letter-spacing: 0.5px;
  text-transform: uppercase;
  margin: 0;
}

.chat-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.chat-item {
  display: flex;
  align-items: center;
  padding: 10px 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  min-height: 48px;
  border: 1px solid transparent;
}

.chat-item:hover {
  background: var(--el-fill-color-light);
}

.chat-item.active {
  background: var(--el-color-primary-light-9);
  border-color: var(--el-color-primary-light-7);
}

.chat-item.active .chat-title {
  color: var(--el-color-primary);
  font-weight: 600;
}

/* 暗色主题下的激活状态样式优化 */
html[class^="theme-dark"] .chat-item.active,
html[class*=" theme-dark"] .chat-item.active {
  background: var(--el-color-primary-light-9) !important;
  border-color: var(--el-color-primary-light-7) !important;
}

html[class^="theme-dark"] .chat-item.active .chat-title,
html[class*=" theme-dark"] .chat-item.active .chat-title {
  color: var(--el-color-primary) !important;
}

.chat-content {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.chat-title {
  font-size: 14px;
  color: var(--el-text-color-primary);
  line-height: 1.4;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.chat-preview {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  line-height: 1.3;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.chat-time {
  font-size: 11px;
  color: var(--el-text-color-placeholder);
  line-height: 1;
}

.chat-actions {
  display: flex;
  align-items: center;
  gap: 4px;
}

.menu-wrapper {
  position: relative;
}

.chat-menu {
  width: 28px;
  height: 28px;
  border: none;
  background: transparent;
  color: var(--el-text-color-placeholder);
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  opacity: 0;
  transition: all 0.2s ease;
  flex-shrink: 0;
}

.chat-item:hover .chat-menu {
  opacity: 1;
}

.chat-menu:hover {
  background: var(--el-fill-color);
  color: var(--el-text-color-regular);
}

.chat-menu:active {
  transform: scale(0.95);
}

.chat-menu.active {
  opacity: 1;
  background: var(--el-fill-color);
  color: var(--el-text-color-regular);
}

/* 下拉菜单样式 */
.dropdown-menu {
  position: absolute;
  top: 100%;
  right: 0;
  background: var(--el-bg-color-overlay);
  border: 1px solid var(--el-border-color-light);
  border-radius: 8px;
  box-shadow: var(--el-box-shadow-light);
  padding: 4px 0;
  min-width: 120px;
  z-index: 1000;
  margin-top: 4px;
}

.menu-item {
  width: 100%;
  padding: 8px 12px;
  border: none;
  background: transparent;
  color: var(--el-text-color-regular);
  font-size: 13px;
  text-align: left;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: background-color 0.2s ease;
}

.menu-item:hover {
  background: var(--el-fill-color-light);
}

.menu-item.delete-item {
  color: var(--el-color-danger);
}



/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
}

.empty-icon {
  color: var(--el-text-color-placeholder);
  margin-bottom: 16px;
}

.empty-text {
  font-size: 14px;
  color: var(--el-text-color-secondary);
  margin: 0;
}

/* 使用全局滚动条样式 */
</style> 