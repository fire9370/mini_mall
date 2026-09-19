import { request } from './http'
import type { Category, PageResult, Product } from '@/types'

export interface ProductQuery {
  page?: number
  size?: number
  keyword?: string
  categoryId?: number
}

export const listCategories = () => request.get<Category[]>('/categories')

export const pageProducts = (params: ProductQuery) => request.get<PageResult<Product>>('/products', params)

export const getProduct = (id: number) => request.get<Product>(`/products/${id}`)
