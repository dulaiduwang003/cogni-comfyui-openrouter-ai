<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { ElForm, ElFormItem, ElInput, ElButton, type FormRules, ElNotification } from 'element-plus'
import { useI18n } from 'vue-i18n'

const emit = defineEmits(['reset-password', 'send-code'])

const { t } = useI18n()

const formRef = ref<InstanceType<typeof ElForm> | null>(null)

const forgotPasswordForm = reactive({
  email: '',
  code: '',
  password: '',
  confirmPassword: ''
})

const validatePass = (_rule: any, value: any, callback: any) => {
  if (value === '') {
    callback(new Error(t('auth.pleaseEnterNewPassword')))
  } else {
    if (forgotPasswordForm.confirmPassword !== '') {
      if (!formRef.value) return
      formRef.value.validateField('confirmPassword')
    }
    callback()
  }
}

const validatePass2 = (_rule: any, value: any, callback: any) => {
  if (value === '') {
    callback(new Error(t('auth.pleaseConfirmPassword')))
  } else if (value !== forgotPasswordForm.password) {
    callback(new Error(t('auth.passwordNotMatch')))
  } else {
    callback()
  }
}

const rules = computed<FormRules>(() => ({
  email: [
    { required: true, message: t('auth.pleaseEnterEmail'), trigger: 'blur' },
    { type: 'email', message: t('auth.pleaseEnterValidEmail'), trigger: ['blur', 'change'] }
  ],
  code: [
    { required: true, message: t('auth.pleaseEnterCode'), trigger: 'blur' }
  ],
  password: [
    { required: true, validator: validatePass, trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validatePass2, trigger: 'blur' }
  ]
}))


const submitForm = () => {
  formRef.value?.validate((valid) => {
    if (valid) {
      emit('reset-password', { ...forgotPasswordForm })
    }
  })
}

const sendCode = () => {
  if (!forgotPasswordForm.email) {
    ElNotification.warning({
      title: t('common.warning'),
      message: t('auth.pleaseEnterEmail')
    })
    return
  }
  // Basic email format check before sending code
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(forgotPasswordForm.email)) {
    ElNotification.warning({
      title: t('common.warning'),
      message: t('auth.pleaseEnterValidEmail')
    })
    return
  }
  emit('send-code', forgotPasswordForm.email)
}
</script>

<template>
  <el-form ref="formRef" :model="forgotPasswordForm" :rules="rules" @submit.prevent="submitForm" autocomplete="off">
    <el-form-item prop="email">
      <el-input v-model="forgotPasswordForm.email" :placeholder="t('auth.email')" size="large" autocomplete="off"/>
    </el-form-item>
    <el-form-item prop="code">
        <div class="verification-code-wrapper">
          <el-input v-model="forgotPasswordForm.code" :placeholder="t('auth.verificationCode')" size="large" autocomplete="off" />
          <div class="custom-send-btn" @click="sendCode">{{ t('auth.getCode') }}</div>
        </div>
    </el-form-item>
    <el-form-item prop="password">
      <el-input v-model="forgotPasswordForm.password" type="password" :placeholder="t('auth.newPassword')" show-password size="large" autocomplete="new-password"/>
    </el-form-item>
    <el-form-item prop="confirmPassword">
      <el-input v-model="forgotPasswordForm.confirmPassword" type="password" :placeholder="t('auth.confirmPassword')" show-password size="large" autocomplete="new-password"/>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="submitForm" style="width: 100%" size="large">{{ t('auth.resetPassword') }}</el-button>
    </el-form-item>
  </el-form>
</template>

<style scoped>
.verification-code-wrapper {
  display: flex;
  width: 100%;
  gap: 10px;
}

.custom-send-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 100px;
  height: 40px;
  background-color: var(--el-color-primary);
  color: white;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.custom-send-btn:hover {
  background-color: var(--el-color-primary-light-3);
}

.custom-send-btn:active {
  background-color: var(--el-color-primary-dark-2);
}

:deep(.el-input) {
  --el-input-bg-color: var(--el-bg-color);
}

:deep(.el-input__wrapper) {
  background-color: var(--el-bg-color);
  box-shadow: 0 0 0 1px var(--el-border-color) inset;
}

:deep(.el-input__wrapper.is-focus) {
  background-color: var(--el-bg-color);
  box-shadow: 0 0 0 1px var(--el-color-primary) inset;
}
</style> 