<template>
  <div class="overview">
    <el-card shadow="never" class="overview-main-card" v-loading="loading" :element-loading-background="loadingBackground">
      <template #header>
        <div class="card-header">
          <span class="header-title">{{ t('system.overview.title') }}</span>
          <div class="header-actions">
            <span v-if="overviewData" class="update-time">{{ overviewData.timestamp }}</span>
            <el-button 
              link 
              :icon="Refresh" 
              @click="handleRefresh" 
              :loading="loading" 
              style="margin-left: 8px;"
            />
          </div>
        </div>
      </template>
      
      <div v-if="overviewData" class="compact-layout">
      <!-- 第一行：用户统计、AI 服务统计、任务状态 -->
      <el-row :gutter="10" class="stats-row">
          <el-col :lg="8" :md="8" :sm="24">
            <el-card shadow="hover" class="compact-card">
              <div class="compact-header">
                <el-icon class="header-icon primary"><User /></el-icon>
                <span class="header-text">{{ t('system.overview.userStats.title') }}</span>
              </div>
              <div class="compact-stats">
                <div class="compact-stat-item">
                  <span class="label">{{ t('system.overview.userStats.totalUsers') }}</span>
                  <span class="value">{{ overviewData.userStats.totalUsers }}</span>
                </div>
                <div class="compact-stat-item">
                  <span class="label">{{ t('system.overview.userStats.onlineUsers') }}</span>
                  <span class="value success">{{ overviewData.userStats.onlineUsers }}</span>
                </div>
                <div class="compact-stat-item">
                  <span class="label">{{ t('system.overview.userStats.todayNewUsers') }}</span>
                  <span class="value warning">{{ overviewData.userStats.todayNewUsers }}</span>
                </div>
              </div>
            </el-card>
          </el-col>
          
          <el-col :lg="8" :md="8" :sm="24">
            <el-card shadow="hover" class="compact-card">
              <div class="compact-header">
                <el-icon class="header-icon success"><ChatDotRound /></el-icon>
                <span class="header-text">{{ t('system.overview.aiStats.title') }}</span>
              </div>
              <div class="compact-stats">
                <div class="compact-stat-item">
                  <span class="label">{{ t('system.overview.aiStats.todayApiCalls') }}</span>
                  <span class="value">{{ overviewData.aiStats.todayApiCalls }}</span>
                </div>
                <div class="compact-stat-item">
                  <span class="label">{{ t('system.overview.aiStats.todayTokens') }}</span>
                  <span class="value">{{ formatNumber(overviewData.aiStats.todayTokensUsed) }}</span>
                </div>
                <div class="compact-stat-item">
                  <span class="label">{{ t('system.overview.aiStats.todayConversations') }}</span>
                  <span class="value">{{ overviewData.aiStats.todayConversations }}</span>
                </div>
              </div>
            </el-card>
          </el-col>
          
          <el-col :lg="8" :md="8" :sm="24">
            <el-card shadow="hover" class="compact-card">
              <div class="compact-header">
                <el-icon class="header-icon info"><List /></el-icon>
                <span class="header-text">{{ t('system.overview.taskStats.title') }}</span>
              </div>
              <div class="compact-stats">
                <div class="compact-stat-item">
                  <span class="label">{{ t('system.overview.taskStats.queuedTasks') }}</span>
                  <span class="value warning">{{ overviewData.taskStats.queuedTasks }}</span>
                </div>
                <div class="compact-stat-item">
                  <span class="label">{{ t('system.overview.taskStats.buildingTasks') }}</span>
                  <span class="value info">{{ overviewData.taskStats.buildingTasks }}</span>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>

      <!-- 第二行：工作流统计、活跃模型 -->
      <el-row :gutter="10" class="stats-row">
          <el-col :lg="12" :md="12" :sm="24">
            <el-card shadow="hover" class="compact-card">
              <div class="compact-header">
                <el-icon class="header-icon primary"><Operation /></el-icon>
                <span class="header-text">{{ t('system.overview.workflowStats.title') }}</span>
              </div>
              <div class="compact-stats workflow-stats">
                <div class="compact-stat-item">
                  <span class="label">{{ t('system.overview.workflowStats.totalWorkflows') }}</span>
                  <span class="value">{{ overviewData.workflowStats.totalWorkflows }}</span>
                </div>
                <div class="compact-stat-item">
                  <span class="label">{{ t('system.overview.workflowStats.todayTasks') }}</span>
                  <span class="value">{{ overviewData.workflowStats.todayTasks }}</span>
                </div>
                <div class="compact-stat-item">
                  <span class="label">{{ t('system.overview.workflowStats.todaySuccessTasks') }}</span>
                  <span class="value success">{{ overviewData.workflowStats.todaySuccessTasks }}</span>
                </div>
                <div class="compact-stat-item">
                  <span class="label">{{ t('system.overview.workflowStats.todayFailedTasks') }}</span>
                  <span class="value danger">{{ overviewData.workflowStats.todayFailedTasks }}</span>
                </div>
              </div>
            </el-card>
          </el-col>
          
          <el-col :lg="12" :md="12" :sm="24">
            <el-card shadow="hover" class="compact-card model-card">
              <div class="compact-header">
                <el-icon class="header-icon warning"><TrendCharts /></el-icon>
                <span class="header-text">{{ t('system.overview.aiStats.activeModelsTop') }} {{ overviewData.aiStats.activeModels.length }}</span>
              </div>
              <div class="model-list">
                <div v-for="(model, index) in overviewData.aiStats.activeModels" :key="index" class="model-item">
                  <span class="model-rank">{{ index + 1 }}</span>
                  <span class="model-name">{{ model.modelName }}</span>
                  <span class="model-count">{{ model.callCount }}</span>
                </div>
                <div v-if="overviewData.aiStats.activeModels.length === 0" class="empty-text">{{ t('system.overview.noData') }}</div>
              </div>
            </el-card>
          </el-col>
        </el-row>

      <!-- 第三行：系统资源 -->
      <el-row :gutter="10" class="stats-row">
          <el-col :span="24">
            <el-card shadow="hover" class="compact-card resource-card">
              <div class="compact-header">
                <el-icon class="header-icon danger"><Monitor /></el-icon>
                <span class="header-text">{{ t('system.overview.systemResources.title') }}</span>
              </div>
              <div class="resource-content">
                <div class="resource-item">
                  <div class="resource-info">
                    <span class="resource-label">{{ t('system.overview.systemResources.cpuUsage') }}</span>
                    <span class="resource-value">{{ overviewData.systemResources.cpuUsage.toFixed(1) }}%</span>
                  </div>
                  <el-progress 
                    :percentage="Number(overviewData.systemResources.cpuUsage.toFixed(1))" 
                    :color="getProgressColor(overviewData.systemResources.cpuUsage)"
                    :stroke-width="6"
                  />
                </div>
                <div class="resource-item">
                  <div class="resource-info">
                    <span class="resource-label">{{ t('system.overview.systemResources.memoryUsage') }}</span>
                    <span class="resource-value">
                      {{ overviewData.systemResources.memoryUsage.toFixed(1) }}% 
                      <span class="memory-detail">
                        ({{ formatMemory(overviewData.systemResources.memoryUsed) }} / {{ formatMemory(overviewData.systemResources.memoryTotal) }})
                      </span>
                    </span>
                  </div>
                  <el-progress 
                    :percentage="Number(overviewData.systemResources.memoryUsage.toFixed(1))" 
                    :color="getProgressColor(overviewData.systemResources.memoryUsage)"
                    :stroke-width="6"
                  />
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { 
  User, 
  ChatDotRound, 
  TrendCharts,
  Operation,
  List,
  Monitor,
  Refresh
} from '@element-plus/icons-vue'
import type { SystemOverviewApi } from '@/api/system-overview/types'

