<script setup lang="ts">
import { onMounted } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

onMounted(() => {
  userStore.fetchUser()
})
</script>

<template>
  <div class="profile">
    <h2>个人中心</h2>
    <el-card v-if="userStore.user" class="card">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="用户名">{{ userStore.user.username }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ userStore.user.nickname }}</el-descriptions-item>
        <el-descriptions-item label="会员等级">
          <el-tag type="warning">{{ userStore.user.memberLevelLabel }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="累计消费">¥{{ Number(userStore.user.totalSpent).toFixed(2) }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<style scoped>
.profile {
  background: #fff;
  border-radius: 6px;
  padding: 20px;
}
.card {
  max-width: 480px;
}
</style>
