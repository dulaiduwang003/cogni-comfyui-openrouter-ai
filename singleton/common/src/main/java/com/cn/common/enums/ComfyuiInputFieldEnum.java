package com.cn.common.enums;

/**
 * ComfyUI 输入字段类型枚举
 * <p>
 * 用于统一管理 ComfyUI 工作流解析时识别的各种输入字段名称。
 * 这些字段名来自 ComfyUI 节点的 inputs 定义。
 * </p>
 *
 * <h3>字段分类</h3>
 * <ul>
 *   <li><b>文本输入类</b>：text, multi_line_prompt, resolution</li>
 *   <li><b>文件上传类</b>：image, video, audio</li>
 * </ul>
 *
 * <h3>使用场景</h3>
 * <pre>
 * // 检查节点是否包含文本输入字段
 * if (inputs.containsKey(ComfyuiInputFieldEnum.TEXT.getFieldName())) {
 *     // 处理文本输入
 * }
 * 
 * // 检查多行文本提示
 * if (inputs.containsKey(ComfyuiInputFieldEnum.MULTI_LINE_PROMPT.getFieldName())) {
 *     // 处理多行文本
 * }
 * </pre>
 *
 * @author system
 * @since 2024-10-11
 * @see ComfyuiFormTypeEnum
 */
public enum ComfyuiInputFieldEnum {
    
    // ==================== 文本输入类字段 ====================
    
    /**
     * 单行文本输入字段
     * <p>常见于：简短的文本提示、参数输入等</p>
     */
    TEXT("text"),
    
    /**
     * 多行文本提示字段
     * <p>常见于：长文本提示词输入</p>
     */
    MULTI_LINE_PROMPT("multi_line_prompt"),
    
    /**
     * 分辨率/尺寸设置字段
     * <p>常见于：图像尺寸选择、分辨率配置等</p>
     */
    RESOLUTION("resolution"),
    
    // ==================== 文件上传类字段 ====================
    
    /**
     * 图片上传字段
     * <p>常见于：图片输入节点</p>
     */
    IMAGE("image"),
    
    /**
     * 视频上传字段
     * <p>常见于：视频输入节点</p>
     */
    VIDEO("video"),
    
    /**
     * 音频上传字段
     * <p>常见于：音频输入节点</p>
     */
    AUDIO("audio");
    
    /**
     * 字段名称（与 ComfyUI 节点定义一致）
     */
    private final String fieldName;
    
    /**
     * 构造函数
     *
     * @param fieldName 字段名称
     */
    ComfyuiInputFieldEnum(String fieldName) {
        this.fieldName = fieldName;
    }
    
    /**
     * 获取字段名称
     *
     * @return 字段名称字符串
     */
    public String getFieldName() {
        return fieldName;
    }
    
    /**
     * 根据字段名称查找对应的枚举
     *
     * @param fieldName 字段名称
     * @return 对应的枚举，如果未找到则返回 null
     */
    public static ComfyuiInputFieldEnum fromFieldName(String fieldName) {
        if (fieldName == null) {
            return null;
        }
        for (ComfyuiInputFieldEnum field : values()) {
            if (field.fieldName.equals(fieldName)) {
                return field;
            }
        }
        return null;
    }
    
    /**
     * 判断是否为文本类字段
     *
     * @return 如果是文本类字段返回 true，否则返回 false
     */
    public boolean isTextInput() {
        return this == TEXT || this == MULTI_LINE_PROMPT || this == RESOLUTION;
    }
    
    /**
     * 判断是否为文件上传类字段
     *
     * @return 如果是文件上传类字段返回 true，否则返回 false
     */
    public boolean isFileUpload() {
        return this == IMAGE || this == VIDEO || this == AUDIO;
    }
}

