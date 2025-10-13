<script setup lang="ts">
import { ElForm, ElFormItem, ElInput, ElButton, type FormRules, ElNotification } from 'element-plus'
import { reactive, ref, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import TermsAgreement from './TermsAgreement.vue'

defineOptions({
  name: 'LoginForm'
})

const props = defineProps({
  loading: {
    type: Boolean,
    default: false
  }
})

const { t } = useI18n()

const loginForm = reactive({
  account: '',
  password: ''
})

const agreementChecked = ref(false)

const emit = defineEmits(['login'])

const formRef = ref()

const rules = computed<FormRules>(() => ({
  account: [
  ],
  password: [
    { required: true, message: t('auth.pleaseEnterPassword'), trigger: 'blur' },
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
      emit('login', loginForm)
    }
  })
}
</script>

<template>
  <div class="login-form-container">
    <el-form 
      ref="formRef"
      :model="loginForm" 
      :rules="rules"
      @submit.prevent 
      autocomplete="off"
    >
      <el-form-item prop="account">
        <el-input v-model="loginForm.account" :placeholder="t('auth.account') + '/' + t('auth.email')" size="large" autocomplete="off" />
      </el-form-item>
      <el-form-item prop="password">
        <el-input
          v-model="loginForm.password"
          type="password"
          :placeholder="t('auth.password')"
          show-password
          size="large"
          autocomplete="new-password"
        />
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
.login-form-container {
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