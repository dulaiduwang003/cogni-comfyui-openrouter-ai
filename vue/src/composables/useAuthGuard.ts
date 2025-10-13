import { computed } from 'vue'
import { useAuthStore } from '@/stores'
import { withAuth, checkAuth, ensureAuth } from '@/utils/authGuard'

/**
 * 认证守卫
 */
export function useAuthGuard() {
  const authStore = useAuthStore()
  
  // 响应式的登录状态
  const isLoggedIn = computed(() => authStore.isLoggedIn)
  
  /**
   * 包装需要认证的方法
   * @param callback 需要执行的回调函数
   * @param message 未登录提示信息
   */
  const withAuthGuard = (callback: () => void | Promise<void>, message?: string) => {
    return () => withAuth(callback, message)
  }
  
  /**
   * 直接检查并执行
   * @param callback 需要执行的回调函数
   * @param message 未登录提示信息
   */
  const executeWithAuth = (callback: () => void | Promise<void>, message?: string) => {
    withAuth(callback, message)
  }
  
  /**
   * 异步确保用户已登录
   * 如果未登录会弹窗，登录成功后 resolve
   */
  const ensureAuthenticated = () => {
    return ensureAuth()
  }
  
  /**
   * 同步检查登录状态
   */
  const checkAuthentication = () => {
    return checkAuth()
  }
  
  return {
    isLoggedIn,
    withAuthGuard,
    executeWithAuth,
    ensureAuthenticated,
    checkAuthentication
  }
} 