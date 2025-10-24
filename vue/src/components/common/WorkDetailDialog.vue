<template>
  <el-dialog
  align-center
    :model-value="visible"
    @update:model-value="$emit('update:visible', $event)"
    :title="t('workDetail.title')"
    width="800px"
    :before-close="handleClose"
    destroy-on-close
  >
    <div v-if="loading" class="dialog-loading">
      <el-skeleton animated>
        <template #template>
          <div class="detail-skeleton">
            <div class="skeleton-image-large"></div>
            <div class="skeleton-info">
              <el-skeleton-item variant="h1" style="width: 60%" />
              <el-skeleton-item variant="text" style="width: 40%" />
              <el-skeleton-item variant="text" style="width: 50%" />
              <el-skeleton-item variant="button" style="width: 100px" />
            </div>
          </div>
        </template>
      </el-skeleton>
    </div>
    
    <div v-else-if="workDetail" class="work-detail-content">
      <!-- 左侧预览区域 -->
      <div class="detail-preview-section">
        <!-- 音频播放器 -->
        <div v-if="workDetail.type === WorkflowResultModelTypeEnum.AUDIO" class="audio-player-container">
          <div class="audio-player-wrapper">
            <div class="audio-visual">
              <svg width="80" height="80" viewBox="0 0 24 24" fill="none" class="audio-icon">
                <path d="M9 18V5l12-2v13" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <circle cx="6" cy="18" r="3" stroke="currentColor" stroke-width="2"/>
                <circle cx="18" cy="16" r="3" stroke="currentColor" stroke-width="2"/>
              </svg>
              <h3 class="audio-title">{{ workDetail.workflowName || t('workDetail.audioTitle') }}</h3>
            </div>
            
            <div class="audio-controls">
              <audio 
                ref="audioPlayer"
                :src="workDetail.url"
                @loadedmetadata="onAudioLoaded"
                @timeupdate="onTimeUpdate"
                @ended="onAudioEnded"
                @error="onAudioError"
                preload="metadata"
              ></audio>
              
              <div class="player-controls">
                <el-button 
                  :icon="isPlaying ? VideoPause : VideoPlay"
                  circle
                  size="large"
                  type="primary"
                  @click="togglePlay"
                  :disabled="audioError"
                >
                </el-button>
              </div>
              
              <div class="progress-section">
                <span class="time-display">{{ formatAudioTime(currentTime) }}</span>
                <el-slider
                  v-model="progressValue"
                  :max="duration"
                  :disabled="audioError"
                  @change="onProgressChange"
                  :show-tooltip="false"
                  class="audio-progress"
                />
                <span class="time-display">{{ formatAudioTime(duration) }}</span>
              </div>
              
              <div v-if="audioError" class="audio-error">
                <el-icon><Warning /></el-icon>
                <span>{{ t('workDetail.audioLoadFailed') }}</span>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 视频播放器 -->
        <div v-else-if="workDetail.type === WorkflowResultModelTypeEnum.VIDEO" class="video-player-container">
          <div class="video-player-wrapper">
            <div class="video-preview-area" @click="toggleVideoPlay">
              <video 
                ref="videoPlayer"
                :src="workDetail.url"
                @loadedmetadata="onVideoLoaded"
                @timeupdate="onVideoTimeUpdate"
                @ended="onVideoEnded"
                @error="onVideoError"
                @click.stop
                preload="metadata"
                class="detail-video"
                :class="{ 'playing': isVideoPlaying }"
              >
                {{ t('workDetail.videoLoadFailed') }}
              </video>
              
              <!-- 播放控制覆盖层 -->
              <div class="video-overlay" v-if="!isVideoPlaying && !videoError">
                <div class="play-button-large">
                  <el-icon size="48"><VideoPlay /></el-icon>
                </div>
              </div>
              
              <!-- 视频错误状态 -->
              <div v-if="videoError" class="video-error-overlay">
                <el-icon size="48" color="#ccc"><Warning /></el-icon>
                <p>{{ t('workDetail.videoLoadFailed') }}</p>
              </div>
            </div>
            
            <!-- 视频控制栏 -->
            <div class="video-controls" v-if="!videoError">
              <div class="video-control-buttons">
                <el-button 
                  :icon="isVideoPlaying ? VideoPause : VideoPlay"
                  circle
                  size="small"
                  type="primary"
                  @click="toggleVideoPlay"
                  :disabled="videoError"
                >
                </el-button>
              </div>
              
              <div class="video-progress-section">
                <span class="time-display">{{ formatVideoTime(videoCurrentTime) }}</span>
                <el-slider
                  v-model="videoProgressValue"
                  :max="videoDuration"
                  :disabled="videoError"
                  @change="onVideoProgressChange"
                  :show-tooltip="false"
                  class="video-progress"
                />
                <span class="time-display">{{ formatVideoTime(videoDuration) }}</span>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 图片/3D模型预览 -->
        <div v-else class="detail-image-container">
          <!-- 3D模型预览 -->
          <Model3DPreview
            v-if="workDetail.type === WorkflowResultModelTypeEnum.MODEL && !imageError"
            :model-src="workDetail.url"
            :alt="`3D模型 ${workDetail.workflowResultId}`"
            @error="imageError = true"
            class="detail-3d-model"
          />
          <!-- 普通图片预览 -->
          <img 
            v-else-if="!imageError"
            :src="workDetail.url" 
            :alt="`作品 ${workDetail.workflowResultId}`"
            @error="imageError = true"
            class="detail-image"
          />
          <!-- 错误状态 -->
          <div v-else class="detail-image-error">
            <el-icon size="64" color="#ccc"><Picture /></el-icon>
            <p>{{ workDetail.type === WorkflowResultModelTypeEnum.MODEL ? t('workDetail.modelLoadFailed') : t('workDetail.imageLoadFailed') }}</p>
          </div>
        </div>
      </div>
      
      <!-- 右侧信息 -->
      <div class="detail-info-wrapper">
        <div class="detail-info-section">
          <div class="info-item">
            <label class="info-label">{{ t('workDetail.workflowName') }}</label>
            <span class="info-value">{{ workDetail.workflowName || t('workDetail.unnamedWorkflow') }}</span>
          </div>
          
          <div class="info-item" v-if="false">
            <label class="info-label">{{ t('workDetail.taskId') }}</label>
            <span class="info-value task-id-text">{{ workDetail.taskId }}</span>
          </div>
          
          <div class="info-item">
            <label class="info-label">{{ t('workDetail.workType') }}</label>
            <div class="info-value">
              <div class="type-display">
                <div class="type-icon" v-html="getTypeIcon(workDetail.type)"></div>
                <span>{{ getTypeDisplayName(workDetail.type) }}</span>
              </div>
            </div>
          </div>
          
          <div class="info-item">
            <label class="info-label">{{ t('workDetail.createTime') }}</label>
            <span class="info-value">{{ formatDetailTime(workDetail.createTime) }}</span>
          </div>
          
          <!-- 表单参数展示 -->
          <div v-if="workDetail.formParams?.taskNodeContainer?.length > 0" class="info-item form-params-section">
            <label class="info-label">{{ t('workDetail.formParams') }}</label>
            <div class="form-params-content">
              <div v-for="(node, index) in workDetail.formParams.taskNodeContainer" :key="index" class="param-item">
                <div class="param-header">
                  <span class="param-type-badge" :class="getTypeBadgeClass(node.type)">
                    {{ getTypeLabel(node.type) }}
                  </span>
                  <span class="param-tips">{{ node.tips || t('workDetail.noTips') }}</span>
                </div>
                <!-- 图片类型展示（缩略图） -->
                <div v-if="isImageType(node.type)" class="param-image-thumbnail">
                  <el-image
                    v-if="node.nodeValue"
                    :src="node.nodeValue"
                    fit="cover"
                    :preview-src-list="[node.nodeValue]"
                    :initial-index="0"
                    class="thumbnail-image"
                    :alt="node.tips || t('workDetail.paramImage')"
                  >
                    <template #error>
                      <div class="thumbnail-error">
                        <el-icon><Picture /></el-icon>
                      </div>
                    </template>
                  </el-image>
                  <div v-else class="param-value">-</div>
                </div>
                <!-- 普通文本值展示 -->
                <div v-else class="param-value">{{ formatNodeValue(node) }}</div>
              </div>
            </div>
          </div>
        </div>
        
        <div class="info-actions">
          <div 
            class="action-button download-button"
            :class="{ 'loading': downloading }"
            @click="handleDownload"
          >
            <el-icon v-if="!downloading"><Download /></el-icon>
            <el-icon v-else class="loading-icon"><Loading /></el-icon>
            {{ t('workDetail.download') }}
          </div>
          <div 
            class="action-button delete-button"
            :class="{ 'loading': deleting }"
            @click="handleDeleteConfirm"
          >
            <el-icon v-if="!deleting"><Delete /></el-icon>
            <el-icon v-else class="loading-icon"><Loading /></el-icon>
            {{ t('workDetail.delete') }}
          </div>
        </div>
      </div>
    </div>
    
    <div v-else class="dialog-error">
      <el-result
        icon="error"
        :title="t('workDetail.notFound')"
        :sub-title="t('workDetail.notFoundMessage')"
      >
        <template #extra>
          <el-button type="primary" @click="handleClose">{{ t('workDetail.close') }}</el-button>
        </template>
      </el-result>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import {  ElMessageBox, ElNotification } from 'element-plus'
