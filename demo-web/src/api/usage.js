import request from './request'

export function getUsageList(params) {
  return request.get('/usage', { params })
}

export function addUsage(data) {
  return request.post('/usage', data)
}

export function deleteUsage(id) {
  return request.delete('/usage/' + id)
}

export function batchDeleteUsage(ids) {
  return request.delete('/usage/batch', { data: ids })
}
