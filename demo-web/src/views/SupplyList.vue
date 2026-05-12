<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <div style="display:flex;align-items:center;gap:12px">
            <span>办公用品列表（共 {{ tableData.length }} 种）</span>
            <el-input v-model="searchName" placeholder="搜索物品名称" clearable style="width:220px" @clear="load" @keyup.enter="load" />
            <el-button type="primary" @click="load">搜索</el-button>
          </div>
          <div style="display:flex;gap:8px">
            <el-button type="danger" :disabled="selectedRows.length === 0" @click="handleBatchDelete">批量删除（{{ selectedRows.length }}）</el-button>
            <el-button type="primary" @click="handleAdd">新增物品</el-button>
          </div>
        </div>
      </template>
      <el-table :data="tableData" stripe @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="50" />
        <el-table-column prop="supplyNo" label="编号" width="80" />
        <el-table-column prop="name" label="办公用品名称" min-width="180" />
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column prop="model" label="型号" width="120" />
        <el-table-column prop="unitPrice" label="单价" width="100">
          <template #default="{ row }">¥{{ row.unitPrice }}</template>
        </el-table-column>
        <el-table-column prop="warningStock" label="预警数" width="80" />
        <el-table-column prop="remark" label="备注" min-width="150" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="编号">
          <el-input-number v-model="form.supplyNo" :min="1" />
        </el-form-item>
        <el-form-item label="名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="单位">
          <el-input v-model="form.unit" placeholder="包/支/只/个" />
        </el-form-item>
        <el-form-item label="型号">
          <el-input v-model="form.model" />
        </el-form-item>
        <el-form-item label="单价">
          <el-input-number v-model="form.unitPrice" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="预警数">
          <el-input-number v-model="form.warningStock" :min="0" :precision="2" />
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
import { ref, reactive, onMounted } from 'vue'
import { getSupplyList, addSupply, updateSupply, deleteSupply, batchDeleteSupply } from '../api/supply'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'SupplyList',
  setup() {
    const tableData = ref([])
    const selectedRows = ref([])
    const dialogVisible = ref(false)
    const dialogTitle = ref('新增')
    const form = reactive({ id: null, supplyNo: 1, name: '', unit: '', model: '', unitPrice: null, warningStock: 5, remark: '' })
    const searchName = ref('')

    async function load() {
      tableData.value = await getSupplyList({ name: searchName.value || undefined })
    }

    function handleSelectionChange(rows) {
      selectedRows.value = rows
    }

    function handleAdd() {
      dialogTitle.value = '新增物品'
      Object.assign(form, { id: null, supplyNo: (tableData.value.length + 1), name: '', unit: '', model: '', unitPrice: null, warningStock: 5, remark: '' })
      dialogVisible.value = true
    }

    function handleEdit(row) {
      dialogTitle.value = '编辑物品'
      Object.assign(form, { ...row })
      dialogVisible.value = true
    }

    async function handleDelete(row) {
      await ElMessageBox.confirm('确定删除吗？', '提示', { type: 'warning' })
      await deleteSupply(row.id)
      ElMessage.success('删除成功')
      load()
    }

    async function handleBatchDelete() {
      await ElMessageBox.confirm(`确定删除选中的 ${selectedRows.value.length} 条记录吗？`, '批量删除', { type: 'warning' })
      const ids = selectedRows.value.map(r => r.id)
      await batchDeleteSupply(ids)
      ElMessage.success('批量删除成功')
      load()
    }

    async function submit() {
      if (form.id) {
        await updateSupply({ ...form })
        ElMessage.success('修改成功')
      } else {
        await addSupply({ ...form })
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      load()
    }

    onMounted(load)
    return { tableData, selectedRows, dialogVisible, dialogTitle, form, searchName, handleSelectionChange, handleAdd, handleEdit, handleDelete, handleBatchDelete, submit, load }
  }
}
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
