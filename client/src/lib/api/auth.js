import { baseUrl, fetch } from '.'
import userStore from '$lib/stores/userStore'

const authUrl = `${baseUrl}/auth`

const login = async (email, password) => {
  const result = await fetch(`${authUrl}/login`, {
    method: 'POST',
    body: JSON.stringify({ email, password })
  })

  const json = await result.json()
  userStore.login(json.user, json.token)
}

const signup = async ({ email, password, firstName, lastName }) =>
  fetch(`${authUrl}/register`, {
    method: 'POST',
    body: JSON.stringify({ email, password, firstName, lastName })
  })

const resendConfirmation = async (email) =>
  fetch(`${authUrl}/resend-confirmation?${new URLSearchParams({ email }).toString()}`, {
    method: 'POST'
  })

const confirmEmail = async ({ email, token }) =>
  fetch(`${authUrl}/confirm?${new URLSearchParams({ email, token }).toString()}`, {
    method: 'POST'
  })

export { login, signup, resendConfirmation, confirmEmail }
