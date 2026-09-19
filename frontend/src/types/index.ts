// 统一返回体
export interface R<T> {
  code: number
  message: string
  data: T
}

// 分页结果
export interface PageResult<T> {
  total: number
  records: T[]
}

// 分类
export interface Category {
  id: number
  name: string
  parentId: number
  sort: number
  status: number
}

// 商品
export interface Product {
  id: number
  categoryId: number
  categoryName: string
  name: string
  subtitle: string
  mainImage: string
  detail: string
  price: number
  stock: number
  sales: number
  status: number
  images: string[]
  createdAt: string
}

// 用户信息
export interface UserInfo {
  id: number
  username: string
  nickname: string
  email: string
  phone: string
  avatar: string
  memberLevel: number
  memberLevelLabel: string
  totalSpent: number
}

// 登录返回
export interface LoginResult {
  token: string
  id: number
  username: string
  nickname: string
  memberLevel: number
  memberLevelLabel: string
  totalSpent: number
}

// 购物车项
export interface CartItem {
  id: number
  productId: number
  productName: string
  productImage: string
  price: number
  stock: number
  quantity: number
  checked: number
}

// 订单明细
export interface OrderItem {
  productId: number
  productName: string
  productImage: string
  price: number
  quantity: number
  totalPrice: number
}

// 订单
export interface Order {
  id: number
  orderNo: string
  totalAmount: number
  status: string
  statusLabel: string
  receiverName: string
  receiverPhone: string
  receiverAddress: string
  payTime: string
  createdAt: string
  items: OrderItem[]
}