import { Picture, Download, Delete, VideoPlay, VideoPause, Warning, Loading } from '@element-plus/icons-vue'
import { WorkflowResultModelApi } from '@/api/workflow-result/workflow-result'
import { WorkflowResultModelTypeEnum, WorkflowFormTypeEnum } from '@/enums/workflow'
import Model3DPreview from '@/views/works/components/Model3DPreview.vue'

const { t } = useI18n()

// 导入SVG图标
import ImageGenIcon from '@/assets/svg/image-gen.svg?raw'
import VideoGenIcon from '@/assets/svg/video-gen.svg?raw'
import AudioGenIcon from '@/assets/svg/audio-gen.svg?raw'
import ModelGenIcon from '@/assets/svg/model-gen.svg?raw'

// Props
const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  }
})

// Emits
const emit = defineEmits(['update:visible', 'workDeleted'])

// 响应式数据
const loading = ref(false)
const workDetail = ref(null)
const imageError = ref(false)
const downloading = ref(false)
const deleting = ref(false)

// 音频播放器相关数据
const audioPlayer = ref(null)
const isPlaying = ref(false)
const currentTime = ref(0)
const duration = ref(0)
const progressValue = ref(0)
const audioError = ref(false)

// 视频播放器相关数据
const videoPlayer = ref(null)
const isVideoPlaying = ref(false)
const videoCurrentTime = ref(0)
const videoDuration = ref(0)
const videoProgressValue = ref(0)
const videoError = ref(false)

