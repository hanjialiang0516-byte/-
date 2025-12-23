<template>
  <div class="admin-users">
    <h2>客户档案管理</h2>
    <el-table :data="users" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" width="120" />
      <el-table-column prop="realName" label="真实姓名" width="120" />
      <el-table-column prop="phone" label="手机号" width="130" />
      <el-table-column prop="email" label="邮箱" width="180" />
      <el-table-column prop="role" label="角色" width="100">
        <template #default="{ row }">
          <el-tag :type="row.role === 1 ? 'danger' : ''">{{ row.role === 1 ? '管理员' : '普通用户' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="注册时间" width="180" />
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="toggleStatus(row)" />
        </template>
      </el-table-column>
    </el-table>
    <el-pagination v-model:current-page="page" :page-size="10" :total="total" @current-change="fetchUsers" layout="prev, pager, next" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { adminApi } from '../../api'

const users = ref([])
const loading = ref(false)
const page = ref(1)
const total = ref(0)

const fetchUsers = async () => {
  loading.value = true
  try {
    const res = await adminApi.getUsers(page.value, 10)
    users.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const toggleStatus = async (user) => {
  await adminApi.updateUserStatus(user.id, user.status)
  ElMessage.success('状态已更新')
}

onMounted(fetchUsers)
</script>

<style scoped>
.admin-users h2 { margin-bottom: 20px; }
.el-pagination { margin-top: 20px; }
</style>
