<template>
  <div class="user-dropdown-container" ref="userDropdownRef">
    <div class="user-info-card" @click="toggleUserDropdown">
      <div class="user-avatar-container">
        <div class="user-avatar">
          <img :src="userInfo?.avatar" alt="avatar" v-if="userInfo?.avatar" />
          <div class="default-avatar" v-else>{{ userInfo?.nickname?.charAt(0) || 'U' }}</div>
        </div>
        <div class="avatar-glow"></div>
      </div>
      <div class="user-details">
        <div class="user-name">{{ userInfo?.nickname || t('user.defaultName') }}</div>
      </div>
      <div class="dropdown-arrow" :class="{ 'dropdown-arrow-up': showUserDropdown }">
        <svg width="12" height="12" viewBox="0 0 12 12" fill="none">
          <path d="M3 4.5L6 7.5L9 4.5" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </div>
    </div>
    
    <!-- 下拉菜单 -->
    <transition name="dropdown">
      <div v-show="showUserDropdown" class="user-dropdown-panel">
        <div class="dropdown-item" @click="handleProfileCenter">
          <svg width="16" height="16" viewBox="0 0 16 16" fill="none" class="dropdown-icon">
            <path d="M8 8C10.2091 8 12 6.20914 12 4C12 1.79086 10.2091 0 8 0C5.79086 0 4 1.79086 4 4C4 6.20914 5.79086 8 8 8Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M16 14C16 11.7909 12.4183 10 8 10C3.58172 10 0 11.7909 0 14" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <span>{{ t('user.profileCenter') }}</span>
        </div>
        <div class="dropdown-item" @click="handleLogout">
          <svg width="16" height="16" viewBox="0 0 16 16" fill="none" class="dropdown-icon">
            <path d="M6 2H4C3.46957 2 2.96086 2.21071 2.58579 2.58579C2.21071 2.96086 2 3.46957 2 4V12C2 12.5304 2.21071 13.0391 2.58579 13.4142C2.96086 13.7893 3.46957 14 4 14H6" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M10 11L14 8L10 5" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M14 8H6" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <span>{{ t('user.logout') }}</span>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useUserStore } from '@/stores/modules/user'
import { useAuthStore } from '@/stores/modules/auth'

const { t } = useI18n()

// 获取用户信息和路由
const router = useRouter()
const userStore = useUserStore()
const authStore = useAuthStore()

// 用户下拉菜单状态
const showUserDropdown = ref(false)
const userDropdownRef = ref<HTMLElement>()

// 计算用户信息
const userInfo = computed(() => userStore.userInfo || null)

// 切换用户下拉菜单
const toggleUserDropdown = () => {
  showUserDropdown.value = !showUserDropdown.value
}

// 个人中心
const handleProfileCenter = () => {
  // 关闭下拉菜单
  showUserDropdown.value = false
  
  // 跳转到个人中心页面
  router.push('/profile')
}

// 退出登录
const handleLogout = async () => {
  // 关闭下拉菜单
  showUserDropdown.value = false
  
  // 调用 auth store 的 logout 方法
  // 该方法已包含完整的错误处理和用户提示
  await authStore.logout()
}

// 点击外部关闭下拉菜单
const handleClickOutside = (event: MouseEvent) => {
  if (userDropdownRef.value && !userDropdownRef.value.contains(event.target as Node)) {
    showUserDropdown.value = false
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
/* 现代化用户信息卡片 */
.user-info-card {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 2px 12px;
  padding-left: 2px;
  height: 36px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 18px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  position: relative;
  overflow: hidden;
}

.user-info-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.6s;
}

.user-info-card:hover::before {
  left: 100%;
}

.user-info-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
  border-color: rgba(102, 126, 234, 0.3);
}

.user-avatar-container {
  position: relative;
  flex-shrink: 0;
}

.user-avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid rgba(102, 126, 234, 0.3);
  transition: all 0.3s ease;
  position: relative;
  z-index: 2;
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.default-avatar {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  font-size: 14px;
  font-weight: 600;
}