// 获取作品类型显示名称
const getTypeDisplayName = (type) => {
  const typeMap = {
    [WorkflowResultModelTypeEnum.IMAGE]: t('workType.image'),
    [WorkflowResultModelTypeEnum.VIDEO]: t('workType.video'),
    [WorkflowResultModelTypeEnum.AUDIO]: t('workType.audio'),
    [WorkflowResultModelTypeEnum.MODEL]: t('workType.model')
  }
  return typeMap[type] || type
}

// 获取作品类型图标
const getTypeIcon = (type) => {
  const iconMap = {
    [WorkflowResultModelTypeEnum.IMAGE]: ImageGenIcon,
    [WorkflowResultModelTypeEnum.VIDEO]: VideoGenIcon,
    [WorkflowResultModelTypeEnum.AUDIO]: AudioGenIcon,
    [WorkflowResultModelTypeEnum.MODEL]: ModelGenIcon
  }
  return iconMap[type] || ImageGenIcon
}

// 详情时间格式化（显示完整时间）
const formatDetailTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

// 获取表单类型的显示标签
const getTypeLabel = (type) => {
  const typeLabels = {
    [WorkflowFormTypeEnum.TEXT_PROMPT]: t('workDetail.types.textPrompt'),
    [WorkflowFormTypeEnum.RADIO_SELECTOR]: t('workDetail.types.radioSelector'),
    [WorkflowFormTypeEnum.CHECKBOX_SELECTOR]: t('workDetail.types.checkboxSelector'),
    [WorkflowFormTypeEnum.IMAGE_UPLOAD]: t('workDetail.types.imageUpload'),
    [WorkflowFormTypeEnum.IMAGE_SCRIBBLE]: t('workDetail.types.imageScribble'),
    [WorkflowFormTypeEnum.VIDEO_UPLOAD]: t('workDetail.types.videoUpload'),
    [WorkflowFormTypeEnum.AUDIO_UPLOAD]: t('workDetail.types.audioUpload')
  }
  return typeLabels[type] || type
}

