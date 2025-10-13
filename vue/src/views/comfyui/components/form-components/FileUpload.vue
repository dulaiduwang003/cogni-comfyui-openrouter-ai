<template>
  <div class="file-upload">
    <el-form-item
      :prop="fieldKey"
      :required="formItem.required"
    >
      <template #label>
        <div class="label-with-tooltip">
          <span>{{ formItem.tips }}</span>
          <el-tooltip v-if="formItem.template" :content="formItem.template" placement="top">
            <el-icon class="tooltip-icon">
              <QuestionFilled />
            </el-icon>
          </el-tooltip>
        </div>
      </template>
      <div class="upload-container">
        <!-- 如果没有文件，显示上传区域 -->
        <div v-if="!modelValue" class="upload-wrapper" @click="handleUploadClick">
          <el-upload
            ref="uploadRef"
            :http-request="customUpload"
            :accept="acceptTypes"
            :limit="1"
            :before-upload="beforeUpload"
            :show-file-list="false"
            :disabled="uploading || !canUpload"
            class="file-uploader"
          >
            <div class="upload-trigger">
              <el-icon class="upload-icon">
                <component :is="getUploadIcon()" />
              </el-icon>
              <div class="upload-text">{{ getUploadText() }}</div>
              <div class="upload-hint">{{ getUploadHints() }}</div>
            </div>
          </el-upload>
        </div>
        
        <!-- 上传进度遮罩层 -->
        <div v-if="uploading" class="upload-progress-overlay">
          <div class="progress-text">{{ Math.round(uploadProgress) }}%</div>
        </div>

        <!-- 如果有文件，显示预览 -->
        <div v-if="modelValue && !uploading" class="file-preview" @mouseenter="showOverlay = true" @mouseleave="showOverlay = false">
          <img 
            v-if="props.formItem.type === WorkflowFormTypeEnum.IMAGE_UPLOAD" 
            :src="modelValue" 
            alt="预览图片"
            class="preview-image"
          />
          <div v-else class="file-placeholder">
            <el-icon class="file-icon">
              <component :is="getUploadIcon()" />
            </el-icon>
            <div class="file-name">已上传文件</div>
          </div>
          
          <!-- 悬浮时的重新上传覆盖层 -->
          <div v-show="showOverlay" class="file-preview-overlay">
            <el-upload
              :http-request="customUpload"
              :accept="acceptTypes"
              :before-upload="beforeUpload"
              :show-file-list="false"
              class="reupload-wrapper"
            >
              <div class="overlay-text">重新上传</div>
            </el-upload>
          </div>
        </div>
      </div>
    </el-form-item>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { ElFormItem, ElUpload, ElIcon, ElNotification, ElTooltip } from 'element-plus'
import { Picture, VideoCamera, Microphone, QuestionFilled } from '@element-plus/icons-vue'
import type {  UploadFiles, UploadRequestOptions } from 'element-plus'
import { ossApi } from '@/api/oss/oss'
import { WorkflowFormTypeEnum } from '@/enums'
import { useAuthStore } from '@/stores'
import emitter, { OPEN_AUTH_DIALOG } from '@/utils/eventBusUtil'

interface Props {
  formItem: {
    inputs: string
    nodeKey: string
    tips: string
    type: string
    required: boolean
    options: string
    template?: string | null
    size?: number
  }
  modelValue: string
}

const props = defineProps<Props>()
const emit = defineEmits<{
  'update:model-value': [value: string]
}>()

const uploadRef = ref()
const fileList = ref<UploadFiles>([])

// 控制悬浮覆盖层显示
const showOverlay = ref(false)

// 上传进度控制
const uploading = ref(false)
const uploadProgress = ref(0)

// 控制是否可以上传（用于权限控制）
const canUpload = ref(true)

const fieldKey = computed(() => {
  return `${props.formItem.nodeKey}_${props.formItem.inputs}`
})

