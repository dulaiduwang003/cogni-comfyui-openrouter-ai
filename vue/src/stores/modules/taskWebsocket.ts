import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import { taskWebSocket, type WebSocketMessage } from '@/utils/taskWebsocketUtil'
import { comfyuiTaskApi } from '@/api/workflow-task/workflow-task'
import type { GetTaskProgressPageApi } from '@/api/workflow-task/types'
import { WebSocketMessageTypeEnum, WorkflowTaskStatusEnum } from '@/enums'
import { ElNotification } from 'element-plus'
import { useUserStore } from './user'
import { useI18n } from 'vue-i18n'

export const useTaskWebSocketStore = defineStore('taskWebsocket', () => {
  // 获取用户store实例
  const userStore = useUserStore()
  const { t } = useI18n()

  // WebSocket连接状态
  const isConnected = ref(false)
  const connectionState = ref<string>('CLOSED')
  const lastError = ref<string | null>(null)
  const taskMessages = ref<WebSocketMessage[]>([])

  // 任务列表状态
  const tasks = ref<GetTaskProgressPageApi.Result['items']>([])
  const tasksLoading = ref(false)
  const currentPage = ref(1)
  const totalTasks = ref(0)
  const hasMoreTasks = ref(true)
  const selectedStatus = ref<WorkflowTaskStatusEnum | ''>('')

  // 计算属性
  const getConnectionStatus = computed(() => ({
    isConnected: isConnected.value,
    state: connectionState.value,
    lastError: lastError.value
  }))

  const getTaskMessages = computed(() => taskMessages.value)

  // 计算运行中的任务数量（等待中 + 构建中）
  const getRunningTaskCount = computed(() => {
    return tasks.value.filter(task => 
      task.status === WorkflowTaskStatusEnum.WAIT || 
      task.status === WorkflowTaskStatusEnum.BUILD
    ).length
  })

  // 获取过滤后的任务列表
  const getFilteredTasks = computed(() => {
    if (!selectedStatus.value) {
      return tasks.value
    }
    return tasks.value.filter(task => task.status === selectedStatus.value)
  })

  // 获取任务列表状态
  const getTasksState = computed(() => ({
    tasks: getFilteredTasks.value,
    loading: tasksLoading.value,
    total: totalTasks.value,
    hasMore: hasMoreTasks.value,
    runningCount: getRunningTaskCount.value,
    selectedStatus: selectedStatus.value
  }))

  // 连接WebSocket
  async function connect(token: string) {
    if (!token) {
      console.warn('WebSocket连接失败: 缺少token')
      return false
    }

    // 检查是否已经连接
    if (isConnected.value) {
      console.log('WebSocket已经连接，跳过重复连接')
      return true
    }

    try {
      // 设置事件监听
      setupEventListeners()

      // 连接WebSocket（使用优化的配置）
      await taskWebSocket.connect({
        url: import.meta.env.VITE_API_BASE_URL + comfyuiTaskApi.getComfyuiTaskProgressWebsocketUrl(),
        token: token,
        maxRetries: 5,                    // 最多重连5次
        retryInterval: 3000,              // 基础重连间隔3秒
        heartbeatInterval: 30000,         // 心跳间隔30秒
        heartbeatTimeout: 10000,          // 心跳超时10秒
        retryStrategy: 'exponential',     // 使用指数退避策略
        maxRetryInterval: 60000           // 最大重连间隔60秒
      })

      console.log('WebSocket连接成功')
      return true
    } catch (error) {
      console.error('WebSocket连接失败:', error)
      lastError.value = error instanceof Error ? error.message : 'WebSocket连接失败'
      return false
    }
  }

  // 设置事件监听器
  function setupEventListeners() {
    // 连接成功
    taskWebSocket.on('connect', () => {
      isConnected.value = true
      connectionState.value = 'OPEN'
      lastError.value = null


      // 连接成功后立即加载任务列表
      console.log('WebSocket连接成功，开始加载任务列表')
      loadTasks(true).catch(error => {
        console.error('连接成功后加载任务列表失败:', error)
      })
    })

    // 连接断开
    taskWebSocket.on('disconnect', (reason: string) => {
      isConnected.value = false
      connectionState.value = 'CLOSED'
      lastError.value = reason
      
      // 只在真正的连接断开时显示通知，重连失败时不显示
      if (reason !== '主动断开' && reason !== '连接关闭' && reason === '重连失败') {
        console.log('WebSocket连接断开', reason)
      } else if (reason !== '主动断开' && reason !== '连接关闭' && reason !== '重连失败') {
        console.log('WebSocket连接断开', reason)
      }
    })

    // 连接错误
    taskWebSocket.on('error', (error: Event) => {
      console.error('WebSocket错误:', error)
      lastError.value = 'WebSocket连接错误'
      
    })

    // 接收消息
    taskWebSocket.on('message', (message: WebSocketMessage) => {
      handleMessage(message)
    })
  }

  // 处理接收到的消息
  function handleMessage(message: WebSocketMessage) {
    console.log('处理WebSocket消息:', message)
    
    // 添加到消息列表
    taskMessages.value.unshift({
      ...message,
      timestamp: message.timestamp || Date.now()
    })

    // 保留最新的100条消息
    if (taskMessages.value.length > 100) {
      taskMessages.value = taskMessages.value.slice(0, 100)
    }

    switch (message.type) {
      case WebSocketMessageTypeEnum.TASK_STATUS_CHANGE:
        handleTaskStatusChange(message)
        break
      case WebSocketMessageTypeEnum.TASK_PROGRESS_UPDATE:
        handleTaskProgressUpdate(message)
        break
      case WebSocketMessageTypeEnum.QUEUE_POSITION_UPDATE:
        handleQueuePositionUpdate(message)
        break
      case WebSocketMessageTypeEnum.CONNECTION_ACK:
        handleConnectionAck(message)
        break
      case WebSocketMessageTypeEnum.ERROR:
        handleError(message)
        break
      default:
        console.log('未知消息类型:', message)
    }
  }

  // 处理任务状态变化消息
  function handleTaskStatusChange(message: WebSocketMessage) {
    if (!message.parsedData) return
    
    const data = message.parsedData as any
    const { taskId, status, progress, workflowResultModel, workflowName } = data
    
    console.log(`任务状态变化 - 任务ID: ${taskId}, 状态: ${status}, 进度: ${progress}%`, { workflowName, workflowResultModel })
    
    // 更新任务状态到本地列表
    updateTaskFromWebSocket(message)
    
    // 获取任务名称
    const taskName = workflowName || `${t('task.taskPrefix')} ${taskId}`
    
    // 显示相应的通知
    switch (status) {
      case WorkflowTaskStatusEnum.SUCCEED:
        ElNotification.success({
          message: workflowResultModel 
            ? `${taskName} ${t('task.taskCompletedWithWork', { type: workflowResultModel.type.toLowerCase() })}`
            : `${taskName} ${t('task.taskCompleted')}`,
          duration: 5000
        })
        break
      case WorkflowTaskStatusEnum.FAILED:
        ElNotification.error({
          message: `${taskName} ${t('task.taskFailed')}`,
          duration: 5000
        })
        // 任务失败时刷新积分（可能有积分退回）
        userStore.refreshUserCredits()
        break
      case WorkflowTaskStatusEnum.CANCELED:
        // ElNotification.warning({
        //   message: `${taskName} ${t('task.taskCanceled')}`,
        //   duration: 3000
        // })
        // 任务取消时刷新积分（通常会退回积分）
        userStore.refreshUserCredits()
        break
      case WorkflowTaskStatusEnum.BUILD:
        // ElNotification.info({
        //   message: `${taskName} ${t('task.taskStartBuilding')}`,
        //   duration: 2000
        // })
        break
    }
  }

  // 处理任务进度更新消息
  function handleTaskProgressUpdate(message: WebSocketMessage) {
    if (!message.parsedData) return
    
    const data = message.parsedData as any
    const { taskId, progress } = data
    
    console.log(`任务进度更新 - 任务ID: ${taskId}, 进度: ${progress}%`)
    
    // 更新任务进度到本地列表
    updateTaskFromWebSocket(message)
    
    // 进度更新不显示通知，避免过多干扰
  }

  // 处理队列位置更新消息
  function handleQueuePositionUpdate(message: WebSocketMessage) {
    if (!message.parsedData) return
    
    const data = message.parsedData as any
    const { taskId, location } = data
    
    console.log(`队列位置更新 - 任务ID: ${taskId}, 位置: ${location}`)
    
    // 更新任务位置到本地列表
    updateTaskFromWebSocket(message)
    
    // 队列位置更新通常不需要显示通知
  }

  // 处理连接确认消息
  function handleConnectionAck(message: WebSocketMessage) {
    console.log('收到连接确认消息:', message.parsedData)
  }

  // 处理错误消息
  function handleError(message: WebSocketMessage) {
    if (!message.parsedData) return
    
    const data = message.parsedData as any
    const { message: errorMsg, code } = data
    
    console.error('收到错误消息:', errorMsg, code)
    
    ElNotification.error({
      title: t('task.serverError'),
      message: t('task.errorMessage', { message: `${errorMsg}${code ? ` (${code})` : ''}` }),
      duration: 5000
    })
  }

  // 发送消息
  function send(message: any): boolean {
    return taskWebSocket.send(message)
  }

  // 断开连接
  function disconnect() {
    taskWebSocket.disconnect()
    stopStatusUpdate()
    isConnected.value = false
    connectionState.value = 'CLOSED'
    lastError.value = null
    taskMessages.value = []
  }

  // 清除消息历史
  function clearMessages() {
    taskMessages.value = []
  }

  // 获取指定任务的最新状态
  function getTaskStatus(taskId: string): WebSocketMessage | null {
    // 从最新的消息开始查找
    for (let i = taskMessages.value.length - 1; i >= 0; i--) {
      const message = taskMessages.value[i]
      if (message.parsedData && typeof message.parsedData === 'object') {
        const data = message.parsedData as any
        if (data.taskId === taskId) {
          return message
        }
      }
    }
    return null
  }

  // ===== 任务列表管理方法 =====

  // 加载任务列表
  async function loadTasks(reset = false) {
    if (tasksLoading.value) return

    try {
      tasksLoading.value = true
      const page = reset ? 1 : currentPage.value

      // 构建请求参数
      const params: { page: number; status?: WorkflowTaskStatusEnum } = { page }
      if (selectedStatus.value) {
        params.status = selectedStatus.value
      }

      const response = await comfyuiTaskApi.reqGetComfyuiTaskProgressPage(params)

      if (reset) {
        tasks.value = response.items
        currentPage.value = 1
      } else {
        tasks.value.push(...response.items)
      }

      totalTasks.value = response.total
      hasMoreTasks.value = tasks.value.length < totalTasks.value

      if (!reset) {
        currentPage.value++
      }

      console.log(`加载任务列表成功: ${response.items.length}个任务`)
    } catch (error) {
      console.error('加载任务列表失败:', error)

    } finally {
      tasksLoading.value = false
    }
  }

  // 刷新任务列表
  async function refreshTasks() {
    currentPage.value = 1
    await loadTasks(true)
  }

  // 加载更多任务
  async function loadMoreTasks() {
    if (!hasMoreTasks.value || tasksLoading.value) return
    await loadTasks(false)
  }

  // 设置状态过滤
  function setStatusFilter(status: WorkflowTaskStatusEnum | '') {
    selectedStatus.value = status
    refreshTasks()
  }

  // 清空任务列表
  function clearTasks() {
    tasks.value = []
    currentPage.value = 1
    totalTasks.value = 0
    hasMoreTasks.value = true
  }

  // 根据WebSocket消息更新任务状态
  function updateTaskFromWebSocket(message: WebSocketMessage) {
    if (!message.parsedData) return

    const data = message.parsedData as any
    const { taskId, status, progress, workflowName, workflowResultModel, position, location } = data

    if (!taskId) return

    // 查找现有任务
    const existingTaskIndex = tasks.value.findIndex(task => task.taskId === taskId)

    if (existingTaskIndex >= 0) {
      // 更新现有任务
      const existingTask = tasks.value[existingTaskIndex]
      const newLocation = location !== undefined ? location : (position !== undefined ? position : existingTask.location)
      tasks.value[existingTaskIndex] = {
        ...existingTask,
        status: status || existingTask.status,
        progress: progress !== undefined ? progress : existingTask.progress,
        workflowName: workflowName || existingTask.workflowName,
        workflowResultModel: workflowResultModel || existingTask.workflowResultModel,
        location: newLocation
      }
      console.log(`更新任务状态: ${taskId} -> ${status}${progress !== undefined ? ` (${progress}%)` : ''}${newLocation !== undefined ? ` 队列位置: ${newLocation}` : ''}${workflowName ? ` [${workflowName}]` : ''}${workflowResultModel ? ` 作品:${workflowResultModel.type}` : ''}`)
    } else {
      // 如果任务不存在，可能是新提交的任务，主动刷新列表
      console.log(`发现新任务: ${taskId}，刷新任务列表`)
      refreshTasks()
    }
  }

  // 更新连接状态
  function updateConnectionState(state: string) {
    connectionState.value = state
    isConnected.value = state === 'OPEN'
  }

  // 触发任务面板打开（用于任务提交成功后）
  function triggerTaskPanelOpen() {
    // 使用自定义事件通知TaskPanel打开
    const event = new CustomEvent('openTaskPanel')
    window.dispatchEvent(event)
    console.log('触发任务面板打开事件')
  }

  // 定期更新连接状态
  let statusUpdateTimer: number | null = null
  
  // 启动状态更新定时器
  function startStatusUpdate() {
    if (statusUpdateTimer) return
    statusUpdateTimer = window.setInterval(updateConnectionState, 1000)
  }
  
  // 停止状态更新定时器
  function stopStatusUpdate() {
    if (statusUpdateTimer) {
      clearInterval(statusUpdateTimer)
      statusUpdateTimer = null
    }
  }
  
  // 启动定时器
  startStatusUpdate()

  return {
    // 状态
    isConnected,
    connectionState,
    lastError,
    taskMessages,
    
    // 任务列表状态
    tasks,
    tasksLoading,
    currentPage,
    totalTasks,
    hasMoreTasks,
    selectedStatus,
    
    // 计算属性
    getConnectionStatus,
    getTaskMessages,
    getRunningTaskCount,
    getFilteredTasks,
    getTasksState,
    
    // 方法
    connect,
    disconnect,
    send,
    clearMessages,
    getTaskStatus,
    updateConnectionState,
    startStatusUpdate,
    stopStatusUpdate,
    loadTasks,
    refreshTasks,
    loadMoreTasks,
    setStatusFilter,
    clearTasks,
    updateTaskFromWebSocket,
    triggerTaskPanelOpen
  }
}) 
