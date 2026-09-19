<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router'
import { useAdminStore } from '@/stores/admin'

const route = useRoute()
const router = useRouter()
const adminStore = useAdminStore()

const activeMenu = () => route.name as string

function onLogout() {
  adminStore.logout()
  router.push({ name: 'admin-login' })
}
</script>

<template>
  <el-container class="admin-layout">
    <el-aside width="200px" class="aside">
      <div class="brand">Mini Mall 后台</div>
      <el-menu :default-active="activeMenu()" router>
        <el-menu-item index="admin-products">商品管理</el-menu-item>
        <el-menu-item index="admin-categories">分类管理</el-menu-item>
        <el-menu-item index="admin-orders">订单管理</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <span class="title">Mini Mall 管理后台</span>
        <div class="right">
          <span class="name">{{ adminStore.name }}</span>
          <el-button link @click="onLogout">退出登录</el-button>
        </div>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.admin-layout {
  height: 100vh;
}
.aside {
  background: #304156;
}
.brand {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  font-weight: 700;
}
.aside :deep(.el-menu) {
  border-right: none;
  background: transparent;
}
.aside :deep(.el-menu-item) {
  color: #bfcbd9;
}
.aside :deep(.el-menu-item.is-active) {
  color: #fff;
  background: #409eff;
}
.header {
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}
.title {
  font-size: 16px;
  font-weight: 600;
}
.right {
  display: flex;
  align-items: center;
  gap: 12px;
}
.name {
  color: #303133;
}
.main {
  background: #f5f7fa;
}
</style>
