<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <div style="display:flex;align-items:center;gap:8px;flex-wrap:wrap">
            <span>领用记录</span>
            <el-select v-model="filterYear" placeholder="年份" clearable style="width:100px">
              <el-option v-for="y in years" :key="y" :label="y" :value="y" />
            </el-select>
            <el-select v-model="filterMonth" placeholder="月份" clearable style="width:100px">
              <el-option v-for="m in 12" :key="m" :label="m+'月'" :value="m" />
            </el-select>
            <el-input v-model="filterDept" placeholder="部门" clearable style="width:120px" @clear="load" @keyup.enter="load" />
            <el-input v-model="searchPerson" placeholder="领用人" clearable style="width:120px" @clear="load" @keyup.enter="load" />
            <el-input v-model="searchSupply" placeholder="物品名称" clearable style="width:150px" @clear="load" @keyup.enter="load" />
            <el-button type="primary" @click="load">搜索</el-button>
          </div>
          <el-button type="primary" @click="dialogVisible = true">新增领用</el-button>
        </div>
      </template>
      <el-table :data="tableData" stripe>
        <el-table-column prop="usageNo" label="NO" width="70" />
        <el-table-column prop="usageDate" label="日期" width="110">
          <template #default="{ row }">{{ formatDate(row.usageDate) }}</template>
        </el-table-column>
        <el-table-column prop="personName" label="领用人" width="100" />
        <el-table-column prop="deptName" label="部门" width="120" />
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

    <el-dialog title="新增领用" v-model="dialogVisible" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="领用日期">
          <el-date-picker v-model="form.usageDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width:100%" />
        </el-form-item>
        <el-form-item label="领用人">
          <el-select v-model="form.personName" placeholder="选择领用人" filterable style="width:100%" @change="onPersonSelect">
            <el-option v-for="p in personnelOptions" :key="p.id" :label="p.name + ' - ' + (p.deptName || '')" :value="p.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="所在部门">
          <el-input v-model="form.deptName" disabled />
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
import { getUsageList, addUsage } from '../api/usage'
import { getSupplyList } from '../api/supply'
import { getPersonnelList } from '../api/personnel'
import { ElMessage } from 'element-plus'

export default {
  name: 'UsageList',
  setup() {
    const tableData = ref([])
    const pageNum = ref(1)
    const pageSize = ref(15)
    const total = ref(0)
    const filterYear = ref(null)
    const filterMonth = ref(null)
    const filterDept = ref('')
    const searchPerson = ref('')
    const searchSupply = ref('')
    const dialogVisible = ref(false)
    const supplyOptions = ref([])
    const personnelOptions = ref([])
    const form = reactive({ usageDate: '', personName: '', deptName: '', supplyId: null, supplyName: '', unit: '', unitPrice: null, quantity: null, remark: '' })
    const years = [2022, 2023, 2024, 2025, 2026]

    async function load() {
      const res = await getUsageList({
        pageNum: pageNum.value, pageSize: pageSize.value,
        year: filterYear.value, month: filterMonth.value,
        deptName: filterDept.value || undefined,
        personName: searchPerson.value || undefined,
        supplyName: searchSupply.value || undefined
      })
      tableData.value = res.records
      total.value = res.total
    }

    async function loadSupplies() {
      supplyOptions.value = await getSupplyList() || []
    }

    async function loadPersonnel() {
      personnelOptions.value = await getPersonnelList() || []
    }

    function onPersonSelect() {
      const selected = personnelOptions.value.find(p => p.name === form.personName)
      if (selected) {
        form.deptName = selected.deptName || ''
      }
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
      await addUsage({ ...form })
      ElMessage.success('领用记录已添加')
      dialogVisible.value = false
      Object.assign(form, { usageDate: '', personName: '', deptName: '', supplyId: null, supplyName: '', unit: '', unitPrice: null, quantity: null, remark: '' })
      load()
    }

    watch([filterYear, filterMonth, filterDept, searchPerson, searchSupply], () => { pageNum.value = 1; load() })
    onMounted(() => { load(); loadSupplies(); loadPersonnel() })
    return { tableData, pageNum, pageSize, total, filterYear, filterMonth, filterDept, searchPerson, searchSupply, dialogVisible, form, years, supplyOptions, personnelOptions, load, loadSupplies, loadPersonnel, onPersonSelect, onSupplySelect, formatDate, submit }
  }
}
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
