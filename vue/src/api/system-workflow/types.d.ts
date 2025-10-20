import type { WorkflowFormTypeEnum, WorkflowResultModelTypeEnum } from "@/enums";

export type FormType =
  | 'TEXT_CONFIGURABLE'
  | 'TEXT_PROMPT'
  | 'RADIO_SELECTOR'
  | 'CHECKBOX_SELECTOR'
  | 'IMAGE_CONFIGURABLE'
  | 'IMAGE_UPLOAD'
  | 'IMAGE_SCRIBBLE'
  | 'VIDEO_UPLOAD'
  | 'AUDIO_UPLOAD'

export interface ParsingWorkflowVo {
  json: string
  allNodeList: { nodeKey: string; tips: string | null }[]
  formNodeList: {
    nodeKey: string
    type: FormType
    availableTypes: ('TEXT_PROMPT' | 'RADIO_SELECTOR' | 'CHECKBOX_SELECTOR')[] | null
    nodeDigital: 'text' | 'multi_line_prompt' | 'resolution' | 'image' | 'video' | 'audio'
    tips: string | null
  }[]
}

export interface SaveWorkflowConfigDto {
  name: string
  description?: string
  url?: string
  json: string
  workflowCategoryId?: string
  creditsDeducted: number
  formNodeList: FormNodeConfig[]
  outputNodeList: OutputNodeConfig[]
}

export interface FormNodeConfig {
  nodeKey: string
  type: WorkflowFormTypeEnum
  inputs: string
  tips: string
  options?: string
  template?: string
  required: 0 | 1
  size?: number
}

export interface OutputNodeConfig {
  nodeKey: string
  type: WorkflowResultModelTypeEnum
}

// Page/list types
export interface WorkflowListItem {
  workflowId: number
  name: string
  description?: string
  categoryName: string
  creditsDeducted: number
  url?: string
}

export interface WorkflowPageVo {
  total: number
  items: WorkflowListItem[]
}

// Update workflow dto
export interface UpdateWorkflowDto {
  workflowId: number
  name: string
  workflowCategoryId: number
}

// Delete workflow dto
export interface DeleteWorkflowDto {
  workflowId: number
}

// Category dtos and types
export interface CreateCategoryDto {
  name: string
  url?: string
}

export interface UpdateCategoryDto {
  categoryId: number
  name: string
  url?: string
}

export interface DeleteCategoryDto {
  categoryId: number
}


