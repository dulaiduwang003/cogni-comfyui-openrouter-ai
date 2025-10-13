import { createPinia } from 'pinia'
import { setupResponseInterceptors } from '@/utils/requestUtil'
import { useAuthStore } from './modules/auth'
import { useUserStore } from './modules/user'

// 创建pinia实例
export const pinia = createPinia()

// 延迟设置响应拦截器的函数
export function setupStoreInterceptors() {
  setupResponseInterceptors(async () => {
    const authStore = useAuthStore()
    const userStore = useUserStore()
    
    // 先清除用户信息，再执行登出
    userStore.clearUserInfo()
    authStore.logout()
  })
}

// 导出模块
export { useAuthStore } from './modules/auth'
export { useUserStore } from './modules/user'
export { useTaskWebSocketStore as useTaskWebSocketStore } from './modules/taskWebsocket'

// 创建一个统一的初始化函数
export async function initializeStores() {
  // 先设置响应拦截器
  setupStoreInterceptors()
  
  // 然后初始化用户store
  const userStore = useUserStore()
  await userStore.init()
}

export default pinia

