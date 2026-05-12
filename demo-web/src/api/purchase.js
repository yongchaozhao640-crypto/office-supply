import request from './request'

export function getPurchaseList(params) {
  return request.get('/purchase', { params })
}

export function addPurchase(data) {
  return request.post('/purchase', data)
}
