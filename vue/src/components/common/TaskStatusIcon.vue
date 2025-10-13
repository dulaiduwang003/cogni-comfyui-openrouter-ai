<template>
  <div class="task-status-icon" :class="getStatusClass(status, artworkType)">
    <!-- 待处理状态 - 时钟图标 -->
    <svg v-if="status === 'WAIT'" width="20" height="20" viewBox="0 0 24 24" fill="none">
      <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
      <path d="M12 6v6l4 2" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
    </svg>
    
    <!-- 处理中状态 - 齿轮图标 (带动画) -->
    <svg v-else-if="status === 'BUILD'" width="20" height="20" viewBox="0 0 24 24" fill="none" class="processing-icon">
      <path d="M12 15a3 3 0 1 0 0-6 3 3 0 0 0 0 6Z" stroke="currentColor" stroke-width="2"/>
      <path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1 1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1Z" stroke="currentColor" stroke-width="2"/>
    </svg>
    
    <!-- 失败状态 - X圆圈图标 -->
    <svg v-else-if="status === 'FAILED'" width="20" height="20" viewBox="0 0 24 24" fill="none">
      <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
      <path d="m15 9-6 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
      <path d="m9 9 6 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
    </svg>
    
    <!-- 取消状态 - 禁止图标 -->
    <svg v-else-if="status === 'CANCELED'" width="20" height="20" viewBox="0 0 24 24" fill="none">
      <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
      <path d="m4.9 4.9 14.2 14.2" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
    </svg>
    
    <!-- 成功状态 - 使用作品类型图标 -->
    <template v-else-if="status === 'SUCCEED'">
      <svg v-if="artworkType === WorkflowResultModelTypeEnum.IMAGE" width="20" height="20" viewBox="0 0 24 24" fill="none">
        <rect x="3" y="3" width="18" height="18" rx="2" ry="2" stroke="currentColor" stroke-width="2"/>
        <circle cx="8.5" cy="8.5" r="1.5" stroke="currentColor" stroke-width="2"/>
        <path d="M21 15l-5-5L5 21" stroke="currentColor" stroke-width="2"/>
      </svg>
      <svg v-else-if="artworkType === WorkflowResultModelTypeEnum.VIDEO" width="20" height="20" viewBox="0 0 24 24" fill="none">
        <path d="M23 7l-7 5 7 5V7z" stroke="currentColor" stroke-width="2"/>
        <rect x="1" y="5" width="15" height="14" rx="2" ry="2" stroke="currentColor" stroke-width="2"/>
      </svg>
      <svg v-else-if="artworkType === WorkflowResultModelTypeEnum.AUDIO" width="20" height="20" viewBox="0 0 24 24" fill="none">
        <path d="M9 18V5l12-2v13" stroke="currentColor" stroke-width="2"/>
        <circle cx="6" cy="18" r="3" stroke="currentColor" stroke-width="2"/>
        <circle cx="18" cy="16" r="3" stroke="currentColor" stroke-width="2"/>
      </svg>
      <svg v-else-if="artworkType === WorkflowResultModelTypeEnum.MODEL" width="20" height="20" viewBox="0 0 24 24" fill="none">
        <path d="M12 2l8 4.5v11L12 22l-8-4.5v-11L12 2z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        <path d="M12 2v20" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        <path d="M4 6.5l8 4.5 8-4.5" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
      <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="none">
        <path d="M9 12l2 2 4-4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
      </svg>
    </template>
    
    <!-- 未知状态 - 问号图标 -->
    <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="none">
      <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
      <path d="M9.09 9a3 3 0 0 1 5.83 1c0 2-3 3-3 3" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
      <path d="M12 17h.01" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
    </svg>
  </div>
</template>

<script setup lang="ts">
import { WorkflowTaskStatusEnum, WorkflowResultModelTypeEnum } from '@/enums'

interface Props {
  status: WorkflowTaskStatusEnum | string
  artworkType?: WorkflowResultModelTypeEnum | string
}

 defineProps<Props>()

// 获取状态样式类
const getStatusClass = (status: string, artworkType?: string) => {
  const classes: Record<string, boolean> = {
    'status-wait': status === WorkflowTaskStatusEnum.WAIT,
    'status-build': status === WorkflowTaskStatusEnum.BUILD, 
    'status-succeed': status === WorkflowTaskStatusEnum.SUCCEED,
    'status-failed': status === WorkflowTaskStatusEnum.FAILED,
    'status-canceled': status === WorkflowTaskStatusEnum.CANCELED,
    'status-unknown': ![
      WorkflowTaskStatusEnum.WAIT,
      WorkflowTaskStatusEnum.BUILD,
      WorkflowTaskStatusEnum.SUCCEED,
      WorkflowTaskStatusEnum.FAILED,
      WorkflowTaskStatusEnum.CANCELED
    ].includes(status as WorkflowTaskStatusEnum)
  }
  
  // 为成功状态添加作品类型类
  if (status === WorkflowTaskStatusEnum.SUCCEED && artworkType) {
    classes[`type-${artworkType.toLowerCase()}`] = true
  }
  
  return classes
}

