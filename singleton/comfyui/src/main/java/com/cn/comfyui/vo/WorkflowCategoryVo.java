package com.cn.comfyui.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 工作流筛选条件VO
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class WorkflowCategoryVo {

    private Long categoryId;

    private String name;


} 