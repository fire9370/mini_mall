<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { cancelOrder, getOrder, payOrder } from '@/api/order'
import { useUserStore } from '@/stores/user'
import type { Order } from '@/types'

const route = useRoute()
const userStore = useUserStore()
const order = ref<Order | null>(null)

async function load() {
  order.value = await getOrder(Number(route.params.id))
}

async function onPay() {
  await payOrder(order.value!.id)
  ElMessage.success('支付成功')
  await load()
  userStore.fetchUser() // 刷新累计消费与会员等级
}

async function onCancel() {
  await cancelOrder(order.value!.id)
  ElMessage.success('订单已取消')
  await load()
}

onMounted(load)
</script>

<template>
  <div v-if="order" class="order-detail">
    <h2>订单详情</h2>
    <el-card class="block">
      <template #header>订单信息</template>
      <p>订单号：{{ order.orderNo }}</p>
      <p>状态：<el-tag>{{ order.statusLabel }}</el-tag></p>
      <p>下单时间：{{ order.createdAt }}</p>
      <p v-if="order.payTime">支付时间：{{ order.payTime }}</p>
      <p>收货人：{{ order.receiverName }}（{{ order.receiverPhone }}）</p>
      <p>收货地址：{{ order.receiverAddress }}</p>
    </el-card>

    <el-card class="block">
      <template #header>商品清单</template>
      <el-table :data="order.items || []">
        <el-table-column prop="productName" label="商品" min-width="200" />
        <el-table-column label="单价" width="120">
          <template #default="{ row }">¥{{ Number(row.price).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="100" />
        <el-table-column label="小计" width="120">
          <template #default="{ row }">¥{{ Number(row.totalPrice).toFixed(2) }}</template>
        </el-table-column>
      </el-table>
      <div class="total">订单总额：<em>¥{{ Number(order.totalAmount).toFixed(2) }}</em></div>
    </el-card>

    <div v-if="order.status === 'UNPAID'" class="actions">
      <el-button type="danger" size="large" @click="onPay">立即支付</el-button>
      <el-button size="large" @click="onCancel">取消订单</el-button>
    </div>
  </div>
</template>

<style scoped>
.order-detail {
  background: #fff;
  border-radius: 6px;
  padding: 20px;
}
.block {
  margin-bottom: 16px;
}
.block p {
  margin: 8px 0;
  color: #303133;
}
.total {
  margin-top: 16px;
  text-align: right;
}
.total em {
  color: #e4393c;
  font-size: 22px;
  font-style: normal;
  font-weight: 700;
}
.actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}
</style>
