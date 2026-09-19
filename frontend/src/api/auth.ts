import { request } from './http'
import type { LoginResult } from '@/types'

export interface LoginParams {
  username: string
  password: string
}

export interface RegisterParams {
  username: string
  password: string
  nickname?: string
}

export const login = (data: LoginParams) => request.post<LoginResult>('/auth/login', data)
export const register = (data: RegisterParams) => request.post<void>('/auth/register', data)
