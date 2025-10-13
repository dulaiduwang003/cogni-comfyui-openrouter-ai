import type { PasswordLoginApi, EmailLoginApi, RegisterApi, ForgotPasswordApi, GetVerificationCodeApi } from './types'
import {  post } from '@/utils/requestUtil'

export const authApi = {
    reqPasswordLogin: (params: PasswordLoginApi.Params) => {
        return post<string>('/auth/password-login', params)
    },
    reqEmailLogin: (params: EmailLoginApi.Params) => {
        return post<string>('/auth/email-login', params)
    },
    reqRegister: (params: RegisterApi.Params) => {
        return post<void>('/auth/register', params)
    },
    reqForgotPassword: (params: ForgotPasswordApi.Params) => {
        return post<void>('/auth/forgot-password', params)
    },
    reqGetVerificationCode: (params: GetVerificationCodeApi.Params) => {
        return post<void>('/auth/get-verification-code', params)
    },
    reqLogout: () => {
        return post<void>('/auth/logout')
    }
}