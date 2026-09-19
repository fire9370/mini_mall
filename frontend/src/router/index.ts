import { createRouter, createWebHistory } from 'vue-router'
import { USER_TOKEN_KEY } from '@/api/http'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: () => import('@/layouts/FrontLayout.vue'),
      children: [
        { path: '', name: 'home', component: () => import('@/views/HomeView.vue') },
        { path: 'product/:id', name: 'product-detail', component: () => import('@/views/ProductDetailView.vue') },
        { path: 'login', name: 'login', component: () => import('@/views/LoginView.vue') },
        { path: 'register', name: 'register', component: () => import('@/views/RegisterView.vue') },
        { path: 'cart', name: 'cart', component: () => import('@/views/CartView.vue'), meta: { requiresAuth: true } },
        { path: 'checkout', name: 'checkout', component: () => import('@/views/CheckoutView.vue'), meta: { requiresAuth: true } },
        { path: 'orders', name: 'orders', component: () => import('@/views/OrderListView.vue'), meta: { requiresAuth: true } },
        { path: 'order/:id', name: 'order-detail', component: () => import('@/views/OrderDetailView.vue'), meta: { requiresAuth: true } },
        { path: 'profile', name: 'profile', component: () => import('@/views/ProfileView.vue'), meta: { requiresAuth: true } }
      ]
    }
  ]
})

// 全局路由守卫：需要登录的页面未登录则跳登录
router.beforeEach((to) => {
  if (to.meta.requiresAuth && !localStorage.getItem(USER_TOKEN_KEY)) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }
})

export default router
