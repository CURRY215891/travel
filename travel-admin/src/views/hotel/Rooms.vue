<template>
  <div class="rooms-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <h3>房型精细化配置</h3>
          <el-button type="primary" @click="openAddDialog">新增房型</el-button>
        </div>
      </template>

      <el-table :data="roomList" border style="width: 100%" v-loading="loading">
        <el-table-column label="房型图片" width="120">
          <template #default="scope">
            <el-image 
              :src="formatImg(getFirstImg(scope.row.image))" 
              style="width: 120px; height: 90px; border-radius: 6px;"
              fit="cover"
            />
          </template>
        </el-table-column>
        <el-table-column prop="name" label="房型名称" />
        <el-table-column prop="price" label="价格 (元/晚)" width="100" sortable />
        <el-table-column prop="area" label="面积 (㎡)" width="100" />
        <el-table-column prop="bedType" label="床型" width="120" />
        <el-table-column prop="window" label="窗户" width="80" />
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <div style="display: flex; gap: 8px;">
              <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
              <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
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
          @size-change="getRoomList"
          @current-change="getRoomList"
        />
      </div>

      <!-- 房型编辑/新增弹窗 -->
      <el-dialog v-model="showDialog" :title="dialogTitle" width="700px">
        <el-form :model="roomForm" label-width="100px" :rules="rules" ref="formRef">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="房型名称" prop="name">
                <el-input v-model="roomForm.name" placeholder="如：豪华大床房" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="房型价格" prop="price">
                <el-input-number v-model="roomForm.price" :min="0" :precision="2" style="width: 100%" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="房间面积" prop="area">
                <el-input v-model="roomForm.area" placeholder="如：25-30" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="床型信息" prop="bedType">
                <el-input v-model="roomForm.bedType" placeholder="如：1张1.8米大床" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="是否有窗" prop="window">
            <el-select v-model="roomForm.window" style="width: 100%">
              <el-option label="有窗" value="有窗" />
              <el-option label="无窗" value="无窗" />
              <el-option label="部分有窗" value="部分有窗" />
            </el-select>
          </el-form-item>

          <el-form-item label="配套设施">
            <div class="facility-selector-box">
              <!-- 已选设施 -->
              <div class="selected-facilities-area">
                <el-tag 
                  v-for="item in selectedFacilities" 
                  :key="item" 
                  closable 
                  @close="removeFacility(item)"
                  class="facility-item"
                >
                  {{ item }}
                </el-tag>
                <span v-if="selectedFacilities.length === 0" class="placeholder-text">请在下方选择配套设施...</span>
              </div>
              
              <!-- 待选设施池 -->
              <div class="facility-pool">
                <small class="tip-text">推荐设施 (点击添加)：</small>
                <div class="pool-items">
                  <el-tag 
                    v-for="item in availableFacilities" 
                    :key="item.id" 
                    :type="selectedFacilities.includes(item.name) ? 'info' : ''"
                    :class="['pool-item', { 'is-disabled': selectedFacilities.includes(item.name) }]"
                    @click="addFacility(item.name)"
                    effect="plain"
                  >
                    {{ item.name }}
                  </el-tag>
                </div>
              </div>
            </div>
          </el-form-item>

          <el-form-item label="房型图片 (最多5张)">
            <div class="multi-image-uploader">
              <div v-for="(img, index) in imageList" :key="index" class="image-item">
                <img :src="formatImg(img)" class="room-img" />
                <el-icon class="delete-icon" @click="removeImage(index)"><CircleCloseFilled /></el-icon>
              </div>
              
              <el-upload
                v-if="imageList.length < 5"
                class="room-uploader"
                action="http://localhost:8080/upload/image"
                name="file"
                :show-file-list="false"
                :on-success="handleUploadSuccess"
                :before-upload="beforeUpload"
              >
                <el-icon class="uploader-icon"><Plus /></el-icon>
              </el-upload>
            </div>
            <div style="margin-top: 10px;">
              <el-input v-model="roomForm.image" placeholder="图片路径，多张用逗号分隔" size="small" />
              <small class="tip-text">上传后自动更新。支持手动编辑。</small>
            </div>
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="showDialog = false">取消</el-button>
          <el-button type="primary" @click="submitSave" :loading="submitting">确定保存</el-button>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, CircleCloseFilled } from '@element-plus/icons-vue'

const loading = ref(false)
const showDialog = ref(false)
const submitting = ref(false)
const roomList = ref([])
const hotelId = ref(localStorage.getItem('hotelId'))
const formRef = ref(null)
const imageList = ref([]) // 用于内部维护图片数组
const availableFacilities = ref([]) // 可选设施池
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 计算属性：将逗号分隔的字符串转换为数组
const selectedFacilities = computed(() => {
  if (!roomForm.value.facilities) return []
  return roomForm.value.facilities.split(',').filter(f => f.trim() !== '')
})

// 添加设施
const addFacility = (name) => {
  const current = [...selectedFacilities.value]
  if (current.includes(name)) return
  current.push(name)
  roomForm.value.facilities = current.join(',')
}

// 移除设施
const removeFacility = (name) => {
  const current = [...selectedFacilities.value]
  const index = current.indexOf(name)
  if (index > -1) {
    current.splice(index, 1)
    roomForm.value.facilities = current.join(',')
  }
}

