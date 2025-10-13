<template>
  <div class="text-prompt-input">
    <el-form-item
      :prop="fieldKey"
      :required="formItem.required"
      class="custom-form-item"
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
      <div class="textarea-wrapper">
        <el-input
          :model-value="modelValue"
          @update:model-value="handleInput"
          type="textarea"
          :rows="4"
          :placeholder="getPlaceholder()"
          resize="vertical"
          :maxlength="formItem.size || 2000"
          show-word-limit
          class="prompt-textarea"
        />
      </div>

    </el-form-item>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { ElFormItem, ElInput, ElTooltip, ElIcon } from 'element-plus'
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

const props = defineProps<Props>()
const emit = defineEmits<{
  'update:model-value': [value: string]
}>()

const fieldKey = computed(() => {
  return `${props.formItem.nodeKey}_${props.formItem.inputs}`
})

const handleInput = (value: string) => {
  emit('update:model-value', value)
}

const getPlaceholder = () => {
  if (props.formItem.tips) {
    return `${props.formItem.tips}`
  }
  return '请输入文本内容'
}
</script>

<style scoped>
.text-prompt-input {
  width: 100% !important;
  display: block;
}

.custom-form-item {
  margin-bottom: 0;
  width: 100% !important;
}

.custom-form-item :deep(.el-form-item__label) {
  font-weight: 500;
  color: var(--el-text-color-primary);
  margin-bottom: 12px;
  font-size: 14px;
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

.custom-form-item :deep(.el-form-item__content) {
  width: 100% !important;
}

.textarea-wrapper {
  position: relative;
  width: 100% !important;
}

.prompt-textarea {
  width: 100% !important;
  display: block;
}

.prompt-textarea :deep(.el-textarea) {
  width: 100% !important;
  display: block;
}

.prompt-textarea :deep(.el-textarea__inner) {
  font-family: inherit;
  line-height: 1.6;
  resize: vertical;
  min-height: 100px;
  border-radius: 8px;
  border: 1px solid var(--el-border-color);
  background-color: var(--el-bg-color);
  color: var(--el-text-color-primary);
  transition: all 0.3s ease;
  padding: 12px;
  font-size: 14px;
  width: 100% !important;
  box-sizing: border-box;
  display: block;
}

.prompt-textarea :deep(.el-textarea__inner):focus {
  border-color: var(--el-color-primary);
  box-shadow: 0 0 0 2px var(--el-color-primary-light-8);
  outline: none;
}

.prompt-textarea :deep(.el-textarea__inner):hover {
  border-color: var(--el-border-color-hover);
}

.prompt-textarea :deep(.el-textarea__inner)::placeholder {
  color: var(--el-text-color-placeholder);
  font-style: italic;
}

.prompt-textarea :deep(.el-input__count) {
  background-color: var(--el-bg-color);
  color: var(--el-text-color-regular);
  border-radius: 4px 0 8px 0;
  padding: 2px 6px;
  font-size: 12px;
}

/* 暗色模式适配 */
@media (prefers-color-scheme: dark) {
  .prompt-textarea :deep(.el-textarea__inner) {
    background-color: var(--el-bg-color-page);
    border-color: var(--el-border-color-dark);
  }

  .prompt-textarea :deep(.el-textarea__inner):focus {
    box-shadow: 0 0 0 2px var(--el-color-primary-dark-2);
  }
}

/* Element Plus 暗色主题适配 */
.dark .prompt-textarea :deep(.el-textarea__inner) {
  background-color: var(--el-bg-color-page);
  border-color: var(--el-border-color-dark);
  color: var(--el-text-color-primary);
}

.dark .prompt-textarea :deep(.el-textarea__inner):focus {
  border-color: var(--el-color-primary);
  box-shadow: 0 0 0 2px var(--el-color-primary-dark-2);
}
</style>
