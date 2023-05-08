import { baseUrl, fetch } from '.'

const authUrl = baseUrl + '/auth'

const login = async (email, password, MFACode = null) => {
  const result = await fetch(`${authUrl}/login`, {
    method: 'POST',
    body: JSON.stringify({ email, password, MFACode })
  })

  const requires2FA = result.status === 403

  if (!requires2FA) {
    const json = await result.json()
    const userStore = await import('$lib/stores/userStore').then((m) => m.default)
    userStore.login(json.user, json.token)
  }

  return requires2FA
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

const confirm2fa = async (code) => {
  await fetch(`${authUrl}/2fa/confirm?${new URLSearchParams({ code }).toString()}`, {
    method: 'POST'
  })
}

const disable2fa = async (code) => {
  await fetch(`${authUrl}/2fa/disable?${new URLSearchParams({ code }).toString()}`, {
    method: 'POST'
  })
}

const get2faStatus = async () => {
  const response = await fetch(`${authUrl}/2fa/status`)
  return await response.json()
}

const getRefreshTokens = async () => {
  const data = await fetch(`${authUrl}/refresh-tokens`)
  return await data.json()
}

const revokeRefreshToken = async (id) => {
  await fetch(`${authUrl}/refresh-tokens/${id}/revoke`, { method: 'POST' })
}

export {
  login,
  signup,
  resendConfirmation,
  confirmEmail,
  add2fa,
  confirm2fa,
  getRefreshTokens,
  get2faStatus,
  disable2fa,
  revokeRefreshToken
}
