import { defineStore } from 'pinia'
import { ref } from 'vue'
import { listCart } from '@/api/cart'
import { USER_TOKEN_KEY } from '@/api/http'

export const useCartStore = defineStore('cart', () => {
  const count = ref(0)

  // 刷新购物车角标数量（未登录则为 0）
  async function refreshCount() {
    if (!localStorage.getItem(USER_TOKEN_KEY)) {
      count.value = 0
      return
    }
    try {
      const items = await listCart()
      count.value = items.reduce((sum, item) => sum + item.quantity, 0)
    } catch {
      count.value = 0
    }
  }

  return { count, refreshCount }
})
