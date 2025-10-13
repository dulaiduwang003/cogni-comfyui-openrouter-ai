<template>
  <div class="profile-banner" @mousemove="handleMouseMove" ref="bannerRef">

    <div class="banner-bg">
      <div class="gradient-overlay"></div>
      <div class="decoration-circles">
        <div class="circle circle-1"></div>
        <div class="circle circle-2"></div>
        <div class="circle circle-3"></div>
      </div>
      <div class="floating-particles">
        <div v-for="i in 6" :key="i" class="particle" :style="getParticleStyle(i)"></div>
      </div>
      <div class="mouse-glow" :style="mouseGlowStyle"></div>
    </div>

    <div class="banner-content">
      <div class="profile-header">
        <div class="avatar-section">
          <div class="avatar-container" @click="handleAvatarClick">
            <img 
              v-if="userInfo?.avatar" 
              :src="userInfo.avatar" 
              :alt="userInfo.nickname || t('profile.banner.avatarAlt')"
              class="avatar-image"
              @error="handleAvatarError"
            />
            <div v-else class="avatar-placeholder">
              <el-icon :size="40">
                <User />
              </el-icon>
            </div>
            <div class="avatar-overlay">
              <el-icon :size="20">
                <Camera />
              </el-icon>
              <!-- <span>{{ t('profile.banner.changeAvatar') }}</span> -->
            </div>
          </div>
        </div>
        
        <div class="user-info">
          <div class="user-name-section">
            <h1 class="user-name">{{ userInfo?.nickname || t('profile.banner.defaultNickname') }}</h1>
            <el-button 
              type="primary" 
              size="small" 
              link 
              @click="handleEditNickname"
              class="edit-name-btn"
            >
              <el-icon><Edit /></el-icon>
              {{ t('profile.banner.edit') }}
            </el-button>
          </div>
          
          <div class="credits-info">
            <div class="credit-item">
              <div class="credit-icon">
                <el-icon><Coin /></el-icon>
              </div>
              <div class="credit-details">
                <span class="credit-label">{{ t('profile.banner.availableCredits') }}</span>
                <span class="credit-value">{{ formatCredits(userCredits?.availableCredits || 0) }}</span>
              </div>
            </div>
            
            <div class="credit-item">
              <div class="credit-icon">
                <el-icon><Lock /></el-icon>
              </div>
              <div class="credit-details">
                <span class="credit-label">{{ t('profile.banner.frozenCredits') }}</span>
                <span class="credit-value">{{ formatCredits(userCredits?.frozenCredits || 0) }}</span>
              </div>
            </div>
            
            <div class="credit-item">
              <div class="credit-icon">
                <el-icon><TrendCharts /></el-icon>
              </div>
              <div class="credit-details">
                <span class="credit-label">{{ t('profile.banner.totalCredits') }}</span>
                <span class="credit-value">{{ formatCredits(userCredits?.totalCredits || 0) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { 
  User, Camera, Edit, Coin, Lock, TrendCharts
} from '@element-plus/icons-vue'
import type { GetUserInfoApi, GetUserCreditsApi } from '@/api/user/types'

const { t } = useI18n()

interface Props {
  userInfo?: GetUserInfoApi.Result
  userCredits?: GetUserCreditsApi.Result
}

defineProps<Props>()

const emit = defineEmits<{
  avatarClick: []
  editNickname: []
}>()

const bannerRef = ref(null)

const mouseGlowStyle = ref({
  left: '50%',
  top: '50%',
  opacity: 0
})


const handleMouseMove = (event: MouseEvent) => {
  if (!bannerRef.value) return
  
  const rect = (bannerRef.value as HTMLElement).getBoundingClientRect()
  const x = ((event.clientX - rect.left) / rect.width) * 100
  const y = ((event.clientY - rect.top) / rect.height) * 100
  
  mouseGlowStyle.value = {
    left: x + '%',
    top: y + '%',
    opacity: 0.6
  }
}

const getParticleStyle = (_index: number) => {
  const size = 2 + Math.random() * 3
  const x = Math.random() * 100
  const y = Math.random() * 100
  const delay = Math.random() * 4
  const duration = 6 + Math.random() * 4

  return {
    left: x + '%',
    top: y + '%',
    width: size + 'px',
    height: size + 'px',
    animationDelay: delay + 's',
    animationDuration: duration + 's'
  }
}

const formatCredits = (credits: number) => {
  if (credits >= 10000) {
    return `${(credits / 1000).toFixed(1)}k`
  }
  return credits.toString()
}

const handleAvatarClick = () => {
  emit('avatarClick')
}

const handleAvatarError = (event: Event) => {
  console.error('头像加载失败:', event)
}

const handleEditNickname = () => {
  emit('editNickname')
}
</script>

<style scoped>
/* Profile Banner */
.profile-banner {
  position: relative;
  min-height: 160px;
  background: linear-gradient(135deg, var(--custom-gradient-start) 0%, var(--custom-gradient-end) 100%);
  border-radius: 16px;
  overflow: hidden;
  margin-bottom: 20px;
  cursor: pointer;
}

.banner-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1;
}

.gradient-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, 
    rgba(var(--el-color-primary-rgb), 0.9) 0%,
    rgba(var(--el-color-primary-rgb), 0.7) 50%,
    rgba(var(--el-color-primary-rgb), 0.5) 100%);
}

