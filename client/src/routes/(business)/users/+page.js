import { getAll } from '$lib/api/users'

/**
 * @type { import('./$types').PageLoad }
 */
export async function load({ url }) {
  const users = await getAll(
    url.searchParams.get('page') ?? 1,
    url.searchParams.get('query')?.trim() ?? ''
  )

  return {
    users
  }
}
