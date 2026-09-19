<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { listCart } from '@/api/cart'
import { createOrder } from '@/api/order'
import { useCartStore } from '@/stores/cart'
import type { CartItem } from '@/types'

const router = useRouter()
const cartStore = useCartStore()

const items = ref<CartItem[]>([])
const form = reactive({ receiverName: '', receiverPhone: '', receiverAddress: '' })
const loading = ref(false)

const checkedItems = computed(() => items.value.filter((i) => i.checked === 1))
const totalPrice = computed(() => checkedItems.value.reduce((sum, i) => sum + Number(i.price) * i.quantity, 0))

async function load() {
  items.value = await listCart()
  if (checkedItems.value.length === 0) {
    ElMessage.warning('没有勾选的商品，请先回购物车勾选')
    router.replace({ name: 'cart' })
  }
}

async function onSubmit() {
  if (!form.receiverName || !form.receiverPhone || !form.receiverAddress) {
    ElMessage.warning('请填写完整的收货信息')
    return
  }
  loading.value = true
  try {
    const orderId = await createOrder({
      receiverName: form.receiverName,
      receiverPhone: form.receiverPhone,
      receiverAddress: form.receiverAddress,
      cartItemIds: checkedItems.value.map((i) => i.id)
    })
    ElMessage.success('下单成功')
    cartStore.refreshCount()
    router.replace({ name: 'order-detail', params: { id: orderId } })
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="checkout">
    <h2>确认订单</h2>
    <el-card class="block">
      <template #header>收货信息</template>
      <el-form label-width="80px">
        <el-form-item label="收货人">
          <el-input v-model="form.receiverName" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="form.receiverPhone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="form.receiverAddress" placeholder="请输入收货地址" />
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="block">
      <template #header>商品清单</template>
      <el-table :data="checkedItems">
        <el-table-column label="商品" min-width="200">
          <template #default="{ row }">{{ row.productName }}</template>
        </el-table-column>
        <el-table-column label="单价" width="120">
          <template #default="{ row }">¥{{ Number(row.price).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column label="数量" width="100">
          <template #default="{ row }">{{ row.quantity }}</template>
        </el-table-column>
        <el-table-column label="小计" width="120">
          <template #default="{ row }">¥{{ (Number(row.price) * row.quantity).toFixed(2) }}</template>
        </el-table-column>
      </el-table>
      <div class="total">应付总额：<em>¥{{ totalPrice.toFixed(2) }}</em></div>
    </el-card>

    <div class="submit-bar">
      <el-button type="danger" size="large" :loading="loading" @click="onSubmit">提交订单</el-button>
    </div>
  </div>
</template>

<style scoped>
.checkout {
  background: #fff;
  border-radius: 6px;
  padding: 20px;
}
.block {
  margin-bottom: 16px;
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
.submit-bar {
  display: flex;
  justify-content: flex-end;
}
</style>
