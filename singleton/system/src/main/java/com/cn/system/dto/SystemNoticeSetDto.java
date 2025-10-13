package com.cn.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统公告设置DTO
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class SystemNoticeSetDto implements Serializable {

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "发布人不能为空")
    private String publisher;

    @NotBlank(message = "内容不能为空")
    private String content;

    /**
     * 展示时间（前端弹窗显示用）；不传则由后端生成当前时间
     */
    private LocalDateTime createTime;
}


