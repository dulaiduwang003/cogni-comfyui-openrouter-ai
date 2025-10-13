import type { SystemOverviewApi } from './types'
import { get } from '@/utils/requestUtil'

export const systemOverviewApi = {
    reqSystemOverview: () => {
        return get<SystemOverviewApi.Result>('/system/overview/get')
    }
} 