// 获取类型徽章的 CSS 类
const getTypeBadgeClass = (type) => {
  const classMap = {
    [WorkflowFormTypeEnum.TEXT_PROMPT]: 'badge-text',
    [WorkflowFormTypeEnum.RADIO_SELECTOR]: 'badge-select',
    [WorkflowFormTypeEnum.CHECKBOX_SELECTOR]: 'badge-select',
    [WorkflowFormTypeEnum.IMAGE_UPLOAD]: 'badge-image',
    [WorkflowFormTypeEnum.IMAGE_SCRIBBLE]: 'badge-image',
    [WorkflowFormTypeEnum.VIDEO_UPLOAD]: 'badge-video',
    [WorkflowFormTypeEnum.AUDIO_UPLOAD]: 'badge-audio'
  }
  return classMap[type] || 'badge-default'
}

// 判断是否是图片类型
const isImageType = (type) => {
  return type === WorkflowFormTypeEnum.IMAGE_UPLOAD || type === WorkflowFormTypeEnum.IMAGE_SCRIBBLE
}

// 格式化节点值
const formatNodeValue = (node) => {
  const value = node.nodeValue
  
  if (value === null || value === undefined || value === '') {
    return '-'
  }
  
  // 对于选择器类型，如果有 options，可以显示更友好
  if ((node.type === 'RADIO_SELECTOR' || node.type === 'CHECKBOX_SELECTOR') && node.options) {
    try {
      const options = JSON.parse(node.options)
      // 如果当前值在选项中，直接显示
      if (options.includes(value)) {
        return value
      }
    } catch (e) {
      // 解析失败，直接返回原值
    }
  }
  
  return String(value)
}

// 获取作品详情
const fetchWorkDetail = async (workflowResultId) => {
  loading.value = true
  imageError.value = false
  
  try {
    const response = await WorkflowResultModelApi.reqGWorkflowResultDetail({ workflowResultId })
    workDetail.value = response
  } catch (error) {
    console.error('获取作品详情失败:', error)

    workDetail.value = null
  } finally {
    loading.value = false
  }
}

// 音频播放器方法
const togglePlay = () => {
  if (!audioPlayer.value || audioError.value) return
  
  if (isPlaying.value) {
    audioPlayer.value.pause()
  } else {
    audioPlayer.value.play()
  }
  isPlaying.value = !isPlaying.value
}

const onAudioLoaded = () => {
  if (audioPlayer.value) {
    duration.value = audioPlayer.value.duration || 0
    audioError.value = false
  }
}

const onTimeUpdate = () => {
  if (audioPlayer.value) {
    currentTime.value = audioPlayer.value.currentTime || 0
    progressValue.value = currentTime.value
  }
}

const onAudioEnded = () => {
  isPlaying.value = false
  currentTime.value = 0
  progressValue.value = 0
  if (audioPlayer.value) {
    audioPlayer.value.currentTime = 0
  }
}

const onAudioError = () => {
  audioError.value = true
  isPlaying.value = false

  ElNotification.error({
    message: t('workDetail.audioLoadFailed')
  })
}

const onProgressChange = (value) => {
  if (audioPlayer.value && !audioError.value) {
    audioPlayer.value.currentTime = value
    currentTime.value = value
  }
}

