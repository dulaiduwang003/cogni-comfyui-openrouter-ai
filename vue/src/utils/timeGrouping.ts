import i18n from '@/i18n'

export interface TimeGroup {
  id: string
  title: string
  startTime: number
  endTime: number
}

/**
 * 获取时间分组配置
 */
export function getTimeGroups(): TimeGroup[] {
  const { t } = i18n.global
  const now = new Date()
  const today = new Date(now.getFullYear(), now.getMonth(), now.getDate())
  const yesterday = new Date(today.getTime() - 24 * 60 * 60 * 1000)
  
  // 获取本周开始时间（周一）
  const dayOfWeek = now.getDay()
  const daysFromMonday = dayOfWeek === 0 ? 6 : dayOfWeek - 1
  const thisWeekStart = new Date(today.getTime() - daysFromMonday * 24 * 60 * 60 * 1000)
  
  // 获取上周开始时间
  const lastWeekStart = new Date(thisWeekStart.getTime() - 7 * 24 * 60 * 60 * 1000)
  
  // 获取本月开始时间
  const thisMonthStart = new Date(now.getFullYear(), now.getMonth(), 1)
  
  // 获取上月开始时间
  const lastMonthStart = new Date(now.getFullYear(), now.getMonth() - 1, 1)
  const lastMonthEnd = new Date(now.getFullYear(), now.getMonth(), 0, 23, 59, 59, 999)
  
  return [
    {
      id: 'today',
      title: t('utils.timeGroup.today'),
      startTime: today.getTime(),
      endTime: now.getTime() + 24 * 60 * 60 * 1000
    },
    {
      id: 'yesterday', 
      title: t('utils.timeGroup.yesterday'),
      startTime: yesterday.getTime(),
      endTime: today.getTime()
    },
    {
      id: 'this-week',
      title: t('utils.timeGroup.thisWeek'),
      startTime: thisWeekStart.getTime(),
      endTime: yesterday.getTime()
    },
    {
      id: 'last-week',
      title: t('utils.timeGroup.lastWeek'),
      startTime: lastWeekStart.getTime(),
      endTime: thisWeekStart.getTime()
    },
    {
      id: 'this-month',
      title: t('utils.timeGroup.thisMonth'),
      startTime: thisMonthStart.getTime(),
      endTime: lastWeekStart.getTime()
    },
    {
      id: 'last-month',
      title: t('utils.timeGroup.lastMonth'),
      startTime: lastMonthStart.getTime(),
      endTime: lastMonthEnd.getTime()
    },
    {
      id: 'earlier',
      title: t('utils.timeGroup.earlier'),
      startTime: 0,
      endTime: lastMonthStart.getTime()
    }
  ]
}

/**
 * 根据时间戳获取对应的分组
 */
export function getTimeGroupForTimestamp(timestamp: number): TimeGroup | null {
  const groups = getTimeGroups()
  
  for (const group of groups) {
    if (timestamp >= group.startTime && timestamp < group.endTime) {
      return group
    }
  }
  
  // 如果没有找到匹配的分组，返回"更早"分组
  return groups.find(g => g.id === 'earlier') || null
}

/**
 * 将会话列表按时间分组
 */
export function groupSessionsByTime<T extends { createdAt: number }>(sessions: T[]): Array<{
  group: TimeGroup
  items: T[]
}> {
  const groups = getTimeGroups()
  const result: Array<{ group: TimeGroup; items: T[] }> = []
  
  // 为每个分组初始化空数组
  const groupMap = new Map<string, T[]>()
  groups.forEach(group => {
    groupMap.set(group.id, [])
  })
  
  // 将会话分配到对应的分组
  sessions.forEach(session => {
    const group = getTimeGroupForTimestamp(session.createdAt)
    if (group) {
      const items = groupMap.get(group.id)
      if (items) {
        items.push(session)
      }
    }
  })
  
  // 只返回有内容的分组
  groups.forEach(group => {
    const items = groupMap.get(group.id) || []
    if (items.length > 0) {
      result.push({
        group,
        items: items.sort((a, b) => b.createdAt - a.createdAt) // 按时间倒序排列
      })
    }
  })
  
  return result
} 