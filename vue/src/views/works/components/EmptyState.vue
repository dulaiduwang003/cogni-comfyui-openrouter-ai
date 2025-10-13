<template>
  <div class="empty-state">
    <div class="empty-icon">
      <el-icon size="64"><Picture /></el-icon>
    </div>
    <h3>{{ t('works.empty.title') }}</h3>
    <p>{{ t('works.empty.description') }}</p>
    <el-button type="primary" @click="$emit('goToCreate')">
      <el-icon><Plus /></el-icon>
      {{ t('works.empty.createButton') }}
    </el-button>
  </div>
</template>

<script setup>
import { Picture, Plus } from '@element-plus/icons-vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

// Emits
defineEmits(['goToCreate'])
</script>

<style scoped>
.empty-state {
  text-align: center;
  padding: 80px 20px;
  color: var(--el-text-color-secondary);
}

.empty-icon {
  margin-bottom: 24px;
  color: var(--el-color-primary);
}

.empty-state h3 {
  font-size: 18px;
  margin: 0 0 8px 0;
  color: var(--el-text-color-primary);
}

.empty-state p {
  margin: 0 0 24px 0;
  font-size: 14px;
}
</style> 