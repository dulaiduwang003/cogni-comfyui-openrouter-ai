<template>
  <div class="task-panel-container" ref="taskPanelRef">
    <button class="navbar-button task-button" @click="toggleTaskPanel">
      <svg width="16" height="16" viewBox="0 0 16 16" fill="none" class="navbar-icon">
        <path d="M8 2V8L12 12" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
        <circle cx="8" cy="8" r="6" stroke="currentColor" stroke-width="1.5"/>
      </svg>
      <span class="navbar-text">{{ t('task.title') }}</span>
      <div v-if="tasksState.runningCount > 0" class="task-badge">{{ tasksState.runningCount }}</div>
    </button>
    
    <!-- 任务面板 -->
    <transition name="task-panel">
      <div v-show="showTaskPanel" class="task-panel">
        <div class="task-panel-header">
          <div class="header-content">
            <h3>{{ t('task.taskProgress') }}</h3>
            <p class="task-description">{{ t('task.taskDescription') }}</p>
          </div>
          <button class="close-button" @click="showTaskPanel = false">
            <svg width="14" height="14" viewBox="0 0 14 14" fill="none">
              <path d="M10.5 3.5L3.5 10.5M3.5 3.5L10.5 10.5" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
            </svg>
          </button>
        </div>
        
        <!-- 状态过滤器 -->
        <div class="task-filter">
          <div class="filter-tags">
            <button 
              @click="selectStatus('')"
              :class="['filter-tag', { active: tasksState.selectedStatus === '' }]"
            >
              {{ t('task.all') }}
            </button>
            <button 
              @click="selectStatus(WorkflowTaskStatusEnum.WAIT)"
              :class="['filter-tag', { active: tasksState.selectedStatus === WorkflowTaskStatusEnum.WAIT }]"
            >
              {{ t('task.waiting') }}
            </button>
            <button 
              @click="selectStatus(WorkflowTaskStatusEnum.BUILD)"
              :class="['filter-tag', { active: tasksState.selectedStatus === WorkflowTaskStatusEnum.BUILD }]"
            >
              {{ t('task.building') }}
            </button>
            <button 
              @click="selectStatus(WorkflowTaskStatusEnum.SUCCEED)"
              :class="['filter-tag', { active: tasksState.selectedStatus === WorkflowTaskStatusEnum.SUCCEED }]"
            >
              {{ t('task.completed') }}
            </button>
            <button 
              @click="selectStatus(WorkflowTaskStatusEnum.FAILED)"
              :class="['filter-tag', { active: tasksState.selectedStatus === WorkflowTaskStatusEnum.FAILED }]"
            >
              {{ t('task.failed') }}
            </button>
            <button 
              @click="selectStatus(WorkflowTaskStatusEnum.CANCELED)"
              :class="['filter-tag', { active: tasksState.selectedStatus === WorkflowTaskStatusEnum.CANCELED }]"
            >
              {{ t('task.canceled') }}
            </button>
          </div>
        </div>
        
        <div class="task-panel-content" ref="taskListRef" @scroll="handleScroll">
          <div v-if="tasksState.loading && tasksState.tasks.length === 0" class="task-loading">
            <div class="loading-spinner"></div>
            <span>{{ t('common.loading') }}</span>
          </div>
          
          <div v-else-if="tasksState.tasks.length === 0" class="task-empty">
            <svg width="48" height="48" viewBox="0 0 48 48" fill="none" class="empty-icon">
              <circle cx="24" cy="24" r="20" stroke="currentColor" stroke-width="2"/>
              <path d="M16 24H32M24 16V32" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
            <p>{{ t('task.noTasks') }}</p>
          </div>
          
          <div v-else class="task-list">
            <div v-for="task in tasksState.tasks" :key="task.taskId" class="task-item">
              <div class="task-header">
                <div class="task-main">
                  <!-- 任务状态图标 -->
                  <TaskStatusIcon 
                    :status="task.status" 
                    :artwork-type="getArtworkType(task)"
                  />
                  <div class="task-left">
                    <div class="workflow-info">
                      <!-- 类型标签 - 只在成功状态且有作品类型时显示 -->
                      <div 
                        v-if="task.status === WorkflowTaskStatusEnum.SUCCEED && getArtworkType(task)" 
                        class="type-label"
                      >
                        {{ getTypeLabel(getArtworkType(task)) }}
                      </div>
                      <span v-if="task.workflowName" class="workflow-name">{{ task.workflowName }}</span>
                    </div>
                    <span class="task-time">{{ formatTime(task.createTime) }}</span>
                  </div>
                </div>
                <div class="task-right">
                  <span class="task-status" :class="formatTaskStatus(task.status).class">
                    {{ formatTaskStatus(task.status).text }}
                    <span v-if="task.status === WorkflowTaskStatusEnum.WAIT" class="queue-position">
                      <template v-if="task.location && task.location > 0">#{{ task.location }}</template>
                      <template v-else>{{ t('task.joining') }}</template>
                    </span>
                  </span>
                </div>
              </div>
              
              <!-- 分割线 -->
              <div class="task-divider"></div>
              
              <!-- 操作按钮区域 -->
              <div class="task-actions-bottom">
                <!-- 任务ID显示 -->
                <div class="task-id-display">
                  {{ t('task.taskId') }}: {{ task.taskId }}
                </div>
                
                <!-- 按钮组 -->
                <div class="action-buttons-group">
                <!-- 查看作品按钮 -->
                <button 
                  v-if="task.status === WorkflowTaskStatusEnum.SUCCEED"
                  class="action-btn view-btn"
                  @click="viewArtwork(task)"
                  title="查看作品"
                >
                  <svg width="12" height="12" viewBox="0 0 24 24" fill="none">
                    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" stroke="currentColor" stroke-width="2"/>
                    <circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="2"/>
                  </svg>
                </button>
                
                <!-- 取消按钮 -->
                <button 
                  v-if="task.status === WorkflowTaskStatusEnum.WAIT"
                  class="action-btn cancel-btn"
                  :class="{ 'is-loading': isCanceling(task.taskId) }"
                  :disabled="isCanceling(task.taskId)"
                  @click="cancelTask(task)"
                  title="取消任务"
                >
                  <svg v-if="!isCanceling(task.taskId)" width="12" height="12" viewBox="0 0 24 24" fill="none">
                    <path d="m18 6-12 12" stroke="currentColor" stroke-width="2"/>
                    <path d="m6 6 12 12" stroke="currentColor" stroke-width="2"/>
                  </svg>
                  <span v-else class="btn-spinner" />
                </button>
                
                <!-- 重新制作按钮 -->
                <button 
                  v-if="task.status === WorkflowTaskStatusEnum.FAILED || task.status === WorkflowTaskStatusEnum.SUCCEED || task.status === WorkflowTaskStatusEnum.CANCELED"
                  class="action-btn remake-btn"
                  :class="{ 'is-loading': isRemaking(task.taskId) }"
                  :disabled="isRemaking(task.taskId)"
                  @click="remakeTask(task)"
                  title="重新制作"
                >
                  <svg v-if="!isRemaking(task.taskId)" width="12" height="12" viewBox="0 0 24 24" fill="none">
                    <path d="M21 12a9 9 0 0 0-9-9 9.75 9.75 0 0 0-6.74 2.74L3 8" stroke="currentColor" stroke-width="2"/>
                    <path d="M3 3v5h5" stroke="currentColor" stroke-width="2"/>
                    <path d="M3 12a9 9 0 0 0 9 9 9.75 9.75 0 0 0 6.74-2.74L21 16" stroke="currentColor" stroke-width="2"/>
                    <path d="M21 21v-5h-5" stroke="currentColor" stroke-width="2"/>
                  </svg>
                  <span v-else class="btn-spinner" />
                </button>
                </div>
              </div>
              <!-- 只有构建中的任务才显示进度条 -->
              <div v-if="task.status === WorkflowTaskStatusEnum.BUILD" class="task-progress">
                <div class="progress-bar">
                  <div 
                    class="progress-fill" 
                    :style="{ width: `${task.progress}%` }"
                  ></div>
                </div>
                <span class="progress-text">{{ task.progress }}%</span>
              </div>
            </div>
          </div>
          
          <div v-if="tasksState.loading && tasksState.tasks.length > 0" class="task-loading-more">
            <div class="loading-spinner small"></div>
            <span>{{ t('task.loadingMore') }}</span>
          </div>
          
          <div v-if="!tasksState.hasMore && tasksState.tasks.length > 0" class="task-no-more">
            {{ t('task.noMore') }}
          </div>
        </div>
      </div>
    </transition>
    
    <!-- 作品详情对话框 -->
    <WorkDetailDialog
      ref="workDetailDialogRef"
      v-model:visible="workDetailVisible"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useTaskWebSocketStore } from '@/stores/modules/taskWebsocket'
