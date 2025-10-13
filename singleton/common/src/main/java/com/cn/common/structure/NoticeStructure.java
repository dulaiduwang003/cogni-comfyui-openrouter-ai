package com.cn.common.structure;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Accessors(chain=true)
public class NoticeStructure implements Serializable {

    private String title;

    private String publisher;

    private String content;

    private LocalDateTime createTime;

    private String md5;

}
