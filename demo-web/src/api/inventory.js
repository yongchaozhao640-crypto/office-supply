import request from './request'

export function getInventoryList() {
  return request.get('/inventory')
}

export function getLowStockCount() {
  return request.get('/inventory/lowStock')
}
