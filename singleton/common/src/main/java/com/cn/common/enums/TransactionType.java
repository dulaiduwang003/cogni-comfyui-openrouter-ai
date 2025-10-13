package com.cn.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TransactionType {
    FREEZE("FREEZE"), //冻结积分
    CONSUME("CONSUME"), //消费积分
    REFUND("REFUND"), //退还积分
    RECHARGE("RECHARGE"); //充值积分

    private final String code;

} 