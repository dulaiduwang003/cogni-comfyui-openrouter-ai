<template>
  <div class="fab-rail">
    <button class="fab-item" :title="sidebarTitle" @click="$emit('toggle-sidebar')">
      <svg v-if="!sidebarCollapsed" width="18" height="18" viewBox="0 0 24 24" fill="none">
        <path d="m15 18-6-6 6-6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
      <svg v-else width="18" height="18" viewBox="0 0 24 24" fill="none">
        <path d="m9 18 6-6-6-6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
    </button>
    <button class="fab-item" :title="t('chat.fab.clearSession')" @click="$emit('clear-session')">
      <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
        <polyline points="3,6 5,6 21,6" stroke="currentColor" stroke-width="2"/>
        <path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" stroke="currentColor" stroke-width="2"/>
        <line x1="10" y1="11" x2="10" y2="18" stroke="currentColor" stroke-width="2"/>
        <line x1="14" y1="11" x2="14" y2="18" stroke="currentColor" stroke-width="2"/>
      </svg>
    </button>
    <button class="fab-item" :title="topBottomTitle" @click="$emit('toggle-top-bottom')">
      <svg v-if="pinnedTop" width="18" height="18" viewBox="0 0 24 24" fill="none">
        <polyline points="8,9 12,5 16,9" stroke="currentColor" stroke-width="2"/>
      </svg>
      <svg v-else width="18" height="18" viewBox="0 0 24 24" fill="none">
        <polyline points="8,15 12,19 16,15" stroke="currentColor" stroke-width="2"/>
      </svg>
    </button>
    <button class="fab-item" :title="fullscreenTitle" @click="$emit('toggle-fullscreen')">
      <svg v-if="isFullscreen" width="18" height="18" viewBox="0 0 24 24" fill="none">
        <path d="M8 3v3a2 2 0 0 1-2 2H3m18 0h-3a2 2 0 0 1-2-2V3m0 18v-3a2 2 0 0 1 2-2h3M3 16h3a2 2 0 0 1 2 2v3" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
      <svg v-else width="18" height="18" viewBox="0 0 24 24" fill="none">
        <path d="m21 21-6-6m6 6v-4.8m0 4.8h-4.8M3 16.2V21m0 0h4.8M3 21l6-6M21 7.8V3m0 0h-4.8M21 3l-6 6M3 7.8V3m0 0h4.8M3 3l6 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
    </button>
  </div>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n'
import { computed } from 'vue'

const { t } = useI18n()

const props = defineProps<{
  sidebarCollapsed?: boolean
  pinnedTop?: boolean
  isFullscreen?: boolean
}>()

// 计算提示文本
const sidebarTitle = computed(() => 
  props.sidebarCollapsed ? t('chat.sidebar.expand') : t('chat.sidebar.collapse')
)
const topBottomTitle = computed(() => 
  props.pinnedTop ? t('chat.fab.pinBottom') : t('chat.fab.pinTop')
)
const fullscreenTitle = computed(() => 
  props.isFullscreen ? t('chat.fab.exitFullscreen') : t('chat.fab.fullscreen')
)

defineEmits<{
  'toggle-sidebar': []
  'clear-session': []
  'toggle-top-bottom': []
  'toggle-fullscreen': []
}>()
</script>

<style scoped>
.fab-rail {
  position: absolute;
  right: 16px;
  top: 50%;
  transform: translateY(-50%);
  display: flex;
  flex-direction: column;
  gap: 8px;
  z-index: 10;
}

.fab-item {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  border: 1px solid var(--el-border-color-light);
  background: var(--el-bg-color);
  color: var(--el-text-color-regular);
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.fab-item:hover {
  background: var(--el-color-primary-light-9);
  border-color: var(--el-color-primary-light-7);
  color: var(--el-color-primary);
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.fab-item:active {
  transform: scale(0.95);
}

/* 响应式设计 */
@media (max-width: 720px) {
  .fab-rail {
    right: 12px;
    gap: 6px;
  }
  
  .fab-item {
    width: 36px;
    height: 36px;
  }
}
</style> 