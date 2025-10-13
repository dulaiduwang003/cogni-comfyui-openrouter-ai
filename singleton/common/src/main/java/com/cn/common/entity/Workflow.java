package com.cn.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@TableName(value = "workflow")
public class Workflow {

  @TableId(type = IdType.AUTO)
  private Long id;

  private String name;

  private String description;

  private String url;

  private String json;

  private String workflowCategoryId;

  private Long creditsDeducted;

  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  @TableField(fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updateTime;

}
