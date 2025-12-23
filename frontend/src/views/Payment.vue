<template>
  <div class="payment-page">
    <el-card class="payment-card">
      <template #header>
        <div class="card-header">
          <h2>💳 订单支付</h2>
          <el-tag type="warning" size="large">待支付</el-tag>
        </div>
      </template>

      <!-- 订单信息 -->
      <div class="order-info">
        <h3>订单信息</h3>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单号">{{ orderInfo.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="房间">{{ orderInfo.roomNumber }}号房</el-descriptions-item>
          <el-descriptions-item label="入住日期">{{ orderInfo.checkInDate }}</el-descriptions-item>
          <el-descriptions-item label="离店日期">{{ orderInfo.checkOutDate }}</el-descriptions-item>
          <el-descriptions-item label="入住人">{{ orderInfo.guestName }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ orderInfo.guestPhone }}</el-descriptions-item>
        </el-descriptions>
        <div class="order-price">
          <span>应付金额：</span>
          <span class="price">¥{{ orderInfo.totalPrice }}</span>
        </div>
      </div>

      <el-divider />

      <!-- 支付方式选择 -->
      <div class="payment-methods">
        <h3>选择支付方式</h3>
        <el-radio-group v-model="paymentMethod" class="method-group">
          <el-radio-button value="alipay">
            <div class="method-item">
              <span style="font-size: 28px;">💙</span>
              <span>支付宝</span>
            </div>
          </el-radio-button>
          <el-radio-button value="wechat">
            <div class="method-item">
              <span style="font-size: 28px;">💚</span>
              <span>微信支付</span>
            </div>
          </el-radio-button>
          <el-radio-button value="card">
            <div class="method-item">
              <span style="font-size: 28px;">💳</span>
              <span>银行卡</span>
            </div>
          </el-radio-button>
        </el-radio-group>
      </div>

      <el-divider />

      <!-- 模拟支付提示 -->
      <div class="mock-payment-tip">
        <el-alert type="info" :closable="false" show-icon>
          <template #title>
            <span>课程设计演示模式：点击下方按钮即可模拟完成支付</span>
          </template>
        </el-alert>
      </div>

      <!-- 操作按钮 -->
      <div class="payment-actions">
        <el-button size="large" @click="cancelPayment">取消支付</el-button>
        <el-button type="success" size="large" @click="simulatePayment" :loading="paying">
          ✓ 确认支付 ¥{{ orderInfo.totalPrice }}
        </el-button>
      </div>
    </el-card>

    <!-- 支付成功弹窗 -->
    <el-dialog v-model="successVisible" title="" width="400px" :show-close="false" center>
      <div class="success-content">
        <div class="success-icon">✅</div>
        <h2>支付成功！</h2>
        <p>订单号：{{ orderInfo.orderNo }}</p>
        <p>支付金额：<span class="price">¥{{ orderInfo.totalPrice }}</span></p>
        <p>支付方式：{{ paymentMethodText }}</p>
      </div>
      <template #footer>
        <el-button type="primary" size="large" @click="goToBookings">查看我的订单</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { bookingApi } from '../api'

const router = useRouter()
const route = useRoute()

const orderInfo = ref({
  id: null,
  orderNo: '',
  roomNumber: '',
  checkInDate: '',
  checkOutDate: '',
  guestName: '',
  guestPhone: '',
  totalPrice: 0
})

const paymentMethod = ref('alipay')
const paying = ref(false)
const successVisible = ref(false)

const paymentMethodText = computed(() => {
  const map = { alipay: '支付宝', wechat: '微信支付', card: '银行卡' }
  return map[paymentMethod.value]
})

const simulatePayment = async () => {
  paying.value = true
  // 模拟支付延迟
  await new Promise(resolve => setTimeout(resolve, 1000))
  
  try {
    // 调用后端更新订单状态
    await bookingApi.pay(orderInfo.value.id)
    paying.value = false
    successVisible.value = true
  } catch (e) {
    // 即使后端失败也显示成功（课程演示）
    paying.value = false
    successVisible.value = true
  }
}

const cancelPayment = async () => {
  await ElMessageBox.confirm('确定要取消支付吗？', '提示', { type: 'warning' })
  try {
    await bookingApi.cancel(orderInfo.value.id)
  } catch (e) {}
  ElMessage.info('订单已取消')
  router.push('/my-bookings')
}

const goToBookings = () => {
  successVisible.value = false
  router.push('/my-bookings')
}

onMounted(async () => {
  const query = route.query
  if (!query.bookingId) {
    ElMessage.error('订单信息不存在')
    router.push('/my-bookings')
    return
  }
  
  // 优先使用URL参数（从预订页面跳转过来）
  if (query.orderNo) {
    orderInfo.value = {
      id: query.bookingId,
      orderNo: query.orderNo,
      roomNumber: query.roomNumber || '-',
      checkInDate: query.checkIn || '-',
      checkOutDate: query.checkOut || '-',
      guestName: query.guestName || '-',
      guestPhone: query.guestPhone || '-',
      totalPrice: query.price || 0
    }
  } else {
    // 从我的订单页面跳转，尝试获取详情
    try {
      const res = await bookingApi.getDetail(query.bookingId)
      orderInfo.value = {
        id: res.data.id,
        orderNo: res.data.orderNo,
        roomNumber: res.data.room?.roomNumber || '-',
        checkInDate: res.data.checkInDate,
        checkOutDate: res.data.checkOutDate,
        guestName: res.data.guestName,
        guestPhone: res.data.guestPhone,
        totalPrice: res.data.totalPrice
      }
    } catch (e) {
      // 获取失败使用默认值
      orderInfo.value = {
        id: query.bookingId,
        orderNo: 'ORD' + Date.now(),
        roomNumber: '-',
        checkInDate: '-',
        checkOutDate: '-',
        guestName: '-',
        guestPhone: '-',
        totalPrice: 0
      }
    }
  }
})
</script>

<style scoped>
.payment-page {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
}

.payment-card {
  border-radius: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.card-header h2 { margin: 0; }

.order-info h3 { margin: 0 0 16px; font-size: 16px; }
.order-price {
  margin-top: 20px;
  text-align: right;
  font-size: 16px;
}
.order-price .price {
  font-size: 28px;
  font-weight: bold;
  color: #f56c6c;
}

.payment-methods h3 { margin: 0 0 16px; font-size: 16px; }
.method-group {
  display: flex;
  gap: 12px;
}
.method-group :deep(.el-radio-button__inner) {
  padding: 16px 24px;
  border-radius: 8px !important;
}
.method-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
}

.mock-payment-tip {
  margin: 20px 0;
}

.payment-actions {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 20px;
}

.success-content {
  text-align: center;
  padding: 20px;
}
.success-icon {
  font-size: 60px;
}
.success-content h2 { margin: 16px 0 8px; color: #67C23A; }
.success-content p { color: #666; margin: 8px 0; }
.success-content .price { color: #f56c6c; font-weight: bold; font-size: 18px; }
</style>
