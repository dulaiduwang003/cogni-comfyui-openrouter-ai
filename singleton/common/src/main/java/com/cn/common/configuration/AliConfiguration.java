package com.cn.common.configuration;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author bdth github@dulaiduwang003
 * @version 1.0
 */
@Configuration
@ConfigurationProperties(prefix = "ali")
@Data
@Accessors(chain = true)
public class AliConfiguration {

    private Certified certified;

    private Oss oss;

    private Sms sms;

    @Data
    @Accessors(chain = true)
    public static class Certified {

        private String accessKey;

        private String secretKey;

    }

    @Data
    @Accessors(chain = true)
    public static class Sms {

        private String templateCode;

        private String signName;

    }

    @Data
    @Accessors(chain = true)
    public static class Oss {

        private String endpoint;

        private String bucketName;

        private String domain;

        private List<SupportedFileType> supportedFileTypes;
    }

    @Data
    @Accessors(chain = true)
    public static class SupportedFileType {
        private String extension;
        private String mimeType;
        private Long maxSizeInBytes;
    }

}
