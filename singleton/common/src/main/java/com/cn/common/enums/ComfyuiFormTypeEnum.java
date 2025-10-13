package com.cn.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 * @author 明明不是下雨天 github@dulaiduwang003 2024/9/26 16:03
 */

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public enum ComfyuiFormTypeEnum {

    /**
     * 文本可配置类型（解析阶段使用，需要管理员选择具体类型）
     */
    TEXT_CONFIGURABLE("TEXT_CONFIGURABLE"),

    /**
     * 纯文本输入框
     */
    TEXT_PROMPT("TEXT_PROMPT"),

    /**
     * 单选下拉框
     */
    RADIO_SELECTOR("RADIO_SELECTOR"),

    /**
     * 多选框
     */
    CHECKBOX_SELECTOR("CHECKBOX_SELECTOR"),

    /**
     * 图片上传
     */
    IMAGE_UPLOAD("IMAGE_UPLOAD"),

    /**
     * 视频上传
     */
    VIDEO_UPLOAD("VIDEO_UPLOAD"),

    /**
     * 音频上传
     */
    AUDIO_UPLOAD("AUDIO_UPLOAD");


    String dec;

}
