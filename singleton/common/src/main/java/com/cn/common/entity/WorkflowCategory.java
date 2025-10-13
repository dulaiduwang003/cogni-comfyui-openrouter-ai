package com.cn.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * Comfyui工作流筛选实体类
 */

@Data
@Accessors(chain = true)
@TableName(value = "workflow_category")
public class WorkflowCategory {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;


    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;


}
