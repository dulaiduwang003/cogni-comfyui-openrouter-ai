package com.cn.system.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 工作流 JSON 解析结果 VO
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class ParsingWorkflowVo implements Serializable {

    /**
     * 原始 JSON 字符串
     */
    private String json;

    /**
     * 所有节点的基础信息列表（用于展示工作流的所有节点）
     */
    private List<AllNode> allNodeList;

    /**
     * 可识别的表单节点列表（可以自动生成表单的节点）
     */
    private List<FormNode> formNodeList;

    /**
     * 输入节点基础信息（所有节点的通用信息）
     */
    @Data
    @Accessors(chain = true)
    public static class AllNode implements Serializable {
        
        /**
         * 节点 Key（ComfyUI 节点 ID）
         */
        private String nodeKey;
        
        /**
         * 节点标题（从 _meta.title 提取，用作表单标签）
         */
        private String tips;
    }

    /**
     * 可识别的表单节点详细信息（已识别类型的输入节点）
     */
    @Data
    @Accessors(chain = true)
    public static class FormNode implements Serializable {
        
        /**
         * 节点 Key（ComfyUI 节点 ID）
         */
        private String nodeKey;
        
        /**
         * 节点类型（TEXT_CONFIGURABLE/IMAGE_UPLOAD/VIDEO_UPLOAD/AUDIO_UPLOAD）
         */
        private String type;
        
        /**
         * 可选的具体类型列表（仅 TEXT_CONFIGURABLE 有值）
         */
        private List<String> availableTypes;
        
        /**
         * 节点输入字段名（text/image/video/audio 等）
         */
        private String nodeDigital;
        
        /**
         * 节点标题（从 _meta.title 提取，用作表单标签）
         */
        private String tips;
    }
}

