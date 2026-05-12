<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>费用汇总查询</span>
          <div>
            <el-date-picker v-model="dateRange" type="daterange" range-separator="至"
              start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD"
              style="margin-right:12px" />
            <el-button type="primary" @click="load">查询</el-button>
          </div>
        </div>
      </template>
      <el-row :gutter="20" style="margin-bottom:20px">
        <el-col :span="8">
          <el-statistic title="采购总金额" :value="summary.purchaseTotal" prefix="¥">
            <template #suffix><span style="font-size:14px">/{{ summary.purchaseCount }}笔</span></template>
          </el-statistic>
        </el-col>
        <el-col :span="8">
          <el-statistic title="领用总金额" :value="summary.usageTotal" prefix="¥">
            <template #suffix><span style="font-size:14px">/{{ summary.usageCount }}笔</span></template>
          </el-statistic>
        </el-col>
        <el-col :span="8">
          <el-statistic title="净增库存金额" :value="summary.netAmount" prefix="¥" />
        </el-col>
      </el-row>
      <el-table :data="tableData" stripe>
        <el-table-column prop="supplyName" label="物品名称" min-width="160" />
        <el-table-column prop="purchaseQty" label="采购数量" width="100" />
        <el-table-column prop="purchaseAmount" label="采购金额" width="120">
          <template #default="{ row }">¥{{ Number(row.purchaseAmount || 0).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="usageQty" label="领用数量" width="100" />
        <el-table-column prop="usageAmount" label="领用金额" width="120">
          <template #default="{ row }">¥{{ Number(row.usageAmount || 0).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column label="净额" width="120">
          <template #default="{ row }">
            ¥{{ (Number(row.purchaseAmount || 0) - Number(row.usageAmount || 0)).toFixed(2) }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { getPurchaseList } from '../api/purchase'
import { getUsageList } from '../api/usage'

export default {
  name: 'ExpenseReport',
  setup() {
    const dateRange = ref([])
    const tableData = ref([])
    const summary = reactive({ purchaseTotal: 0, purchaseCount: 0, usageTotal: 0, usageCount: 0, netAmount: 0 })

    async function load() {
      const [purRes, useRes] = await Promise.all([
        getPurchaseList({ pageNum: 1, pageSize: 9999 }),
        getUsageList({ pageNum: 1, pageSize: 9999 })
      ])

      const purRecords = purRes.records || []
      const useRecords = useRes.records || []

      // Filter by date range if set
      let pFiltered = purRecords
      let uFiltered = useRecords
      if (dateRange.value && dateRange.value.length === 2) {
        const [start, end] = dateRange.value
        pFiltered = purRecords.filter(r => r.purchaseDate >= start && r.purchaseDate <= end)
        uFiltered = useRecords.filter(r => r.usageDate >= start && r.usageDate <= end)
      }

      const map = {}
      pFiltered.forEach(r => {
        const key = r.supplyName
        if (!map[key]) map[key] = { supplyName: key, purchaseQty: 0, purchaseAmount: 0, usageQty: 0, usageAmount: 0 }
        map[key].purchaseQty += Number(r.quantity || 0)
        map[key].purchaseAmount += Number(r.amount || 0)
      })
      uFiltered.forEach(r => {
        const key = r.supplyName
        if (!map[key]) map[key] = { supplyName: key, purchaseQty: 0, purchaseAmount: 0, usageQty: 0, usageAmount: 0 }
        map[key].usageQty += Number(r.quantity || 0)
        map[key].usageAmount += Number(r.amount || 0)
      })

      tableData.value = Object.values(map)
      summary.purchaseCount = pFiltered.length
      summary.purchaseTotal = pFiltered.reduce((s, r) => s + Number(r.amount || 0), 0).toFixed(2)
      summary.usageCount = uFiltered.length
      summary.usageTotal = uFiltered.reduce((s, r) => s + Number(r.amount || 0), 0).toFixed(2)
      summary.netAmount = (Number(summary.purchaseTotal) - Number(summary.usageTotal)).toFixed(2)
    }

    onMounted(load)
    return { dateRange, tableData, summary, load }
  }
}
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
