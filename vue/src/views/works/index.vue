<template>
  <div class="works-page page-container">
    <!-- Banner 组件 -->
    <div class="banner-container">
      <WorksBanner class="header-enter" />
    </div>
    
    <!-- 批量操作工具栏 -->
    <div class="batch-toolbar" v-if="!selectionMode && works.length > 0">
      <el-button 
        type="primary" 
        :icon="Select" 
        @click="enterSelectionMode"
      >
        {{ t('works.batchSelect') }}
      </el-button>
    </div>

    <!-- 选择模式工具栏 -->
    <div class="selection-toolbar" v-if="selectionMode">
      <div class="selection-info">
        <el-checkbox 
          :model-value="isAllSelected"
          :indeterminate="isIndeterminate"
          @change="toggleSelectAll"
        >
          {{ t('works.selectAll') }}
        </el-checkbox>
        <span class="selected-count">{{ t('works.selectedCount', { count: selectedWorks.length }) }}</span>
      </div>
      
      <div class="selection-actions">
        <el-button 
          type="danger" 
          :icon="Delete" 
          :disabled="selectedWorks.length === 0"
          :loading="batchDeleting"
          @click="handleBatchDeleteConfirm"
        >
          {{ t('works.batchDelete') }}
        </el-button>
        <el-button 
          @click="exitSelectionMode"
        >
          {{ t('works.cancel') }}
        </el-button>
      </div>
    </div>
    
    <!-- 作品网格 -->
    <div class="works-container">
      <div class="works-content content-enter">
      <div class="works-grid" v-if="works.length > 0">
        <WorkCard
          class="grid-item-enter"
          v-for="work in works" 
          :key="work.workflowResultId"
          :work="work"
          :selection-mode="selectionMode"
          :is-selected="isWorkSelected(work.workflowResultId)"
          @click="handleWorkClick"
          @select="handleWorkSelect"
          @imageError="handleImageError"
        />
      </div>
      
      <!-- 空状态 -->
      <EmptyState 
        class="empty-state-enter"
        v-else-if="!pagination.loading" 
        @goToCreate="goToCreate"
      />
      
      <!-- 加载更多指示器 -->
      <div ref="sentinel" class="scroll-sentinel"></div>
      
      <!-- 加载状态 -->
      <LoadingState v-if="pagination.loading" />
      </div>
      
      <!-- 没有更多数据 - 置底显示 -->
      <div v-if="!pagination.hasMore && works.length > 0" class="no-more">
        <el-divider>
          <span class="no-more-text">{{ t('works.noMore') }}</span>
        </el-divider>
      </div>
    </div>

    <!-- 作品详情对话框 -->
    <WorkDetailDialog
      ref="detailDialogRef"
      v-model:visible="detailDialog.visible"
      @workDeleted="handleWorkDeleted"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, watch, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElNotification, ElMessageBox } from 'element-plus'
import { Select, Delete } from '@element-plus/icons-vue'
import { WorkflowResultModelApi } from '@/api/workflow-result/workflow-result'
import { useTaskWebSocketStore } from '@/stores/modules/taskWebsocket'
import { WorkflowTaskStatusEnum } from '@/enums'

// 导入组件
import WorkCard from './components/WorkCard.vue'
import WorkDetailDialog from '@/components/common/WorkDetailDialog.vue'
import EmptyState from './components/EmptyState.vue'
import LoadingState from './components/LoadingState.vue'
import WorksBanner from './components/WorksBanner.vue'

const router = useRouter()
const { t } = useI18n()

// 使用WebSocket Store
const webSocketStore = useTaskWebSocketStore()

// 响应式数据
const works = ref([])
const sentinel = ref(null)
const detailDialogRef = ref(null)

// 对话框状态
const detailDialog = reactive({
  visible: false
})

// 分页参数
const pagination = reactive({
  page: 1,
  size: 20,
  hasMore: true,
  loading: false,
  total: 0
})

// 批量选择相关
const selectionMode = ref(false)
const selectedWorks = ref([])
const batchDeleting = ref(false)

// 计算属性
const isAllSelected = computed(() => {
  return works.value.length > 0 && selectedWorks.value.length === works.value.length
})

const isIndeterminate = computed(() => {
  const count = selectedWorks.value.length
  return count > 0 && count < works.value.length
})