const { t } = useI18n()

interface Props {
  overviewData: SystemOverviewApi.Result | null
  loading?: boolean
}

defineProps<Props>()

const emit = defineEmits(['refresh'])

const handleRefresh = () => {
  emit('refresh')
}

// 根据主题动态设置加载遮罩背景色
const loadingBackground = computed(() => {
  // 检测当前是否为暗色主题
  const isDark = document.documentElement.classList.contains('dark')
  return isDark ? 'rgba(0, 0, 0, 0.3)' : 'rgba(255, 255, 255, 0.3)'
})

const formatNumber = (num: number): string => {
  if (num >= 1000000) {
    return (num / 1000000).toFixed(1) + 'M'
  } else if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'K'
  }
  return num.toString()
}

const formatMemory = (mb: number): string => {
  if (mb >= 1024) {
    return (mb / 1024).toFixed(1) + ' GB'
  }
  return mb.toFixed(0) + ' MB'
}

const getProgressColor = (percentage: number): string => {
  if (percentage >= 80) return '#f56c6c'
  if (percentage >= 60) return '#e6a23c'
  return '#67c23a'
}
</script>

<style scoped>
.overview {
  width: 100%;
  height: 100%;
}

.overview-main-card {
  height: 100%;
  background-color: var(--el-bg-color-overlay);
  display: flex;
  flex-direction: column;
}

