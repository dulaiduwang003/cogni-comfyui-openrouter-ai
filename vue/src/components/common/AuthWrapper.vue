<script setup lang="ts">
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { useAuthGuard } from '@/composables/useAuthGuard'

const { t } = useI18n()

interface Props {
  /** 是否在未认证时隐藏内容 */
  hideWhenUnauth?: boolean
  /** 未认证时显示的内容 */
  fallback?: string
  /** 点击时是否需要认证检查 */
  requireAuth?: boolean
  /** 未登录时的提示信息 */
  message?: string
}

const props = withDefaults(defineProps<Props>(), {
  hideWhenUnauth: false,
  fallback: '',
  requireAuth: false,
  message: ''
})

// 使用计算属性提供默认值和国际化支持
const fallbackText = computed(() => props.fallback || t('authWrapper.pleaseLogin'))
const messageText = computed(() => props.message || t('authWrapper.requireLogin'))

const emit = defineEmits<{
  click: []
}>()

const { isLoggedIn, executeWithAuth } = useAuthGuard()

const handleClick = () => {
  if (props.requireAuth) {
    executeWithAuth(() => {
      emit('click')
    }, messageText.value)
  } else {
    emit('click')
  }
}
</script>

<template>
  <div v-if="!hideWhenUnauth || isLoggedIn" @click="handleClick">
    <slot v-if="isLoggedIn" />
    <div v-else-if="!hideWhenUnauth" class="auth-fallback">
      {{ fallbackText }}
    </div>
  </div>
</template>

<style scoped>
.auth-fallback {
  color: var(--el-text-color-placeholder);
  font-size: 14px;
  text-align: center;
  padding: 20px;
}
</style> 