import { baseUrl, fetch } from './'

const certsUrl = `${baseUrl}/certs`

const getAll = (page, storeType = 'keystore') => 
  fetch(certsUrl + '?' + new URLSearchParams({ page, storeType }).toString())

const getCA = async () => { 
  const response = await fetch(certsUrl + '/ca')
  return await response.json()
}

const getByAlias = async alias => {
  const response = await fetch(certsUrl + '/' + alias)
  return await response.text()
}

const checkValidity = async PEMChain => {
  const response = await fetch(certsUrl + '/check-validity', {
    method: 'POST',
    body: PEMChain,
    headers: {
      'Content-Type': 'text/plain'
    }
  })
  return await response.json()
}

const revoke = async (serialNumber, reason) => {
  await fetch(
    certsUrl + `/${serialNumber}/revoke?reason=${reason}`, 
    {
      method: 'POST',
    }
  )
}

export {
  getAll,
  getCA,
  getByAlias,
  checkValidity,
  revoke
}