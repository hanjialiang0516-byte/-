<template>
  <div class="register-page">
    <div class="register-left">
      <div class="brand">
        <h1>🏨 星辰酒店</h1>
        <p>加入我们，开启美好旅程</p>
      </div>
      <div class="benefits">
        <h3>会员专属权益</h3>
        <div class="benefit-item">
          <div class="benefit-icon">🎁</div>
          <div class="benefit-text">
            <h4>新人礼包</h4>
            <p>注册即送100元优惠券</p>
          </div>
        </div>
        <div class="benefit-item">
          <div class="benefit-icon">💰</div>
          <div class="benefit-text">
            <h4>积分返利</h4>
            <p>消费1元积1分，积分可抵现</p>
          </div>
        </div>
        <div class="benefit-item">
          <div class="benefit-icon">⭐</div>
          <div class="benefit-text">
            <h4>会员折扣</h4>
            <p>专享9折优惠价格</p>
          </div>
        </div>
        <div class="benefit-item">
          <div class="benefit-icon">🎂</div>
          <div class="benefit-text">
            <h4>生日特权</h4>
            <p>生日当天免费升级房型</p>
          </div>
        </div>
      </div>
    </div>
    <div class="register-right">
      <el-card class="register-card">
        <h2>创建账户</h2>
        <p class="subtitle">填写以下信息完成注册</p>
        
        <el-form :model="form" :rules="rules" ref="formRef" @submit.prevent="handleRegister">
          <el-form-item prop="username">
            <el-input v-model="form.username" placeholder="用户名（用于登录）" prefix-icon="User" size="large" />
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="form.password" type="password" placeholder="密码（至少6位）" prefix-icon="Lock" show-password size="large" />
          </el-form-item>
          <el-form-item prop="confirmPassword">
            <el-input v-model="form.confirmPassword" type="password" placeholder="确认密码" prefix-icon="Lock" show-password size="large" />
          </el-form-item>
          <el-form-item prop="realName">
            <el-input v-model="form.realName" placeholder="真实姓名" prefix-icon="UserFilled" size="large" />
          </el-form-item>
          <el-form-item prop="phone">
            <el-input v-model="form.phone" placeholder="手机号" prefix-icon="Phone" size="large" />
          </el-form-item>
          <el-form-item prop="email">
            <el-input v-model="form.email" placeholder="邮箱（选填）" prefix-icon="Message" size="large" />
          </el-form-item>
          <el-form-item>
            <el-checkbox v-model="agreeTerms">
              我已阅读并同意 <el-link type="primary">《用户协议》</el-link> 和 <el-link type="primary">《隐私政策》</el-link>
            </el-checkbox>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" native-type="submit" :loading="loading" :disabled="!agreeTerms" size="large" style="width: 100%">
              立即注册
            </el-button>
          </el-form-item>
        </el-form>
        
        <div class="login-link">
          已有账号？<router-link to="/login">立即登录</router-link>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authApi } from '../api'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const agreeTerms = ref(false)
const form = reactive({ username: '', password: '', confirmPassword: '', realName: '', phone: '', email: '' })

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度3-20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1\d{10}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    await authApi.register(form)
    ElMessage.success('注册成功！请登录')
    router.push('/login')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  display: flex;
}

.register-left {
  flex: 1;
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 60px;
  color: #fff;
}
.brand h1 { font-size: 48px; margin-bottom: 12px; }
.brand p { font-size: 20px; opacity: 0.9; margin-bottom: 40px; }

.benefits h3 { margin-bottom: 24px; font-size: 20px; }
.benefit-item {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
  background: rgba(255,255,255,0.1);
  padding: 16px;
  border-radius: 12px;
}
.benefit-icon { font-size: 32px; }
.benefit-text h4 { margin: 0 0 4px; }
.benefit-text p { margin: 0; opacity: 0.9; font-size: 14px; }

.register-right {
  width: 520px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  padding: 20px;
}
.register-card {
  width: 100%;
  max-width: 450px;
  padding: 20px;
}
.register-card h2 { margin: 0 0 8px; text-align: center; }
.subtitle { text-align: center; color: #909399; margin-bottom: 24px; }

.login-link {
  text-align: center;
  color: #909399;
  margin-top: 16px;
}
.login-link a { color: #409EFF; text-decoration: none; }
</style>
