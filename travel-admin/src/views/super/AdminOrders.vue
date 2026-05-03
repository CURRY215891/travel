<template>
  <div class="admin-orders-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-label">今日订单数</div>
            <div class="stat-value">{{ stats.todayOrderCount || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-label">待处理订单</div>
            <div class="stat-value" style="color: #f56c6c">{{ stats.pendingCount || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-label">今日交易额</div>
            <div class="stat-value" style="color: #67c23a">¥{{ stats.todayAmount || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-label">订单总数</div>
            <div class="stat-value">{{ total || 0 }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>订单状态分布</span>
            </div>
          </template>
          <div ref="statusChartRef" style="height: 300px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>订单趋势（最近30天）</span>
            </div>
          </template>
          <div ref="trendChartRef" style="height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <h3>全平台订单监控</h3>
        </div>
      </template>

      <div class="filter-bar">
        <el-form :inline="true" :model="filters" size="small">
          <el-form-item label="酒店">
            <el-select v-model="filters.hotelId" placeholder="全部酒店" clearable style="width: 200px;">
              <el-option v-for="hotel in hotelList" :key="hotel.id" :label="hotel.name" :value="hotel.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="filters.status" placeholder="全部状态" clearable style="width: 150px;">
              <el-option label="待支付" :value="0" />
              <el-option label="待确认" :value="1" />
              <el-option label="待入住" :value="2" />
              <el-option label="已入住" :value="3" />
              <el-option label="已完成" :value="4" />
              <el-option label="已取消" :value="5" />
              <el-option label="退款中" :value="6" />
              <el-option label="已退款" :value="7" />
            </el-select>
          </el-form-item>
          <el-form-item label="日期">
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              style="width: 300px;"
            />
          </el-form-item>
          <el-form-item label="搜索">
            <el-input v-model="filters.keyword" placeholder="订单号/姓名/手机号" clearable style="width: 200px;" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="loadOrders" :loading="loading">查询</el-button>
            <el-button @click="resetFilters">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="orderList" border stripe v-loading="loading">
        <el-table-column prop="id" label="订单号" width="80" />
        <el-table-column label="酒店" width="180">
          <template #default="scope">
            <span>{{ scope.row.hotelName }}</span>
          </template>
        </el-table-column>
        <el-table-column label="房型" width="150">
          <template #default="scope">
            <span>{{ scope.row.roomName }}</span>
          </template>
        </el-table-column>
        <el-table-column label="预订人" width="120">
          <template #default="scope">
            <div>{{ scope.row.userName }}</div>
            <small style="color: #999">{{ maskPhone(scope.row.userPhone) }}</small>
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
        <el-table-column prop="createTime" label="下单时间" width="170" />
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="scope">
            <div style="display: flex; gap: 6px; flex-wrap: wrap;">
              <el-button type="primary" size="small" @click="viewDetail(scope.row)">详情</el-button>
              <el-button 
                size="small" 
                type="warning" 
                @click="adminCancel(scope.row)"
              >
                强制取消
              </el-button>
              <el-button 
                size="small" 
                type="danger" 
                @click="adminRefund(scope.row)"
              >
                强制退款
              </el-button>
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
          @size-change="loadOrders"
          @current-change="loadOrders"
        />
      </div>
    </el-card>

    <el-dialog v-model="showDetailDialog" title="订单详情" width="700px">
      <div v-if="currentOrder">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单号">{{ currentOrder.id }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(currentOrder.status)">{{ getStatusText(currentOrder.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="酒店">{{ currentOrder.hotelName }}</el-descriptions-item>
          <el-descriptions-item label="房型">{{ currentOrder.roomName }}</el-descriptions-item>
          <el-descriptions-item label="预订人">{{ currentOrder.userName }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ currentOrder.userPhone }}</el-descriptions-item>
          <el-descriptions-item label="入住日期">{{ currentOrder.checkInDate }}</el-descriptions-item>
          <el-descriptions-item label="退房日期">{{ currentOrder.checkOutDate }}</el-descriptions-item>
          <el-descriptions-item label="订单金额" span="2">
            <span style="color: #ff5a5f; font-size: 20px; font-weight: bold;">￥{{ currentOrder.totalPrice }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="下单时间" span="2">{{ currentOrder.createTime }}</el-descriptions-item>
          <el-descriptions-item v-if="currentOrder.payTime" label="支付时间" span="2">{{ currentOrder.payTime }}</el-descriptions-item>
          <el-descriptions-item v-if="currentOrder.completeTime" label="完成时间" span="2">{{ currentOrder.completeTime }}</el-descriptions-item>
          <el-descriptions-item v-if="currentOrder.remark" label="备注" span="2">{{ currentOrder.remark }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as echarts from 'echarts'

const loading = ref(false)
const orderList = ref([])
const stats = ref({})
const hotelList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const dateRange = ref([])
const showDetailDialog = ref(false)
const currentOrder = ref(null)

const statusChartRef = ref(null)
const trendChartRef = ref(null)
let statusChart = null
let trendChart = null

const filters = ref({
  hotelId: null,
  status: null,
  keyword: ''
})

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

const maskPhone = (phone) => {
  if (!phone) return ''
  if (phone.length >= 11) {
    return phone.substring(0, 3) + '****' + phone.substring(7)
  }
  return phone
}

const loadStats = async () => {
  try {
    const res = await axios.get('http://localhost:8080/booking/admin/stats')
    stats.value = res.data || {}
    await nextTick()
    renderCharts()
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

const loadHotels = async () => {
  try {
    const res = await axios.get('http://localhost:8080/hotel/list')
    hotelList.value = res.data || []
  } catch (error) {
    console.error('加载酒店列表失败:', error)
  }
}

const loadOrders = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value
    }
    if (filters.value.hotelId) {
      params.hotelId = filters.value.hotelId
    }
    if (filters.value.status !== null && filters.value.status !== '') {
      params.status = filters.value.status
    }
    if (filters.value.keyword) {
      params.keyword = filters.value.keyword
    }
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }
    const res = await axios.get('http://localhost:8080/booking/admin/list', { params })
    orderList.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (error) {
    ElMessage.error('获取订单列表失败')
  } finally {
    loading.value = false
  }
}

const resetFilters = () => {
  filters.value = {
    hotelId: null,
    status: null,
    keyword: ''
  }
  dateRange.value = []
  currentPage.value = 1
  loadOrders()
}

const viewDetail = async (row) => {
  try {
    const res = await axios.get(`http://localhost:8080/booking/admin/${row.id}`)
    currentOrder.value = res.data
    showDetailDialog.value = true
  } catch (error) {
    ElMessage.error('获取订单详情失败')
  }
}

const adminCancel = (row) => {
  ElMessageBox.prompt('请输入取消原因', '强制取消订单', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    inputPattern: /.+/,
    inputErrorMessage: '请输入取消原因'
  }).then(async ({ value }) => {
    try {
      const res = await axios.post(`http://localhost:8080/booking/admin/${row.id}/cancel`, { reason: value })
      if (res.data && res.data.success) {
        ElMessage.success('强制取消成功')
        loadOrders()
        loadStats()
      } else {
        ElMessage.error(res.data.message || '操作失败')
      }
    } catch (error) {
      ElMessage.error('操作失败')
    }
  }).catch(() => {})
}

const adminRefund = (row) => {
  ElMessageBox.prompt('请输入退款原因', '强制退款', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    inputPattern: /.+/,
    inputErrorMessage: '请输入退款原因'
  }).then(async ({ value }) => {
    try {
      const res = await axios.post(`http://localhost:8080/booking/admin/${row.id}/refund`, { reason: value })
      if (res.data && res.data.success) {
        ElMessage.success('强制退款成功')
        loadOrders()
        loadStats()
      } else {
        ElMessage.error(res.data.message || '操作失败')
      }
    } catch (error) {
      ElMessage.error('操作失败')
    }
  }).catch(() => {})
}

const renderCharts = () => {
  if (statusChartRef.value) {
    statusChart = echarts.init(statusChartRef.value)
    const statusNames = ['待支付', '待确认', '待入住', '已入住', '已完成', '已取消', '退款中', '已退款']
    const data = []
    for (let i = 0; i < 8; i++) {
      data.push({
        name: statusNames[i],
        value: stats.value.statusDistribution ? (stats.value.statusDistribution[i] || 0) : 0
      })
    }
    statusChart.setOption({
      tooltip: { trigger: 'item' },
      legend: { bottom: '5%', left: 'center' },
      series: [{
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
        label: { show: false },
        emphasis: { label: { show: true, fontSize: 14, fontWeight: 'bold' } },
        labelLine: { show: false },
        data: data
      }]
    })
  }

  if (trendChartRef.value && stats.value.orderTrend) {
    trendChart = echarts.init(trendChartRef.value)
    const dates = Object.keys(stats.value.orderTrend)
    const counts = Object.values(stats.value.orderTrend)
    trendChart.setOption({
      tooltip: { trigger: 'axis' },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: { type: 'category', boundaryGap: false, data: dates },
      yAxis: { type: 'value' },
      series: [{
        name: '订单数',
        type: 'line',
        stack: 'Total',
        data: counts,
        smooth: true,
        areaStyle: { opacity: 0.3 }
      }]
    })
  }
}

onMounted(() => {
  loadStats()
  loadHotels()
  loadOrders()
})
</script>

<style scoped>
.admin-orders-container {
  padding: 20px;
  font-size: 15px;
}
.stat-card {
  cursor: pointer;
}
.stat-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}
.stat-content {
  text-align: center;
  padding: 10px 0;
}
.stat-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}
.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #409eff;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.card-header h3 {
  font-size: 20px;
  margin: 0;
}
.filter-bar {
  margin-bottom: 20px;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 6px;
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
