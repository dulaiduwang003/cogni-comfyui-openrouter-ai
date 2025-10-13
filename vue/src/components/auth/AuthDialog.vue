<script setup lang="ts">
import { ElDialog, ElLink, ElTabs, ElTabPane, ElNotification } from 'element-plus'
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { useI18n } from 'vue-i18n'
import emitter, { OPEN_AUTH_DIALOG, LOGIN_SUCCESS } from '@/utils/eventBusUtil'
import LoginForm from './components/LoginForm.vue'
import RegisterForm from './components/RegisterForm.vue'
import CodeLoginForm from './components/CodeLoginForm.vue'
import ForgotPasswordForm from './components/ForgotPasswordForm.vue'
import lottie from 'lottie-web'
import logoAnimation from '@/assets/lottie/logo.json'
import { useAuthStore } from '@/stores'
import { useUserStore } from '@/stores/modules/user'

const { t } = useI18n()
const authStore = useAuthStore()
const userStore = useUserStore()

const visible = ref(false)
const activeTab = ref('password') // 'password', 'code'
const viewState = ref('login') // 'login', 'register', 'forgotPassword'
const logoContainer = ref<HTMLElement | null>(null)

const passwordLoginLoading = ref(false)
const codeLoginLoading = ref(false)
const registerLoading = ref(false)
const forgotPasswordLoading = ref(false)

let logoAnimation_instance: any = null

const initAnimation = () => {
  // 确保先清理之前的动画实例
  if (logoAnimation_instance) {
    logoAnimation_instance.destroy()
    logoAnimation_instance = null
  }
  
  // 初始化新的动画实例
  if (logoContainer.value) {
    try {
      logoAnimation_instance = lottie.loadAnimation({
        container: logoContainer.value,
        renderer: 'svg',
        loop: true,
        autoplay: true,
        animationData: logoAnimation
      })
    } catch (error) {
      console.error('Failed to load animation:', error)
    }
  }
}

const handleClose = () => {
  visible.value = false
}

const openDialog = () => {
  visible.value = true
  activeTab.value = 'password'
  viewState.value = 'login'
  // 对话框打开时初始化动画
  nextTick(() => {
    initAnimation()
  })
}

const handleLogin = async (loginForm: { account: string; password: string }) => {
  // 注意：这里接收的参数是 account，但 API 需要的是 email
  passwordLoginLoading.value = true
  try {
    const success = await authStore.passwordLogin(loginForm.account, loginForm.password)
    if (success) {
      // 登录成功后获取用户信息并刷新页面
      ElNotification.success({
        title: t('common.success'),
        message: t('auth.loginSuccess')
      })
      handleClose()
      await userStore.handleLoginSuccess()
      // 触发登录成功事件
      emitter.emit(LOGIN_SUCCESS)
    }
  } finally {
    passwordLoginLoading.value = false
  }
}

const handleRegister = async (registerForm: { email: string; code: string; password: string }) => {
  registerLoading.value = true
  try {
    const success = await authStore.register(registerForm)
    if (success) {
      ElNotification.success({
        title: t('common.success'),
        message: t('auth.registerSuccess')
      })
      viewState.value = 'login'
    }
  } finally {
    registerLoading.value = false
  }
}

const handleCodeLogin = async (codeLoginForm: { email: string; code: string }) => {
  codeLoginLoading.value = true
  try {
    const success = await authStore.emailLogin(codeLoginForm.email, codeLoginForm.code)
    if (success) {
      // 登录成功后获取用户信息并刷新页面
      ElNotification.success({
        title: t('common.success'),
        message: t('auth.loginSuccess')
      })
      handleClose()
      await userStore.handleLoginSuccess()
      // 触发登录成功事件
      emitter.emit(LOGIN_SUCCESS)
    }
  } finally {
    codeLoginLoading.value = false
  }
}

const handlePasswordReset = async (data: { email: string; code: string; password: string }) => {
  forgotPasswordLoading.value = true
  try {
    const success = await authStore.forgotPassword(data)
    if (success) {
      ElNotification.success({
        title: t('common.success'),
        message: t('auth.resetPasswordSuccess')
      })
      viewState.value = 'login'
    }
  } finally {
    forgotPasswordLoading.value = false
  }
}

