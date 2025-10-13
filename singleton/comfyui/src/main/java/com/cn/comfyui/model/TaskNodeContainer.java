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


}
