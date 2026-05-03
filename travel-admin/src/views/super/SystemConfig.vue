<template>
  <div class="system-config-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <el-radio-group v-model="activeTab" @change="handleTabChange">
            <el-radio-button label="hotel-tag">酒店标签</el-radio-button>
            <el-radio-button label="hotel-facility">酒店设施</el-radio-button>
            <el-radio-button label="room-facility">房间设施</el-radio-button>
          </el-radio-group>
          <el-button type="primary" @click="openAddDialog">新增</el-button>
        </div>
      </template>

      <el-table :data="tableData" border style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="名称" />
        <el-table-column v-if="activeTab !== 'hotel-tag'" label="类型" width="150">
          <template #default="scope">
            <el-tag :type="scope.row.type === 1 ? 'primary' : 'success'">
              {{ scope.row.type === 1 ? '酒店设施' : '房间设施' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="openEditDialog(scope.row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showDialog" :title="isEdit ? '编辑' : '新增'" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="名称">
          <el-input v-model="form.name" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item v-if="activeTab !== 'hotel-tag'" label="类型">
          <el-radio-group v-model="form.type">
            <el-radio :label="1">酒店设施</el-radio>
            <el-radio :label="2">房间设施</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          {{ isEdit ? '更新' : '添加' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

const activeTab = ref('hotel-tag')
const tableData = ref([])
const loading = ref(false)
const showDialog = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const form = ref({
  id: null,
  name: '',
  type: 1
})

const getList = async () => {
  loading.value = true
  try {
    let url = ''
    let params = {}
    if (activeTab.value === 'hotel-tag') {
      url = 'http://localhost:8080/hotel-tag/list'
    } else if (activeTab.value === 'hotel-facility') {
      url = 'http://localhost:8080/room-facility/list'
      params = { type: 1 }
    } else {
      url = 'http://localhost:8080/room-facility/list'
      params = { type: 2 }
    }
    const res = await axios.get(url, { params })
    tableData.value = res.data || []
  } catch (error) {
    console.error('获取列表失败:', error)
    ElMessage.error('获取列表失败')
  } finally {
    loading.value = false
  }
}

const handleTabChange = () => {
  getList()
}

const openAddDialog = () => {
  isEdit.value = false
  form.value = {
    id: null,
    name: '',
    type: activeTab.value === 'hotel-facility' ? 1 : 2
  }
  showDialog.value = true
}

const openEditDialog = (row) => {
  isEdit.value = true
  form.value = { ...row }
  showDialog.value = true
}

const handleSubmit = async () => {
  if (!form.value.name) {
    ElMessage.warning('请输入名称')
    return
  }
  submitting.value = true
  try {
    let url = ''
    if (activeTab.value === 'hotel-tag') {
      url = isEdit.value ? 'http://localhost:8080/hotel-tag/update' : 'http://localhost:8080/hotel-tag/add'
    } else {
      url = isEdit.value ? 'http://localhost:8080/room-facility/update' : 'http://localhost:8080/room-facility/add'
    }
    const res = await axios.post(url, form.value)
    if (res.data) {
      ElMessage.success(isEdit.value ? '更新成功' : '添加成功')
      showDialog.value = false
      getList()
    } else {
      ElMessage.error('操作失败')
    }
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('操作失败')
  } finally {
    submitting.value = false
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定删除「${row.name}」吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      let url = ''
      if (activeTab.value === 'hotel-tag') {
        url = `http://localhost:8080/hotel-tag/${row.id}`
      } else {
        url = `http://localhost:8080/room-facility/${row.id}`
      }
      await axios.delete(url)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.system-config-container {
  padding: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
