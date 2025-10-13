package com.cn.notice.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 通知VO
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class NoticeVo implements Serializable {

    private String title;

    private String publisher;

    private String content;

    private LocalDateTime createTime;

    private String md5;
}


