package com.cn.common.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class PageVo<T>{


    private Long total;

    private List<T> items;
}
