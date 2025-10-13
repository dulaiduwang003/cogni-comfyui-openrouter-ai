import { InputTypeEnum, OutputTypeEnum, PaymentModeEnum } from '@/enums/chat'

// 基础分页结构
export interface BasePage<T> {
  total: number
  items: T[]
}

export interface ModelItem {
    // 是否支持深度思考
    supportReasoning: boolean
    // 模型名称
    name: string
    // 模型图标
    icon: string
    // 最大token数(暂时用不到)
    maxTokens: number
    // 输出类型
    outputType:OutputTypeEnum[]
    // 输入类型
    inputType:InputTypeEnum[]
    // 是否支持PDF
    supportPdf:boolean
    // 模型id
    id:string,
    // 是否付费
    paymentMode:PaymentModeEnum
    // 是否支持联网搜索
    supportWeb:boolean
}

export namespace GetModelsPageApi {

    export interface Params {
        page?: number // 页码，默认为1
        size?: number // 每页大小，默认为20
        paymentMode?:PaymentModeEnum,
        inputType?:string, //如果有多个 那么 逗号分隔的输入模态, text,image
        outputType?:string, //如果有多个 那么 逗号分隔的输出模态, text,image
        name?:string
        supportReasoning?:boolean
    }



    export type Result = BasePage<ModelItem>
}

export namespace GetModelsListApi {
    export interface Params {
        paymentMode?:PaymentModeEnum,
        inputType?:string, //如果有多个 那么 逗号分隔的输入模态, text,image
        outputType?:string, //如果有多个 那么 逗号分隔的输出模态, text,image
        name?:string
        supportReasoning?:boolean
    }

    export type Result = ModelItem[]
}


export namespace SubmitMessageApi {

    export interface PdfFileDto {
        url: string
        filename: string
    }

    export interface AudioFileDto {
        url: string
    }

    export interface Params {
        sessionId: string //会话id 时间戳构建
        text: string //聊天文本
        imageUrls?: string[] //图片url
        pdfFiles?: PdfFileDto[]
        audioFiles?: AudioFileDto[]
    }

}

// SSE 聊天流 API
export namespace ChatStreamApi {
    export interface Params {
        sessionId: string
        modelId?: string,
        generateImages?:boolean //是否生成图片,
        enableWebSearch?:boolean //是否启用网络搜索,
    }
}


export namespace DeleteChatSessionApi {
    export interface Params {
        sessionId: string
    }
    export type Result = void
}