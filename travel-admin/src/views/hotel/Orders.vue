<template>
  <div class="orders-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <h3>酒店订单管理</h3>
          <el-radio-group v-model="filterStatus" @change="handleFilterChange" size="small">
            <el-radio-button label="">全部</el-radio-button>
            <el-radio-button :label="0">待支付</el-radio-button>
            <el-radio-button :label="1">待确认</el-radio-button>
            <el-radio-button :label="2">待入住</el-radio-button>
            <el-radio-button :label="3">已入住</el-radio-button>
            <el-radio-button :label="4">已完成</el-radio-button>
            <el-radio-button :label="5">已取消</el-radio-button>
            <el-radio-button :label="6">退款中</el-radio-button>
            <el-radio-button :label="7">已退款</el-radio-button>
          </el-radio-group>
        </div>
      </template>

      <el-table :data="filteredOrders" border stripe v-loading="loading">
        <el-table-column prop="id" label="订单号" width="80" />
        <el-table-column label="房型信息" min-width="150">
          <template #default="scope">
            <div class="room-info-cell">
              <el-image 
                :src="formatImg(getFirstImg(scope.row.roomImage))" 
                class="room-thumb" 
                fit="cover"
              >
                <template #error>
                  <div class="image-slot">无图</div>
                </template>
              </el-image>
              <span>{{ scope.row.roomName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="预订人" width="120">
          <template #default="scope">
            <div>{{ scope.row.userName }}</div>
            <small style="color: #999">{{ scope.row.userPhone }}</small>
          </template>
        </el-table-column>
        <el-table-column label="入住日期" width="200">
          <template #default="scope">
            <div>{{ scope.row.checkInDate }} 至</div>
            <div>{{ scope.row.checkOutDate }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="totalPrice" label="总价" width="100">
          <template #default="scope">
            <span style="color: #ff5a5f; font-weight: bold">￥{{ scope.row.totalPrice }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="备注" prop="remark" show-overflow-tooltip />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button 
              v-if="scope.row.status === 1" 
              type="success" 
              size="small" 
              @click="handleConfirm(scope.row)"
            >
              确认订单
            </el-button>
            <el-button 
              v-if="scope.row.status === 2" 
              type="success" 
              size="small" 
              @click="handleCheckIn(scope.row)"
            >
              办理入住
            </el-button>
            <el-button 
              v-if="scope.row.status === 3" 
              type="primary" 
              size="small" 
              @click="handleCheckOut(scope.row)"
            >
              办理退房
            </el-button>
            <el-button 
              v-if="scope.row.status === 6" 
              type="warning" 
              size="small" 
              @click="handleCompleteRefund(scope.row)"
            >
              完成退款
            </el-button>
            <el-button 
              v-if="scope.row.status === 1 || scope.row.status === 2" 
              type="danger" 
              size="small" 
              @click="handleCancel(scope.row)"
            >
              取消订单
            </el-button>
            <el-button 
              v-if="scope.row.status === 4 || scope.row.status === 5 || scope.row.status === 7" 
              type="danger" 
              size="small" 
              @click="handleDelete(scope.row)"
            >
              删除
            </el-button>
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
          @size-change="getOrders"
          @current-change="getOrders"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const orderList = ref([])
const filterStatus = ref('')
const hotelId = localStorage.getItem('hotelId')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const filteredOrders = computed(() => {
  return orderList.value
})

const handleFilterChange = () => {
  currentPage.value = 1
  getOrders()
}

const getOrders = async () => {
  if (!hotelId) return
  loading.value = true
  try {
    const res = await axios.get(`http://localhost:8080/booking/hotel/${hotelId}`, {
      params: {
        page: currentPage.value,
        pageSize: pageSize.value
      }
    })
    let list = res.data.list || []
    if (filterStatus.value !== '') {
      list = list.filter(o => o.status === filterStatus.value)
    }
    orderList.value = list
    total.value = res.data.total || 0
  } catch (error) {
    ElMessage.error('获取订单列表失败')
  } finally {
    loading.value = false
  }
}

const handleConfirm = (row) => {
  ElMessageBox.confirm(
    `确认订单，订单状态将变为待入住？`,
    '确认订单',
    {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'info',
    }
  ).then(async () => {
    try {
      const res = await axios.post('http://localhost:8080/booking/confirm', { id: row.id })
      if (res.data && res.data.success) {
        ElMessage.success('确认成功')
        getOrders()
      } else {
        ElMessage.error(res.data.message || '操作失败')
      }
    } catch (error) {
      ElMessage.error('操作失败')
    }
  })
}

const handleCheckIn = (row) => {
  ElMessageBox.confirm(
    `确认客人 ${row.userName} 已到店并办理入住吗？`,
    '入住核销',
    {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'info',
    }
  ).then(async () => {
    try {
      const res = await axios.post('http://localhost:8080/booking/check-in', { id: row.id })
      if (res.data && res.data.success) {
        ElMessage.success('核销成功，已办理入住')
        getOrders()
      } else {
        ElMessage.error(res.data.message || '操作失败')
      }
    } catch (error) {
      ElMessage.error('操作失败')
    }
  })
}

const handleCheckOut = (row) => {
  ElMessageBox.confirm(
    `确认客人 ${row.userName} 已退房，订单完成？`,
    '办理退房',
    {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'info',
    }
  ).then(async () => {
    try {
      const res = await axios.post('http://localhost:8080/booking/check-out', { id: row.id })
      if (res.data && res.data.success) {
        ElMessage.success('退房成功，订单已完成')
        getOrders()
      } else {
        ElMessage.error(res.data.message || '操作失败')
      }
    } catch (error) {
      ElMessage.error('操作失败')
    }
  })
}

const handleCancel = (row) => {
  ElMessageBox.confirm(
    `确定要取消该订单吗？`,
    '取消订单',
    {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const res = await axios.post('http://localhost:8080/booking/cancel', { id: row.id })
      if (res.data && res.data.success) {
        ElMessage.success('订单已取消')
        getOrders()
      } else {
        ElMessage.error(res.data.message || '操作失败')
      }
    } catch (error) {
      ElMessage.error('操作失败')
    }
  })
}

const handleCompleteRefund = (row) => {
  ElMessageBox.confirm(
    `确认退款已完成，订单状态将变为已退款？`,
    '完成退款',
    {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const res = await axios.post('http://localhost:8080/booking/complete-refund', { id: row.id })
      if (res.data && res.data.success) {
        ElMessage.success('退款已完成')
        getOrders()
      } else {
        ElMessage.error(res.data.message || '操作失败')
      }
    } catch (error) {
      ElMessage.error('操作失败')
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除该订单吗？删除后无法恢复！`,
    '删除订单',
    {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const res = await axios.delete(`http://localhost:8080/booking/${row.id}`)
      if (res.data) {
        ElMessage.success('删除成功')
        getOrders()
      }
    } catch (error) {
      ElMessage.error('操作失败')
    }
  })
}

const getStatusText = (status) => {
  const map = {
    0: '待支付',
    1: '待确认',
    2: '待入住',
    3: '已入住',
    4: '已完成',
    5: '已取消',
    6: '退款中',
    7: '已退款'
  }
  return map[status] || '未知'
}

const getStatusType = (status) => {
  const map = {
    0: 'warning',
    1: 'warning',
    2: 'primary',
    3: 'success',
    4: 'info',
    5: 'info',
    6: 'warning',
    7: 'info'
  }
  return map[status] || ''
}

const getFirstImg = (imageStr) => {
  if (!imageStr) return ''
  return imageStr.split(',')[0]
}

const formatImg = (url) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  const baseUrl = 'http://localhost:8080'
  if (url.startsWith('/uploads/')) return baseUrl + url
  return baseUrl + '/assets/' + url
}

onMounted(getOrders)
</script>

<style scoped>
.orders-container {
  padding: 20px;
  font-size: 16px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.card-header h3 {
  font-size: 24px;
  margin: 0;
}
.room-info-cell {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 16px;
}
.room-thumb {
  width: 100px;
  height: 75px;
  border-radius: 6px;
}
:deep(.el-table) {
  font-size: 15px;
}
:deep(.el-table th) {
  font-size: 16px;
}
:deep(.el-table td) {
  padding: 16px 0;
}
:deep(.el-button) {
  font-size: 14px;
  padding: 10px 18px;
}
:deep(.el-tag) {
  font-size: 14px;
  padding: 6px 12px;
}
:deep(.el-radio-button__inner) {
  font-size: 14px;
  padding: 10px 20px;
}
.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
