<template>
  <div class="overview-view">
    <Overview 
      :overview-data="overviewData" 
      :loading="loading"
      @refresh="fetchSystemOverview" 
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useI18n } from 'vue-i18n'
import Overview from '../components/Overview.vue'
import { systemOverviewApi } from '@/api/system-overview/system-overview'
import type { SystemOverviewApi } from '@/api/system-overview/types'

const { t } = useI18n()
const overviewData = ref<SystemOverviewApi.Result | null>(null)
const loading = ref(false)

const fetchSystemOverview = async () => {
  try {
    loading.value = true
    const data = await systemOverviewApi.reqSystemOverview()
    overviewData.value = data
  } catch (error) {
    ElMessage.error(t('system.overview.errors.fetchFailed'))
    console.error('Failed to fetch system overview:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchSystemOverview()
})
</script>

<style scoped>
.overview-view {
  height: 100%;
}
</style>

