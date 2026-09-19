import axios from 'axios'
import { ElMessage } from 'element-plus'

// 用户与管理员 token 分别存储
export const USER_TOKEN_KEY = 'mm_token'
export const ADMIN_TOKEN_KEY = 'mm_admin_token'

interface R<T> {
  code: number
  message: string
  data: T
}

function createInstance(baseURL: string, tokenKey: string) {
  const instance = axios.create({ baseURL, timeout: 15000 })

  // 请求拦截：自动附加 token
  instance.interceptors.request.use((config) => {
    const token = localStorage.getItem(tokenKey)
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  })

  // 响应拦截：统一解包 R，处理错误
  instance.interceptors.response.use(
    (response) => {
      const res = response.data as R<unknown>
      if (res.code !== 0) {
        ElMessage.error(res.message || '请求失败')
        return Promise.reject(new Error(res.message || '请求失败'))
      }
      return res.data as never
    },
    (error) => {
      const status = error.response?.status
      if (status === 401) {
        localStorage.removeItem(tokenKey)
        ElMessage.error('请先登录')
        window.location.href = tokenKey === ADMIN_TOKEN_KEY ? '/admin/login' : '/login'
      } else {
        ElMessage.error('网络异常，请稍后重试')
      }
      return Promise.reject(error)
    }
  )

  return instance
}

// 前台用户请求实例（baseURL /api）
export const http = createInstance('/api', USER_TOKEN_KEY)
// 后台管理员请求实例（baseURL /api/admin）
export const adminHttp = createInstance('/api/admin', ADMIN_TOKEN_KEY)

// 类型化请求助手
export const request = {
  get: <T>(url: string, params?: object) => http.get(url, { params }) as unknown as Promise<T>,
  post: <T>(url: string, data?: object) => http.post(url, data) as unknown as Promise<T>,
  put: <T>(url: string, data?: object) => http.put(url, data) as unknown as Promise<T>,
  del: <T>(url: string) => http.delete(url) as unknown as Promise<T>
}

export const adminRequest = {
  get: <T>(url: string, params?: object) => adminHttp.get(url, { params }) as unknown as Promise<T>,
  post: <T>(url: string, data?: object) => adminHttp.post(url, data) as unknown as Promise<T>,
  put: <T>(url: string, data?: object) => adminHttp.put(url, data) as unknown as Promise<T>,
  del: <T>(url: string) => adminHttp.delete(url) as unknown as Promise<T>
}
