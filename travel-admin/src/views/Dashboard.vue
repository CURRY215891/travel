<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card>
          <h2>欢迎登录智慧旅游管理系统</h2>
          <p>当前登录角色：{{ roleName }}</p>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header"><span>待办任务</span></div>
          </template>
          <div class="card-content">
            <p v-if="role == 1">待审核商户：3 个</p>
            <p v-if="role == 1">违规举报：5 条</p>
            <p v-if="role == 2">今日新订单：8 个</p>
            <p v-if="role == 2">待入住：12 个</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const role = ref(localStorage.getItem('role'))
const roleName = computed(() => role.value == 1 ? '超级管理员' : '酒店管理员')
</script>