.decoration-circles {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  overflow: hidden;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;
}

.circle-1 {
  width: 120px;
  height: 120px;
  top: -60px;
  right: -60px;
  animation-delay: 0s;
}

.circle-2 {
  width: 80px;
  height: 80px;
  bottom: -40px;
  left: 20%;
  animation-delay: 2s;
}

.circle-3 {
  width: 60px;
  height: 60px;
  top: 30%;
  left: -30px;
  animation-delay: 4s;
}

.floating-particles {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
}

.particle {
  position: absolute;
  background: rgba(255, 255, 255, 0.6);
  border-radius: 50%;
  animation: particleFloat 6s linear infinite;
}

.mouse-glow {
  position: absolute;
  width: 150px;
  height: 150px;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.3) 0%, transparent 70%);
  border-radius: 50%;
  transform: translate(-50%, -50%);
  pointer-events: none;
  transition: all 0.3s ease;
  z-index: 2;
}

.banner-content {
  position: relative;
  z-index: 3;
  padding: 20px;
  height: 100%;
  color: white;
}

.profile-header {
  display: flex;
  align-items: center;
  gap: 20px;
}

.avatar-section {
  flex-shrink: 0;
}

.avatar-container {
  position: relative;
  width: 80px;
  height: 80px;
  border-radius: 50%;
  overflow: hidden;
  cursor: pointer;
  border: 3px solid rgba(255, 255, 255, 0.3);
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.1);
}

.avatar-container:hover {
  border-color: rgba(255, 255, 255, 0.6);
  transform: scale(1.05);
}

.avatar-container:hover .avatar-overlay {
  opacity: 1;
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  background: rgba(255, 255, 255, 0.2);
  color: rgba(255, 255, 255, 0.8);
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
  color: white;
  font-size: 12px;
  gap: 4px;
}

.user-info {
  flex: 1;
}

.user-name-section {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.user-name {
  font-size: 22px;
  font-weight: 600;
  margin: 0;
  line-height: 1.2;
}

.edit-name-btn {
  color: rgba(255, 255, 255, 0.8) !important;
  font-size: 14px;
}

.edit-name-btn:hover {
  color: white !important;
}

.credits-info {
  display: flex;
  gap: 24px;
  flex-wrap: wrap;
}

.credit-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 12px;
  backdrop-filter: blur(10px);
  min-width: 140px;
}

.credit-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  color: white;
}

.credit-details {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.credit-label {
  font-size: 12px;
  opacity: 0.8;
}

.credit-value {
  font-size: 16px;
  font-weight: 600;
}

.credit-value.primary {
  color: var(--el-color-primary-light-3);
}

/* Animations */
@keyframes float {
  0%, 100% { transform: translateY(0px) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(180deg); }
}

@keyframes particleFloat {
  0% {
    transform: translateY(100vh) rotate(0deg);
    opacity: 0;
  }
  10% {
    opacity: 0.8;
  }
  90% {
    opacity: 0.8;
  }
  100% {
    transform: translateY(-100vh) rotate(360deg);
    opacity: 0;
  }
}

/* Responsive Design */
@media (max-width: 768px) {
  .profile-banner {
    min-height: 220px;
  }
  
  .banner-content {
    padding: 20px 15px;
  }
  
  .profile-header {
    flex-direction: column;
    text-align: center;
    gap: 16px;
  }
  
  .avatar-container {
    width: 100px;
    height: 100px;
  }
  
  .user-name {
    font-size: 24px;
  }
  
  .credits-info {
    justify-content: center;
    gap: 16px;
  }
  
  .credit-item {
    min-width: 120px;
    padding: 10px 12px;
  }
  
  .user-name-section {
    flex-direction: column;
    gap: 8px;
  }
}

@media (max-width: 480px) {
  .credits-info {
    flex-direction: column;
    gap: 12px;
  }
  
  .credit-item {
    width: 100%;
    min-width: auto;
  }
}
</style> 