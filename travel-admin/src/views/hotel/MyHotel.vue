<template>
  <div class="my-hotel-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <h3>我的酒店信息维护</h3>
          <div class="header-btns">
            <el-button v-if="!isEditing" type="primary" @click="isEditing = true">编辑信息</el-button>
            <template v-else>
              <el-button @click="cancelEdit">取消</el-button>
              <el-button type="success" @click="handleSave" :loading="submitting">保存修改</el-button>
            </template>
          </div>
        </div>
      </template>

      <!-- 1. 展示视图 -->
      <el-descriptions v-if="!isEditing" :column="1" border v-loading="loading">
        <el-descriptions-item label="酒店名称">{{ hotelForm.name }}</el-descriptions-item>
        <el-descriptions-item label="酒店介绍">{{ hotelForm.description || '暂无介绍' }}</el-descriptions-item>
        <el-descriptions-item label="酒店地址">{{ hotelForm.address || '暂无地址' }}</el-descriptions-item>
        <el-descriptions-item label="酒店标签">
          <el-tag v-for="tag in selectedTags" :key="tag" class="mr-2">{{ tag }}</el-tag>
          <span v-if="selectedTags.length === 0">暂无标签</span>
        </el-descriptions-item>
        <el-descriptions-item label="星级">{{ hotelForm.starLevel || '暂无' }} 星级</el-descriptions-item>
        <el-descriptions-item label="最低价格">{{ hotelForm.minPrice || '0' }} 元起</el-descriptions-item>
        <el-descriptions-item label="酒店设施">{{ hotelForm.facilities || '暂无设施' }}</el-descriptions-item>
        <el-descriptions-item label="酒店主图">
          <div v-if="hotelForm.mainImage" class="img-preview-box">
            <el-image 
              :src="formatImg(hotelForm.mainImage)" 
              :preview-src-list="[formatImg(hotelForm.mainImage)]"
              class="hotel-img-preview"
              fit="cover"
            >
              <template #error>
                <div class="image-slot">图片加载失败</div>
              </template>
            </el-image>
          </div>
          <span v-else>暂无图片</span>
        </el-descriptions-item>
      </el-descriptions>

      <!-- 2. 编辑视图 -->
      <el-form v-else :model="hotelForm" label-width="100px" v-loading="loading">
        <el-form-item label="酒店名称">
          <el-input v-model="hotelForm.name" disabled />
          <small class="tip-text">酒店名称由平台统一管理</small>
        </el-form-item>
        
        <el-form-item label="酒店介绍">
          <el-input v-model="hotelForm.description" type="textarea" :rows="4" placeholder="请输入酒店详细介绍" />
        </el-form-item>

        <el-form-item label="酒店地址">
          <el-input v-model="hotelForm.address" placeholder="请输入详细地址" />
        </el-form-item>

        <el-form-item label="酒店标签">
          <div class="tag-selector-box">
            <!-- 已选标签 -->
            <div class="selected-tags-area">
              <el-tag 
                v-for="tag in selectedTags" 
                :key="tag" 
                closable 
                @close="removeTag(tag)"
                class="tag-item"
              >
                {{ tag }}
              </el-tag>
              <span v-if="selectedTags.length === 0" class="placeholder-text">请在下方选择标签...</span>
            </div>
            
            <!-- 待选标签池 -->
            <div class="tag-pool">
              <small class="tip-text">推荐标签 (点击添加)：</small>
              <div class="pool-items">
                <el-tag 
                  v-for="tag in availableTags" 
                  :key="tag.id" 
                  :type="selectedTags.includes(tag.name) ? 'info' : ''"
                  :class="['pool-item', { 'is-disabled': selectedTags.includes(tag.name) }]"
                  @click="addTag(tag.name)"
                  effect="plain"
                >
                  {{ tag.name }}
                </el-tag>
              </div>
            </div>
          </div>
        </el-form-item>

        <el-form-item label="星级">
          <el-input-number v-model="hotelForm.starLevel" :min="1" :max="5" />
        </el-form-item>

        <el-form-item label="最低价格">
          <el-input-number v-model="hotelForm.minPrice" :precision="2" :step="10" />
        </el-form-item>

        <el-form-item label="酒店设施">
          <div class="tag-selector-box">
            <!-- 已选设施 -->
            <div class="selected-tags-area">
              <el-tag 
                v-for="item in selectedFacilities" 
                :key="item" 
                closable 
                @close="removeFacility(item)"
                class="tag-item"
                type="success"
              >
                {{ item }}
              </el-tag>
              <span v-if="selectedFacilities.length === 0" class="placeholder-text">请在下方选择酒店设施...</span>
            </div>
            
            <!-- 待选设施池 -->
            <div class="tag-pool">
              <small class="tip-text">推荐设施 (点击添加)：</small>
              <div class="pool-items">
                <el-tag 
                  v-for="item in availableFacilities" 
                  :key="item.id" 
                  :type="selectedFacilities.includes(item.name) ? 'info' : 'success'"
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

        <el-form-item label="酒店主图">
          <div class="image-uploader-wrapper">
            <!-- 如果有图片，显示预览图和删除叉号 -->
            <div v-if="hotelForm.mainImage" class="single-image-preview">
              <img :src="formatImg(hotelForm.mainImage)" class="uploaded-img" />
              <el-icon class="delete-icon" @click="handleRemoveImage"><CircleCloseFilled /></el-icon>
            </div>
            
            <!-- 如果没有图片，显示上传按钮 -->
            <el-upload
              v-else
              class="hotel-uploader"
              action="http://localhost:8080/upload/image"
              name="file"
              :show-file-list="false"
              :on-success="handleUploadSuccess"
              :before-upload="beforeUpload"
            >
              <el-icon class="uploader-icon"><Plus /></el-icon>
            </el-upload>
          </div>
          <div class="image-path-input">
            <el-input v-model="hotelForm.mainImage" placeholder="图片路径" size="small" style="margin-top: 10px;" />
            <small class="tip-text">支持上传或手动输入路径（如 /uploads/xxx.jpg）</small>
          </div>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { Plus, CircleCloseFilled } from '@element-plus/icons-vue'

