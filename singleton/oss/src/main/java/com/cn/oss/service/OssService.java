package com.cn.oss.service;


import org.springframework.web.multipart.MultipartFile;

/**
 * OSS服务接口
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
public interface OssService {


    String uploadFile(final MultipartFile file);

}
