import request from './request'

export function getPersonnelList(params) {
  return request.get('/personnel', { params })
}

export function addPersonnel(data) {
  return request.post('/personnel', data)
}

export function updatePersonnel(data) {
  return request.put('/personnel', data)
}

export function deletePersonnel(id) {
  return request.delete('/personnel/' + id)
}
