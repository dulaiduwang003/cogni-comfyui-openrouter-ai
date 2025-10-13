import type { NoticeApi } from './types'
import { get } from '@/utils/requestUtil'

export const noticeApi = {
  getNotice: () => get<NoticeApi.NoticeVo | null>('/notice/get')
}



