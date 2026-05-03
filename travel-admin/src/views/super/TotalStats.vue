<template>
  <div class="total-stats-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-label">总用户数</div>
            <div class="stat-value">{{ overview.userCount || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-label">总订单数</div>
            <div class="stat-value">{{ overview.orderCount || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-label">总交易额</div>
            <div class="stat-value">¥{{ overview.totalAmount || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-label">景点数量</div>
            <div class="stat-value">{{ overview.attractionCount || 0 }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>日交易额趋势</span>
            </div>
          </template>
          <div ref="dailyChartRef" style="height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>热门景点排行</span>
            </div>
          </template>
          <el-table :data="hotAttractions" border style="width: 100%">
            <el-table-column prop="name" label="景点名称" />
            <el-table-column prop="viewCount" label="浏览量" width="120" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>各酒店营收排行</span>
            </div>
          </template>
          <el-table :data="hotelRevenue" border style="width: 100%">
            <el-table-column prop="hotelName" label="酒店名称" />
            <el-table-column prop="revenue" label="营收" width="150">
              <template #default="scope">
                ¥{{ scope.row.revenue }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>搜索词排行</span>
            </div>
          </template>
          <el-table :data="searchHotwords" border style="width: 100%">
            <el-table-column prop="keyword" label="搜索词" />
            <el-table-column prop="searchCount" label="搜索次数" width="120" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import axios from 'axios'
import * as echarts from 'echarts'

const overview = ref({})
const hotAttractions = ref([])
const searchHotwords = ref([])
const hotelRevenue = ref([])
const dailyFinance = ref([])

const dailyChartRef = ref(null)

let dailyChart = null

const getOverview = async () => {
  try {
    const res = await axios.get('http://localhost:8080/admin/stats/overview')
    overview.value = res.data || {}
  } catch (error) {
    console.error('获取概览失败:', error)
  }
}

const getHotAttractions = async () => {
  try {
    const res = await axios.get('http://localhost:8080/admin/stats/hot-attractions?limit=10')
    hotAttractions.value = res.data || []
  } catch (error) {
    console.error('获取热门景点失败:', error)
  }
}

const getSearchHotwords = async () => {
  try {
    const res = await axios.get('http://localhost:8080/admin/stats/search-hotwords?limit=10')
    searchHotwords.value = res.data || []
  } catch (error) {
    console.error('获取搜索热词失败:', error)
  }
}

const getHotelRevenue = async () => {
  try {
    const res = await axios.get('http://localhost:8080/admin/stats/hotels/revenue')
    hotelRevenue.value = res.data || []
  } catch (error) {
    console.error('获取酒店营收失败:', error)
  }
}

const getDailyFinance = async () => {
  try {
    const res = await axios.get('http://localhost:8080/admin/stats/finance/daily')
    dailyFinance.value = res.data || []
  } catch (error) {
    console.error('获取日交易额失败:', error)
  }
}

const initCharts = async () => {
  await nextTick()
  
  if (dailyChartRef.value) {
    dailyChart = echarts.init(dailyChartRef.value)
    renderDailyChart()
  }
  
  window.addEventListener('resize', () => {
    dailyChart?.resize()
  })
}

const renderDailyChart = () => {
  const dates = dailyFinance.value.map(item => item.date)
  const amounts = dailyFinance.value.map(item => item.amount)
  
  const option = {
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: dates },
    yAxis: { type: 'value', name: '金额(元)' },
    series: [{
      data: amounts,
      type: 'line',
      smooth: true,
      areaStyle: { opacity: 0.3 }
    }]
  }
  dailyChart.setOption(option)
}

onMounted(async () => {
  await Promise.all([
    getOverview(),
    getHotAttractions(),
    getSearchHotwords(),
    getHotelRevenue(),
    getDailyFinance()
  ])
  initCharts()
})
</script>

<style scoped>
.total-stats-container {
  padding: 20px;
  font-size: 15px;
}
.stat-card {
  cursor: pointer;
  transition: all 0.3s;
}
.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}
.stat-content {
  text-align: center;
}
.stat-label {
  font-size: 16px;
  color: #666;
  margin-bottom: 10px;
}
.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #409eff;
}
.card-header {
  font-weight: bold;
  font-size: 16px;
}
</style>
