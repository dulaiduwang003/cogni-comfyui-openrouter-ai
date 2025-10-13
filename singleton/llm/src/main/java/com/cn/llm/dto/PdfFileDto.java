package com.cn.llm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * PDF文件DTO
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Data
public class PdfFileDto {
	/** PDF 文件 URL（仅允许 http/https） */
	@NotBlank(message = "pdfFiles.url 不能为空")
	private String url;
} 