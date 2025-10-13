<script setup lang="ts">
import { ElForm, ElFormItem, ElInput, ElButton, ElNotification, type FormRules } from 'element-plus'
import { reactive, ref, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import TermsAgreement from './TermsAgreement.vue'

defineOptions({
  name: 'CodeLoginForm'
})

const props = defineProps({
  loading: {
    type: Boolean,
    default: false
  }
})

const { t } = useI18n()

const codeLoginForm = reactive({
  email: '',
  code: ''
})

const agreementChecked = ref(false)
const formRef = ref()
const codeSending = ref(false)

const emit = defineEmits(['login-with-code', 'send-code'])

const rules = computed<FormRules>(() => ({
  email: [
    { required: true, message: t('auth.pleaseEnterEmail'), trigger: 'blur' },
    { type: 'email', message: t('auth.pleaseEnterValidEmail'), trigger: 'blur' }
  ],
  code: [
    { required: true, message: t('auth.pleaseEnterCode'), trigger: 'blur' }
  ]
}))

const handleLogin = () => {
  if (!agreementChecked.value) {
    ElNotification.warning({
      title: t('common.warning'),
      message: t('auth.pleaseAgreeTerms')
    })
    return
  }
  
  formRef.value.validate((valid: boolean) => {
    if (valid) {
      emit('login-with-code', codeLoginForm)
    }
  })
}

const sendCode = async () => {
  // 验证邮箱
  formRef.value.validateField('email', (valid: boolean) => {
    if (valid) {
      codeSending.value = true
      emit('send-code', codeLoginForm.email)
      
      // 模拟API调用延迟
      setTimeout(() => {
        codeSending.value = false
      }, 2000)
    }
  })
}
</script>

<template>
  <div class="code-login-form-container">
    <el-form 
      ref="formRef"
      :model="codeLoginForm" 
      :rules="rules"
      @submit.prevent 
      autocomplete="off"
    >
      <el-form-item prop="email">
        <el-input v-model="codeLoginForm.email" :placeholder="t('auth.email')" size="large" autocomplete="off" />
      </el-form-item>
      <el-form-item prop="code">
        <div class="verification-code-wrapper">
          <el-input v-model="codeLoginForm.code" :placeholder="t('auth.verificationCode')" size="large" autocomplete="off" />
          <div 
            class="custom-send-btn" 
            @click="sendCode" 
            :class="{ 'is-loading': codeSending }"
          >
            {{ codeSending ? t('auth.sendingCode') : t('auth.getCode') }}
          </div>
        </div>
      </el-form-item>
      <TermsAgreement v-model="agreementChecked" />
      <el-form-item>
        <el-button 
          class="auth-button" 
          type="primary" 
          @click="handleLogin" 
          size="large" 
          :loading="props.loading"
        >
          {{ props.loading ? t('common.loading') : t('auth.login') }}
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<style scoped>
.code-login-form-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
}

.auth-button {
  width: 100%;
  border-radius: 182px;
  font-weight: 600;
}

.verification-code-wrapper {
  display: flex;
  width: 100%;
  gap: 10px;
}

.verification-code-wrapper :deep(.el-input) {
  flex: 1;
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

.custom-send-btn.is-loading {
  opacity: 0.8;
  cursor: not-allowed;
}

.custom-send-btn:hover:not(.is-loading) {
  background-color: var(--el-color-primary-light-3);
}

.custom-send-btn:active:not(.is-loading) {
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

:deep(.el-form) {
  width: 100%;
}
</style> 