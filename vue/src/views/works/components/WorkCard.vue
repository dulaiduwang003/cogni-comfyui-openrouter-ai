<template>
  <div 
    class="work-card"
    :class="{ 'selection-mode': selectionMode, 'selected': isSelected }"
    @click="handleCardClick"
  >
    <!-- 选择模式复选框 -->
    <div v-if="selectionMode" class="selection-checkbox" @click.stop="handleSelect">
      <el-checkbox 
        :model-value="isSelected"
        size="small"
      />
    </div>
    
    <div class="work-image">
      <!-- 3D模型预览 -->
      <Model3DPreview
        v-if="work.type === WorkflowResultModelTypeEnum.MODEL && !work.imageError"
        :model-src="work.url"
        :alt="`${t('works.model3D.alt')} ${work.workflowResultId}`"
        @error="handleImageError"
      />
      <!-- 视频预览 -->
      <div v-else-if="work.type === WorkflowResultModelTypeEnum.VIDEO" class="video-preview">
        <video 
          :src="work.url" 
          @error="handleImageError"
          preload="metadata"
          muted
          disablePictureInPicture
          controlslist="nodownload nofullscreen noremoteplayback"
          class="video-element"
 
        >
          {{ t('works.workCard.videoNotSupported') }}
        </video>
      </div>
      <!-- 音频预览 -->
      <div v-else-if="work.type === WorkflowResultModelTypeEnum.AUDIO" class="audio-preview-placeholder">
        <svg width="64" height="64" viewBox="0 0 24 24" fill="none" class="audio-icon">
          <path d="M9 18V5l12-2v13" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          <circle cx="6" cy="18" r="3" stroke="currentColor" stroke-width="2"/>
          <circle cx="18" cy="16" r="3" stroke="currentColor" stroke-width="2"/>
        </svg>
        <p>{{ t('works.workCard.audioNoPreview') }}</p>
      </div>
      <!-- 普通图片预览 -->
      <img 
        v-else-if="!work.imageError"
        :src="work.url" 
        @error="handleImageError"
      />
      <!-- 错误状态 -->
      <div v-else class="image-error-placeholder">
        <el-icon size="48" color="#ccc"><Picture /></el-icon>
        <p>{{ t('works.workCard.loadFailed') }}</p>
      </div>
      <!-- 渲染类型标签 -->
      <div class="workflow-name-tag" v-if="work.type">
        <div class="tag-icon" v-html="getTypeIcon(work.type)"></div>
        <span>{{ getTypeDisplayName(work.type) }}</span>
      </div>
    </div>
    
    <div class="work-info">
      <h3 class="work-title">{{ work.workflowName || `${t('works.workCard.workPrefix')} ${work.workflowResultId}` }}</h3>
      
      <div class="work-meta">
        <div class="work-time">
          {{ formatTime(work.createTime) }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Picture } from '@element-plus/icons-vue'
import Model3DPreview from './Model3DPreview.vue'
import { WorkflowResultModelTypeEnum } from '@/enums/workflow'
import { useI18n } from 'vue-i18n'

const { t, locale } = useI18n()

// 导入SVG图标
import ImageGenIcon from '@/assets/svg/image-gen.svg?raw'
import VideoGenIcon from '@/assets/svg/video-gen.svg?raw'
import AudioGenIcon from '@/assets/svg/audio-gen.svg?raw'
import ModelGenIcon from '@/assets/svg/model-gen.svg?raw'

// Props
const props = defineProps({
  work: {
    type: Object,
    required: true
  },
  selectionMode: {
    type: Boolean,
    default: false
  },
  isSelected: {
    type: Boolean,
    default: false
  }
})

// Emits
const emit = defineEmits(['click', 'imageError', 'select'])

// 处理卡片点击
const handleCardClick = () => {
  if (props.selectionMode) {
    handleSelect()
  } else {
    emit('click', props.work)
  }
}

// 处理选择
const handleSelect = () => {
  emit('select', props.work)
}

