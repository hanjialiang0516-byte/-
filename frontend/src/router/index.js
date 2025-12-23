import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../store/user'

const routes = [
  { path: '/login', name: 'Login', component: () => import('../views/Login.vue') },
  { path: '/register', name: 'Register', component: () => import('../views/Register.vue') },
  {
    path: '/',
    component: () => import('../layouts/UserLayout.vue'),
    children: [
      { path: '', name: 'Home', component: () => import('../views/Home.vue') },
      { path: 'rooms', name: 'Rooms', component: () => import('../views/RoomList.vue') },
      { path: 'booking', name: 'Booking', component: () => import('../views/Booking.vue'), meta: { auth: true } },
      { path: 'my-bookings', name: 'MyBookings', component: () => import('../views/MyBookings.vue'), meta: { auth: true } },
      { path: 'payment', name: 'Payment', component: () => import('../views/Payment.vue'), meta: { auth: true } },
      { path: 'profile', name: 'Profile', component: () => import('../views/Profile.vue'), meta: { auth: true } },
      { path: 'messages', name: 'Messages', component: () => import('../views/Messages.vue'), meta: { auth: true } }
    ]
  },
  {
    path: '/admin',
    component: () => import('../layouts/AdminLayout.vue'),
    meta: { auth: true, admin: true },
    children: [
      { path: '', name: 'AdminDashboard', component: () => import('../views/admin/Dashboard.vue') },
      { path: 'rooms', name: 'AdminRooms', component: () => import('../views/admin/Rooms.vue') },
      { path: 'bookings', name: 'AdminBookings', component: () => import('../views/admin/Bookings.vue') },
      { path: 'users', name: 'AdminUsers', component: () => import('../views/admin/Users.vue') },
      { path: 'finance', name: 'AdminFinance', component: () => import('../views/admin/Finance.vue') },
      { path: 'reports', name: 'AdminReports', component: () => import('../views/admin/Reports.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  if (to.meta.auth && !userStore.isLoggedIn) {
    next('/login')
  } else if (to.meta.admin && !userStore.isAdmin) {
    next('/')
  } else {
    next()
  }
})

export default router
