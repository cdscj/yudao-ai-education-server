import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import {
  getAccessToken, setAccessToken,
  getRefreshToken, setRefreshToken,
  getUser, setUser, removeTokens
} from '@/utils/auth'
import { authApi } from '@/api/index'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(getAccessToken())
  const refreshToken = ref(getRefreshToken())
  const user = ref(getUser())

  const isLoggedIn = computed(() => !!token.value)

  function saveLoginData(data, extra) {
    token.value = data.accessToken
    refreshToken.value = data.refreshToken
    setAccessToken(data.accessToken)
    setRefreshToken(data.refreshToken)
    user.value = { userId: data.userId, ...extra }
    setUser(user.value)
  }

  async function login(credentials) {
    const res = await authApi.login(credentials)
    if (res.data.code !== 0) throw new Error(res.data.msg)
    saveLoginData(res.data.data, { mobile: credentials.mobile })
    return res.data.data
  }

  async function smsLogin(credentials) {
    const res = await authApi.smsLogin(credentials)
    if (res.data.code !== 0) throw new Error(res.data.msg)
    saveLoginData(res.data.data, { mobile: credentials.mobile })
    return res.data.data
  }

  async function socialLogin(credentials) {
    const res = await authApi.socialLogin(credentials)
    if (res.data.code !== 0) throw new Error(res.data.msg)
    saveLoginData(res.data.data)
    return res.data.data
  }

  function logout() {
    token.value = null
    refreshToken.value = null
    user.value = null
    removeTokens()
  }

  return { token, refreshToken, user, isLoggedIn, login, smsLogin, socialLogin, logout }
})
