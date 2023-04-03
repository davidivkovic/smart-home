// import userStore from '$lib/stores/userStore'
import { get } from 'svelte/store'

const baseUrl = 'http://localhost:8080'

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

  const result = await window.fetch(url, {
    headers: { ...init.headers },
    ...init
  })

  if (!result.ok) {
    throw new Error(await result.text())
  }

  return result
}

export { fetch, baseUrl }