import { WorkflowTaskStatusEnum, WorkflowResultModelTypeEnum } from '@/enums'
import { comfyuiTaskApi } from '@/api/workflow-task/workflow-task'
import { ElNotification } from 'element-plus'
import TaskStatusIcon from './TaskStatusIcon.vue'
import WorkDetailDialog from './WorkDetailDialog.vue'

const { t } = useI18n()

// 使用WebSocket Store
const webSocketStore = useTaskWebSocketStore()

// 任务面板状态
const showTaskPanel = ref(false)
const taskPanelRef = ref<HTMLElement>()
const taskListRef = ref<HTMLElement>()

// 作品详情对话框状态
const workDetailVisible = ref(false)
const workDetailDialogRef = ref()

// 每个任务的操作loading状态，避免重复提交
const cancelingTaskIds = ref<Set<string | number>>(new Set())
const remakingTaskIds = ref<Set<string | number>>(new Set())

const isCanceling = (taskId: string | number) => cancelingTaskIds.value.has(taskId)
const isRemaking = (taskId: string | number) => remakingTaskIds.value.has(taskId)

// 从WebSocket Store获取任务状态
const tasksState = computed(() => webSocketStore.getTasksState)

// 切换任务面板
const toggleTaskPanel = async () => {
  showTaskPanel.value = !showTaskPanel.value
  // 打开时刷新任务列表
  if (showTaskPanel.value) {
    await webSocketStore.refreshTasks()
  }
}

