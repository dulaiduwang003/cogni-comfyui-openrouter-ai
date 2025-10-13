import { ref, onMounted } from 'vue'

/**
 * 页面进入动画组合式函数
 * 提供多种页面进入动画效果
 */
export function usePageTransition(options?: {
  /** 动画延迟时间（毫秒） */
  delay?: number
  /** 动画持续时间（毫秒） */
  duration?: number
  /** 动画类型 */
  type?: 'fade-up' | 'fade-down' | 'fade-left' | 'fade-right' | 'scale' | 'fade'
}) {
  const {
    delay = 0,
    duration = 600,
    type = 'fade-up'
  } = options || {}

  const isVisible = ref(false)

  onMounted(() => {
    setTimeout(() => {
      isVisible.value = true
    }, delay)
  })

  return {
    isVisible,
    transitionClass: `page-transition-${type}`,
    transitionStyle: {
      transitionDuration: `${duration}ms`
    }
  }
}

/**
 * 列表项交错动画
 * 为列表中的每个项目创建交错的进入动画
 */
export function useStaggeredTransition(options?: {
  /** 每个项目之间的延迟时间（毫秒） */
  staggerDelay?: number
  /** 初始延迟时间（毫秒） */
  initialDelay?: number
  /** 动画持续时间（毫秒） */
  duration?: number
}) {
  const {
    staggerDelay = 80,
    initialDelay = 100,
    duration = 500
  } = options || {}

  const visibleItems = ref<Set<number>>(new Set())

  onMounted(() => {
    // 可以在需要时手动触发
  })

  const showItem = (index: number) => {
    setTimeout(() => {
      visibleItems.value.add(index)
    }, initialDelay + index * staggerDelay)
  }

  const showAllItems = (count: number) => {
    for (let i = 0; i < count; i++) {
      showItem(i)
    }
  }

  const isItemVisible = (index: number) => {
    return visibleItems.value.has(index)
  }

  return {
    visibleItems,
    showItem,
    showAllItems,
    isItemVisible,
    getItemStyle: (index: number) => ({
      transitionDelay: `${index * staggerDelay}ms`,
      transitionDuration: `${duration}ms`
    })
  }
}

