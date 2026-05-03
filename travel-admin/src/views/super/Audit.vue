<template>
  <div class="audit-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <h3>内容审核</h3>
          <div class="filter-area">
            <el-select v-model="auditStatus" placeholder="审核状态" clearable @change="getList" style="min-width: 120px;">
              <el-option label="全部" :value="null" />
              <el-option label="正常" :value="1" />
              <el-option label="已屏蔽" :value="2" />
            </el-select>
          </div>
        </div>
      </template>

      <div class="type-tabs">
        <div 
          class="type-tab-item" 
          :class="{ active: activeTab === 'post' }"
          @click="handleTypeChange('post')"
        >
          动态评论
        </div>
        <div 
          class="type-tab-item" 
          :class="{ active: activeTab === 'comment' && commentType === 1 }"
          @click="handleTypeChange('comment', 1)"
        >
          景点评论
        </div>
        <div 
          class="type-tab-item" 
          :class="{ active: activeTab === 'comment' && commentType === 2 }"
          @click="handleTypeChange('comment', 2)"
        >
          美食评论
        </div>
        <div 
          class="type-tab-item" 
          :class="{ active: activeTab === 'comment' && commentType === 3 }"
          @click="handleTypeChange('comment', 3)"
        >
          酒店评论
        </div>
        <div v-if="activeTab === 'comment' && commentType === 3" class="hotel-selector">
          <el-select 
            v-model="selectedHotelId" 
            placeholder="切换酒店" 
            clearable 
            @change="getList"
            style="min-width: 120px;"
          >
            <el-option 
              :label="hotel.name" 
              :value="hotel.id" 
              v-for="hotel in hotelList" 
              :key="hotel.id" 
            />
          </el-select>
        </div>
      </div>

      <el-table :data="tableData" border style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="内容" min-width="300">
          <template #default="scope">
            <div class="content-cell">
              <div class="content-text">{{ scope.row.content }}</div>
              <div v-if="activeTab === 'post'" class="post-image">
                <img v-if="scope.row.image" :src="formatImg(scope.row.image)" alt="动态图片" />
                <div v-else style="color: #999; font-size: 12px;">无图片</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="发布人" width="150">
          <template #default="scope">
            <span class="author-name">{{ scope.row.nickname || '用户' + scope.row.userId }}</span>
          </template>
        </el-table-column>
        <el-table-column v-if="activeTab === 'comment'" label="来源" width="150">
          <template #default="scope">
            <el-tag :type="getSourceType(scope.row.type || 0)">{{ getSourceLabel(scope.row) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.auditStatus)">
              {{ getStatusLabel(scope.row.auditStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="scope">
            {{ formatTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button 
              v-if="scope.row.auditStatus === 1" 
              type="warning" 
              size="small" 
              @click="handleReject(scope.row)"
            >
              屏蔽
            </el-button>
            <el-button 
              v-if="scope.row.auditStatus === 2" 
              type="success" 
              size="small" 
              @click="handleApprove(scope.row)"
            >
              恢复
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

const activeTab = ref('post')
const auditStatus = ref(null)
const commentType = ref(1)
const selectedHotelId = ref(null)
const tableData = ref([])
const loading = ref(false)
const hotelList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const getStatusType = (status) => {
  const map = { 0: 'warning', 1: 'success', 2: 'danger' }
  return map[status] || 'info'
}

const getStatusLabel = (status) => {
  const map = { 0: '待审核', 1: '正常', 2: '已屏蔽' }
  return map[status] || '未知'
}

const getSourceType = (type) => {
  const map = { 0: 'info', 1: 'primary', 2: 'success', 3: 'warning' }
  return map[type] || 'info'
}

const getSourceLabel = (row) => {
  if (activeTab.value === 'post') return '动态'
  if (row.attrName) {
    return row.attrName
  }
  const map = { 1: '景点', 2: '美食', 3: '酒店' }
  return map[row.type] || '其他'
}

const getHotelList = async () => {
  try {
    const res = await axios.get('http://localhost:8080/hotel/list')
    hotelList.value = res.data || []
  } catch (error) {
    console.error('获取酒店列表失败:', error)
  }
}

const getList = async () => {
  loading.value = true
  try {
    if (activeTab.value === 'post') {
      const params = {
        page: currentPage.value,
        pageSize: pageSize.value
      }
      if (auditStatus.value !== null) params.auditStatus = auditStatus.value
      const res = await axios.get('http://localhost:8080/post/admin/list', { params })
      tableData.value = res.data.list || []
      total.value = res.data.total || 0
    } else {
      const params = {
        page: currentPage.value,
        pageSize: pageSize.value
      }
      if (auditStatus.value !== null) params.auditStatus = auditStatus.value
      if (commentType.value !== null) params.type = commentType.value
      if (commentType.value === 3 && selectedHotelId.value) params.hotelId = selectedHotelId.value
      
      const res = await axios.get('http://localhost:8080/comment/admin/list', { params })
      tableData.value = res.data.list || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('获取列表失败:', error)
    ElMessage.error('获取列表失败')
  } finally {
    loading.value = false
  }
}

const handleTypeChange = (tab, type = 1) => {
  activeTab.value = tab
  if (tab === 'comment') {
    commentType.value = type
    if (type !== 3) {
      selectedHotelId.value = null
    }
  } else {
    commentType.value = 1
    selectedHotelId.value = null
  }
  currentPage.value = 1
  getList()
}

const handleApprove = async (row) => {
  try {
    const url = activeTab.value === 'post' 
      ? `http://localhost:8080/post/audit/approve/${row.id}`
      : `http://localhost:8080/comment/audit/approve/${row.id}`
    const res = await axios.post(url)
    if (res.data) {
      ElMessage.success('已恢复')
      getList()
    } else {
      ElMessage.error('操作失败')
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleReject = async (row) => {
  try {
    const url = activeTab.value === 'post' 
      ? `http://localhost:8080/post/audit/reject/${row.id}`
      : `http://localhost:8080/comment/audit/reject/${row.id}`
    const res = await axios.post(url)
    if (res.data) {
      ElMessage.success('已屏蔽')
      getList()
    } else {
      ElMessage.error('操作失败')
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定删除此内容吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const url = activeTab.value === 'post' 
        ? `http://localhost:8080/post/admin/${row.id}`
        : `http://localhost:8080/comment/admin/${row.id}`
      await axios.delete(url)
      ElMessage.success('删除成功')
      getList()
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
  getHotelList()
  getList()
})
</script>

<style scoped>
.audit-container {
  padding: 20px;
  font-size: 15px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.card-header h3 {
  margin: 0;
  font-size: 19px;
}
.filter-area {
  display: flex;
  gap: 10px;
  align-items: center;
}
.type-tabs {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  flex-wrap: wrap;
  align-items: center;
}
.type-tab-item {
  padding: 8px 20px;
  background: #f5f7fa;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  cursor: pointer;
  font-size: 15px;
  transition: all 0.3s;
  user-select: none;
}
.type-tab-item:hover {
  background: #ecf5ff;
  border-color: #409eff;
  color: #409eff;
}
.type-tab-item.active {
  background: #409eff;
  border-color: #409eff;
  color: #fff;
}
.hotel-selector {
  margin-left: 10px;
}
.content-cell {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.content-text {
  line-height: 1.5;
  color: #333;
  font-size: 15px;
}
.post-image img {
  max-width: 200px;
  max-height: 200px;
  border-radius: 6px;
  object-fit: cover;
  border: 1px solid #eee;
}
.author-name {
  font-size: 15px;
  color: #333;
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
