<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <div style="display:flex;align-items:center;gap:12px">
            <span>当前库存（共 {{ tableData.length }} 种物品）</span>
            <el-input v-model="searchName" placeholder="搜索物品名称" clearable style="width:220px" @clear="searchName=''" />
            <el-select v-model="statusFilter" placeholder="采购状态" clearable style="width:130px">
              <el-option label="需采购" value="low" />
              <el-option label="正常" value="normal" />
            </el-select>
            <el-tag type="danger">预警: {{ lowCount }} 种需采购</el-tag>
          </div>
        </div>
      </template>
      <el-table :data="tableData" stripe :row-class-name="rowClass">
        <el-table-column prop="supplyNo" label="编号" width="70" />
        <el-table-column prop="supplyName" label="物品名称" min-width="160" />
        <el-table-column prop="unit" label="单位" width="70" />
        <el-table-column prop="unitPrice" label="单价" width="90">
          <template #default="{ row }">¥{{ row.unitPrice }}</template>
        </el-table-column>
        <el-table-column prop="currentQty" label="当前库存" width="100">
          <template #default="{ row }">
            <span :class="{ 'low-stock': (Number(row.safetyStock)||0) > 0 && (Number(row.currentQty)||0) <= (Number(row.safetyStock)||0) }">
              {{ row.currentQty }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="currentAmount" label="库存金额" width="120">
          <template #default="{ row }">¥{{ Number(row.currentAmount || 0).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="safetyStock" label="安全库存" width="100" />
        <el-table-column label="采购建议" width="100">
          <template #default="{ row }">
            <el-tag v-if="(Number(row.safetyStock)||0) > 0 && (Number(row.currentQty)||0) <= (Number(row.safetyStock)||0)" type="danger" size="small">需采购</el-tag>
            <el-tag v-else-if="row.needPurchase === '是'" type="warning" size="small">建议采购</el-tag>
            <el-tag v-else type="success" size="small">正常</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { getInventoryList } from '../api/inventory'

export default {
  name: 'InventoryList',
  setup() {
    const allData = ref([])
    const searchName = ref('')
    const statusFilter = ref('')
    const lowCount = ref(0)

    function isLow(item) {
      const qty = Number(item.currentQty) || 0
      const safety = Number(item.safetyStock) || 0
      return safety > 0 && qty <= safety
    }

    const tableData = computed(() => {
      let list = allData.value || []
      if (searchName.value) {
        const kw = searchName.value.toLowerCase()
        list = list.filter(i => i.supplyName && i.supplyName.toLowerCase().includes(kw))
      }
      if (statusFilter.value === 'low') {
        list = list.filter(i => isLow(i))
      } else if (statusFilter.value === 'normal') {
        list = list.filter(i => !isLow(i))
      }
      // sort: low stock first, then by supplyNo
      return [...list].sort((a, b) => {
        const aLow = isLow(a) ? 1 : 0
        const bLow = isLow(b) ? 1 : 0
        if (aLow !== bLow) return bLow - aLow
        return (a.supplyNo || 0) - (b.supplyNo || 0)
      })
    })

    function rowClass({ row }) {
      return isLow(row) ? 'warning-row' : ''
    }

    async function load() {
      const data = await getInventoryList()
      allData.value = data || []
      lowCount.value = (data || []).filter(i => isLow(i)).length
    }

    onMounted(load)
    return { tableData, lowCount, searchName, statusFilter, rowClass }
  }
}
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.low-stock { color: #F56C6C; font-weight: bold; }
:deep(.warning-row) { background-color: #fef0f0 !important; }
</style>