// 获取作品列表
const fetchWorks = async (isNewSearch = false) => {
  if (pagination.loading) return
  
  if (isNewSearch) {
    pagination.page = 1
    pagination.hasMore = true
    works.value = []
  }
  
  if (!pagination.hasMore) return

  pagination.loading = true
  
  try {
    const response = await WorkflowResultModelApi.reqGetWorkflowResultPage({ page: pagination.page })
    const { items = [], total = 0 } = response || {}
    
    // 直接使用接口返回的数据结构
    const transformedItems = items.map(item => ({
      workflowResultId: item.workflowResultId,
      type: item.type,
      url: item.url,
      workflowName: item.workflowName,
      taskId: item.taskId,
      createTime: item.createTime,
      imageError: false // 添加图片错误状态
    }))
    
    if (isNewSearch) {
      works.value = transformedItems
    } else {
      works.value.push(...transformedItems)
    }
    
    pagination.total = total
    pagination.hasMore = works.value.length < total
    
    if (pagination.hasMore) {
      pagination.page++
    }
  } catch (error) {
    console.error(t('works.fetchError'), error)

  } finally {
    pagination.loading = false
  }
}

// 监听WebSocket任务完成消息，自动刷新作品列表
const handleTaskCompletion = () => {
  // 监听WebSocket store中的任务状态变化
  watch(
    () => webSocketStore.getTaskMessages,
    (messages) => {
      // 检查最新消息是否是任务完成
      if (messages.length > 0) {
        const latestMessage = messages[messages.length - 1]
        if (latestMessage.parsedData) {
          const data = latestMessage.parsedData
          const { status, workflowResultModel } = data
          
          // 当任务成功完成且有作品生成时，刷新作品列表
          if (status === WorkflowTaskStatusEnum.SUCCEED && workflowResultModel) {
            console.log(t('works.taskCompleted'))
            // 延迟刷新作品列表
            setTimeout(() => {
              fetchWorks(true)
            }, 1000)
          }
        }
      }
    },
    { deep: true }
  )
}

// 图片加载错误处理
const handleImageError = (work) => {
  // 设置作品的图片错误状态
  work.imageError = true
}

// 作品点击处理
const handleWorkClick = async (work) => {
  detailDialog.visible = true
  await detailDialogRef.value?.fetchWorkDetail(work.workflowResultId)
}

// 作品删除处理
const handleWorkDeleted = (deletedId) => {
  // 从本地列表中移除已删除的作品
  works.value = works.value.filter(work => work.workflowResultId !== deletedId)
  
  // 更新总数
  pagination.total = Math.max(0, pagination.total - 1)
}

// 跳转到创建页面
const goToCreate = () => {
  router.push('/comfyui')
}

// 进入选择模式
const enterSelectionMode = () => {
  selectionMode.value = true
  selectedWorks.value = []
}

// 退出选择模式
const exitSelectionMode = () => {
  selectionMode.value = false
  selectedWorks.value = []
}

// 判断作品是否被选中
const isWorkSelected = (workflowResultId) => {
  return selectedWorks.value.includes(workflowResultId)
}

// 处理作品选择
const handleWorkSelect = (work) => {
  const index = selectedWorks.value.indexOf(work.workflowResultId)
  if (index > -1) {
    selectedWorks.value.splice(index, 1)
  } else {
    selectedWorks.value.push(work.workflowResultId)
  }
}

// 全选/取消全选
const toggleSelectAll = () => {
  if (isAllSelected.value) {
    selectedWorks.value = []
  } else {
    selectedWorks.value = works.value.map(work => work.workflowResultId)
  }
}

// 批量删除确认
const handleBatchDeleteConfirm = async () => {
  if (selectedWorks.value.length === 0) {
    ElNotification.warning({
      message: t('works.noWorksSelected')
    })
    return
  }

  try {
    await ElMessageBox.confirm(
      t('works.batchDeleteConfirm', { count: selectedWorks.value.length }),
      t('works.batchDeleteTitle'),
      {
        confirmButtonText: t('works.confirmDelete'),
        cancelButtonText: t('works.cancel'),
        type: 'warning',
        confirmButtonClass: 'el-button--danger'
      }
    )

    await handleBatchDelete()
  } catch (error) {
    console.log('用户取消批量删除')
  }
}

// 批量删除作品
const handleBatchDelete = async () => {
  batchDeleting.value = true
  
  try {
    await WorkflowResultModelApi.reqBatchDeleteWorkflowResult({ 
      workflowResultIds: selectedWorks.value 
    })
    
    ElNotification.success({
      message: t('works.batchDeleteSuccess', { count: selectedWorks.value.length })
    })
    
    // 从本地列表中移除已删除的作品
    works.value = works.value.filter(work => !selectedWorks.value.includes(work.workflowResultId))
    
    // 更新总数
    pagination.total = Math.max(0, pagination.total - selectedWorks.value.length)
    
    // 退出选择模式
    exitSelectionMode()
    
  } catch (error) {
    console.error('批量删除作品失败:', error)
    ElNotification.error({
      message: t('works.batchDeleteFailed')
    })
  } finally {
    batchDeleting.value = false
  }
}

