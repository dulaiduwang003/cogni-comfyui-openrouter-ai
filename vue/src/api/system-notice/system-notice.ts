import type { SystemNoticeApi } from './types'
import { post } from '@/utils/requestUtil'

export const systemNoticeApi = {
  setNotice: (body: SystemNoticeApi.NoticeSetDto) => post<void>('/system/notice/set', body),
  clearNotice: () => post<void>('/system/notice/clear')
}