const formatAudioTime = (seconds) => {
  if (!seconds || isNaN(seconds)) return '00:00'
  const mins = Math.floor(seconds / 60)
  const secs = Math.floor(seconds % 60)
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

// 视频播放器方法
const toggleVideoPlay = () => {
  if (!videoPlayer.value || videoError.value) return
  
  if (isVideoPlaying.value) {
    videoPlayer.value.pause()
  } else {
    videoPlayer.value.play()
  }
  isVideoPlaying.value = !isVideoPlaying.value
}

const onVideoLoaded = () => {
  if (videoPlayer.value) {
    videoDuration.value = videoPlayer.value.duration || 0
    videoError.value = false
  }
}

const onVideoTimeUpdate = () => {
  if (videoPlayer.value) {
    videoCurrentTime.value = videoPlayer.value.currentTime || 0
    videoProgressValue.value = videoCurrentTime.value
  }
}

const onVideoEnded = () => {
  isVideoPlaying.value = false
  videoCurrentTime.value = 0
  videoProgressValue.value = 0
  if (videoPlayer.value) {
    videoPlayer.value.currentTime = 0
  }
}

const onVideoError = () => {
  videoError.value = true
  isVideoPlaying.value = false

  ElNotification.error({
    message: t('workDetail.videoLoadFailed')
  })
}

const onVideoProgressChange = (value) => {
  if (videoPlayer.value && !videoError.value) {
    videoPlayer.value.currentTime = value
    videoCurrentTime.value = value
  }
}

const formatVideoTime = (seconds) => {
  if (!seconds || isNaN(seconds)) return '00:00'
  const mins = Math.floor(seconds / 60)
  const secs = Math.floor(seconds % 60)
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

// 关闭对话框
const handleClose = () => {
  // 停止音频播放
  if (audioPlayer.value && isPlaying.value) {
    audioPlayer.value.pause()
  }
  
  // 停止视频播放
  if (videoPlayer.value && isVideoPlaying.value) {
    videoPlayer.value.pause()
  }
  
  emit('update:visible', false)
  workDetail.value = null
  imageError.value = false
  downloading.value = false
  deleting.value = false
  
  // 重置音频播放器状态
  isPlaying.value = false
  currentTime.value = 0
  duration.value = 0
  progressValue.value = 0
  audioError.value = false
  
  // 重置视频播放器状态
  isVideoPlaying.value = false
  videoCurrentTime.value = 0
  videoDuration.value = 0
  videoProgressValue.value = 0
  videoError.value = false
}

// 下载功能
const handleDownload = async () => {
  if (!workDetail.value?.url) {

    ElNotification.error({
        message: t('workDetail.downloadUnavailable')
      })
    return
  }
  
  downloading.value = true
  
  try {

    const link = document.createElement('a')
    link.href = workDetail.value.url
    link.download = `作品_${workDetail.value.workflowResultId}_${workDetail.value.workflowName || 'unnamed'}`
      
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)

  } catch (error) {
    console.error('下载失败:', error)
    ElNotification.error({
        message: t('workDetail.downloadFailed')
      })
  } finally {
    downloading.value = false
  }
}

// 删除确认
const handleDeleteConfirm = async () => {
  try {
    await ElMessageBox.confirm(
      t('workDetail.deleteConfirm'),
      t('workDetail.deleteTitle'),
      {
        confirmButtonText: t('workDetail.deleteButton'),
        cancelButtonText: t('common.cancel'),
        type: 'warning',
        confirmButtonClass: 'el-button--danger'
      }
    )

    await handleDelete()
  } catch (error) {

    console.log('用户取消删除')
  }
}

// 删除作品
const handleDelete = async () => {
  if (!workDetail.value?.workflowResultId) {
    ElNotification.error({
        message: t('workDetail.idUnavailable')
      })
    return
  }
  
  // 保存作品ID
  const deletedId = workDetail.value.workflowResultId
  
  deleting.value = true
  
  try {
    await WorkflowResultModelApi.reqDeleteWorkflowResult({ 
      workflowResultId: deletedId 
    })
    
    ElNotification.success({
        message: t('workDetail.deleteSuccess')
      })
    
    // 关闭对话框
    handleClose()
    
    // 通知父组件作品已删除
    emit('workDeleted', deletedId)
    
  } catch (error) {
    console.error('删除作品失败:', error)

  } finally {
    deleting.value = false
  }
}

// 暴露方法给父组件调用
defineExpose({
  fetchWorkDetail
})
</script>

<style scoped>
/* 对话框样式 */
.dialog-loading {
  padding: 20px;
}

.detail-skeleton {
  display: flex;
  gap: 24px;
  align-items: stretch;
}

.skeleton-image-large {
  width: 280px;
  background: var(--el-fill-color-light);
  border-radius: 8px;
  flex-shrink: 0;
  flex: 1;
}

.skeleton-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding-top: 8px;
}

.work-detail-content {
  display: flex;
  gap: 24px;
  align-items: stretch;
}

