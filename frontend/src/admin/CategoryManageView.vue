<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { createCategory, deleteCategory, listCategories, updateCategory } from '@/api/admin'
import type { Category } from '@/types'

const categories = ref<Category[]>([])
const dialogVisible = ref(false)
const editingId = ref<number>()
const form = reactive({ name: '', parentId: 0, sort: 0 })

async function load() {
  categories.value = await listCategories()
}

function openCreate() {
  editingId.value = undefined
  form.name = ''
  form.parentId = 0
  form.sort = 0
  dialogVisible.value = true
}

function openEdit(c: Category) {
  editingId.value = c.id
  form.name = c.name
  form.parentId = c.parentId
  form.sort = c.sort
  dialogVisible.value = true
}

async function onSubmit() {
  if (!form.name) {
    ElMessage.warning('请输入分类名称')
    return
  }
  if (editingId.value) {
    await updateCategory(editingId.value, { ...form })
  } else {
    await createCategory({ ...form })
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  load()
}

async function onDelete(c: Category) {
  await deleteCategory(c.id)
  ElMessage.success('已删除')
  load()
}

function parentName(id: number) {
  const p = categories.value.find((c) => c.id === id)
  return p ? p.name : '—'
}

onMounted(load)
</script>

<template>
  <div class="page">
    <div class="toolbar">
      <el-button type="primary" @click="openCreate">新增分类</el-button>
    </div>
    <el-table :data="categories" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="名称" min-width="140" />
      <el-table-column label="父分类" width="140">
        <template #default="{ row }">{{ parentName(row.parentId) }}</template>
      </el-table-column>
      <el-table-column prop="sort" label="排序" width="80" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
          <el-button link type="danger" @click="onDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑分类' : '新增分类'" width="420px">
      <el-form label-width="80px">
        <el-form-item label="名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="父分类">
          <el-select v-model="form.parentId" style="width: 100%">
            <el-option :value="0" label="（无，作为一级分类）" />
            <el-option v-for="c in categories.filter((x) => x.parentId === 0 && x.id !== editingId)" :key="c.id" :value="c.id" :label="c.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" />
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
}
</style>
