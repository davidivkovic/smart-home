import { getAll } from '$lib/api/users'
import { get } from 'svelte/store'
import { currentPage } from './store'

/**
 * @type { import('./$types').PageLoad }
 */
export async function load({ url }) {
  const users = await getAll(get(currentPage), url.searchParams.get('query')?.trim() ?? '')

  return {
    users
  }
}
