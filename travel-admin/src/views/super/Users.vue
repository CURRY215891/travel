<template>
  <div class="users-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <h3>用户治理中心</h3>
          <div class="filter-area">
            <el-select v-model="statusFilter" placeholder="用户状态" clearable @change="getList">
              <el-option label="全部" :value="null" />
              <el-option label="正常" :value="0" />
              <el-option label="已封禁" :value="1" />
            </el-select>
            <el-input
              v-model="searchKeyword"
              placeholder="搜索昵称"
              clearable
              style="width: 200px"
              @keyup.enter="getList"
            >
              <template #append>
                <el-button @click="getList">搜索</el-button>
              </template>
            </el-input>
          </div>
        </div>
      </template>

      <el-table :data="userList" border style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="用户ID" width="100" />
        <el-table-column prop="nickname" label="昵称" width="150" />
        <el-table-column label="头像" width="100">
          <template #default="scope">
            <el-image
              :src="formatImg(scope.row.avatar)"
              style="width: 40px; height: 40px; border-radius: 50%;"
              fit="cover"
            />
          </template>
        </el-table-column>
        <el-table-column label="状态" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'danger' : 'success'">
              {{ scope.row.status === 1 ? '已封禁' : '正常' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="180">
          <template #default="scope">
            {{ formatTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="scope">
            <div style="display: flex; gap: 6px; flex-wrap: wrap;">
              <el-button type="primary" size="small" @click="handleViewDetail(scope.row)">详情</el-button>
              <el-button
                v-if="scope.row.status !== 1"
                type="warning"
                size="small"
                @click="handleBan(scope.row)"
              >
                封禁
              </el-button>
              <el-button
                v-if="scope.row.status === 1"
                type="success"
                size="small"
                @click="handleUnban(scope.row)"
              >
                解封
              </el-button>
              <el-button type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="getList"
          @current-change="getList"
        />
      </div>
    </el-card>

    <el-dialog v-model="showDetailDialog" title="用户详情" width="600px">
      <el-descriptions :column="1" border v-if="currentUser">
        <el-descriptions-item label="用户ID">{{ currentUser.id }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ currentUser.nickname || '-' }}</el-descriptions-item>
        <el-descriptions-item label="OpenID">{{ currentUser.openid || '-' }}</el-descriptions-item>
        <el-descriptions-item label="头像">
          <el-image
            v-if="currentUser.avatar"
            :src="formatImg(currentUser.avatar)"
            style="width: 100px; height: 100px; border-radius: 50%;"
            fit="cover"
          />
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentUser.status === 1 ? 'danger' : 'success'">
            {{ currentUser.status === 1 ? '已封禁' : '正常' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="注册时间">{{ formatTime(currentUser.createTime) }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="showDetailDialog = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

const userList = ref([])
const loading = ref(false)
const statusFilter = ref(null)
const searchKeyword = ref('')
const showDetailDialog = ref(false)
const currentUser = ref(null)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const getList = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value
    }
    if (statusFilter.value !== null) {
      params.status = statusFilter.value
    }
    if (searchKeyword.value) {
      params.keyword = searchKeyword.value
    }
    const res = await axios.get('http://localhost:8080/user/list', { params })
    userList.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('获取用户列表失败:', error)
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

const handleBan = async (row) => {
  ElMessageBox.confirm(`确定要封禁用户「${row.nickname}」吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await axios.post(`http://localhost:8080/user/ban/${row.id}`)
      if (res.data) {
        ElMessage.success('封禁成功')
        getList()
      } else {
        ElMessage.error('操作失败')
      }
    } catch (error) {
      ElMessage.error('操作失败')
    }
  }).catch(() => {})
}

const handleUnban = async (row) => {
  try {
    const res = await axios.post(`http://localhost:8080/user/unban/${row.id}`)
    if (res.data) {
      ElMessage.success('解封成功')
      getList()
    } else {
      ElMessage.error('操作失败')
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleViewDetail = (row) => {
  currentUser.value = { ...row }
  showDetailDialog.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除用户「${row.nickname}」吗？此操作不可恢复！`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await axios.delete(`http://localhost:8080/user/${row.id}`)
      if (res.data) {
        ElMessage.success('删除成功')
        getList()
      } else {
        ElMessage.error('删除失败')
      }
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const formatTime = (time) => {
  if (!time) return '-'
  const date = new Date(time)
  return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
}

const formatImg = (url) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  const baseUrl = 'http://localhost:8080'
  if (url.startsWith('/uploads/')) return baseUrl + url
  if (url.startsWith('/assets/')) return baseUrl + url
  return baseUrl + '/assets/' + url
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.users-container {
  padding: 20px;
  font-size: 15px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.filter-area {
  display: flex;
  gap: 10px;
}
.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
:deep(.el-table) {
  font-size: 14px;
}
:deep(.el-table th) {
  font-size: 15px;
}
</style>
