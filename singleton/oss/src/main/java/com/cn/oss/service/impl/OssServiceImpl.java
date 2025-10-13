package com.cn.oss.service.impl;

import com.cn.common.configuration.AliConfiguration;
import com.cn.common.enums.FilePathEnum;
import com.cn.common.utils.UploadUtil;
import com.cn.oss.exceptions.UploadException;
import com.cn.oss.service.OssService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * OSS服务实现类
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OssServiceImpl implements OssService {

    private final UploadUtil uploadUtil;
    private final AliConfiguration aliConfiguration;

    @SuppressWarnings("all")
    public String uploadFile(final MultipartFile file) {
        String contentType = file.getContentType();
        long size = file.getSize();
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        }

        AliConfiguration.Oss ossConfig = aliConfiguration.getOss();
        boolean isValidType = false;
        Long maxSizeInBytes = null;

        for (AliConfiguration.SupportedFileType supportedFileType : ossConfig.getSupportedFileTypes()) {
            if (supportedFileType.getExtension().equalsIgnoreCase(extension)
                    && supportedFileType.getMimeType().equalsIgnoreCase(contentType)) {
                isValidType = true;
                maxSizeInBytes = supportedFileType.getMaxSizeInBytes();
                break;
            }
        }

        if (!isValidType) {
            throw new UploadException("不支持的文件类型: " + contentType + " (扩展名: " + extension + ")");
        }

        if (maxSizeInBytes != null && size > maxSizeInBytes) {
            throw new UploadException("文件大小超过限制. 最大允许: " + maxSizeInBytes / (1024 * 1024) + "MB");
        }

        return uploadUtil.uploadFile(file, FilePathEnum.TEMP.getDec());


    }

}
