package com.cn.llm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 音频文件DTO
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Data
public class AudioFileDto {
	/** 音频文件 URL */
	@NotBlank(message = "audio url 不能为空")
	private String url;
} 