import dayjs from 'dayjs'
import { baseUrl, fetch } from './'

const logsUrl = `${baseUrl}/logs`

const getAll = async (before, after, level, regex) => {

  before = after ? null : before
  before ??= new Date()

  const params = new URLSearchParams()

  before && params.append('before', dayjs(before).toISOString().replace('Z', ''))
  after && params.append('after', dayjs(after).toISOString().replace('Z', ''))
  regex && params.append('regex', regex)
  level && level != 'ALL' && params.append('level', level)

  const response = await fetch(
    logsUrl + '?' + params.toString()
  )
  return await response.json()
}

export { getAll }