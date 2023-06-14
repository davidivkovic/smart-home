import { baseUrlDevices, fetch } from './'

const devicesUrl = `${baseUrlDevices}/devices`

const getDeviceTypes = async () => {
  const response = await fetch(devicesUrl + '/types')
  return await response.json()
}

const addDevice = async (name, brand, typeId, buildingId) => { 
  const response = await fetch(devicesUrl, {
    method: 'POST',
    body: JSON.stringify({ name, brand, typeId, buildingId })
  })
  return await response.json()
}


export { getDeviceTypes, addDevice }