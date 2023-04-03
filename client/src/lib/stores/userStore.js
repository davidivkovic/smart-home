import { derived, get, writable } from 'svelte/store'
import { goto } from '$app/navigation'

const userKey = 'authenticated-user'
const tokenKey = 'access-token'
const storage = localStorage

const savedUser = JSON.parse(storage.getItem(userKey)) ?? {}

export const user = writable(savedUser)
export const isAuthenticated = derived(user, ($user) => Object.keys($user).length > 0)
export const isAdmin = derived(user, ($user) => Object.keys($user).length > 0 && $user.role === 'admin')

const login = (newUser, accessToken) => {
  storage.setItem(userKey, JSON.stringify(newUser))
  storage.setItem(tokenKey, accessToken)
  user.set(newUser)
}

const logout = () => {
  storage.removeItem(userKey)
  storage.removeItem(tokenKey)
  user.set({})
  goto('/')
}

const getToken = () => {
  return storage.getItem(tokenKey)
}

export default {
  login,
  logout,
  getToken
}
