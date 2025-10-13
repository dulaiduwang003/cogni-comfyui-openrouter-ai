<template>
  <div class="avatar-upload">
    <div class="upload-area">
      <div v-if="!imageFile" class="upload-placeholder" @click="selectFile">
        <div class="upload-icon">
          <el-icon :size="48">
            <Plus />
          </el-icon>
        </div>
        <p class="upload-text">{{ t('profile.avatar.selectImage') }}</p>
        <p class="upload-hint">{{ t('profile.avatar.formatHint') }}</p>
      </div>
      
      <div v-else class="image-preview">
        <div class="preview-container">
          <img 
            ref="previewImage"
            :src="previewUrl" 
            :alt="t('profile.avatar.preview')"
            class="preview-img"
          />

          <div v-if="uploading" class="upload-overlay">
            <div class="progress-percentage">
              {{ Math.round(uploadProgress) }}%
            </div>
          </div>
        </div>
      </div>
    </div>

    <input
      ref="fileInput"
      type="file"
      accept="image/jpeg,image/jpg,image/png"
      @change="handleFileSelect"
      style="display: none"
    />

    <div class="actions">
      <el-button @click="handleCancel">{{ t('profile.avatar.cancel') }}</el-button>
      <el-button 
        type="primary" 
        @click="handleUpload"
        :disabled="!imageFile || uploading"
        :loading="uploading"
      >
        {{ uploading ? t('profile.avatar.uploading') : t('profile.avatar.upload') }}
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElNotification } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { userApi } from '@/api/user/user'
import { ossApi } from '@/api/oss/oss'
import { useUserStore } from '@/stores/modules/user'

const { t } = useI18n()

const emit = defineEmits<{
  avatarUpdated: []
  cancel: []
}>()

const userStore = useUserStore()

const fileInput = ref<HTMLInputElement>()
const previewImage = ref<HTMLImageElement>()
const imageFile = ref<File | null>(null)
const previewUrl = ref<string>('')
const uploading = ref(false)
const uploadProgress = ref(0)

const isValidFile = computed(() => {
  if (!imageFile.value) return false
  
  const validTypes = ['image/jpeg', 'image/jpg', 'image/png']
  if (!validTypes.includes(imageFile.value.type)) {
    return false
  }

  const maxSize = 2 * 1024 * 1024
  if (imageFile.value.size > maxSize) {
    return false
  }
  
  return true
})

// Methods
const selectFile = () => {
  fileInput.value?.click()
}

const handleFileSelect = (event: Event) => {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  
  if (!file) return

  const validTypes = ['image/jpeg', 'image/jpg', 'image/png']
  if (!validTypes.includes(file.type)) {
    ElNotification.error(t('profile.avatar.formatError'))
    return
  }
  
  const maxSize = 2 * 1024 * 1024 // 2MB
  if (file.size > maxSize) {
    ElNotification.error(t('profile.avatar.sizeError'))
    return
  }
  
  imageFile.value = file

  const reader = new FileReader()
  reader.onload = (e) => {
    previewUrl.value = e.target?.result as string
  }
  reader.readAsDataURL(file)
}

const resetImage = () => {
  imageFile.value = null
  previewUrl.value = ''
  if (fileInput.value) {
    fileInput.value.value = ''
  }
}

defineExpose({
  reset: resetImage
})

const handleCancel = () => {
  resetImage()
  emit('cancel')
}

const handleUpload = async () => {
  if (!imageFile.value || !isValidFile.value) {
    ElNotification.error(t('profile.avatar.invalidFile'))
    return
  }
  
  uploading.value = true
  uploadProgress.value = 0
  
  try {

    const progressInterval = setInterval(() => {
      if (uploadProgress.value < 80) {
        uploadProgress.value += Math.random() * 10
      }
    }, 200)
    

    const uploadResult = await ossApi.uploadFile({
      file: imageFile.value
    })
    
    clearInterval(progressInterval)
    uploadProgress.value = 90

    await userApi.reqUpdateAvatar({
      avatar: uploadResult
    })
    
    uploadProgress.value = 100
    
    setTimeout(async () => {
      uploading.value = false
      uploadProgress.value = 0
      resetImage()

      await userStore.fetchUserInfo()

      ElNotification.success(t('profile.avatar.uploadSuccess'))
      
      emit('avatarUpdated')
    }, 500)
    
  } catch (error) {
    console.error('头像上传失败:', error)
    uploading.value = false
    uploadProgress.value = 0

  }
}
</script>

<style scoped>
.avatar-upload {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.upload-area {
  width: 100%;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 200px;
  border: 2px dashed var(--el-border-color);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: var(--el-bg-color-page);
}

.upload-placeholder:hover {
  border-color: var(--el-color-primary);

}

.upload-icon {
  margin-bottom: 12px;
  color: var(--el-text-color-placeholder);
}

.upload-placeholder:hover .upload-icon {
  color: var(--el-color-primary);
}

.upload-text {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: var(--el-text-color-primary);
}

.upload-hint {
  margin: 0;
  font-size: 12px;
  color: var(--el-text-color-placeholder);
}

.image-preview {
  display: flex;
  flex-direction: column;
}

.preview-container {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 200px;
  border: 1px solid var(--el-border-color);
  border-radius: 8px;
  background: var(--el-bg-color-page);
  overflow: hidden;
}

.preview-img {
  max-width: 100%;
  max-height: 200px;
  border-radius: 4px;
  object-fit: contain;
}

.upload-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 8px;
}

.progress-percentage {
  font-size: 24px;
  font-weight: 600;
  color: white;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5);
}

.actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 16px;
  border-top: 1px solid var(--el-border-color-lighter);
}

/* 按钮样式统一 */
.actions :deep(.el-button) {
  border-radius: 8px !important;
  font-weight: 500 !important;
  transition: all 0.2s ease !important;
}

/* 取消按钮样式 */
.actions :deep(.el-button:not(.el-button--primary)) {
  border: 1px solid var(--el-border-color) !important;
  background-color: var(--el-fill-color-lighter) !important;
  color: var(--el-text-color-regular) !important;
}

.actions :deep(.el-button:not(.el-button--primary)):hover {
  border-color: var(--el-border-color-dark) !important;
  background-color: var(--el-fill-color) !important;
  color: var(--el-text-color-primary) !important;
}

/* 主要按钮样式 */
.actions :deep(.el-button--primary) {
  background-color: var(--el-color-primary) !important;
  border-color: var(--el-color-primary) !important;
  color: white !important;
}

.actions :deep(.el-button--primary):hover {
  background-color: var(--el-color-primary-light-3) !important;
  border-color: var(--el-color-primary-light-3) !important;
}

.actions :deep(.el-button--primary.is-disabled),
.actions :deep(.el-button--primary.is-loading) {
  background-color: var(--el-color-primary-light-5) !important;
  border-color: var(--el-color-primary-light-5) !important;
  color: white !important;
  opacity: 0.6 !important;
}

/* Responsive Design */
@media (max-width: 480px) {
  .upload-placeholder {
    min-height: 160px;
  }
  
  .preview-container {
    min-height: 160px;
  }
  
  .actions {
    flex-direction: column-reverse;
    gap: 8px;
  }
  
  .actions .el-button {
    width: 100%;
  }
}
</style> 