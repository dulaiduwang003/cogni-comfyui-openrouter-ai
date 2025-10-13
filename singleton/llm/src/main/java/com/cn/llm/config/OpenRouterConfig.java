package com.cn.llm.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.AssertTrue;

import java.util.Optional;

/**
 * OpenRouter 配置类
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "open-router")
@Validated
public class OpenRouterConfig {
    
    /**
     * OpenRouter API 密钥
     */
    private String apiKey;
    
    /**
     * OpenRouter API 基础 URL
     */
    private String baseUrl;
    
    /**
     * 连接超时时间（毫秒）
     */
    private Integer connectTimeout;
    
    /**
     * 读取超时时间（毫秒）
     */
    private Integer readTimeout;
    
    
    /**
	 * 远程注册配置
     */
	private RemoteRegistry remoteRegistry = new RemoteRegistry();

    /**
     * 截断估算配置
     */
    private Truncation truncation = new Truncation();
	
	/**
	 * 插件配置
	 */
	private Plugins plugins = new Plugins();
	
	/**
	 * 音频上传处理配置
	 */
	private Audio audio = new Audio();
	
	/**
	 * 深度思考（reasoning）配置
	 */
	private Reasoning reasoning = new Reasoning();

	/**
	 * 上传限制配置
	 */
	private Upload upload = new Upload();

	/**
	 * 聊天配置
	 */
	private Chat chat = new Chat();

	@Data
	public static class Reasoning {
		/** 是否启用深度思考 */
		private Boolean enabled = false;
		/** 深度思考最大 token 预算 */
		private Integer maxTokens = 8000;
	}
    
	public enum FilterMode {
		ALL,
		FREE,
		PAID
	}

    @Data
    public static class Truncation {
        /** 为回复预留token */
        private Integer responseTokenReserve = 2000;
        /** 文本估算字符/Token */
        private Integer textCharsPerToken = 4;
        /** 单张图片估算 token */
        private Integer imageTokenEstimate = 1500;
		/** 单个文件估算 token */
		private Integer fileTokenEstimate = 4000;
		/** 单段音频估算 token */
		private Integer audioTokenEstimate = 6000;
        /** 是否启用文本压缩（截断前比例缩短） */
        private Boolean enableCompression = true;
    }
	
	    @Data
    public static class RemoteRegistry {
        /** 是否启用远程注册数据预取与定时刷新 */
        private boolean enabled = false;
        /** 远程接口 URL */
        private String url;
        /** 刷新 Cron 表达式 */
        private String cron;
        /** 读取超时时间（秒） */
        private Integer readTimeoutSeconds;
	        /** 模型过滤模式：ALL、FREE、PAID */
        private FilterMode filter = FilterMode.ALL;
        /** 当请求未指定模型时的自动选择偏好：FREE 或 PAID */
        private FilterMode defaultWhenModelEmpty = FilterMode.FREE;
	        /** 自动选择配置（支持固定 id 与偏好优先级） */
	        private Auto auto = new Auto();

        @AssertTrue(message = "启用 open-router.remote-registry 时必须配置 url")
        public boolean isUrlPresentWhenEnabled() {
            return !enabled || (url != null && !url.isBlank());
        }
    }

	    @Data
	    public static class Auto {
	        /** 指定模型 id（最高优先级） */
	        private String modelId;
	        /** 指定偏好：FREE 或 PAID（当未指定 id 时生效） */
	        private FilterMode prefer = FilterMode.FREE;
	    }
	
	@Data
	public static class Plugins {
		private FileParser fileParser = new FileParser();
		private Web web = new Web();
		
		@Data
		public static class Web {
			/** 是否启用联网搜索插件 */
			private Boolean enabled = true;
			/** 搜索结果最大数量 */
			private Integer maxResults = 5;
		}
		
		@Data
		public static class FileParser {
			private Boolean enabled = true;
			private Pdf pdf = new Pdf();
			@Data
			public static class Pdf {
				private String engine = "mistral-ocr";
				/** 静态文件名（用于多模态 file 内容的 filename），由配置指定 */
				@NotBlank(message = "open-router.plugins.file-parser.pdf.static-filename 不能为空")
				private String staticFilename;
        }
		}
	}
	
	@Data
	public static class Audio {
		/** 允许的最大音频大小（字节） */
		private Long maxSizeBytes = 20L * 1024 * 1024;
		/** 允许的音频格式列表（按优先级） */
		private java.util.List<String> allowedFormats;
		/** 无法判断时的默认格式（必须属于 allowedFormats） */
		private String defaultFormat;
	}
	
	@Data
	public static class Upload {
		/** imageUrls + pdfFiles + audioFiles 的最大总数 */
		private Integer maxAttachmentCount = 10;
	}

	@Data
	public static class Chat {
		/** 会话TTL（秒）- 默认30天 */
		private Integer sessionTtlSeconds = 30 * 24 * 60 * 60;
	}

} 
