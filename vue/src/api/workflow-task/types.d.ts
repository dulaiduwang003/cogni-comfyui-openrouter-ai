import { WorkflowFormTypeEnum, WorkflowTaskStatusEnum, WorkflowResultModelTypeEnum }  from '@/enums/workflow'
import { WebSocketMessageTypeEnum } from '@/enums/websocket'

export namespace GetWorkflowPageApi {

   interface Item {
        workflowId: number //工作流ID
        name: string //工作流名称
        description: string //工作流介绍
        url: string //工作流封面
        categoryName: string //所属类别名称
    }

    export interface Result {
        total: number
        items: Item[]
    }
    export interface Params {
        page: string
        prompt?: string //搜索
        categoryId?: number //所属类别
    }

    
}



export namespace GetWorkflowCategoryListApi {

     export interface Result {
        categoryId: number
        name: string
     }
 }


export namespace GetWorkflowInterfaceApi {

    interface FormContainer {
        inputs: string //提交时有用 页面无需渲染
        nodeKey: string //提交时有用 页面无需渲染
        tips: string //控件提示
        type: WorkflowFormTypeEnum //控件类型
        required: boolean //该控件是否必填?
        size: number //如果是 输入类型组件 则代表这个输入类型组件maxlength 如果是 上传类型组件 则代表这个上传类型组件maxsize 其它情况下 为空不必理会
        options: string //如果是 RADIO_SELECTOR CHECKBOX_SELECTOR 就会出现如 [{"key": "女孩风格", "value": "1girl"}] 其它情况下 为空
    }

    export interface Result {
        workflowId: number //工作流id
        name: string //工作流名称
        formContainer: FormContainer[] //工作流表单
        creditsDeducted:number //扣除的积分
    }

    export interface Params {
        workflowId: number
    }
}

export namespace SubmitTaskApi {

    interface NodeContainer {
        nodeKey: string //节点键
        inputs: string //输入类型
        nodeValue: string //节点值
        isUpload: boolean //是否为上传类型
    }

    export interface Params {
        workflowId: number //工作流ID
        nodeContainer: NodeContainer[] //节点容器
    }


}

export namespace CancelTaskApi {

    export interface Params {
        taskId: string
    }
}

export namespace RemakeTaskApi {

    export interface Params {
        taskId: string
    }
}

export namespace GetTaskProgressPageApi {

    interface WorkflowResultModel {
        url: string //作品URL
        type: WorkflowResultModelTypeEnum //作品类型
        workflowResultId: number //作品ID
    }

    interface Item {
        taskId: string //任务ID
        workflowName: string //工作流名称
        progress: number //进度
        workflowResultModel?: WorkflowResultModel //ComfyUI作品信息，只有成功状态才存在
        status: WorkflowTaskStatusEnum //状态
        location: number //位置
        createTime: string //创建时间
    }

    export interface Result {
        total: number
        items: Item[]
    }

    export interface Params {
        status?: WorkflowTaskStatusEnum //状态
        page: number //页码，默认为1
    }
}

// WebSocket相关类型定义
export namespace WebSocketMessageApi {
    
    // ComfyUI作品信息
    interface WorkflowResultModel {
        url: string
        type: WorkflowResultModelTypeEnum
        workflowResultId: number
    }
    
    // 任务状态变化数据
    interface TaskStatusChangeData {
        taskId: string
        workflowName: string
        progress: number
        workflowResultModel?: WorkflowResultModel // 成功时才有这个字段
        status: WorkflowTaskStatusEnum
        createTime: number
    }
    
    // 任务进度更新数据
    interface TaskProgressUpdateData {
        taskId: string
        progress: number
        createTime: number
    }
    
    // 队列位置更新数据
    interface QueuePositionUpdateData {
        taskId: string
        position: number
        createTime: number
    }
    
    // 连接确认数据
    interface ConnectionAckData {
        message: string
        timestamp: number
    }
    
    // 错误消息数据
    interface ErrorData {
        message: string
        code?: string
        timestamp: number
    }
    
    // WebSocket消息基础结构
    export interface BaseMessage {
        type: WebSocketMessageTypeEnum
        timestamp: number
        data: string // 后端返回的data是JSON字符串
    }
    
    // 解析后的消息数据类型
    export type MessageData = 
        | TaskStatusChangeData 
        | TaskProgressUpdateData 
        | QueuePositionUpdateData 
        | ConnectionAckData 
        | ErrorData
    
    // 完整的WebSocket消息
    export interface Message extends BaseMessage {
        parsedData?: MessageData
    }
}

