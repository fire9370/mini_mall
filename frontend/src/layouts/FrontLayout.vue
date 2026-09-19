<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'

const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()
const keyword = ref('')

function onSearch() {
  router.push({ name: 'home', query: keyword.value ? { keyword: keyword.value } : {} })
}

function onLogout() {
  userStore.logout()
  cartStore.refreshCount()
  router.push({ name: 'home' })
}

onMounted(() => {
  if (userStore.token) {
    userStore.fetchUser()
  }
  cartStore.refreshCount()
})
</script>

<template>
  <div class="front-layout">
    <header class="header">
      <div class="header-inner">
        <router-link :to="{ name: 'home' }" class="logo">Mini Mall</router-link>
        <div class="search">
          <el-input v-model="keyword" placeholder="搜索商品" clearable @keyup.enter="onSearch">
            <template #append>
              <el-button :icon="Search" @click="onSearch" />
            </template>
          </el-input>
        </div>
        <div class="actions">
          <template v-if="userStore.user">
            <el-badge :value="cartStore.count" :hidden="cartStore.count === 0">
              <el-button text @click="router.push({ name: 'cart' })">购物车</el-button>
            </el-badge>
            <el-dropdown>
              <span class="user-chip">
                {{ userStore.user.nickname }}
                <el-tag size="small" type="warning">{{ userStore.user.memberLevelLabel }}</el-tag>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="router.push({ name: 'profile' })">个人中心</el-dropdown-item>
                  <el-dropdown-item divided @click="onLogout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <el-button text @click="router.push({ name: 'login' })">登录</el-button>
            <el-button text @click="router.push({ name: 'register' })">注册</el-button>
          </template>
        </div>
      </div>
    </header>
    <main class="main">
      <router-view />
    </main>
  </div>
</template>

<style scoped>
.front-layout {
  min-height: 100vh;
  background: #f5f7fa;
}
.header {
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 0;
  z-index: 10;
}
.header-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 16px;
  height: 60px;
  display: flex;
  align-items: center;
  gap: 24px;
}
.logo {
  font-size: 22px;
  font-weight: 700;
  color: #e4393c;
  text-decoration: none;
  white-space: nowrap;
}
.search {
  flex: 1;
  max-width: 420px;
}
.actions {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 8px;
}
.user-chip {
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: #303133;
}
.main {
  max-width: 1200px;
  margin: 0 auto;
  padding: 16px;
}
</style>