// 滚动加载更多
const handleScroll = async () => {
  if (!taskListRef.value || tasksState.value.loading || !tasksState.value.hasMore) return
  
  const { scrollTop, scrollHeight, clientHeight } = taskListRef.value
  
  // 当滚动到底部附近时加载更多
  if (scrollHeight - scrollTop - clientHeight < 50) {
    await webSocketStore.loadMoreTasks()
  }
}

// 格式化时间
const formatTime = (timeStr: string) => {
  const time = new Date(timeStr)
  const now = new Date()
  const diff = now.getTime() - time.getTime()
  
  const minutes = Math.floor(diff / (1000 * 60))
  const hours = Math.floor(diff / (1000 * 60 * 60))
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  
  if (days > 0) return `${days}${t('task.daysAgo')}`
  if (hours > 0) return `${hours}${t('task.hoursAgo')}`
  if (minutes > 0) return `${minutes}${t('task.minutesAgo')}`
  return t('task.justNow')
}

// 格式化任务状态
const formatTaskStatus = (status: WorkflowTaskStatusEnum) => {
  switch (status) {
    case WorkflowTaskStatusEnum.WAIT:
      return { text: t('task.waitingAt'), class: 'status-pending' }
    case WorkflowTaskStatusEnum.BUILD:
      return { text: t('task.building'), class: 'status-running' }
    case WorkflowTaskStatusEnum.SUCCEED:
      return { text: t('task.completed'), class: 'status-completed' }
    case WorkflowTaskStatusEnum.CANCELED:
      return { text: t('task.canceled'), class: 'status-canceled' }
    case WorkflowTaskStatusEnum.FAILED:
      return { text: t('task.failed'), class: 'status-failed' }
    default:
      return { text: '未知', class: 'status-unknown' }
  }
}

// 选择状态过滤器
const selectStatus = (status: WorkflowTaskStatusEnum | '') => {
  webSocketStore.setStatusFilter(status)
}

// 打开任务面板的方法（供外部调用）
const openTaskPanel = async () => {
  showTaskPanel.value = true
  // 每次打开都刷新任务列表，确保显示最新数据
  await webSocketStore.refreshTasks()
}

