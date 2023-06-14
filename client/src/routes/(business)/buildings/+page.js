import { getBuildings, getDevices } from '$lib/api/buildings'

/**
 * @type { import('./$types').PageLoad }
 */
export async function load() {
  const buildings = await getBuildings()
  await Promise.all(
    buildings.map(async (building) => {
      building.devices = await getDevices(building.id)
    })
  )
  return { buildings }
}
