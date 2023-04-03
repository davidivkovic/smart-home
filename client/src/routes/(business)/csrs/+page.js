import { getCsrs } from '$lib/api/csrs'

/** 
 * @type { import('./$types').PageLoad }
 */
export async function load({ url }) {
  const csrs = await getCsrs(url.searchParams.get('page') ?? 1)
  return { csrs }
}