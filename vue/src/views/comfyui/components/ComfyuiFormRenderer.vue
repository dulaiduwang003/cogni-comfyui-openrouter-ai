<template>
  <div class="comfyui-form-renderer">
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="5" animated />
    </div>
    
    <div v-else-if="error" class="error-container">
      <el-alert
        :title="t('comfyui.formRenderer.loadFailed')"
        :description="error"
        type="error"
        show-icon
        :closable="false"
      />
    </div>
    
    <div v-else-if="formData" class="form-container">
      <div class="form-content">
        <div>
          <el-form
          ref="formRef"
          :model="formValues"
          :rules="formRules"
          label-position="top"
          class="comfyui-form"
        >
          <div
            v-for="(formItem, index) in formData.formContainer"
            :key="index"
            class="form-item-wrapper"
          >
            <!-- 文本输入框 -->
            <TextPromptInput
              v-if="formItem.type === WorkflowFormTypeEnum.TEXT_PROMPT"
              :form-item="formItem"
              :model-value="formValues[getFieldKey(formItem)]"
              @update:model-value="updateFormValue(formItem, $event)"
            />

            <!-- 文件上传 -->
            <FileUpload
              v-else-if="[WorkflowFormTypeEnum.IMAGE_UPLOAD, WorkflowFormTypeEnum.AUDIO_UPLOAD, WorkflowFormTypeEnum.VIDEO_UPLOAD].includes(formItem.type)"
              :form-item="formItem"
              :model-value="formValues[getFieldKey(formItem)]"
              @update:model-value="updateFormValue(formItem, $event)"
            />

            <!-- 单选下拉框 -->
            <RadioSelector
              v-else-if="formItem.type === WorkflowFormTypeEnum.RADIO_SELECTOR"
              :form-item="formItem"
              :model-value="formValues[getFieldKey(formItem)]"
              @update:model-value="updateFormValue(formItem, $event)"
            />

            <!-- 多选组件 -->
            <CheckboxSelector
              v-else-if="formItem.type === WorkflowFormTypeEnum.CHECKBOX_SELECTOR"
              :form-item="formItem"
              :model-value="formValues[getFieldKey(formItem)]"
              @update:model-value="updateFormValue(formItem, $event)"
            />
          </div>
        </el-form>
        </div>
      </div>

      <div v-if="!props.hideSubmitButton" class="form-actions">
        <el-button
          type="primary"
          size="large"
          :loading="submitting"
          v-auth="handleSubmit"
          class="submit-btn"
        >
          <span v-if="submitting">{{ t('comfyui.formRenderer.generating') }}</span>
          <span v-else>
            {{ t('comfyui.formRenderer.startGenerate') }} 
            <span class="credits-text">{{ t('comfyui.formRenderer.creditsDeduct', { credits: formData?.creditsDeducted || 0 }) }}</span>
          </span>
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import { ElForm, ElSkeleton, ElAlert, ElButton } from 'element-plus'
import { useI18n } from 'vue-i18n'
import { comfyuiTaskApi } from '@/api/workflow-task/workflow-task'
import type { GetWorkflowInterfaceApi } from '@/api/workflow-task/types'
import { WorkflowFormTypeEnum } from '@/enums'
import TextPromptInput from './form-components/TextPromptInput.vue'
import FileUpload from './form-components/FileUpload.vue'
import RadioSelector from './form-components/RadioSelector.vue'
import CheckboxSelector from './form-components/CheckboxSelector.vue'

const { t } = useI18n()

interface Props {
  workflowId: number | null
  hideSubmitButton?: boolean
}

const props = defineProps<Props>()
const emit = defineEmits<{
  submit: [formData: Record<string, any>]
}>()

const loading = ref(false)
const error = ref('')
const submitting = ref(false)
const formRef = ref<InstanceType<typeof ElForm>>()
const formData = ref<GetWorkflowInterfaceApi.Result | null>(null)
const formValues = reactive<Record<string, any>>({})
const formRules = reactive<Record<string, any>>({})



// 获取表单字段的唯一键
const getFieldKey = (formItem: any) => {
  return `${formItem.nodeKey}_${formItem.inputs}`
}

// 更新表单值
const updateFormValue = (formItem: any, value: any) => {
  const key = getFieldKey(formItem)
  formValues[key] = value
}

// 加载工作流接口配置
const loadWorkflowInterface = async (workflowId: number) => {
  if (!workflowId) return
  
  loading.value = true
  error.value = ''
  
  try {
    const result = await comfyuiTaskApi.reqGetWorkflowResultModelflowsInterface({
      workflowId
    })
    
    formData.value = result
    
    // 初始化表单值和验证规则
    initFormValues()
    initFormRules()
    
  } catch (err: any) {
    error.value = err.message || t('comfyui.formRenderer.loadWorkflowFailed')
    console.error('Failed to load workflow interface:', err)
  } finally {
    loading.value = false
  }
}

