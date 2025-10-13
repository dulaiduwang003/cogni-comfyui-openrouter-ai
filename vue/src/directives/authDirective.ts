import type { Directive, DirectiveBinding } from 'vue'
import { withAuth } from '@/utils/authGuard'

interface AuthDirectiveValue {
  handler: () => void | Promise<void>
  message?: string
  hideWhenUnauth?: boolean // 是否在未认证时隐藏元素
}

interface SafeAuthDirectiveValue {
  handler: () => void | Promise<void>
  message?: string
  hideWhenUnauth: boolean
}

/**
 * v-auth 指令

 */
export const authDirective: Directive<HTMLElement, AuthDirectiveValue | (() => void | Promise<void>)> = {
  mounted(el: HTMLElement, binding: DirectiveBinding<AuthDirectiveValue | (() => void | Promise<void>)>) {
    const { value } = binding
    
    // 处理不同类型的指令值
    let safeConfig: SafeAuthDirectiveValue
    
    if (typeof value === 'function') {
      // 简化用法：直接传入函数
      safeConfig = {
        handler: value,
        hideWhenUnauth: false
      }
    } else if (value && typeof value === 'object') {
      // 完整配置对象
      safeConfig = {
        handler: value.handler || (() => {}),
        message: value.message,
        hideWhenUnauth: value.hideWhenUnauth || false
      }
    } else {
      // 兜底处理：无效值
      console.warn('v-auth directive received invalid value:', value)
      safeConfig = {
        handler: () => {},
        hideWhenUnauth: false
      }
    }
    
    // 添加点击事件监听器
    const clickHandler = (event: Event) => {
      event.preventDefault()
      event.stopPropagation()
      
      withAuth(safeConfig.handler, safeConfig.message)
    }
    
    el.addEventListener('click', clickHandler)
    

    ;(el as any)._authClickHandler = clickHandler
    

    if (safeConfig.hideWhenUnauth) {
      import('@/stores').then(({ useAuthStore }) => {
        const authStore = useAuthStore()
        if (!authStore.isLoggedIn) {
          el.style.display = 'none'
        }
      })
    }
  },
  
  unmounted(el: HTMLElement) {
    // 移除事件监听器
    const handler = (el as any)._authClickHandler
    if (handler) {
      el.removeEventListener('click', handler)
      delete (el as any)._authClickHandler
    }
  }
}

/**
 * v-auth-show 指令
 * 根据认证状态控制元素显示/隐藏
 * 用法: v-auth-show (已登录显示，未登录隐藏)
 * 或: v-auth-show="false" (已登录隐藏，未登录显示)
 */
export const authShowDirective: Directive<HTMLElement, boolean> = {
  async mounted(el: HTMLElement, binding: DirectiveBinding<boolean>) {
    const { useAuthStore } = await import('@/stores')
    const authStore = useAuthStore()
    const showWhenAuth = binding.value !== false // 默认为 true
    
    const updateVisibility = () => {
      const shouldShow = showWhenAuth ? authStore.isLoggedIn : !authStore.isLoggedIn
      el.style.display = shouldShow ? '' : 'none'
    }
    
    // 初始设置
    updateVisibility()
    
    // 监听认证状态变化（这里简化处理，实际项目中可能需要更复杂的响应式处理）
    const checkInterval = setInterval(updateVisibility, 1000)
    ;(el as any)._authCheckInterval = checkInterval
  },
  
  unmounted(el: HTMLElement) {
    const interval = (el as any)._authCheckInterval
    if (interval) {
      clearInterval(interval)
      delete (el as any)._authCheckInterval
    }
  }
} 