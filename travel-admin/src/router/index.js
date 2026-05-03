import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/',
    component: () => import('../views/Layout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue')
      },
      // 超级管理员专用菜单 (role: 1)
      {
        path: 'merchants',
        name: 'Merchants',
        component: () => import('../views/super/Merchants.vue'),
        meta: { role: 1 }
      },
      {
        path: 'attractions',
        name: 'Attractions',
        component: () => import('../views/super/Attractions.vue'),
        meta: { role: 1 }
      },
      {
        path: 'users',
        name: 'Users',
        component: () => import('../views/super/Users.vue'),
        meta: { role: 1 }
      },
      {
        path: 'audit',
        name: 'Audit',
        component: () => import('../views/super/Audit.vue'),
        meta: { role: 1 }
      },
      {
        path: 'total-stats',
        name: 'TotalStats',
        component: () => import('../views/super/TotalStats.vue'),
        meta: { role: 1 }
      },
      {
        path: 'system-config',
        name: 'SystemConfig',
        component: () => import('../views/super/SystemConfig.vue'),
        meta: { role: 1 }
      },
      {
        path: 'admin-orders',
        name: 'AdminOrders',
        component: () => import('../views/super/AdminOrders.vue'),
        meta: { role: 1 }
      },
      // 酒店管理员专用菜单 (role: 2)
      {
        path: 'my-hotel',
        name: 'MyHotel',
        component: () => import('../views/hotel/MyHotel.vue'),
        meta: { role: 2 }
      },
      {
        path: 'rooms',
        name: 'Rooms',
        component: () => import('../views/hotel/Rooms.vue'),
        meta: { role: 2 }
      },
      {
        path: 'orders',
        name: 'Orders',
        component: () => import('../views/hotel/Orders.vue'),
        meta: { role: 2 }
      },
      {
        path: 'my-stats',
        name: 'MyStats',
        component: () => import('../views/hotel/MyStats.vue'),
        meta: { role: 2 }
      },
      {
        path: 'hotel-feedback',
        name: 'HotelFeedback',
        component: () => import('../views/hotel/HotelFeedback.vue'),
        meta: { role: 2 }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

// 路由守卫：登录校验与角色访问控制
router.beforeEach((to, from, next) => {
  const role = localStorage.getItem('role')
  
  if (to.path !== '/login' && !role) {
    // 1. 未登录，跳转到登录页
    next('/login')
  } else if (to.meta.role && to.meta.role != role) {
    // 2. 角色不匹配，拦截越权访问
    next('/dashboard')
  } else {
    // 3. 放行
    next()
  }
})

export default router
