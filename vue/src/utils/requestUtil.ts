import axios from 'axios'
import type { AxiosRequestConfig, AxiosResponse } from 'axios'
import { ElNotification } from 'element-plus'
import router from '../router'
import { HttpStatus } from '../constants/http-status/httpStatus'
import { isSuccessResponse } from '../constants/http-status/helps'
import i18n from '@/i18n'

// 响应结果接口
interface HttpResponse<T = any> {
  data: T
  msg: string
  code: number | string
}

// 创建axios实例
const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL, // 从环境变量获取API基础URL
  timeout: 15000, 
})

// 封装网络错误通知函数
const showNetworkErrorNotification = (message: string) => {
  ElNotification.error({
    message,
    duration: 5000,
  })
}

// 添加网络状态检测
const checkNetworkStatus = () => {
  const { t } = i18n.global
  if (!navigator.onLine) {
    showNetworkErrorNotification(t('utils.request.networkDisconnected'))
    return false
  }
  return true
}

// 创建一个自定义错误类型，用于标记网络错误已被处理
class NetworkError extends Error {
  handled: boolean = true;
  
  constructor(message: string) {
    super(message);
    this.name = 'NetworkError';
  }
}

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    const { t } = i18n.global
    // 检查网络连接状态
    if (!checkNetworkStatus()) {
      // 如果网络断开，创建一个被拒绝的Promise，使用自定义NetworkError
      return Promise.reject(new NetworkError(t('utils.request.networkDisconnected')))
    }
    
    // 可以在这里设置token
    const token = localStorage.getItem('token')
    console.log(`请求拦截器 - 路径: ${config.url}, token: ${token ? '已获取' : '未获取'}`)
    
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
      console.log('添加Authorization头:', `Bearer ${token}`)
    } else {
      console.warn('请求未携带token - localStorage中无token')
    }
    
    return config
  },
  (error) => {
    const { t } = i18n.global
    console.error('Request error:', error)
    showNetworkErrorNotification(t('utils.request.networkRequestFailed'))
    // 标记错误已被处理
    const networkError = new NetworkError(t('utils.request.networkRequestFailed'));
    return Promise.reject(networkError)
  }
)

// 响应拦截器设置函数，解决循环引用问题
export function setupResponseInterceptors(logout: () => Promise<any>) {
  const { t } = i18n.global
  // 响应拦截器
  service.interceptors.response.use(
    (response: AxiosResponse) => {
      const res = response.data as HttpResponse
      console.log('Response:', res.code)
      // 根据状态码处理不同情况
      if (isSuccessResponse(res.code)) {
        return response
      } else if (res.code === HttpStatus.UNAUTHORIZED) {
        // 使用后端返回的消息
        ElNotification.warning({
          message: res.msg || t('utils.request.unauthorizedWarning'),
          duration: 3000,
        })
        
        // 调用登出函数
        logout()
        
        // 跳转到首页
        router.push('/')
        return Promise.reject(new Error(res.msg || t('utils.request.unauthorized')))
      } else {
        // 其他错误情况，使用后端返回的消息
        ElNotification.error({
          message: res.msg || t('utils.request.requestFailed'),
          duration: 3000
        })
        return Promise.reject(new Error(res.msg || t('utils.request.requestFailed')))
      }
    },
    (error) => {
      console.error('Response error:', error)
      
      if (error instanceof NetworkError || (error && error.handled === true)) {
        return Promise.reject(error)
      }
      

      let errorMessage = t('utils.request.requestError')
      
      // 检查是否为网络连接问题
      if (!navigator.onLine) {
        const networkError = new NetworkError(t('utils.request.networkDisconnected'));
        showNetworkErrorNotification(networkError.message)
        return Promise.reject(networkError)
      }
      
      // 如果有响应对象，尝试从中获取错误信息
      if (error.response) {
        const { status, statusText, data } = error.response
        
        // 尝试获取详细错误信息
        if (data && data.msg) {
          errorMessage = data.msg
        } else {
          // 根据HTTP状态码定制错误消息
          switch (status) {
            case HttpStatus.BAD_REQUEST:
              errorMessage = `${t('utils.request.badRequest')} (${status}: ${statusText})`
              break
            case HttpStatus.UNAUTHORIZED:
              errorMessage = t('utils.request.unauthorized')
              // 登出处理
              logout()
              router.push('/')
              break
            case HttpStatus.FORBIDDEN:
              errorMessage = `${t('utils.request.forbidden')} (${status}: ${statusText})`
              break
            case HttpStatus.NOT_FOUND:
              errorMessage = `${t('utils.request.notFound')} (${status}: ${statusText})`
              break
            case HttpStatus.INTERNAL_SERVER_ERROR:
              errorMessage = `${t('utils.request.internalServerError')} (${status}: ${statusText})`
              break
            default:
              errorMessage = `${t('utils.request.serverResponseFailed')} (${status}: ${statusText})`
          }
        }
      } else if (error.request) {
        // 请求已发出但没有收到响应
        const networkError = new NetworkError(t('utils.request.serverNoResponse'));
        showNetworkErrorNotification(networkError.message)
        return Promise.reject(networkError)
      } else if (error.message) {
        // 错误信息处理
        if (error.message.includes('Network Error')) {
          const networkError = new NetworkError(t('utils.request.networkConnectionFailed'));
          showNetworkErrorNotification(networkError.message)
          return Promise.reject(networkError)
        } else if (error.message.includes('timeout')) {
          const networkError = new NetworkError(t('utils.request.requestTimeout'));
          showNetworkErrorNotification(networkError.message)
          return Promise.reject(networkError)
        } else if (error.message === t('utils.request.networkDisconnected')) {
          // 这是我们在请求拦截器中自定义的错误，已经显示了通知，这里直接返回
          return Promise.reject(error)
        } else {
          errorMessage = error.message
        }
      }
      
      // 显示错误通知
      ElNotification.error({
        message: errorMessage,
        duration: 5000,
      })
      
      return Promise.reject(error)
    }
  )
}

// 封装GET请求
export function get<T = any>(url: string, params?: any, config?: AxiosRequestConfig): Promise<T> {
  console.log(`发起GET请求: ${url}`, params)
  return service.get(url, { params, ...config }).then(res => {
    console.log(`GET请求返回 ${url}:`, res.data)
    return res.data.data
  })
}

// 封装POST请求
export function post<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
  console.log(`发起POST请求: ${url}`, data)
  return service.post(url, data, config).then(res => {
    console.log(`POST请求返回 ${url}:`, res.data)
    return res.data.data
  })
}

// 封装PUT请求
export function put<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
  console.log(`发起PUT请求: ${url}`, data)
  return service.put(url, data, config).then(res => {
    console.log(`PUT请求返回 ${url}:`, res.data)
    return res.data.data
  })
}

// 封装DELETE请求
export function del<T = any>(url: string, params?: any, config?: AxiosRequestConfig): Promise<T> {
  console.log(`发起DELETE请求: ${url}`, params)
  return service.delete(url, { params, ...config }).then(res => {
    console.log(`DELETE请求返回 ${url}:`, res.data)
    return res.data.data
  })
}

export default service 