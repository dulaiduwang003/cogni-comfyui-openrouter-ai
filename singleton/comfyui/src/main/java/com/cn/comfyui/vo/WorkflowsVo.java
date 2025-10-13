package com.cn.comfyui.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 工作流VO
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class WorkflowsVo implements Serializable {

    private Long workflowId;

    private String name;

    private String description;

    private String url;
    
    private String categoryName;

    /**
     * 工作流需要消耗的积分数量
     */
    private Long creditsDeducted;
}
