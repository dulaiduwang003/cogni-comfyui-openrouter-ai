import type {
  ParsingWorkflowVo,
  SaveWorkflowConfigDto,
  WorkflowPageVo,
  UpdateWorkflowDto,
  DeleteWorkflowDto,
  CreateCategoryDto,
  UpdateCategoryDto,
  DeleteCategoryDto
} from './types'
import { post, get } from '@/utils/requestUtil'

export const workflowApi = {
  parseWorkflow: (file: File) => {
    const formData = new FormData()
    formData.append('file', file)
    return post<ParsingWorkflowVo>('/system/workflow/parsing', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  },

  saveWorkflowConfig: (payload: SaveWorkflowConfigDto) => {
    return post<void>('/system/workflow/save', payload)
  },

  // Page list
  getWorkflowPage: (params: { page?: number; size?: number; keyword?: string; categoryId?: number }) => {
    return get<WorkflowPageVo>('/system/workflow/page', params)
  },

  // Update workflow (name & category only)
  updateWorkflow: (payload: UpdateWorkflowDto) => {
    return post<void>('/system/workflow/update', payload)
  },

  // Delete workflow
  deleteWorkflow: (payload: DeleteWorkflowDto) => {
    return post<void>('/system/workflow/delete', payload)
  },

  // Category CRUD
  getCategoryList: () => {
    return get<Array<{ categoryId: number; name: string }>>('/system/workflow/category/list')
  },
  createCategory: (payload: CreateCategoryDto) => {
    return post<number>('/system/workflow/category/create', payload)
  },
  updateCategory: (payload: UpdateCategoryDto) => {
    return post<void>('/system/workflow/category/update', payload)
  },
  deleteCategory: (payload: DeleteCategoryDto) => {
    return post<void>('/system/workflow/category/delete', payload)
  }
}


