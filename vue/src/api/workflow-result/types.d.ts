import { WorkflowResultModelTypeEnum } from "@/enums/workflow"


// 基础分页结构
export interface BasePage<T> {
  total: number
  items: T[]
}



// 作品信息
export interface WorkflowResultModelsVo {
  workflowResultId: number // 作品ID
  type: string // 作品类型
  url: string // 作品URL
  workflowName: string // 工作流名称
  taskId: string // 任务ID
  createTime: string // 创建时间
}



// 获取作品分页列表 API
export namespace GetWorkflowResultPageApi {
  export interface Params {
    page?: number // 页码，默认为1
  }
  
  export type Result = BasePage<WorkflowResultModelsVo>
}

// 获取作品详情 API
export namespace GetWorkflowDetailDetailApi {
  export interface Params {
    workflowResultId: number // 作品ID
  }
  
  export type Result = WorkflowResultModelsVo
}

// 删除作品 API
export namespace DeleteWorkflowResultApi {
  export interface Params {
    workflowResultId: number // 作品ID
  }
  
  export type Result = void
}

// 批量删除作品 API
export namespace BatchDeleteWorkflowResultApi {
  export interface Params {
    workflowResultIds: number[] // 作品ID列表
  }
  
  export type Result = void
} 