// 自定义上传方法
const customUpload = async (options: UploadRequestOptions) => {
  try {
    const { file, onProgress, onSuccess} = options
    
    // 开始上传，显示进度
    uploading.value = true
    uploadProgress.value = 0
    
    // 模拟进度更新
    const progressInterval = setInterval(() => {
      if (uploadProgress.value < 90) {
        uploadProgress.value += Math.random() * 10
        // 调用 Element Plus 的进度回调
        onProgress?.({ percent: uploadProgress.value } as any)
      }
    }, 200)
    
    // 调用我们的 OSS API
    const result = await ossApi.uploadFile({ file: file as File })
    
    // 清除进度定时器
    clearInterval(progressInterval)
    
    // 完成进度
    uploadProgress.value = 100
    onProgress?.({ percent: 100 } as any)
    
    // 短暂延迟显示完成状态
    setTimeout(() => {
      uploading.value = false
      uploadProgress.value = 0
      
      // 上传成功，调用成功回调
      onSuccess?.(result)
      
      // 更新表单值
      emit('update:model-value', result)
      
      ElNotification.success({
        title: '成功',
        message: '文件上传成功'
      })
    }, 500)
    
  } catch (error) {
    console.error('Upload error:', error)
    
    // 重置上传状态
    uploading.value = false
    uploadProgress.value = 0
    
    // 创建符合 UploadAjaxError 接口的错误对象
    const uploadError = {
      name: 'UploadError',
      status: 500,
      method: 'POST',
      url: '/upload/file',
      message: error instanceof Error ? error.message : '上传失败'
    }
    options.onError?.(uploadError)
    ElNotification.error({
      title: '错误',
      message: '文件上传失败，请重试'
    })
  }
}

// 接受的文件类型
const acceptTypes = computed(() => {
  switch (props.formItem.type) {
    case WorkflowFormTypeEnum.IMAGE_UPLOAD:
      return 'image/*'
    case WorkflowFormTypeEnum.AUDIO_UPLOAD:
      return 'audio/*'
    case WorkflowFormTypeEnum.VIDEO_UPLOAD:
      return 'video/*'
    default:
      return '*'
  }
})

// 获取上传图标
const getUploadIcon = () => {
  switch (props.formItem.type) {
    case WorkflowFormTypeEnum.IMAGE_UPLOAD:
      return Picture
    case WorkflowFormTypeEnum.AUDIO_UPLOAD:
      return Microphone
    case WorkflowFormTypeEnum.VIDEO_UPLOAD:
      return VideoCamera
    default:
      return Picture
  }
}

// 获取上传文本
const getUploadText = () => {
  switch (props.formItem.type) {
    case WorkflowFormTypeEnum.IMAGE_UPLOAD:
      return '上传图片'
    case WorkflowFormTypeEnum.AUDIO_UPLOAD:
      return '上传音频'
    case WorkflowFormTypeEnum.VIDEO_UPLOAD:
      return '上传视频'
    default:
      return '上传文件'
  }
}

// 获取上传提示
const getUploadHints = () => {
  const sizeLimit = getFileSizeLimit()
  switch (props.formItem.type) {
    case WorkflowFormTypeEnum.IMAGE_UPLOAD:
      return `JPG、PNG、GIF，≤${sizeLimit}MB`
    case WorkflowFormTypeEnum.AUDIO_UPLOAD:
      return `MP3、WAV、AAC，≤${sizeLimit}MB`
    case WorkflowFormTypeEnum.VIDEO_UPLOAD:
      return `MP4、AVI、MOV，≤${sizeLimit}MB`
    default:
      return '请选择合适格式'
  }
}

// 文件大小限制（MB）
const getFileSizeLimit = () => {
  // 如果有 size 字段，优先使用（假设 size 是以 MB 为单位）
  if (props.formItem.size) {
    return props.formItem.size
  }
  
  // 否则使用默认值
  switch (props.formItem.type) {
    case WorkflowFormTypeEnum.IMAGE_UPLOAD:
      return 10
    case WorkflowFormTypeEnum.AUDIO_UPLOAD:
      return 50
    case WorkflowFormTypeEnum.VIDEO_UPLOAD:
      return 100
    default:
      return 10
  }
}

// 上传前检查（文件大小等基础检查）
const beforeUpload = (file: File) => {
  const sizeLimit = getFileSizeLimit()
  const isValidSize = file.size / 1024 / 1024 < sizeLimit
  
  if (!isValidSize) {
    ElNotification.error({
      title: '错误',
      message: `文件大小不能超过 ${sizeLimit}MB`
    })
    return false
  }
  
  return true
}

// 处理上传点击事件，进行权限验证
const handleUploadClick = (event: Event) => {
  if (!canUpload.value) {
    // 未登录，阻止默认行为并触发登录弹窗
    event.preventDefault()
    event.stopPropagation()
    emitter.emit(OPEN_AUTH_DIALOG)
    return false
  }
  
  // 已登录，允许继续上传流程
  return true
}





