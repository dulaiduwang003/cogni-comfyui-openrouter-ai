<template>
  <div class="radio-selector">
    <el-form-item
      :prop="fieldKey"
      :required="formItem.required"
    >
      <template #label>
        <div class="label-with-tooltip">
          <span>{{ formItem.tips }}</span>
          <el-tooltip v-if="formItem.template" :content="formItem.template" placement="top">
            <el-icon class="tooltip-icon">
              <QuestionFilled />
            </el-icon>
          </el-tooltip>
        </div>
      </template>
      <el-select
        :model-value="modelValue"
        @update:model-value="handleChange"
        :placeholder="getPlaceholder()"
        clearable
        class="radio-select"
      >
        <el-option
          v-for="option in options"
          :key="option.value"
          :label="option.key"
          :value="option.value"
        />
      </el-select>
    </el-form-item>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { ElFormItem, ElSelect, ElOption, ElTooltip, ElIcon } from 'element-plus'
import { QuestionFilled } from '@element-plus/icons-vue'

interface Props {
  formItem: {
    inputs: string
    nodeKey: string
    tips: string
    type: string
    required: boolean
    options: string
    template?: string | null
    size?: number
  }
  modelValue: string
}

interface OptionItem {
  key: string
  value: string
}

const props = defineProps<Props>()
const emit = defineEmits<{
  'update:model-value': [value: string]
}>()

const fieldKey = computed(() => {
  return `${props.formItem.nodeKey}_${props.formItem.inputs}`
})

// 解析选项数据
const options = computed<OptionItem[]>(() => {
  if (!props.formItem.options) {
    return []
  }
  
  try {
    const parsed = JSON.parse(props.formItem.options)
    if (Array.isArray(parsed)) {
      return parsed.map(item => ({
        key: item.key || item.label || item.text || String(item.value),
        value: String(item.value)
      }))
    }
  } catch (error) {
    console.error('Failed to parse radio options:', error)
  }
  
  return []
})

const handleChange = (value: string) => {
  emit('update:model-value', value)
}

const getPlaceholder = () => {
  if (props.formItem.tips) {
    return `${props.formItem.tips}`
  }
  return '请选择选项'
}
</script>

<style scoped>
.radio-selector {
  width: 100%;
}

.radio-select {
  width: 100%;
}

.radio-select :deep(.el-select) {
  width: 100%;
}

.radio-select :deep(.el-select__wrapper) {
  background-color: var(--el-bg-color);
  border: 1px solid var(--el-border-color);
  border-radius: 8px;
  transition: all 0.3s ease;
  box-shadow: none;
}

.radio-select :deep(.el-select__wrapper:hover) {
  border-color: var(--el-border-color-hover);
}

.radio-select :deep(.el-select__wrapper.is-focused) {
  border-color: var(--el-color-primary);
  box-shadow: 0 0 0 2px var(--el-color-primary-light-8);
}

.radio-select :deep(.el-select__placeholder) {
  color: var(--el-text-color-placeholder);
  font-style: italic;
}

.radio-select :deep(.el-select__selected-item) {
  color: var(--el-text-color-primary);
}

.radio-select :deep(.el-select__caret) {
  color: var(--el-text-color-regular);
}



.el-form-item {
  margin-bottom: 0;
}

.el-form-item :deep(.el-form-item__label) {
  font-weight: 500;
  color: var(--el-text-color-primary);
  margin-bottom: 12px;
}

.label-with-tooltip {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.tooltip-icon {
  font-size: 14px;
  color: var(--el-text-color-secondary);
  cursor: help;
  transition: color 0.3s ease;
}

.tooltip-icon:hover {
  color: var(--el-color-primary);
}

/* 暗色模式适配 */
@media (prefers-color-scheme: dark) {
  .radio-select :deep(.el-select__wrapper) {
    background-color: var(--el-bg-color-page);
    border-color: var(--el-border-color-dark);
  }

  .radio-select :deep(.el-select__wrapper.is-focused) {
    box-shadow: 0 0 0 2px var(--el-color-primary-dark-2);
  }
}

/* Element Plus 暗色主题适配 */
.dark .radio-select :deep(.el-select__wrapper) {
  background-color: var(--el-bg-color-page);
  border-color: var(--el-border-color-dark);
  color: var(--el-text-color-primary);
}

.dark .radio-select :deep(.el-select__wrapper.is-focused) {
  border-color: var(--el-color-primary);
  box-shadow: 0 0 0 2px var(--el-color-primary-dark-2);
}

.dark .radio-select :deep(.el-select__selected-item) {
  color: var(--el-text-color-primary);
}

.dark .radio-select :deep(.el-select__placeholder) {
  color: var(--el-text-color-placeholder);
}

/* 下拉选项样式 - 使用全局样式 */
:global(.el-select-dropdown) {
  background-color: var(--el-bg-color) !important;
  border: 1px solid var(--el-border-color) !important;
  border-radius: 8px !important;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1) !important;
}

:global(.el-select-dropdown__item) {
  color: var(--el-text-color-primary) !important;
  background-color: transparent !important;
  transition: all 0.2s ease !important;
}

:global(.el-select-dropdown__item:hover) {
  background-color: var(--el-fill-color-light) !important;
}

:global(.el-select-dropdown__item.is-selected) {
  background-color: var(--el-color-primary-light-9) !important;
  color: var(--el-color-primary) !important;
  font-weight: 500 !important;
}

/* 暗色模式下拉选项 */
@media (prefers-color-scheme: dark) {
  :global(.el-select-dropdown) {
    background-color: var(--el-bg-color-page) !important;
    border-color: var(--el-border-color-dark) !important;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3) !important;
  }

  :global(.el-select-dropdown__item:hover) {
    background-color: var(--el-fill-color-dark) !important;
  }

  :global(.el-select-dropdown__item.is-selected) {
    background-color: var(--el-color-primary-dark-2) !important;
  }
}

/* Element Plus 暗色主题下拉选项 */
.dark :global(.el-select-dropdown) {
  background-color: var(--el-bg-color-page) !important;
  border-color: var(--el-border-color-dark) !important;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3) !important;
}

.dark :global(.el-select-dropdown__item) {
  color: var(--el-text-color-primary) !important;
}

.dark :global(.el-select-dropdown__item:hover) {
  background-color: var(--el-fill-color-dark) !important;
}

.dark :global(.el-select-dropdown__item.is-selected) {
  background-color: var(--el-color-primary-dark-2) !important;
  color: var(--el-color-primary-light-3) !important;
}
</style>
