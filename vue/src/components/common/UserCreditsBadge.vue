<template>
  <div class="user-credits-badge">
    <div class="credits-display">
      <el-icon class="credits-icon" :size="18">
        <Coin />
      </el-icon>
      <span class="credits-text">{{ formatCredits(userCredits?.availableCredits || 0) }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { ElIcon } from 'element-plus'
import { Coin } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/modules/user'

const userStore = useUserStore()

// 获取用户积分信息
const userCredits = computed(() => userStore.userCredits)

// 格式化积分显示
const formatCredits = (credits: number) => {
  if (credits >= 10000) {
    return `${(credits / 1000).toFixed(1)}k`
  }
  return credits.toString()
}

// 组件挂载时获取积分信息
onMounted(async () => {
  if (!userCredits.value) {
    await userStore.fetchUserCredits()
  }
})
</script>

<style scoped>
.user-credits-badge {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.credits-display {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  border-radius: 8px;
  border: 1px solid var(--el-border-color-light);

  transition: all 0.3s ease;
}

.credits-display:hover {
  background: var(--el-bg-color);
  border-color: var(--el-border-color);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.credits-icon {
  color: var(--el-text-color-primary);
  flex-shrink: 0;
}

.credits-text {
  font-size: 14px;
  font-weight: 500;
  color: var(--el-text-color-primary);
  min-width: 20px;
  text-align: center;
}



/* 响应式适配 */
@media (max-width: 768px) {
  .credits-display {
    padding: 4px 8px;
    gap: 4px;
  }
  
  .credits-text {
    font-size: 13px;
  }
}

@media (max-width: 480px) {
  .credits-text {
    display: none;
  }
  
  .credits-display {
    padding: 6px;
    min-width: 32px;
    justify-content: center;
  }
}
</style> 