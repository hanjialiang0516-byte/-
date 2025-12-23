<template>
  <el-container class="layout-container">
    <el-header class="header">
      <div class="logo" @click="$router.push('/')">🏨 星辰酒店</div>
      <el-menu mode="horizontal" :ellipsis="false" :router="true" :default-active="currentPath" class="nav-menu">
        <el-menu-item index="/">首页</el-menu-item>
        <el-menu-item index="/rooms">客房</el-menu-item>
        <el-menu-item index="/booking" v-if="userStore.isLoggedIn">预订</el-menu-item>
        <el-menu-item index="/my-bookings" v-if="userStore.isLoggedIn">我的订单</el-menu-item>
      </el-menu>
      <div class="header-right">
        <template v-if="userStore.isLoggedIn">
          <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="msg-badge">
            <el-button :icon="Bell" circle @click="$router.push('/messages')" />
          </el-badge>
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" :src="userStore.user?.avatar">{{ userStore.user?.realName?.[0] || 'U' }}</el-avatar>
              <span class="user-name">{{ userStore.user?.realName || userStore.user?.username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon> 个人中心
                </el-dropdown-item>
                <el-dropdown-item command="bookings">
                  <el-icon><List /></el-icon> 我的订单
                </el-dropdown-item>
                <el-dropdown-item command="messages">
                  <el-icon><Bell /></el-icon> 消息中心
                  <el-badge :value="unreadCount" :hidden="unreadCount === 0" style="margin-left: 8px;" />
                </el-dropdown-item>
                <el-dropdown-item command="admin" v-if="userStore.isAdmin" divided>
                  <el-icon><Setting /></el-icon> 管理后台
                </el-dropdown-item>
                <el-dropdown-item command="logout" divided>
                  <el-icon><SwitchButton /></el-icon> 退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <el-button type="primary" @click="$router.push('/login')">登录</el-button>
          <el-button @click="$router.push('/register')">注册</el-button>
        </template>
      </div>
    </el-header>
    <el-main class="main-content">
      <router-view />
    </el-main>
    <el-footer class="footer">
      <div class="footer-content">
        <div class="footer-section">
          <h4>🏨 星辰酒店</h4>
          <p>始于1998年，专注品质服务</p>
          <p>让每一次旅途都成为美好回忆</p>
        </div>
        <div class="footer-section">
          <h4>快速链接</h4>
          <router-link to="/">首页</router-link>
          <router-link to="/rooms">客房预览</router-link>
          <router-link to="/booking">在线预订</router-link>
        </div>
        <div class="footer-section">
          <h4>联系我们</h4>
          <p>📞 400-888-8888</p>
          <p>📧 service@starhotel.com</p>
          <p>📍 北京市朝阳区星辰大道88号</p>
        </div>
        <div class="footer-section">
          <h4>关注我们</h4>
          <p>微信公众号：星辰酒店</p>
          <p>微博：@星辰酒店官方</p>
        </div>
      </div>
      <div class="footer-bottom">
        <p>© 2025 星辰酒店 版权所有 | 京ICP备12345678号</p>
      </div>
    </el-footer>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Bell, ArrowDown, User, List, Setting, SwitchButton } from '@element-plus/icons-vue'
import { useUserStore } from '../store/user'
import { messageApi } from '../api'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const unreadCount = ref(0)
const currentPath = computed(() => route.path)

const fetchUnreadCount = async () => {
  if (userStore.isLoggedIn) {
    try {
      const res = await messageApi.getUnreadCount()
      unreadCount.value = res.data.count
    } catch (e) {
      console.error(e)
    }
  }
}

const handleCommand = (command) => {
  switch (command) {
    case 'logout':
      userStore.logout()
      router.push('/login')
      break
    case 'profile':
      router.push('/profile')
      break
    case 'bookings':
      router.push('/my-bookings')
      break
    case 'messages':
      router.push('/messages')
      break
    case 'admin':
      router.push('/admin')
      break
  }
}

onMounted(fetchUnreadCount)
</script>

<style scoped>
.layout-container { min-height: 100vh; flex-direction: column; }

.header {
  display: flex;
  align-items: center;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  padding: 0 40px;
  position: sticky;
  top: 0;
  z-index: 1000;
}
.logo {
  font-size: 22px;
  font-weight: bold;
  margin-right: 40px;
  cursor: pointer;
  color: #409EFF;
}
.nav-menu { flex: 1; border-bottom: none; }
.header-right { display: flex; align-items: center; gap: 16px; }
.user-info {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
}
.user-name { max-width: 80px; overflow: hidden; text-overflow: ellipsis; }
.msg-badge { margin-right: 8px; }

.main-content {
  flex: 1;
  background: #f5f7fa;
  padding: 20px 40px;
}

.footer {
  background: #2c3e50;
  color: #fff;
  padding: 0;
  height: auto;
}
.footer-content {
  display: flex;
  justify-content: space-around;
  padding: 40px;
  max-width: 1200px;
  margin: 0 auto;
}
.footer-section h4 {
  margin-bottom: 16px;
  font-size: 16px;
}
.footer-section p, .footer-section a {
  color: #bdc3c7;
  font-size: 14px;
  line-height: 2;
  display: block;
  text-decoration: none;
}
.footer-section a:hover { color: #fff; }
.footer-bottom {
  text-align: center;
  padding: 20px;
  border-top: 1px solid #34495e;
  color: #7f8c8d;
  font-size: 13px;
}
</style>
