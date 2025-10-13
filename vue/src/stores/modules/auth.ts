import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import { authApi } from '@/api/auth/auth'
import type { RegisterApi, ForgotPasswordApi } from '@/api/auth/types'
import { ElNotification } from 'element-plus'
import router from '@/router'

// 认证相关store
export const useAuthStore = defineStore('auth', () => {
  // 状态
  const token = ref(localStorage.getItem('token') || '')
  
  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  
  // 方法
  // 密码登录
  async function passwordLogin(email: string, password: string) {
    try {
      console.log('开始密码登录请求')
      const tokenValue = await authApi.reqPasswordLogin({ email, password })
      console.log('获取到token:', tokenValue)
      setToken(tokenValue)
      console.log('token已保存到localStorage:', localStorage.getItem('token'))
      
      // 延迟导入userStore，避免循环依赖
      const { useUserStore } = await import('../modules/user')
      const userStore = useUserStore()
      await userStore.fetchUserInfo()
      
      return true
    } catch (err: any) {
      console.error('登录失败:', err)
      return false
    }
  }
  
  // 邮箱登录
  async function emailLogin(email: string, code: string) {
    try {
      console.log('开始邮箱登录请求')
      const tokenValue = await authApi.reqEmailLogin({ email, code })
      console.log('获取到token:', tokenValue)
      setToken(tokenValue)
      console.log('token已保存到localStorage:', localStorage.getItem('token'))
      
      // 延迟导入userStore，避免循环依赖
      const { useUserStore } = await import('../modules/user')
      const userStore = useUserStore()
      await userStore.fetchUserInfo()
      
      return true
    } catch (err: any) {
      console.error('邮箱登录失败:', err)
      return false
    }
  }
  
  // 获取验证码
  async function getVerificationCode(email: string, password: string) {
    try {
      await authApi.reqGetVerificationCode({ email, password })
      ElNotification.success({
        title: '成功',
        message: '验证码已发送'
      })
      return true
    } catch (err: any) {
      return false
    }
  }
  
  // 注册
  async function register(params: RegisterApi.Params) {
    try {
      await authApi.reqRegister(params)
      ElNotification.success({
        title: '成功',
        message: '注册成功'
      })
      return true
    } catch (err: any) {
      return false
    }
  }
  
  // 忘记密码
  async function forgotPassword(params: ForgotPasswordApi.Params) {
    try {
      await authApi.reqForgotPassword(params)
      ElNotification.success({
        title: '成功',
        message: '密码重置成功'
      })
      return true
    } catch (err: any) {
      return false
    }
  }
  
  // 设置token
  function setToken(tokenValue: string | any) {
    if (!tokenValue) {
      console.error('设置token失败: tokenValue为空')
      return
    }
    
    // 处理对象形式的token响应 {code, msg, data}
    let actualToken = tokenValue
    if (tokenValue && typeof tokenValue === 'object' && tokenValue.data) {
      console.log('从对象中提取真实token:', tokenValue.data)
      actualToken = tokenValue.data
    }
    
    console.log('设置token:', actualToken)
    token.value = actualToken
    localStorage.setItem('token', actualToken)
    console.log('token设置后检查localStorage:', localStorage.getItem('token'))
  }
  
  // 退出登录
  async function logout() {
    try {
      // 调用退出登录接口
      await authApi.reqLogout()
      
      // 断开WebSocket连接
      try {
        const { useTaskWebSocketStore: useComfyuiTaskProgressWebSocketStore } = await import('./taskWebsocket')
        const webSocketStore = useComfyuiTaskProgressWebSocketStore()
        webSocketStore.disconnect()
        console.log('WebSocket连接已断开')
      } catch (error) {
        console.error('断开WebSocket连接失败:', error)
      }
      
      // 清除本地状态
      token.value = ''
      localStorage.removeItem('token')
      
      // 延迟导入userStore，避免循环依赖，并清除用户信息
      const { useUserStore } = await import('../modules/user')
      const userStore = useUserStore()
      userStore.clearUserInfo()
      
      ElNotification.success({
        title: '成功',
        message: '已安全退出登录'
      })
      
      // 重定向到首页，使用replace防止用户通过浏览器后退按钮回到之前的页面
      await router.replace('/')
      window.location.reload()
      
      return true
    } catch (error: any) {
      console.error('退出登录失败:', error)
      
      // 断开WebSocket连接
      try {
        const { useTaskWebSocketStore: useComfyuiTaskProgressWebSocketStore } = await import('./taskWebsocket')
        const webSocketStore = useComfyuiTaskProgressWebSocketStore()
        webSocketStore.disconnect()
        console.log('WebSocket连接已断开')
      } catch (error) {
        console.error('断开WebSocket连接失败:', error)
      }
      
      // 即使接口调用失败，也要清除本地状态
      token.value = ''
      localStorage.removeItem('token')
      
      // 清除用户信息
      const { useUserStore } = await import('../modules/user')
      const userStore = useUserStore()
      userStore.clearUserInfo()
      
      ElNotification.warning({
        title: '警告',
        message: '退出登录时发生错误，已清除本地登录状态'
      })
      
      // 即使失败也要重定向到首页
      await router.replace('/')
      window.location.reload()
      
      return false
    }
  }
  
  // 返回状态和方法
  return {
    token,
    isLoggedIn,
    passwordLogin,
    emailLogin,
    getVerificationCode,
    register,
    forgotPassword,
    setToken,
    logout
  }
}) 