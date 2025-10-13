<template>
  <div class="announcement-view">
    <el-card shadow="never" class="content-card" :body-style="{ padding: '16px' }">
      <template #header>
        <div class="card-header">
          <span>{{ t('system.announcement.title') }}</span>
          <div class="header-actions">
            <el-button type="primary" @click="openPublishDialog">
              <el-icon><Plus /></el-icon>
              {{ t('system.announcement.publishButton') }}
            </el-button>
            <el-button v-if="notice" type="danger" link @click="handleClear">
              {{ t('system.announcement.clearButton') }}
            </el-button>
          </div>
        </div>
      </template>

      <div class="notice-content" v-loading="loading">
        <template v-if="notice">
          <div class="notice-card">
            <div class="notice-title">{{ notice.title }}</div>
            <div class="notice-meta">
              <span class="publisher">{{ t('system.announcement.publisher') }}：{{ notice.publisher }}</span>
              <span class="dot">·</span>
              <span class="time">{{ formatTime(notice.createTime) }}</span>
            </div>
            <div class="notice-body">{{ notice.content }}</div>
          </div>
        </template>
        <template v-else>
          <el-empty :description="t('system.announcement.empty.title')">
            <el-text type="info">{{ t('system.announcement.empty.hint') }}</el-text>
          </el-empty>
        </template>
      </div>
    </el-card>

    <!-- 发布公告对话框 -->
    <el-dialog v-model="dialog.visible" :title="t('system.announcement.dialogTitle')" width="520px" class="notice-dialog" @close="onDialogClose">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item :label="t('system.announcement.form.title')" prop="title">
          <el-input v-model="form.title" :placeholder="t('system.announcement.form.titlePlaceholder')" maxlength="100"  />
        </el-form-item>
        <el-form-item :label="t('system.announcement.form.publisher')" prop="publisher">
          <el-input v-model="form.publisher" :placeholder="t('system.announcement.form.publisherPlaceholder')" maxlength="50"  />
        </el-form-item>
        <el-form-item :label="t('system.announcement.form.content')" prop="content">
          <el-input v-model="form.content" type="textarea" :autosize="{ minRows: 4, maxRows: 8 }" maxlength="1000"  :placeholder="t('system.announcement.form.contentPlaceholder')" />
        </el-form-item>
        <el-form-item :label="t('system.announcement.form.time')" prop="createTime">
          <el-date-picker
            v-model="form.createTime"
            type="datetime"
            :placeholder="t('system.announcement.form.timePlaceholder')"
            value-format="YYYY-MM-DDTHH:mm:ss"
            style="width: 100%"
            clearable
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button class="cancel-btn" :disabled="submitting" @click="dialog.visible = false">{{ t('common.cancel') }}</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">{{ t('common.confirm') }}</el-button>
      </template>
    </el-dialog>
  </div>
  
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessageBox, ElNotification, type FormInstance, type FormRules } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import { useI18n } from 'vue-i18n'
import { systemNoticeApi } from '@/api/system-notice/system-notice'
import type { SystemNoticeApi } from '@/api/system-notice/types'
import { noticeApi } from '@/api/notice/notice'
import type { NoticeApi } from '@/api/notice/types'

const { t } = useI18n()

const loading = ref(false)
const notice = ref<NoticeApi.NoticeVo | null>(null)

const dialog = reactive({ visible: false })
const formRef = ref<FormInstance>()
const submitting = ref(false)
const form = reactive<{ title: string; publisher: string; content: string; createTime: string | null }>({
  title: '',
  publisher: '',
  content: '',
  createTime: null
})

const rules: FormRules<typeof form> = {
  title: [
    { required: true, message: () => t('system.announcement.validation.titleRequired'), trigger: 'blur' },
    { max: 100, message: () => t('system.announcement.validation.titleMaxLength'), trigger: 'blur' }
  ],
  publisher: [
    { required: true, message: () => t('system.announcement.validation.publisherRequired'), trigger: 'blur' },
    { max: 50, message: () => t('system.announcement.validation.publisherMaxLength'), trigger: 'blur' }
  ],
  content: [
    { required: true, message: () => t('system.announcement.validation.contentRequired'), trigger: 'blur' },
    { max: 1000, message: () => t('system.announcement.validation.contentMaxLength'), trigger: 'blur' }
  ]
}

const formatTime = (isoLike: string) => {
  return dayjs(isoLike).format('YYYY-MM-DD HH:mm:ss')
}

