import type { WebSocketMessageApi } from '@/api/workflow-task/types'

// 使用新的类型定义
export type WebSocketMessage = WebSocketMessageApi.Message
export type MessageData = WebSocketMessageApi.MessageData

// 重连策略类型
export type RetryStrategy = 'fixed' | 'exponential' | 'linear'

export interface WebSocketConfig {
  url: string
  token: string
  maxRetries?: number
  retryInterval?: number
  heartbeatInterval?: number
  heartbeatTimeout?: number
  retryStrategy?: RetryStrategy
  maxRetryInterval?: number
}

export class TaskWebSocketService {
  private static instance: TaskWebSocketService | null = null
  private ws: WebSocket | null = null
  private config: WebSocketConfig | null = null
  private retryCount = 0
  private isConnecting = false
  private isDestroyed = false
  private isReconnecting = false
  private heartbeatTimer: number | null = null
  private reconnectTimer: number | null = null
  private heartbeatTimeoutTimer: number | null = null
  private lastHeartbeatTime: number = 0
  private networkOnlineHandler: (() => void) | null = null
  private visibilityChangeHandler: (() => void) | null = null
  
  // 事件监听器
  private listeners: {
    onMessage?: (message: WebSocketMessage) => void
    onConnect?: () => void
    onDisconnect?: (reason: string) => void
    onError?: (error: Event) => void
  } = {}

  private constructor() {
    this._setupNetworkListeners()
  }

  // 单例模式
  static getInstance(): TaskWebSocketService {
    if (!TaskWebSocketService.instance) {
      TaskWebSocketService.instance = new TaskWebSocketService()
    }
    return TaskWebSocketService.instance
  }

  // 连接WebSocket
  async connect(config: WebSocketConfig): Promise<void> {
    if (this.isDestroyed) {
      console.warn('WebSocket服务已销毁，无法连接')
      return
    }

    if (this.isConnecting) {
      console.log('WebSocket正在连接中...')
      return
    }

    if (this.ws?.readyState === WebSocket.OPEN) {
      console.log('WebSocket已连接')
      return
    }

    this.config = {
      maxRetries: 5,
      retryInterval: 3000,
      heartbeatInterval: 30000,
      heartbeatTimeout: 10000,
      retryStrategy: 'exponential',
      maxRetryInterval: 60000,
      ...config
    }

    this.isConnecting = true

    try {
      await this._connect()
    } catch (error) {
      console.error('WebSocket连接失败:', error)
      this.isConnecting = false
      throw error
    }
  }

