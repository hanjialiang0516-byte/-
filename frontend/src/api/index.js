import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../store/user'

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000
})

api.interceptors.request.use(config => {
  const userStore = useUserStore()
  if (userStore.token) {
    config.headers.Authorization = `Bearer ${userStore.token}`
  }
  return config
})

api.interceptors.response.use(
  response => {
    if (response.data.code !== 200) {
      ElMessage.error(response.data.message)
      return Promise.reject(response.data)
    }
    return response.data
  },
  error => {
    if (error.response?.status === 401) {
      const userStore = useUserStore()
      userStore.logout()
      window.location.href = '/login'
    }
    ElMessage.error(error.message || '请求失败')
    return Promise.reject(error)
  }
)

export const authApi = {
  login: (data) => api.post('/auth/login', data),
  register: (data) => api.post('/auth/register', data)
}

export const roomApi = {
  getAvailable: (checkIn, checkOut) => api.get('/rooms/available', { params: { checkIn, checkOut } }),
  getAll: () => api.get('/rooms')
}

export const roomTypeApi = {
  getAll: () => api.get('/room-types')
}

export const bookingApi = {
  create: (data) => api.post('/bookings', data),
  getMyBookings: (page = 1, size = 10) => api.get('/bookings', { params: { page, size } }),
  cancel: (id) => api.post(`/bookings/${id}/cancel`)
}

export const messageApi = {
  getAll: () => api.get('/messages'),
  getUnreadCount: () => api.get('/messages/unread-count'),
  markAsRead: (id) => api.post(`/messages/${id}/read`)
}

export const userApi = {
  getProfile: () => api.get('/user/profile'),
  updateProfile: (data) => api.put('/user/profile', data)
}

export const adminApi = {
  getBookings: (page, size, status) => api.get('/admin/bookings', { params: { page, size, status } }),
  confirmBooking: (id) => api.post(`/admin/bookings/${id}/confirm`),
  checkIn: (id) => api.post(`/admin/bookings/${id}/check-in`),
  checkOut: (id) => api.post(`/admin/bookings/${id}/check-out`),
  cancelBooking: (id) => api.post(`/admin/bookings/${id}/cancel`),
  createPhoneBooking: (data) => api.post('/admin/bookings/phone', data),
  getRooms: () => api.get('/admin/rooms'),
  createRoom: (data) => api.post('/admin/rooms', data),
  updateRoom: (id, data) => api.put(`/admin/rooms/${id}`, data),
  updateRoomStatus: (id, status) => api.put(`/admin/rooms/${id}/status`, { status }),
  deleteRoom: (id) => api.delete(`/admin/rooms/${id}`),
  getRoomTypes: () => api.get('/admin/rooms/types'),
  createRoomType: (data) => api.post('/admin/rooms/types', data),
  updateRoomType: (id, data) => api.put(`/admin/rooms/types/${id}`, data),
  deleteRoomType: (id) => api.delete(`/admin/rooms/types/${id}`),
  getUsers: (page, size) => api.get('/admin/users', { params: { page, size } }),
  updateUserStatus: (id, status) => api.put(`/admin/users/${id}/status`, { status })
}

export default api
