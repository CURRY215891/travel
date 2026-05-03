<template>
  <div class="feedback-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <h3>客情反馈查看</h3>
        </div>
      </template>

      <el-empty v-if="loading" description="加载中..." />

      <div v-else-if="comments.length === 0" class="empty-state">
        <el-empty description="暂无评论" />
      </div>

      <div v-else class="comments-list">
        <div v-for="comment in comments" :key="comment.id" class="comment-item">
          <div class="comment-header">
            <el-avatar :size="48" :src="formatImg(comment.avatar)">{{ comment.nickname?.charAt(0) }}</el-avatar>
            <div class="user-info">
              <div class="username">{{ comment.nickname || '匿名用户' }}</div>
              <div class="room-name">{{ comment.attrName }}</div>
            </div>
            <div class="comment-time">{{ formatDate(comment.createTime) }}</div>
          </div>

          <div class="comment-ratings">
            <div class="rating-item">
              <span class="rating-label">整体评分</span>
              <el-rate v-model="comment.star" disabled show-score />
            </div>
            <div v-if="comment.hygieneScore != null" class="rating-item">
              <span class="rating-label">房间卫生</span>
              <el-rate v-model="comment.hygieneScore" disabled show-score />
            </div>
            <div v-if="comment.environmentScore != null" class="rating-item">
              <span class="rating-label">周边环境</span>
              <el-rate v-model="comment.environmentScore" disabled show-score />
            </div>
            <div v-if="comment.serviceScore != null" class="rating-item">
              <span class="rating-label">酒店服务</span>
              <el-rate v-model="comment.serviceScore" disabled show-score />
            </div>
            <div v-if="comment.facilityScore != null" class="rating-item">
              <span class="rating-label">设备设施</span>
              <el-rate v-model="comment.facilityScore" disabled show-score />
            </div>
          </div>

          <div class="comment-content">{{ comment.content }}</div>

          <div v-if="comment.images" class="comment-images">
            <el-image
              v-for="(img, idx) in comment.images.split(',')"
              :key="idx"
              :src="formatImg(img)"
              :preview-src-list="comment.images.split(',').map(i => formatImg(i))"
              fit="cover"
              class="comment-image"
            />
          </div>
        </div>
      </div>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadComments"
          @current-change="loadComments"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const loading = ref(true)
const comments = ref([])
const hotelId = localStorage.getItem('hotelId')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const formatImg = (path) => {
  if (!path) return ''
  if (path.startsWith('http')) return path
  return 'http://localhost:8080' + path
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}

const loadComments = async () => {
  if (!hotelId) return
  loading.value = true
  try {
    const res = await axios.get(`http://localhost:8080/comment/hotel/${hotelId}`, {
      params: {
        page: currentPage.value,
        pageSize: pageSize.value
      }
    })
    comments.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('加载评论失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadComments()
})
</script>

<style scoped>
.feedback-container {
  padding: 20px;
  font-size: 16px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.card-header h3 {
  margin: 0;
  font-size: 24px;
}
.empty-state {
  padding: 60px 0;
}
.comments-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.comment-item {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 20px;
  background: #fafafa;
}
.comment-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}
.user-info {
  flex: 1;
}
.username {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}
.room-name {
  font-size: 14px;
  color: #909399;
}
.comment-time {
  font-size: 14px;
  color: #909399;
}
.comment-ratings {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 12px;
}
.rating-item {
  display: flex;
  align-items: center;
  gap: 8px;
}
.rating-label {
  font-size: 14px;
  color: #606266;
  white-space: nowrap;
}
.comment-content {
  font-size: 15px;
  color: #303133;
  line-height: 1.8;
  margin-bottom: 12px;
}
.comment-images {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}
.comment-image {
  width: 120px;
  height: 90px;
  border-radius: 6px;
  cursor: pointer;
}
.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
