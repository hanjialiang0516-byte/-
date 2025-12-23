<template>
  <div class="reports-page">
    <h2>数据报表中心</h2>
    
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="stat-item">
            <el-icon size="40" color="#409EFF"><House /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalRooms }}</div>
              <div class="stat-label">总房间数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="stat-item">
            <el-icon size="40" color="#67C23A"><User /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalUsers }}</div>
              <div class="stat-label">注册用户</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="stat-item">
            <el-icon size="40" color="#E6A23C"><List /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalBookings }}</div>
              <div class="stat-label">总订单数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header>房间状态分布</template>
          <el-table :data="stats.roomStats" stripe>
            <el-table-column prop="status" label="状态" />
            <el-table-column prop="count" label="数量" />
            <el-table-column label="占比">
              <template #default="{ row }">
                <el-progress :percentage="getPercentage(row.count, stats.totalRooms)" :stroke-width="10" />
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>订单状态分布</template>
          <el-table :data="stats.bookingStats" stripe>
            <el-table-column prop="status" label="状态" />
            <el-table-column prop="count" label="数量" />
            <el-table-column label="占比">
              <template #default="{ row }">
                <el-progress :percentage="getPercentage(row.count, stats.totalBookings)" :stroke-width="10" />
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { House, User, List } from '@element-plus/icons-vue'
import api from '../../api'

const stats = reactive({
  totalRooms: 0,
  totalUsers: 0,
  totalBookings: 0,
  roomStats: [],
  bookingStats: []
})

const getPercentage = (count, total) => {
  if (!total) return 0
  return Math.round((count / total) * 100)
}

onMounted(async () => {
  const res = await api.get('/admin/reports/dashboard')
  Object.assign(stats, res.data)
})
</script>

<style scoped>
.reports-page h2 { margin-bottom: 20px; }
.stat-cards { margin-bottom: 20px; }
.stat-item { display: flex; align-items: center; gap: 20px; }
.stat-info { flex: 1; }
.stat-value { font-size: 32px; font-weight: bold; color: #303133; }
.stat-label { color: #909399; margin-top: 4px; }
</style>
