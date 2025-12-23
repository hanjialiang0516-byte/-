<template>
  <div class="my-bookings">
    <div class="page-header">
      <h2>📋 我的预订</h2>
      <el-button type="primary" @click="$router.push('/booking')">
        <el-icon><Plus /></el-icon> 新建预订
      </el-button>
    </div>

    <el-tabs v-model="activeTab" @tab-change="fetchBookings">
      <el-tab-pane label="全部订单" name="all" />
      <el-tab-pane label="待确认" name="0" />
      <el-tab-pane label="已确认" name="1" />
      <el-tab-pane label="已入住" name="2" />
      <el-tab-pane label="已完成" name="3" />
      <el-tab-pane label="已取消" name="4" />
    </el-tabs>

    <div class="booking-list" v-loading="loading">
      <el-card v-for="booking in bookings" :key="booking.id" class="booking-card">
        <div class="booking-header">
          <div class="order-info">
            <span class="order-no">订单号：{{ booking.orderNo }}</span>
            <span class="order-time">{{ booking.createTime }}</span>
          </div>
          <el-tag :type="statusType[booking.status]" size="large">{{ statusText[booking.status] }}</el-tag>
        </div>
        
        <div class="booking-content">
          <div class="room-image" :style="{ backgroundImage: `url(${getRoomImage(booking.roomId)})` }"></div>
          <div class="booking-info">
            <h3>{{ booking.room?.roomNumber || '-' }}号房</h3>
            <div class="info-row">
              <el-icon><Calendar /></el-icon>
              <span>{{ booking.checkInDate }} 至 {{ booking.checkOutDate }}</span>
              <el-tag size="small" type="info">{{ getNights(booking) }}晚</el-tag>
            </div>
            <div class="info-row">
              <el-icon><User /></el-icon>
              <span>{{ booking.guestName }} {{ booking.guestPhone }}</span>
            </div>
            <div class="info-row" v-if="booking.remark">
              <el-icon><ChatDotRound /></el-icon>
              <span>{{ booking.remark }}</span>
            </div>
          </div>
          <div class="booking-price">
            <div class="price">¥{{ booking.totalPrice }}</div>
            <div class="actions">
              <el-button v-if="booking.status === 0" type="warning" text @click="goToPay(booking.id)">去支付</el-button>
              <el-button v-if="booking.status === 0" type="primary" text @click="showDetail(booking)">查看详情</el-button>
              <el-button v-if="booking.status < 2" type="danger" text @click="handleCancel(booking.id)">取消订单</el-button>
              <el-button v-if="booking.status === 3" type="primary" text @click="$router.push('/booking')">再次预订</el-button>
            </div>
          </div>
        </div>
      </el-card>
      
      <el-empty v-if="!loading && !bookings.length" description="暂无订单记录">
        <el-button type="primary" @click="$router.push('/booking')">去预订</el-button>
      </el-empty>
    </div>

    <el-pagination 
      v-if="total > 10"
      v-model:current-page="page" 
      :page-size="10" 
      :total="total" 
      @current-change="fetchBookings" 
      layout="prev, pager, next"
      style="margin-top: 20px; justify-content: center;"
    />

    <!-- 订单详情弹窗 -->
    <el-dialog v-model="detailVisible" title="订单详情" width="500px">
      <el-descriptions :column="1" border v-if="currentBooking">
        <el-descriptions-item label="订单号">{{ currentBooking.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag :type="statusType[currentBooking.status]">{{ statusText[currentBooking.status] }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="房间">{{ currentBooking.room?.roomNumber || '-' }}号房</el-descriptions-item>
        <el-descriptions-item label="入住日期">{{ currentBooking.checkInDate }}</el-descriptions-item>
        <el-descriptions-item label="离店日期">{{ currentBooking.checkOutDate }}</el-descriptions-item>
        <el-descriptions-item label="入住人">{{ currentBooking.guestName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentBooking.guestPhone }}</el-descriptions-item>
        <el-descriptions-item label="身份证号">{{ currentBooking.guestIdCard || '-' }}</el-descriptions-item>
        <el-descriptions-item label="订单金额">
          <span style="color: #f56c6c; font-size: 18px; font-weight: bold;">¥{{ currentBooking.totalPrice }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="备注">{{ currentBooking.remark || '无' }}</el-descriptions-item>
        <el-descriptions-item label="下单时间">{{ currentBooking.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Calendar, User, ChatDotRound } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import { bookingApi } from '../api'

const router = useRouter()

const bookings = ref([])
const loading = ref(false)
const page = ref(1)
const total = ref(0)
const activeTab = ref('all')
const detailVisible = ref(false)
const currentBooking = ref(null)

const statusText = ['待支付', '已支付', '已入住', '已完成', '已取消']
const statusType = ['warning', 'primary', 'success', 'info', 'danger']

const goToPay = (bookingId) => {
  router.push({ path: '/payment', query: { bookingId } })
}

const getRoomImage = (roomId) => {
  const images = [
    'https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=200',
    'https://images.unsplash.com/photo-1590490360182-c33d57733427?w=200',
    'https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=200'
  ]
  return images[(roomId - 1) % 3]
}

const getNights = (booking) => {
  return dayjs(booking.checkOutDate).diff(dayjs(booking.checkInDate), 'day')
}

const fetchBookings = async () => {
  loading.value = true
  try {
    const res = await bookingApi.getMyBookings(page.value)
    let data = res.data.records
    // 前端筛选状态
    if (activeTab.value !== 'all') {
      data = data.filter(b => b.status === parseInt(activeTab.value))
    }
    bookings.value = data
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const handleCancel = async (id) => {
  await ElMessageBox.confirm('确定要取消此订单吗？取消后无法恢复。', '取消订单', { type: 'warning' })
  await bookingApi.cancel(id)
  ElMessage.success('订单已取消')
  fetchBookings()
}

const showDetail = (booking) => {
  currentBooking.value = booking
  detailVisible.value = true
}

onMounted(fetchBookings)
</script>

<style scoped>
.my-bookings { max-width: 900px; margin: 0 auto; padding: 20px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header h2 { margin: 0; }

.booking-card { margin-bottom: 16px; }
.booking-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 12px;
  border-bottom: 1px solid #eee;
  margin-bottom: 16px;
}
.order-info { display: flex; gap: 20px; }
.order-no { font-weight: bold; }
.order-time { color: #999; }

.booking-content { display: flex; gap: 20px; }
.room-image {
  width: 150px;
  height: 100px;
  background-size: cover;
  background-position: center;
  border-radius: 8px;
  flex-shrink: 0;
}
.booking-info { flex: 1; }
.booking-info h3 { margin: 0 0 12px; }
.info-row {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
  margin-bottom: 8px;
  font-size: 14px;
}

.booking-price {
  text-align: right;
  min-width: 120px;
}
.booking-price .price {
  font-size: 24px;
  font-weight: bold;
  color: #f56c6c;
  margin-bottom: 12px;
}
.actions { display: flex; flex-direction: column; gap: 4px; }
</style>
