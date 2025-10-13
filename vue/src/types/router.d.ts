import 'vue-router'

// 返回按钮配置接口
interface BackButtonConfig {
  // 是否显示返回按钮
  show: boolean
  // 返回按钮的目标路由（如果不设置则使用浏览器历史返回）
  to?: string
  // 返回按钮的文本（默认为"返回"）
  text?: string
}

declare module 'vue-router' {
  interface RouteMeta {
    // 是否需要认证
    requiresAuth?: boolean
    // 是否隐藏侧边栏
    hideSidebar?: boolean
    // 页面标题
    title?: string
    // 页面描述
    description?: string
    // 返回按钮配置
    backButton?: BackButtonConfig
    // 其他自定义元信息
    [key: string]: any
  }
} 