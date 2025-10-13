import type { GetUserInfoApi, UpdateAvatarApi, UpdateNicknameApi, GetUserCreditsApi, GetCreditTransactionsApi } from './types'
import { get, post } from '@/utils/requestUtil'

export const userApi = {
   reqGetUserInfo: () => {
       return get<GetUserInfoApi.Result>('/user/get/user-info')
    },
    
    // 更新用户头像
    reqUpdateAvatar: (params: UpdateAvatarApi.Params) => {
        return post<void>('/user/update-avatar', params)
    },
    
    // 更新用户昵称
    reqUpdateNickname: (params: UpdateNicknameApi.Params) => {
        return post<void>('/user/update-nickname', params)
    },
    
    // 获取用户积分信息
    reqGetUserCredits: () => {
        return get<GetUserCreditsApi.Result>('/user/get/credits')
    },
    
    // 获取积分交易记录
    reqGetCreditTransactions: (params?: GetCreditTransactionsApi.Params) => {
        return get<GetCreditTransactionsApi.Result>('/user/get/credit-transactions',params)
    }
}