const loading = ref(false)
const submitting = ref(false)
const isEditing = ref(false)
const hotelId = ref(localStorage.getItem('hotelId'))
const availableTags = ref([]) // 所有可选标签
const availableFacilities = ref([]) // 所有可选酒店设施

const hotelForm = ref({
  id: null,
  name: '',
  description: '',
  address: '',
  mainImage: '',
  minPrice: 0,
  starLevel: 1,
  facilities: '',
  tags: ''
})

// 计算属性：将酒店标签逗号分隔的字符串转换为数组
const selectedTags = computed(() => {
  if (!hotelForm.value.tags) return []
  return hotelForm.value.tags.split(',').filter(t => t.trim() !== '')
})

// 计算属性：将酒店设施逗号分隔的字符串转换为数组
const selectedFacilities = computed(() => {
  if (!hotelForm.value.facilities) return []
  return hotelForm.value.facilities.split(',').filter(f => f.trim() !== '')
})

// 添加标签
const addTag = (tagName) => {
  const currentTags = [...selectedTags.value]
  if (currentTags.includes(tagName)) return
  currentTags.push(tagName)
  hotelForm.value.tags = currentTags.join(',')
}

// 移除标签
const removeTag = (tagName) => {
  const currentTags = [...selectedTags.value]
  const index = currentTags.indexOf(tagName)
  if (index > -1) {
    currentTags.splice(index, 1)
    hotelForm.value.tags = currentTags.join(',')
  }
}

// 添加设施
const addFacility = (name) => {
  const current = [...selectedFacilities.value]
  if (current.includes(name)) return
  current.push(name)
  hotelForm.value.facilities = current.join(',')
}

