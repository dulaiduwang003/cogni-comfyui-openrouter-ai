<template>
  <aside class="sidebar" :class="{ 'sidebar-collapsed': isCollapsed }">
    <!-- 顶部区域：折叠按钮 + 按钮组 -->
    <div class="sidebar-header">
      <div class="action-buttons">
        <button class="btn btn-new-chat" v-show="!isCollapsed" @click="handleNewChat">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none">
            <path d="M12 5v14M5 12h14" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          </svg>
          {{ t('chat.sidebar.newChat') }}
        </button>
        <button class="collapse-btn" @click="toggleSidebar" :title="isCollapsed ? t('chat.sidebar.expand') : t('chat.sidebar.collapse')">
          <!-- 展开状态：显示左箭头收起图标 -->
          <svg v-if="!isCollapsed" width="16" height="16" viewBox="0 0 24 24" fill="none">
            <path d="m15 18-6-6 6-6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <!-- 收起状态：显示右箭头展开图标 -->
          <svg v-else width="16" height="16" viewBox="0 0 24 24" fill="none">
            <path d="m9 18 6-6-6-6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>
      </div>
      
      <!-- Add Model 按钮独立一行 -->
      <button class="btn btn-add-model" v-show="!isCollapsed" @click="openModelDialog">
        <!-- 如果有选中的模型，显示模型图标，否则显示加号 -->
        <img 
          v-if="props.selectedModel?.icon && !selectedModelImageError" 
          :src="props.selectedModel.icon" 
          :alt="props.selectedModel.name" 
          class="model-icon-small" 
          @error="handleSelectedModelImageError"
        />
              <div v-else-if="props.selectedModel" class="model-icon-placeholder-small">
        <img src="@/assets/svg/model-icon.svg" alt="Default Model Icon" class="model-icon-fallback-small" />
      </div>
        <svg v-else width="14" height="14" viewBox="0 0 24 24" fill="none">
          <path d="M12 2L2 7l10 5 10-5-10-5z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          <path d="M2 17l10 5 10-5" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          <path d="M2 12l10 5 10-5" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        <!-- 如果有选中的模型，显示模型名称，否则显示 加载中… -->
        <span class="model-name">{{ props.selectedModel ? props.selectedModel.name : t('chat.sidebar.loading') }}</span>
      </button>
    </div>

    <!-- 搜索框 -->
    <SearchBox 
      v-show="!isCollapsed"
      v-model="searchQuery"
      :placeholder="t('chat.sidebar.searchPlaceholder')"
      @search="handleSearch"
      @clear="handleSearchClear"
    />

    <!-- 聊天历史区域 -->
    <ChatHistory 
      v-show="!isCollapsed"
      :sections="chatSections"
      :active-chat="activeChat"
      :search-query="searchQuery"
      @chat-select="handleChatSelect"
      @chat-menu="handleChatMenu"
      @chat-delete="handleChatDelete"
    />

    <!-- 模型选择对话框 -->
    <el-dialog
      v-model="showModelDialog"
      :title="t('chat.sidebar.selectModel')"
      width="520px"
      align-center
      :close-on-click-modal="true"
      :close-on-press-escape="true"
      :destroy-on-close="true"
    >
      <ModelDropdown 
        v-if="showModelDialog"
        @model-selected="handleModelSelected"
      />
    </el-dialog>
  </aside>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElNotification } from 'element-plus'
import SearchBox from './SearchBox.vue'
import ChatHistory, { type ChatItem, type ChatSection } from './ChatHistory.vue'
import ModelDropdown from './ModelDropdown.vue'
import type { ModelItem } from '@/api/chat/types'
import { useChatStore } from '@/stores/modules/chat'
import { groupSessionsByTime } from '@/utils/timeGrouping'

const { t } = useI18n()


// 定义props
const props = defineProps<{
  selectedModel?: ModelItem | null
  collapsed?: boolean
  isTyping?: boolean
}>()

// 定义事件
const emit = defineEmits<{
  'toggle-sidebar': [collapsed: boolean]
  'model-selected': [model: ModelItem | null]
  'new-chat': []
  'chat-select': [chat: ChatItem]
  'chat-menu': [chat: ChatItem]
  'search': [query: string]
  'chat-delete': [chat: ChatItem]
  'interrupt-ai': []
}>()

// 侧边栏收起状态管理（受控）
const isCollapsed = computed(() => !!props.collapsed)

// 图片加载错误状态
const selectedModelImageError = ref(false)
// 搜索查询
const searchQuery = ref('')
const chatStore = useChatStore()

const activeChat = computed(() => chatStore.activeSessionId || '')
// 模型选择对话框状态
const showModelDialog = ref(false)

// 聊天记录数据
const chatSections = computed<ChatSection[]>(() => {
  const sessions = chatStore.sessions.slice()
  
  // 使用时间分组工具对会话进行分组
  const timeGroups = groupSessionsByTime(sessions)
  
  // 将分组转换为ChatSection格式
  return timeGroups.map(({ group, items }) => ({
    id: group.id,
    title: group.title.toUpperCase(),
    chats: items.map(s => ({
      id: s.id,
      title: s.title || t('chat.sidebar.newSession'),
      lastMessage: (chatStore.messagesBySession[s.id]?.slice(-1)[0]?.content) || '',
      timestamp: new Date(s.createdAt)
    }))
  }))
})

// 切换侧边栏状态
const toggleSidebar = () => {
  emit('toggle-sidebar', !isCollapsed.value)
}

