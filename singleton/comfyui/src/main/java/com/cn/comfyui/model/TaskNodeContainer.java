package com.cn.comfyui.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 任务节点容器
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class TaskNodeContainer {

    private String nodeKey;

    private String inputs;

    private String nodeValue;

    private Boolean isUpload;

    // ========== 元数据字段（用于前端显示和作品记录）==========

    /**
     * 表单提示文字
     */
    private String tips;

    /**
     * 表单类型（TEXT_PROMPT、IMAGE_UPLOAD、RADIO_SELECTOR 等）
     */
    private String type;

    /**
     * 选择器选项（JSON 数组字符串）
     */
    private String options;

}
