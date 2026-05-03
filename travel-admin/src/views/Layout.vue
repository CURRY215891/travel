<template>
  <el-container class="layout-container">
    <el-aside width="220px">
      <div class="aside-logo">
        <h3>旅游管理后台</h3>
      </div>
      <el-menu :default-active="activePath" router class="aside-menu">
        <el-menu-item index="/dashboard">
          <el-icon><Monitor /></el-icon>
          <span>首页控制台</span>
        </el-menu-item>

        <!-- 超级管理员功能 (role == 1) -->
        <template v-if="role == 1">
          <el-menu-item index="/merchants">
            <el-icon><Shop /></el-icon>
            <span>商户入驻管理</span>
          </el-menu-item>
          <el-menu-item index="/attractions">
            <el-icon><Compass /></el-icon>
            <span>景点资源管理</span>
          </el-menu-item>
          <el-menu-item index="/users">
            <el-icon><User /></el-icon>
            <span>用户治理中心</span>
          </el-menu-item>
          <el-menu-item index="/audit">
            <el-icon><Checked /></el-icon>
            <span>内容合规审计</span>
          </el-menu-item>
          <el-menu-item index="/total-stats">
            <el-icon><DataAnalysis /></el-icon>
            <span>全平台财务统计</span>
          </el-menu-item>
          <el-menu-item index="/admin-orders">
            <el-icon><Document /></el-icon>
            <span>全平台订单监控</span>
          </el-menu-item>
          <el-menu-item index="/system-config">
            <el-icon><Setting /></el-icon>
            <span>平台运营配置</span>
          </el-menu-item>
        </template>

        <!-- 酒店管理员功能 (role == 2) -->
        <template v-else-if="role == 2">
          <el-menu-item index="/my-hotel">
            <el-icon><OfficeBuilding /></el-icon>
            <span>酒店资产维护</span>
          </el-menu-item>
          <el-menu-item index="/rooms">
            <el-icon><House /></el-icon>
            <span>房型精细化配置</span>
          </el-menu-item>
          <el-menu-item index="/orders">
            <el-icon><Document /></el-icon>
            <span>酒店订单管理</span>
          </el-menu-item>
          <el-menu-item index="/my-stats">
            <el-icon><PieChart /></el-icon>
            <span>营收报表统计</span>
          </el-menu-item>
          <el-menu-item index="/hotel-feedback">
            <el-icon><ChatDotSquare /></el-icon>
            <span>客情反馈查看</span>
          </el-menu-item>
        </template>

        <el-menu-item @click="handleLogout">
          <el-icon><SwitchButton /></el-icon>
          <span>退出登录</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown>
            <span class="user-info">
              {{ nickname }} ({{ roleName }})
              <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <el-main>
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import {
  Monitor, Shop, Compass, User, Checked, DataAnalysis, Setting,
  OfficeBuilding, House, Document, PieChart, ChatDotSquare, SwitchButton, ArrowDown
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const role = ref(localStorage.getItem('role'))
const nickname = ref('')
const roleName = computed(() => role.value == 1 ? '超级管理员' : '酒店管理员')
const activePath = computed(() => route.path)
const currentTitle = computed(() => route.name || '管理后台')

onMounted(() => {
  const adminInfo = JSON.parse(localStorage.getItem('adminInfo') || '{}')
  nickname.value = adminInfo.nickname || '未命名'
})

const handleLogout = () => {
  ElMessageBox.confirm('确定退出系统吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    localStorage.clear()
    router.push('/login')
    ElMessage.success('已安全退出')
  }).catch(() => {})
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}
.aside-logo {
  height: 60px;
  background: #001529;
  color: white;
  display: flex;
  justify-content: center;
  align-items: center;
  border-bottom: 1px solid #000;
}
.aside-menu {
  height: calc(100vh - 60px);
  border-right: none;
}
.header {
  background: #fff;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}
.user-info {
  cursor: pointer;
  color: #409eff;
}
</style>