// 设置无限滚动观察器
let observer = null

const setupIntersectionObserver = () => {
  if (!sentinel.value) return
  
  observer = new IntersectionObserver(
    (entries) => {
      const entry = entries[0]
      if (entry.isIntersecting && pagination.hasMore && !pagination.loading) {
        fetchWorks()
      }
    },
    { threshold: 0.1 }
  )
  
  observer.observe(sentinel.value)
}

// 生命周期
onMounted(() => {
  fetchWorks(true)
  setupIntersectionObserver()
  // 设置WebSocket任务完成监听
  handleTaskCompletion()
})

onUnmounted(() => {
  if (observer) {
    observer.disconnect()
  }
})
</script>

<style scoped>
.works-page {
  min-height: 100vh;
  background: var(--el-bg-color-page);
  width: 100%;
}

.banner-container {
  padding: 10px 10px 0 10px;
  width: 100%;
}

.batch-toolbar {
  padding: 10px;
  display: flex;
  justify-content: flex-end;
}

.selection-toolbar {
  padding: 12px 16px;
  background: var(--el-bg-color);
  border-radius: 8px;
  margin: 0 10px 10px 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border: 1px solid var(--el-border-color-lighter);
  transition: all 0.3s ease;
}

/* 暗色主题适配 */
html.dark .selection-toolbar {
  background: var(--el-bg-color);
  border-color: var(--el-border-color);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

.selection-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

/* 全选复选框样式 - 参考 CheckboxSelector */
.selection-info :deep(.el-checkbox__inner) {
  background-color: var(--el-bg-color);
  border-color: var(--el-border-color);
  transition: all 0.3s ease;
}

.selection-info :deep(.el-checkbox__input:hover .el-checkbox__inner) {
  border-color: var(--el-color-primary);
}

.selection-info :deep(.el-checkbox__input.is-checked .el-checkbox__inner),
.selection-info :deep(.el-checkbox__input.is-indeterminate .el-checkbox__inner) {
  background-color: var(--el-color-primary);
  border-color: var(--el-color-primary);
}

.selection-info :deep(.el-checkbox__input.is-checked .el-checkbox__inner::after) {
  border-color: #fff;
}

/* 暗色模式适配 */
@media (prefers-color-scheme: dark) {
  .selection-info :deep(.el-checkbox__inner) {
    background-color: var(--el-fill-color-dark);
    border-color: var(--el-border-color-dark);
  }
}

.dark .selection-info :deep(.el-checkbox__inner) {
  background-color: var(--el-fill-color-dark);
  border-color: var(--el-border-color-dark);
}

.dark .selection-info :deep(.el-checkbox__input.is-checked .el-checkbox__inner),
.dark .selection-info :deep(.el-checkbox__input.is-indeterminate .el-checkbox__inner) {
  background-color: var(--el-color-primary);
  border-color: var(--el-color-primary);
}

.selected-count {
  font-size: 14px;
  color: var(--el-text-color-secondary);
  font-weight: 500;
}

.selection-actions {
  display: flex;
  gap: 8px;
}

.works-container {
  padding: 10px;
  width: 100%;
  min-height: calc(100vh - 120px);
  display: flex;
  flex-direction: column;
}

.works-content {
  flex: 1;
}

.works-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 10px;
  width: 100%;
}

.scroll-sentinel {
  height: 20px;
}

.no-more {
  padding: 40px 0;
  text-align: center;
  margin-top: auto;
}

.no-more-text {
  font-size: 14px;
  color: var(--el-text-color-placeholder);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .banner-container {
    padding: 10px 10px 0 10px;
  }
  
  .works-container {
    padding: 10px;
  }
  
  .works-grid {
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: 8px;
  }

  .selection-toolbar {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }

  .selection-info {
    justify-content: space-between;
  }

  .selection-actions {
    width: 100%;
  }

  .selection-actions .el-button {
    flex: 1;
  }
}

@media (max-width: 480px) {
  .works-grid {
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 8px;
  }
}

/* 针对非常小的屏幕 - 单列布局 */
@media (max-width: 320px) {
  .works-grid {
    grid-template-columns: 1fr;
    gap: 8px;
  }
}
</style> 