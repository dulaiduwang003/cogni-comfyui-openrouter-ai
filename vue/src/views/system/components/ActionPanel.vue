<template>
  <el-card shadow="never" class="action-panel-card">
    <template #header>
      <div class="card-header">
        <span>{{ t('system.actionPanel.title') }}</span>
      </div>
    </template>
    <div class="action-panel">
      <el-card 
        v-for="action in actions" 
        :key="action.titleKey" 
        shadow="hover" 
        class="action-card" 
        @click="handleAction(action)"
      >
        <div class="action-content">
          <div class="icon-wrapper" :class="action.colorClass">
            <el-icon :size="20">
              <component :is="action.icon" />
            </el-icon>
          </div>
          <div class="action-text">
            <p class="action-title">{{ t(action.titleKey) }}</p>
            <p class="action-description">{{ t(action.descKey) }}</p>
          </div>
        </div>
      </el-card>
    </div>
  </el-card>

  
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useI18n } from 'vue-i18n'
import {
  User,
  Promotion,
  Present,
  Notification,
  
} from '@element-plus/icons-vue'

const { t } = useI18n()

interface Action {
  titleKey: string
  descKey: string
  icon: any
  colorClass: string
  actionType: string
}

const actions = ref<Action[]>([
  {
    titleKey: 'system.actionPanel.userManagement',
    descKey: 'system.actionPanel.userManagementDesc',
    icon: User,
    colorClass: 'primary',
    actionType: 'user-management',
  },
  {
    titleKey: 'system.actionPanel.workflowManagement',
    descKey: 'system.actionPanel.workflowManagementDesc',
    icon: Promotion,
    colorClass: 'success',
    actionType: 'workflow-management',
  },
  {
    titleKey: 'system.actionPanel.redemptionManagement',
    descKey: 'system.actionPanel.redemptionManagementDesc',
    icon: Present,
    colorClass: 'warning',
    actionType: 'redemption-management',
  },
  {
    titleKey: 'system.actionPanel.announcement',
    descKey: 'system.actionPanel.announcementDesc',
    icon: Notification,
    colorClass: 'danger',
    actionType: 'announcement',
  },
])

const handleAction = (action: Action) => {
  ElMessage.info(t('system.actionPanel.clickedAction', { action: t(action.titleKey) }))
}
</script>

<style scoped>
.action-panel-card {
  height: 100%;
  background-color: var(--el-bg-color-overlay);
}

.action-panel-card :deep(.el-card__header) {
  padding: 14px 16px;
}

.action-panel-card :deep(.el-card__body) {
  padding: 12px;
  height: calc(100% - 54px);
  overflow-y: auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.action-panel {
  display: flex;
  flex-direction: column;
  gap: 10px;
  color: var(--el-text-color-primary);
}

.action-card {
  cursor: pointer;
  transition: all 0.2s ease-in-out;
  background-color: var(--el-bg-color-overlay);
  border: 1px solid var(--el-border-color-light);
}

.action-card :deep(.el-card__body) {
  padding: 12px;
}

.action-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--el-box-shadow-light);
  border-color: var(--el-color-primary);
}

.action-content {
  display: flex;
  align-items: center;
  gap: 12px;
}

.icon-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 38px;
  height: 38px;
  border-radius: 8px;
  background-color: var(--el-fill-color-light);
  flex-shrink: 0;
  transition: all 0.2s ease-in-out;
}

.icon-wrapper.primary {
  color: var(--el-color-primary);
  background-color: var(--el-color-primary-light-9);
}
.icon-wrapper.success {
  color: var(--el-color-success);
  background-color: var(--el-color-success-light-9);
}
.icon-wrapper.warning {
  color: var(--el-color-warning);
  background-color: var(--el-color-warning-light-9);
}
.icon-wrapper.danger {
  color: var(--el-color-danger);
  background-color: var(--el-color-danger-light-9);
}
.icon-wrapper.info {
  color: var(--el-color-info);
  background-color: var(--el-color-info-light-9);
}

.action-text {
  flex: 1;
  min-width: 0;
}

.action-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  margin: 0;
  line-height: 1.4;
}

.action-description {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  margin: 2px 0 0 0;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
