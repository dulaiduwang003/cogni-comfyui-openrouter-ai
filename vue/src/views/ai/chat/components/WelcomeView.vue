<template>
  <div class="welcome-view">
    <div class="welcome-content">

      <!-- 标题和描述 -->
      <div class="welcome-text">
        <h1 class="welcome-title">{{ t('chat.welcome.title') }}</h1>
        <p class="welcome-description">
          {{ t('chat.welcome.description') }}
        </p>
      </div>
    </div>

    <!-- 输入框区域 -->
    <div class="input-area">
      <MessageComposer 
        :chat-mode="false"
        :web-search-enabled="webSearchEnabled"
        :image-gen-enabled="imageGenEnabled"
        :selected-model="selectedModel"
        @websearch-change="handleWebSearchChange"
        @imagegen-change="handleImageGenChange"
        @send-message="handleSendMessage" 
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n'
import MessageComposer from './MessageComposer.vue'
import type { ModelItem } from '@/api/chat/types'

const { t } = useI18n()

interface Props {
  webSearchEnabled?: boolean
  imageGenEnabled?: boolean
  selectedModel?: ModelItem | null
}

defineProps<Props>()

interface Emits {
  (e: 'send-message', payload: { content: string; imageUrls?: string[]; pdfFiles?: { url: string; filename: string }[]; audioFiles?: { url: string }[] }): void
  (e: 'websearch-change', enabled: boolean): void
  (e: 'imagegen-change', enabled: boolean): void
}

const emit = defineEmits<Emits>()

const handleSendMessage = (payload: { content: string; imageUrls?: string[]; pdfFiles?: { url: string; filename: string }[]; audioFiles?: { url: string }[] }) => {
  emit('send-message', payload)
}

const handleWebSearchChange = (enabled: boolean) => {
  emit('websearch-change', enabled)
}

const handleImageGenChange = (enabled: boolean) => {
  emit('imagegen-change', enabled)
}
</script>

<style scoped>
.welcome-view {
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  max-width: 800px;
  margin: 0 auto;
  min-height: 600px;
}

.welcome-content {
  text-align: center;
  margin-bottom: 60px;
}

.ai-models {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 40px;
}

.model-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: var(--el-bg-color);
  border: 1px solid var(--el-border-color-light);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  transition: all 0.3s ease;
  cursor: pointer;
}

.model-icon:hover {
  background: var(--el-color-primary-light-9);
}
</style> 