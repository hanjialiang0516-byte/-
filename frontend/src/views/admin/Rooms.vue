<template>
  <div class="admin-rooms">
    <h2>客房资源管理</h2>
    
    <el-tabs v-model="activeTab">
      <el-tab-pane label="房间管理" name="rooms">
        <el-button type="primary" @click="openRoomDialog()" style="margin-bottom: 16px">添加房间</el-button>
        <el-table :data="rooms" stripe>
          <el-table-column prop="roomNumber" label="房间号" width="120" />
          <el-table-column label="房型" width="150">
            <template #default="{ row }">{{ row.roomType?.name }}</template>
          </el-table-column>
          <el-table-column prop="floor" label="楼层" width="80" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="statusType[row.status]">{{ statusText[row.status] }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" />
          <el-table-column label="操作" width="150">
            <template #default="{ row }">
              <el-button type="primary" text @click="openRoomDialog(row)">编辑</el-button>
              <el-button type="danger" text @click="deleteRoom(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      
      <el-tab-pane label="房型管理" name="types">
        <el-button type="primary" @click="openTypeDialog()" style="margin-bottom: 16px">添加房型</el-button>
        <el-table :data="roomTypes" stripe>
          <el-table-column prop="name" label="房型名称" width="150" />
          <el-table-column prop="price" label="价格" width="100">
            <template #default="{ row }">¥{{ row.price }}</template>
          </el-table-column>
          <el-table-column prop="capacity" label="可住人数" width="100" />
          <el-table-column prop="bedType" label="床型" width="120" />
          <el-table-column prop="area" label="面积" width="80">
            <template #default="{ row }">{{ row.area }}㎡</template>
          </el-table-column>
          <el-table-column prop="description" label="描述" />
          <el-table-column label="操作" width="150">
            <template #default="{ row }">
              <el-button type="primary" text @click="openTypeDialog(row)">编辑</el-button>
              <el-button type="danger" text @click="deleteType(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="roomDialogVisible" :title="roomForm.id ? '编辑房间' : '添加房间'" width="500px">
      <el-form :model="roomForm" label-width="80px">
        <el-form-item label="房间号" v-if="roomForm.id">
          <el-input v-model="roomForm.roomNumber" disabled />
        </el-form-item>
        <el-form-item label="房型">
          <el-select v-model="roomForm.roomTypeId" style="width: 100%" placeholder="请选择房型">
            <el-option v-for="t in roomTypes" :key="t.id" :value="t.id" :label="t.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="楼层">
          <el-input-number v-model="roomForm.floor" :min="1" :max="99" @change="generateRoomNumber" />
          <span style="margin-left: 12px; color: #909399;" v-if="!roomForm.id">
            将创建房间号: <b style="color: #409EFF;">{{ nextRoomNumber }}</b>
          </span>
        </el-form-item>
        <el-form-item label="备注"><el-input v-model="roomForm.remark" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="roomDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveRoom">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="typeDialogVisible" :title="typeForm.id ? '编辑房型' : '添加房型'" width="500px">
      <el-form :model="typeForm" label-width="80px">
        <el-form-item label="名称"><el-input v-model="typeForm.name" /></el-form-item>
        <el-form-item label="价格"><el-input-number v-model="typeForm.price" :min="0" /></el-form-item>
        <el-form-item label="可住人数"><el-input-number v-model="typeForm.capacity" :min="1" /></el-form-item>
        <el-form-item label="床型"><el-input v-model="typeForm.bedType" /></el-form-item>
        <el-form-item label="面积"><el-input-number v-model="typeForm.area" :min="0" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="typeForm.description" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="typeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveType">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
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

// 计算该楼层下一个房间号
const nextRoomNumber = computed(() => {
  const floor = roomForm.floor
  // 获取该楼层所有房间号
  const floorRooms = rooms.value.filter(r => r.floor === floor)
  if (floorRooms.length === 0) {
    return `${floor}01`
  }
  // 找出该楼层最大的房间号
  const maxNum = Math.max(...floorRooms.map(r => parseInt(r.roomNumber)))
  const nextNum = maxNum + 1
  // 如果下一个号码超出该楼层范围，提示
  if (nextNum >= (floor + 1) * 100) {
    return '该楼层已满'
  }
  return String(nextNum)
})

const generateRoomNumber = () => {
  // 楼层变化时自动更新房间号
  roomForm.roomNumber = nextRoomNumber.value
}

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
    // 新建时自动生成房间号
    roomForm.roomNumber = nextRoomNumber.value
  }
  roomDialogVisible.value = true
}

const saveRoom = async () => {
  if (!roomForm.roomTypeId) {
    ElMessage.warning('请选择房型')
    return
  }
  // 新建时使用自动生成的房间号
  if (!roomForm.id) {
    roomForm.roomNumber = nextRoomNumber.value
    if (roomForm.roomNumber === '该楼层已满') {
      ElMessage.warning('该楼层房间已满，请选择其他楼层')
      return
    }
  }
  if (roomForm.id) {
    await adminApi.updateRoom(roomForm.id, roomForm)
  } else {
    await adminApi.createRoom(roomForm)
  }
  ElMessage.success('保存成功')
  roomDialogVisible.value = false
  fetchData()
}

const deleteRoom = async (id) => {
  await ElMessageBox.confirm('确定删除此房间？', '提示', { type: 'warning' })
  await adminApi.deleteRoom(id)
  ElMessage.success('已删除')
  fetchData()
}

const openTypeDialog = (type = null) => {
  if (type) {
    Object.assign(typeForm, type)
  } else {
    Object.assign(typeForm, { id: null, name: '', price: 0, capacity: 2, bedType: '', area: 0, description: '' })
  }
  typeDialogVisible.value = true
}

const saveType = async () => {
  if (typeForm.id) {
    await adminApi.updateRoomType(typeForm.id, typeForm)
  } else {
    await adminApi.createRoomType(typeForm)
  }
  ElMessage.success('保存成功')
  typeDialogVisible.value = false
  fetchData()
}

const deleteType = async (id) => {
  await ElMessageBox.confirm('确定删除此房型？', '提示', { type: 'warning' })
  await adminApi.deleteRoomType(id)
  ElMessage.success('已删除')
  fetchData()
}

onMounted(fetchData)
</script>

<style scoped>
.admin-rooms h2 { margin-bottom: 20px; }
</style>