// 处理新建聊天
const handleNewChat = () => {
  // 如果AI正在输出，先中断
  if (props.isTyping) {
    emit('interrupt-ai')
  }
  
  // 检查当前最新会话是否为空
  const latestSession = chatStore.latestSession
  if (latestSession && latestSession.messageCount === 0) {
    // 如果最新会话为空，提示用户并设为活跃会话
    ElNotification.info(t('chat.sidebar.currentSessionLatest'))
    chatStore.setActiveSession(latestSession.id)
    return
  }
  
  // 使用当前选择的模型创建新会话
  const currentModelId = props.selectedModel?.id || null
  const id = chatStore.createSession(currentModelId)
  chatStore.setActiveSession(id)
}

// 打开模型选择对话框
const openModelDialog = () => {
  showModelDialog.value = true
}

// 处理模型选择
const handleModelSelected = (model: ModelItem | null) => {
  emit('model-selected', model)
  showModelDialog.value = false
}

// 处理搜索
const handleSearch = (query: string) => {
  emit('search', query)
}

// 处理搜索清空
const handleSearchClear = () => {
  searchQuery.value = ''
  emit('search', '')
}

// 处理聊天选择
const handleChatSelect = (chat: ChatItem) => {
  chatStore.setActiveSession(chat.id)
  emit('chat-select', chat)
}

// 处理聊天菜单
const handleChatMenu = (chat: ChatItem) => {
  emit('chat-menu', chat)
}

// 处理聊天删除
const handleChatDelete = (chat: ChatItem) => {
  emit('chat-delete', chat)
}

// 处理选中模型图片加载错误
const handleSelectedModelImageError = () => {
  selectedModelImageError.value = true
}
</script>

<style scoped>
.sidebar {
  width: 260px;
  height: 100%;
  background: var(--el-bg-color);
  border-right: 1px solid var(--el-border-color-lighter);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  transition: width 0.3s ease;
}

/* 收起状态 */
.sidebar-collapsed {
  width: 60px;
}

.sidebar-collapsed .action-buttons {
  justify-content: center;
}

.sidebar-collapsed .collapse-btn {
  margin: 0;
}

/* 顶部区域 */
.sidebar-header {
  padding: 12px;
  border-bottom: 1px solid var(--el-border-color-extra-light);
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.collapse-btn {
  width: 36px;
  height: 36px;
  border: none;
  background: var(--el-fill-color);
  color: var(--el-text-color-regular);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  cursor: pointer;
  transition: all 0.2s ease;
  padding-left: 10px;
}

.collapse-btn:hover {
  background: var(--el-fill-color-light);
  color: var(--el-text-color-primary);
}

.action-buttons {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 8px;
  align-items: center;
}

.btn {
  height: 36px;
  border-radius: 10px;
  border: 1px solid transparent;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s ease, color 0.2s ease, box-shadow 0.2s ease, transform 0.08s ease;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 0 14px;
  position: relative;
  box-shadow: 0 1.5px 3px rgba(0, 0, 0, 0.06), 0 1px 1px rgba(0, 0, 0, 0.04);
}

.btn svg {
  width: 16px;
  height: 16px;
}

.btn:hover {
  transform: translateY(-1px);
}

.btn:active {
  transform: translateY(0) scale(0.98);
}

.btn:focus-visible {
  outline: none;
  box-shadow: 0 0 0 2px var(--el-color-primary-light-9), 0 1.5px 3px rgba(0, 0, 0, 0.06);
}

html[class^="theme-dark"] .sidebar .btn,
html[class*=" theme-dark"] .sidebar .btn {
  box-shadow: 0 1.5px 3px rgba(0, 0, 0, 0.35), 0 1px 1px rgba(0, 0, 0, 0.25);
}

.btn-new-chat {
  background: var(--el-color-primary);
  color: white;
  flex: 1;
  justify-content: center;
}

.btn-new-chat:hover {
  background: var(--el-color-primary-dark-2);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.btn-add-model {
  background: var(--el-fill-color);
  color: var(--el-text-color-regular);
  justify-content: flex-start;
  border-color: var(--el-border-color-light);
  width: 100%;
}

.btn-add-model:hover {
  background: var(--el-fill-color-light);
  color: var(--el-text-color-primary);
  border-color: var(--el-color-primary-light-7);
}

/* 模型图标样式 */
.model-icon-small {
  width: 14px;
  height: 14px;
  border-radius: 2px;
  object-fit: cover;
  flex-shrink: 0;
  background: #000000;
  padding: 1px;
}

.model-icon-placeholder-small {
  width: 14px;
  height: 14px;
  background: #000000;
  border-radius: 2px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  border: 1px solid #333333;
}

.model-icon-placeholder-small svg {
  width: 8px;
  height: 8px;
}

.model-icon-fallback-small {
  width: 8px;
  height: 8px;
  color: #ffffff;
}

.model-name {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  text-align: left;
}

/* 响应式设计 */
@media (max-width: 960px) {
  .sidebar {
    width: 220px;
  }
  
  .action-buttons {
    grid-template-columns: 1fr auto;
  }
}

@media (max-width: 720px) {
  .sidebar {
    position: fixed;
    left: -260px;
    z-index: 1000;
    transition: left 0.3s ease;
  }
  
  .sidebar.open {
    left: 0;
  }
}
</style>

<style>
/* 模型选择对话框的全局样式 */
.el-dialog__header {
  padding: 20px 20px 10px 20px;
}

.el-dialog__title {
  font-size: 18px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.el-dialog__body {
  padding: 0 20px 20px 20px;
}

.el-dialog {
  border-radius: 12px;
  overflow: hidden;
}
</style> 