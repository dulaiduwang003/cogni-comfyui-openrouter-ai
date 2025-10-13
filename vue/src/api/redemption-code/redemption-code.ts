import type { RedeemCodeApi } from './types'
import { post } from '@/utils/requestUtil'

export const redemptionCodeApi = {
    reqRedeemCode: (params: RedeemCodeApi.Params) => {
        return post<void>('/redemption-code/redeem', params)
    }
} 