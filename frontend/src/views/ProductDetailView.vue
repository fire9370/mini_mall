<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getProduct } from '@/api/product'
import { addCart } from '@/api/cart'
import { useCartStore } from '@/stores/cart'
import { useUserStore } from '@/stores/user'
import type { Product } from '@/types'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()
const userStore = useUserStore()

const product = ref<Product | null>(null)
const quantity = ref(1)

const images = () => {
  const p = product.value
  if (!p) return []
  const list = p.images && p.images.length ? p.images : [p.mainImage]
  return list.filter(Boolean)
}

async function load() {
  const id = Number(route.params.id)
  product.value = await getProduct(id)
}

async function onAddCart() {
  if (!userStore.token) {
    ElMessage.warning('请先登录')
    router.push({ name: 'login', query: { redirect: route.fullPath } })
    return
  }
  await addCart({ productId: product.value!.id, quantity: quantity.value })
  ElMessage.success('已加入购物车')
  cartStore.refreshCount()
}

onMounted(load)
</script>

<template>
  <div v-if="product" class="detail">
    <div class="top">
      <div class="gallery">
        <el-image v-if="images().length" :src="images()[0]" fit="cover" class="main-img" />
        <div v-else class="main-img placeholder">暂无图片</div>
        <div class="thumbs">
          <img v-for="(img, i) in images()" :key="i" :src="img" class="thumb" alt="" />
        </div>
      </div>
      <div class="meta">
        <h1 class="title">{{ product.name }}</h1>
        <p class="subtitle">{{ product.subtitle }}</p>
        <div class="price-box">
          <span class="price">¥{{ Number(product.price).toFixed(2) }}</span>
          <span class="stock">库存 {{ product.stock }} · 销量 {{ product.sales }}</span>
        </div>
        <div class="buy-row">
          <el-input-number v-model="quantity" :min="1" :max="product.stock" />
          <el-button type="danger" size="large" @click="onAddCart">加入购物车</el-button>
        </div>
      </div>
    </div>
    <div class="desc">
      <h3>商品详情</h3>
      <div v-if="product.detail" class="detail-text">{{ product.detail }}</div>
      <el-empty v-else description="暂无详情" />
    </div>
  </div>
</template>

<style scoped>
.detail {
  background: #fff;
  border-radius: 6px;
  padding: 24px;
}
.top {
  display: flex;
  gap: 32px;
}
.gallery {
  width: 420px;
}
.main-img {
  width: 100%;
  height: 320px;
  border-radius: 6px;
  background: #f5f7fa;
}
.placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #c0c4cc;
}
.thumbs {
  display: flex;
  gap: 8px;
  margin-top: 8px;
}
.thumb {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 4px;
}
.meta {
  flex: 1;
}
.title {
  font-size: 24px;
  margin: 0;
}
.subtitle {
  color: #909399;
  margin-top: 8px;
}
.price-box {
  background: #fef0f0;
  padding: 16px;
  border-radius: 6px;
  margin-top: 16px;
}
.price {
  color: #e4393c;
  font-size: 28px;
  font-weight: 700;
}
.stock {
  color: #909399;
  margin-left: 12px;
  font-size: 13px;
}
.buy-row {
  margin-top: 24px;
  display: flex;
  gap: 12px;
  align-items: center;
}
.desc {
  margin-top: 32px;
}
.detail-text {
  color: #303133;
  line-height: 1.8;
  white-space: pre-wrap;
}
</style>
