import { createRouter, createWebHistory } from 'vue-router'
import Layout from '../components/Layout.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { title: '登录' }
  },
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
      { path: 'report', name: 'Report', component: () => import('../views/ExpenseReport.vue'), meta: { title: '费用汇总' } },
      { path: 'users', name: 'Users', component: () => import('../views/UserList.vue'), meta: { title: '用户管理' } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router
