<template>
  <div class="admin-rooms">
    <h2>🏠 客房资源管理</h2>
    
    <el-tabs v-model="activeTab" class="room-tabs">
      <el-tab-pane label="房间管理" name="rooms">
        <div class="toolbar">
          <el-button type="primary" @click="openRoomDialog()">
            <el-icon><Plus /></el-icon> 添加房间
          </el-button>
          <span class="count-info">共 {{ rooms.length }} 间房</span>
        </div>
        <el-table :data="rooms" stripe border>
          <el-table-column prop="roomNumber" label="房间号" width="100" align="center">
            <template #default="{ row }">
              <span class="room-num">{{ row.roomNumber }}</span>
            </template>
          </el-table-column>
          <el-table-column label="房型" width="130">
            <template #default="{ row }">
              <el-tag>{{ row.roomType?.name }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="floor" label="楼层" width="80" align="center">
            <template #default="{ row }">{{ row.floor }}F</template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="statusType[row.status]" effect="dark">{{ statusText[row.status] }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" show-overflow-tooltip />
          <el-table-column label="操作" width="180" align="center" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" size="small" @click="openRoomDialog(row)">
                <el-icon><Edit /></el-icon> 编辑
              </el-button>
              <el-button type="danger" size="small" @click="deleteRoom(row.id)">
                <el-icon><Delete /></el-icon> 删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      
      <el-tab-pane label="房型管理" name="types">
        <div class="toolbar">
          <el-button type="primary" @click="openTypeDialog()">
            <el-icon><Plus /></el-icon> 添加房型
          </el-button>
          <span class="count-info">共 {{ roomTypes.length }} 种房型</span>
        </div>
        <el-table :data="roomTypes" stripe border>
          <el-table-column prop="name" label="房型名称" width="130">
            <template #default="{ row }">
              <span class="type-name">{{ row.name }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="price" label="价格" width="120" align="center">
            <template #default="{ row }">
              <span class="price">¥{{ row.price }}</span>/晚
            </template>
          </el-table-column>
          <el-table-column prop="capacity" label="可住" width="80" align="center">
            <template #default="{ row }">{{ row.capacity }}人</template>
          </el-table-column>
          <el-table-column prop="bedType" label="床型" width="120" />
          <el-table-column prop="area" label="面积" width="80" align="center">
            <template #default="{ row }">{{ row.area }}㎡</template>
          </el-table-column>
          <el-table-column prop="description" label="描述" show-overflow-tooltip />
          <el-table-column label="操作" width="180" align="center" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" size="small" @click="openTypeDialog(row)">
                <el-icon><Edit /></el-icon> 编辑
              </el-button>
              <el-button type="danger" size="small" @click="deleteType(row.id)">
                <el-icon><Delete /></el-icon> 删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 房间弹窗 -->
    <el-dialog v-model="roomDialogVisible" :title="roomForm.id ? '✏️ 编辑房间' : '➕ 添加房间'" width="450px">
      <el-form :model="roomForm" label-width="80px">
        <el-form-item label="房间号" v-if="roomForm.id">
          <el-input v-model="roomForm.roomNumber" disabled />
        </el-form-item>
        <el-form-item label="房型" required>
          <el-select v-model="roomForm.roomTypeId" style="width: 100%" placeholder="请选择房型">
            <el-option v-for="t in roomTypes" :key="t.id" :value="t.id" :label="t.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="楼层" required>
          <el-input-number v-model="roomForm.floor" :min="1" :max="99" @change="generateRoomNumber" />
          <el-tag v-if="!roomForm.id" type="success" style="margin-left: 12px;">
            房间号: {{ nextRoomNumber }}
          </el-tag>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="roomForm.remark" type="textarea" :rows="2" placeholder="选填" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="roomDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveRoom">确认保存</el-button>
      </template>
    </el-dialog>

    <!-- 房型弹窗 -->
    <el-dialog v-model="typeDialogVisible" :title="typeForm.id ? '✏️ 编辑房型' : '➕ 添加房型'" width="500px">
      <el-form :model="typeForm" label-width="80px">
        <el-form-item label="名称" required>
          <el-input v-model="typeForm.name" placeholder="如：豪华大床房" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="价格" required>
              <el-input-number v-model="typeForm.price" :min="0" :step="50" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="面积">
              <el-input-number v-model="typeForm.area" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="可住人数">
              <el-input-number v-model="typeForm.capacity" :min="1" :max="10" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="床型">
              <el-input v-model="typeForm.bedType" placeholder="如：1.8米大床" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="描述">
          <el-input v-model="typeForm.description" type="textarea" :rows="2" placeholder="房型描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="typeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveType">确认保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import { adminApi } from '../../api'

const activeTab = ref('rooms')
const rooms = ref([])
const roomTypes = ref([])
const roomDialogVisible = ref(false)
const typeDialogVisible = ref(false)
const roomForm = reactive({ id: null, roomNumber: '', roomTypeId: null, floor: 1, remark: '' })
const typeForm = reactive({ id: null, name: '', price: 0, capacity: 2, bedType: '', area: 0, description: '' })

const statusText = ['空闲', '已预订', '入住中', '清洁中', '维修中']
const statusType = ['success', 'warning', 'primary', 'info', 'danger']

const nextRoomNumber = computed(() => {
  const floor = roomForm.floor
  const floorRooms = rooms.value.filter(r => r.floor === floor)
  if (floorRooms.length === 0) return `${floor}01`
  const maxNum = Math.max(...floorRooms.map(r => parseInt(r.roomNumber)))
  const nextNum = maxNum + 1
  if (nextNum >= (floor + 1) * 100) return '该楼层已满'
  return String(nextNum)
})

const generateRoomNumber = () => { roomForm.roomNumber = nextRoomNumber.value }

const fetchData = async () => {
  const [roomsRes, typesRes] = await Promise.all([adminApi.getRooms(), adminApi.getRoomTypes()])
  rooms.value = roomsRes.data
  roomTypes.value = typesRes.data
}

const openRoomDialog = (room = null) => {
  if (room) {
    Object.assign(roomForm, room)
  } else {
    Object.assign(roomForm, { id: null, roomNumber: '', roomTypeId: null, floor: 1, remark: '' })
    roomForm.roomNumber = nextRoomNumber.value
  }
  roomDialogVisible.value = true
}

const saveRoom = async () => {
  if (!roomForm.roomTypeId) { ElMessage.warning('请选择房型'); return }
  if (!roomForm.id) {
    roomForm.roomNumber = nextRoomNumber.value
    if (roomForm.roomNumber === '该楼层已满') { ElMessage.warning('该楼层房间已满'); return }
  }
  roomForm.id ? await adminApi.updateRoom(roomForm.id, roomForm) : await adminApi.createRoom(roomForm)
  ElMessage.success('保存成功')
  roomDialogVisible.value = false
  fetchData()
}

const deleteRoom = async (id) => {
  await ElMessageBox.confirm('确定要删除此房间吗？', '删除确认', { type: 'warning' })
  await adminApi.deleteRoom(id)
  ElMessage.success('删除成功')
  fetchData()
}

const openTypeDialog = (type = null) => {
  type ? Object.assign(typeForm, type) : Object.assign(typeForm, { id: null, name: '', price: 0, capacity: 2, bedType: '', area: 0, description: '' })
  typeDialogVisible.value = true
}

const saveType = async () => {
  if (!typeForm.name) { ElMessage.warning('请输入房型名称'); return }
  typeForm.id ? await adminApi.updateRoomType(typeForm.id, typeForm) : await adminApi.createRoomType(typeForm)
  ElMessage.success('保存成功')
  typeDialogVisible.value = false
  fetchData()
}

const deleteType = async (id) => {
  await ElMessageBox.confirm('确定要删除此房型吗？', '删除确认', { type: 'warning' })
  await adminApi.deleteRoomType(id)
  ElMessage.success('删除成功')
  fetchData()
}

onMounted(fetchData)
</script>

<style scoped>
.admin-rooms h2 { margin-bottom: 20px; }
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.count-info { color: #909399; font-size: 14px; }
.room-num { font-weight: bold; color: #409EFF; font-size: 15px; }
.type-name { font-weight: 600; }
.price { color: #f56c6c; font-weight: bold; font-size: 15px; }
:deep(.el-dialog__header) { border-bottom: 1px solid #eee; padding-bottom: 15px; }
:deep(.el-dialog__footer) { border-top: 1px solid #eee; padding-top: 15px; }
</style>
