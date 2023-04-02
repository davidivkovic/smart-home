import { baseUrl } from '.'

const csrsUrl = baseUrl + '/csrs'

const getCsrs = async (page = 1) => {
  const response = await fetch(`${csrsUrl}?page=${page}`)
  return await response.json()
}

export { getCsrs }
