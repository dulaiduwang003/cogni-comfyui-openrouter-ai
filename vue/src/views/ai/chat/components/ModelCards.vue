<template>
  <div class="cards-grid">
    <div class="model-card">
      <div class="card-title">Flagship models</div>
      <div class="card-icons">
        <span class="badge">A</span>
        <span class="badge">X</span>
        <span class="badge">G</span>
      </div>
    </div>
    <div class="model-card">
      <div class="card-title">Best roleplay models</div>
      <div class="card-icons">
        <span class="badge">S</span>
        <span class="badge">W</span>
      </div>
    </div>
    <div class="model-card">
      <div class="card-title">Best coding models</div>
      <div class="card-icons">
        <span class="badge">A</span>
        <span class="badge">X</span>
        <span class="badge">G</span>
      </div>
    </div>
    <div class="model-card">
      <div class="card-title">Reasoning models</div>
      <div class="card-icons">
        <span class="badge">Δ</span>
        <span class="badge">X</span>
        <span class="badge">G</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// 可以在这里添加模型卡片相关的逻辑
</script>

<style scoped>
.cards-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(260px, 420px));
  gap: 16px 24px;
  justify-content: center;
  padding-top: 40px;
}

.model-card {
  background: var(--el-bg-color);
  border: 1px solid var(--el-border-color-light);
  border-radius: 14px;
  padding: 18px 16px;
  height: 110px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.card-title {
  color: var(--el-text-color-primary);
  font-size: 14px;
  font-weight: 600;
}

.card-icons {
  display: flex;
  align-items: center;
  gap: 8px;
}

.badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 22px;
  height: 22px;
  border-radius: 999px;
  background: var(--el-fill-color);
  color: var(--el-text-color-primary);
  font-size: 12px;
  border: 1px solid var(--el-border-color);
}

@media (max-width: 1200px) {
  .cards-grid {
    grid-template-columns: repeat(2, minmax(240px, 1fr));
  }
}

@media (max-width: 720px) {
  .cards-grid {
    grid-template-columns: 1fr;
  }
}
</style> 