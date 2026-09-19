<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { listOrders } from '@/api/order'
import type { Order } from '@/types'

const router = useRouter()
const orders = ref<Order[]>([])

async function load() {
  orders.value = await listOrders()
}

const statusType = (status: string): 'primary' | 'success' | 'warning' | 'danger' | 'info' => {
  switch (status) {
    case 'UNPAID':
      return 'warning'
    case 'PAID':
    case 'SHIPPED':
      return 'primary'
    case 'COMPLETED':
      return 'success'
    case 'CANCELLED':
      return 'info'
    default:
      return 'info'
  }
}

onMounted(load)
</script>

<template>
  <div class="orders">
    <h2>我的订单</h2>
    <el-empty v-if="orders.length === 0" description="还没有订单" />
    <el-table v-else :data="orders">
      <el-table-column prop="orderNo" label="订单号" min-width="200" />
      <el-table-column label="金额" width="140">
        <template #default="{ row }">¥{{ Number(row.totalAmount).toFixed(2) }}</template>
      </el-table-column>
      <el-table-column label="状态" width="120">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">{{ row.statusLabel }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="下单时间" min-width="170" />
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button link type="primary" @click="router.push({ name: 'order-detail', params: { id: row.id } })">查看</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<style scoped>
.orders {
  background: #fff;
  border-radius: 6px;
  padding: 20px;
}
</style>
