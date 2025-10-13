package com.cn.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@TableName(value = "credit_transactions")
public class CreditTransaction {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String transactionType;

    private Long amount;


    private String description;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

} 