<template>
  <transition name="loading-fade">
    <div v-if="visible" class="loading-overlay" :class="{ 'full-screen': fullScreen }">
      <div class="loading-backdrop" @click="handleBackdropClick"></div>
      <div class="loading-content">
        <div class="loading-spinner">
          <div class="spinner-ring">
            <div class="spinner-circle"></div>
            <div class="spinner-circle"></div>
            <div class="spinner-circle"></div>
          </div>
        </div>
        <div class="loading-text">{{ loadingText }}</div>
        <div class="loading-subtext" v-if="subtext">{{ subtext }}</div>
      </div>
    </div>
  </transition>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

interface Props {
  visible: boolean
  text?: string
  subtext?: string
  fullScreen?: boolean
  allowBackdropClose?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  text: '',
  subtext: '',
  fullScreen: false,
  allowBackdropClose: false
})

// 使用计算属性提供默认值和国际化支持
const loadingText = computed(() => props.text || t('loading.processing'))

const emit = defineEmits<{
  close: []
}>()

const handleBackdropClick = () => {
  if (props.allowBackdropClose) {
    emit('close')
  }
}
</script>

<style scoped>
.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  pointer-events: all;
}

.loading-overlay.full-screen {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
}

.loading-backdrop {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: var(--el-mask-color);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
}

.loading-content {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 32px;
  background: var(--el-bg-color);
  border-radius: 16px;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.12);
  border: 1px solid var(--el-border-color-extra-light);
  min-width: 280px;
  z-index: 1;
}

.loading-spinner {
  margin-bottom: 20px;
}

.spinner-ring {
  position: relative;
  width: 60px;
  height: 60px;
}

.spinner-circle {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  border: 3px solid transparent;
  border-radius: 50%;
  animation: spin 2s linear infinite;
}

.spinner-circle:nth-child(1) {
  border-top-color: var(--el-color-primary);
  animation-delay: 0s;
}

.spinner-circle:nth-child(2) {
  border-right-color: var(--el-color-primary-light-3);
  animation-delay: 0.4s;
  animation-duration: 1.8s;
}

.spinner-circle:nth-child(3) {
  border-bottom-color: var(--el-color-primary-light-5);
  animation-delay: 0.8s;
  animation-duration: 1.6s;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.loading-text {
  font-size: 16px;
  font-weight: 500;
  color: var(--el-text-color-primary);
  margin-bottom: 8px;
  letter-spacing: 0.3px;
}

.loading-subtext {
  font-size: 14px;
  color: var(--el-text-color-regular);
  line-height: 1.5;
  max-width: 240px;
}

/* 进入/离开动画 - 减少闪烁 */
.loading-fade-enter-active {
  transition: opacity 0.15s ease-out;
}

.loading-fade-leave-active {
  transition: opacity 0.15s ease-in;
}

.loading-fade-enter-from {
  opacity: 0;
}

.loading-fade-enter-to {
  opacity: 1;
}

.loading-fade-leave-from {
  opacity: 1;
}

.loading-fade-leave-to {
  opacity: 0;
}

.loading-fade-enter-active .loading-content {
  animation: loading-bounce-in 0.3s ease-out forwards;
}

.loading-fade-leave-active .loading-content {
  animation: loading-bounce-out 0.2s ease-in forwards;
}

@keyframes loading-bounce-in {
  0% {
    transform: scale(0.9);
    opacity: 0;
  }
  50% {
    transform: scale(1.02);
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}

@keyframes loading-bounce-out {
  0% {
    transform: scale(1);
    opacity: 1;
  }
  100% {
    transform: scale(0.95);
    opacity: 0;
  }
}

/* Element Plus 暗色主题适配 */
.dark .loading-backdrop {
  background: var(--el-mask-color);
}

.dark .loading-content {
  background: var(--el-bg-color-page);
  border-color: var(--el-border-color);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.3);
}

/* 移动端适配 */
@media (max-width: 768px) {
  .loading-content {
    padding: 24px;
    min-width: 240px;
    margin: 20px;
  }
  
  .spinner-ring {
    width: 50px;
    height: 50px;
  }
  
  .loading-text {
    font-size: 15px;
  }
  
  .loading-subtext {
    font-size: 13px;
  }
}
</style> 