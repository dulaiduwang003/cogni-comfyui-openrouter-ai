<script setup lang="ts">
import { useI18n } from 'vue-i18n'
import emitter, { OPEN_NOTICE_ANNOUNCER } from '@/utils/eventBusUtil'
const { t } = useI18n()

const handleAnnouncementClick = () => {
  emitter.emit(OPEN_NOTICE_ANNOUNCER)
}

const handleGitHubClick = () => {
  window.open('https://github.com/dulaiduwang003?tab=repositories', '_blank')
}
</script>

<template>
  <div class="sidebar-footer">
    <div class="footer-links">
      <span @click="handleAnnouncementClick">{{ t('layouts.sidebar.footer.announcement') }}</span>
      <span @click="handleGitHubClick">{{ t('layouts.sidebar.footer.visitGitHub') }}</span>
    </div>
  </div>
</template>

<style scoped>
.sidebar-footer {
  margin-top: auto;
  padding-top: 16px;
  border-top: 1px solid var(--el-border-color-dark);
}

.footer-links {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  padding: 0 8px;
  gap: 8px;
}

.footer-links span {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  cursor: pointer;
  flex-basis: auto;
  text-align: left;
}

.footer-links span:hover {
  color: var(--el-text-color-regular);
}
</style> 