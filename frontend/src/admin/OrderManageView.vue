<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getOrder, pageOrders, shipOrder } from '@/api/admin'
import type { Order } from '@/types'

const orders = ref<Order[]>([])
const total = ref(0)
const page = ref(1)
const size = 10
const status = ref('')

const detailVisible = ref(false)
const detail = ref<Order | null>(null)

async function load() {
  const result = await pageOrders({ page: page.value, size, status: status.value || undefined })
  orders.value = result.records
  total.value = result.total
}

async function onView(row: Order) {
  detail.value = await getOrder(row.id)
  detailVisible.value = true
}

async function onShip(row: Order) {
  await shipOrder(row.id)
  ElMessage.success('已发货')
  load()
}

const statusType = (s: string): 'primary' | 'success' | 'warning' | 'danger' | 'info' => {
  switch (s) {
    case 'UNPAID':
      return 'warning'
    case 'PAID':
    case 'SHIPPED':
      return 'primary'
    case 'COMPLETED':
      return 'success'
    default:
      return 'info'
  }
}

onMounted(load)
</script>

<template>
  <div class="page">
    <div class="toolbar">
      <el-select v-model="status" placeholder="全部状态" clearable style="width: 160px" @change="load">
        <el-option value="UNPAID" label="待支付" />
        <el-option value="PAID" label="已支付" />
        <el-option value="SHIPPED" label="已发货" />
        <el-option value="COMPLETED" label="已完成" />
        <el-option value="CANCELLED" label="已取消" />
      </el-select>
    </div>
    <el-table :data="orders" border>
      <el-table-column prop="orderNo" label="订单号" min-width="190" />
      <el-table-column prop="receiverName" label="收货人" width="120" />
      <el-table-column label="金额" width="130">
        <template #default="{ row }">¥{{ Number(row.totalAmount).toFixed(2) }}</template>
      </el-table-column>
      <el-table-column label="状态" width="110">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">{{ row.statusLabel }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="下单时间" min-width="170" />
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button link type="primary" @click="onView(row)">详情</el-button>
          <el-button v-if="row.status === 'PAID'" link type="success" @click="onShip(row)">发货</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination">
      <el-pagination background layout="prev, pager, next, total" :total="total" :page-size="size" :current-page="page" @current-change="(p: number) => { page = p; load() }" />
    </div>

    <el-dialog v-model="detailVisible" title="订单详情" width="640px">
      <template v-if="detail">
        <p>订单号：{{ detail.orderNo }}</p>
        <p>状态：{{ detail.statusLabel }}</p>
        <p>收货人：{{ detail.receiverName }}（{{ detail.receiverPhone }}）</p>
        <p>地址：{{ detail.receiverAddress }}</p>
        <el-table :data="detail.items || []" size="small" border>
          <el-table-column prop="productName" label="商品" min-width="160" />
          <el-table-column label="单价" width="100">
            <template #default="{ row }">¥{{ Number(row.price).toFixed(2) }}</template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="70" />
          <el-table-column label="小计" width="100">
            <template #default="{ row }">¥{{ Number(row.totalPrice).toFixed(2) }}</template>
          </el-table-column>
        </el-table>
        <p class="total">总额：¥{{ Number(detail.totalAmount).toFixed(2) }}</p>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page {
  background: #fff;
  border-radius: 6px;
  padding: 20px;
}
.toolbar {
  margin-bottom: 16px;
}
.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
.total {
  text-align: right;
  font-weight: 700;
}
</style>