// 初始化表单值
const initFormValues = () => {
  if (!formData.value) return
  
  // 清空之前的值
  Object.keys(formValues).forEach(key => {
    delete formValues[key]
  })
  
  // 设置默认值
  formData.value.formContainer.forEach(formItem => {
    const key = getFieldKey(formItem)
    if (formItem.type === WorkflowFormTypeEnum.CHECKBOX_SELECTOR) {
      formValues[key] = []
    } else {
      formValues[key] = ''
    }
  })
}

// 初始化表单验证规则
const initFormRules = () => {
  if (!formData.value) return
  
  // 清空之前的规则
  Object.keys(formRules).forEach(key => {
    delete formRules[key]
  })
  
  // 设置验证规则
  formData.value.formContainer.forEach(formItem => {
    const key = getFieldKey(formItem)
    const rules: any[] = []
    
    // 必填验证
    if (formItem.required) {
      rules.push({
          required: true,
          message: t('comfyui.formRenderer.validation.pleaseEnter', { field: formItem.tips || t('comfyui.formRenderer.validation.thisField') }),
          trigger: ['blur', 'change']
      })
    }
    
    // 长度验证（仅针对输入类型组件）
    if (formItem.size && [WorkflowFormTypeEnum.TEXT_PROMPT].includes(formItem.type)) {
      rules.push({
        max: formItem.size,
        message: t('comfyui.formRenderer.validation.maxLength', { max: formItem.size }),
        trigger: ['blur', 'change']
      })
    }
    
    // 如果有验证规则则设置
    if (rules.length > 0) {
      formRules[key] = rules
    }
  })
}

// 处理表单提交
const handleSubmit = async () => {

  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    submitting.value = true
    
    // 构建提交数据
    const submitData = {
      workflowId: formData.value?.workflowId,
      formData: { ...formValues }
    }
    
    emit('submit', submitData)
    
  } catch (error) {
    console.error('Form validation failed:', error)
    // 只有在验证失败时才重置submitting状态
    submitting.value = false
  }
}

// 单独的校验方法
const validateForm = async () => {
  if (!formRef.value) throw new Error('Form not ready')
  
  try {
    await formRef.value.validate()
  } catch (error) {
    const validationError = new Error('Form validation failed')
    validationError.name = 'ValidationError'
    throw validationError
  }
}

// 不包含校验的提交方法（校验已在外部完成）
const submitFormWithoutValidation = async () => {
  if(loading.value) return
  submitting.value = true
  
  try {
    // 构建提交数据
    const submitData = {
      workflowId: formData.value?.workflowId,
      formData: { ...formValues }
    }
    
    emit('submit', submitData)
    
  } catch (error) {
    console.error('Form submission failed:', error)
    throw error
  } finally {
    submitting.value = false
  }
}

// 重置表单方法
const resetForm = () => {
  if (formRef.value) {
    // 使用 Element Plus 的 resetFields 方法重置表单
    formRef.value.resetFields()
  }
  // 手动设置默认值（因为 resetFields 可能无法处理所有字段）
  if (formData.value) {
    formData.value.formContainer.forEach(formItem => {
      const key = getFieldKey(formItem)
      if (formItem.type === WorkflowFormTypeEnum.CHECKBOX_SELECTOR) {
        formValues[key] = []
      } else {
        formValues[key] = ''
      }
    })
  }
}

// 暴露方法和状态给父组件调用
defineExpose({
  submitForm: handleSubmit,
  validateForm: validateForm,
  submitFormWithoutValidation: submitFormWithoutValidation,
  resetForm: resetForm,
  setSubmitting: (value: boolean) => {
    submitting.value = value
  }
})

// 监听 workflowId 变化
watch(
  () => props.workflowId,
  (newId) => {
    if (newId) {
      loadWorkflowInterface(newId)
    } else {
      formData.value = null
      error.value = ''
    }
  },
  { immediate: true }
)
</script>

<style scoped>
.comfyui-form-renderer {
  height: 100%;
  display: flex;
  flex-direction: column;

}

.loading-container,
.error-container {
  padding: 20px;
}

.form-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.form-content {
  flex: 1;
  overflow-y: auto;
  padding: 20px;

}
.form-content >div{
  padding-bottom: 6px;
}

.comfyui-form {
  height: 100%;
}

.form-item-wrapper {
  margin-bottom: 24px;

}

.form-actions {
  flex-shrink: 0;
  padding-top: 10px;
  padding-left: 10px;
  padding-right: 10px;
  padding-bottom: 10px;
  background: var(--el-bg-color);
  border-top: 1px solid var(--el-border-color-lighter);
}

.submit-btn {
  width: 100%;
  height: 40px;
  font-size: 15px;
  font-weight: 500;
}

.credits-text {
  font-size: 12px;
  opacity: 0.8;
}

/* 使用全局滚动条样式 */
</style>
