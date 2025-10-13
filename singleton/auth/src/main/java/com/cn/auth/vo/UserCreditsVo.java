package com.cn.auth.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用户积分信息视图对象
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class UserCreditsVo {

    /**
     * 总积分
     */
    private Long totalCredits;

    /**
     * 可用积分
     */
    private Long availableCredits;

    /**
     * 冻结积分
     */
    private Long frozenCredits;

} 