  private async _connect(): Promise<void> {
    return new Promise((resolve, reject) => {
      if (!this.config) {
        reject(new Error('WebSocket配置未设置'))
        return
      }

      const wsUrl = `${this.config.url}?token=${this.config.token}`
      console.log(`尝试连接WebSocket: ${wsUrl}`)

      this.ws = new WebSocket(wsUrl)

      // 连接成功
      this.ws.onopen = () => {
        console.log('WebSocket连接成功')
        this.isConnecting = false
        this.retryCount = 0
        this._startHeartbeat()
        this.listeners.onConnect?.()
        resolve()
      }

      // 接收消息
      this.ws.onmessage = (event) => {
        try {
          // 检查是否为空或无效数据
          if (!event.data || event.data === 'undefined' || event.data === 'null') {
            console.warn('收到空的WebSocket消息，跳过处理:', event.data)
            return
          }

          const rawMessage = JSON.parse(event.data)
          console.log('收到WebSocket原始消息:', rawMessage)
          
          // 收到消息，更新心跳时间
          this.lastHeartbeatTime = Date.now()
          this._resetHeartbeatTimeout()
          
          // 验证消息必要字段
          if (!rawMessage || typeof rawMessage !== 'object') {
            console.warn('WebSocket消息格式无效，必须是对象:', rawMessage)
            return
          }

          if (!rawMessage.type) {
            console.warn('WebSocket消息缺少type字段:', rawMessage)
            return
          }
          
          // 检查消息格式，支持两种格式：
          // 1. 标准格式：{type, timestamp, data} - data是JSON字符串
          // 2. 直接格式：{type, timestamp, message} - 直接包含消息内容
          let parsedData: MessageData | undefined
          let formattedMessage: WebSocketMessage
          
          if (rawMessage.data !== undefined && rawMessage.data !== null) {
            // 标准格式：需要解析data字段
            try {
              if (typeof rawMessage.data === 'string') {
                // 检查是否为空字符串或特殊值
                if (rawMessage.data === '' || rawMessage.data === 'undefined' || rawMessage.data === 'null') {
                  console.warn('data字段为空值，跳过解析:', rawMessage.data)
                  parsedData = undefined
                } else {
                  parsedData = JSON.parse(rawMessage.data)
                }
              } else {
                parsedData = rawMessage.data
              }
            } catch (parseError) {
              console.warn('解析消息data字段失败，使用原始值:', parseError, rawMessage.data)
              // 如果data解析失败，将原始data作为parsedData
              parsedData = rawMessage.data
            }
            
            formattedMessage = {
              type: rawMessage.type,
              timestamp: rawMessage.timestamp || Date.now(),
              data: rawMessage.data,
              parsedData
            } as WebSocketMessage
          } else {
            // 直接格式：消息内容直接在根级别
            // 这种情况下，整个rawMessage就是parsedData
            parsedData = rawMessage
            
            formattedMessage = {
              type: rawMessage.type,
              timestamp: rawMessage.timestamp || Date.now(),
              data: JSON.stringify(rawMessage), // 将整个消息序列化为data
              parsedData
            } as WebSocketMessage
          }
          
          console.log('处理后的WebSocket消息:', formattedMessage)
          this.listeners.onMessage?.(formattedMessage)
        } catch (error) {
          console.error('解析WebSocket消息失败:', error, '原始数据:', event.data)
          // 可以选择性地触发错误事件
          this.listeners.onError?.(new ErrorEvent('message_parse_error', { 
            message: `消息解析失败: ${error}` 
          }))
        }
      }

      // 连接关闭
      this.ws.onclose = (event) => {
        console.log('WebSocket连接关闭:', event.code, event.reason)
        this.isConnecting = false
        this._stopHeartbeat()
        
        if (!this.isDestroyed && event.code !== 1000) {
          // 非正常关闭，尝试重连
          this.isReconnecting = true
          this._attemptReconnect('连接意外关闭')
        } else {
          // 正常关闭或已销毁，通知disconnect
          this.listeners.onDisconnect?.(event.reason || '连接关闭')
        }
      }

      // 连接错误
      this.ws.onerror = (error) => {
        console.error('WebSocket连接错误:', error)
        this.isConnecting = false
        this.listeners.onError?.(error)
        reject(error)
      }
    })
  }

  // 计算重连延迟（支持多种策略）
  private _calculateRetryDelay(): number {
    if (!this.config) return 5000

    const baseDelay = this.config.retryInterval || 3000
    const maxDelay = this.config.maxRetryInterval || 60000
    const strategy = this.config.retryStrategy || 'exponential'

    let delay = baseDelay

    switch (strategy) {
      case 'fixed':
        // 固定间隔
        delay = baseDelay
        break
      
      case 'linear':
        // 线性增长: baseDelay * retryCount
        delay = baseDelay * this.retryCount
        break
      
      case 'exponential':
        // 指数退避: baseDelay * 2^(retryCount - 1)
        delay = baseDelay * Math.pow(2, this.retryCount - 1)
        break
    }

    // 限制最大延迟时间
    return Math.min(delay, maxDelay)
  }

