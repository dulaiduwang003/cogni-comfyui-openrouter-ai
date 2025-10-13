import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { setupRouterGuards } from './guards'

import MainLayout from '@/layouts/MainLayout.vue'
import ComfyUIPage from '@/views/comfyui/index.vue' // 确保ComfyUI页面路径正确
import WorksPage from '@/views/works/index.vue' // 添加作品页面
import ProfilePage from '@/views/profile/index.vue' // 添加个人中心页面
// AI功能页面
import AIChatPage from '@/views/ai/chat/index.vue'
// 系统管理页面
import SystemPage from '@/views/system/index.vue'


const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    component: MainLayout,
    children: [
      {
        path: '',
        redirect: '/comfyui'
      },
      {
        path: 'comfyui',
        component: ComfyUIPage,
        meta: { requiresAuth: false }
      },
      {
        path: 'works',
        component: WorksPage,
        meta: { 
          requiresAuth: true,
          backButton: {
            show: true,
            to: '/comfyui',
            text: '返回主页'
          },
          title: '我的作品'
        }
      },
      {
        path: 'profile',
        component: ProfilePage,
        meta: { 
          requiresAuth: true,
          title: '个人中心'
        }
      },
      {
        path: 'ai/chat',
        component: AIChatPage,
        meta: { 
          hideSidebar: true,  // 这个页面不显示左侧菜单
          backButton: {
            show: true,
            to: '/comfyui',
            text: '返回'
          },
          title: 'AI聊天'
        }
      },
      {
        path: 'system',
        component: SystemPage,
        meta: { 
          requiresAuth: true,
          requiresAdmin: true, // 需要管理员权限
          title: '系统管理'
        }
      }
    ]
  }

  // 你可以在这里添加其他顶级路由
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

// 设置路由守卫
setupRouterGuards(router)

export default router 