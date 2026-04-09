<template>
  <div class="finance-page">
    <h2>💰 经营统计</h2>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card total">
          <div class="stat-icon">💵</div>
          <div class="stat-info">
            <div class="stat-value">¥{{ stats.totalIncome.toLocaleString() }}</div>
            <div class="stat-label">累计营收</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card month">
          <div class="stat-icon">📅</div>
          <div class="stat-info">
            <div class="stat-value">¥{{ stats.monthIncome.toLocaleString() }}</div>
            <div class="stat-label">本月营收</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card today">
          <div class="stat-icon">📈</div>
          <div class="stat-info">
            <div class="stat-value">¥{{ stats.todayIncome.toLocaleString() }}</div>
            <div class="stat-label">今日营收</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card orders">
          <div class="stat-icon">🛏️</div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalOrders }}</div>
            <div class="stat-label">成交订单</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 筛选 -->
    <el-card class="filter-card">
      <el-form inline>
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            @change="fetchRecords"
          />
        </el-form-item>
        <el-form-item label="房型">
          <el-select v-model="filterRoomTypeId" placeholder="全部房型" clearable @change="fetchRecords" style="width: 140px">
            <el-option v-for="type in roomTypes" :key="type.id" :label="type.name" :value="type.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button @click="resetFilter">重置</el-button>
          <el-button type="primary" :loading="exporting" @click="handleExport">导出报表</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 收入明细表格 -->
    <el-card v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>住房收入明细</span>
          <span class="summary">
            筛选结果: <b>{{ records.length }}</b> 笔，
            合计: <b class="income-text">¥{{ filteredTotal.toLocaleString() }}</b>
          </span>
        </div>
      </template>
      
      <el-table :data="paginatedRecords" stripe>
        <el-table-column prop="payTime" label="支付时间" width="180">
          <template #default="{ row }">{{ row.payTime ? dayjs(row.payTime).format('YYYY-MM-DD HH:mm:ss') : '' }}</template>
        </el-table-column>
        <el-table-column prop="orderNo" label="订单号" width="200" />
        <el-table-column prop="roomNumber" label="房间号" width="100" />
        <el-table-column prop="roomTypeName" label="房型" width="120" />
        <el-table-column prop="guestName" label="入住人" width="100" />
        <el-table-column prop="stayDays" label="入住天数" width="100">
          <template #default="{ row }">{{ row.stayDays }}晚</template>
        </el-table-column>
        <el-table-column prop="amount" label="金额" width="120">
          <template #default="{ row }">
            <span class="income-text">¥{{ row.amount.toLocaleString() }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="checkInDate" label="入住日期">
          <template #default="{ row }">{{ row.checkInDate ? dayjs(row.checkInDate).format('YYYY-MM-DD') : '' }}</template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="page"
        :page-size="pageSize"
        :total="records.length"
        layout="total, prev, pager, next"
        style="margin-top: 16px; justify-content: flex-end;"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import dayjs from 'dayjs'
import { adminApi } from '../../api'
import { ElMessage } from 'element-plus'

const page = ref(1)
const pageSize = ref(10)
const dateRange = ref([])
const filterRoomTypeId = ref('')
const roomTypes = ref([])
const loading = ref(false)
const exporting = ref(false)

const stats = reactive({
  totalIncome: 0,
  monthIncome: 0,
  todayIncome: 0,
  totalOrders: 0
})

const records = ref([])

const fetchRoomTypes = async () => {
  try {
    const res = await adminApi.getRoomTypes()
    roomTypes.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

const fetchRecords = async () => {
  loading.value = true
  try {
    const params = {}
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }
    if (filterRoomTypeId.value) {
      params.roomTypeId = filterRoomTypeId.value
    }
    const res = await adminApi.getReportList(params)
    records.value = res.data || []
    page.value = 1
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const filteredTotal = computed(() => {
  return records.value.reduce((sum, r) => sum + r.amount, 0)
})

const paginatedRecords = computed(() => {
  const start = (page.value - 1) * pageSize.value
  return records.value.slice(start, start + pageSize.value)
})

const resetFilter = () => {
  dateRange.value = []
  filterRoomTypeId.value = ''
  fetchRecords()
}

const calculateStats = async () => {
  try {
    const res = await adminApi.getReportList({ startDate: '2000-01-01', endDate: dayjs().format('YYYY-MM-DD') })
    const allData = res.data || []
    
    const today = dayjs().format('YYYY-MM-DD')
    const monthStart = dayjs().startOf('month').format('YYYY-MM-DD')
    
    stats.totalIncome = allData.reduce((sum, r) => sum + r.amount, 0)
    stats.monthIncome = allData
      .filter(r => r.payTime && dayjs(r.payTime).format('YYYY-MM-DD') >= monthStart)
      .reduce((sum, r) => sum + r.amount, 0)
    stats.todayIncome = allData
      .filter(r => r.payTime && dayjs(r.payTime).format('YYYY-MM-DD') === today)
      .reduce((sum, r) => sum + r.amount, 0)
    stats.totalOrders = allData.length
  } catch (e) {
    console.error(e)
  }
}

const handleExport = async () => {
  exporting.value = true
  try {
    const params = {}
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }
    if (filterRoomTypeId.value) {
      params.roomTypeId = filterRoomTypeId.value
    }
    
    const response = await adminApi.exportReport(params)
    
    let filename = `住房收入明细_${dayjs().format('YYYYMMDD')}.xlsx`
    const disposition = response.headers['content-disposition']
    if (disposition && disposition.includes('filename=')) {
      const filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/
      const matches = filenameRegex.exec(disposition)
      if (matches != null && matches[1]) {
        filename = decodeURIComponent(matches[1].replace(/['"]/g, '').replace("utf-8''", ''))
      }
    }
    
    const blob = new Blob([response.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', filename)
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
  } catch (error) {
    // 拦截器已经处理错误提示
  } finally {
    exporting.value = false
  }
}

onMounted(async () => {
  await fetchRoomTypes()
  await calculateStats()
  await fetchRecords()
})
</script>

<style scoped>
.finance-page h2 { margin-bottom: 20px; }

.stat-cards { margin-bottom: 20px; }
.stat-card :deep(.el-card__body) {
  display: flex;
  align-items: center;
}
.stat-icon { font-size: 40px; margin-right: 16px; }
.stat-info { flex: 1; }
.stat-value { font-size: 24px; font-weight: bold; }
.stat-label { color: #909399; font-size: 14px; margin-top: 4px; }

.stat-card.total .stat-value { color: #67C23A; }
.stat-card.month .stat-value { color: #409EFF; }
.stat-card.today .stat-value { color: #E6A23C; }
.stat-card.orders .stat-value { color: #909399; }

.filter-card { margin-bottom: 20px; }
.filter-card :deep(.el-card__body) { padding-bottom: 2px; }

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.summary { font-size: 14px; color: #606266; }
.summary b { margin: 0 4px; }

.income-text { color: #67C23A; font-weight: bold; }
</style>
