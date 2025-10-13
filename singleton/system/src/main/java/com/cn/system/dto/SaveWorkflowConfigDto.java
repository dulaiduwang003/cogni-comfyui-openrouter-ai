package com.cn.system.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 保存工作流配置 DTO
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Data
public class SaveWorkflowConfigDto {

    /**
     * 工作流名称
     */
    @NotBlank(message = "工作流名称不能为空")
    private String name;

    /**
     * 工作流描述
     */
    private String description;

    /**
     * 工作流封面图 URL
     */
    private String url;

    /**
     * 原始 ComfyUI JSON（从解析接口返回的 json 字段）
     */
    @NotBlank(message = "工作流 JSON 不能为空")
    private String json;

    /**
     * 工作流类别 ID
     */
    private String workflowCategoryId;

    /**
     * 执行一次扣除的积分
     */
    @NotNull(message = "积分扣除量不能为空")
    private Long creditsDeducted;

    /**
     * 表单节点配置列表
     */
    @Valid
    @NotEmpty(message = "至少需要配置一个表单节点")
    private List<FormNodeConfig> formNodeList;

    /**
     * 输出节点配置列表
     */
    @Valid
    @NotEmpty(message = "至少需要配置一个输出节点")
    private List<OutputNodeConfig> outputNodeList;

    /**
     * 输入节点配置
     */
    @Data
    public static class FormNodeConfig {

        /**
         * ComfyUI 节点 Key
         */
        @NotBlank(message = "节点 Key 不能为空")
        private String nodeKey;

        /**
         * 节点类型（TEXT_PROMPT/RADIO_SELECTOR/CHECKBOX_SELECTOR/IMAGE_UPLOAD/VIDEO_UPLOAD/AUDIO_UPLOAD）
         */
        @NotBlank(message = "节点类型不能为空")
        private String type;

        /**
         * 输入字段名（text/image/video/audio）
         */
        @NotBlank(message = "输入字段名不能为空")
        private String inputs;

        /**
         * 表单提示文本
         */
        @NotBlank(message = "提示文本不能为空")
        private String tips;

        /**
         * 选项列表（JSON 格式，仅 RADIO_SELECTOR 和 CHECKBOX_SELECTOR 需要）
         * 格式：{"key1":"label1", "key2":"label2"}
         */
        private String options;

        /**
         * 默认值模板
         */
        private String template;

        /**
         * 是否必填（true/false）
         */
        @NotNull(message = "required 字段不能为空")
        private Boolean required;

        /**
         * 大小限制
         * - 文本类型：最大字符数
         * - 文件类型：最大文件大小（MB）
         */
        private Long size;
    }

    /**
     * 输出节点配置
     */
    @Data
    public static class OutputNodeConfig {

        /**
         * ComfyUI 节点 Key
         */
        @NotBlank(message = "输出节点 Key 不能为空")
        private String nodeKey;

        /**
         * 输出类型（IMAGE/VIDEO/AUDIO）
         */
        @NotBlank(message = "输出类型不能为空")
        private String type;
    }
}

