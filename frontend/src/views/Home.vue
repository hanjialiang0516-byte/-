<template>
  <div class="home">
    <!-- 全屏轮播大图 -->
    <div class="hero-section">
      <el-carousel height="600px" :interval="5000" arrow="always">
        <el-carousel-item v-for="slide in heroSlides" :key="slide.id">
          <div class="hero-slide" :style="{ backgroundImage: `url(${slide.image})` }">
            <div class="hero-overlay">
              <div class="hero-content">
                <h1>{{ slide.title }}</h1>
                <p>{{ slide.subtitle }}</p>
                <el-button type="primary" size="large" round @click="$router.push('/booking')">
                  立即预订
                </el-button>
              </div>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
      
      <!-- 快速搜索框 -->
      <div class="quick-search">
        <el-card shadow="always">
          <div class="search-form">
            <div class="search-item">
              <label>入住日期</label>
              <el-date-picker v-model="checkIn" type="date" placeholder="选择日期" :disabled-date="disablePastDate" />
            </div>
            <div class="search-item">
              <label>离店日期</label>
              <el-date-picker v-model="checkOut" type="date" placeholder="选择日期" :disabled-date="disableCheckOutDate" />
            </div>
            <div class="search-item">
              <label>房间数量</label>
              <el-select v-model="roomCount" placeholder="选择">
                <el-option label="1间" :value="1" />
                <el-option label="2间" :value="2" />
                <el-option label="3间" :value="3" />
              </el-select>
            </div>
            <div class="search-item">
              <label>入住人数</label>
              <el-select v-model="guestCount" placeholder="选择">
                <el-option label="1人" :value="1" />
                <el-option label="2人" :value="2" />
                <el-option label="3人" :value="3" />
                <el-option label="4人" :value="4" />
              </el-select>
            </div>
            <el-button type="primary" size="large" @click="handleSearch">
              <el-icon><Search /></el-icon> 搜索房间
            </el-button>
          </div>
        </el-card>
      </div>
    </div>

    <!-- 特色服务 -->
    <div class="features-section">
      <div class="section-header">
        <h2>为什么选择我们</h2>
        <p>星辰酒店，您的品质之选</p>
      </div>
      <el-row :gutter="30">
        <el-col :span="6" v-for="feature in features" :key="feature.title">
          <div class="feature-card">
            <div class="feature-icon" :style="{ background: feature.color }">
              <span>{{ feature.icon }}</span>
            </div>
            <h3>{{ feature.title }}</h3>
            <p>{{ feature.desc }}</p>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 精选房型 -->
    <div class="rooms-section">
      <div class="section-header">
        <h2>精选房型</h2>
        <p>舒适空间，品质享受</p>
      </div>
      <el-row :gutter="20">
        <el-col :span="8" v-for="type in roomTypes" :key="type.id">
          <el-card shadow="hover" class="room-card" @click="showRoomDetail(type)">
            <div class="room-image-wrapper">
              <img :src="getRoomImage(type.id)" :alt="type.name" class="room-image" />
              <div class="room-badge" v-if="type.id >= 4">热门</div>
              <div class="room-price-tag">¥{{ type.price }}<small>/晚</small></div>
            </div>
            <div class="room-info">
              <h3>{{ type.name }}</h3>
              <div class="room-meta">
                <span>👥 {{ type.capacity }}人</span>
                <span>📐 {{ type.area }}㎡</span>
                <span>🛏️ {{ type.bedType }}</span>
              </div>
              <p class="room-desc">{{ type.description }}</p>
              <div class="room-amenities">
                <el-tag size="small" v-for="a in ['WiFi', '空调', '早餐']" :key="a">{{ a }}</el-tag>
              </div>
              <el-button type="primary" round style="width: 100%" @click.stop="$router.push('/booking')">
                立即预订
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
      <div class="view-all">
        <el-button size="large" round @click="$router.push('/rooms')">查看全部房型 →</el-button>
      </div>
    </div>

    <!-- 酒店图集 -->
    <div class="gallery-section">
      <div class="section-header light">
        <h2>酒店风采</h2>
        <p>探索星辰酒店的每一个角落</p>
      </div>
      <div class="gallery-grid">
        <div class="gallery-item large" :style="{ backgroundImage: `url(${gallery[0]})` }" @click="previewImage(0)">
          <div class="gallery-overlay"><span>酒店大堂</span></div>
        </div>
        <div class="gallery-item" :style="{ backgroundImage: `url(${gallery[1]})` }" @click="previewImage(1)">
          <div class="gallery-overlay"><span>豪华客房</span></div>
        </div>
        <div class="gallery-item" :style="{ backgroundImage: `url(${gallery[2]})` }" @click="previewImage(2)">
          <div class="gallery-overlay"><span>无边泳池</span></div>
        </div>
        <div class="gallery-item" :style="{ backgroundImage: `url(${gallery[3]})` }" @click="previewImage(3)">
          <div class="gallery-overlay"><span>精致餐厅</span></div>
        </div>
        <div class="gallery-item" :style="{ backgroundImage: `url(${gallery[4]})` }" @click="previewImage(4)">
          <div class="gallery-overlay"><span>健身中心</span></div>
        </div>
        <div class="gallery-item" :style="{ backgroundImage: `url(${gallery[5]})` }" @click="previewImage(5)">
          <div class="gallery-overlay"><span>SPA水疗</span></div>
        </div>
      </div>
    </div>

    <!-- 特别优惠 -->
    <div class="offers-section">
      <div class="section-header">
        <h2>特别优惠</h2>
        <p>限时特惠，不容错过</p>
      </div>
      <el-row :gutter="20">
        <el-col :span="8" v-for="offer in offers" :key="offer.title">
          <el-card shadow="hover" class="offer-card">
            <div class="offer-image" :style="{ backgroundImage: `url(${offer.image})` }">
              <div class="offer-discount">{{ offer.discount }}</div>
            </div>
            <div class="offer-content">
              <h3>{{ offer.title }}</h3>
              <p>{{ offer.desc }}</p>
              <div class="offer-footer">
                <span class="offer-price">¥{{ offer.price }}起</span>
                <el-button type="primary" size="small" round>立即抢购</el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 住客评价 -->
    <div class="reviews-section">
      <div class="section-header">
        <h2>住客评价</h2>
        <p>真实评价，值得信赖</p>
      </div>
      <div class="reviews-stats">
        <div class="stats-score">
          <div class="score">4.9</div>
          <el-rate v-model="avgRating" disabled />
          <p>基于 2,386 条评价</p>
        </div>
        <div class="stats-bars">
          <div class="bar-item" v-for="(percent, index) in [95, 88, 92, 90, 96]" :key="index">
            <span>{{ ['位置', '服务', '卫生', '设施', '性价比'][index] }}</span>
            <el-progress :percentage="percent" :stroke-width="8" />
          </div>
        </div>
      </div>
      <el-row :gutter="20">
        <el-col :span="8" v-for="review in reviews" :key="review.id">
          <el-card shadow="hover" class="review-card">
            <div class="review-header">
              <el-avatar :size="50" :src="review.avatar">{{ review.name[0] }}</el-avatar>
              <div class="review-user">
                <div class="name">{{ review.name }}</div>
                <div class="meta">{{ review.roomType }} · {{ review.date }}</div>
              </div>
              <el-rate v-model="review.rating" disabled size="small" />
            </div>
            <p class="review-content">"{{ review.content }}"</p>
            <div class="review-images" v-if="review.images">
              <img v-for="(img, i) in review.images" :key="i" :src="img" />
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 品牌合作 -->
    <div class="partners-section">
      <div class="section-header">
        <h2>合作伙伴</h2>
      </div>
      <div class="partners-logos">
        <div class="partner-logo" v-for="i in 6" :key="i">
          <img :src="`https://via.placeholder.com/120x60?text=Partner${i}`" />
        </div>
      </div>
    </div>

    <!-- 图片预览 -->
    <el-image-viewer v-if="showViewer" :url-list="gallery" :initial-index="viewerIndex" @close="showViewer = false" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import { roomTypeApi } from '../api'

