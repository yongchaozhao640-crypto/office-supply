<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <div style="display:flex;align-items:center;gap:8px;flex-wrap:wrap">
            <span>采购记录</span>
            <el-select v-model="filterYear" placeholder="年份" clearable style="width:100px">
              <el-option v-for="y in years" :key="y" :label="y" :value="y" />
            </el-select>
            <el-select v-model="filterMonth" placeholder="月份" clearable style="width:100px">
              <el-option v-for="m in 12" :key="m" :label="m+'月'" :value="m" />
            </el-select>
            <el-input v-model="searchPurchaser" placeholder="采购人" clearable style="width:130px" @clear="load" @keyup.enter="load" />
            <el-input v-model="searchSupply" placeholder="物品名称" clearable style="width:150px" @clear="load" @keyup.enter="load" />
            <el-button type="primary" @click="load">搜索</el-button>
          </div>
          <el-button type="primary" @click="dialogVisible = true">新增采购</el-button>
        </div>
      </template>
      <el-table :data="tableData" stripe>
        <el-table-column prop="purchaseNo" label="NO" width="70" />
        <el-table-column prop="purchaseDate" label="日期" width="110">
          <template #default="{ row }">{{ formatDate(row.purchaseDate) }}</template>
        </el-table-column>
        <el-table-column prop="purchaser" label="采购人" width="100" />
        <el-table-column prop="supplyName" label="物品名称" min-width="140" />
        <el-table-column prop="unit" label="单位" width="70" />
        <el-table-column prop="unitPrice" label="单价" width="80">
          <template #default="{ row }">¥{{ row.unitPrice }}</template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column prop="amount" label="金额" width="100">
          <template #default="{ row }">¥{{ row.amount }}</template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="120" />
      </el-table>
      <div style="margin-top:12px;text-align:right">
        <el-pagination
          v-model:current-page="pageNum" :page-size="pageSize"
          :total="total" layout="total, prev, pager, next" @current-change="load" />
      </div>
    </el-card>

    <el-dialog title="新增采购" v-model="dialogVisible" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="采购日期">
          <el-date-picker v-model="form.purchaseDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width:100%" />
        </el-form-item>
        <el-form-item label="采购人">
          <el-input v-model="form.purchaser" />
        </el-form-item>
        <el-form-item label="物品名称">
          <el-select v-model="form.supplyId" placeholder="选择物品" filterable style="width:100%" @change="onSupplySelect">
            <el-option v-for="s in supplyOptions" :key="s.id" :label="s.name" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="单位">
          <el-input v-model="form.unit" placeholder="自动填充" disabled />
        </el-form-item>
        <el-form-item label="单价">
          <el-input-number v-model="form.unitPrice" :min="0" :precision="2" style="width:100%" disabled />
        </el-form-item>
        <el-form-item label="数量">
          <el-input-number v-model="form.quantity" :min="1" :precision="2" style="width:100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted, watch } from 'vue'
import { getPurchaseList, addPurchase } from '../api/purchase'
import { getSupplyList } from '../api/supply'
import { ElMessage } from 'element-plus'

export default {
  name: 'PurchaseList',
  setup() {
    const tableData = ref([])
    const pageNum = ref(1)
    const pageSize = ref(15)
    const total = ref(0)
    const filterYear = ref(null)
    const filterMonth = ref(null)
    const searchPurchaser = ref('')
    const searchSupply = ref('')
    const dialogVisible = ref(false)
    const supplyOptions = ref([])
    const form = reactive({ purchaseDate: '', purchaser: '', supplyId: null, supplyName: '', unit: '', unitPrice: null, quantity: null, remark: '' })
    const years = [2022, 2023, 2024, 2025, 2026]

    async function load() {
      const res = await getPurchaseList({
        pageNum: pageNum.value, pageSize: pageSize.value,
        year: filterYear.value, month: filterMonth.value,
        purchaser: searchPurchaser.value || undefined,
        supplyName: searchSupply.value || undefined
      })
      tableData.value = res.records
      total.value = res.total
    }

    async function loadSupplies() {
      supplyOptions.value = await getSupplyList() || []
    }

    function onSupplySelect() {
      const selected = supplyOptions.value.find(s => s.id === form.supplyId)
      if (selected) {
        form.supplyName = selected.name
        form.unit = selected.unit || ''
        form.unitPrice = selected.unitPrice || null
      }
    }

    function formatDate(d) {
      return d ? d.substring(0, 10) : ''
    }

    async function submit() {
      await addPurchase({ ...form })
      ElMessage.success('采购记录已添加')
      dialogVisible.value = false
      Object.assign(form, { purchaseDate: '', purchaser: '', supplyId: null, supplyName: '', unit: '', unitPrice: null, quantity: null, remark: '' })
      load()
    }

    watch([filterYear, filterMonth, searchPurchaser, searchSupply], () => { pageNum.value = 1; load() })
    onMounted(() => { load(); loadSupplies() })
    return { tableData, pageNum, pageSize, total, filterYear, filterMonth, searchPurchaser, searchSupply, dialogVisible, form, years, supplyOptions, load, loadSupplies, onSupplySelect, formatDate, submit }
  }
}
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
