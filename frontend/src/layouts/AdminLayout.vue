<template>
  <el-container class="admin-layout">
    <el-aside width="200px" class="aside">
      <div class="logo">🏨 管理后台</div>
      <el-menu :default-active="$route.path" router background-color="#304156" text-color="#bfcbd9" active-text-color="#409EFF">
        <el-menu-item index="/admin">
          <el-icon><DataAnalysis /></el-icon>
          <span>房态总控</span>
        </el-menu-item>
        <el-menu-item index="/admin/bookings">
          <el-icon><List /></el-icon>
          <span>订单管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/rooms">
          <el-icon><House /></el-icon>
          <span>客房管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/users">
          <el-icon><User /></el-icon>
          <span>客户档案</span>
        </el-menu-item>
        <el-menu-item index="/admin/finance">
          <el-icon><Money /></el-icon>
          <span>经营统计</span>
        </el-menu-item>
        <el-menu-item index="/admin/reports">
          <el-icon><DataAnalysis /></el-icon>
          <span>数据报表</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="admin-header">
        <el-button text @click="$router.push('/')">
          <el-icon><Back /></el-icon> 返回前台
        </el-button>
        <div class="header-right">
          <span>{{ userStore.user?.realName || userStore.user?.username }}</span>
          <el-button type="danger" text @click="handleLogout">退出</el-button>
        </div>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { DataAnalysis, List, House, User, Money, Back } from '@element-plus/icons-vue'
import { useUserStore } from '../store/user'

const router = useRouter()
const userStore = useUserStore()

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.admin-layout { min-height: 100vh; }
.aside { background: #304156; }
.logo { color: #fff; font-size: 18px; font-weight: bold; padding: 20px; text-align: center; }
.admin-header { display: flex; justify-content: space-between; align-items: center; background: #fff; box-shadow: 0 1px 4px rgba(0,0,0,0.1); }
.header-right { display: flex; align-items: center; gap: 16px; }
</style>
