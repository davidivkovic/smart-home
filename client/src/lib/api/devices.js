import { baseUrlDevices, fetch } from './'

const devicesUrl = `${baseUrlDevices}/devices`

const getDeviceTypes = async () => {
  const response = await fetch(devicesUrl + '/types')
  return await response.json()
}

export { getDeviceTypes }