.detail-preview-section {
  flex-shrink: 0;
  width: 280px;
  display: flex;
}

.detail-image-container {
  width: 100%;
  height: fit-content;
  max-height: 500px;
  border-radius: 8px;
  overflow: hidden;
  background: var(--el-fill-color-light);
  display: flex;
  align-items: center;
  justify-content: center;
}

.detail-image {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
  border-radius: 8px;
}

.detail-3d-model {
  width: 100%;
  height: 100%;
  border-radius: 8px;
}

.detail-image-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: var(--el-text-color-placeholder);
}

.detail-image-error p {
  margin: 8px 0 0 0;
  font-size: 14px;
}

/* 音频播放器样式 */
.audio-player-container {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.audio-player-wrapper {
  width: 100%;
  padding: 24px;
  background: var(--el-bg-color);
  border-radius: 12px;
  border: 1px solid var(--el-border-color-light);
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.audio-visual {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  padding: 20px 0;
  background: linear-gradient(135deg, color-mix(in srgb, var(--el-color-primary) 5%, transparent), color-mix(in srgb, var(--el-color-primary) 2%, transparent));
  border-radius: 8px;
}

.audio-visual .audio-icon {
  color: var(--el-color-primary);
}

.audio-title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  text-align: center;
  word-break: break-word;
}

.audio-controls {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.player-controls {
  display: flex;
  justify-content: center;
}

.progress-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.time-display {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  min-width: 40px;
  text-align: center;
}

.audio-progress {
  flex: 1;
}

.audio-progress :deep(.el-slider__runway) {
  height: 6px;
}

.audio-progress :deep(.el-slider__button) {
  width: 16px;
  height: 16px;
}

.audio-error {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: var(--el-color-danger);
  font-size: 14px;
  padding: 12px;
  background: color-mix(in srgb, var(--el-color-danger) 10%, transparent);
  border-radius: 6px;
  border: 1px solid color-mix(in srgb, var(--el-color-danger) 20%, transparent);
}

/* 视频播放器样式 */
.video-player-container {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.video-player-wrapper {
  width: 100%;
  background: var(--el-bg-color);
  border-radius: 12px;
  border: 1px solid var(--el-border-color-light);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.video-preview-area {
  position: relative;
  width: 100%;
  aspect-ratio: 16/9;
  background: #000;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.detail-video {
  width: 100%;
  height: 100%;
  object-fit: contain;
  outline: none;
}

.detail-video.playing {
  cursor: default;
}

.video-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.video-overlay:hover {
  background: rgba(0, 0, 0, 0.5);
}

.play-button-large {
  width: 80px;
  height: 80px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--el-color-primary);
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.play-button-large:hover {
  background: rgba(255, 255, 255, 1);
  transform: scale(1.1);
}

.video-error-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: var(--el-fill-color-light);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: var(--el-text-color-placeholder);
}

.video-error-overlay p {
  margin: 8px 0 0 0;
  font-size: 14px;
}

.video-controls {
  padding: 16px;
  background: var(--el-bg-color);
  display: flex;
  align-items: center;
  gap: 16px;
}

.video-control-buttons {
  flex-shrink: 0;
}

.video-progress-section {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 12px;
}

.video-progress {
  flex: 1;
}

.video-progress :deep(.el-slider__runway) {
  height: 4px;
}

.video-progress :deep(.el-slider__button) {
  width: 14px;
  height: 14px;
}

.detail-info-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
  overflow: hidden;
}

.detail-info-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 8px 16px 8px 0;
  overflow-y: auto;
}

/* 自定义滚动条样式 */
.detail-info-section::-webkit-scrollbar {
  width: 6px;
}

.detail-info-section::-webkit-scrollbar-track {
  background: var(--el-fill-color-lighter);
  border-radius: 3px;
}

.detail-info-section::-webkit-scrollbar-thumb {
  background: var(--el-border-color);
  border-radius: 3px;
  transition: background 0.3s;
}

.detail-info-section::-webkit-scrollbar-thumb:hover {
  background: var(--el-border-color-dark);
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-label {
  font-size: 14px;
  font-weight: 600;
  color: var(--el-text-color-secondary);
}

.info-value {
  font-size: 16px;
  color: var(--el-text-color-primary);
  word-break: break-all;
}

.task-id-text {
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  background: var(--el-fill-color-light);
  padding: 8px 12px;
  border-radius: 6px;
  font-size: 14px;
  border: 1px solid var(--el-border-color-lighter);
}

.type-display {
  display: flex;
  align-items: center;
  gap: 8px;
}

.type-icon {
  width: 18px;
  height: 18px;
  flex-shrink: 0;
}

.type-icon :deep(svg) {
  width: 100%;
  height: 100%;
  stroke: var(--el-color-primary);
}

/* 表单参数展示样式 - 简约风格 */
.form-params-section {
  margin-top: 4px;
}

.form-params-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.param-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.param-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.param-header {
  display: flex;
  align-items: center;
  gap: 8px;
}

.param-type-badge {
  display: inline-flex;
  align-items: center;
  padding: 2px 6px;
  border-radius: 3px;
  font-size: 11px;
  font-weight: 500;
  background: var(--el-fill-color);
  color: var(--el-text-color-secondary);
  letter-spacing: 0.3px;
  flex-shrink: 0;
}

.param-tips {
  color: var(--el-text-color-regular);
  font-size: 13px;
  flex: 1;
  line-height: 1.4;
}

.param-value {
  color: var(--el-text-color-primary);
  font-size: 13px;
  line-height: 1.6;
  word-break: break-word;
  white-space: pre-wrap;
}

/* 参数图片缩略图样式 */
.param-image-thumbnail {
  display: flex;
  justify-content: flex-start;
}

.thumbnail-image {
  width: 64px;
  height: 64px;
  border-radius: 4px;
  cursor: pointer;
  transition: opacity 0.2s;
  overflow: hidden;
}

.thumbnail-image:hover {
  opacity: 0.85;
}

.thumbnail-error {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 64px;
  height: 64px;
  color: var(--el-text-color-placeholder);
  background: var(--el-fill-color-light);
  border-radius: 4px;
}

.thumbnail-error .el-icon {
  font-size: 20px;
}

.info-actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
  padding: 12px 16px 0 0;
  border-top: 1px solid var(--el-border-color-lighter);
}

.action-button {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  user-select: none;
  border: 1px solid;
  min-height: 32px;
  flex: 1;
}

.action-button .el-icon {
  font-size: 14px;
}

.download-button {
  background: var(--el-color-primary);
  color: white;
  border-color: var(--el-color-primary);
}

.download-button:hover:not(.loading) {
  background: var(--el-color-primary-dark-2);
  border-color: var(--el-color-primary-dark-2);
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.delete-button {
  background: transparent;
  color: var(--el-color-danger);
  border-color: var(--el-color-danger);
}

.delete-button:hover:not(.loading) {
  background: var(--el-color-danger);
  color: white;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.action-button.loading {
  cursor: not-allowed;
  opacity: 0.7;
}

.loading-icon {
  animation: rotate 1s linear infinite;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.dialog-error {
  text-align: center;
  padding: 40px 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .work-detail-content {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }

  .detail-preview-section {
    width: 100%;
    display: block;
  }

  .detail-image-container {
    height: 250px;
    flex: none;
  }

  .skeleton-image-large {
    width: 100%;
    height: 250px;
    flex: none;
  }

  .detail-skeleton {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }
  
  .info-actions {
    flex-direction: column;
    gap: 8px;
  }
  
  .action-button {
    width: 100%;
    flex: none;
  }
}

@media (max-width: 480px) {
  :deep(.el-dialog) {
    width: 95% !important;
    margin: 0 auto;
  }

  .detail-image-container {
    height: 200px;
    flex: none;
  }
  
  .audio-player-wrapper {
    padding: 16px;
    gap: 16px;
  }
  
  .audio-visual {
    padding: 16px 0;
  }
  
  .audio-visual .audio-icon {
    width: 60px;
    height: 60px;
  }
  
  .audio-title {
    font-size: 14px;
  }
  
  .video-player-wrapper {
    border-radius: 8px;
  }
  
  .video-controls {
    padding: 12px;
    gap: 12px;
  }
  
  .play-button-large {
    width: 60px;
    height: 60px;
  }
  
  .video-progress-section {
    gap: 8px;
  }
}
</style> 