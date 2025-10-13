package com.cn.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName(value = "workflow_output")
public class WorkflowOutput {

  @TableId(type = IdType.AUTO)
  private Long id;

  private Long workflowId;

  private String nodeKey;

  private String type;

}
