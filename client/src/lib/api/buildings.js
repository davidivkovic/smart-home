import { baseUrl, fetch } from './'

const buildingsUrl = `${baseUrl}/buildings`

const getBuildingTypes = async () => {
  const response = await fetch(buildingsUrl + '/types')
  return await response.json()
}

export { getBuildingTypes }