<script setup lang="ts">
import { onMounted, onBeforeUnmount, ref, computed } from 'vue'
import { noticeApi } from '@/api/notice/notice'
import type { NoticeApi } from '@/api/notice/types'

const props = defineProps<{ scopeKey?: string; ttlMs?: number; forceShow?: boolean }>()

const STORAGE_PREFIX = 'notice:lastMd5'
const scope = computed(() => props.scopeKey || 'default')
const storageKey = computed(() => `${STORAGE_PREFIX}:${scope.value}`)

const dialogVisible = ref(false)
const loading = ref(false)
const notice = ref<NoticeApi.NoticeVo | null>(null)
let openedForMd5: string | null = null

function getStoredEntry() {
  try {
    const raw = localStorage.getItem(storageKey.value)
    if (!raw) return null
    const parsed = JSON.parse(raw) as { md5: string; ts?: number }
    return parsed
  } catch {
    return null
  }
}

function setStoredEntry(md5: string) {
  try {
    const payload = JSON.stringify({ md5, ts: Date.now() })
    localStorage.setItem(storageKey.value, payload)
  } catch {
    // ignore
  }
}

function clearStoredEntry() {
  try {
    localStorage.removeItem(storageKey.value)
  } catch {
    // ignore
  }
}

function isExpired(ts?: number | null) {
  if (!props.ttlMs) return false
  if (!ts) return true
  return Date.now() - ts > props.ttlMs
}

async function fetchAndMaybeOpen() {
  loading.value = true
  try {
    const res = await noticeApi.getNotice()
    notice.value = res
    const stored = getStoredEntry()

    if (!res) {
      // 无公告，清理本地缓存
      clearStoredEntry()
      dialogVisible.value = false
      return
    }

    if (props.forceShow) {
      openedForMd5 = res.md5
      dialogVisible.value = true
      return
    }

    const alreadyRead = stored && stored.md5 === res.md5 && !isExpired(stored.ts)
    if (alreadyRead) {
      dialogVisible.value = false
      return
    }
    openedForMd5 = res.md5
    dialogVisible.value = true
  } catch (e) {
    // 静默失败
  } finally {
    loading.value = false
  }
}

function onClose() {
  dialogVisible.value = false
  if (notice.value && notice.value.md5 && openedForMd5 === notice.value.md5) {
    setStoredEntry(notice.value.md5)
  }
}

function handleStorage(ev: StorageEvent) {
  if (!ev.key || ev.key !== storageKey.value) return
  const stored = getStoredEntry()
  // 其他标签页已读，同步关闭弹窗
  if (stored && notice.value && stored.md5 === notice.value.md5) {
    dialogVisible.value = false
  }
}

onMounted(() => {
  window.addEventListener('storage', handleStorage)
  // 延迟加载，等待路由初始化
  setTimeout(fetchAndMaybeOpen, 150)
})

onBeforeUnmount(() => {
  window.removeEventListener('storage', handleStorage)
})

// 暴露给父组件的方法，用于手动打开公告（强制显示）
async function manualOpen() {
  console.log('NoticeAnnouncer.manualOpen() called')
  loading.value = true
  try {
    const res = await noticeApi.getNotice()
    notice.value = res
    
    if (!res) {
      // 无公告
      clearStoredEntry()
      dialogVisible.value = false
      console.log('No notice available')
      return
    }
    
    // 手动打开时强制显示，忽略已读状态
    openedForMd5 = res.md5
    dialogVisible.value = true
    console.log('Dialog should be visible now, dialogVisible:', dialogVisible.value)
  } catch (e) {
    console.error('Failed to fetch notice:', e)
  } finally {
    loading.value = false
  }
}

defineExpose({
  manualOpen
})
</script>

<template>
  <div>
    <!-- Debug info -->
    <!-- <div style="position: fixed; top: 10px; right: 10px; background: red; color: white; padding: 10px; z-index: 9999;">
      dialogVisible: {{ dialogVisible }}
    </div> -->
    <el-dialog
      v-model="dialogVisible"
      :show-close="true"
      :close-on-click-modal="false"
      :close-on-press-escape="true"
      :align-center="true"
      width="560px"
      class="notice-announcer-dialog"
      @close="onClose"
    >
    <template #header>
      <div class="na-hero">
        <div class="na-title">{{ notice?.title || '最新公告' }}</div>
      </div>
    </template>
    <div class="notice-announcer__body" v-loading="loading">
      <template v-if="notice">
        <div class="content" data-animate>
          {{ notice.content }}
        </div>
      </template>
      <template v-else>
        <el-empty description="暂无站点公告" />
      </template>
    </div>
    <template #footer>
      <div class="footer-actions">
        <el-button type="primary" class="cta" @click="onClose">我知道了</el-button>
      </div>
    </template>
  </el-dialog>
  </div>
</template>

<style scoped>
.notice-announcer-dialog :deep(.el-dialog) {
  border-radius: 20px;
  overflow: hidden;
  background: #fff;
  border: none;
  box-shadow: 0 24px 64px rgba(0, 0, 0, 0.14);
}

.notice-announcer-dialog :deep(.el-dialog__header) {
  padding: 0;
  border-bottom: none;
}

.na-hero {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 18px 20px 0 20px;
}

.na-title {
  text-align: center;
  font-size: 22px;
  line-height: 1.15;
  font-weight: 700;
  letter-spacing: 0.2px;
  color: var(--el-text-color-primary);
}

.notice-announcer-dialog :deep(.el-dialog__body) {
  padding: 0;
}

.notice-announcer__body {
  padding: 12px 18px 10px 18px;
}

.content {
  margin-top: 2px;
  white-space: pre-wrap;
  line-height: 1.9;
  font-size: 16px;
  color: var(--el-text-color-primary);
  max-height: clamp(220px, 40vh, 520px);
  overflow: auto;
  padding-right: 2px;
}

.footer-actions {
  display: flex;
  justify-content: center;
  padding: 0 0 12px 0;
}

.cta {
  border-radius: 999px;
  padding: 12px 22px;
  font-weight: 600;
  min-width: 160px;
}

/* 微动画 */
@keyframes float-in {
  from { transform: translateY(6px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}

[data-animate] {
  animation: fade-up 260ms ease-out both;
}

@keyframes fade-up {
  from { transform: translateY(4px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}

/* 暗色模式自适应 */
@media (prefers-color-scheme: dark) {
  .notice-announcer-dialog :deep(.el-dialog) {
    background: #181a1b;
    box-shadow: 0 24px 64px rgba(0, 0, 0, 0.6);
  }
}
</style>


