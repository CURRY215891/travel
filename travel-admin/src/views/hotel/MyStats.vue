<template>
  <div class="stats-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <h3>营收报表统计</h3>
        </div>
      </template>

      <el-empty v-if="loading" description="加载中..." />

      <div v-else>
        <el-row :gutter="20" style="margin-bottom: 20px;">
          <el-col :span="8">
            <el-card shadow="hover">
              <div class="stat-card">
                <div class="stat-label">今日营收</div>
                <div class="stat-value">¥{{ todayRevenue.toFixed(2) }}</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover">
              <div class="stat-card">
                <div class="stat-label">本月营收</div>
                <div class="stat-value">¥{{ monthRevenue.toFixed(2) }}</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover">
              <div class="stat-card">
                <div class="stat-label">总订单数</div>
                <div class="stat-value">{{ totalOrders }}</div>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <el-row :gutter="20" style="margin-bottom: 20px;">
          <el-col :span="12">
            <el-card>
              <template #header>
                <div class="card-header">
                  <span>日营收趋势（最近30天）</span>
                </div>
              </template>
              <div ref="dailyRevenueChartRef" style="width: 100%; height: 350px;"></div>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card>
              <template #header>
                <div class="card-header">
                  <span>订单成交趋势（最近30天）</span>
                </div>
              </template>
              <div ref="orderTrendChartRef" style="width: 100%; height: 350px;"></div>
            </el-card>
          </el-col>
        </el-row>

        <el-row :gutter="20" style="margin-bottom: 20px;">
          <el-col :span="12">
            <el-card>
              <template #header>
                <div class="card-header">
                  <span>月度营收趋势（最近12个月）</span>
                </div>
              </template>
              <div ref="monthlyRevenueChartRef" style="width: 100%; height: 350px;"></div>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card>
              <template #header>
                <div class="card-header">
                  <span>各房型入住率</span>
                </div>
              </template>
              <div ref="roomOccupancyChartRef" style="width: 100%; height: 350px;"></div>
            </el-card>
          </el-col>
        </el-row>

        <el-card v-if="roomOccupancy.length > 0">
          <template #header>
            <div class="card-header">
              <span>房型入住率详情</span>
            </div>
          </template>
          <el-table :data="roomOccupancy" border style="width: 100%">
            <el-table-column prop="roomName" label="房型名称" />
            <el-table-column prop="bookingCount" label="预订次数" width="150" />
            <el-table-column label="入住率" width="180">
              <template #default="{ row }">
                <el-progress :percentage="row.occupancyRate" :color="getProgressColor(row.occupancyRate)" />
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import axios from 'axios'
import * as echarts from 'echarts'

const loading = ref(true)
const statsData = ref(null)
const dailyRevenueChartRef = ref(null)
const monthlyRevenueChartRef = ref(null)
const orderTrendChartRef = ref(null)
const roomOccupancyChartRef = ref(null)
const hotelId = localStorage.getItem('hotelId')

const todayRevenue = ref(0)
const monthRevenue = ref(0)
const totalOrders = ref(0)
const roomOccupancy = ref([])

let dailyRevenueChart = null
let monthlyRevenueChart = null
let orderTrendChart = null
let roomOccupancyChart = null

const getProgressColor = (percent) => {
  if (percent >= 80) return '#67c23a'
  if (percent >= 50) return '#e6a23c'
  return '#f56c6c'
}

const loadStats = async () => {
  if (!hotelId) return
  loading.value = true
  try {
    const res = await axios.get(`http://localhost:8080/booking/stats/${hotelId}`)
    statsData.value = res.data
    
    if (statsData.value) {
      const daily = statsData.value.dailyRevenue || {}
      const monthly = statsData.value.monthlyRevenue || {}
      const orderTrend = statsData.value.orderTrend || {}
      roomOccupancy.value = statsData.value.roomOccupancy || []
      
      const today = new Date().toISOString().split('T')[0]
      todayRevenue.value = daily[today] ? daily[today].toString() * 1 : 0
      
      const currentMonth = today.substring(0, 7)
      monthRevenue.value = monthly[currentMonth] ? monthly[currentMonth].toString() * 1 : 0
      
      totalOrders.value = Object.values(orderTrend).reduce((a, b) => a + b, 0)
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  } finally {
    loading.value = false
    nextTick(() => {
      initCharts()
    })
  }
}

const initCharts = () => {
  if (!statsData.value) return
  
  if (dailyRevenueChartRef.value) {
    dailyRevenueChart = echarts.init(dailyRevenueChartRef.value)
    const daily = statsData.value.dailyRevenue || {}
    dailyRevenueChart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: Object.keys(daily) },
      yAxis: { type: 'value', name: '金额(元)' },
      series: [{
        data: Object.values(daily).map(v => v.toString() * 1),
        type: 'line',
        smooth: true,
        areaStyle: { opacity: 0.3 }
      }]
    })
  }
  
  if (monthlyRevenueChartRef.value) {
    monthlyRevenueChart = echarts.init(monthlyRevenueChartRef.value)
    const monthly = statsData.value.monthlyRevenue || {}
    monthlyRevenueChart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: Object.keys(monthly) },
      yAxis: { type: 'value', name: '金额(元)' },
      series: [{
        data: Object.values(monthly).map(v => v.toString() * 1),
        type: 'bar',
        itemStyle: { color: '#409eff' }
      }]
    })
  }
  
  if (orderTrendChartRef.value) {
    orderTrendChart = echarts.init(orderTrendChartRef.value)
    const orderTrend = statsData.value.orderTrend || {}
    orderTrendChart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: Object.keys(orderTrend) },
      yAxis: { type: 'value', name: '订单数' },
      series: [{
        data: Object.values(orderTrend),
        type: 'line',
        smooth: true,
        itemStyle: { color: '#67c23a' },
        areaStyle: { opacity: 0.3, color: '#67c23a' }
      }]
    })
  }
  
  if (roomOccupancyChartRef.value && roomOccupancy.value.length > 0) {
    roomOccupancyChart = echarts.init(roomOccupancyChartRef.value)
    roomOccupancyChart.setOption({
      tooltip: { trigger: 'item' },
      series: [{
        type: 'pie',
        radius: '60%',
        data: roomOccupancy.value.map(r => ({
          name: r.roomName,
          value: r.occupancyRate
        }))
      }]
    })
  }
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.stats-container {
  padding: 20px;
  font-size: 16px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.card-header h3, .card-header span {
  margin: 0;
  font-size: 22px;
}
.stat-card {
  text-align: center;
  padding: 10px 0;
}
.stat-label {
  font-size: 16px;
  color: #909399;
  margin-bottom: 8px;
}
.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #409eff;
}
</style>
