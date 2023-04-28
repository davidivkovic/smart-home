import { baseUrl, fetch } from './'

const usersUrl = `${baseUrl}/users`

const getAll = (page, query = '') =>
  fetch(usersUrl + '?' + new URLSearchParams({ page, query }).toString())

const deleteOne = (id) => fetch(`${usersUrl}/${id}`, { method: 'DELETE' })

const changeRole = (id, role) =>
  fetch(`${usersUrl}/${id}?role=${role}`, {
    method: 'PUT'
  })

export { getAll, deleteOne, changeRole }
