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
            @change="filterRecords"
          />
        </el-form-item>
        <el-form-item label="房型">
          <el-select v-model="filterRoomType" placeholder="全部房型" clearable @change="filterRecords" style="width: 140px">
            <el-option label="经济单人间" value="经济单人间" />
            <el-option label="标准双人间" value="标准双人间" />
            <el-option label="豪华大床房" value="豪华大床房" />
            <el-option label="家庭亲子房" value="家庭亲子房" />
            <el-option label="总统套房" value="总统套房" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 收入明细表格 -->
    <el-card>
      <template #header>
        <div class="card-header">
          <span>住房收入明细</span>
          <span class="summary">
            筛选结果: <b>{{ filteredRecords.length }}</b> 笔，
            合计: <b class="income-text">¥{{ filteredTotal.toLocaleString() }}</b>
          </span>
        </div>
      </template>
      
      <el-table :data="paginatedRecords" stripe>
        <el-table-column prop="payTime" label="支付时间" width="180" />
        <el-table-column prop="orderNo" label="订单号" width="200" />
        <el-table-column prop="roomNumber" label="房间号" width="100" />
        <el-table-column prop="roomType" label="房型" width="120" />
        <el-table-column prop="guestName" label="入住人" width="100" />
        <el-table-column prop="nights" label="入住天数" width="100">
          <template #default="{ row }">{{ row.nights }}晚</template>
        </el-table-column>
        <el-table-column prop="amount" label="金额" width="120">
          <template #default="{ row }">
            <span class="income-text">¥{{ row.amount.toLocaleString() }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="checkInDate" label="入住日期" />
      </el-table>

      <el-pagination
        v-model:current-page="page"
        :page-size="pageSize"
        :total="filteredRecords.length"
        layout="total, prev, pager, next"
        style="margin-top: 16px; justify-content: flex-end;"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import dayjs from 'dayjs'

const page = ref(1)
const pageSize = ref(10)
const dateRange = ref([])
const filterRoomType = ref('')

const stats = reactive({
  totalIncome: 0,
  monthIncome: 0,
  todayIncome: 0,
  totalOrders: 0
})

// 模拟住房收入数据
const records = ref([])

const roomTypes = ['经济单人间', '标准双人间', '豪华大床房', '家庭亲子房', '总统套房']
const prices = [168, 268, 388, 458, 1288]
const names = ['张三', '李四', '王五', '赵六', '钱七', '孙八', '周九', '吴十']

// 生成模拟数据
const generateMockData = () => {
  const mockRecords = []
  const today = dayjs()
  
  for (let i = 0; i < 60; i++) {
    const daysAgo = Math.floor(Math.random() * 60)
    const date = today.subtract(daysAgo, 'day')
    const typeIdx = Math.floor(Math.random() * 5)
    const nights = Math.floor(Math.random() * 4) + 1
    const floor = typeIdx + 1
    const roomNum = Math.floor(Math.random() * 6) + 1
    
    mockRecords.push({
      id: i + 1,
      orderNo: 'ORD' + date.format('YYYYMMDDHHmmss') + (1000 + i),
      roomNumber: `${floor}0${roomNum}`,
      roomType: roomTypes[typeIdx],
      guestName: names[Math.floor(Math.random() * names.length)],
      nights: nights,
      amount: prices[typeIdx] * nights,
      checkInDate: date.format('YYYY-MM-DD'),
      payTime: date.format('YYYY-MM-DD HH:mm:ss')
    })
  }
  
  // 按时间排序
  mockRecords.sort((a, b) => new Date(b.payTime) - new Date(a.payTime))
  return mockRecords
}

const filteredRecords = computed(() => {
  let result = [...records.value]
  
  // 按房型筛选
  if (filterRoomType.value) {
    result = result.filter(r => r.roomType === filterRoomType.value)
  }
  
  // 按日期筛选
  if (dateRange.value && dateRange.value.length === 2) {
    const [start, end] = dateRange.value
    result = result.filter(r => {
      const date = r.payTime.split(' ')[0]
      return date >= start && date <= end
    })
  }
  
  return result
})

const filteredTotal = computed(() => {
  return filteredRecords.value.reduce((sum, r) => sum + r.amount, 0)
})

const paginatedRecords = computed(() => {
  const start = (page.value - 1) * pageSize.value
  return filteredRecords.value.slice(start, start + pageSize.value)
})

const filterRecords = () => {
  page.value = 1
}

const resetFilter = () => {
  dateRange.value = []
  filterRoomType.value = ''
  page.value = 1
}

const calculateStats = () => {
  const today = dayjs().format('YYYY-MM-DD')
  const monthStart = dayjs().startOf('month').format('YYYY-MM-DD')
  
  stats.totalIncome = records.value.reduce((sum, r) => sum + r.amount, 0)
  stats.monthIncome = records.value
    .filter(r => r.payTime.split(' ')[0] >= monthStart)
    .reduce((sum, r) => sum + r.amount, 0)
  stats.todayIncome = records.value
    .filter(r => r.payTime.split(' ')[0] === today)
    .reduce((sum, r) => sum + r.amount, 0)
  stats.totalOrders = records.value.length
}

onMounted(() => {
  records.value = generateMockData()
  calculateStats()
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
