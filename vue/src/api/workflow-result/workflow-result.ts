import type { GetWorkflowResultPageApi, GetWorkflowDetailDetailApi, DeleteWorkflowResultApi, BatchDeleteWorkflowResultApi } from './types'
import { get, post } from '@/utils/requestUtil'

export const WorkflowResultModelApi = {
  /**
   * 获取作品分页列表
   * @param params 请求参数
   * @returns 作品分页列表
   */
  reqGetWorkflowResultPage: (params: GetWorkflowResultPageApi.Params = { page: 1 }) => {
    return get<GetWorkflowResultPageApi.Result>('/comfyui/result/get/workflow-result/page', params)
  },

  /**
   * 获取作品详情
   * @param params 请求参数
   * @returns 作品详情
   */
  reqGWorkflowResultDetail: (params: GetWorkflowDetailDetailApi.Params) => {
    return get<GetWorkflowDetailDetailApi.Result>('/comfyui/result/get/workflow-result/detail', params)
  },

  /**
   * 删除作品
   * @param params 请求参数
   * @returns 删除结果
   */
  reqDeleteWorkflowResult: (params: DeleteWorkflowResultApi.Params) => {
      return post<DeleteWorkflowResultApi.Result>('/comfyui/result/delete/workflow-result', params)
    },

  /**
   * 批量删除作品
   * @param params 请求参数
   * @returns 删除结果
   */
  reqBatchDeleteWorkflowResult: (params: BatchDeleteWorkflowResultApi.Params) => {
      return post<BatchDeleteWorkflowResultApi.Result>('/comfyui/result/batch-delete/workflow-result', params)
    }
} 