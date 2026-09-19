import { request } from './http'
import type { Order } from '@/types'

export interface CreateOrderParams {
  receiverName: string
  receiverPhone: string
  receiverAddress: string
  cartItemIds: number[]
}

export const createOrder = (data: CreateOrderParams) => request.post<number>('/orders', data)

export const listOrders = () => request.get<Order[]>('/orders')

export const getOrder = (id: number) => request.get<Order>(`/orders/${id}`)

export const payOrder = (id: number) => request.post<void>(`/orders/${id}/pay`)

export const cancelOrder = (id: number) => request.post<void>(`/orders/${id}/cancel`)
