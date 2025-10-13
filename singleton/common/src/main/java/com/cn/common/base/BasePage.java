package com.cn.common.base;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author 明明不是下雨天 github@dulaiduwang003 2024/7/4 下午4:37
 */
@Data
@Accessors(chain = true)
public class BasePage<T> {

    private List<T> items;

    private Long total;

}
