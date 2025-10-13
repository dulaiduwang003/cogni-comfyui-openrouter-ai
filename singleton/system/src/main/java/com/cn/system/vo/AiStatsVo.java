package com.cn.system.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * AI 对话统计 VO
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiStatsVo {

    /**
     * 今日 API 调用总次数
     */
    private Long todayApiCalls;

    /**
     * 今日 Token 消耗总量
     */
    private Long todayTokensUsed;

    /**
     * 今日对话次数（可选，从数据库统计）
     */
    private Long todayConversations;

    /**
     * 活跃的 AI 模型统计（Top 5）
     */
    private List<ModelCallStatsVo> activeModels;
}

