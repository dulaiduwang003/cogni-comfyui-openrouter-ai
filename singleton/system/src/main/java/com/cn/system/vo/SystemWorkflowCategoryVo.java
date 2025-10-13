package com.cn.system.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 系统工作流类别VO
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class SystemWorkflowCategoryVo {

    private Long categoryId;

    private String name;
}


