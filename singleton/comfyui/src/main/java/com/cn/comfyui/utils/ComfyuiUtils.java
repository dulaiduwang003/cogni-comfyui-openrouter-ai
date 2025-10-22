package com.cn.comfyui.utils;

import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.alibaba.fastjson2.JSON.parseObject;

/**
 * ComfyUI工具类
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class ComfyuiUtils {



    public static String getFileExtensionFromUrl(String fileName) {
        if (fileName == null) {
            return "";
        }
        int lastIndexOfDot = fileName.lastIndexOf('.');
        if (lastIndexOfDot == -1 || lastIndexOfDot == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(lastIndexOfDot + 1).toLowerCase();
    }

    public static String getBodyError(final String errorBody) {
        final JSONObject error = parseObject(errorBody).getJSONObject("error");
        return error.getString("message");
    }
}
