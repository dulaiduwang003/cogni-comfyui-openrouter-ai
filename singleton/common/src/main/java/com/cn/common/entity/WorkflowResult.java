package com.cn.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@TableName(value = "workflow_result")
public class WorkflowResult {

  @TableId(type = IdType.AUTO)
  private Long id;

  private String taskId;

  private String type;

  private String url;

  private Long userId;

  private String workflowName;

  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  @TableField(fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updateTime;
}
