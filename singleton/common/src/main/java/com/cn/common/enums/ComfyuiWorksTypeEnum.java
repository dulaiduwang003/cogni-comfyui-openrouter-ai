package com.cn.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public enum ComfyuiWorksTypeEnum {

    AUDIO("AUDIO"),

    VIDEO("VIDEO"),

    MODEL("MODEL"),

    IMAGE("IMAGE");



    String dec;

}
