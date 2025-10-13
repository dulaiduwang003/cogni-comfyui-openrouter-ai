import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import { userApi } from '@/api/user/user'
import type { GetUserInfoApi, GetUserCreditsApi } from '@/api/user/types'
import { useAuthStore } from './auth'

// 用户信息本地存储的key
const USER_INFO_KEY = 'user_info'
// 用户积分本地存储的key
const USER_CREDITS_KEY = 'user_credits'

// 用户信息相关store
export const useUserStore = defineStore('user', () => {
  // 状态
  const userInfo = ref<GetUserInfoApi.Result | null>(null)
  const userCredits = ref<GetUserCreditsApi.Result | null>(null)
  
  // 计算属性
  const getUserInfo = computed(() => userInfo.value)
  const getUserCredits = computed(() => userCredits.value)
  
  // 方法
  // 获取用户信息
  async function fetchUserInfo() {
    const authStore = useAuthStore()
    console.log('fetchUserInfo - 从authStore获取token:', authStore.token)
    console.log('fetchUserInfo - 从localStorage获取token:', localStorage.getItem('token'))
    
    if (!authStore.token) {
      console.error('获取用户信息失败: 无token')
      return null
    }
    
    try {
      console.log('开始请求用户信息')
      const userInfoData = await userApi.reqGetUserInfo()
      console.log('用户信息响应:', userInfoData)
      
      // 保存用户信息
      setUserInfo(userInfoData)
      
      return userInfoData
    } catch (err: any) {
      console.error('获取用户信息失败:', err)
      return null
    }
  }
  
  // 获取用户积分信息
  async function fetchUserCredits() {
    const authStore = useAuthStore()
    
    if (!authStore.token) {
      console.error('获取用户积分失败: 无token')
      return null
    }
    
    try {
      console.log('开始请求用户积分信息')
      const creditsData = await userApi.reqGetUserCredits()
      console.log('用户积分响应:', creditsData)
      
      // 保存积分信息
      setUserCredits(creditsData)
      
      return creditsData
    } catch (err: any) {
      console.error('获取用户积分失败:', err)
      return null
    }
  }
  
  // 刷新用户积分（强制从服务器获取最新数据）
  async function refreshUserCredits() {
    console.log('刷新用户积分信息')
    return await fetchUserCredits()
  }
  
  // 设置并缓存用户信息
  function setUserInfo(info: GetUserInfoApi.Result | null) {
    userInfo.value = info
    if (info) {
      localStorage.setItem(USER_INFO_KEY, JSON.stringify(info))
      console.log('用户信息已缓存到localStorage')
    } else {
      localStorage.removeItem(USER_INFO_KEY)
      console.log('用户信息缓存已清除')
    }
  }
  
  // 设置并缓存用户积分信息
  function setUserCredits(credits: GetUserCreditsApi.Result | null) {
    userCredits.value = credits
    if (credits) {
      localStorage.setItem(USER_CREDITS_KEY, JSON.stringify(credits))
      console.log('用户积分已缓存到localStorage')
    } else {
      localStorage.removeItem(USER_CREDITS_KEY)
      console.log('用户积分缓存已清除')
    }
  }
  
  // 从本地缓存恢复用户信息
  function restoreUserInfo(): GetUserInfoApi.Result | null {
    if (userInfo.value) return userInfo.value
    
    const cachedInfo = localStorage.getItem(USER_INFO_KEY)
    if (cachedInfo) {
      try {
        const parsedInfo = JSON.parse(cachedInfo)
        userInfo.value = parsedInfo
        console.log('已从localStorage恢复用户信息')
        return parsedInfo
      } catch (err) {
        console.error('解析缓存的用户信息失败:', err)
        localStorage.removeItem(USER_INFO_KEY)
        return null
      }
    }
    return null
  }
  
  // 从本地缓存恢复用户积分信息
  function restoreUserCredits(): GetUserCreditsApi.Result | null {
    if (userCredits.value) return userCredits.value
    
    const cachedCredits = localStorage.getItem(USER_CREDITS_KEY)
    if (cachedCredits) {
      try {
        const parsedCredits = JSON.parse(cachedCredits)
        userCredits.value = parsedCredits
        console.log('已从localStorage恢复用户积分')
        return parsedCredits
      } catch (err) {
        console.error('解析缓存的用户积分失败:', err)
        localStorage.removeItem(USER_CREDITS_KEY)
        return null
      }
    }
    return null
  }
  
  // 清除用户信息
  function clearUserInfo() {
    setUserInfo(null)
    setUserCredits(null)
  }
  
  // 登录成功后的处理逻辑
  async function handleLoginSuccess() {
    try {
      // 获取用户信息
      const userInfoData = await fetchUserInfo()
      if (userInfoData) {
        console.log('登录成功，用户信息获取完成，即将刷新页面')
        // 延迟刷新，显示成功提示
        setTimeout(() => {
          window.location.reload()
        }, 1000)
        return true
      }
      return false
    } catch (error) {
      console.error('登录成功后处理失败:', error)
      return false
    }
  }
  
  // 初始化
  async function init() {
    const authStore = useAuthStore()

    // 如果有token，先从缓存恢复数据，然后在后台刷新
    if (authStore.token) {
      console.log('有token，优先从缓存恢复数据')
      
      // 从缓存恢复数据
      restoreUserInfo()
      restoreUserCredits()
      
      // 在后台异步获取最新数据，不阻塞页面渲染
      Promise.resolve().then(async () => {
        console.log('后台获取最新用户信息和积分')
        await Promise.all([
          fetchUserInfo(),
          fetchUserCredits()
        ])
      }).catch(error => {
        console.error('后台获取用户数据失败:', error)
      })
      
      // WebSocket连接也在后台进行
      Promise.resolve().then(async () => {
        try {
          const { useTaskWebSocketStore: useComfyuiTaskProgressWebSocketStore } = await import('./taskWebsocket')
          const webSocketStore = useComfyuiTaskProgressWebSocketStore()
          await webSocketStore.connect(authStore.token)
          console.log('应用初始化时WebSocket连接已启动')
        } catch (error) {
          console.error('应用初始化时WebSocket连接失败:', error)
        }
      })
    }
  }
  
  // 返回状态和方法
  return {
    userInfo,
    getUserInfo,
    fetchUserInfo,
    setUserInfo,
    restoreUserInfo,
    clearUserInfo,
    handleLoginSuccess,
    init,
    userCredits,
    getUserCredits,
    fetchUserCredits,
    refreshUserCredits,
    setUserCredits,
    restoreUserCredits
  }
}) 