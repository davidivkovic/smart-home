import { derived, get, writable } from 'svelte/store'

const userKey = 'authenticated-user'
const tokenKey = 'access-token'

const savedUser = JSON.parse(localStorage.getItem(userKey)) ?? {}

export const user = writable(savedUser)
export const isAuthenticated = derived(user, ($user) => Object.keys($user).length > 0)

const login = (newUser, accessToken) => {
  console.log('login', newUser, accessToken)
  localStorage.setItem(userKey, JSON.stringify(newUser))
  localStorage.setItem(tokenKey, accessToken)
  user.set(newUser)
}

const logout = () => {
  localStorage.removeItem(userKey)
  localStorage.removeItem(tokenKey)
  user.set({})
  console.log(get(user))
  console.log(get(isAuthenticated))
}

const getToken = () => {
  return localStorage.getItem(tokenKey)
}

export default {
  login,
  logout,
  getToken
}
