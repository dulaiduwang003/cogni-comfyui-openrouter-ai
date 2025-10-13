<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import emitter, { OPEN_REDEMPTION_DIALOG } from '@/utils/eventBusUtil'
import { withAuth } from '@/utils/authGuard'
import { useUserStore } from '@/stores'
import { Role } from '@/enums/user'

const { t } = useI18n()

// 导入所有SVG图标
import CreativeIcon from '@/assets/svg/creative.svg'
import AssetsIcon from '@/assets/svg/assets.svg'
import ProfileIcon from '@/assets/svg/profile.svg'
import ChatIcon from '@/assets/svg/chatIcon.svg'
import ExchangeCodeIcon from '@/assets/svg/exchange-code.svg'
import SystemManagementIcon from '@/assets/svg/system-management.svg'

const router = useRouter()
const route = useRoute()
const activeItem = ref('')

interface MenuItem {
  id: string;
  title: string;
  route?: string; // 使 route 可选
  icon: string;
  badge?: string;
  description?: string;
  action?: 'route' | 'dialog' | 'function'; // 添加动作类型
  requiresAdmin?: boolean; // 新增：是否需要管理员权限
}

interface MenuSection {
  title?: string;
  items: MenuItem[];
}

// Define menu sections and items based on the image - 改为计算属性以支持国际化
const menuSections = computed<MenuSection[]>(() => [
  {
    items: [
      { 
        id: 'comfyui', 
        title: t('layouts.sidebar.menu.onlineGeneration'), 
        route: '/comfyui', 
        icon: CreativeIcon, 
        description: t('layouts.sidebar.menu.onlineGenerationDesc')
      },
      { 
        id: 'works', 
        title: t('layouts.sidebar.menu.myWorks'), 
        route: '/works', 
        icon: AssetsIcon 
      },
      { 
        id: 'profile', 
        title: t('layouts.sidebar.menu.profile'), 
        route: '/profile', 
        icon: ProfileIcon 
      },
    ]
  },
  {
    title: t('layouts.sidebar.menu.sectionAI'),
    items: [
      { 
        id: 'chat', 
        title: t('layouts.sidebar.menu.aiChat'), 
        route: '/ai/chat', 
        icon: ChatIcon, 
        badge: t('layouts.sidebar.menu.aiChatBadge')
      },
    ]
  },
  {
    title: t('layouts.sidebar.menu.sectionOther'),
    items: [
      { 
        id: 'other', 
        title: t('layouts.sidebar.menu.redemptionCode'), 
        icon: ExchangeCodeIcon, 
        action: 'dialog' 
      },
      { 
        id: 'system', 
        title: t('layouts.sidebar.menu.systemManagement'), 
        route: '/system', 
        icon: SystemManagementIcon, 
        requiresAdmin: true 
      },
    ]
  }
])

const userStore = useUserStore()
const isAdmin = computed(() => userStore.userInfo?.role === Role.ADMIN)

// 根据用户角色过滤菜单
const filteredMenuSections = computed(() => {
  if (isAdmin.value) {
    return menuSections.value
  }
  return menuSections.value
    .map(section => ({
      ...section,
      items: section.items.filter(item => !item.requiresAdmin)
    }))
    .filter(section => section.items.length > 0)
})

// 根据当前路由获取激活的菜单项
const getActiveItemFromRoute = (currentPath: string) => {
  for (const section of menuSections.value) {
    for (const item of section.items) {
      if (item.route && currentPath.startsWith(item.route)) { // 检查 route 是否存在
        return item.id
      }
    }
  }
  return 'comfyui' // 默认激活第一个
}

// 计算当前激活的菜单项
const computedActiveItem = computed(() => {
  return getActiveItemFromRoute(route.path)
})

const handleMenuClick = (item: MenuItem) => {
  activeItem.value = item.id
  
  // 根据菜单项类型执行不同的动作
  if (item.route) {
    // 路由导航
    router.push(item.route)
  } else if (item.action === 'dialog') {
    // 触发弹出框事件
    if (item.id === 'other') {
      // 打开兑换码弹出框 - 需要登录验证
      withAuth(() => {
        emitter.emit(OPEN_REDEMPTION_DIALOG)
      }, t('layouts.authRequired.redemptionCode'))
    }
  } else if (item.action === 'function') {
    // 执行其他自定义功能
    console.log(`执行 ${item.title} 功能`)
    // TODO: 实现具体的功能逻辑
  }
}

// 组件挂载时设置正确的激活状态
onMounted(() => {
  activeItem.value = computedActiveItem.value
})

// 监听路由变化，更新激活状态
router.afterEach((to) => {
  activeItem.value = getActiveItemFromRoute(to.path)
})
</script>

<template>
  <div class="menu-sections">
    <TransitionGroup name="section-fade" tag="div">
      <div v-for="(section, sectionIndex) in filteredMenuSections" :key="sectionIndex" class="menu-section">
        <h3 v-if="section.title" class="section-title">{{ section.title }}</h3>
        <ul>
          <TransitionGroup name="menu-slide">
            <li v-for="(item, itemIndex) in section.items" :key="item.id" 
              class="menu-item" 
              :class="{ active: computedActiveItem === item.id }"
              :style="{ '--item-index': itemIndex }"
              @click="handleMenuClick(item)">
              <div class="item-content">
                <span class="icon">
                  <img :src="item.icon" :alt="item.title" />
                </span>
                <div class="item-text-content">
                  <span class="text">{{ item.title }}</span>
                  <span v-if="item.description" class="description">{{ item.description }}</span>
                </div>
                <span v-if="item.badge" class="badge default">{{ item.badge }}</span>
              </div>
              <div class="ripple-effect"></div>
            </li>
          </TransitionGroup>
        </ul>
      </div>
    </TransitionGroup>
  </div>
