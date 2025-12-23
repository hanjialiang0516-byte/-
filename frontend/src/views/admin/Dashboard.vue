<template>
  <div class="dashboard">
    <h2>🏠 房态总控面板</h2>
    
    <el-row :gutter="16" class="stat-row">
      <el-col :span="6" v-for="stat in roomStats" :key="stat.status">
        <el-card shadow="hover" :class="['stat-card', 'status-' + stat.status]">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon size="30"><component :is="stat.icon" /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stat.count }}</div>
              <div class="stat-label">{{ stat.name }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <div class="legend">
      <span class="status-item" v-for="(name, index) in statusNames" :key="index">
        <span class="dot" :class="'status-' + index"></span>{{ name }}
      </span>
    </div>

    <div class="floor-section" v-for="floor in floors" :key="floor">
      <h3>{{ floor }}楼</h3>
      <el-row :gutter="16">
        <el-col :span="4" v-for="room in getRoomsByFloor(floor)" :key="room.id">
          <el-card :class="['room-card', 'status-' + room.status]" @click="openStatusDialog(room)">
            <div class="room-image" :style="{ backgroundImage: `url(${getRoomImage(room.roomTypeId)})` }">
              <div class="room-status-badge">{{ statusNames[room.status] }}</div>
            </div>
            <div class="room-info">
              <div class="room-number">{{ room.roomNumber }}</div>
              <div class="room-type">{{ room.roomType?.name }}</div>
              <div class="room-price">¥{{ room.roomType?.price }}/晚</div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <el-dialog v-model="dialogVisible" title="修改房间状态" width="450px">
      <div class="dialog-room-info" v-if="selectedRoom">
        <div class="room-preview" :style="{ backgroundImage: `url(${getRoomImage(selectedRoom.roomTypeId)})` }"></div>
        <div class="room-details">
          <h3>{{ selectedRoom.roomNumber }}号房</h3>
          <p>{{ selectedRoom.roomType?.name }}</p>
          <p>当前状态：<el-tag :type="statusType[selectedRoom.status]">{{ statusNames[selectedRoom.status] }}</el-tag></p>
        </div>
      </div>
      <el-divider />
      <el-form label-width="80px">
        <el-form-item label="修改为">
          <el-radio-group v-model="newStatus">
            <el-radio-button v-for="(name, index) in statusNames" :key="index" :value="index">{{ name }}</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="remark" placeholder="可选填备注信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="updateStatus">确定修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { House, Calendar, User, Warning, Tools } from '@element-plus/icons-vue'
import { adminApi } from '../../api'

const rooms = ref([])
const dialogVisible = ref(false)
const selectedRoom = ref(null)
const newStatus = ref(0)
const remark = ref('')

const statusNames = ['空闲', '已预订', '入住中', '清洁中', '维修中']
const statusType = ['success', 'warning', 'primary', 'info', 'danger']

const roomStats = computed(() => [
  { status: 0, name: '空闲', count: rooms.value.filter(r => r.status === 0).length, icon: House },
  { status: 1, name: '已预订', count: rooms.value.filter(r => r.status === 1).length, icon: Calendar },
  { status: 2, name: '入住中', count: rooms.value.filter(r => r.status === 2).length, icon: User },
  { status: 4, name: '维修中', count: rooms.value.filter(r => r.status === 4).length, icon: Tools }
])

const floors = computed(() => {
  const floorSet = new Set(rooms.value.map(r => r.floor))
  return Array.from(floorSet).sort((a, b) => a - b)
})

const getRoomsByFloor = (floor) => {
  return rooms.value.filter(r => r.floor === floor).sort((a, b) => a.roomNumber.localeCompare(b.roomNumber))
}

const getRoomImage = (typeId) => {
  const images = [
    'https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=200',
    'https://images.unsplash.com/photo-1590490360182-c33d57733427?w=200',
    'https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=200',
    'https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=200',
    'https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=200'
  ]
  return images[(typeId - 1) % 5]
}

const fetchRooms = async () => {
  const res = await adminApi.getRooms()
  rooms.value = res.data
}

const openStatusDialog = (room) => {
  selectedRoom.value = room
  newStatus.value = room.status
  remark.value = room.remark || ''
  dialogVisible.value = true
}

const updateStatus = async () => {
  await adminApi.updateRoomStatus(selectedRoom.value.id, newStatus.value)
  ElMessage.success('状态已更新')
  dialogVisible.value = false
  fetchRooms()
}

onMounted(fetchRooms)
</script>

<style scoped>
.dashboard h2 { margin-bottom: 20px; }

.stat-row { margin-bottom: 30px; }
.stat-card { cursor: default; }
.stat-card.status-0 { border-top: 3px solid #67c23a; }
.stat-card.status-1 { border-top: 3px solid #e6a23c; }
.stat-card.status-2 { border-top: 3px solid #409eff; }
.stat-card.status-4 { border-top: 3px solid #f56c6c; }
.stat-content { display: flex; align-items: center; gap: 16px; }
.stat-icon { width: 60px; height: 60px; border-radius: 50%; background: #f5f7fa; display: flex; align-items: center; justify-content: center; }
.stat-value { font-size: 28px; font-weight: bold; }
.stat-label { color: #909399; }

.legend { margin-bottom: 20px; display: flex; gap: 24px; }
.status-item { display: flex; align-items: center; gap: 6px; }
.dot { width: 12px; height: 12px; border-radius: 50%; }
.dot.status-0 { background: #67c23a; }
.dot.status-1 { background: #e6a23c; }
.dot.status-2 { background: #409eff; }
.dot.status-3 { background: #909399; }
.dot.status-4 { background: #f56c6c; }

.floor-section { margin-bottom: 30px; }
.floor-section h3 { margin-bottom: 16px; padding-bottom: 8px; border-bottom: 2px solid #409eff; }

.room-card { margin-bottom: 16px; cursor: pointer; transition: all 0.3s; overflow: hidden; }
.room-card:hover { transform: scale(1.03); box-shadow: 0 4px 12px rgba(0,0,0,0.15); }
.room-card :deep(.el-card__body) { padding: 0; }
.room-card.status-0 { border-top: 3px solid #67c23a; }
.room-card.status-1 { border-top: 3px solid #e6a23c; }
.room-card.status-2 { border-top: 3px solid #409eff; }
.room-card.status-3 { border-top: 3px solid #909399; }
.room-card.status-4 { border-top: 3px solid #f56c6c; }

.room-image {
  height: 80px;
  background-size: cover;
  background-position: center;
  position: relative;
}
.room-status-badge {
  position: absolute;
  top: 5px;
  right: 5px;
  background: rgba(0,0,0,0.6);
  color: #fff;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}
.room-info { padding: 10px; text-align: center; }
.room-number { font-size: 18px; font-weight: bold; }
.room-type { color: #666; font-size: 12px; margin: 4px 0; }
.room-price { color: #f56c6c; font-size: 13px; }

.dialog-room-info { display: flex; gap: 20px; }
.room-preview { width: 150px; height: 100px; background-size: cover; background-position: center; border-radius: 8px; }
.room-details h3 { margin: 0 0 8px; }
.room-details p { margin: 4px 0; color: #666; }
</style>
