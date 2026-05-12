import request from './request'

export function getSupplyList(params) {
  return request.get('/supply', { params })
}

export function addSupply(data) {
  return request.post('/supply', data)
}

export function updateSupply(data) {
  return request.put('/supply', data)
}

export function deleteSupply(id) {
  return request.delete('/supply/' + id)
}

export function batchDeleteSupply(ids) {
  return request.delete('/supply/batch', { data: ids })
}
