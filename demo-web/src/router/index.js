import { createRouter, createWebHistory } from 'vue-router'
import Layout from '../components/Layout.vue'

const routes = [
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('../views/Dashboard.vue'), meta: { title: '仪表盘' } },
      { path: 'supply', name: 'Supply', component: () => import('../views/SupplyList.vue'), meta: { title: '办公用品管理' } },
      { path: 'purchase', name: 'Purchase', component: () => import('../views/PurchaseList.vue'), meta: { title: '采购入库' } },
      { path: 'usage', name: 'Usage', component: () => import('../views/UsageList.vue'), meta: { title: '领用出库' } },
      { path: 'inventory', name: 'Inventory', component: () => import('../views/InventoryList.vue'), meta: { title: '当前库存' } },
      { path: 'personnel', name: 'Personnel', component: () => import('../views/PersonnelList.vue'), meta: { title: '人员信息' } },
      { path: 'report', name: 'Report', component: () => import('../views/ExpenseReport.vue'), meta: { title: '费用汇总' } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
