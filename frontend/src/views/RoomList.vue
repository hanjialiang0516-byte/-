<template>
  <div class="room-list-page">
    <div class="page-hero">
      <h1>🛏️ 客房预览</h1>
      <p>精心设计的客房，为您打造舒适的休憩空间</p>
    </div>

    <div class="room-types">
      <el-card v-for="type in roomTypes" :key="type.id" class="room-type-card" shadow="hover">
        <el-row :gutter="0">
          <el-col :span="10">
            <el-carousel height="300px" :autoplay="false">
              <el-carousel-item v-for="i in 3" :key="i">
                <div class="room-image" :style="{ backgroundImage: `url(${getRoomImage(type.id, i)})` }"></div>
              </el-carousel-item>
            </el-carousel>
          </el-col>
          <el-col :span="14">
            <div class="room-detail">
              <div class="room-header">
                <h2>{{ type.name }}</h2>
                <div class="room-price">
                  <span class="price">¥{{ type.price }}</span>
                  <span class="unit">/晚起</span>
                </div>
              </div>
              
              <p class="room-desc">{{ type.description }}</p>
              
              <div class="room-specs">
                <div class="spec-item">
                  <el-icon><User /></el-icon>
                  <span>可住{{ type.capacity }}人</span>
                </div>
                <div class="spec-item">
                  <el-icon><House /></el-icon>
                  <span>{{ type.area }}㎡</span>
                </div>
                <div class="spec-item">
                  <el-icon><Bed /></el-icon>
                  <span>{{ type.bedType }}</span>
                </div>
              </div>

              <div class="room-amenities">
                <h4>房间设施</h4>
                <div class="amenity-tags">
                  <el-tag v-for="a in getAmenities(type.id)" :key="a" size="small">{{ a }}</el-tag>
                </div>
              </div>

              <div class="room-actions">
                <el-button type="primary" size="large" @click="goBooking(type)">
                  立即预订
                </el-button>
                <el-button size="large" @click="showDetail(type)">
                  查看详情
                </el-button>
              </div>
            </div>
          </el-col>
        </el-row>
      </el-card>
    </div>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" :title="currentType?.name" width="800px">
      <div class="detail-content" v-if="currentType">
        <el-carousel height="400px">
          <el-carousel-item v-for="i in 5" :key="i">
            <div class="detail-image" :style="{ backgroundImage: `url(${getRoomImage(currentType.id, i)})` }"></div>
          </el-carousel-item>
        </el-carousel>
        
        <div class="detail-info">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="房型名称">{{ currentType.name }}</el-descriptions-item>
            <el-descriptions-item label="房间价格">
              <span style="color: #f56c6c; font-size: 20px; font-weight: bold;">¥{{ currentType.price }}</span>/晚
            </el-descriptions-item>
            <el-descriptions-item label="房间面积">{{ currentType.area }}㎡</el-descriptions-item>
            <el-descriptions-item label="床型配置">{{ currentType.bedType }}</el-descriptions-item>
            <el-descriptions-item label="可住人数">{{ currentType.capacity }}人</el-descriptions-item>
            <el-descriptions-item label="楼层位置">{{ currentType.id }}楼</el-descriptions-item>
            <el-descriptions-item label="房型描述" :span="2">{{ currentType.description }}</el-descriptions-item>
          </el-descriptions>

          <h4 style="margin: 20px 0 12px;">房间设施</h4>
          <div class="amenity-tags">
            <el-tag v-for="a in allAmenities" :key="a" style="margin: 4px;">{{ a }}</el-tag>
          </div>

          <h4 style="margin: 20px 0 12px;">预订须知</h4>
          <ul class="notice-list">
            <li>入住时间：14:00 以后</li>
            <li>退房时间：12:00 以前</li>
            <li>取消政策：入住前24小时可免费取消</li>
            <li>早餐：含双人早餐（豪华房型及以上）</li>
          </ul>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button type="primary" @click="goBooking(currentType)">立即预订</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { User, House } from '@element-plus/icons-vue'
import { roomTypeApi } from '../api'

const router = useRouter()
const roomTypes = ref([])
const detailVisible = ref(false)
const currentType = ref(null)

const allAmenities = [
  '免费WiFi', '中央空调', '液晶电视', '迷你冰箱', '电热水壶',
  '保险箱', '吹风机', '浴袍拖鞋', '洗漱用品', '矿泉水',
  '茶包咖啡', '独立卫浴', '24小时热水', '隔音玻璃', '遮光窗帘'
]

const getRoomImage = (id, index = 1) => {
  const images = [
    'https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=600',
    'https://images.unsplash.com/photo-1590490360182-c33d57733427?w=600',
    'https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=600',
    'https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=600',
    'https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=600'
  ]
  return images[(id + index - 2) % 5]
}

const getAmenities = (id) => {
  const base = ['免费WiFi', '空调', '电视', '独立卫浴', '吹风机']
  if (id >= 3) base.push('迷你吧', '保险箱')
  if (id >= 4) base.push('浴缸')
  if (id >= 5) base.push('管家服务', '私人泳池')
  return base
}

const showDetail = (type) => {
  currentType.value = type
  detailVisible.value = true
}

const goBooking = (type) => {
  detailVisible.value = false
  router.push('/booking')
}

onMounted(async () => {
  const res = await roomTypeApi.getAll()
  roomTypes.value = res.data
})
</script>

<style scoped>
.room-list-page { max-width: 1200px; margin: 0 auto; padding: 20px; }

.page-hero {
  text-align: center;
  padding: 60px 20px;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: #fff;
  border-radius: 12px;
  margin-bottom: 40px;
}
.page-hero h1 { font-size: 36px; margin-bottom: 12px; }

.room-type-card { margin-bottom: 30px; overflow: hidden; }
.room-type-card :deep(.el-card__body) { padding: 0; }

.room-image, .detail-image {
  height: 100%;
  min-height: 300px;
  background-size: cover;
  background-position: center;
}

.room-detail { padding: 30px; height: 100%; display: flex; flex-direction: column; }
.room-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 16px; }
.room-header h2 { margin: 0; }
.room-price .price { font-size: 32px; color: #f56c6c; font-weight: bold; }
.room-price .unit { color: #999; }

.room-desc { color: #666; line-height: 1.8; margin-bottom: 20px; }

.room-specs { display: flex; gap: 30px; margin-bottom: 20px; }
.spec-item { display: flex; align-items: center; gap: 6px; color: #666; }

.room-amenities { margin-bottom: 20px; }
.room-amenities h4 { margin: 0 0 12px; font-size: 14px; color: #999; }
.amenity-tags { display: flex; flex-wrap: wrap; gap: 8px; }

.room-actions { margin-top: auto; display: flex; gap: 12px; }

.notice-list { color: #666; line-height: 2; padding-left: 20px; }
</style>