// 监听登录状态变化，更新上传权限
const authStore = useAuthStore()
watch(
  () => authStore.isLoggedIn,
  (isLoggedIn) => {
    canUpload.value = isLoggedIn
  },
  { immediate: true }
)

// 监听 modelValue 变化，更新文件列表
watch(
  () => props.modelValue,
  (newValue) => {
    if (newValue && fileList.value.length === 0) {
      fileList.value = [{
        name: '已上传文件',
        url: newValue,
        uid: Date.now(),
        status: 'success'
      }]
    } else if (!newValue) {
      fileList.value = []
    }
  },
  { immediate: true }
)
</script>

<style scoped>
.file-upload {
  width: 100%;
}

.upload-container {
  width: 148px;
  height: 148px;
  position: relative;
}

.upload-wrapper {
  width: 148px;
  height: 148px;
  cursor: pointer;
}

.upload-wrapper * {
  cursor: pointer !important;
}

/* 覆盖 Element Plus 禁用状态的鼠标指针样式 */
.file-uploader :deep(.el-upload.is-disabled) {
  cursor: pointer !important;
}

.file-uploader :deep(.el-upload.is-disabled *) {
  cursor: pointer !important;
}

.file-uploader {
  width: 148px;
  height: 148px;
}

.file-uploader :deep(.el-upload) {
  display: block;
  width: 148px;
  height: 148px;
  border: 2px dashed var(--el-border-color);
  border-radius: 12px;
  transition: all 0.3s ease;
  background: transparent;
  position: relative;
  overflow: hidden;
  cursor: pointer !important;
}

.file-uploader :deep(.el-upload:hover) {
  border-color: var(--el-color-primary);
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.upload-trigger {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  width: 100%;
  padding: 12px;
  box-sizing: border-box;
  cursor: pointer !important;
}

.upload-icon {
  font-size: 32px;
  color: var(--el-color-primary);
  margin-bottom: 8px;
  transition: transform 0.3s ease;
}

.file-uploader :deep(.el-upload:hover) .upload-icon {
  transform: scale(1.1);
}

.upload-text {
  font-size: 13px;
  color: var(--el-text-color-regular);
  text-align: center;
  line-height: 1.3;
  font-weight: 500;
}

.upload-hint {
  font-size: 10px;
  color: var(--el-text-color-placeholder);
  text-align: center;
  margin-top: 4px;
  line-height: 1.2;
}

/* 文件预览样式 */
.file-preview {
  width: 148px;
  height: 148px;
  border-radius: 12px;
  background: transparent;
  position: relative;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid var(--el-border-color);
}

.file-preview:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.15);
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 12px;
}

.file-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: var(--el-text-color-primary);
  background: transparent;
}

.file-icon {
  font-size: 32px;
  margin-bottom: 8px;
  opacity: 0.8;
}

.file-name {
  font-size: 12px;
  opacity: 0.9;
  text-align: center;
}

/* 悬浮覆盖层样式 */
.file-preview-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  border-radius: 12px;
  z-index: 10;
  opacity: 0;
  transition: opacity 0.3s ease-in-out;
}

.file-preview:hover .file-preview-overlay {
  opacity: 1;
}

.reupload-wrapper {
  display: contents;
}

.reupload-wrapper :deep(.el-upload) {
  display: contents;
}

.overlay-text {
  color: #ffffff;
  font-size: 14px;
  font-weight: 500;
}



.el-form-item {
  margin-bottom: 0;
}

.el-form-item :deep(.el-form-item__label) {
  font-weight: 500;
  color: var(--el-text-color-primary);
  margin-bottom: 12px;
}

.label-with-tooltip {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.tooltip-icon {
  font-size: 14px;
  color: var(--el-text-color-secondary);
  cursor: help;
  transition: color 0.3s ease;
}

.tooltip-icon:hover {
  color: var(--el-color-primary);
}

/* 上传进度遮罩层样式 */
.upload-progress-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 148px;
  height: 148px;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  z-index: 20;
  opacity: 0;
  animation: fadeIn 0.3s ease-in-out forwards;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.progress-text {
  font-size: 24px;
  font-weight: 600;
  color: #ffffff;
}
</style>
