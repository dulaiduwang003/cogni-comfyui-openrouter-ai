<template>
  <div class="top-navbar">
    <div class="navbar-content">
      <!-- 左侧：返回按钮或标题 -->
      <div class="navbar-left">
        <!-- 返回按钮 -->
        <el-button 
          v-if="showBackButton"
          type="text"
          class="back-button"
          @click="handleBackClick"
        >
          <el-icon size="16">
            <ArrowLeft />
          </el-icon>
         
        </el-button>

      </div>
      
      <!-- 右侧功能区 -->
      <div class="navbar-right">
        <!-- 语言切换 -->
        <el-tooltip :content="t('navbar.switchLanguage')"  placement="bottom" :show-after="500">
          <div class="navbar-item">
            <LanguageSwitcher />
          </div>
        </el-tooltip>
        
        <!-- 主题切换 -->
        <el-tooltip :content="t('navbar.switchTheme')" placement="bottom" :show-after="500">
          <div class="navbar-item theme-switcher-container">
            <ThemeSwitcher />
          </div>
        </el-tooltip>
        
        <!-- 用户积分徽章 -->
        <el-tooltip :content="t('navbar.myCredits')"  placement="bottom" :show-after="500" v-if="isLoggedIn">
          <div class="navbar-item">
            <UserCreditsBadge />
          </div>
        </el-tooltip>
        
        <!-- 任务面板 -->
        <el-tooltip :content="t('navbar.taskManagement')"  placement="bottom" :show-after="500" v-if="isLoggedIn">
          <div class="navbar-item">
            <TaskPanel />
          </div>
        </el-tooltip>
        
        <!-- 用户信息/登录 -->
        <template v-if="isLoggedIn">
          <el-tooltip :content="t('navbar.userMenu')"  placement="bottom" :show-after="500">
            <div class="navbar-item">
              <UserDropdown />
            </div>
          </el-tooltip>
        </template>
        <el-tooltip :content="t('navbar.clickToLogin')" placement="bottom" :show-after="500" v-else>
          <button class="modern-login-button" @click="handleLoginClick">
            <span class="login-text">{{ t('auth.loginNow') }}</span>
            <div class="login-ripple"></div>
          </button>
        </el-tooltip>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ArrowLeft } from '@element-plus/icons-vue'
import ThemeSwitcher from '@/components/theme/ThemeSwitcher.vue'
import LanguageSwitcher from '@/components/common/LanguageSwitcher.vue'
import TaskPanel from '@/components/common/TaskPanel.vue'
import UserDropdown from '@/components/common/UserDropdown.vue'
import UserCreditsBadge from '@/components/common/UserCreditsBadge.vue'
import { useUserStore } from '@/stores/modules/user'
import { useAuthStore } from '@/stores/modules/auth'

const { t } = useI18n()
const emit = defineEmits(['login-click'])

// 路由相关
const route = useRoute()
const router = useRouter()

// 获取用户信息
const userStore = useUserStore()
const authStore = useAuthStore()

// 计算用户是否已登录
const isLoggedIn = computed(() => !!authStore.token && !!userStore.userInfo)

// 返回按钮相关计算属性
const showBackButton = computed(() => {
  return route.meta?.backButton?.show === true
})





// 处理返回按钮点击
const handleBackClick = () => {
  const backTo = route.meta?.backButton?.to
  
  if (backTo) {
    // 跳转到指定路由
    router.push(backTo)
  } else {
    // 浏览器历史返回
    if (window.history.length > 1) {
      router.go(-1)
    } else {
      // 回到默认页面
      router.push('/comfyui')
    }
  }
}

const handleLoginClick = () => {
  emit('login-click')
}
</script>

<style scoped>
.top-navbar {
  height: 50px;
  background-color: var(--el-bg-color);
  border-bottom: 1px solid var(--el-border-color-light);
  display: flex;
  align-items: center;
  padding: 0 10px;
  transition: background-color 0.3s ease, border-color 0.3s ease;
}

.navbar-content {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.navbar-left {
  flex: 1;
}

/* 返回按钮样式 */
.back-button {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  margin: 0;
  height: auto;
  font-size: 14px;
  color: var(--el-text-color-primary);
  border-radius: 8px;
  transition: all 0.2s ease;
}

.back-button:hover {
  background-color: var(--el-fill-color-light);
  color: var(--el-color-primary);
}

.back-button:active {
  background-color: var(--el-fill-color);
}

.back-text {
  font-weight: 500;
}

.navbar-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.navbar-item {
  display: flex;
  align-items: center;
}

/* 现代化登录按钮 */
.modern-login-button {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 20px;
  height: 36px;
  background: var(--el-color-primary);
  border: none;
  border-radius: 12px;
  color: var(--el-color-white);
  font-weight: 500;
  font-size: 14px;
  cursor: pointer;
  overflow: hidden;
  transition: all 0.3s ease;
}

.modern-login-button:hover {
  background: var(--el-color-primary-light-3);
}

.modern-login-button:active {
  background: var(--el-color-primary-dark-2);
}

.login-text {
  position: relative;
  z-index: 2;
}

.login-ripple {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.3);
  transform: translate(-50%, -50%);
  transition: width 0.6s, height 0.6s;
}

.modern-login-button:active .login-ripple {
  width: 300px;
  height: 300px;
}

/* 主题切换器容器样式 */
.theme-switcher-container :deep(.theme-switcher) {
  margin: 0;
  width: auto;
}

.theme-switcher-container :deep(.theme-trigger) {
  padding: 8px;
  width: 36px;
  height: 36px;
  border-radius: 12px;
  min-width: auto;
}

/* 响应式适配 */
@media (max-width: 768px) {
  .navbar-right {
    gap: 8px;
  }
  
  .modern-login-button {
    padding: 0 16px;
    height: 36px;
    font-size: 13px;
  }
}

@media (max-width: 480px) {
  .modern-login-button .login-text {
    display: none;
  }
  
  .modern-login-button {
    padding: 0;
    width: 36px;
    height: 36px;
    justify-content: center;
  }
}
</style> 