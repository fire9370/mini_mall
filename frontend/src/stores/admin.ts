import { defineStore } from 'pinia'
import { ref } from 'vue'
import { adminLogin } from '@/api/admin'
import { ADMIN_TOKEN_KEY } from '@/api/http'

export const useAdminStore = defineStore('admin', () => {
  const token = ref(localStorage.getItem(ADMIN_TOKEN_KEY) || '')
  const name = ref('')

  async function login(username: string, password: string) {
    const result = await adminLogin({ username, password })
    token.value = result.token
    name.value = result.name
    localStorage.setItem(ADMIN_TOKEN_KEY, result.token)
  }

  function logout() {
    token.value = ''
    name.value = ''
    localStorage.removeItem(ADMIN_TOKEN_KEY)
  }

  return { token, name, login, logout }
})
