import { baseUrl, fetch } from './'

const usersUrl = `${baseUrl}/users`

const getAll = (page, query = '') =>
  fetch(usersUrl + '?' + new URLSearchParams({ page, query }).toString())

const deleteOne = (id) => fetch(`${usersUrl}/${id}`, { method: 'DELETE' })

const changeRole = (id, role) =>
  fetch(`${usersUrl}/${id}?role=${role}`, {
    method: 'PUT'
  })

const getAllRoles = async () => {
  const response = await fetch(`${usersUrl}/roles`)
  return await response.json()
}

export { getAll, deleteOne, changeRole, getAllRoles }
