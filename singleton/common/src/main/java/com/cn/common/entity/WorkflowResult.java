package com.cn.common.entity;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@TableName(value = "workflow_result", autoResultMap = true)
public class WorkflowResult {

  @TableId(type = IdType.AUTO)
  private Long id;

  private String taskId;

  private String type;

  private String url;

  private Long userId;

  private String workflowName;

  /**
   * 工作流ID
   */
  private Long workflowId;

  /**
   * 生成时使用的表单参数（包含 tips、type、options 等元数据）
   * 存储格式为 JSON，对应 TaskInfoStructure.Form 结构
   */
  @TableField(typeHandler = JacksonTypeHandler.class)
  private JSONObject formParams;

  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  @TableField(fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updateTime;
}
