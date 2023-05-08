import { getRefreshTokens, get2faStatus } from '$lib/api/auth'

/**
 * @type { import('./$types').PageLoad }
 */
export async function load() {
  const [refreshTokens, mfaEnabled] = await Promise.all([getRefreshTokens(), get2faStatus()])
  return {
    refreshTokens,
    mfaEnabled
  }
}