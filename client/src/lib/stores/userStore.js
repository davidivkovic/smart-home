import { derived, get, writable } from 'svelte/store'
import { goto } from '$app/navigation'

const userKey = 'authenticated-user'
const tokenKey = 'access-token'
const storage = localStorage

const savedUser = JSON.parse(storage.getItem(userKey)) ?? {}

export const user = writable(savedUser)
export const isAuthenticated = derived(user, ($user) => Object.keys($user).length > 0)
export const isAdmin = derived(user, ($user) => Object.keys($user).length > 0 && $user.role === 'admin')
export const isLandlord = derived(user, ($user) => Object.keys($user).length > 0 && $user.role === 'landlord')

const login = (newUser, accessToken) => {
  storage.setItem(userKey, JSON.stringify(newUser))
  setToken(accessToken)
  user.set(newUser)
}

const logout = async () => {
  storage.removeItem(userKey)
  storage.removeItem(tokenKey)
  user.set({})
  await goto('/')
}

const getToken = () => {
  return storage.getItem(tokenKey)
}

const setToken = (accessToken) => {
  storage.setItem(tokenKey, accessToken)
}

export default {
  login,
  logout,
  getToken,
  setToken
}
