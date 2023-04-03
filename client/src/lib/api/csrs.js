import { baseUrl, fetch } from '.'

const csrsUrl = baseUrl + '/csrs'

const getCsrs = async (page = 1) => {
  const response = await fetch(`${csrsUrl}?page=${page}`)
  return await response.json()
}

const approveCSR = async (csrId, request) => {
  const response = await fetch(`${csrsUrl}/${csrId}/approve`, {
    method: 'POST',
    body: JSON.stringify(request),
    headers: {
      'Content-Type': 'application/json'
    }
  })
  return await response.text()
} 

const rejectCSR = async (csrId, reason) => {
  const response = await fetch(
    `${csrsUrl}/${csrId}/reject?` + new URLSearchParams({ reason }).toString(), 
    {
      method: 'POST',
    }
  )
}

const submitCSR = async PEMCSR => {
  const response = await fetch(csrsUrl, {
    method: 'POST',
    body: PEMCSR,
    headers: {
      'Content-Type': 'text/plain'
    }
  })
  return await response.json()
}

export { getCsrs, approveCSR, submitCSR, rejectCSR }
