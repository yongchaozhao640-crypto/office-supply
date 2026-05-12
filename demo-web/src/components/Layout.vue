<template>
  <el-container class="layout">
    <el-aside width="220px">
      <div class="logo">
        <el-icon :size="24"><Box /></el-icon>
        <span>办公用品进销存</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF">
        <el-menu-item index="/dashboard">
          <el-icon><DataAnalysis /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>
        <el-menu-item index="/supply">
          <el-icon><Goods /></el-icon>
          <span>办公用品管理</span>
        </el-menu-item>
        <el-menu-item index="/purchase">
          <el-icon><ShoppingCart /></el-icon>
          <span>采购入库</span>
        </el-menu-item>
        <el-menu-item index="/usage">
          <el-icon><Sell /></el-icon>
          <span>领用出库</span>
        </el-menu-item>
        <el-menu-item index="/inventory">
          <el-icon><List /></el-icon>
          <span>当前库存</span>
        </el-menu-item>
        <el-menu-item index="/personnel">
          <el-icon><User /></el-icon>
          <span>人员信息</span>
        </el-menu-item>
        <el-menu-item index="/report">
          <el-icon><TrendCharts /></el-icon>
          <span>费用汇总</span>
        </el-menu-item>
        <el-menu-item index="/users" v-if="user?.role === 'admin'">
          <el-icon><Setting /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header>
        <span class="title">{{ $route.meta.title }}</span>
        <div class="header-right">
          <el-dropdown trigger="click">
            <span class="user-info">
              <el-icon><User /></el-icon>
              {{ user?.name || user?.username }}
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item disabled>
                  角色: {{ user?.role === 'admin' ? '管理员' : '普通用户' }}
                </el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

export default {
  name: 'Layout',
  setup() {
    const route = useRoute()
    const router = useRouter()
    const authStore = useAuthStore()
    const activeMenu = computed(() => route.path)
    const user = computed(() => authStore.currentUser)

    function handleLogout() {
      authStore.logout()
      router.push('/login')
    }

    return { activeMenu, user, handleLogout }
  }
}
</script>

<style>
* { margin: 0; padding: 0; box-sizing: border-box; }
body { font-family: 'Microsoft YaHei', Arial, sans-serif; }
.layout { height: 100vh; }
.el-aside { background: #304156; overflow: hidden; }
.logo {
  height: 60px; display: flex; align-items: center; justify-content: center;
  color: #fff; font-size: 16px; font-weight: bold; gap: 8px;
  border-bottom: 1px solid rgba(255,255,255,0.1);
}
.logo .el-icon { color: #409EFF; }
.el-header {
  background: #fff; display: flex; align-items: center; justify-content: space-between;
  border-bottom: 1px solid #e6e6e6; padding: 0 20px;
}
.el-header .title { font-size: 18px; font-weight: bold; color: #303133; }
.header-right {
  display: flex; align-items: center; gap: 16px; cursor: pointer;
}
.user-info {
  display: flex; align-items: center; gap: 4px;
  color: #303133; font-size: 14px;
}
.el-main { background: #f0f2f5; padding: 20px; }
</style>
