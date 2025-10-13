// ComfyUI 表单类型枚举
export enum WorkflowFormTypeEnum {
    IMAGE_UPLOAD = "IMAGE_UPLOAD", //上传图片控件
    AUDIO_UPLOAD = "AUDIO_UPLOAD", //上传音频控件
    VIDEO_UPLOAD = "VIDEO_UPLOAD", //上传视频控件
    RADIO_SELECTOR = "RADIO_SELECTOR", //单选下拉框
    CHECKBOX_SELECTOR = "CHECKBOX_SELECTOR",//多选控件 值使用,分割
    TEXT_PROMPT = "TEXT_PROMPT", //普通文本输入框
    TEXT_CONFIGURABLE="TEXT_CONFIGURABLE"//文本类（需选择具体控件）
}

// ComfyUI 任务状态枚举
export enum WorkflowTaskStatusEnum {
    WAIT = "WAIT", //等待中
    BUILD = "BUILD",//构建中
    SUCCEED = "SUCCEED", //成功
    CANCELED = "CANCELED", //已取消
    FAILED = "FAILED" //失败
}

// ComfyUI 作品类型枚举
export enum WorkflowResultModelTypeEnum {
    AUDIO = "AUDIO",
    VIDEO = "VIDEO", 
    MODEL = "MODEL",
    IMAGE = "IMAGE"
} 

export enum WorkflowResultModelDigitalEnum {
    TEXT = "text",
    MULTI_LINE_PROMPT = "multi_line_prompt",
    RESOLUTION = "resolution",
    IMAGE = "image",
    VIDEO = "video",
    AUDIO = "audio"
}