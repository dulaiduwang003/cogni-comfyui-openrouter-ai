import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import i18n from './i18n'

import './style.css'
import './styles/page-transitions.css'
import './assets/highlight.css'
import App from './App.vue'
import router from './router'
import { initializeStores } from './stores'
import { authDirective, authShowDirective } from './directives/authDirective'

// 隐藏初始加载屏幕的函数
function hideInitialLoading() {
  const loadingElement = document.getElementById('initial-loading')
  if (loadingElement) {
    loadingElement.classList.add('fade-out')
    // 动画完成后移除元素
    setTimeout(() => {
      loadingElement.remove()
      document.body.style.overflow = 'auto'
    }, 500)
  }
}

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(i18n)
app.use(ElementPlus)
app.use(router)

// 注册认证指令
app.directive('auth', authDirective)
app.directive('auth-show', authShowDirective)

// 初始化stores
initializeStores().then(() => {
  app.mount('#app')
  
  // 等待渲染完成后隐藏加载屏幕
  setTimeout(() => {
    setTimeout(hideInitialLoading, 300)
  }, 100)
}).catch((error) => {
  console.error('应用初始化失败:', error)
  // 即使初始化失败也要隐藏加载屏幕
  hideInitialLoading()
})
