<template>
  <div class="edit-nickname">

    <!-- Form Section -->
    <div class="form-section">
      <el-form 
        ref="formRef"
        :model="form" 
        :rules="rules" 
        label-position="top"
        @submit.prevent="handleSubmit"
      >
        <!-- Current Nickname Display -->
        <div class="current-nickname">
          <label class="field-label">{{ t('profile.nickname.currentLabel') }}</label>
          <div class="current-value">
            <el-icon class="current-icon">
              <User />
            </el-icon>
            <span class="current-text">{{ currentNickname || t('profile.nickname.notSet') }}</span>
          </div>
        </div>
        
        <!-- New Nickname Input -->
        <el-form-item :label="t('profile.nickname.newLabel')" prop="nickname" class="nickname-field">
          <el-input 
            v-model="form.nickname"
            :placeholder="t('profile.nickname.placeholder')"
            maxlength="20"

            clearable
            size="large"
            @keyup.enter="handleSubmit"
          >
            <template #prefix>
              <el-icon class="input-icon">
                <EditPen />
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
      </el-form>
    </div>
    
    <!-- Rules Section -->
    <div class="rules-section">
      <div class="rules-header">
        <el-icon class="rules-icon">
          <InfoFilled />
        </el-icon>
        <span class="rules-title">{{ t('profile.nickname.rulesTitle') }}</span>
      </div>
      <ul class="rules-list">
        <li class="rule-item">
          <el-icon class="rule-icon">
            <Check />
          </el-icon>
          <span>{{ t('profile.nickname.rule1') }}</span>
        </li>
        <li class="rule-item">
          <el-icon class="rule-icon">
            <Check />
          </el-icon>
          <span>{{ t('profile.nickname.rule2') }}</span>
        </li>
        <li class="rule-item">
          <el-icon class="rule-icon">
            <Check />
          </el-icon>
          <span>{{ t('profile.nickname.rule3') }}</span>
        </li>
        <li class="rule-item">
          <el-icon class="rule-icon">
            <Check />
          </el-icon>
          <span>{{ t('profile.nickname.rule4') }}</span>
        </li>
      </ul>
    </div>
    
    <!-- Actions Section -->
    <div class="actions-section">
      <el-button 
        @click="handleCancel"
        size="large"
        class="cancel-btn"
      >
        {{ t('profile.nickname.cancel') }}
      </el-button>
      <el-button 
        type="primary" 
        @click="handleSubmit"
        :loading="submitting"
        :disabled="!isFormValid"
        size="large"
        class="submit-btn"
      >
        <el-icon v-if="!submitting" class="btn-icon">
          <Check />
        </el-icon>
        {{ submitting ? t('profile.nickname.saving') : t('profile.nickname.save') }}
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElNotification, ElForm } from 'element-plus'
import { User, EditPen, InfoFilled, Check } from '@element-plus/icons-vue'
import { userApi } from '@/api/user/user'
import { useUserStore } from '@/stores/modules/user'

const { t } = useI18n()

interface Props {
  currentNickname: string
}

const props = defineProps<Props>()

const emit = defineEmits<{

  cancel: []
}>()

const userStore = useUserStore()

// Reactive data
const formRef = ref<InstanceType<typeof ElForm>>()
const submitting = ref(false)

const form = reactive({
  nickname: props.currentNickname || ''
})