// 移除设施
const removeFacility = (name) => {
  const current = [...selectedFacilities.value]
  const index = current.indexOf(name)
  if (index > -1) {
    current.splice(index, 1)
    hotelForm.value.facilities = current.join(',')
  }
}

// 获取可选标签库
const getAvailableTags = async () => {
  try {
    const res = await axios.get('http://localhost:8080/system-config/tags')
    availableTags.value = res.data
  } catch (error) {
    console.error('获取标签库失败:', error)
  }
}

// 获取可选酒店设施库 (type=1)
const getAvailableFacilities = async () => {
  try {
    const res = await axios.get('http://localhost:8080/system-config/facilities?type=1')
    availableFacilities.value = res.data
  } catch (error) {
    console.error('获取设施库失败:', error)
  }
}

// 备份数据，用于取消编辑
let backupData = null

const getHotelDetail = async () => {
  if (!hotelId.value) {
    ElMessage.error('未获取到酒店ID，请重新登录')
    return
  }
  loading.value = true
  try {
    const res = await axios.get(`http://localhost:8080/hotel/${hotelId.value}`)
    console.log('获取到的酒店数据:', res.data)
    hotelForm.value = res.data
    backupData = JSON.parse(JSON.stringify(res.data))
  } catch (error) {
    console.error('获取信息失败:', error)
    ElMessage.error('获取信息失败')
  } finally {
    loading.value = false
  }
}

const cancelEdit = () => {
  hotelForm.value = JSON.parse(JSON.stringify(backupData))
  isEditing.value = false
}

const handleUploadSuccess = (res) => {
  console.log('上传成功返回路径:', res)
  // res 是后端返回的字符串 "/uploads/xxx.jpg"
  hotelForm.value.mainImage = res 
  ElMessage.success('上传成功')
}

const handleRemoveImage = () => {
  hotelForm.value.mainImage = ''
}

const handleSave = async () => {
  submitting.value = true
  console.log('准备保存的数据:', hotelForm.value)
  try {
    const res = await axios.post('http://localhost:8080/hotel/update', hotelForm.value)
    if (res.data) {
      ElMessage.success('保存成功！')
      isEditing.value = false
      await getHotelDetail() // 重新拉取，同步 backupData
    } else {
      ElMessage.error('保存失败，后端返回 false')
    }
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败')
  } finally {
    submitting.value = false
  }
}

const beforeUpload = (file) => {
  const isImg = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isImg) ElMessage.error('只能上传图片!')
  if (!isLt2M) ElMessage.error('图片大小不能超过 2MB!')
  return isImg && isLt2M
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
  getHotelDetail()
  getAvailableTags()
  getAvailableFacilities()
})
</script>

<style scoped>
.my-hotel-container {
  padding: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.img-preview-box {
  margin-top: 10px;
}
.hotel-img-preview {
  width: 300px;
  height: 180px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}
.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  color: #909399;
}
.tip-text {
  color: #999;
  display: block;
  margin-top: 5px;
}
.mr-2 {
  margin-right: 8px;
}

/* 标签选择器样式 */
.tag-selector-box {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 10px;
  width: 100%;
}
.selected-tags-area {
  min-height: 40px;
  border-bottom: 1px dashed #eee;
  padding-bottom: 10px;
  margin-bottom: 10px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.tag-item {
  cursor: default;
}
.placeholder-text {
  color: #c0c4cc;
  font-size: 13px;
}
.tag-pool {
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

/* 上传组件样式 */
.image-uploader-wrapper {
  display: inline-block;
}
.single-image-preview {
  position: relative;
  width: 178px;
  height: 178px;
  border: 1px solid #d9d9d9;
  border-radius: 6px;
}
.uploaded-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 6px;
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
.hotel-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  width: 178px;
  height: 178px;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
}
.hotel-uploader:hover {
  border-color: #409eff;
}
.uploader-icon {
  font-size: 28px;
  color: #8c939d;
}
</style>
