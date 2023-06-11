import { baseUrl, fetch } from './'

const buildingsUrl = `${baseUrl}/buildings`

const getBuildingTypes = async () => {
  const response = await fetch(buildingsUrl + '/types')
  return await response.json()
}

const getBuildings = async () => {
  const response = await fetch(buildingsUrl)
  return await response.json()
}

const addBuilding = async (name, address, type) => {
  const response = await fetch(buildingsUrl, {
    method: 'POST',
    body: JSON.stringify({ name, address, type })
  })
  return await response.json()
}

const deleteBuilding = async (id) => {
  await fetch(buildingsUrl + `/${id}`, {
    method: 'DELETE'
  })
}

export { getBuildingTypes, getBuildings, addBuilding, deleteBuilding }