// 点击外部关闭任务面板
const handleClickOutside = (event: MouseEvent) => {
  if (taskPanelRef.value && !taskPanelRef.value.contains(event.target as Node)) {
    showTaskPanel.value = false
  }
}

// 监听全局事件打开任务面板
const handleOpenTaskPanelEvent = () => {
  openTaskPanel()
}

// 查看作品
const viewArtwork = (task: any) => {
  console.log(task)
  if (!task.workflowResultModel) {
    console.warn('任务没有关联的作品信息')
    ElNotification.warning({
      title: t('common.warning'),
      message: t('task.noWorkInfo'),
      duration: 2000
    })
    return
  }
  
  // 打开作品详情弹窗
  workDetailVisible.value = true
  
  // 使用作品ID获取详情
  if (workDetailDialogRef.value && task.workflowResultModel.workflowResultId) {
    workDetailDialogRef.value.fetchWorkDetail(task.workflowResultModel.workflowResultId)
  }
}

// 重新制作
const remakeTask = async (task: any) => {
  if (isRemaking(task.taskId)) return
  remakingTaskIds.value.add(task.taskId)
  try {
    await comfyuiTaskApi.reqRemakeComfyuiTask({ taskId: task.taskId })
    ElNotification.success({
      title: t('common.success'),
      message: t('task.remakeSuccess'),
      duration: 3000
    })
    await webSocketStore.refreshTasks()
  } catch (error) {
    console.error('重新制作任务失败:', error)
  } finally {
    remakingTaskIds.value.delete(task.taskId)
  }
}

// 取消任务
const cancelTask = async (task: any) => {
  if (isCanceling(task.taskId)) return
  cancelingTaskIds.value.add(task.taskId)
  try {
    await comfyuiTaskApi.reqCancelComfyuiTask({ taskId: task.taskId })
    ElNotification.success({
      title: t('common.success'),
      message: t('task.cancelSuccess'),
      duration: 2000
    })
    await webSocketStore.refreshTasks()
  } catch (error) {
    console.error('取消任务失败:', error)
  } finally {
    cancelingTaskIds.value.delete(task.taskId)
  }
}

// 获取作品类型
const getArtworkType = (task: any) => {
  return task.workflowResultModel?.type || 'UNKNOWN'
}

// 获取类型标签文字
const getTypeLabel = (artworkType: string) => {
  const typeLabels = {
    [WorkflowResultModelTypeEnum.IMAGE]: t('workType.image'),
    [WorkflowResultModelTypeEnum.VIDEO]: t('workType.video'),
    [WorkflowResultModelTypeEnum.AUDIO]: t('workType.audio'),
    [WorkflowResultModelTypeEnum.MODEL]: t('workType.model')
  }
  return typeLabels[artworkType as WorkflowResultModelTypeEnum] || t('workType.work')
}



onMounted(() => {
  document.addEventListener('click', handleClickOutside)
  // 监听任务面板打开事件
  window.addEventListener('openTaskPanel', handleOpenTaskPanelEvent)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
  // 移除任务面板打开事件监听
  window.removeEventListener('openTaskPanel', handleOpenTaskPanelEvent)
})

// 暴露方法给父组件
defineExpose({
  openTaskPanel
})
</script>

<style scoped>
.navbar-button {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  background-color: transparent;
  border: 1px solid var(--el-border-color);
  border-radius: 12px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
  color: var(--el-text-color-regular);
}

.navbar-button:hover {
  background-color: var(--el-fill-color-lighter);
  border-color: var(--el-border-color-dark);
}

.navbar-icon {
  width: 16px;
  height: 16px;
  filter: none;
}

html[class^="theme-dark"] .navbar-icon,
html[class*=" theme-dark"] .navbar-icon {
  filter: brightness(0) invert(1);
}

.navbar-text {
  font-weight: 500;
  white-space: nowrap;
}

/* 任务面板样式 */
.task-panel-container {
  position: relative;
}

.task-button {
  position: relative;
}

.task-badge {
  position: absolute;
  top: -8px;
  right: -8px;
  min-width: 18px;
  height: 18px;
  background: var(--el-color-danger);
  color: white;
  border-radius: 9px;
  font-size: 11px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 4px;
  line-height: 1;
  box-shadow: 0 2px 4px rgba(245, 87, 108, 0.3);
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% { transform: scale(1); }
  50% { transform: scale(1.1); }
  100% { transform: scale(1); }
}

