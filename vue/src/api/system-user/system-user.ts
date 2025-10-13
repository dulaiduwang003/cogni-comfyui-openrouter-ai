import type { 
    GetUserPageApi, 
    CreateUserApi, 
    UpdateUserApi, 
    DeleteUserApi 
} from './types'
import { get, post } from '@/utils/requestUtil'

export const systemUserApi = {
    // 获取用户分页列表
    reqGetUserPage: (params?: GetUserPageApi.Params) => {
        return get<GetUserPageApi.Result>('/system/user/page', params)
    },
    
    // 创建用户
    reqCreateUser: (params: CreateUserApi.Params) => {
        return post<void>('/system/user/create', params)
    },
    
    // 更新用户信息
    reqUpdateUser: (params: UpdateUserApi.Params) => {
        return post<void>('/system/user/update', params)
    },
    
    // 删除用户
    reqDeleteUser: (params: DeleteUserApi.Params) => {
        return post<void>('/system/user/delete', params)
    }
}