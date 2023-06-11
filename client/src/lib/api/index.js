// import userStore from '$lib/stores/userStore'
import { get } from 'svelte/store'

const baseUrl = 'http://localhost:8080'
const baseUrlDevices = 'http://localhost:8081'

/**
 *
 * @param {RequestInfo | URL} url
 * @param {RequestInit} init
 * @returns
 */
const fetch = async (url, init = {}) => {
  /**
   * @type {import('$lib/stores/userStore')}
   */
  const userStore = await import('$lib/stores/userStore')

  if (init?.body && typeof init.body === 'string') {
    init.headers ??= {}
    init.headers['Content-Type'] ??= 'application/json'
  }

  if (get(userStore.isAuthenticated)) {
    init.headers = { ...init?.headers, Authorization: `Bearer ${userStore.default.getToken()}` }
  }

  let result = await window.fetch(url, {
    ...init,
    credentials: 'include',
    headers: { ...init.headers }
  })

  if (result.status === 401) {
    try {
      const response = await window.fetch(baseUrl + '/auth/token-refresh', { 
        credentials: 'include',
        method: 'POST' 
      })
      if (response.ok) {
        const json = await response.json()
        userStore.default.setToken(json.token)
        result = await window.fetch(url, {
          ...init,
          credentials: 'include',
          headers: { ...init?.headers, Authorization: `Bearer ${userStore.default.getToken()}` },
        })
      }
    }
    catch {
      userStore.default.logout()
    }
  }

  if (!result.ok && result.status !== 403) {
    throw new Error(await result.text())
  }

  return result
}

export { fetch, baseUrl, baseUrlDevices }
