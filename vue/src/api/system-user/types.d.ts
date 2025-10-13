import type { Role } from "@/enums/user"

// 获取用户分页列表接口
export namespace GetUserPageApi {
    export interface Params {
        page?: number
        size?: number
        keyword?: string
        role?: Role
    }
    
    export interface Result {
        total: number
        items: SystemUser[]
    }
    
    export interface SystemUser {
        id: number
        email: string
        nickname: string
        avatar: string
        role: Role
        createTime: string
        updateTime: string
    }
}

// 创建用户接口
export namespace CreateUserApi {
    export interface Params {
        email: string
        password: string
        nickname?: string
        avatar?: string
        role?: Role
    }
    
   
}

// 更新用户接口
export namespace UpdateUserApi {
    export interface Params {
        id: number
        email?: string
        nickname?: string
        avatar?: string
        role?: Role
    }
}

// 删除用户接口
export namespace DeleteUserApi {
    export interface Params {
        id: number
    }
}