  // 重连机制（优化版）
  private _attemptReconnect(reason: string): void {
    if (this.isDestroyed || !this.config) {
      this.isReconnecting = false
      return
    }

    if (this.retryCount >= (this.config.maxRetries || 5)) {
      console.error('WebSocket重连次数已达上限，停止重连')
      this.isReconnecting = false
      this.listeners.onDisconnect?.('重连失败')
      return
    }

    this.retryCount++
    const delay = this._calculateRetryDelay()

    console.log(`WebSocket将在${delay}ms后进行第${this.retryCount}/${this.config.maxRetries}次重连 (原因: ${reason})`)

    this.reconnectTimer = window.setTimeout(async () => {
      if (this.isDestroyed) {
        this.isReconnecting = false
        return
      }

      try {
        console.log(`开始第${this.retryCount}次重连...`)
        await this._connect()
        // 重连成功
        console.log('重连成功!')
        this.isReconnecting = false
      } catch (error) {
        console.warn(`第${this.retryCount}次重连失败:`, error)
        
        // 检查是否已达到最大重连次数
        if (this.retryCount >= (this.config!.maxRetries || 5)) {
          console.error('所有重连尝试都已失败')
          this.isReconnecting = false
          this.listeners.onDisconnect?.('重连失败')
        } else {
          // 继续尝试重连
          this._attemptReconnect('重连失败')
        }
      }
    }, delay)
  }

  // 心跳机制（优化版，带超时检测）
  private _startHeartbeat(): void {
    if (!this.config?.heartbeatInterval) return

    this._stopHeartbeat()
    this.lastHeartbeatTime = Date.now()
    
    this.heartbeatTimer = window.setInterval(() => {
      if (this.ws?.readyState === WebSocket.OPEN) {
        this.ws.send(JSON.stringify({ type: 'ping' }))
        this._resetHeartbeatTimeout()
      }
    }, this.config.heartbeatInterval)
    
    // 启动心跳超时检测
    this._resetHeartbeatTimeout()
  }

  private _stopHeartbeat(): void {
    if (this.heartbeatTimer) {
      clearInterval(this.heartbeatTimer)
      this.heartbeatTimer = null
    }
    this._clearHeartbeatTimeout()
  }

  // 重置心跳超时定时器
  private _resetHeartbeatTimeout(): void {
    this._clearHeartbeatTimeout()
    
    if (!this.config?.heartbeatTimeout) return
    
    this.heartbeatTimeoutTimer = window.setTimeout(() => {
      const timeSinceLastHeartbeat = Date.now() - this.lastHeartbeatTime
      
      if (timeSinceLastHeartbeat > (this.config!.heartbeatTimeout || 10000)) {
        console.warn('心跳超时，连接可能已断开，尝试重连...')
        
        // 关闭当前连接并重连
        if (this.ws) {
          this.ws.close()
        }
      }
    }, this.config.heartbeatTimeout)
  }

  // 清除心跳超时定时器
  private _clearHeartbeatTimeout(): void {
    if (this.heartbeatTimeoutTimer) {
      clearTimeout(this.heartbeatTimeoutTimer)
      this.heartbeatTimeoutTimer = null
    }
  }

  // 发送消息
  send(message: any): boolean {
    if (this.ws?.readyState === WebSocket.OPEN) {
      this.ws.send(JSON.stringify(message))
      return true
    }
    console.warn('WebSocket未连接，无法发送消息')
    return false
  }

  // 设置事件监听器
  on(event: 'message', callback: (message: WebSocketMessage) => void): void
  on(event: 'connect', callback: () => void): void
  on(event: 'disconnect', callback: (reason: string) => void): void
  on(event: 'error', callback: (error: Event) => void): void
  on(event: string, callback: any): void {
    switch (event) {
      case 'message':
        this.listeners.onMessage = callback
        break
      case 'connect':
        this.listeners.onConnect = callback
        break
      case 'disconnect':
        this.listeners.onDisconnect = callback
        break
      case 'error':
        this.listeners.onError = callback
        break
    }
  }

  // 移除事件监听器
  off(event: string): void {
    switch (event) {
      case 'message':
        this.listeners.onMessage = undefined
        break
      case 'connect':
        this.listeners.onConnect = undefined
        break
      case 'disconnect':
        this.listeners.onDisconnect = undefined
        break
      case 'error':
        this.listeners.onError = undefined
        break
    }
  }