.task-panel {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  width: 450px;
  max-height: 500px;
  background: var(--el-bg-color);
  border: 1px solid var(--el-border-color-light);
  border-radius: 8px;
  box-shadow: 0 12px 48px rgba(0, 0, 0, 0.15);
  backdrop-filter: blur(20px);
  z-index: 1000;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.task-panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid var(--el-border-color-lighter);
  background: var(--el-fill-color-extra-light);
}

.task-panel-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.header-content {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.task-description {
  margin: 0;
  font-size: 12px;
  color: var(--el-text-color-secondary);
  font-weight: 400;
}

.close-button {
  width: 28px;
  height: 28px;
  border: none;
  background: transparent;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--el-text-color-secondary);
  transition: all 0.2s ease;
}

.close-button:hover {
  background: var(--el-fill-color);
  color: var(--el-text-color-primary);
}

.task-filter {
  padding: 16px 20px 16px 20px;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.filter-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.filter-tag {
  padding: 6px 12px;
  border: 1px solid var(--el-border-color);
  border-radius: 16px;
  background-color: var(--el-fill-color-lighter);
  color: var(--el-text-color-regular);
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;
  outline: none;
}

.filter-tag:hover {
  border-color: var(--el-border-color-dark);
  background-color: var(--el-fill-color);
  color: var(--el-text-color-primary);
}

.filter-tag.active {
  border-color: var(--el-color-primary);
  background-color: var(--el-fill-color-lighter);
  color: var(--el-color-primary);
}

.filter-tag.active:hover {
  background-color: var(--el-fill-color);
}

.task-panel-content {
  flex: 1;
  overflow-y: auto;
  padding: 0;
  min-height: 0;
}

/* 使用全局滚动条样式 */

.task-loading,
.task-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  color: var(--el-text-color-secondary);
  gap: 12px;
}

.task-empty .empty-icon {
  color: var(--el-border-color);
  opacity: 0.6;
}

.task-empty p {
  margin: 0;
  font-size: 14px;
}

