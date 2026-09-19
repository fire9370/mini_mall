<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import type { UploadRequestOptions } from 'element-plus'
import {
  createProduct,
  deleteProduct,
  listCategories,
  pageProducts,
  updateProduct,
  updateProductStatus,
  uploadImage
} from '@/api/admin'
import type { ProductForm } from '@/api/admin'
import type { Category, Product } from '@/types'

const products = ref<Product[]>([])
const categories = ref<Category[]>([])
const total = ref(0)
const page = ref(1)
const size = 10
const keyword = ref('')

const dialogVisible = ref(false)
const editingId = ref<number>()
const form = reactive<ProductForm>({ categoryId: 0, name: '', subtitle: '', mainImage: '', detail: '', price: 0, stock: 0, status: 1, images: [] })

async function load() {
  const result = await pageProducts({ page: page.value, size, keyword: keyword.value || undefined })
  products.value = result.records
  total.value = result.total
}

async function loadCategories() {
  categories.value = await listCategories()
}

function categoryName(id: number) {
  return categories.value.find((c) => c.id === id)?.name || '—'
}

function resetForm() {
  Object.assign(form, { categoryId: 0, name: '', subtitle: '', mainImage: '', detail: '', price: 0, stock: 0, status: 1, images: [] })
}

function openCreate() {
  editingId.value = undefined
  resetForm()
  dialogVisible.value = true
}

function openEdit(p: Product) {
  editingId.value = p.id
  Object.assign(form, {
    categoryId: p.categoryId,
    name: p.name,
    subtitle: p.subtitle || '',
    mainImage: p.mainImage || '',
    detail: p.detail || '',
    price: Number(p.price),
    stock: p.stock,
    status: p.status,
    images: p.images || []
  })
  dialogVisible.value = true
}

async function onSubmit() {
  if (!form.name || !form.categoryId) {
    ElMessage.warning('请填写商品名称并选择分类')
    return
  }
  if (editingId.value) {
    await updateProduct(editingId.value, { ...form })
  } else {
    await createProduct({ ...form })
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  load()
}

async function onDelete(p: Product) {
  await deleteProduct(p.id)
  ElMessage.success('已删除')
  load()
}

async function onToggleStatus(p: Product) {
  await updateProductStatus(p.id, p.status === 1 ? 0 : 1)
  ElMessage.success('状态已更新')
  load()
}

async function handleUploadMain(options: UploadRequestOptions) {
  try {
    const url = await uploadImage(options.file)
    form.mainImage = url
    options.onSuccess(url)
  } catch {
    // 上传失败，错误提示已由 axios 拦截器处理
  }
}

async function handleUploadImages(options: UploadRequestOptions) {
  try {
    const url = await uploadImage(options.file)
    form.images!.push(url)
    options.onSuccess(url)
  } catch {
    // 上传失败，错误提示已由 axios 拦截器处理
  }
}

function removeImage(index: number) {
  form.images!.splice(index, 1)
}

onMounted(() => {
  load()
  loadCategories()
})
</script>

<template>
  <div class="page">
    <div class="toolbar">
      <el-input v-model="keyword" placeholder="搜索商品" clearable style="width: 220px" @keyup.enter="load" />
      <el-button type="primary" @click="load">搜索</el-button>
      <el-button type="primary" @click="openCreate">新增商品</el-button>
    </div>

    <el-table :data="products" border>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="name" label="名称" min-width="160" />
      <el-table-column label="分类" width="120">
        <template #default="{ row }">{{ categoryName(row.categoryId) }}</template>
      </el-table-column>
      <el-table-column label="价格" width="110">
        <template #default="{ row }">¥{{ Number(row.price).toFixed(2) }}</template>
      </el-table-column>
      <el-table-column prop="stock" label="库存" width="80" />
      <el-table-column prop="sales" label="销量" width="80" />
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '上架' : '下架' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
          <el-button link :type="row.status === 1 ? 'warning' : 'success'" @click="onToggleStatus(row)">
            {{ row.status === 1 ? '下架' : '上架' }}
          </el-button>
          <el-button link type="danger" @click="onDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination">
      <el-pagination background layout="prev, pager, next, total" :total="total" :page-size="size" :current-page="page" @current-change="(p: number) => { page = p; load() }" />
    </div>

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑商品' : '新增商品'" width="640px">
      <el-form label-width="80px">
        <el-form-item label="名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.categoryId" style="width: 100%">
            <el-option v-for="c in categories" :key="c.id" :value="c.id" :label="c.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="副标题">
          <el-input v-model="form.subtitle" />
        </el-form-item>
        <el-form-item label="价格">
          <el-input-number v-model="form.price" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="库存">
          <el-input-number v-model="form.stock" :min="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">上架</el-radio>
            <el-radio :label="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="主图">
          <el-upload :show-file-list="false" :http-request="handleUploadMain" accept="image/*">
            <el-image v-if="form.mainImage" :src="form.mainImage" fit="cover" class="preview" />
            <el-button v-else>上传主图</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="轮播图">
          <div class="images">
            <div v-for="(img, i) in form.images" :key="i" class="img-item">
              <el-image :src="img" fit="cover" class="preview" />
              <el-button link type="danger" size="small" @click="removeImage(i)">移除</el-button>
            </div>
            <el-upload :show-file-list="false" :http-request="handleUploadImages" accept="image/*" multiple>
              <el-button>+ 添加</el-button>
            </el-upload>
          </div>
        </el-form-item>
        <el-form-item label="详情">
          <el-input v-model="form.detail" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="onSubmit">保存</el-button>
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
  display: flex;
  gap: 8px;
}
.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
.preview {
  width: 80px;
  height: 80px;
  border-radius: 4px;
}
.images {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  align-items: center;
}
.img-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}
</style>
