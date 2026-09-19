<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { listCategories, pageProducts } from '@/api/product'
import type { Category, Product } from '@/types'

const route = useRoute()
const router = useRouter()

const categories = ref<Category[]>([])
const products = ref<Product[]>([])
const total = ref(0)
const page = ref(1)
const size = 8
const keyword = ref('')
const activeCategory = ref<number>()

const topCategories = computed(() => categories.value.filter((c) => c.parentId === 0))
const childrenOf = (id: number) => categories.value.filter((c) => c.parentId === id)

async function loadCategories() {
  categories.value = await listCategories()
}

async function loadProducts() {
  const result = await pageProducts({
    page: page.value,
    size,
    keyword: keyword.value || undefined,
    categoryId: activeCategory.value
  })
  products.value = result.records
  total.value = result.total
}

function selectCategory(id?: number) {
  activeCategory.value = id
  page.value = 1
  loadProducts()
}

function onPageChange(p: number) {
  page.value = p
  loadProducts()
}

watch(
  () => route.query.keyword,
  (val) => {
    keyword.value = (val as string) || ''
    activeCategory.value = undefined
    page.value = 1
    loadProducts()
  }
)

onMounted(() => {
  keyword.value = (route.query.keyword as string) || ''
  loadCategories()
  loadProducts()
})
</script>

<template>
  <div class="home">
    <aside class="sidebar">
      <div class="cat-group" :class="{ active: activeCategory === undefined }" @click="selectCategory(undefined)">
        全部商品
      </div>
      <div v-for="top in topCategories" :key="top.id" class="cat-group">
        <div class="cat-top" :class="{ active: activeCategory === top.id }" @click="selectCategory(top.id)">
          {{ top.name }}
        </div>
        <div
          v-for="child in childrenOf(top.id)"
          :key="child.id"
          class="cat-child"
          :class="{ active: activeCategory === child.id }"
          @click="selectCategory(child.id)"
        >
          {{ child.name }}
        </div>
      </div>
    </aside>

    <section class="content">
      <div v-if="products.length === 0" class="empty">
        <el-empty description="暂无商品" />
      </div>
      <div v-else class="grid">
        <div v-for="p in products" :key="p.id" class="card" @click="router.push({ name: 'product-detail', params: { id: p.id } })">
          <div class="img">
            <el-image v-if="p.mainImage" :src="p.mainImage" fit="cover" class="img-el" />
            <div v-else class="img-placeholder">暂无图片</div>
          </div>
          <div class="info">
            <div class="name">{{ p.name }}</div>
            <div class="subtitle">{{ p.subtitle }}</div>
            <div class="price-row">
              <span class="price">¥{{ Number(p.price).toFixed(2) }}</span>
              <span class="sales">销量 {{ p.sales }}</span>
            </div>
          </div>
        </div>
      </div>
      <div class="pagination">
        <el-pagination
          background
          layout="prev, pager, next, total"
          :total="total"
          :page-size="size"
          :current-page="page"
          @current-change="onPageChange"
        />
      </div>
    </section>
  </div>
</template>

<style scoped>
.home {
  display: flex;
  gap: 16px;
}
.sidebar {
  width: 180px;
  background: #fff;
  border-radius: 6px;
  padding: 12px 0;
  flex-shrink: 0;
  height: fit-content;
}
.cat-group,
.cat-top,
.cat-child {
  padding: 8px 16px;
  cursor: pointer;
  color: #303133;
}
.cat-group {
  font-weight: 600;
}
.cat-child {
  padding-left: 28px;
  font-weight: 400;
  font-size: 14px;
  color: #606266;
}
.cat-group.active,
.cat-top.active,
.cat-child.active {
  color: #e4393c;
  background: #fef0f0;
}
.content {
  flex: 1;
}
.grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}
.card {
  background: #fff;
  border-radius: 6px;
  overflow: hidden;
  cursor: pointer;
  transition: box-shadow 0.2s;
}
.card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
}
.img {
  height: 180px;
  background: #f5f7fa;
}
.img-el {
  width: 100%;
  height: 100%;
}
.img-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #c0c4cc;
}
.info {
  padding: 10px 12px;
}
.name {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.subtitle {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.price-row {
  margin-top: 8px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.price {
  color: #e4393c;
  font-size: 18px;
  font-weight: 700;
}
.sales {
  color: #909399;
  font-size: 12px;
}
.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: center;
}
</style>
