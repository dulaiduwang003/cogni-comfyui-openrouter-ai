<template>
  <div class="checkbox-selector">
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
      <div class="checkbox-group">
        <el-checkbox-group
          :model-value="modelValue"
          @update:model-value="handleChange"
          class="checkbox-list"
        >
          <el-checkbox
            v-for="option in options"
            :key="option.value"
            :label="option.value"
            class="checkbox-item"
          >
            {{ option.key }}
          </el-checkbox>
        </el-checkbox-group>
      </div>

      <div v-if="modelValue && modelValue.length > 0" class="selected-summary">
        已选择 {{ modelValue.length }} 项：{{ getSelectedLabels() }}
      </div>
    </el-form-item>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { ElFormItem, ElCheckboxGroup, ElCheckbox, ElTooltip, ElIcon } from 'element-plus'
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
  modelValue: string[]
}

interface OptionItem {
  key: string
  value: string
}

const props = defineProps<Props>()
const emit = defineEmits<{
  'update:model-value': [value: string[]]
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
    console.error('Failed to parse checkbox options:', error)
  }
  
  return []
})

const handleChange = (value: (string | number)[]) => {
  // 转换为字符串
  const stringValues = value.map(v => String(v))
  emit('update:model-value', stringValues)
}

// 获取已选择项的标签
const getSelectedLabels = () => {
  if (!props.modelValue || props.modelValue.length === 0) {
    return ''
  }
  
  const selectedOptions = options.value.filter(option => 
    props.modelValue.includes(option.value)
  )
  
  return selectedOptions.map(option => option.key).join('、')
}
</script>

<style scoped>
.checkbox-selector {
  width: 100%;
}

.checkbox-group {
  width: 100%;
  padding: 12px;

  border: 1px solid var(--el-border-color);
  border-radius: 8px;
  transition: all 0.3s ease;
}

.checkbox-group:hover {
  border-color: var(--el-border-color-hover);
}

.checkbox-list {
  width: 100%;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.checkbox-item {
  margin-right: 0;
  margin-bottom: 0;
  flex: 0 0 auto;
  min-width: fit-content;
  padding: 6px 8px;
  border-radius: 6px;
  transition: background-color 0.2s ease;
}

.checkbox-item:hover { 
  background-color: var(--el-fill-color-light);
}

.checkbox-item :deep(.el-checkbox__label) {
  font-size: 14px;
  color: var(--el-text-color-primary);
  line-height: 1.5;
  word-break: break-word;
  margin-left: 8px;
}

.checkbox-item :deep(.el-checkbox__input) {
  margin-right: 0;
}

.checkbox-item :deep(.el-checkbox__inner) {
  background-color: var(--el-bg-color);
  border-color: var(--el-border-color);
  transition: all 0.3s ease;
}

.checkbox-item :deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
  background-color: var(--el-color-primary);
  border-color: var(--el-color-primary);
}

.checkbox-item :deep(.el-checkbox__input.is-checked .el-checkbox__inner::after) {
  border-color: #fff;
}

.checkbox-item :deep(.el-checkbox__input:hover .el-checkbox__inner) {
  border-color: var(--el-color-primary);
}



.selected-summary {
  margin-top: 8px;
  padding: 8px 12px;
  background-color: var(--el-color-primary-light-9);
  border: 1px solid var(--el-color-primary-light-7);
  border-radius: 6px;
  font-size: 12px;
  color: var(--el-color-primary);
  line-height: 1.4;
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

/* 当选项较多时，限制最大高度并添加滚动 */
.checkbox-list {
  max-height: 150px;
  overflow-y: auto;
  padding-right: 4px;
}

/* 使用全局滚动条样式 */

/* 暗色模式适配 */
@media (prefers-color-scheme: dark) {
  .checkbox-group {
    background-color: var(--el-bg-color-page);
    border-color: var(--el-border-color-dark);
  }

  .checkbox-item:hover {
    background-color: var(--el-fill-color);
  }

  .checkbox-item :deep(.el-checkbox__inner) {
    background-color: var(--el-fill-color-dark);
    border-color: var(--el-border-color-dark);
  }

  .selected-summary {
    background-color: var(--el-fill-color-dark);
    border-color: var(--el-border-color-dark);
    color: var(--el-text-color-secondary);
  }
}

/* Element Plus 暗色主题适配 */
.dark .checkbox-group {
  background-color: var(--el-bg-color-page);
  border-color: var(--el-border-color-dark);
}

.dark .checkbox-item:hover {
  background-color: var(--el-fill-color);
}

.dark .checkbox-item :deep(.el-checkbox__label) {
  color: var(--el-text-color-primary);
}

.dark .checkbox-item :deep(.el-checkbox__inner) {
  background-color: var(--el-fill-color-dark);
  border-color: var(--el-border-color-dark);
}

.dark .checkbox-item :deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
  background-color: var(--el-color-primary);
  border-color: var(--el-color-primary);
}

.dark .selected-summary {
  background-color: var(--el-fill-color-dark);
  border-color: var(--el-border-color-dark);
  color: var(--el-text-color-secondary);
}
</style>
