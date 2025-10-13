package com.cn.common.configuration;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

/**
 * @author bdth github@dulaiduwang003
 * @version 1.0
 */
@Configuration
@ConfigurationProperties(prefix = "comfyui")
@Data
@Accessors(chain = true)
public class ComfyuiConfiguration {

    private List<ServerConfig> server;

    private Long submitTaskMax;

    private Task task;

    private Map<String, String> supportedFileTypes;

    @Data
    @Accessors(chain = true)
    public static class Task {

        private Long maxRetryTime;

        private Long maxRetries;

        private Long timeoutCheckInterval;
    }

    @Data
    @Accessors(chain = true)
    public static class ServerConfig {
        private String name;
        private String url;
    }
}
