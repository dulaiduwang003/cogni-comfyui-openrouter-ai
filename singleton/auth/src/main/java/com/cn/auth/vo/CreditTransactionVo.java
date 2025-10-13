package com.cn.auth.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 积分交易记录视图对象
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class CreditTransactionVo {

    /**
     * 交易ID
     */
    private Long id;

    /**
     * 交易类型
     */
    private String transactionType;

    /**
     * 交易金额
     */
    private Long amount;

    /**
     * 交易描述
     */
    private String description;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

} 