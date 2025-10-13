package com.cn.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@TableName(value = "user_credits")
public class UserCredits {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long totalCredits;

    private Long availableCredits;

    private Long frozenCredits;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

} 