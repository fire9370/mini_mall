import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as loginApi } from '@/api/auth'
import { getUserInfo } from '@/api/user'
import { USER_TOKEN_KEY } from '@/api/http'
import type { UserInfo } from '@/types'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem(USER_TOKEN_KEY) || '')
  const user = ref<UserInfo | null>(null)

  async function login(username: string, password: string) {
    const result = await loginApi({ username, password })
    token.value = result.token
    localStorage.setItem(USER_TOKEN_KEY, result.token)
    await fetchUser()
  }

  async function fetchUser() {
    user.value = await getUserInfo()
  }

  function logout() {
    token.value = ''
    user.value = null
    localStorage.removeItem(USER_TOKEN_KEY)
  }

  return { token, user, login, fetchUser, logout }
})
