<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <div style="display:flex;align-items:center;gap:8px;flex-wrap:wrap">
            <span>人员信息（共 {{ tableData.length }} 人）</span>
            <el-input v-model="searchName" placeholder="姓名" clearable style="width:130px" @clear="load" @keyup.enter="load" />
            <el-input v-model="searchDept" placeholder="部门" clearable style="width:130px" @clear="load" @keyup.enter="load" />
            <el-button type="primary" @click="load">搜索</el-button>
          </div>
          <el-button type="primary" @click="handleAdd">新增人员</el-button>
        </div>
      </template>
      <el-table :data="tableData" stripe>
        <el-table-column prop="personNo" label="编号" width="70" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="deptName" label="所属部门" min-width="160" />
        <el-table-column prop="remark" label="备注" min-width="120" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="450px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="所属部门" prop="deptName">
          <el-input v-model="form.deptName" />
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
import { getPersonnelList, addPersonnel, updatePersonnel, deletePersonnel } from '../api/personnel'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'PersonnelList',
  setup() {
    const tableData = ref([])
    const searchName = ref('')
    const searchDept = ref('')
    const dialogVisible = ref(false)
    const dialogTitle = ref('新增')
    const formRef = ref(null)
    const form = reactive({ id: null, personNo: null, name: '', deptName: '', remark: '' })
    const rules = {
      name: [{ required: true, message: '姓名不能为空', trigger: 'blur' }],
      deptName: [{ required: true, message: '所属部门不能为空', trigger: 'blur' }]
    }

    async function load() {
      const data = await getPersonnelList({
        name: searchName.value || undefined,
        deptName: searchDept.value || undefined
      })
      tableData.value = data || []
    }

    function handleAdd() {
      dialogTitle.value = '新增人员'
      Object.assign(form, { id: null, personNo: null, name: '', deptName: '', remark: '' })
      dialogVisible.value = true
    }

    function handleEdit(row) {
      dialogTitle.value = '编辑人员'
      Object.assign(form, { ...row })
      dialogVisible.value = true
    }

    async function handleDelete(row) {
      await ElMessageBox.confirm('确定删除 ' + row.name + ' 吗？', '提示', { type: 'warning' })
      await deletePersonnel(row.id)
      ElMessage.success('删除成功')
      load()
    }

    async function submit() {
      const valid = await formRef.value.validate().catch(() => false)
      if (!valid) return
      if (form.id) {
        await updatePersonnel({ ...form })
        ElMessage.success('修改成功')
      } else {
        await addPersonnel({ ...form })
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      load()
    }

    onMounted(load)
    return { tableData, searchName, searchDept, dialogVisible, dialogTitle, form, formRef, rules, load, handleAdd, handleEdit, handleDelete, submit }
  }
}
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
