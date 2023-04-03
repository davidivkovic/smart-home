import { getAll } from '$lib/api/certificates'

/** 
 * @type { import('./$types').PageLoad }
 */
export async function load({ url }) {
  const response = await getAll(url.searchParams.get('page') ?? 1)

  return {
    certificates: await response.json()
  }
}