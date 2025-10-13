package com.cn.llm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 提交消息DTO
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Data
public class SubmitMessageDto {

	@NotBlank(message = "sessionId 不能为空")
	private String sessionId;

	/**
	 * 文本可为空，但 text、imageUrls、pdfFiles、audioFiles 不能同时为空（由控制器校验）
	 */
	private String text;

	/**
	 * 图片URL列表，仅 http/https 或 data:image;base64 由前端转为URL，这里只接收URL。
	 */
	private List<String> imageUrls;

	/**
	 * PDF 文件（多个）
	 */
	private List<PdfFileDto> pdfFiles;

	/**
	 * 音频文件（多个，base64 + format）
	 */
	private List<AudioFileDto> audioFiles;
} 