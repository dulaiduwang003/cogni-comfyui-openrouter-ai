import type { UploadFileApi } from './types'
import { post } from '@/utils/requestUtil'

export const ossApi = {
    /**
     * 上传文件
     * @param params 包含文件的参数对象
     * @returns Promise<string> 返回文件URL或文件路径
     */
    uploadFile: (params: UploadFileApi.Params) => {
        const formData = new FormData()
        formData.append('file', params.file)
        
        return post<string>('/oss/upload/file', formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        })
    }
} 