<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="card-header">
          <h3>智慧旅游管理系统 - 登录</h3>
        </div>
      </template>
      <el-form :model="loginForm" label-width="60px">
        <el-form-item label="账号">
          <el-input v-model="loginForm.username" placeholder="请输入管理员账号" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="login-btn" :loading="loading" @click="handleLogin">登录</el-button>
        </el-form-item>
      </el-form>
      <div class="login-tips">
        <p>提示：超级管理员登录(admin/123456)</p>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const loginForm = ref({
  username: '',
  password: ''
})

const handleLogin = async () => {
  if (!loginForm.value.username || !loginForm.value.password) {
    return ElMessage.warning('请输入用户名和密码')
  }

  loading.value = true
  try {
    // 调用之前在后端写好的登录接口
    const res = await axios.post('http://localhost:8080/admin/login', loginForm.value)
    
    // 如果返回数据包含 id，说明登录成功
    if (res.data && res.data.id) {
      // 1. 存储登录态
      localStorage.setItem('adminInfo', JSON.stringify(res.data))
      localStorage.setItem('role', res.data.role) // 角色: 1-超管, 2-酒店管理
      localStorage.setItem('hotelId', res.data.hotelId || '') // 存储所属酒店ID
      
      ElMessage.success('登录成功，欢迎使用！')
      
      // 2. 跳转到首页
      router.push('/dashboard')
    } else {
      ElMessage.error(res.data.msg || '登录失败，请检查账号密码')
    }
  } catch (error) {
    console.error('登录异常:', error)
    ElMessage.error('服务器连接失败，请检查后端服务是否启动')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #1890ff 0%, #722ed1 100%);
}
.login-card {
  width: 400px;
}
.card-header {
  text-align: center;
}
.login-btn {
  width: 100%;
}
.login-tips {
  margin-top: 15px;
  font-size: 12px;
  color: #999;
  text-align: center;
}
</style>
