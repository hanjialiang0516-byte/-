<template>
  <div class="booking-page">
    <el-card class="search-card">
      <h3>🔍 搜索可用房间</h3>
      <el-form inline>
        <el-form-item label="入住日期">
          <el-date-picker v-model="checkIn" type="date" placeholder="选择入住日期" :disabled-date="disablePastDate" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="离店日期">
          <el-date-picker v-model="checkOut" type="date" placeholder="选择离店日期" :disabled-date="disableCheckOutDate" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchRooms" :loading="loading">
            <el-icon><Search /></el-icon> 查询
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <div class="filter-bar" v-if="searched">
      <span>共找到 <b>{{ rooms.length }}</b> 间可用房间</span>
      <div class="filters">
        <el-select v-model="sortBy" placeholder="排序方式" style="width: 140px" @change="sortRooms">
          <el-option label="价格从低到高" value="priceAsc" />
          <el-option label="价格从高到低" value="priceDesc" />
          <el-option label="面积从大到小" value="areaDesc" />
        </el-select>
        <el-select v-model="filterType" placeholder="房型筛选" clearable style="width: 140px" @change="filterRooms">
          <el-option v-for="t in roomTypes" :key="t.id" :label="t.name" :value="t.id" />
        </el-select>
      </div>
    </div>

    <div class="room-list" v-if="filteredRooms.length">
      <el-card v-for="room in filteredRooms" :key="room.id" class="room-item">
        <div class="room-image" :style="{ backgroundImage: `url(${getRoomImage(room.roomTypeId)})` }"></div>
        <div class="room-info">
          <div class="room-header">
            <h3>{{ room.roomNumber }}号房 - {{ room.roomType?.name }}</h3>
            <div class="room-tags">
              <el-tag size="small">{{ room.floor }}楼</el-tag>
              <el-tag size="small" type="success">可预订</el-tag>
            </div>
          </div>
          <p class="room-desc">{{ room.roomType?.description }}</p>
          <div class="room-features">
            <span><el-icon><User /></el-icon> {{ room.roomType?.capacity }}人入住</span>
            <span><el-icon><House /></el-icon> {{ room.roomType?.area }}㎡</span>
            <span>{{ room.roomType?.bedType }}</span>
          </div>
          <div class="room-amenities">
            <el-tag size="small" type="info" v-for="a in ['免费WiFi', '空调', '电视', '独立卫浴']" :key="a">{{ a }}</el-tag>
          </div>
        </div>
        <div class="room-action">
          <div class="price">
            <span class="amount">¥{{ room.roomType?.price }}</span>
            <span class="unit">/晚</span>
          </div>
          <div class="total" v-if="nights > 0">
            共 {{ nights }} 晚，总计 <b>¥{{ (room.roomType?.price * nights).toFixed(0) }}</b>
          </div>
          <el-button type="primary" size="large" @click="openBookingDialog(room)">立即预订</el-button>
        </div>
      </el-card>
    </div>
    <el-empty v-else-if="searched" description="暂无可用房间，请尝试其他日期" />
    <el-empty v-else description="请选择入住和离店日期后查询" />

    <!-- 预订弹窗 -->
    <el-dialog v-model="dialogVisible" title="确认预订信息" width="550px">
      <div class="booking-summary" v-if="selectedRoom">
        <div class="summary-item">
          <label>房间</label>
          <span>{{ selectedRoom.roomNumber }}号 - {{ selectedRoom.roomType?.name }}</span>
        </div>
        <div class="summary-item">
          <label>入住日期</label>
          <span>{{ checkIn }}</span>
        </div>
        <div class="summary-item">
          <label>离店日期</label>
          <span>{{ checkOut }}</span>
        </div>
        <div class="summary-item">
          <label>入住天数</label>
          <span>{{ nights }} 晚</span>
        </div>
        <div class="summary-item total">
          <label>订单总价</label>
          <span class="price">¥{{ totalPrice }}</span>
        </div>
      </div>
      
      <el-divider />
      
      <el-form :model="bookingForm" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="入住人姓名" prop="guestName">
          <el-input v-model="bookingForm.guestName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="联系电话" prop="guestPhone">
          <el-input v-model="bookingForm.guestPhone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="身份证号" prop="guestIdCard">
          <el-input v-model="bookingForm.guestIdCard" placeholder="请输入身份证号（选填）" />
        </el-form-item>
        <el-form-item label="特殊要求">
          <el-input v-model="bookingForm.remark" type="textarea" :rows="2" placeholder="如需要安静房间、加床等" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitBooking" :loading="submitting">确认预订</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, User, House } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import { roomApi, roomTypeApi, bookingApi } from '../api'
import { useUserStore } from '../store/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const checkIn = ref('')
const checkOut = ref('')
const rooms = ref([])
const roomTypes = ref([])
const loading = ref(false)
const searched = ref(false)
const dialogVisible = ref(false)
const selectedRoom = ref(null)
const submitting = ref(false)
const formRef = ref()
const sortBy = ref('')
const filterType = ref('')
const filteredRooms = ref([])

