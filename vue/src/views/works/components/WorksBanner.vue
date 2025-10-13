<template>
  <div class="works-banner" @mousemove="handleMouseMove" ref="bannerRef">
    <!-- 背景装饰 -->
    <div class="banner-bg">
      <!-- 渐变背景 -->
      <div class="gradient-overlay"></div>
      
      <!-- 装饰圆圈 -->
      <div class="decoration-circles">
        <div class="circle circle-1"></div>
        <div class="circle circle-2"></div>
        <div class="circle circle-3"></div>
      </div>
      
      <!-- 浮动粒子 -->
      <div class="floating-particles">
        <div v-for="i in 8" :key="i" class="particle" :style="getParticleStyle(i)"></div>
      </div>
      
      <!-- 鼠标跟随光效 -->
      <div class="mouse-glow" :style="mouseGlowStyle"></div>
    </div>
    
    <!-- 内容区域 -->
    <div class="banner-content">
      <div class="text-section">
        <h1 class="title">{{ displayTitle }}<span class="cursor" v-show="showCursor">|</span></h1>
        <p class="description">{{ displayDesc }}</p>
        <div class="stats">
          <div class="stat-item">
            <div class="stat-icon">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
                <path d="M12 2L13.09 8.26L20 9L13.09 9.74L12 16L10.91 9.74L4 9L10.91 8.26L12 2Z" stroke="currentColor" stroke-width="2" fill="currentColor"/>
              </svg>
            </div>
            <span>{{ t('works.banner.stats.aiCreation') }}</span>
          </div>
          <div class="stat-item">
            <div class="stat-icon">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
                <path d="M21 16V8C21 6.895 20.105 6 19 6H5C3.895 6 3 6.895 3 8V16C3 17.105 3.895 18 5 18H19C20.105 18 21 17.105 21 16Z" stroke="currentColor" stroke-width="2"/>
                <path d="M7 10L9 12L17 4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <span>{{ t('works.banner.stats.highQuality') }}</span>
          </div>
          <div class="stat-item">
            <div class="stat-icon">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
                <path d="M12 22C17.5228 22 22 17.5228 22 12C22 6.47715 17.5228 2 12 2C6.47715 2 2 6.47715 2 12C2 17.5228 6.47715 22 12 22Z" stroke="currentColor" stroke-width="2"/>
                <path d="M8 12L11 15L16 10" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <span>{{ t('works.banner.stats.permanentSave') }}</span>
          </div>
        </div>
      </div>
      
      <div class="visual-section">
        <div class="artwork-showcase">
          <svg viewBox="0 0 120 120" class="showcase-svg">
            <defs>
                             <linearGradient id="artGradient" x1="0%" y1="0%" x2="100%" y2="100%">
                 <stop offset="0%" stop-color="var(--el-color-primary-light-5)" stop-opacity="0.9"/>
                 <stop offset="50%" stop-color="var(--el-color-primary-light-7)" stop-opacity="0.7"/>
                 <stop offset="100%" stop-color="var(--el-color-primary-light-9)" stop-opacity="0.5"/>
               </linearGradient>
              <filter id="glow">
                <feGaussianBlur stdDeviation="4" result="coloredBlur"/>
                <feMerge>
                  <feMergeNode in="coloredBlur"/>
                  <feMergeNode in="SourceGraphic"/>
                </feMerge>
              </filter>
            </defs>
            
            <!-- 主要图形 -->
            <g class="artwork-main">
              <!-- 背景圆 -->
              <circle cx="60" cy="60" r="45" fill="none" stroke="url(#artGradient)" stroke-width="2" opacity="0.6">
                <animate attributeName="stroke-dasharray" values="0 283;141 142;0 283" dur="4s" repeatCount="indefinite"/>
              </circle>
              
              <!-- 内部装饰 -->
              <g class="inner-decoration">
                <!-- 中心星形 -->
                <polygon points="60,30 65,45 80,45 68,55 73,70 60,60 47,70 52,55 40,45 55,45" 
                         fill="url(#artGradient)" filter="url(#glow)">
                  <animateTransform attributeName="transform" type="rotate" values="0 60 60;360 60 60" dur="8s" repeatCount="indefinite"/>
                </polygon>
                
                <!-- 轨道圆点 -->
                                 <circle cx="90" cy="60" r="3" fill="var(--el-color-primary-light-7)">
                   <animateTransform attributeName="transform" type="rotate" values="0 60 60;360 60 60" dur="6s" repeatCount="indefinite"/>
                   <animate attributeName="opacity" values="0.4;1;0.4" dur="2s" repeatCount="indefinite"/>
                 </circle>
                 <circle cx="30" cy="60" r="2" fill="var(--el-color-primary-light-8)">
                  <animateTransform attributeName="transform" type="rotate" values="360 60 60;0 60 60" dur="8s" repeatCount="indefinite"/>
                  <animate attributeName="opacity" values="0.4;1;0.4" dur="3s" repeatCount="indefinite"/>
                </circle>
              </g>
            </g>
          </svg>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, reactive } from 'vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