  // 设置网络状态监听和页面可见性监听
  private _setupNetworkListeners(): void {
    // 网络恢复时自动重连
    this.networkOnlineHandler = () => {
      console.log('检测到网络恢复，尝试重新连接WebSocket...')
      
      if (!this.isConnected && !this.isConnecting && !this.isDestroyed && this.config) {
        // 重置重连计数，网络恢复时给更多机会
        this.retryCount = 0
        this._attemptReconnect('网络恢复')
      }
    }
    
    // 页面可见性变化时检查连接
    this.visibilityChangeHandler = () => {
      if (document.visibilityState === 'visible') {
        console.log('页面切换到前台，检查WebSocket连接状态...')
        
        // 页面回到前台时，检查连接状态
        if (!this.isConnected && !this.isConnecting && !this.isDestroyed && this.config) {
          console.log('WebSocket未连接，尝试重新连接...')
          this.retryCount = 0
          this._attemptReconnect('页面切回前台')
        } else if (this.isConnected) {
          // 如果已连接，发送一个心跳确认连接正常
          this.send({ type: 'ping' })
        }
      }
    }
    
    window.addEventListener('online', this.networkOnlineHandler)
    document.addEventListener('visibilitychange', this.visibilityChangeHandler)
  }

  // 清理网络监听器
  private _removeNetworkListeners(): void {
    if (this.networkOnlineHandler) {
      window.removeEventListener('online', this.networkOnlineHandler)
      this.networkOnlineHandler = null
    }
    
    if (this.visibilityChangeHandler) {
      document.removeEventListener('visibilitychange', this.visibilityChangeHandler)
      this.visibilityChangeHandler = null
    }
  }

  // 断开连接
  disconnect(): void {
    this.isDestroyed = true
    this.isReconnecting = false
    
    // 清理定时器
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer)
      this.reconnectTimer = null
    }
    this._stopHeartbeat()
    this._removeNetworkListeners()

    // 关闭WebSocket连接
    if (this.ws) {
      this.ws.close(1000, '主动断开')
      this.ws = null
    }

    // 清理监听器
    this.listeners = {}
    this.retryCount = 0
    this.isConnecting = false
  }

  // 获取连接状态
  get isConnected(): boolean {
    return this.ws?.readyState === WebSocket.OPEN
  }

  get connectionState(): string {
    if (!this.ws) return 'CLOSED'
    
    switch (this.ws.readyState) {
      case WebSocket.CONNECTING: return 'CONNECTING'
      case WebSocket.OPEN: return 'OPEN'
      case WebSocket.CLOSING: return 'CLOSING'
      case WebSocket.CLOSED: return 'CLOSED'
      default: return 'UNKNOWN'
    }
  }

  // 重置实例（用于测试或特殊情况）
  static reset(): void {
    if (TaskWebSocketService.instance) {
      TaskWebSocketService.instance.disconnect()
      TaskWebSocketService.instance = null
    }
  }

  // 获取重连状态
  get reconnecting(): boolean {
    return this.isReconnecting
  }

  // 获取重连信息
  get reconnectInfo(): { retryCount: number; maxRetries: number; isReconnecting: boolean } {
    return {
      retryCount: this.retryCount,
      maxRetries: this.config?.maxRetries || 5,
      isReconnecting: this.isReconnecting
    }
  }

  // 手动触发重连（重置计数）
  async manualReconnect(): Promise<boolean> {
    if (this.isDestroyed) {
      console.warn('WebSocket服务已销毁，无法重连')
      return false
    }

    console.log('手动触发重连...')
    
    // 关闭当前连接
    if (this.ws) {
      this.ws.close()
    }
    
    // 重置状态
    this.retryCount = 0
    this.isReconnecting = false
    this.isConnecting = false
    
    // 尝试重连
    if (this.config) {
      try {
        await this._connect()
        return true
      } catch (error) {
        console.error('手动重连失败:', error)
        return false
      }
    }
    
    return false
  }
}

// 导出单例实例
export const taskWebSocket = TaskWebSocketService.getInstance() 