package com.cn.oss.controller;


import com.cn.common.exceptions.OssException;
import com.cn.common.msg.Result;
import com.cn.oss.exceptions.UploadException;
import com.cn.oss.service.OssService;
import com.cn.common.annotations.RateLimit;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * OSS控制器
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/oss")
@RequiredArgsConstructor
public class OssController {

    private final OssService ossService;



    /**
     * Upload file result.
     *
     * @param file the file
     * @return the result
     */
    @PostMapping(value = "/upload/file", consumes = "multipart/form-data")
    @RateLimit(permitsPerSecond = 0.2, limitType = RateLimit.LimitType.USER, message = "文件上传过于频繁，请稍后再试")
    public Result uploadFile(@Valid @NotNull(message = "上传文件不能为空") final MultipartFile file) {
        try {
            return Result.data(ossService.uploadFile(file));
        } catch (OssException | UploadException ex) {
            return Result.error(ex.getMessage());
        }

    }
}