.avatar-glow {
  position: absolute;
  top: -2px;
  left: -2px;
  right: -2px;
  bottom: -2px;
  border-radius: 50%;
  background: linear-gradient(45deg, #667eea, #764ba2, #f093fb, #f5576c);
  opacity: 0;
  animation: rotate 3s linear infinite;
  transition: opacity 0.3s ease;
}

@keyframes rotate {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.user-info-card:hover .avatar-glow {
  opacity: 0.6;
}

.user-info-card:hover .user-avatar {
  border-color: var(--el-color-primary);
  transform: scale(1.05);
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
  flex: 1;
}

.user-name {
  font-weight: 600;
  font-size: 13px;
  color: var(--el-text-color-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 100px;
  transition: color 0.3s ease;
}

.user-info-card:hover .user-name {
  color: var(--el-color-primary);
}

.dropdown-arrow {
  color: var(--el-text-color-secondary);
  transition: all 0.3s ease, transform 0.3s ease;
  opacity: 0.7;
}

.user-info-card:hover .dropdown-arrow {
  color: var(--el-color-primary);
  opacity: 1;
  transform: translateY(1px);
}

.user-info-card:hover .dropdown-arrow.dropdown-arrow-up {
  transform: translateY(1px) rotate(180deg);
}

/* 用户下拉容器 */
.user-dropdown-container {
  position: relative;
}

/* 下拉箭头动画 */
.dropdown-arrow-up {
  transform: rotate(180deg);
}

/* 下拉面板样式 */
.user-dropdown-panel {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  min-width: 180px;
  background: var(--el-bg-color);
  border: 1px solid var(--el-border-color-light);
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  backdrop-filter: blur(10px);
  z-index: 1000;
  overflow: hidden;
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  cursor: pointer;
  transition: all 0.2s ease;
  color: var(--el-text-color-regular);
  font-size: 14px;
  font-weight: 500;
}

.dropdown-item:hover {
  background: var(--el-fill-color-lighter);
  color: var(--el-color-primary);
}

.dropdown-item:hover .dropdown-icon {
  color: var(--el-color-primary);
}

.dropdown-item:last-child:hover {
  color: var(--el-color-danger);
}

.dropdown-item:last-child:hover .dropdown-icon {
  color: var(--el-color-danger);
}

.dropdown-icon {
  flex-shrink: 0;
  transition: color 0.2s ease;
}

/* 下拉动画 */
.dropdown-enter-active,
.dropdown-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  transform-origin: top right;
}

.dropdown-enter-from {
  opacity: 0;
  transform: scale(0.9) translateY(-10px);
}

.dropdown-leave-to {
  opacity: 0;
  transform: scale(0.9) translateY(-10px);
}

/* 深色主题适配 */
html[class^="theme-dark"] .user-info-card,
html[class*=" theme-dark"] .user-info-card {
  background: rgba(30, 30, 30, 0.9);
  border-color: rgba(255, 255, 255, 0.1);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
}

html[class^="theme-dark"] .user-info-card:hover,
html[class*=" theme-dark"] .user-info-card:hover {
  background: rgba(40, 40, 40, 0.9);
  border-color: rgba(102, 126, 234, 0.4);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.4);
}

html[class^="theme-dark"] .user-dropdown-panel,
html[class*=" theme-dark"] .user-dropdown-panel {
  background: rgba(30, 30, 30, 0.95);
  border-color: rgba(255, 255, 255, 0.1);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.4);
}

/* 响应式适配 */
@media (max-width: 768px) {
  .user-info-card {
    padding: 2px 8px;
    height: 36px;
    gap: 6px;
    border-radius: 18px;
  }
  
  .user-avatar {
    width: 28px;
    height: 28px;
  }
  
  .user-name {
    font-size: 12px;
    max-width: 60px;
  }
  
  .dropdown-arrow {
    display: none;
  }
}

@media (max-width: 480px) {
  .user-details {
    display: none;
  }
  
  .user-info-card {
    padding: 2px 6px;
    height: 36px;
    gap: 6px;
  }
}
</style> 