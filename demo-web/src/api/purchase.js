import request from './request'

export function getPurchaseList(params) {
  return request.get('/purchase', { params })
}

export function addPurchase(data) {
  return request.post('/purchase', data)
}

export function deletePurchase(id) {
  return request.delete('/purchase/' + id)
}

export function batchDeletePurchase(ids) {
  return request.delete('/purchase/batch', { data: ids })
}