const bookingForm = reactive({ guestName: '', guestPhone: '', guestIdCard: '', remark: '' })
const rules = {
  guestName: [{ required: true, message: '请输入入住人姓名', trigger: 'blur' }],
  guestPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }, { pattern: /^1\d{10}$/, message: '请输入正确的手机号', trigger: 'blur' }]
}

const nights = computed(() => {
  if (!checkIn.value || !checkOut.value) return 0
  return dayjs(checkOut.value).diff(dayjs(checkIn.value), 'day')
})

const totalPrice = computed(() => {
  if (!selectedRoom.value) return 0
  return (nights.value * (selectedRoom.value.roomType?.price || 0)).toFixed(0)
})

const disablePastDate = (date) => date < new Date(new Date().setHours(0, 0, 0, 0))
const disableCheckOutDate = (date) => !checkIn.value || date <= new Date(checkIn.value)

const getRoomImage = (typeId) => {
  const images = [
    'https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=300',
    'https://images.unsplash.com/photo-1590490360182-c33d57733427?w=300',
    'https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=300',
    'https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=300',
    'https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=300'
  ]
  return images[(typeId - 1) % 5]
}

const searchRooms = async () => {
  if (!checkIn.value || !checkOut.value) {
    ElMessage.warning('请选择入住和离店日期')
    return
  }
  loading.value = true
  searched.value = true
  try {
    const res = await roomApi.getAvailable(checkIn.value, checkOut.value)
    rooms.value = res.data
    filteredRooms.value = [...res.data]
  } finally {
    loading.value = false
  }
}

const sortRooms = () => {
  const sorted = [...filteredRooms.value]
  if (sortBy.value === 'priceAsc') {
    sorted.sort((a, b) => a.roomType?.price - b.roomType?.price)
  } else if (sortBy.value === 'priceDesc') {
    sorted.sort((a, b) => b.roomType?.price - a.roomType?.price)
  } else if (sortBy.value === 'areaDesc') {
    sorted.sort((a, b) => b.roomType?.area - a.roomType?.area)
  }
  filteredRooms.value = sorted
}

const filterRooms = () => {
  if (filterType.value) {
    filteredRooms.value = rooms.value.filter(r => r.roomTypeId === filterType.value)
  } else {
    filteredRooms.value = [...rooms.value]
  }
  sortRooms()
}

const openBookingDialog = (room) => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录后再预订')
    router.push('/login')
    return
  }
  selectedRoom.value = room
  // 预填用户信息
  if (userStore.user) {
    bookingForm.guestName = userStore.user.realName || ''
    bookingForm.guestPhone = userStore.user.phone || ''
  }
  dialogVisible.value = true
}

const submitBooking = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    await bookingApi.create({
      roomId: selectedRoom.value.id,
      checkInDate: checkIn.value,
      checkOutDate: checkOut.value,
      totalPrice: totalPrice.value,
      ...bookingForm
    })
    ElMessage.success('预订成功！请在"我的预订"中查看订单')
    dialogVisible.value = false
    router.push('/my-bookings')
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  // 获取房型列表用于筛选
  const res = await roomTypeApi.getAll()
  roomTypes.value = res.data
  
  // 从URL获取日期参数
  if (route.query.checkIn && route.query.checkOut) {
    checkIn.value = route.query.checkIn
    checkOut.value = route.query.checkOut
    searchRooms()
  }
})
</script>

<style scoped>
.booking-page { max-width: 1000px; margin: 0 auto; padding: 20px; }
.search-card { margin-bottom: 20px; }
.search-card h3 { margin-bottom: 20px; }

.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 12px 16px;
  background: #fff;
  border-radius: 8px;
}
.filters { display: flex; gap: 12px; }

.room-item {
  margin-bottom: 16px;
  display: flex;
  overflow: hidden;
}
.room-item :deep(.el-card__body) {
  display: flex;
  padding: 0;
  width: 100%;
}
.room-image {
  width: 250px;
  min-height: 200px;
  background-size: cover;
  background-position: center;
  flex-shrink: 0;
}
.room-info {
  flex: 1;
  padding: 20px;
}
.room-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}
.room-header h3 { margin: 0; }
.room-tags { display: flex; gap: 8px; }
.room-desc { color: #666; margin-bottom: 12px; }
.room-features {
  display: flex;
  gap: 20px;
  color: #999;
  font-size: 14px;
  margin-bottom: 12px;
}
.room-features span { display: flex; align-items: center; gap: 4px; }
.room-amenities { display: flex; gap: 8px; flex-wrap: wrap; }

.room-action {
  width: 180px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  border-left: 1px solid #eee;
  background: #fafafa;
}
.room-action .price { margin-bottom: 8px; }
.room-action .amount { font-size: 28px; color: #f56c6c; font-weight: bold; }
.room-action .unit { color: #999; }
.room-action .total { font-size: 13px; color: #666; margin-bottom: 16px; }

.booking-summary { background: #f5f7fa; padding: 16px; border-radius: 8px; }
.summary-item { display: flex; justify-content: space-between; padding: 8px 0; border-bottom: 1px dashed #e4e7ed; }
.summary-item:last-child { border-bottom: none; }
.summary-item label { color: #909399; }
.summary-item.total { font-size: 16px; font-weight: bold; }
.summary-item .price { color: #f56c6c; font-size: 20px; }
</style>
