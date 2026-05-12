<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户列表（共 {{ tableData.length }} 人）</span>
          <el-button type="primary" @click="handleAdd">新增用户</el-button>
        </div>
      </template>
      <el-table :data="tableData" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="name" label="姓名" width="150" />
        <el-table-column prop="phone" label="电话" width="150" />
        <el-table-column prop="role" label="角色" width="120">
          <template #default="{ row }">
            <el-tag :type="row.role === 'admin' ? 'danger' : 'info'">
              {{ row.role === 'admin' ? '管理员' : '普通用户' }}
            </el-tag>
          </template>
        </el-table-column>
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
        <el-form-item label="用户名" required>
          <el-input v-model="form.username" :disabled="!!form.id" />
        </el-form-item>
        <el-form-item label="姓名" required>
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="密码" :required="!form.id">
          <el-input v-model="form.password" type="password" show-password
            :placeholder="form.id ? '留空则不修改' : '请输入密码'" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.role">
            <el-option label="普通用户" value="user" />
            <el-option label="管理员" value="admin" />
          </el-select>
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
import { getUserList, addUser, updateUser, deleteUser } from '../api/user'
import { useAuthStore } from '../stores/auth'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'UserList',
  setup() {
    const authStore = useAuthStore()
    const tableData = ref([])
    const dialogVisible = ref(false)
    const dialogTitle = ref('新增用户')
    const form = reactive({ id: null, username: '', name: '', phone: '', password: '', role: 'user' })

    async function load() {
      tableData.value = await getUserList()
    }

    function handleAdd() {
      dialogTitle.value = '新增用户'
      Object.assign(form, { id: null, username: '', name: '', phone: '', password: '', role: 'user' })
      dialogVisible.value = true
    }

    function handleEdit(row) {
      dialogTitle.value = '编辑用户'
      Object.assign(form, { ...row, password: '' })
      dialogVisible.value = true
    }

    async function handleDelete(row) {
      if (authStore.currentUser && authStore.currentUser.id === row.id) {
        ElMessage.warning('不能删除自己的账号')
        return
      }
      await ElMessageBox.confirm('确定删除该用户吗？', '提示', { type: 'warning' })
      await deleteUser(row.id)
      ElMessage.success('删除成功')
      load()
    }

    async function submit() {
      if (form.id) {
        const payload = { id: form.id, name: form.name, phone: form.phone, role: form.role }
        if (form.password) payload.password = form.password
        await updateUser(payload)
        ElMessage.success('修改成功')
      } else {
        await addUser({ ...form })
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      load()
    }

    onMounted(load)
    return { tableData, dialogVisible, dialogTitle, form, handleAdd, handleEdit, handleDelete, submit, load }
  }
}
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