.overview-main-card :deep(.el-card__header) {
  padding: 14px 16px;
  flex-shrink: 0;
}

.overview-main-card :deep(.el-card__body) {
  padding: 12px;
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.header-actions {
  display: flex;
  align-items: center;
}

.update-time {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.compact-layout {
  display: flex;
  flex-direction: column;
  gap: 10px;
  flex: 1;
}

.stats-row {
  margin-bottom: 0 !important;
  flex: 1;
  display: flex;
}

.stats-row .el-col {
  display: flex;
  flex-direction: column;
}

.compact-card {
  height: 100%;
  background-color: var(--el-bg-color-overlay);
  border: 1px solid var(--el-border-color-light);
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
}

.compact-card:hover {
  transform: translateY(-2px);
  border-color: var(--el-color-primary);
}

.compact-card :deep(.el-card__body) {
  padding: 10px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.compact-header {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 8px;
  padding-bottom: 6px;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.header-icon {
  font-size: 16px;
}

.header-icon.primary {
  color: var(--el-color-primary);
}

.header-icon.success {
  color: var(--el-color-success);
}

.header-icon.warning {
  color: var(--el-color-warning);
}

.header-icon.info {
  color: var(--el-color-info);
}

.header-icon.danger {
  color: var(--el-color-danger);
}

.header-text {
  font-size: 13px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.compact-stats {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.workflow-stats {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 6px;
}

.compact-stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 5px 8px;
  background-color: var(--el-fill-color-light);
  border-radius: 6px;
  transition: background-color 0.2s;
}

.compact-stat-item:hover {
  background-color: var(--el-fill-color);
}

.compact-stat-item .label {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.compact-stat-item .value {
  font-size: 16px;
  font-weight: 700;
  color: var(--el-text-color-primary);
}

.compact-stat-item .value.primary {
  color: var(--el-color-primary);
}

.compact-stat-item .value.success {
  color: var(--el-color-success);
}

.compact-stat-item .value.warning {
  color: var(--el-color-warning);
}

.compact-stat-item .value.info {
  color: var(--el-color-info);
}

.compact-stat-item .value.danger {
  color: var(--el-color-danger);
}

/* 活跃模型 */
.model-card :deep(.el-card__body) {
  padding: 10px;
}

.model-list {
  display: flex;
  flex-direction: column;
  gap: 5px;
  flex: 1;
  overflow-y: auto;
}

.model-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 5px 8px;
  background-color: var(--el-fill-color-light);
  border-radius: 6px;
  transition: all 0.2s;
}

.model-item:hover {
  background-color: var(--el-fill-color);
}

.model-rank {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 18px;
  height: 18px;
  padding: 0 5px;
  background: linear-gradient(135deg, var(--el-color-primary), var(--el-color-primary-light-3));
  color: white;
  border-radius: 4px;
  font-size: 10px;
  font-weight: 700;
}

.model-name {
  flex: 1;
  font-size: 12px;
  color: var(--el-text-color-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.model-count {
  font-size: 12px;
  font-weight: 600;
  color: var(--el-color-primary);
}

.empty-text {
  text-align: center;
  padding: 20px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

/* 系统资源 */
.resource-card :deep(.el-card__body) {
  padding: 10px;
}

.resource-content {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  flex: 1;
  align-content: start;
}

.resource-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.resource-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.resource-label {
  font-size: 12px;
  font-weight: 500;
  color: var(--el-text-color-secondary);
}

.resource-value {
  font-size: 14px;
  font-weight: 700;
  color: var(--el-text-color-primary);
}

.memory-detail {
  font-size: 10px;
  font-weight: 400;
  color: var(--el-text-color-secondary);
}

:deep(.el-progress__text) {
  font-size: 12px !important;
  font-weight: 600;
}

/* 响应式 */
@media (max-width: 768px) {
  .compact-layout {
    gap: 8px;
  }
  
  .stats-row {
    margin-bottom: 8px !important;
  }
  
  .resource-content {
    grid-template-columns: 1fr;
  }
  
  .workflow-stats {
    grid-template-columns: 1fr;
  }
}
</style>
