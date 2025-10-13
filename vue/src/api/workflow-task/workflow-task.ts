import type { GetWorkflowPageApi,CancelTaskApi, RemakeTaskApi, GetWorkflowCategoryListApi, GetWorkflowInterfaceApi, SubmitTaskApi, GetTaskProgressPageApi } from './types'
import { get, post } from '@/utils/requestUtil'

export const comfyuiTaskApi = {
   reqGetWorkflowResultModelflowsPage: (params: GetWorkflowPageApi.Params) => {
       return get<GetWorkflowPageApi.Result>('/comfyui/task/get/workflow/page',params)
   },
   reqGetWorkflowResultModelflowFilterList: () => {
       return get<GetWorkflowCategoryListApi.Result[]>('/comfyui/task/get/workflow-category/list')
   },
   reqGetWorkflowResultModelflowsInterface: (params: GetWorkflowInterfaceApi.Params) => {
       return get<GetWorkflowInterfaceApi.Result>('/comfyui/task/get/workflow/interface', params)
   },
   reqSubmitComfyuiTask: (params: SubmitTaskApi.Params) => {
       return post<void>('/comfyui/task/submit/task', params)
   },
   reqGetComfyuiTaskProgressPage: (params: GetTaskProgressPageApi.Params) => {
       return get<GetTaskProgressPageApi.Result>('/comfyui/task/get/task/progress-page', params)
   },
   reqCancelComfyuiTask: (params: CancelTaskApi.Params) => {
       return post<void>('/comfyui/task/cancel/task', params)
   },
   reqRemakeComfyuiTask: (params: RemakeTaskApi.Params) => {
       return post<void>('/comfyui/task/remake/task', params)
   },
   getComfyuiTaskProgressWebsocketUrl: () => {
       return ('/ws/task-progress')
   }
}