<template>
  <div class="system-container page-container">
    <el-tabs v-model="activeTab" class="system-tabs content-enter" type="card">
      <el-tab-pane :label="t('system.tabs.overview')" name="overview">
        <OverviewView />
      </el-tab-pane>
      <el-tab-pane :label="t('system.tabs.users')" name="users">
        <UserManagementView />
      </el-tab-pane>
      <el-tab-pane :label="t('system.tabs.workflow')" name="workflow">
        <WorkflowManagementView />
      </el-tab-pane>
      <el-tab-pane :label="t('system.tabs.redemption')" name="redemption">
        <RedemptionCodeView />
      </el-tab-pane>
      <el-tab-pane :label="t('system.tabs.announcement')" name="announcement">
        <AnnouncementView />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'
import OverviewView from './views/OverviewView.vue'
import UserManagementView from './views/UserManagementView.vue'
import WorkflowManagementView from './views/WorkflowManagementView.vue'
import RedemptionCodeView from './views/RedemptionCodeView.vue'
import AnnouncementView from './views/AnnouncementView.vue'

const { t } = useI18n()
const activeTab = ref('overview')
</script>

<style scoped>
.system-container {
  height: 100%;
  padding: 16px;
  background-color: var(--el-bg-color-page);
  overflow: auto;
}

.system-tabs {
  height: 100%;
}

.system-tabs :deep(.el-tabs__content) {
  height: calc(100% - 55px);
  overflow: auto;
}

.system-tabs :deep(.el-tab-pane) {
  height: 100%;
}

@media (max-width: 992px) {
  .system-container {
    height: auto;
  }
}
</style>