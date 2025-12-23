<template>
  <div class="profile-page">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="user-card">
          <div class="avatar-section">
            <el-avatar :size="100" :src="avatarUrl">{{ userStore.user?.realName?.[0] || 'U' }}</el-avatar>
            <h2>{{ userStore.user?.realName || userStore.user?.username }}</h2>
            <p class="user-role">
              <el-tag :type="userStore.isAdmin ? 'danger' : ''">{{ userStore.isAdmin ? '管理员' : '普通用户' }}</el-tag>
            </p>
          </div>
          <el-divider />
          <div class="user-stats">
            <div class="stat-item">
              <div class="stat-value">{{ stats.totalBookings }}</div>
              <div class="stat-label">总订单</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ stats.completedBookings }}</div>
              <div class="stat-label">已完成</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">¥{{ stats.totalSpent }}</div>
              <div class="stat-label">总消费</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>个人资料</span>
              <el-button type="primary" text @click="editing = !editing">
                {{ editing ? '取消' : '编辑' }}
              </el-button>
            </div>
          </template>
          
          <el-form :model="form" label-width="100px" :disabled="!editing">
            <el-form-item label="用户名">
              <el-input v-model="form.username" disabled />
            </el-form-item>
            <el-form-item label="真实姓名">
              <el-input v-model="form.realName" placeholder="请输入真实姓名" />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
            <el-form-item label="头像链接">
              <el-input v-model="form.avatar" placeholder="请输入头像图片URL" />
            </el-form-item>
            <el-form-item v-if="editing">
              <el-button type="primary" @click="handleSave" :loading="loading">保存修改</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card style="margin-top: 20px">
          <template #header>修改密码</template>
          <el-form :model="passwordForm" label-width="100px">
            <el-form-item label="当前密码">
              <el-input v-model="passwordForm.oldPassword" type="password" show-password placeholder="请输入当前密码" />
            </el-form-item>
            <el-form-item label="新密码">
              <el-input v-model="passwordForm.newPassword" type="password" show-password placeholder="请输入新密码" />
            </el-form-item>
            <el-form-item label="确认密码">
              <el-input v-model="passwordForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleChangePassword">修改密码</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { userApi, bookingApi } from '../api'
import { useUserStore } from '../store/user'

const userStore = useUserStore()
const loading = ref(false)
const editing = ref(false)
const form = reactive({ username: '', realName: '', phone: '', email: '', avatar: '' })
const passwordForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })
const stats = reactive({ totalBookings: 0, completedBookings: 0, totalSpent: 0 })

const avatarUrl = computed(() => form.avatar || '')

onMounted(async () => {
  // 获取用户信息
  const res = await userApi.getProfile()
  Object.assign(form, res.data)
  
  // 获取订单统计
  try {
    const bookingsRes = await bookingApi.getMyBookings(1, 100)
    const bookings = bookingsRes.data.records
    stats.totalBookings = bookings.length
    stats.completedBookings = bookings.filter(b => b.status === 3).length
    stats.totalSpent = bookings.filter(b => b.status !== 4).reduce((sum, b) => sum + parseFloat(b.totalPrice || 0), 0).toFixed(0)
  } catch (e) {
    console.error(e)
  }
})

const handleSave = async () => {
  loading.value = true
  try {
    await userApi.updateProfile(form)
    userStore.user.realName = form.realName
    userStore.user.phone = form.phone
    userStore.user.email = form.email
    userStore.user.avatar = form.avatar
    localStorage.setItem('user', JSON.stringify(userStore.user))
    ElMessage.success('保存成功')
    editing.value = false
  } finally {
    loading.value = false
  }
}

const handleChangePassword = () => {
  if (!passwordForm.oldPassword || !passwordForm.newPassword) {
    ElMessage.warning('请填写完整')
    return
  }
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    ElMessage.warning('两次输入的密码不一致')
    return
  }
  ElMessage.info('密码修改功能开发中')
}
</script>

<style scoped>
.profile-page { max-width: 1000px; margin: 0 auto; padding: 20px; }

.user-card { text-align: center; }
.avatar-section { padding: 20px 0; }
.avatar-section h2 { margin: 16px 0 8px; }
.user-role { margin-bottom: 0; }

.user-stats {
  display: flex;
  justify-content: space-around;
  padding: 10px 0;
}
.stat-item { text-align: center; }
.stat-value { font-size: 24px; font-weight: bold; color: #409EFF; }
.stat-label { color: #999; font-size: 14px; margin-top: 4px; }

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
