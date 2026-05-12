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
      </el-menu>
    </el-aside>
    <el-container>
      <el-header>
        <span class="title">{{ $route.meta.title }}</span>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

export default {
  name: 'Layout',
  setup() {
    const route = useRoute()
    const activeMenu = computed(() => route.path)
    return { activeMenu }
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
  background: #fff; display: flex; align-items: center;
  border-bottom: 1px solid #e6e6e6; padding: 0 20px;
}
.el-header .title { font-size: 18px; font-weight: bold; color: #303133; }
.el-main { background: #f0f2f5; padding: 20px; }
</style>
