<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { deleteCart, listCart, toggleCartChecked, updateCart } from '@/api/cart'
import { useCartStore } from '@/stores/cart'
import type { CartItem } from '@/types'

const router = useRouter()
const cartStore = useCartStore()
const items = ref<CartItem[]>([])

const checkedItems = computed(() => items.value.filter((i) => i.checked === 1))
const totalPrice = computed(() => checkedItems.value.reduce((sum, i) => sum + Number(i.price) * i.quantity, 0))

async function load() {
  items.value = await listCart()
}

async function onToggle(item: CartItem, checked: boolean) {
  await toggleCartChecked(item.id, checked ? 1 : 0)
  item.checked = checked ? 1 : 0
}

async function onQuantityChange(item: CartItem, qty: number) {
  await updateCart(item.id, { quantity: qty })
  cartStore.refreshCount()
}

async function onDelete(item: CartItem) {
  await deleteCart(item.id)
  ElMessage.success('已删除')
  await load()
  cartStore.refreshCount()
}

function goCheckout() {
  if (checkedItems.value.length === 0) {
    ElMessage.warning('请先勾选要结算的商品')
    return
  }
  router.push({ name: 'checkout' })
}

onMounted(load)
</script>

<template>
  <div class="cart">
    <h2>购物车</h2>
    <el-empty v-if="items.length === 0" description="购物车是空的，去逛逛吧" />
    <template v-else>
      <el-table :data="items">
        <el-table-column width="60">
          <template #default="{ row }">
            <el-checkbox :model-value="row.checked === 1" @change="(v: unknown) => onToggle(row, Boolean(v))" />
          </template>
        </el-table-column>
        <el-table-column label="商品" min-width="240">
          <template #default="{ row }">
            <div class="product">
              <el-image v-if="row.productImage" :src="row.productImage" fit="cover" class="thumb" />
              <div v-else class="thumb placeholder">图</div>
              <span>{{ row.productName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="单价" width="120">
          <template #default="{ row }">¥{{ Number(row.price).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column label="数量" width="160">
          <template #default="{ row }">
            <el-input-number :model-value="row.quantity" :min="1" :max="row.stock" size="small" @change="(v: unknown) => onQuantityChange(row, Number(v))" />
          </template>
        </el-table-column>
        <el-table-column label="小计" width="120">
          <template #default="{ row }">¥{{ (Number(row.price) * row.quantity).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="80">
          <template #default="{ row }">
            <el-button link type="danger" @click="onDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="footer">
        <span class="total">合计：<em>¥{{ totalPrice.toFixed(2) }}</em></span>
        <el-button type="danger" size="large" @click="goCheckout">去结算</el-button>
      </div>
    </template>
  </div>
</template>

<style scoped>
.cart {
  background: #fff;
  border-radius: 6px;
  padding: 20px;
}
.product {
  display: flex;
  align-items: center;
  gap: 10px;
}
.thumb {
  width: 48px;
  height: 48px;
  border-radius: 4px;
}
.placeholder {
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #c0c4cc;
}
.footer {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 16px;
}
.total em {
  color: #e4393c;
  font-size: 22px;
  font-style: normal;
  font-weight: 700;
}
</style>
