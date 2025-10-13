export namespace SystemRedemptionApi {
  export type Result<T = any> = { code: number; msg: string; data: T }

  export type PageVo<T> = { total: number; items: T[] }

  export type SystemRedemptionCodeVo = {
    id: number
    code: string
    creditsAmount: number
    status: 1 | 0 | -1
    usedByUserId: number | null
    usedTime: string | null
    expireTime: string | null
    description: string | null
    createTime: string
  }

  export type CreateRedemptionCodeDto = {
    creditsAmount: number
    prefix?: string
    length?: number // >= 4
    expireTime?: string // ISO 8601
    description?: string
  }

  export type UpdateRedemptionCodeCreditsDto = { id: number; creditsAmount: number }
  export type DeleteRedemptionCodeDto = { id: number }
  export type UpdateRedemptionCodeDto = {
    id: number
    creditsAmount?: number // >= 0（已使用不允许改）
    status?: 1 | 0 | -1
    expireTime?: string
    description?: string
  }

  export type FetchCodesParams = {
    page?: number
    size?: number
    keyword?: string
    status?: 1 | 0 | -1
  }
}


