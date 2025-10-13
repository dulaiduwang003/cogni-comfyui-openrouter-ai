import { TransactionType, Role } from '@/enums/user'

export namespace GetUserInfoApi {
    export interface Result {
        nickname: string
        avatar: string
        role: Role
    }
    
  
    
}

// 更新用户头像接口
export namespace UpdateAvatarApi {
    export interface Params {
        avatar: string
    }
}

// 更新用户昵称接口  
export namespace UpdateNicknameApi {
    export interface Params {
        nickname: string
    }
}

// 获取用户积分信息接口
export namespace GetUserCreditsApi {
    export interface Result {
        totalCredits: number
        availableCredits: number
        frozenCredits: number
    }
}

// 获取积分交易记录接口
export namespace GetCreditTransactionsApi {
    export interface Params {
        page?: number
        size?: number
        transactionType?: TransactionType
    }
    
    export interface Result {
        total: number
        items: CreditTransaction[]
    }
    
    export interface CreditTransaction {
        id: number
        transactionType: TransactionType
        amount: number
        description: string
        createTime: string
    }
}
