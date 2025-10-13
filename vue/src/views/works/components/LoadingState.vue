<template>
  <div class="loading-state">
    <div class="loading-cards">
      <div v-for="i in 6" :key="i" class="skeleton-card">
        <el-skeleton animated>
          <template #template>
            <div class="skeleton-image"></div>
            <div class="skeleton-content">
              <el-skeleton-item variant="h3" style="width: 80%" />
              <el-skeleton-item variant="text" />
              <el-skeleton-item variant="text" style="width: 60%" />
            </div>
          </template>
        </el-skeleton>
      </div>
    </div>
  </div>
</template>


<style scoped>
.loading-state {
  padding: 24px 0;
}

.loading-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 10px;
  width: 100%;
}

.skeleton-card {
  background: var(--el-bg-color);
  border-radius: 16px;
  overflow: hidden;
  padding: 20px;
  border: 1px solid var(--el-border-color-lighter);
}

.skeleton-image {
  aspect-ratio: 4/3;
  background: var(--el-fill-color-light);
  border-radius: 12px;
  margin-bottom: 16px;
}

.skeleton-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .loading-cards {
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 10px;
  }
}

@media (max-width: 480px) {
  .loading-cards {
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  }
}
</style> 