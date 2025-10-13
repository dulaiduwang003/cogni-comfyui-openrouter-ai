import type { SystemRedemptionApi } from './types'
import { get, post } from '@/utils/requestUtil'

export const systemRedemptionApi = {
  // 分页查询兑换码
  fetchCodes: (params: SystemRedemptionApi.FetchCodesParams) => {
    return get<SystemRedemptionApi.PageVo<SystemRedemptionApi.SystemRedemptionCodeVo>>(
      '/system/redemption-code/page',
      params
    )
  },

  // 创建兑换码
  createCode: (body: SystemRedemptionApi.CreateRedemptionCodeDto) => {
    return post<number>('/system/redemption-code/create', body)
  },

  // 删除兑换码
  deleteCode: (id: number) => {
    return post<void>('/system/redemption-code/delete', { id } as SystemRedemptionApi.DeleteRedemptionCodeDto)
  },

  // 修改兑换码积分
  updateCodeCredits: (id: number, creditsAmount: number) => {
    return post<void>('/system/redemption-code/update-credits', {
      id,
      creditsAmount
    } as SystemRedemptionApi.UpdateRedemptionCodeCreditsDto)
  },

  // 修改兑换码（除 code 外）
  updateCode: (body: SystemRedemptionApi.UpdateRedemptionCodeDto) => {
    return post<void>('/system/redemption-code/update', body)
  }
}


