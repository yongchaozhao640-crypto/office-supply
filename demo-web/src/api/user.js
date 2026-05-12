import request from './request'

export function getUserList() {
  return request.get('/users')
}

export function addUser(data) {
  return request.post('/users', data)
}

export function updateUser(data) {
  return request.put('/users', data)
}

export function deleteUser(id) {
  return request.delete('/users/' + id)
}
