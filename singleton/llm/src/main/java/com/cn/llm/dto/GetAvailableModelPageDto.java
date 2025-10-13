package com.cn.llm.dto;

import lombok.Data;

/**
 * 获取可用模型分页DTO
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Data
public class GetAvailableModelPageDto {
    private String inputType;     // 逗号分隔的输入模态
    private String outputType;    // 逗号分隔的输出模态
    private String name;          // 名称模糊匹配
    private Boolean supportReasoning;  // 是否为推理模型（null 不筛选）
    private String paymentMode;   // 付费模式筛选（收费/免费，null 不筛选
    // 分页参数
    private Integer page;              // 当前页码
} 