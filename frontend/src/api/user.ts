import { request } from './http'
import type { UserInfo } from '@/types'

export const getUserInfo = () => request.get<UserInfo>('/user/info')