const loadNotice = async () => {
  loading.value = true
  try {
    notice.value = await noticeApi.getNotice()
  } catch (e) {
    // 错误通知由请求拦截器统一处理
  } finally {
    loading.value = false
  }
}

const openPublishDialog = () => {
  dialog.visible = true
}

const resetForm = () => {
  form.title = ''
  form.publisher = ''
  form.content = ''
  form.createTime = null
  formRef.value?.clearValidate()
}

const onDialogClose = () => {
  resetForm()
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      const payload: SystemNoticeApi.NoticeSetDto = {
        title: form.title.trim(),
        publisher: form.publisher.trim(),
        content: form.content.trim(),
        createTime: form.createTime || undefined
      }
      await systemNoticeApi.setNotice(payload)
      ElNotification.success(t('system.announcement.messages.publishSuccess'))
      dialog.visible = false
      await loadNotice()
    } catch (e: any) {
      // 429/其他错误已在拦截器提示
    } finally {
      submitting.value = false
    }
  })
}

const handleClear = async () => {
  try {
    await ElMessageBox.confirm(
      t('system.announcement.messages.clearConfirm'),
      t('system.announcement.messages.clearTitle'),
      {
        type: 'warning',
        confirmButtonText: t('system.announcement.clearButton'),
        cancelButtonText: t('common.cancel')
      }
    )
    await systemNoticeApi.clearNotice()
    ElNotification.success(t('system.announcement.messages.clearSuccess'))
    await loadNotice()
  } catch (e) {
    // 取消或已提示
  }
}

onMounted(() => {
  loadNotice()
})
</script>

<style scoped>
.announcement-view {
  height: 100%;
}

.content-card {
  height: 100%;
  background-color: var(--el-bg-color-overlay);
}

.content-card :deep(.el-card__header) {
  padding: 14px 16px;
  border-bottom: 1px solid var(--el-border-color-light);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  font-size: 16px;
  color: var(--el-text-color-primary);
}

.notice-content {
  min-height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.notice-card {
  width: 100%;
  max-width: 860px;
  background-color: var(--el-bg-color);
  border: 1px solid var(--el-border-color);
  border-radius: 8px;
  padding: 16px;
}

.notice-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.notice-meta {
  margin-top: 8px;
  color: var(--el-text-color-secondary);
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.notice-body {
  margin-top: 12px;
  white-space: pre-wrap;
  line-height: 1.7;
  color: var(--el-text-color-primary);
}

/* Dialog styles to match dark/light */
:deep(.notice-dialog .el-dialog) {
  background-color: var(--el-bg-color-overlay);
  color: var(--el-text-color-primary);
}
:deep(.notice-dialog .el-dialog__header),
:deep(.notice-dialog .el-dialog__footer) {
  background-color: var(--el-bg-color-overlay);
}
:deep(.notice-dialog .el-dialog__header) {
  border-bottom: 1px solid var(--el-border-color-light);
}
:deep(.notice-dialog .el-dialog__footer) {
  border-top: 1px solid var(--el-border-color-light);
}
:deep(.notice-dialog .el-dialog__title) {
  color: var(--el-text-color-primary);
}
:deep(.notice-dialog .el-dialog__body) {
  background-color: var(--el-bg-color-overlay);
}
:deep(.notice-dialog .el-form-item__label) {
  color: var(--el-text-color-primary);
}
:deep(.notice-dialog .el-input__wrapper),
:deep(.notice-dialog .el-select__wrapper),
:deep(.notice-dialog .el-input-number),
:deep(.notice-dialog .el-textarea__inner) {
  background-color: var(--el-bg-color) !important;
  border: 1px solid var(--el-border-color);
  transition: all 0.2s ease;
  box-shadow: none;
}
:deep(.notice-dialog .el-input__wrapper:hover),
:deep(.notice-dialog .el-select__wrapper:hover),
:deep(.notice-dialog .el-input-number:hover),
:deep(.notice-dialog .el-textarea__inner:hover) {
  border-color: var(--el-border-color-hover);
}
:deep(.notice-dialog .el-input__wrapper.is-focus),
:deep(.notice-dialog .el-select__wrapper.is-focused),
:deep(.notice-dialog .el-input-number.is-focus),
:deep(.notice-dialog .el-textarea__inner:focus) {
  border-color: var(--el-color-primary);
  box-shadow: 0 0 0 2px var(--el-color-primary-light-8);
}

.cancel-btn {
  background-color: var(--el-fill-color-lighter);
  color: var(--el-text-color-primary);
  border-color: var(--el-border-color);
}
.cancel-btn:hover {
  background-color: var(--el-fill-color-light);
}
</style>

