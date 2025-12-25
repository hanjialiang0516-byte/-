<template>
  <div class="login-page">
    <div class="login-left">
      <div class="brand">
        <h1>🏨 星辰酒店</h1>
        <p>舒适住宿，便捷预订</p>
      </div>
      <div class="features">
        <div class="feature-item">
          <el-icon size="24"><Check /></el-icon>
          <span>在线快速预订</span>
        </div>
        <div class="feature-item">
          <el-icon size="24"><Check /></el-icon>
          <span>会员专属优惠</span>
        </div>
        <div class="feature-item">
          <el-icon size="24"><Check /></el-icon>
          <span>积分兑换礼品</span>
        </div>
        <div class="feature-item">
          <el-icon size="24"><Check /></el-icon>
          <span>24小时客服支持</span>
        </div>
      </div>
    </div>
    <div class="login-right">
      <el-card class="login-card">
        <h2>欢迎回来</h2>
        <p class="subtitle">登录您的账户继续预订</p>
        
        <el-form :model="form" :rules="rules" ref="formRef" @submit.prevent="handleLogin">
          <el-form-item prop="username">
            <el-input v-model="form.username" placeholder="用户名" prefix-icon="User" size="large" />
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="Lock" show-password size="large" />
          </el-form-item>
          <el-form-item>
            <div class="form-options">
              <el-checkbox v-model="rememberMe">记住我</el-checkbox>
              <el-link type="primary">忘记密码？</el-link>
            </div>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" native-type="submit" :loading="loading" size="large" style="width: 100%">
              登录
            </el-button>
          </el-form-item>
        </el-form>
        
        <el-divider />
        
        <div class="register-link">
          还没有账号？<router-link to="/register">立即注册</router-link>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Check } from '@element-plus/icons-vue'
import { authApi } from '../api'
import { useUserStore } from '../store/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)
const rememberMe = ref(false)
const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    const res = await authApi.login(form)
    userStore.setAuth(res.data.token, res.data.user)
    ElMessage.success('登录成功，欢迎回来！')
    router.push(userStore.isAdmin ? '/admin' : '/')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
}

.login-left {
  flex: 1;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 60px;
  color: #fff;
}
.brand h1 { font-size: 48px; margin-bottom: 12px; }
.brand p { font-size: 20px; opacity: 0.9; margin-bottom: 60px; }
.features { display: flex; flex-direction: column; gap: 20px; }
.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 18px;
  opacity: 0.9;
}

.login-right {
  width: 500px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
}
.login-card {
  width: 400px;
  padding: 20px;
}
.login-card h2 { margin: 0 0 8px; text-align: center; }
.subtitle { text-align: center; color: #909399; margin-bottom: 30px; }

.form-options {
  display: flex;
  justify-content: space-between;
  width: 100%;
}

.register-link {
  text-align: center;
  color: #909399;
}
.register-link a { color: #409EFF; text-decoration: none; }
</style>