// Validation rules
const rules = computed(() => ({
  nickname: [
    { required: true, message: t('profile.nickname.required'), trigger: 'blur' },
    { min: 2, max: 20, message: t('profile.nickname.lengthError'), trigger: 'blur' },
    {
      pattern: /^(?!^\d+$)[a-zA-Z0-9\u4e00-\u9fa5]+$/,
      message: t('profile.nickname.formatError'),
      trigger: 'blur'
    },
    {
      validator: (_rule: any, value: string, callback: Function) => {
        if (value === props.currentNickname) {
          callback(new Error(t('profile.nickname.sameError')))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}))

// Computed
const isFormValid = computed(() => {
  return form.nickname.trim() !== '' && 
         form.nickname !== props.currentNickname &&
         form.nickname.length >= 2 && 
         form.nickname.length <= 20 &&
         /^(?!^\d+$)[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(form.nickname)
})

// Methods
const resetForm = () => {
  form.nickname = props.currentNickname || ''
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

const handleCancel = () => {
  resetForm()
  emit('cancel')
}

// 暴露重置方法给父组件
defineExpose({
  reset: resetForm
})

const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    const valid = await formRef.value.validate()
    if (!valid) return
  } catch {
    return
  }
  
  if (!isFormValid.value) {
    ElNotification.error(t('profile.nickname.invalidError'))
    return
  }
  
  submitting.value = true
  
  try {
    await userApi.reqUpdateNickname({
      nickname: form.nickname.trim()
    })
    // Refresh user info after nickname update
    await userStore.fetchUserInfo()
    ElNotification.success(t('profile.nickname.updateSuccess'))

  } catch (error) {
    console.error('昵称更新失败:', error)
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.edit-nickname {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.edit-header {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 15px 20px;
  background-color: var(--el-color-primary-light-9);
  border-radius: 10px;
  margin-bottom: 20px;
}

.header-icon {
  color: var(--el-color-primary);
}

.header-text h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.header-text p {
  margin: 4px 0 0 0;
  font-size: 14px;
  color: var(--el-text-color-secondary);
}

.form-section {
  padding: 0 20px;
  background-color: var(--el-bg-color-page);
  border-radius: 10px;
  box-shadow: var(--el-box-shadow-light);
}

.current-nickname {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 15px 0;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.field-label {
  font-size: 14px;
  font-weight: 500;
  color: var(--el-text-color-regular);
}

.current-value {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-grow: 1;
}

.current-icon {
  color: var(--el-text-color-secondary);
}

.current-text {
  font-size: 16px;
  font-weight: 500;
  color: var(--el-text-color-primary);
}

.nickname-field {
  margin-top: 20px;
}

.rules-section {
  padding: 0 20px;
  background-color: var(--el-bg-color-page);
  border-radius: 10px;
  box-shadow: var(--el-box-shadow-light);
  margin-top: 20px;
}

.rules-header {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 15px 0;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.rules-icon {
  color: var(--el-color-primary);
}

.rules-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.rules-list {
  padding: 15px 0;
  margin: 0;
  list-style: none;
}

.rule-item {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
}

.rule-icon {
  color: var(--el-color-success);
}

.rule-item span {
  font-size: 14px;
  color: var(--el-text-color-regular);
  line-height: 1.6;
}

.actions-section {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px;
  border-top: 1px solid var(--el-border-color-lighter);
}

.cancel-btn {
  border-radius: 8px !important;
  font-weight: 500 !important;
  transition: all 0.2s ease !important;
  border: 1px solid var(--el-border-color) !important;
  background-color: var(--el-fill-color-lighter) !important;
  color: var(--el-text-color-regular) !important;
}

.cancel-btn:hover {
  border-color: var(--el-border-color-dark) !important;
  background-color: var(--el-fill-color) !important;
  color: var(--el-text-color-primary) !important;
}

.submit-btn {
  border-radius: 8px !important;
  font-weight: 500 !important;
  transition: all 0.2s ease !important;
  background-color: var(--el-color-primary) !important;
  border-color: var(--el-color-primary) !important;
  color: white !important;
}

.submit-btn:hover {
  background-color: var(--el-color-primary-light-3) !important;
  border-color: var(--el-color-primary-light-3) !important;
}

.submit-btn.is-disabled {
  background-color: var(--el-color-primary-light-5) !important;
  border-color: var(--el-color-primary-light-5) !important;
  color: white !important;
  opacity: 0.6 !important;
}

.btn-icon {
  margin-right: 8px;
}

/* Element Plus form overrides */
:deep(.el-form-item__label) {
  font-weight: 600 !important;
  color: var(--el-text-color-primary) !important;
  font-size: 16px !important;
  margin-bottom: 12px !important;
}

/* 输入框样式优化 */
:deep(.el-input__wrapper) {
  background-color: var(--el-bg-color) !important;
  border: 2px solid var(--el-border-color-light) !important;
  transition: all 0.2s ease !important;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06) !important;
  border-radius: 12px !important;
  padding: 12px 16px !important;
  min-height: 48px !important;
}

:deep(.el-input__wrapper):hover {
  border-color: var(--el-color-primary-light-5) !important;
  background-color: var(--el-bg-color) !important;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08) !important;
}

:deep(.el-input__wrapper.is-focus) {
  border-color: var(--el-color-primary) !important;
  background-color: var(--el-bg-color) !important;
  box-shadow: 0 0 0 4px rgba(var(--el-color-primary-rgb), 0.1) !important;
}

:deep(.el-input__inner) {
  color: var(--el-text-color-primary) !important;
  background-color: transparent !important;
  transition: all 0.2s ease !important;
  font-size: 16px !important;
  font-weight: 500 !important;
  padding: 0 !important;
}

:deep(.el-input__inner::placeholder) {
  color: var(--el-text-color-placeholder) !important;
  font-weight: 400 !important;
}

:deep(.el-input__prefix) {
  margin-right: 12px !important;
}

:deep(.el-input__prefix .input-icon) {
  color: var(--el-color-primary) !important;
  font-size: 18px !important;
}

:deep(.el-input__suffix) {
  color: var(--el-text-color-regular) !important;
}

/* 字数统计样式 */
:deep(.el-input__count) {
  color: var(--el-text-color-placeholder) !important;
  background-color: transparent !important;
  font-size: 12px !important;
  position: absolute !important;
  right: 12px !important;
  bottom: -24px !important;
}

/* 清除按钮样式 */
:deep(.el-input__clear) {
  color: var(--el-text-color-placeholder) !important;
  font-size: 16px !important;
}

:deep(.el-input__clear):hover {
  color: var(--el-color-danger) !important;
}

/* Responsive Design */
@media (max-width: 640px) {
  .edit-nickname {
    gap: 16px;
  }
  
  .edit-header {
    padding: 12px 16px;
    border-radius: 8px;
    margin-bottom: 16px;
  }
  
  .header-text h3 {
    font-size: 18px;
  }
  
  .header-text p {
    font-size: 13px;
  }
  
  .form-section,
  .rules-section {
    padding: 0 16px;
    border-radius: 8px;
  }
  
  .current-nickname {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
    padding: 12px 0;
  }
  
  .current-value {
    width: 100%;
    padding: 10px;
    background-color: var(--el-fill-color-lighter);
    border-radius: 8px;
  }
  
  .rules-header {
    padding: 12px 0;
  }
  
  .rules-title {
    font-size: 16px;
  }
  
  .rules-list {
    padding: 12px 0;
  }
  
  .rule-item {
    margin-bottom: 8px;
  }
  
  .rule-item span {
    font-size: 13px;
  }
  
  .actions-section {
    flex-direction: column-reverse;
    gap: 10px;
    padding: 16px;
  }
  
  .cancel-btn,
  .submit-btn {
    width: 100% !important;
  }
  
  /* 移动端输入框优化 */
  :deep(.el-input__wrapper) {
    border-radius: 10px !important;
    padding: 10px 14px !important;
    min-height: 44px !important;
  }
  
  :deep(.el-input__inner) {
    font-size: 15px !important;
  }
  
  :deep(.el-input__count) {
    right: 10px !important;
    bottom: -20px !important;
  }
}

@media (max-width: 480px) {
  .edit-header {
    padding: 10px 12px;
  }
  
  .form-section,
  .rules-section {
    padding: 0 12px;
  }
  
  .actions-section {
    padding: 12px;
  }
}
</style> 