const router = useRouter()
const roomTypes = ref([])
const checkIn = ref('')
const checkOut = ref('')
const roomCount = ref(1)
const guestCount = ref(2)
const avgRating = ref(4.9)
const showViewer = ref(false)
const viewerIndex = ref(0)

const heroSlides = [
  { id: 1, title: '尊享奢华体验', subtitle: '在星辰酒店，每一刻都是难忘的回忆', image: 'https://images.unsplash.com/photo-1566073771259-6a8506099945?w=1920' },
  { id: 2, title: '城市中的绿洲', subtitle: '繁华都市中的宁静港湾，让身心得到完全放松', image: 'https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?w=1920' },
  { id: 3, title: '品质服务承诺', subtitle: '25年专注品质，只为给您最好的入住体验', image: 'https://images.unsplash.com/photo-1551882547-ff40c63fe5fa?w=1920' }
]

const features = [
  { icon: '🏆', title: '五星品质', desc: '国际五星级酒店标准，品质保证', color: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' },
  { icon: '📍', title: '黄金地段', desc: '位于市中心CBD，交通便利', color: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' },
  { icon: '🛎️', title: '贴心服务', desc: '24小时管家服务，有求必应', color: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' },
  { icon: '💰', title: '超值价格', desc: '官网预订最低价，会员更优惠', color: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)' }
]

const gallery = [
  'https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800',
  'https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=600',
  'https://images.unsplash.com/photo-1576013551627-0cc20b96c2a7?w=600',
  'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=600',
  'https://images.unsplash.com/photo-1534438327276-14e5300c3a48?w=600',
  'https://images.unsplash.com/photo-1544161515-4ab6ce6db874?w=600'
]

const offers = [
  { title: '早鸟特惠', desc: '提前30天预订，享受超值折扣', discount: '7折', price: 299, image: 'https://images.unsplash.com/photo-1590490360182-c33d57733427?w=400' },
  { title: '连住优惠', desc: '连续入住3晚及以上，第3晚半价', discount: '5折', price: 399, image: 'https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=400' },
  { title: '周末特惠', desc: '周五至周日入住，含双人早餐', discount: '8折', price: 458, image: 'https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=400' }
]

const reviews = [
  { id: 1, name: '王先生', avatar: '', roomType: '豪华大床房', date: '2025-12-20', rating: 5, content: '房间非常干净整洁，服务态度超级好！前台小姐姐很热情，还免费升级了房型。下次来北京一定还住这里！', images: ['https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=200'] },
  { id: 2, name: '李女士', avatar: '', roomType: '家庭亲子房', date: '2025-12-18', rating: 5, content: '带孩子出行首选！房间很大，儿童设施齐全，早餐也很丰盛。孩子玩得很开心，强烈推荐给有娃家庭！' },
  { id: 3, name: '张先生', avatar: '', roomType: '总统套房', date: '2025-12-15', rating: 5, content: '商务出差的最佳选择，位置绝佳，设施一流。管家服务非常专业，每个细节都照顾到了。物超所值！' }
]

const getRoomImage = (id) => {
  const images = [
    'https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=500',
    'https://images.unsplash.com/photo-1590490360182-c33d57733427?w=500',
    'https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=500',
    'https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=500',
    'https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=500'
  ]
  return images[(id - 1) % 5]
}

const disablePastDate = (date) => date < new Date(new Date().setHours(0, 0, 0, 0))
const disableCheckOutDate = (date) => !checkIn.value || date <= new Date(checkIn.value)

const handleSearch = () => {
  router.push('/booking')
}

const showRoomDetail = (type) => {
  router.push('/rooms')
}

const previewImage = (index) => {
  viewerIndex.value = index
  showViewer.value = true
}

onMounted(async () => {
  try {
    const res = await roomTypeApi.getAll()
    roomTypes.value = res.data.slice(0, 3)
  } catch (e) {
    console.error(e)
  }
})
</script>

<style scoped>
.home { background: #fff; }

/* Hero Section */
.hero-section { position: relative; }
.hero-slide {
  height: 100%;
  background-size: cover;
  background-position: center;
}
.hero-overlay {
  height: 100%;
  background: linear-gradient(to right, rgba(0,0,0,0.7) 0%, rgba(0,0,0,0.3) 100%);
  display: flex;
  align-items: center;
  padding-left: 10%;
}
.hero-content { color: #fff; max-width: 600px; }
.hero-content h1 { font-size: 56px; margin-bottom: 20px; text-shadow: 2px 2px 4px rgba(0,0,0,0.3); }
.hero-content p { font-size: 22px; margin-bottom: 30px; opacity: 0.9; }

.quick-search {
  position: absolute;
  bottom: -40px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 5;
  width: 90%;
  max-width: 1000px;
}
.search-form {
  display: flex;
  align-items: flex-end;
  gap: 20px;
  padding: 10px;
}
.search-item { flex: 1; }
.search-item label { display: block; font-size: 13px; color: #666; margin-bottom: 8px; }
.search-form .el-button { height: 40px; padding: 0 30px; }

/* Section Headers */
.section-header { text-align: center; margin-bottom: 40px; padding-top: 80px; }
.section-header.light { color: #fff; }
.section-header h2 { font-size: 36px; margin-bottom: 12px; }
.section-header p { color: #666; font-size: 16px; }
.section-header.light p { color: rgba(255,255,255,0.8); }

/* Features */
.features-section { padding: 0 5% 60px; background: #f8f9fa; }
.feature-card {
  text-align: center;
  padding: 40px 20px;
  background: #fff;
  border-radius: 16px;
  transition: all 0.3s;
  height: 100%;
}
.feature-card:hover { transform: translateY(-10px); box-shadow: 0 20px 40px rgba(0,0,0,0.1); }
.feature-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  font-size: 36px;
}
.feature-card h3 { margin-bottom: 12px; }
.feature-card p { color: #666; font-size: 14px; }

/* Rooms */
.rooms-section { padding: 0 5% 60px; }
.room-card { border-radius: 16px; overflow: hidden; cursor: pointer; transition: all 0.3s; }
.room-card:hover { transform: translateY(-10px); box-shadow: 0 20px 40px rgba(0,0,0,0.15); }
.room-card :deep(.el-card__body) { padding: 0; }
.room-image-wrapper { position: relative; overflow: hidden; }
.room-image { width: 100%; height: 220px; object-fit: cover; transition: transform 0.5s; }
.room-card:hover .room-image { transform: scale(1.1); }
.room-badge {
  position: absolute;
  top: 15px;
  left: 15px;
  background: #f56c6c;
  color: #fff;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
}
.room-price-tag {
  position: absolute;
  bottom: 15px;
  right: 15px;
  background: rgba(0,0,0,0.7);
  color: #fff;
  padding: 8px 16px;
  border-radius: 8px;
  font-size: 20px;
  font-weight: bold;
}
.room-price-tag small { font-size: 12px; font-weight: normal; }
.room-info { padding: 20px; }
.room-info h3 { margin-bottom: 10px; }
.room-meta { display: flex; gap: 15px; color: #666; font-size: 13px; margin-bottom: 10px; }
.room-desc { color: #999; font-size: 14px; margin-bottom: 15px; height: 40px; overflow: hidden; }
.room-amenities { display: flex; gap: 8px; margin-bottom: 15px; }
.view-all { text-align: center; margin-top: 40px; }

/* Gallery */
.gallery-section {
  padding: 0 5% 60px;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
}
.gallery-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  grid-template-rows: repeat(2, 200px);
  gap: 15px;
}
.gallery-item {
  background-size: cover;
  background-position: center;
  border-radius: 12px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.gallery-item.large { grid-column: span 2; grid-row: span 2; }
.gallery-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0,0,0,0.3);
  display: flex;
  align-items: flex-end;
  padding: 20px;
  opacity: 0;
  transition: opacity 0.3s;
}
.gallery-item:hover .gallery-overlay { opacity: 1; }
.gallery-overlay span { color: #fff; font-size: 18px; font-weight: bold; }

/* Offers */
.offers-section { padding: 0 5% 60px; background: #f8f9fa; }
.offer-card { border-radius: 16px; overflow: hidden; }
.offer-card :deep(.el-card__body) { padding: 0; }
.offer-image {
  height: 180px;
  background-size: cover;
  background-position: center;
  position: relative;
}
.offer-discount {
  position: absolute;
  top: 15px;
  right: 15px;
  background: #f56c6c;
  color: #fff;
  padding: 8px 16px;
  border-radius: 20px;
  font-weight: bold;
}
.offer-content { padding: 20px; }
.offer-content h3 { margin-bottom: 8px; }
.offer-content p { color: #666; font-size: 14px; margin-bottom: 15px; }
.offer-footer { display: flex; justify-content: space-between; align-items: center; }
.offer-price { font-size: 24px; color: #f56c6c; font-weight: bold; }

/* Reviews */
.reviews-section { padding: 0 5% 60px; }
.reviews-stats {
  display: flex;
  gap: 60px;
  background: #f8f9fa;
  padding: 30px 40px;
  border-radius: 16px;
  margin-bottom: 30px;
}
.stats-score { text-align: center; }
.stats-score .score { font-size: 64px; font-weight: bold; color: #409EFF; }
.stats-score p { color: #999; margin-top: 8px; }
.stats-bars { flex: 1; display: flex; flex-direction: column; justify-content: center; gap: 12px; }
.bar-item { display: flex; align-items: center; gap: 15px; }
.bar-item span { width: 60px; color: #666; }
.bar-item .el-progress { flex: 1; }

.review-card { height: 100%; }
.review-header { display: flex; align-items: center; gap: 12px; margin-bottom: 15px; }
.review-user { flex: 1; }
.review-user .name { font-weight: bold; }
.review-user .meta { font-size: 12px; color: #999; }
.review-content { color: #666; line-height: 1.8; font-style: italic; margin-bottom: 15px; }
.review-images { display: flex; gap: 8px; }
.review-images img { width: 80px; height: 60px; object-fit: cover; border-radius: 8px; }

/* Partners */
.partners-section { padding: 40px 5% 60px; background: #f8f9fa; }
.partners-logos { display: flex; justify-content: center; gap: 60px; flex-wrap: wrap; }
.partner-logo { opacity: 0.5; transition: opacity 0.3s; }
.partner-logo:hover { opacity: 1; }
</style>
