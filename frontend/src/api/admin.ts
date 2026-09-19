import { adminHttp, adminRequest } from './http'
import type { Category, Order, PageResult, Product } from '@/types'

// 管理员登录返回
export interface AdminLoginResult {
  token: string
  id: number
  username: string
  name: string
}

// 商品表单
export interface ProductForm {
  categoryId: number
  name: string
  subtitle?: string
  mainImage?: string
  detail?: string
  price: number
  stock: number
  status: number
  images?: string[]
}

export const adminLogin = (data: { username: string; password: string }) =>
  adminRequest.post<AdminLoginResult>('/login', data)

// 分类管理
export const listCategories = () => adminRequest.get<Category[]>('/categories')
export const createCategory = (data: { name: string; parentId?: number; sort?: number; status?: number }) =>
  adminRequest.post<void>('/categories', data)
export const updateCategory = (id: number, data: { name: string; parentId?: number; sort?: number; status?: number }) =>
  adminRequest.put<void>(`/categories/${id}`, data)
export const deleteCategory = (id: number) => adminRequest.del<void>(`/categories/${id}`)

// 商品管理
export const pageProducts = (params: { page?: number; size?: number; keyword?: string; categoryId?: number }) =>
  adminRequest.get<PageResult<Product>>('/products', params)
export const createProduct = (data: ProductForm) => adminRequest.post<void>('/products', data)
export const updateProduct = (id: number, data: ProductForm) => adminRequest.put<void>(`/products/${id}`, data)
export const deleteProduct = (id: number) => adminRequest.del<void>(`/products/${id}`)
export const updateProductStatus = (id: number, status: number) =>
  adminRequest.put<void>(`/products/${id}/status?status=${status}`)

// 图片上传
export const uploadImage = (file: File) => {
  const formData = new FormData()
  formData.append('file', file)
  return adminHttp.post<string>('/upload', formData, { headers: { 'Content-Type': 'multipart/form-data' } }) as unknown as Promise<string>
}

// 订单管理
export const pageOrders = (params: { page?: number; size?: number; status?: string }) =>
  adminRequest.get<PageResult<Order>>('/orders', params)
export const getOrder = (id: number) => adminRequest.get<Order>(`/orders/${id}`)
export const shipOrder = (id: number) => adminRequest.put<void>(`/orders/${id}/ship`)
