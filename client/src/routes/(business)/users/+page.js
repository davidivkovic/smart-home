import { getAll } from '$lib/api/users'

/** 
 * @type { import('./$types').PageLoad }
 */
export async function load({ url }) {
  const response = await getAll(
    url.searchParams.get('page') ?? 1,
    url.searchParams.get('query')?.trim() ?? ''
  )

  return {
    users: await response.json()
  }
}