const bannerRef = ref(null)
const displayTitle = ref('')
const displayDesc = ref('')
const showCursor = ref(true)

// 鼠标光效
const mouseGlowStyle = ref({
  left: '50%',
  top: '50%',
  opacity: 0
})

const fullTitle = t('works.banner.title')
const fullDesc = t('works.banner.description')

let titleIndex = 0
let descIndex = 0
let titleTimer = null
let descTimer = null
let cursorTimer = null

// 打字机效果
const typeTitle = () => {
  if (titleIndex < fullTitle.length) {
    displayTitle.value += fullTitle[titleIndex]
    titleIndex++
    titleTimer = setTimeout(typeTitle, 120)
  } else {
    setTimeout(typeDesc, 500)
  }
}

const typeDesc = () => {
  if (descIndex < fullDesc.length) {
    displayDesc.value += fullDesc[descIndex]
    descIndex++
    descTimer = setTimeout(typeDesc, 60)
  } else {
    showCursor.value = false
  }
}

// 光标闪烁
const blinkCursor = () => {
  showCursor.value = !showCursor.value
  cursorTimer = setTimeout(blinkCursor, 500)
}

// 粒子样式
const getParticleStyle = (index) => {
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

// 鼠标移动事件
const handleMouseMove = (event) => {
  if (!bannerRef.value) return
  
  const rect = bannerRef.value.getBoundingClientRect()
  const x = ((event.clientX - rect.left) / rect.width) * 100
  const y = ((event.clientY - rect.top) / rect.height) * 100
  
  mouseGlowStyle.value = {
    left: x + '%',
    top: y + '%',
    opacity: 0.6
  }
}

onMounted(() => {
  blinkCursor()
  setTimeout(typeTitle, 500)
})

onUnmounted(() => {
  if (titleTimer) clearTimeout(titleTimer)
  if (descTimer) clearTimeout(descTimer)
  if (cursorTimer) clearTimeout(cursorTimer)
})
</script>

<style scoped>
.works-banner {
  position: relative;
  min-height: 200px;
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
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  padding: 30px;
  color: white;
}

.text-section {
  flex: 1;
  max-width: 60%;
}

.title {
  font-size: 28px;
  font-weight: 600;
  margin: 0 0 12px 0;
  line-height: 1.2;
}

.cursor {
  color: rgba(255, 255, 255, 0.8);
  animation: blink 1s infinite;
}

.description {
  font-size: 15px;
  line-height: 1.5;
  margin: 0 0 20px 0;
  opacity: 0.9;
}

.stats {
  display: flex;
  gap: 24px;
  margin-top: 16px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  opacity: 0.9;
}

.stat-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  color: white;
}

.visual-section {
  flex-shrink: 0;
  margin-left: 30px;
}

.artwork-showcase {
  width: 100px;
  height: 100px;
}

.showcase-svg {
  width: 100%;
  height: 100%;
  filter: drop-shadow(0 4px 12px rgba(0, 0, 0, 0.3));
}

.artwork-main {
  transform-origin: center;
}

.inner-decoration {
  transform-origin: center;
}

/* 动画效果 */
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

@keyframes blink {
  0%, 50% { opacity: 1; }
  51%, 100% { opacity: 0; }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .works-banner {
    min-height: 160px;
  }
  
  .banner-content {
    flex-direction: column;
    text-align: center;
    padding: 20px 15px;
  }
  
  .text-section {
    max-width: 100%;
    margin-bottom: 15px;
  }
  
  .title {
    font-size: 22px;
  }
  
  .description {
    font-size: 14px;
  }
  
  .stats {
    justify-content: center;
    gap: 16px;
  }
  
  .visual-section {
    margin-left: 0;
  }
  
  .artwork-showcase {
    width: 70px;
    height: 70px;
  }
}

@media (max-width: 480px) {
  .works-banner {
    min-height: 140px;
  }
  
  .stats {
    flex-direction: column;
    gap: 10px;
  }
  
  .title {
    font-size: 18px;
  }
}
</style> 