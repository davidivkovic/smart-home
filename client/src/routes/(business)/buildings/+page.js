import { getBuildings } from '$lib/api/buildings'

/** 
 * @type { import('./$types').PageLoad }
 */
export async function load({ url }) {
  const buildings = await getBuildings()
  return { buildings }
}