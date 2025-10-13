<script setup lang="ts">
import { ref, onMounted } from 'vue'
import lottie from 'lottie-web'
import logoAnimation from '@/assets/lottie/logo.json'

// 添加 lottie 动画相关的 ref
const logoContainer = ref<HTMLElement | null>(null)

// 在组件挂载后初始化 Lottie 动画
onMounted(() => {
  if (logoContainer.value) {
    lottie.loadAnimation({
      container: logoContainer.value,
      renderer: 'svg',
      loop: true,
      autoplay: true,
      animationData: logoAnimation
    })
  }
})
</script>

<template>
  <div class="logo-container">
    <div class="logo-icon">
      <div ref="logoContainer" class="lottie-container"></div>
    </div>
    <span class="logo-text">Cogni X</span>
  </div>
</template>

<style scoped>
.logo-container {
  display: flex;
  align-items: center;
  padding: 10px 8px;
  margin-bottom: 25px;
}

.logo-icon {
  margin-right: 12px;
  display: flex;
  align-items: center;
}

.lottie-container {
  width: 42px;
  height: 42px;
}

.logo-text {
  font-size: 24px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}
</style> 