// 获取作品类型显示名称
const getTypeDisplayName = (type) => {
  const typeMap = {
    [WorkflowResultModelTypeEnum.IMAGE]: t('works.workCard.type.image'),
    [WorkflowResultModelTypeEnum.VIDEO]: t('works.workCard.type.video'),
    [WorkflowResultModelTypeEnum.AUDIO]: t('works.workCard.type.audio'),
    [WorkflowResultModelTypeEnum.MODEL]: t('works.workCard.type.model')
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

// 时间格式化
const formatTime = (timeStr) => {
  if (!timeStr) return ''
  
  const date = new Date(timeStr)
  const now = new Date()
  const diff = now - date
  
  const minute = 60 * 1000
  const hour = 60 * minute
  const day = 24 * hour
  
  if (diff < minute) {
    return t('works.workCard.time.justNow')
  } else if (diff < hour) {
    return `${Math.floor(diff / minute)} ${t('works.workCard.time.minutesAgo')}`
  } else if (diff < day) {
    return `${Math.floor(diff / hour)} ${t('works.workCard.time.hoursAgo')}`
  } else {
    const localeCode = locale.value === 'zh-CN' ? 'zh-CN' : 'en-US'
    return date.toLocaleDateString(localeCode)
  }
}

// 图片加载错误处理
const handleImageError = () => {
  emit('imageError', props.work)
}
</script>

<style scoped>
.work-card {
  background: var(--el-bg-color);
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  cursor: pointer;
  border: 1px solid var(--el-border-color-lighter);
  width: 100%;
  position: relative;
}

.work-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.work-card.selection-mode {
  cursor: pointer;
}

.work-card.selected {
  border: 2px solid var(--el-color-primary);
  box-shadow: 0 4px 16px rgba(64, 158, 255, 0.3);
}

/* 暗色主题下选中状态更明显 */
html.dark .work-card.selected {
  box-shadow: 0 4px 20px rgba(64, 158, 255, 0.5);
}

.selection-checkbox {
  position: absolute;
  top: 8px;
  left: 8px;
  z-index: 10;

  border-radius: 4px;
  padding: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(8px);
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.selection-checkbox:hover {

  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
}

/* 暗色主题适配 */
html.dark .selection-checkbox {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

html.dark .selection-checkbox:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.5);
}

/* 复选框样式 - 参考 CheckboxSelector */
.selection-checkbox :deep(.el-checkbox__inner) {
  background-color: var(--el-bg-color);
  border-color: var(--el-border-color);
  transition: all 0.3s ease;
}

.selection-checkbox :deep(.el-checkbox__input:hover .el-checkbox__inner) {
  border-color: var(--el-color-primary);
}

.selection-checkbox :deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
  background-color: var(--el-color-primary);
  border-color: var(--el-color-primary);
}

.selection-checkbox :deep(.el-checkbox__input.is-checked .el-checkbox__inner::after) {
  border-color: #fff;
}

/* 暗色模式适配 */
@media (prefers-color-scheme: dark) {
  .selection-checkbox :deep(.el-checkbox__inner) {
    background-color: var(--el-fill-color-dark);
    border-color: var(--el-border-color-dark);
  }
}

.dark .selection-checkbox :deep(.el-checkbox__inner) {
  background-color: var(--el-fill-color-dark);
  border-color: var(--el-border-color-dark);
}

.dark .selection-checkbox :deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
  background-color: var(--el-color-primary);
  border-color: var(--el-color-primary);
}

.work-image {
  position: relative;
  aspect-ratio: 4/3;
  overflow: hidden;
  background: var(--el-fill-color-light);
  display: flex;
  align-items: center;
  justify-content: center;
}

.work-image img {
  max-width: 100%;
  max-height: 100%;
  width: auto;
  height: auto;
  object-fit: contain;
  transition: transform 0.3s ease;
  border-radius: 8px;
}

.work-card:hover .work-image img {
  transform: scale(1.05);
}

.image-error-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
}

.image-error-placeholder p {
  margin: 8px 0 0 0;
  font-size: 14px;
  color: var(--el-text-color-placeholder);
}

.audio-preview-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;

}

.audio-preview-placeholder .audio-icon {
  color: var(--el-color-primary);
  margin-bottom: 8px;
}

.audio-preview-placeholder p {
  margin: 0;
  font-size: 14px;
  color: var(--el-text-color-secondary);
  font-weight: 500;
}

.video-preview {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.video-element {
  max-width: 100%;
  max-height: 100%;
  width: auto;
  height: auto;
  object-fit: contain;
  transition: transform 0.3s ease;
  border-radius: 8px;
}

.work-card:hover .video-element {
  transform: scale(1.05);
}



.workflow-name-tag {
  position: absolute;
  top: 8px;
  right: 8px;
  background-color: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  backdrop-filter: blur(4px);
  z-index: 2;
  display: flex;
  align-items: center;
  gap: 4px;
}

.tag-icon {
  width: 14px;
  height: 14px;
  flex-shrink: 0;
}

.tag-icon :deep(svg) {
  width: 100%;
  height: 100%;
  stroke: currentColor;
}

.work-info {
  padding: 20px;
}

.work-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  margin: 0 0 8px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.work-meta {
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

.work-time {
  font-size: 12px;
  color: var(--el-text-color-placeholder);
}
</style> 