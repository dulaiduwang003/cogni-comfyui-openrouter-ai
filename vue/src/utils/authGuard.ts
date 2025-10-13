import { useAuthStore } from '@/stores'
import emitter, { OPEN_AUTH_DIALOG, LOGIN_SUCCESS } from '@/utils/eventBusUtil'

/**
 * 认证守卫 - 检查用户是否已登录
 * @param callback 登录成功后的回调函数
 * @param message 未登录时的提示信息（可选）
 * @returns boolean 是否已登录
 */
export function withAuth(callback: () => void | Promise<void>, message?: string): void {
  const authStore = useAuthStore()
  
  if (authStore.isLoggedIn) {
    // 已登录，执行回调
    callback()
  } else {
    // 未登录，弹出登录框
    if (message) {
      console.log(message)
    }
    emitter.emit(OPEN_AUTH_DIALOG)
  }
}

/**
 * 检查是否已登录（同步方法）
 * @returns boolean 是否已登录
 */
export function checkAuth(): boolean {
  const authStore = useAuthStore()
  return authStore.isLoggedIn
}

/**
 * 要求登录的装饰器方法
 * 可以用于包装需要登录的函数
 */
export function requireAuth<T extends (...args: any[]) => any>(
  fn: T,
  options?: {
    message?: string;
    onUnauthorized?: () => void;
  }
): T {
  return ((...args: any[]) => {
    const authStore = useAuthStore()
    
    if (authStore.isLoggedIn) {
      return fn(...args)
    } else {
      if (options?.message) {
        console.log(options.message)
      }
      
      if (options?.onUnauthorized) {
        options.onUnauthorized()
      } else {
        emitter.emit(OPEN_AUTH_DIALOG)
      }
      
      return undefined
    }
  }) as T
}

/**
 * Promise 版本的认证检查
 * 返回 Promise，登录成功后 resolve
 */
export function ensureAuth(): Promise<void> {
  return new Promise((resolve) => {
    const authStore = useAuthStore()
    
    if (authStore.isLoggedIn) {
      resolve()
    } else {
      // 监听登录成功事件
      const handleLoginSuccess = () => {
        emitter.off(LOGIN_SUCCESS, handleLoginSuccess)
        resolve()
      }
      
      emitter.on(LOGIN_SUCCESS, handleLoginSuccess)
      emitter.emit(OPEN_AUTH_DIALOG)
    }
  })
} 