<template>
  <div class="phone-booking">
    <h2>电话预订登记</h2>
    <el-card style="max-width: 600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="入住日期" prop="checkInDate">
          <el-date-picker v-model="form.checkInDate" type="date" placeholder="选择入住日期" style="width: 100%" />
        </el-form-item>
        <el-form-item label="离店日期" prop="checkOutDate">
          <el-date-picker v-model="form.checkOutDate" type="date" placeholder="选择离店日期" style="width: 100%" />
        </el-form-item>
        <el-form-item label="选择房间" prop="roomId">
          <el-select v-model="form.roomId" placeholder="请先选择日期" style="width: 100%" @focus="loadRooms">
            <el-option v-for="room in availableRooms" :key="room.id" :value="room.id" :label="`${room.roomNumber} - ${room.roomType?.name} (¥${room.roomType?.price}/晚)`" />
          </el-select>
        </el-form-item>
        <el-form-item label="入住人姓名" prop="guestName">
          <el-input v-model="form.guestName" />
        </el-form-item>
        <el-form-item label="联系电话" prop="guestPhone">
          <el-input v-model="form.guestPhone" />
        </el-form-item>
        <el-form-item label="身份证号">
          <el-input v-model="form.guestIdCard" />
        </el-form-item>
        <el-form-item label="总价" prop="totalPrice">
          <el-input-number v-model="form.totalPrice" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitBooking" :loading="loading">提交预订</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'
import { roomApi, adminApi } from '../../api'

const formRef = ref()
const loading = ref(false)
const availableRooms = ref([])

const form = reactive({
  checkInDate: '',
  checkOutDate: '',
  roomId: null,
  guestName: '',
  guestPhone: '',
  guestIdCard: '',
  totalPrice: 0,
  remark: ''
})

const rules = {
  checkInDate: [{ required: true, message: '请选择入住日期', trigger: 'change' }],
  checkOutDate: [{ required: true, message: '请选择离店日期', trigger: 'change' }],
  roomId: [{ required: true, message: '请选择房间', trigger: 'change' }],
  guestName: [{ required: true, message: '请输入入住人姓名', trigger: 'blur' }],
  guestPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
  totalPrice: [{ required: true, message: '请输入总价', trigger: 'blur' }]
}

const loadRooms = async () => {
  if (!form.checkInDate || !form.checkOutDate) {
    ElMessage.warning('请先选择入住和离店日期')
    return
  }
  const res = await roomApi.getAvailable(
    dayjs(form.checkInDate).format('YYYY-MM-DD'),
    dayjs(form.checkOutDate).format('YYYY-MM-DD')
  )
  availableRooms.value = res.data
}

const submitBooking = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    await adminApi.createPhoneBooking({
      ...form,
      checkInDate: dayjs(form.checkInDate).format('YYYY-MM-DD'),
      checkOutDate: dayjs(form.checkOutDate).format('YYYY-MM-DD')
    })
    ElMessage.success('预订登记成功')
    resetForm()
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  formRef.value?.resetFields()
  availableRooms.value = []
}
</script>

<style scoped>
.phone-booking h2 { margin-bottom: 20px; }
</style>
