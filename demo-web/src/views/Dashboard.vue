<template>
  <div>
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-label">库存预警数</div>
          <div class="stat-value danger">{{ stats.lowStockCount ?? 0 }}</div>
          <div class="stat-sub">低于安全库存</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-label">本月采购金额</div>
          <div class="stat-value primary">{{ formatMoney(stats.monthPurchaseAmount) }}</div>
          <div class="stat-sub">{{ currentMonth }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-label">本月领用金额</div>
          <div class="stat-value warning">{{ formatMoney(stats.monthUsageAmount) }}</div>
          <div class="stat-sub">{{ currentMonth }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-label">库存总种类</div>
          <div class="stat-value success">{{ inventoryList.length }}</div>
          <div class="stat-sub">种办公用品</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top:20px">
      <el-col :span="12">
        <el-card header="库存预警列表">
          <el-table :data="lowStockList" size="small" max-height="300">
            <el-table-column prop="supplyName" label="物品名称" />
            <el-table-column prop="currentQty" label="当前库存" width="100" />
            <el-table-column prop="safetyStock" label="安全库存" width="100" />
            <el-table-column label="状态" width="80">
              <template #default="{ row }">
                <el-tag type="danger" size="small">需采购</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card header="库存分布">
          <div ref="chartRef" style="height:300px"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { getDashboardStats } from '../api/dashboard'
import { getInventoryList } from '../api/inventory'
import * as echarts from 'echarts'

export default {
  name: 'Dashboard',
  setup() {
    const stats = reactive({ lowStockCount: 0, monthPurchaseAmount: 0, monthUsageAmount: 0 })
    const inventoryList = ref([])
    const lowStockList = ref([])
    const chartRef = ref(null)

    const now = new Date()
    const currentMonth = now.getFullYear() + '年' + (now.getMonth() + 1) + '月'

    function formatMoney(v) {
      if (v == null) return '¥0'
      return '¥' + Number(v).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
    }

    async function load() {
      const [s, inv] = await Promise.all([getDashboardStats(), getInventoryList()])
      Object.assign(stats, s)
      inventoryList.value = inv || []
      lowStockList.value = (inv || []).filter(i => i.needPurchase === '是' || (i.currentQty > 0 && i.safetyStock > 0 && i.currentQty <= i.safetyStock))
    }

    onMounted(async () => {
      await load()
      await nextTick()
      if (chartRef.value) {
        const chart = echarts.init(chartRef.value)
        const names = inventoryList.value.map(i => i.supplyName).slice(0, 15)
        const values = inventoryList.value.map(i => Number(i.currentAmount || 0)).slice(0, 15)
        chart.setOption({
          tooltip: { trigger: 'item', formatter: '{b}: ¥{c}' },
          series: [{
            type: 'treemap', data: names.map((n, i) => ({ name: n, value: values[i] })),
            label: { show: true, formatter: '{b}' }
          }]
        })
      }
    })

    return { stats, inventoryList, lowStockList, chartRef, currentMonth, formatMoney }
  }
}
</script>

<style scoped>
.stats-row { margin-bottom: 0; }
.stat-card { text-align: center; }
.stat-label { font-size: 14px; color: #909399; margin-bottom: 8px; }
.stat-value { font-size: 32px; font-weight: bold; }
.stat-value.danger { color: #F56C6C; }
.stat-value.primary { color: #409EFF; }
.stat-value.warning { color: #E6A23C; }
.stat-value.success { color: #67C23A; }
.stat-sub { font-size: 12px; color: #C0C4CC; margin-top: 4px; }
</style>
