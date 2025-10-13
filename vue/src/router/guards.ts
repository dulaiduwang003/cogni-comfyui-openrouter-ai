import type { Router } from 'vue-router'
import { useAuthStore, useUserStore } from '@/stores'
import emitter, { OPEN_AUTH_DIALOG } from '@/utils/eventBusUtil'
import { Role } from '@/enums/user'

// 白名单路由 - 不需要登录就可以访问的路由
const WHITE_LIST_ROUTES = [
  '/',
  '/comfyui'
]

// 检查路由是否在白名单中
const isWhiteListRoute = (path: string): boolean => {
  return WHITE_LIST_ROUTES.includes(path)
}

// 检查路由是否需要认证
const requiresAuth = (route: any): boolean => {
  // 优先检查路由元信息
  if (route.meta && typeof route.meta.requiresAuth === 'boolean') {
    return route.meta.requiresAuth
  }
  
  // 如果没有元信息，则检查是否在白名单中
  // 白名单中的路由不需要认证，其他路由需要认证
  return !isWhiteListRoute(route.path)
}

// 设置路由守卫
export function setupRouterGuards(router: Router) {
  // 全局前置守卫
  router.beforeEach(async (to, from, next) => {
    console.log(`路由守卫 - 从 ${from.path} 到 ${to.path}`)
    
    const authStore = useAuthStore()
    const isLoggedIn = authStore.isLoggedIn
    const needsAuth = requiresAuth(to)
    
    console.log(`路由守卫 - 登录状态: ${isLoggedIn}`)
    console.log(`路由守卫 - 目标路由: ${to.path}`)
    console.log(`路由守卫 - 需要认证: ${needsAuth}`)
    console.log(`路由守卫 - 路由元信息:`, to.meta)
    
    // 如果路由不需要认证，直接放行
    if (!needsAuth) {
      console.log('路由守卫 - 路由不需要认证，直接放行')
      next()
      return
    }
    
    // 如果路由需要认证但用户未登录
    if (!isLoggedIn) {
      console.log('路由守卫 - 需要登录但用户未登录，显示登录对话框')
      
      // 打开登录对话框
      emitter.emit(OPEN_AUTH_DIALOG)
      
      // 阻止路由跳转
      next(false)
      return
    }
    
    // 检查是否需要管理员权限
    if (to.meta.requiresAdmin) {
      const userStore = useUserStore()
      // 加载用户信息
      if (!userStore.userInfo) {
        await userStore.fetchUserInfo()
      }
      
      console.log(`路由守卫 - 用户角色: ${userStore.userInfo?.role}`)
      
      if (userStore.userInfo?.role !== Role.ADMIN) {
        console.error('路由守卫 - 访问被拒绝：需要管理员权限')
        // 如果不是管理员，重定向到主页或无权限页面
        next('/')
        return
      }
    }
    
    // 用户已登录，且权限满足，允许访问
    console.log('路由守卫 - 用户已登录，且权限满足，允许访问')
    next()
  })
  
  // 全局后置钩子
  router.afterEach((to, from) => {
    console.log(`路由切换完成 - 从 ${from.path} 到 ${to.path}`)
  })
}

// 导出白名单配置，供其他地方使用
export { WHITE_LIST_ROUTES } 