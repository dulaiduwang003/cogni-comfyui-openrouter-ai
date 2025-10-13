<script setup lang="ts">
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
const model = defineModel<boolean>()
</script>

<template>
  <div class="terms-agreement">
    <el-checkbox v-model="model">
      {{ t('auth.agreeTo') }}
      <a href="/terms" target="_blank">{{ t('auth.termsOfService') }}</a>
      {{ t('auth.and') }}
      <a href="/privacy" target="_blank">{{ t('auth.privacyPolicy') }}</a>
    </el-checkbox>
  </div>
</template>

<style scoped>
.terms-agreement {
  margin: -8px 0 12px;
  font-size: 12px;
}

.terms-agreement a {
  color: var(--el-color-primary);
  text-decoration: none;
}

.terms-agreement a:hover {
  text-decoration: underline;
}

:deep(.el-checkbox__label) {
  color: var(--el-text-color-regular);
  font-size: 12px;
  white-space: normal;
  line-height: 1.2;
}

:deep(.el-checkbox__inner) {
  background-color: transparent;
}

:deep(.el-checkbox.is-checked .el-checkbox__label) {
  color: var(--el-text-color-regular);
}
</style> 