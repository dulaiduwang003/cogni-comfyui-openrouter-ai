package com.cn.common.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.ObjectMetadata;
import com.cn.common.configuration.AliConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Base64;
import java.util.UUID;
import java.io.ByteArrayInputStream;

/**
 * The type Upload util.
 *
 * @author 时间海 @github dulaiduwang003
 * @version 1.0
 */
@Component
@SuppressWarnings("all")
@Slf4j
@RequiredArgsConstructor
public class UploadUtil {

    private final AliConfiguration aliConfiguration;

    public String uploadFile(final MultipartFile file, final String path) {
        AliConfiguration.Oss oss = aliConfiguration.getOss();
        AliConfiguration.Certified certified = aliConfiguration.getCertified();
        OSS ossClient = new OSSClientBuilder()
                .build(oss.getEndpoint(), certified.getAccessKey(), certified.getSecretKey());
        try (InputStream inputStream = file.getInputStream()) {
            String originalFileName = file.getOriginalFilename();

            assert originalFileName != null;
            String fileName;
            fileName = UUID.randomUUID() + originalFileName.substring(originalFileName.lastIndexOf('.'));

            String filePath = path + "/" + fileName;
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(file.getContentType());
            ossClient.putObject(oss.getBucketName(), filePath, inputStream, objectMetadata);
            return oss.getDomain() + "/" + filePath;

        } catch (IOException e) {
            throw new OSSException();
        } finally {
            ossClient.shutdown();
        }
    }

    public String uploadUrl(String imageUrl, String path) {
        AliConfiguration.Oss oss = aliConfiguration.getOss();
        AliConfiguration.Certified certified = aliConfiguration.getCertified();
        OSS ossClient = new OSSClientBuilder().build(oss.getEndpoint(),certified.getAccessKey(),certified.getSecretKey());
        try {
            URL url = new URL(imageUrl);
            String pathWithoutQuery = url.getPath();
            String fileNameWithExt = null;

            // 尝试从查询参数中获取文件名
            String query = url.getQuery();
            if (query != null && query.contains("filename=")) {
                String[] params = query.split("&");
                for (String param : params) {
                    if (param.startsWith("filename=")) {
                        fileNameWithExt = URLDecoder.decode(param.substring("filename=".length()), "UTF-8");
                        break;
                    }
                }
            }

            // 如果没有从查询参数中获取到文件名，则使用 URL 路径中的文件名
            if (fileNameWithExt == null) {
                fileNameWithExt = pathWithoutQuery.substring(pathWithoutQuery.lastIndexOf('/') + 1);
            }

            String fileName = path + "/" + UUID.randomUUID().toString() + "." + getFileExtension(fileNameWithExt);

            InputStream inputStream = url.openStream();
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(getMimeType(fileNameWithExt));

            ossClient.putObject(oss.getBucketName(), fileName, inputStream, objectMetadata);

            return oss.getDomain() + "/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload media from URL: " + imageUrl, e);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    public String uploadBase64Image(String base64OrDataUri, String path) {
        AliConfiguration.Oss oss = aliConfiguration.getOss();
        AliConfiguration.Certified certified = aliConfiguration.getCertified();
        OSS ossClient = new OSSClientBuilder().build(oss.getEndpoint(),certified.getAccessKey(),certified.getSecretKey());
        try {
            String base64Data = base64OrDataUri;
            if (base64OrDataUri != null && base64OrDataUri.startsWith("data:")) {
                int commaIdx = base64OrDataUri.indexOf(',');
                if (commaIdx > 0) {
                    String meta = base64OrDataUri.substring(5, commaIdx); // e.g. image/png;base64
                    String contentType = meta.contains(";") ? meta.substring(0, meta.indexOf(';')) : meta;
                    if (!"image/png".equalsIgnoreCase(contentType)) {
                        throw new IllegalArgumentException("Only PNG base64 is supported");
                    }
                    base64Data = base64OrDataUri.substring(commaIdx + 1);
                }
            }

            byte[] decodedBytes = Base64.getDecoder().decode(base64Data);

            String objectKey = path + "/" + UUID.randomUUID().toString() + ".png";
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType("image/png");
            objectMetadata.setContentLength(decodedBytes.length);

            ByteArrayInputStream bais = new ByteArrayInputStream(decodedBytes);
            ossClient.putObject(oss.getBucketName(), objectKey, bais, objectMetadata);
            return oss.getDomain() + "/" + objectKey;
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload base64 image (PNG only)", e);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    private String getMimeType(String fileName) {
        if (fileName.endsWith(".jpg")) {
            return "image/jpeg";
        } else if (fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (fileName.endsWith(".png")) {
            return "image/png";
        } else if (fileName.endsWith(".webp")) {
            return "image/webp";
        } else if (fileName.endsWith(".mp4")) {
            return "video/mp4";
        } else if (fileName.endsWith(".avi")) {
            return "video/x-msvideo";
        } else if (fileName.endsWith(".mp3")) {
            return "audio/mpeg";
        } else if (fileName.endsWith(".wav")) {
            return "audio/x-wav";
        }
        // 默认返回二进制流类型
        return "application/octet-stream";
    }

    private String getFileExtension(String fileName) {
        if (fileName == null) {
            return "";
        }
        int lastIndexOfDot = fileName.lastIndexOf('.');
        if (lastIndexOfDot == -1) {
            return ""; // 没有扩展名
        }
        return fileName.substring(lastIndexOfDot + 1);
    }



}
