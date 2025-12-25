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
            <el-form-item label="上传头像">
              <div class="avatar-upload">
                <el-upload
                  class="avatar-uploader"
                  :show-file-list="false"
                  :before-upload="beforeAvatarUpload"
                  :http-request="handleAvatarUpload"
                  accept="image/*"
                  :disabled="!editing"
                >
                  <el-avatar v-if="form.avatar" :size="80" :src="form.avatar" />
                  <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
                </el-upload>
                <div class="upload-tip">
                  <span v-if="!editing" style="color: #E6A23C;">请先点击右上角"编辑"按钮</span>
                  <span v-else>点击上传头像，支持 JPG/PNG 格式，大小不超过 2MB</span>
                </div>
              </div>
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
import { Plus } from '@element-plus/icons-vue'
import { userApi, bookingApi } from '../api'
import { useUserStore } from '../store/user'

const userStore = useUserStore()
const loading = ref(false)
const editing = ref(false)
const form = reactive({ username: '', realName: '', phone: '', email: '', avatar: '' })
const passwordForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })
const stats = reactive({ totalBookings: 0, completedBookings: 0, totalSpent: 0 })

const avatarUrl = computed(() => form.avatar || '')

// 头像上传前校验
const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件！')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB！')
    return false
  }
  return true
}

// 压缩图片
const compressImage = (file, maxWidth = 60, quality = 0.5) => {
  return new Promise((resolve) => {
    const reader = new FileReader()
    reader.onload = (e) => {
      const img = new Image()
      img.onload = () => {
        const canvas = document.createElement('canvas')
        let width = img.width
        let height = img.height
        
        // 按比例缩放
        if (width > maxWidth) {
          height = (maxWidth / width) * height
          width = maxWidth
        }
        
        canvas.width = width
        canvas.height = height
        const ctx = canvas.getContext('2d')
        ctx.drawImage(img, 0, 0, width, height)
        
        // 转为压缩后的base64
        const compressedBase64 = canvas.toDataURL('image/jpeg', quality)
        resolve(compressedBase64)
      }
      img.src = e.target.result
    }
    reader.readAsDataURL(file)
  })
}

// 处理头像上传（压缩后转为base64）
const handleAvatarUpload = async (options) => {
  const file = options.file
  try {
    const compressedBase64 = await compressImage(file, 100, 0.7)
    form.avatar = compressedBase64
    ElMessage.success('头像上传成功')
  } catch (err) {
    ElMessage.error('头像上传失败')
  }
}

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

.avatar-upload {
  display: flex;
  align-items: center;
  gap: 16px;
}

.avatar-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 50%;
  cursor: pointer;
  overflow: hidden;
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: border-color 0.3s;
}

.avatar-uploader:hover {
  border-color: #409EFF;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
}

.upload-tip {
  color: #999;
  font-size: 12px;
  line-height: 1.5;
}
</style>
