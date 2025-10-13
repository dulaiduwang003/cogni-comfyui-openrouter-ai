package com.cn.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * The enum File enum.
 *
 * @author 时间海 @github dulaiduwang003
 * @version 1.0
 */
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public enum FilePathEnum {

    TEMP("TEMP"),

    COMFYUI("COMFYUI"),

    USER("USER");

    String dec;

}
