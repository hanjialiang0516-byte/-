<template>
  <div class="admin-bookings">
    <h2>预订订单管理</h2>
    <el-select v-model="statusFilter" placeholder="筛选状态" clearable @change="fetchBookings" style="margin-bottom: 20px">
      <el-option :value="0" label="待确认" />
      <el-option :value="1" label="已确认" />
      <el-option :value="2" label="已入住" />
      <el-option :value="3" label="已退房" />
      <el-option :value="4" label="已取消" />
    </el-select>

    <el-table :data="bookings" v-loading="loading" stripe>
      <el-table-column prop="orderNo" label="订单号" width="180" />
      <el-table-column prop="guestName" label="入住人" width="100" />
      <el-table-column prop="guestPhone" label="电话" width="130" />
      <el-table-column prop="checkInDate" label="入住日期" width="110" />
      <el-table-column prop="checkOutDate" label="离店日期" width="110" />
      <el-table-column prop="totalPrice" label="总价" width="90">
        <template #default="{ row }">¥{{ row.totalPrice }}</template>
      </el-table-column>
      <el-table-column prop="source" label="来源" width="90">
        <template #default="{ row }">{{ sourceText[row.source] }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="statusType[row.status]">{{ statusText[row.status] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" min-width="200">
        <template #default="{ row }">
          <el-button v-if="row.status === 0" type="primary" text @click="confirm(row.id)">确认</el-button>
          <el-button v-if="row.status === 1" type="success" text @click="checkIn(row.id)">入住</el-button>
          <el-button v-if="row.status === 2" type="info" text @click="checkOut(row.id)">退房</el-button>
          <el-button v-if="row.status < 2" type="danger" text @click="cancel(row.id)">取消</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination v-model:current-page="page" :page-size="10" :total="total" @current-change="fetchBookings" layout="prev, pager, next" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminApi } from '../../api'

const bookings = ref([])
const loading = ref(false)
const page = ref(1)
const total = ref(0)
const statusFilter = ref(null)

const statusText = ['待确认', '已确认', '已入住', '已退房', '已取消']
const statusType = ['warning', 'primary', 'success', 'info', 'danger']
const sourceText = ['在线预订', '电话预订', '现场预订']

const fetchBookings = async () => {
  loading.value = true
  try {
    const res = await adminApi.getBookings(page.value, 10, statusFilter.value)
    bookings.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const confirm = async (id) => {
  await adminApi.confirmBooking(id)
  ElMessage.success('已确认')
  fetchBookings()
}

const checkIn = async (id) => {
  await adminApi.checkIn(id)
  ElMessage.success('已入住')
  fetchBookings()
}

const checkOut = async (id) => {
  await adminApi.checkOut(id)
  ElMessage.success('已退房')
  fetchBookings()
}

const cancel = async (id) => {
  await ElMessageBox.confirm('确定要取消此订单吗？', '提示', { type: 'warning' })
  await adminApi.cancelBooking(id)
  ElMessage.success('已取消')
  fetchBookings()
}

onMounted(fetchBookings)
</script>

<style scoped>
.admin-bookings h2 { margin-bottom: 20px; }
.el-pagination { margin-top: 20px; }
</style>