const handleGetLoginCode = async (email: string, password?: string) => {
  if (viewState.value === 'login' || viewState.value === 'register' || viewState.value === 'forgotPassword') {
    try {
      // 根据当前视图状态决定是否需要提供密码
      if (viewState.value === 'login') {
        // 邮箱验证码登录时不需要密码
        await authStore.getVerificationCode(email, '')
      } else {
        // 注册和忘记密码需要密码
        await authStore.getVerificationCode(email, password || '')
      }
    } catch (error) {
      console.error('Failed to send verification code:', error)
    }
  }
}

onMounted(() => {
  emitter.on(OPEN_AUTH_DIALOG, openDialog)
})

onUnmounted(() => {
  emitter.off(OPEN_AUTH_DIALOG, openDialog)
  if (logoAnimation_instance) {
    logoAnimation_instance.destroy()
  }
})

// 监听 visible 变化
watch(visible, (newVal) => {
  if (newVal) {
    nextTick(() => {
      initAnimation()
    })
  } else {
    if (logoAnimation_instance) {
      logoAnimation_instance.destroy()
      logoAnimation_instance = null
    }
  }
})
</script>

<template>
  <el-dialog
    v-model="visible"
    width="450px"
    :before-close="handleClose"
    align-center
    class="auth-dialog"
    :append-to-body="true"
    :lock-scroll="true"
    :close-on-click-modal="false"
  >
    <div class="auth-dialog-header">
      <div ref="logoContainer" class="lottie-container"></div>
      <h2 class="logo-text">Cogni X</h2>
    </div>

    <div class="auth-dialog-content">
      <div v-if="viewState === 'login'">
        <el-tabs v-model="activeTab" class="login-tabs">
          <el-tab-pane :label="t('auth.passwordLogin')" name="password">
            <LoginForm @login="handleLogin" :loading="passwordLoginLoading" />
          </el-tab-pane>
          <el-tab-pane :label="t('auth.codeLogin')" name="code">
            <CodeLoginForm 
              @login-with-code="handleCodeLogin" 
              @send-code="handleGetLoginCode"
              :loading="codeLoginLoading" 
            />
          </el-tab-pane>
        </el-tabs>
        <div class="extra-actions">
          <el-link type="primary" :underline="false" style="font-size: 12px;" @click="viewState = 'forgotPassword'">
            {{ t('auth.forgotPassword') }}
          </el-link>
        </div>
      </div>
      
      <RegisterForm 
        v-else-if="viewState === 'register'" 
        @register="handleRegister" 
        @send-code="handleGetLoginCode" 
        :loading="registerLoading"
      />
      <ForgotPasswordForm 
        v-else-if="viewState === 'forgotPassword'" 
        @reset-password="handlePasswordReset" 
        @send-code="handleGetLoginCode"
        :loading="forgotPasswordLoading" 
      />

      <div class="auth-toggle">
        <span v-if="viewState === 'login'">
          {{ t('auth.noAccount') }} <el-link type="primary" @click="viewState = 'register'">{{ t('auth.registerNow') }}</el-link>
        </span>
        <span v-if="viewState === 'register'">
          {{ t('auth.hasAccount') }} <el-link type="primary" @click="viewState = 'login'">{{ t('auth.loginNow') }}</el-link>
        </span>
        <span v-if="viewState === 'forgotPassword'">
          <el-link type="primary" @click="viewState = 'login'">{{ t('auth.backToLogin') }}</el-link>
        </span>
      </div>
    </div>
  </el-dialog>
</template>

<style scoped>
.auth-dialog {
  :deep(.el-dialog__body) {
    padding: 0;
  }
}

.auth-dialog-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 30px 0;
  padding-bottom: 10px;
  background: var(--el-bg-color);
}

.lottie-container {
  width: 80px;
  height: 80px;
  margin-bottom: 8px;
}

.logo-text {
  font-size: 24px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  margin: 0;
  text-align: center;
}

.auth-dialog-content {
  width: 100%;
  padding: 20px;
}

.login-tabs {
  width: 100%;
  :deep(.el-tabs__nav) {
    width: 100%;
    display: flex;
  }
  :deep(.el-tabs__item) {
    flex: 1;
    text-align: center;
    font-size: 16px;
  }
}

.auth-actions {
  margin: 16px 0;
  width: 100%;
  text-align: right;
}

.login-actions {
  display: flex;
  justify-content: space-between;
  width: 100%;
}

.extra-actions {
  margin-top: 0px;
  text-align: right;
  width: 100%;
}

.auth-toggle {
  margin-top: 54px;
  text-align: center;
  width: 100%;
  font-size: 14px;
}

.auth-toggle .el-link {
  font-size: 14px;
  vertical-align: baseline;
}
</style> 