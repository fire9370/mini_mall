import { request } from './http'
import type { CartItem } from '@/types'

export const listCart = () => request.get<CartItem[]>('/cart')

export const addCart = (data: { productId: number; quantity?: number }) => request.post<void>('/cart', data)

export const updateCart = (id: number, data: { quantity: number }) => request.put<void>(`/cart/${id}`, data)

export const deleteCart = (id: number) => request.del<void>(`/cart/${id}`)

export const toggleCartChecked = (id: number, checked: number) =>
  request.put<void>(`/cart/${id}/checked?checked=${checked}`)