.loading-spinner {
  width: 24px;
  height: 24px;
  border: 2px solid var(--el-border-color-lighter);
  border-top: 2px solid var(--el-color-primary);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.loading-spinner.small {
  width: 16px;
  height: 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.task-list {
  padding: 8px 0;
}

.task-item {
  padding: 10px 14px;
  border-bottom: 1px solid var(--el-border-color-extra-light);
  transition: all 0.2s ease;
}

.task-item:hover {
  background: var(--el-fill-color-extra-light);
}

.task-item:last-child {
  border-bottom: none;
}

.task-header {
  position: relative;
}

.task-main {
  display: flex;
  align-items: center;
  gap: 12px;
  padding-right: 60px;
}

.task-left {
  display: flex;
  flex-direction: column;
  gap: 2px;
  flex: 1;
  min-width: 0;
}

.task-right {
  position: absolute;
  top: 0;
  right: 0;
  display: flex;
  align-items: center;
  gap: 6px;
}

.task-id {
  font-size: 13px;
  font-weight: 500;
  color: var(--el-text-color-primary);
}

.task-status {
  font-size: 9px;
  font-weight: 500;
  padding: 2px 6px;
  border-radius: 8px;
  text-transform: uppercase;
  letter-spacing: 0.2px;
  display: flex;
  align-items: center;
  gap: 3px;
}

.queue-position {
  font-size: 8px;
  font-weight: 600;
  background: rgba(255, 255, 255, 0.2);
  padding: 1px 3px;
  border-radius: 3px;
  text-transform: none;
  letter-spacing: 0;
}

.status-completed {
  background: var(--el-color-primary);
  color: white;
}

.status-running {
  background: var(--el-color-primary);
  color: white;
}

.status-pending {
  background: var(--el-color-primary);
  color: white;
}

.status-failed {
  background: var(--el-color-primary);
  color: white;
}

.status-canceled {
  background: var(--el-color-primary);
  color: white;
}

.status-unknown {
  background: var(--el-color-primary);
  color: white;
}

.task-workflow {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 4px;
}

.workflow-label {
  font-size: 11px;
  color: var(--el-text-color-secondary);
  font-weight: 500;
}

/* 工作流信息容器 */
.workflow-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 类型标签样式 */
.type-label {
  font-size: 10px;
  font-weight: 500;
  padding: 2px 6px;
  border-radius: 4px;
  white-space: nowrap;
  user-select: none;
  transition: all 0.2s ease;
  background: var(--el-color-primary);
  color: white;
  flex-shrink: 0;
}

.workflow-name {
  font-size: 13px;
  color: var(--el-text-color-primary);
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.task-progress {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 4px;
}

.progress-bar {
  flex: 1;
  height: 4px;
  background: var(--el-fill-color);
  border-radius: 2px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, var(--el-color-primary), var(--el-color-primary-light-3));
  border-radius: 2px;
  transition: width 0.3s ease;
  position: relative;
}

.progress-fill::after {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  animation: shimmer 2s infinite;
}

@keyframes shimmer {
  0% { left: -100%; }
  100% { left: 100%; }
}

.progress-text {
  font-size: 12px;
  font-weight: 500;
  color: var(--el-text-color-regular);
  min-width: 35px;
  text-align: right;
}

.task-time {
  font-size: 10px;
  color: var(--el-text-color-placeholder);
  font-weight: 400;
}

.task-loading-more {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 16px;
  color: var(--el-text-color-secondary);
  font-size: 13px;
}

.task-no-more {
  text-align: center;
  padding: 16px;
  color: var(--el-text-color-secondary);
  font-size: 12px;
}

/* 任务面板动画 */
.task-panel-enter-active,
.task-panel-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  transform-origin: top right;
}

.task-panel-enter-from {
  opacity: 0;
  transform: scale(0.9) translateY(-10px);
}

.task-panel-leave-to {
  opacity: 0;
  transform: scale(0.9) translateY(-10px);
}



/* 任务分割线 */
.task-divider {
  height: 1px;
  background: var(--el-border-color-light);
  margin: 12px 0 8px 0;
  opacity: 0.6;
}

/* 任务底部操作按钮区域 */
.task-actions-bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 8px;
}

/* 任务ID显示 */
.task-id-display {
  font-size: 10px;
  color: var(--el-text-color-placeholder);
  font-weight: 400;
  user-select: text;
}

/* 操作按钮组 */
.action-buttons-group {
  display: flex;
  gap: 6px;
}

.action-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border: none;
  border-radius: 4px;
  background: var(--el-fill-color-light);
  cursor: pointer;
  transition: all 0.2s ease;
  color: var(--el-text-color-regular);
}

.action-btn:hover {
  background: var(--el-fill-color);
  transform: scale(1.1);
}

.action-btn.is-loading,
.action-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.btn-spinner {
  width: 12px;
  height: 12px;
  border: 2px solid var(--el-border-color-lighter);
  border-top: 2px solid var(--el-color-primary);
  border-radius: 50%;
  animation: spin 0.9s linear infinite;
}

.action-btn svg {
  width: 12px;
  height: 12px;
  flex-shrink: 0;
}

/* 所有按钮统一样式 */
.view-btn,
.cancel-btn,
.remake-btn {
  color: var(--el-color-primary);
  background: var(--el-fill-color-lighter);
}

.view-btn:hover,
.cancel-btn:hover,
.remake-btn:hover {
  background: var(--el-color-primary);
  color: white;
}

/* 深色主题适配 - 任务面板 */
html[class^="theme-dark"] .task-panel,
html[class*=" theme-dark"] .task-panel {
  background: #1f1f1f;
  border-color: #3a3a3a;
  box-shadow: 0 12px 48px rgba(0, 0, 0, 0.6);
}

html[class^="theme-dark"] .task-panel-header,
html[class*=" theme-dark"] .task-panel-header {
  background: #2a2a2a;
  border-color: #404040;
}

/* 响应式适配 */
@media (max-width: 768px) {
  .navbar-text {
    display: none;
  }
  
  .task-panel {
    width: 320px;
    right: -20px;
  }
}

@media (max-width: 480px) {
  .task-panel {
    width: calc(100vw - 40px);
    right: -10px;
    max-height: 60vh;
  }
}
</style> 