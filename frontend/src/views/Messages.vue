<template>
  <div class="messages-page">
    <div class="page-header">
      <h2>🔔 消息中心</h2>
      <el-button type="primary" text @click="markAllRead" v-if="unreadCount > 0">
        全部标为已读
      </el-button>
    </div>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="全部消息" name="all">
        <template #label>
          <span>全部消息</span>
        </template>
      </el-tab-pane>
      <el-tab-pane label="预订提醒" name="1">
        <template #label>
          <el-badge :value="getTypeUnread(1)" :hidden="getTypeUnread(1) === 0">预订提醒</el-badge>
        </template>
      </el-tab-pane>
      <el-tab-pane label="系统通知" name="0">
        <template #label>
          <el-badge :value="getTypeUnread(0)" :hidden="getTypeUnread(0) === 0">系统通知</el-badge>
        </template>
      </el-tab-pane>
      <el-tab-pane label="促销信息" name="2">
        <template #label>
          <el-badge :value="getTypeUnread(2)" :hidden="getTypeUnread(2) === 0">促销信息</el-badge>
        </template>
      </el-tab-pane>
    </el-tabs>

    <div class="message-list">
      <el-card 
        v-for="msg in filteredMessages" 
        :key="msg.id" 
        class="message-card" 
        :class="{ unread: !msg.isRead }"
        @click="handleClick(msg)"
      >
        <div class="message-icon">
          <el-icon size="24" :color="getIconColor(msg.type)">
            <component :is="getIcon(msg.type)" />
          </el-icon>
        </div>
        <div class="message-content">
          <div class="message-header">
            <span class="title">{{ msg.title }}</span>
            <el-tag size="small" v-if="!msg.isRead" type="danger">未读</el-tag>
            <el-tag size="small" type="info">{{ typeText[msg.type] }}</el-tag>
          </div>
          <p class="content">{{ msg.content }}</p>
          <div class="message-footer">
            <span class="time">{{ msg.createTime }}</span>
          </div>
        </div>
      </el-card>
      
      <el-empty v-if="!filteredMessages.length" description="暂无消息" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Bell, Notification, Discount } from '@element-plus/icons-vue'
import { messageApi } from '../api'

const messages = ref([])
const activeTab = ref('all')

const typeText = ['系统通知', '预订提醒', '促销信息']

const filteredMessages = computed(() => {
  if (activeTab.value === 'all') return messages.value
  return messages.value.filter(m => m.type === parseInt(activeTab.value))
})

const unreadCount = computed(() => messages.value.filter(m => !m.isRead).length)

const getTypeUnread = (type) => {
  return messages.value.filter(m => m.type === type && !m.isRead).length
}

const getIcon = (type) => {
  const icons = [Notification, Bell, Discount]
  return icons[type] || Bell
}

const getIconColor = (type) => {
  const colors = ['#409EFF', '#67C23A', '#E6A23C']
  return colors[type] || '#409EFF'
}

const handleClick = async (msg) => {
  if (!msg.isRead) {
    await messageApi.markAsRead(msg.id)
    msg.isRead = 1
  }
}

const markAllRead = async () => {
  for (const msg of messages.value) {
    if (!msg.isRead) {
      await messageApi.markAsRead(msg.id)
      msg.isRead = 1
    }
  }
}

onMounted(async () => {
  const res = await messageApi.getAll()
  messages.value = res.data
})
</script>

<style scoped>
.messages-page { max-width: 800px; margin: 0 auto; padding: 20px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header h2 { margin: 0; }

.message-card {
  margin-bottom: 12px;
  cursor: pointer;
  transition: all 0.3s;
}
.message-card:hover { transform: translateX(5px); }
.message-card.unread { border-left: 3px solid #409EFF; background: #f0f9ff; }
.message-card :deep(.el-card__body) { display: flex; gap: 16px; }

.message-icon {
  width: 50px;
  height: 50px;
  background: #f5f7fa;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.message-content { flex: 1; }
.message-header { display: flex; align-items: center; gap: 12px; margin-bottom: 8px; }
.message-header .title { font-weight: bold; font-size: 16px; }
.content { color: #666; margin-bottom: 8px; line-height: 1.6; }
.message-footer { display: flex; justify-content: space-between; }
.time { color: #999; font-size: 12px; }
</style>
