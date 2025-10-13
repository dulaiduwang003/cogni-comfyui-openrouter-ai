// 用户积分交易类型枚举
export enum TransactionType {
    FREEZE = "FREEZE",       // 冻结积分
    CONSUME = "CONSUME",     // 消费积分  
    REFUND = "REFUND",       // 退还积分
    RECHARGE = "RECHARGE"    // 充值积分
} 

export enum Role {
    USER = "USER", // 用户
    ADMIN = "ADMIN" // 管理员
} 