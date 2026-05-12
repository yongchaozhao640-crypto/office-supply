import request from './request'

export function getUsageList(params) {
  return request.get('/usage', { params })
}

export function addUsage(data) {
  return request.post('/usage', data)
}