const getAvailableFacilities = async () => {
  try {
    const res = await axios.get('http://localhost:8080/system-config/facilities?type=2')
    availableFacilities.value = res.data
  } catch (error) {
    console.error('获取设施库失败:', error)
  }
}

const dialogTitle = computed(() => roomForm.value.id ? '编辑房型' : '新增房型')

const roomForm = ref({
  id: null,
  hotelId: parseInt(hotelId.value),
  name: '',
  price: 0,
  area: '',
  bedType: '',
  window: '有窗',
  facilities: '',
  image: ''
})

const rules = {
  name: [{ required: true, message: '请输入房型名称', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }]
}

// 监听 roomForm.image 字符串，同步到 imageList 数组
watch(() => roomForm.value.image, (newVal) => {
  if (newVal) {
    imageList.value = newVal.split(',').filter(s => s.trim() !== '')
  } else {
    imageList.value = []
  }
}, { immediate: true })

const getRoomList = async () => {
  if (!hotelId.value) return
  loading.value = true
  try {
    const res = await axios.get(`http://localhost:8080/hotel-room/list`, {
      params: {
        hotelId: parseInt(hotelId.value),
        page: currentPage.value,
        pageSize: pageSize.value
      }
    })
    roomList.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('获取房型列表失败:', error)
    ElMessage.error('获取房型列表失败')
  } finally {
    loading.value = false
  }
}

const openAddDialog = () => {
  roomForm.value = {
    id: null,
    hotelId: parseInt(hotelId.value),
    name: '',
    price: 0,
    area: '',
    bedType: '',
    window: '有窗',
    facilities: '',
    image: ''
  }
  showDialog.value = true
}

const handleEdit = (row) => {
  roomForm.value = { ...row }
  showDialog.value = true
}

const beforeUpload = (file) => {
  const isImg = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isImg) ElMessage.error('只能上传图片!')
  if (!isLt2M) ElMessage.error('大小不能超过 2MB!')
  return isImg && isLt2M
}

const handleUploadSuccess = (res) => {
  imageList.value.push(res)
  roomForm.value.image = imageList.value.join(',')
  ElMessage.success('上传成功')
}

const removeImage = (index) => {
  imageList.value.splice(index, 1)
  roomForm.value.image = imageList.value.join(',')
}

const submitSave = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      const url = roomForm.value.id 
        ? 'http://localhost:8080/hotel-room/update' 
        : 'http://localhost:8080/hotel-room/add'
      
      try {
        const res = await axios.post(url, roomForm.value)
        if (res.data) {
          ElMessage.success('保存成功！')
          showDialog.value = false
          getRoomList()
        }
      } catch (error) {
        ElMessage.error('操作失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定删除房型 ${row.name} 吗?`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await axios.delete(`http://localhost:8080/hotel-room/${row.id}`)
      ElMessage.success('删除成功')
      getRoomList()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

const getFirstImg = (imageStr) => {
  if (!imageStr) return ''
  return imageStr.split(',')[0]
}

const formatImg = (url) => {
  if (!url) return ''
  // 1. 如果是 http 开头，说明是外链，直接返回
  if (url.startsWith('http')) return url
  
  const baseUrl = 'http://localhost:8080'
  // 2. 如果是 /uploads/ 开头，拼接 baseUrl
  if (url.startsWith('/uploads/')) return baseUrl + url
  // 3. 如果是 /assets/ 开头，拼接 baseUrl
  if (url.startsWith('/assets/')) return baseUrl + url
  
  // 4. 如果只有文件名 (如 ct1.jpg)，说明是老数据，拼接 /assets/
  return baseUrl + '/assets/' + url
}

onMounted(() => {
  getRoomList()
  getAvailableFacilities()
})
</script>

<style scoped>
.rooms-container {
  padding: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.tip-text {
  color: #999;
  display: block;
  margin-top: 5px;
}

/* 设施选择器样式 */
.facility-selector-box {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 10px;
  width: 100%;
}
.selected-facilities-area {
  min-height: 40px;
  border-bottom: 1px dashed #eee;
  padding-bottom: 10px;
  margin-bottom: 10px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.facility-item {
  cursor: default;
}
.placeholder-text {
  color: #c0c4cc;
  font-size: 13px;
}
.facility-pool {
  padding-top: 5px;
}
.pool-items {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 8px;
}
.pool-item {
  cursor: pointer;
  transition: all 0.2s;
}
.pool-item:hover:not(.is-disabled) {
  transform: scale(1.05);
  border-color: #409eff;
}
.is-disabled {
  cursor: not-allowed;
  opacity: 0.6;
}

/* 多图上传样式 */
.multi-image-uploader {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}
.image-item {
  position: relative;
  width: 140px;
  height: 140px;
  border: 1px solid #d9d9d9;
  border-radius: 6px;
}
.room-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 6px;
}
.delete-icon {
  position: absolute;
  top: -8px;
  right: -8px;
  font-size: 20px;
  color: #f56c6c;
  cursor: pointer;
  background: #fff;
  border-radius: 50%;
  z-index: 10;
}
.room-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  width: 140px;
  height: 140px;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
}
.room-uploader:hover {
  border-color: #409eff;
}
.uploader-icon {
  font-size: 24px;
  color: #8c939d;
}
.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
