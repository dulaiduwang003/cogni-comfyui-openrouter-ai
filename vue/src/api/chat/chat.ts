import type { GetModelsPageApi, GetModelsListApi, SubmitMessageApi, ChatStreamApi, DeleteChatSessionApi,ModelItem } from './types'
import { get, post } from '@/utils/requestUtil'

export const chatApi = {
    // 获取模型列表(分页)
   reqGetModelsPage: (params: GetModelsPageApi.Params) => {
       return post<GetModelsPageApi.Result>('/llm/get/available-model/page', params)
   },
   // 获取自动模型 
   reqGetDefaultModel:()=>{
    return get<ModelItem>('/llm/get/default-model')
   },
   // 获取模型列表(不分页)
   reqGetModelsList: (params: GetModelsListApi.Params) => {
    return post<GetModelsListApi.Result>('/llm/get/available-model/list', params)
    },
    // 提交消息
    reqSubmitMessage: (params: SubmitMessageApi.Params) => {
        return post<void>('/llm/chat/submit', params)
    },
    // 删除会话
    reqDeleteChatSession: (params: DeleteChatSessionApi.Params) => {
        return post<void>('/llm/chat/session/delete', params)
    },
    // 建立SSE聊天流连接
    reqChatStream: (params: ChatStreamApi.Params) => {
        const { sessionId, modelId, enableWebSearch, generateImages } = params
        const q = new URLSearchParams()
        q.set('sessionId', sessionId)
        if (modelId) q.set('modelId', modelId)
        if (enableWebSearch !== undefined) q.set('enableWebSearch', String(enableWebSearch))
        if (generateImages !== undefined) q.set('generateImages', String(generateImages))
        
        // SSE 无法设置请求头，需要通过 URL 参数传递 token
        const token = localStorage.getItem('token')
        if (token) {
            q.set('token', token)
        }
        
        const url = `${import.meta.env.VITE_API_BASE_URL}/llm/chat/stream?${q.toString()}`
        const eventSource = new EventSource(url)
        
        return eventSource
    },

}
