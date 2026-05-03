<template>
  <div class="attractions-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <el-radio-group v-model="activeTab" @change="handleTabChange">
            <el-radio-button label="attraction">景点管理</el-radio-button>
            <el-radio-button label="food">美食管理</el-radio-button>
          </el-radio-group>
          <el-button type="primary" @click="openAddDialog">新增{{ activeTabLabel }}</el-button>
        </div>
      </template>

      <el-table :data="tableData" border style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="主图" width="130">
          <template #default="scope">
            <el-image
              :src="formatImg(scope.row.mainImage)"
              style="width: 150px; height: 150px; border-radius: 6px;"
              fit="cover"
              :preview-src-list="[formatImg(scope.row.mainImage)]"
            />
          </template>
        </el-table-column>
        <el-table-column prop="name" label="名称" width="200" />
        <el-table-column v-if="activeTab === 'attraction'" label="类型" width="150">
          <template #default="scope">
            <el-tag>{{ getCategoryName(scope.row.categoryId) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="简介" show-overflow-tooltip />
        <el-table-column prop="address" label="地址" width="150" show-overflow-tooltip />
        <el-table-column v-if="activeTab === 'attraction'" prop="viewCount" label="浏览量" width="100" />
        <el-table-column v-if="activeTab === 'food'" prop="avgPrice" label="价格" width="100">
          <template #default="scope">
            ¥{{ scope.row.avgPrice || scope.row.price }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
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
          @size-change="getList"
          @current-change="getList"
        />
      </div>
    </el-card>

    <el-dialog v-model="showDialog" :title="isEdit ? '编辑' + activeTabLabel : '新增' + activeTabLabel" width="700px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="名称">
          <el-input v-model="form.name" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item v-if="activeTab === 'attraction'" label="类型">
          <el-select v-model="form.categoryId" placeholder="请选择类型" style="width: 100%;">
            <el-option label="自然风光" :value="3" />
            <el-option label="历史名胜" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="简介">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入简介" />
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="form.address" placeholder="请输入地址" />
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="纬度">
              <el-input v-model="form.latitude" placeholder="请输入纬度" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="经度">
              <el-input v-model="form.longitude" placeholder="请输入经度" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item v-if="activeTab === 'attraction'" label="价格">
          <el-input-number v-model="form.price" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item v-if="activeTab === 'food'" label="均价">
          <el-input-number v-model="form.avgPrice" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        
        <el-form-item v-if="activeTab === 'attraction'" label="开放时间">
          <el-input v-model="form.openTime" placeholder="例如：08:00-18:00" />
        </el-form-item>
        <el-form-item v-if="activeTab === 'food'" label="营业时间">
          <el-input v-model="form.businessHours" placeholder="例如：09:00-22:00" />
        </el-form-item>

        <el-form-item label="主图">
          <div class="image-uploader-wrapper">
            <div v-if="form.mainImage" class="single-image-preview">
              <img :src="formatImg(form.mainImage)" class="uploaded-img" />
              <el-icon class="delete-icon" @click="removeMainImage"><CircleCloseFilled /></el-icon>
            </div>
            <el-upload
              v-else
              class="single-uploader"
              action="http://localhost:8080/upload/image"
              name="file"
              :show-file-list="false"
              :on-success="handleMainImageSuccess"
              :before-upload="beforeUpload"
            >
              <el-icon class="uploader-icon"><Plus /></el-icon>
            </el-upload>
          </div>
          <div class="image-path-input">
            <el-input v-model="form.mainImage" placeholder="图片路径或URL" size="small" style="margin-top: 10px;" />
            <small class="tip-text">支持上传或手动输入路径/URL</small>
          </div>
        </el-form-item>

        <el-form-item label="图集">
          <div class="multi-uploader-wrapper">
            <div v-for="(img, index) in imageList" :key="index" class="multi-image-preview">
              <img :src="formatImg(img)" class="multi-uploaded-img" />
              <el-icon class="delete-icon" @click="removeImage(index)"><CircleCloseFilled /></el-icon>
            </div>
            <el-upload
              class="multi-uploader"
              action="http://localhost:8080/upload/image"
              name="file"
              :show-file-list="false"
              :on-success="(res) => handleImageSuccess(res)"
              :before-upload="beforeUpload"
            >
              <el-icon class="uploader-icon"><Plus /></el-icon>
            </el-upload>
          </div>
          <div class="image-path-input">
            <el-input v-model="imagesText" placeholder="多个图片用逗号分隔" size="small" style="margin-top: 10px;" />
            <small class="tip-text">支持手动输入多个图片路径/URL，用逗号分隔</small>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, CircleCloseFilled } from '@element-plus/icons-vue'

const activeTab = ref('attraction')
const tableData = ref([])
const loading = ref(false)
const showDialog = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const imageList = ref([])
const imagesText = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const form = ref({})

const activeTabLabel = computed(() => activeTab.value === 'attraction' ? '景点' : '美食')

const getList = async () => {
  loading.value = true
  try {
    const url = activeTab.value === 'attraction' 
      ? 'http://localhost:8080/attraction/admin/list'
      : 'http://localhost:8080/food/admin/list'
    const res = await axios.get(url, {
      params: {
        page: currentPage.value,
        pageSize: pageSize.value
      }
    })
    tableData.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('获取列表失败:', error)
    ElMessage.error('获取列表失败')
  } finally {
    loading.value = false
  }
}

const handleTabChange = () => {
  currentPage.value = 1
  getList()
}

const openAddDialog = () => {
  isEdit.value = false
  form.value = {}
  imageList.value = []
  imagesText.value = ''
  showDialog.value = true
}

const openEditDialog = (row) => {
  isEdit.value = true
  form.value = { ...row }
  
  if (row.images) {
    imageList.value = row.images.split(',').filter(img => img.trim())
  } else {
    imageList.value = []
  }
  imagesText.value = row.images || ''
  showDialog.value = true
}

watch(imagesText, (val) => {
  if (val) {
    imageList.value = val.split(',').filter(img => img.trim())
  } else {
    imageList.value = []
  }
})

watch(imageList, (val) => {
  imagesText.value = val.join(',')
  form.value.images = val.join(',')
}, { deep: true })

const removeMainImage = () => {
  form.value.mainImage = ''
}

const removeImage = (index) => {
  imageList.value.splice(index, 1)
}

const handleMainImageSuccess = (res) => {
  form.value.mainImage = res
  ElMessage.success('上传成功')
}

const handleImageSuccess = (res) => {
  imageList.value.push(res)
  ElMessage.success('上传成功')
}

const beforeUpload = (file) => {
  const isImg = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isImg) ElMessage.error('只能上传图片!')
  if (!isLt2M) ElMessage.error('图片大小不能超过 2MB!')
  return isImg && isLt2M
}

const handleSubmit = async () => {
  if (!form.value.name) {
    ElMessage.warning('请输入名称')
    return
  }

  submitting.value = true
  try {
    const baseUrl = activeTab.value === 'attraction' 
      ? 'http://localhost:8080/attraction'
      : 'http://localhost:8080/food'
    
    form.value.images = imageList.value.join(',')
    
    const url = isEdit.value ? baseUrl + '/update' : baseUrl + '/add'
    const res = await axios.post(url, form.value)
    
    if (res.data) {
      ElMessage.success(isEdit.value ? '更新成功' : '添加成功')
      showDialog.value = false
      getList()
    } else {
      ElMessage.error('操作失败')
    }
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('操作失败')
  } finally {
    submitting.value = false
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定删除${activeTabLabel}「${row.name}」吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const url = activeTab.value === 'attraction' 
        ? `http://localhost:8080/attraction/${row.id}`
        : `http://localhost:8080/food/${row.id}`
      await axios.delete(url)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const getCategoryName = (categoryId) => {
  const map = { 1: '历史名胜', 3: '自然风光' }
  return map[categoryId] || '未分类'
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
.attractions-container {
  padding: 20px;
  font-size: 15px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
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
.image-uploader-wrapper {
  display: inline-block;
}
.single-image-preview {
  position: relative;
  width: 220px;
  height: 220px;
  border: 1px solid #d9d9d9;
  border-radius: 6px;
}
.uploaded-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 6px;
}
.single-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  width: 220px;
  height: 220px;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
}
.single-uploader:hover {
  border-color: #409eff;
}
.multi-uploader-wrapper {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}
.multi-image-preview {
  position: relative;
  width: 140px;
  height: 140px;
  border: 1px solid #d9d9d9;
  border-radius: 6px;
}
.multi-uploaded-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 6px;
}
.multi-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  width: 140px;
  height: 140px;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
}
.multi-uploader:hover {
  border-color: #409eff;
}
.delete-icon {
  position: absolute;
  top: -10px;
  right: -10px;
  font-size: 24px;
  color: #f56c6c;
  cursor: pointer;
  background: #fff;
  border-radius: 50%;
  z-index: 10;
}
.delete-icon:hover {
  color: #ff0000;
}
.uploader-icon {
  font-size: 28px;
  color: #8c939d;
}
.tip-text {
  color: #999;
  display: block;
  margin-top: 5px;
}
</style>
