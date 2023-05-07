import { baseUrl, fetch } from '.'
import userStore from '$lib/stores/userStore'
import { data } from 'autoprefixer'

const authUrl = baseUrl + '/auth'

const login = async (email, password) => {
  const result = await fetch(`${authUrl}/login`, {
    method: 'POST',
    body: JSON.stringify({ email, password })
  })

  const json = await result.json()
  userStore.login(json.user, json.token)
}

const signup = ({ email, password, firstName, lastName }) =>
  fetch(`${authUrl}/register`, {
    method: 'POST',
    body: JSON.stringify({ email, password, firstName, lastName })
  })

const resendConfirmation = (email) =>
  fetch(`${authUrl}/resend-confirmation?${new URLSearchParams({ email }).toString()}`, {
    method: 'POST'
  })

const confirmEmail = ({ email, token }) =>
  fetch(`${authUrl}/confirm?${new URLSearchParams({ email, token }).toString()}`, {
    method: 'POST'
  })

const add2fa = async () => {
  const data = await fetch(`${authUrl}/2fa/add`, { method: 'POST' })
  return await data.json()
}

const confirm2fa = (code) =>
  fetch(`${authUrl}/2fa/confirm?${new URLSearchParams({ code }).toString()}`, {
    method: 'POST'
  })

export { login, signup, resendConfirmation, confirmEmail, add2fa, confirm2fa }
