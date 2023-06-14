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

const setTenants = async (id, tenantIds) => {
  await fetch(
    buildingsUrl +
      `/${id}/tenants?${new URLSearchParams(tenantIds.map((id) => ['tenantIds', id])).toString()}`,
    {
      method: 'POST'
    }
  )
}

const getDevices = async (id) => {
  const response = await fetch(buildingsUrl + `/${id}/devices`)
  return await response.json()
}

export { getBuildingTypes, getBuildings, addBuilding, deleteBuilding, setTenants, getDevices }
