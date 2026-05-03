<template>
  <div class="merchants-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <h3>商户入驻管理</h3>
          <el-button type="primary" @click="openAddDialog">新增商户账号</el-button>
        </div>
      </template>

      <!-- 商户列表 -->
      <el-table :data="merchantList" border style="width: 100%" v-loading="loading">
        <el-table-column prop="username" label="登录账号" />
        <el-table-column prop="nickname" label="商户名称" />
        <el-table-column prop="hotelId" label="绑定酒店ID" width="120" />
        <el-table-column prop="createTime" label="入驻时间">
          <template #default="scope">
            {{ formatTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button type="primary" size="small" @click="openEditDialog(scope.row)">编辑</el-button>
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
          @size-change="getMerchantList"
          @current-change="getMerchantList"
        />
      </div>

      <!-- 新增/编辑商户弹窗 -->
      <el-dialog v-model="showDialog" :title="isEdit ? '商户入驻 - 编辑账号' : '商户入驻 - 创建账号'" width="500px">
        <el-form :model="merchantForm" label-width="100px" :rules="rules" ref="formRef">
          <el-form-item label="登录账号" prop="username">
            <el-input v-model="merchantForm.username" placeholder="用于后台登录" :disabled="isEdit" />
          </el-form-item>
          <el-form-item label="登录密码" prop="password" v-if="!isEdit">
            <el-input v-model="merchantForm.password" type="password" show-password placeholder="请输入密码" />
          </el-form-item>
          <el-form-item label="商户昵称" prop="nickname">
            <el-input v-model="merchantForm.nickname" placeholder="如：XX酒店管理员" />
          </el-form-item>
          <el-form-item label="所属酒店" prop="hotelId">
            <el-select v-model="merchantForm.hotelId" placeholder="请选择要绑定的酒店" style="width: 100%">
              <el-option
                v-for="item in hotelList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="showDialog = false">取消</el-button>
          <el-button type="primary" @click="submitForm" :loading="submitting">{{ isEdit ? '保存' : '确定创建' }}</el-button>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const showDialog = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const hotelList = ref([])
const merchantList = ref([])
const formRef = ref(null)
const currentId = ref(null)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const merchantForm = ref({
  username: '',
  password: '',
  nickname: '',
  hotelId: null
})

const rules = {
  username: [{ required: true, message: '请输入登录账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入登录密码', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入商户名称', trigger: 'blur' }],
  hotelId: [{ required: true, message: '请选择所属酒店', trigger: 'change' }]
}

const editRules = {
  username: [{ required: true, message: '请输入登录账号', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入商户名称', trigger: 'blur' }],
  hotelId: [{ required: true, message: '请选择所属酒店', trigger: 'change' }]
}

// 获取所有酒店列表 (用于下拉框)
const getHotelList = async () => {
  try {
    const res = await axios.get('http://localhost:8080/hotel/list')
    hotelList.value = res.data
  } catch (error) {
    console.error('获取酒店列表失败:', error)
  }
}

// 获取商户列表 (role=2)
const getMerchantList = async () => {
  loading.value = true
  try {
    const res = await axios.get('http://localhost:8080/admin/list-merchants', {
      params: {
        page: currentPage.value,
        pageSize: pageSize.value
      }
    })
    merchantList.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('获取商户列表失败:', error)
  } finally {
    loading.value = false
  }
}

const openAddDialog = () => {
  isEdit.value = false
  currentId.value = null
  merchantForm.value = { username: '', password: '', nickname: '', hotelId: null }
  showDialog.value = true
}

const openEditDialog = (row) => {
  isEdit.value = true
  currentId.value = row.id
  merchantForm.value = {
    username: row.username,
    password: '',
    nickname: row.nickname,
    hotelId: row.hotelId
  }
  showDialog.value = true
}

const submitForm = async () => {
  if (!formRef.value) return
  const rulesToUse = isEdit.value ? editRules : rules
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        if (isEdit.value) {
          const res = await axios.post('http://localhost:8080/admin/update-merchant', {
            id: currentId.value,
            nickname: merchantForm.value.nickname,
            hotelId: merchantForm.value.hotelId
          })
          if (res.data === 'success' || res.data.code === 200) {
            ElMessage.success('商户账号更新成功！')
            showDialog.value = false
            getMerchantList()
          } else {
            ElMessage.error(res.data.msg || '更新失败')
          }
        } else {
          const res = await axios.post('http://localhost:8080/admin/add-merchant', merchantForm.value)
          if (res.data === 'success' || res.data.code === 200) {
            ElMessage.success('商户账号创建成功！')
            showDialog.value = false
            getMerchantList()
          } else {
            ElMessage.error(res.data.msg || '创建失败')
          }
        }
      } catch (error) {
        ElMessage.error('服务器连接失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定删除商户账号 ${row.username} 吗?`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await axios.delete(`http://localhost:8080/admin/${row.id}`)
      ElMessage.success('删除成功')
      getMerchantList()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

const formatTime = (time) => {
  if (!time) return '-'
  const date = new Date(time)
  return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`
}

onMounted(() => {
  getHotelList()
  getMerchantList()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.merchants-container {
  padding: 20px;
  font-size: 15px;
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
