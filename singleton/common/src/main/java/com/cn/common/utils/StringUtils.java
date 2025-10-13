package com.cn.common.utils;

import java.util.List;
import java.util.Objects;

/**
 * 字符串处理类
 *
 * @author 时间海 @github dulaiduwang003
 * @version 1.0
 */
@SuppressWarnings("all")
public final class StringUtils extends org.apache.commons.lang3.StringUtils {


    public static boolean isLegal(String str) {
        if (isEmpty(str)) {
            return true;
        }
        return !str.contains("|");
    }

    public static boolean notEmpty(String str) {
        if (Objects.nonNull(str) && str.length() > 0) {
            return true;
        }

        return false;
    }


    public static <T> String join(List<T> list) {
        return join(list.toArray());
    }


    public static <T> String join(List<T> list, String separator) {
        return join(list.toArray(), separator);
    }

}