</template>

<style scoped>
.menu-sections {
  flex-grow: 1;
  overflow-y: auto;
  margin-right: -12px;
  padding-right: 12px;
}

/* 使用全局滚动条样式 */

/* Section fade transition */
.section-fade-enter-active {
  transition: all 0.4s ease;
}

.section-fade-leave-active {
  transition: all 0.3s ease;
}

.section-fade-enter-from {
  opacity: 0;
  transform: translateY(-10px);
}

.section-fade-leave-to {
  opacity: 0;
  transform: translateY(10px);
}

.menu-section {
  margin-bottom: 20px;
}

.section-title {
  font-size: 13px;
  font-weight: 500;
  color: var(--el-text-color-secondary);
  padding: 0 12px 8px 12px;
  text-transform: uppercase;
}

.menu-section ul {
  list-style: none;
}

.menu-slide-enter-active {
  transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
  transition-delay: calc(var(--item-index) * 0.05s);
}

.menu-slide-leave-active {
  transition: all 0.3s ease;
}

.menu-slide-enter-from {
  opacity: 0;
  transform: translateX(-20px);
}

.menu-slide-leave-to {
  opacity: 0;
  transform: translateX(20px);
}

.menu-slide-move {
  transition: transform 0.3s ease;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  margin: 5px 0;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  color: var(--el-text-color-regular);
  position: relative;
  overflow: hidden;
  transform-origin: left center;
}

.menu-item:hover {
  transform: translateX(4px) scale(1.02);
}

.menu-item.active {
  transform: translateX(0) scale(1);
}

.ripple-effect {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  pointer-events: none;
  border-radius: 20px;
  overflow: hidden;
}

.menu-item:active .ripple-effect::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(var(--el-color-primary-rgb), 0.3) 0%, transparent 70%);
  transform: translate(-50%, -50%);
  animation: ripple 0.6s ease-out;
}

@keyframes ripple {
  to {
    width: 300px;
    height: 300px;
    opacity: 0;
  }
}

.menu-item .item-content {
  display: flex;
  align-items: center;
  width: 100%;
  position: relative;
  z-index: 1;
}

.menu-item .item-text-content {
  display: flex;
  flex-direction: column;
  flex-grow: 1;
  overflow: hidden;
}

.menu-item .icon {
  margin-right: 12px;
  display: flex;
  align-items: center;
  transition: transform 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.menu-item:hover .icon {
  transform: rotate(5deg) scale(1.1);
}

.menu-item.active .icon {
  transform: scale(1.15);
  animation: iconPulse 0.6s ease-out;
}

@keyframes iconPulse {
  0%, 100% {
    transform: scale(1.15);
  }
  50% {
    transform: scale(1.25);
  }
}

.menu-item .icon img {
  width: 16px;
  height: 16px;
  display: block;
  filter: none;
  transition: filter 0.3s ease;
}

html[class^="theme-dark"] .menu-item .icon img,
html[class*=" theme-dark"] .menu-item .icon img {
  filter: brightness(0) invert(1);
}

html[class^="theme-light"] .menu-item .icon img,
html[class*=" theme-light"] .menu-item .icon img {
  filter: none;
}

html[class^="theme-dark"] .menu-item.active .icon img,
html[class*=" theme-dark"] .menu-item.active .icon img {
  filter: brightness(0) invert(1);
}

html[class^="theme-light"] .menu-item.active .icon img,
html[class*=" theme-light"] .menu-item.active .icon img {
  filter: none;
}

.menu-item .text {
  flex-grow: 1;
  font-size: 14px;
  font-weight: 500;
  transition: transform 0.3s ease, color 0.3s ease;
}

.menu-item:hover .text {
  transform: translateX(2px);
}

.menu-item .description {
  font-size: 11px;
  color: var(--el-text-color-secondary);
  margin-top: 2px;
  font-weight: bold;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 150px;
  transition: color 0.3s ease, transform 0.3s ease;
  opacity: 0.8;
}

.menu-item:hover .description {
  opacity: 1;
  transform: translateX(2px);
}

.menu-item.active {
  background-color: var(--el-fill-color);
  color: var(--el-text-color-primary);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.menu-item:hover:not(.active) {
  background-color: var(--el-fill-color-light);
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.03);
}

html[class^="theme-dark"] .menu-item.active,
html[class*=" theme-dark"] .menu-item.active {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

html[class^="theme-dark"] .menu-item:hover:not(.active),
html[class*=" theme-dark"] .menu-item:hover:not(.active) {
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.15);
}

.badge {
  margin-left: auto;
  padding: 3px 8px;
  border-radius: 6px;
  font-size: 11px;
  font-weight: 600;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.badge.default {
  background-color: var(--el-color-primary);
  color: var(--el-color-white);
  opacity: 0.9;
}

.menu-item:hover .badge.default {
  opacity: 1;
  transform: scale(1.05);
  box-shadow: 0 2px 8px rgba(var(--el-color-primary-rgb), 0.3);
}

.menu-item.active .badge.default {
  animation: badgePulse 2s ease-in-out infinite;
}

@keyframes badgePulse {
  0%, 100% {
    transform: scale(1);
    opacity: 0.9;
  }
  50% {
    transform: scale(1.05);
    opacity: 1;
  }
}

/* Section title animation */
.section-title {
  transition: color 0.3s ease, transform 0.3s ease;
}

.section-title:hover {
  transform: translateX(2px);
}
</style> 