// 获取类型标签文字



</script>

<style scoped>
.task-status-icon {
  width: 41px;
  height: 41px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  border: 1px solid var(--el-border-color-light);
  transition: all 0.2s ease;
}

/* 待处理状态 - 橙色主题 */
.task-status-icon.status-wait {
  background: linear-gradient(135deg, color-mix(in srgb, var(--el-color-warning) 15%, transparent), color-mix(in srgb, var(--el-color-warning) 8%, transparent));
  border-color: color-mix(in srgb, var(--el-color-warning) 30%, transparent);
  color: var(--el-color-warning);
}

/* 处理中状态 - 蓝色主题 + 动画 */
.task-status-icon.status-build {
  background: linear-gradient(135deg, color-mix(in srgb, var(--el-color-primary) 15%, transparent), color-mix(in srgb, var(--el-color-primary) 8%, transparent));
  border-color: color-mix(in srgb, var(--el-color-primary) 30%, transparent);
  color: var(--el-color-primary);
}

/* 处理中动画 */
.processing-icon {
  animation: rotate-gear 2s linear infinite;
}

@keyframes rotate-gear {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

/* 成功状态 - 根据作品类型使用不同颜色 */
.task-status-icon.status-succeed.type-image {
  background: linear-gradient(135deg, color-mix(in srgb, var(--el-color-primary) 15%, transparent), color-mix(in srgb, var(--el-color-primary) 8%, transparent));
  border-color: color-mix(in srgb, var(--el-color-primary) 30%, transparent);
  color: var(--el-color-primary);
}

.task-status-icon.status-succeed.type-video {
  background: linear-gradient(135deg, color-mix(in srgb, var(--el-color-danger) 15%, transparent), color-mix(in srgb, var(--el-color-danger) 8%, transparent));
  border-color: color-mix(in srgb, var(--el-color-danger) 30%, transparent);
  color: var(--el-color-danger);
}

.task-status-icon.status-succeed.type-audio {
  background: linear-gradient(135deg, color-mix(in srgb, var(--el-color-success) 15%, transparent), color-mix(in srgb, var(--el-color-success) 8%, transparent));
  border-color: color-mix(in srgb, var(--el-color-success) 30%, transparent);
  color: var(--el-color-success);
}

.task-status-icon.status-succeed.type-model {
  background: linear-gradient(135deg, color-mix(in srgb, var(--el-color-warning) 15%, transparent), color-mix(in srgb, var(--el-color-warning) 8%, transparent));
  border-color: color-mix(in srgb, var(--el-color-warning) 30%, transparent);
  color: var(--el-color-warning);
}

/* 默认成功状态样式 (未知类型或无类型) */
.task-status-icon.status-succeed:not(.type-image):not(.type-video):not(.type-audio):not(.type-model) {
  background: linear-gradient(135deg, color-mix(in srgb, var(--el-color-success) 15%, transparent), color-mix(in srgb, var(--el-color-success) 8%, transparent));
  border-color: color-mix(in srgb, var(--el-color-success) 30%, transparent);
  color: var(--el-color-success);
}

/* 失败状态 - 红色主题 */
.task-status-icon.status-failed {
  background: linear-gradient(135deg, color-mix(in srgb, var(--el-color-danger) 15%, transparent), color-mix(in srgb, var(--el-color-danger) 8%, transparent));
  border-color: color-mix(in srgb, var(--el-color-danger) 30%, transparent);
  color: var(--el-color-danger);
}

/* 取消状态 - 灰色主题 */
.task-status-icon.status-canceled {
  background: linear-gradient(135deg, color-mix(in srgb, var(--el-color-info) 15%, transparent), color-mix(in srgb, var(--el-color-info) 8%, transparent));
  border-color: color-mix(in srgb, var(--el-color-info) 30%, transparent);
  color: var(--el-color-info);
}

/* 未知状态 - 灰色主题 */
.task-status-icon.status-unknown {
  background: linear-gradient(135deg, color-mix(in srgb, var(--el-text-color-secondary) 15%, transparent), color-mix(in srgb, var(--el-text-color-secondary) 8%, transparent));
  border-color: color-mix(in srgb, var(--el-text-color-secondary) 30%, transparent);
  color: var(--el-text-color-secondary);
}

.task-status-icon:hover {
  transform: scale(1.05);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* 处理中状态的特殊效果 - 脉冲 */
.task-status-icon.status-build {
  position: relative;
}

.task-status-icon.status-build::after {
  content: '';
  position: absolute;
  top: -2px;
  left: -2px;
  right: -2px;
  bottom: -2px;
  border-radius: 10px;
  border: 2px solid var(--el-color-primary);
  opacity: 0;
  animation: pulse-ring 2s infinite;
}

@keyframes pulse-ring {
  0% {
    transform: scale(0.8);
    opacity: 1;
  }
  100% {
    transform: scale(1.2);
    opacity: 0;
  }
}


</style> 