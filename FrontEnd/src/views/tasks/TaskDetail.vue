<template>
  <div class="task-detail-page">
    <div v-if="loading" class="loading-container">
      <div class="spinner"></div>
      <p>Đang tải thông tin công việc...</p>
    </div>

    <template v-else-if="task">
      <!-- Header Banner -->
      <div class="header-banner">
        <div class="header-overlay"></div>
        <div class="header-content">
          <div class="header-left">
            <button class="back-btn" @click="router.back()">
              <span>⬅️</span> Quay lại
            </button>
            <h1>{{ task.title }}</h1>
            <div class="task-meta-top">
              <span :class="['priority-badge', task.priority?.toLowerCase()]">{{ formatPriority(task.priority) }}</span>
              <span :class="['status-badge', task.status?.toLowerCase()]">{{ formatStatus(task.status) }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="detail-grid">
        <!-- Main Info Section -->
        <div class="column span-2">
          <div class="card info-card">
            <div class="card-header">
              <h3>📄 Chi tiết công việc</h3>
            </div>
            <div class="info-content">
              <div class="info-item full">
                <span class="label">Mô tả</span>
                <p class="description">{{ task.description || 'Không có mô tả.' }}</p>
              </div>
              <div class="info-row">
                <div class="info-item">
                  <span class="label">Thực tập sinh</span>
                  <span class="value">{{ task.internName || '---' }}</span>
                </div>
                <div class="info-item">
                  <span class="label">Người hướng dẫn</span>
                  <span class="value mentor">{{ task.mentorName || '---' }}</span>
                </div>
              </div>
              <div class="info-row">
                <div class="info-item">
                  <span class="label">Hạn chót</span>
                  <span class="value">{{ formatDate(task.deadline) }}</span>
                </div>
                <div class="info-item">
                  <span class="label">Ngày tạo</span>
                  <span class="value">---</span>
                </div>
              </div>
            </div>
          </div>

          <!-- Comments Section -->
          <div class="card comment-card">
            <div class="card-header">
              <h3>💬 Thảo luận ({{ comments.length }})</h3>
            </div>
            <div class="comment-input-area">
              <textarea 
                v-model="newComment" 
                placeholder="Viết phản hồi hoặc cập nhật tiến độ..."
                rows="3"
              ></textarea>
              <button class="send-btn" :disabled="!newComment.trim()" @click="handleAddComment">
                Gửi bình luận
              </button>
            </div>
            
            <div class="comment-list">
              <div v-for="comment in comments" :key="comment.id" class="comment-item">
                <img :src="comment.userAvatar || 'https://ui-avatars.com/api/?name=' + comment.userName" class="comment-avatar" />
                <div class="comment-body">
                  <div class="comment-header">
                    <span class="user-name">{{ comment.userName }}</span>
                    <span class="user-role">{{ comment.userRole }}</span>
                    <span class="comment-time">{{ formatDateTime(comment.createdAt) }}</span>
                    <button v-if="canDeleteComment(comment)" class="delete-comment" @click="handleDeleteComment(comment.id)">🗑️</button>
                  </div>
                  <p class="comment-text">{{ comment.content }}</p>
                </div>
              </div>
              <div v-if="comments.length === 0" class="empty-comments">
                Chưa có bình luận nào.
              </div>
            </div>
          </div>
        </div>

        <!-- Sidebar Section -->
        <div class="column">
          <!-- Status Update Card -->
          <div class="card action-card">
            <div class="card-header">
              <h3>⚙️ Trạng thái</h3>
            </div>
            <div class="status-options">
              <button 
                v-for="status in statusOptions" 
                :key="status.value"
                :class="['status-opt', { active: task.status === status.value }]"
                @click="updateTaskStatus(status.value)"
              >
                {{ status.label }}
              </button>
            </div>
          </div>

          <!-- Files Card -->
          <div class="card file-card">
            <div class="card-header">
              <h3>📁 Tài liệu đính kèm</h3>
              <label class="upload-btn-mini">
                <input type="file" @change="handleFileUpload" hidden />
                <span>➕</span>
              </label>
            </div>
            <div class="file-list">
              <div v-for="file in files" :key="file.id" class="file-item">
                <span class="file-icon">📄</span>
                <div class="file-info">
                  <span class="file-name">{{ file.fileName }}</span>
                  <span class="file-size">{{ file.fileType }}</span>
                </div>
                <button class="download-btn" @click="downloadFile(file)">⬇️</button>
              </div>
              <div v-if="files.length === 0" class="empty-files">
                Chưa có tài liệu đính kèm.
              </div>
            </div>
          </div>
        </div>
      </div>
    </template>

    <div v-else class="error-container">
      <span class="error-icon">❌</span>
      <p>Không tìm thấy công việc yêu cầu.</p>
      <button class="back-btn" @click="router.back()">Quay lại</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { taskService } from '../../services/taskService';
import { commentService } from '../../services/commentService';
import { useAuthStore } from '../../store/authStore';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

const task = ref(null);
const comments = ref([]);
const files = ref([]);
const loading = ref(true);
const newComment = ref('');

const statusOptions = [
  { label: 'Chờ xử lý', value: 'PENDING' },
  { label: 'Đang thực hiện', value: 'IN_PROGRESS' },
  { label: 'Hoàn thành', value: 'COMPLETED' }
];

const fetchAllData = async () => {
  const id = route.params.id;
  loading.value = true;
  try {
    const [taskData, commentData] = await Promise.all([
      taskService.getTaskById(id),
      commentService.getCommentsByTaskId(id)
    ]);
    files.value = []; // Temporary disable files
    task.value = taskData;
    comments.value = commentData;
  } catch (error) {
    console.error("Lỗi khi tải chi tiết task:", error);
  } finally {
    loading.value = false;
  }
};

const updateTaskStatus = async (newStatus) => {
  if (task.value.status === newStatus) return;
  try {
    const updatedTask = await taskService.updateTask(task.value.id, {
      ...task.value,
      status: newStatus,
      // Backend expects IDs for updates sometimes
      internId: task.value.internId, 
      mentorId: task.value.mentorId
    });
    task.value.status = newStatus;
  } catch (error) {
    alert("Cập nhật trạng thái thất bại!");
  }
};

const handleAddComment = async () => {
  if (!newComment.value.trim()) return;
  try {
    const res = await commentService.addComment({
      content: newComment.value,
      taskId: task.value.id,
      userId: authStore.user.id
    });
    comments.value.unshift(res);
    newComment.value = '';
  } catch (error) {
    alert("Gửi bình luận thất bại!");
  }
};

const canDeleteComment = (comment) => {
  return authStore.user.id === comment.userId || authStore.user.role === 'ADMIN';
};

const handleDeleteComment = async (id) => {
  if (!confirm("Xóa bình luận này?")) return;
  try {
    await commentService.deleteComment(id);
    comments.value = comments.value.filter(c => c.id !== id);
  } catch (error) {
    alert("Xóa thất bại!");
  }
};

const handleFileUpload = async (event) => {
  const file = event.target.files[0];
  if (!file) return;
  // This would typically involve a multipart upload
  alert("Tính năng upload file đang được tích hợp!");
};

const downloadFile = (file) => {
  window.open(file.filePath, '_blank');
};

const formatStatus = (status) => {
  const map = {
    'PENDING': 'Chờ xử lý',
    'IN_PROGRESS': 'Đang thực hiện',
    'COMPLETED': 'Hoàn thành'
  };
  return map[status] || status;
};

const formatPriority = (priority) => {
  const map = {
    'HIGH': 'Cao',
    'MEDIUM': 'Trung bình',
    'LOW': 'Thấp'
  };
  return map[priority] || priority;
};

const formatDate = (dateStr) => {
  if (!dateStr) return '---';
  return new Date(dateStr).toLocaleDateString('vi-VN');
};

const formatDateTime = (dateStr) => {
  if (!dateStr) return '';
  return new Date(dateStr).toLocaleString('vi-VN', {
    hour: '2-digit',
    minute: '2-digit',
    day: '2-digit',
    month: '2-digit'
  });
};

onMounted(fetchAllData);
</script>

<style scoped>
.task-detail-page {
  padding: 0;
}

/* Header Banner */
.header-banner {
  height: 180px;
  background: linear-gradient(135deg, #1b2559 0%, #111c44 100%);
  position: relative;
  border-radius: 0 0 30px 30px;
  margin-bottom: 40px;
  display: flex;
  align-items: center;
  padding: 0 40px;
}

.header-overlay {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  background-image: radial-gradient(circle at 2px 2px, rgba(255, 255, 255, 0.05) 1px, transparent 0);
  background-size: 24px 24px;
}

.header-content {
  position: relative;
  z-index: 1;
  width: 100%;
}

.header-left h1 {
  font-size: 32px;
  font-weight: 800;
  color: white;
  margin: 12px 0;
}

.back-btn {
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: white;
  padding: 8px 16px;
  border-radius: 10px;
  cursor: pointer;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.2s;
}

.back-btn:hover {
  background: rgba(255, 255, 255, 0.2);
}

.task-meta-top {
  display: flex;
  gap: 12px;
}

/* Badges */
.priority-badge {
  padding: 4px 12px;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 800;
  text-transform: uppercase;
  background: rgba(255, 255, 255, 0.1);
  color: white;
}

.priority-badge.high { color: #ff5b5b; background: rgba(255, 91, 91, 0.1); }
.priority-badge.medium { color: #ffb547; background: rgba(255, 181, 71, 0.1); }
.priority-badge.low { color: #05cd99; background: rgba(5, 205, 153, 0.1); }

.status-badge {
  padding: 4px 12px;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 800;
  background: white;
  color: #1b2559;
}

/* Grid Layout */
.detail-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 24px;
  padding: 0 40px 40px;
}

.column {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.span-2 {
  grid-column: span 1;
}

/* Cards */
.card {
  background: white;
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  border-bottom: 1px solid #f4f7fe;
  padding-bottom: 12px;
}

.card-header h3 {
  font-size: 18px;
  font-weight: 700;
  color: #1b2559;
  margin: 0;
}

/* Info Card */
.info-row {
  display: flex;
  gap: 40px;
  margin-bottom: 20px;
}

.info-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.info-item.full {
  margin-bottom: 24px;
}

.info-item .label {
  font-size: 12px;
  font-weight: 700;
  color: #a3aed0;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.info-item .value {
  color: #1b2559;
  font-weight: 700;
  font-size: 16px;
}

.info-item .value.mentor {
  color: #4318ff;
}

.description {
  color: #1b2559;
  font-weight: 500;
  line-height: 1.6;
  white-space: pre-line;
}

/* Status Update */
.status-options {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.status-opt {
  padding: 12px;
  border-radius: 12px;
  border: 2px solid #f4f7fe;
  background: white;
  color: #1b2559;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s;
  text-align: left;
}

.status-opt:hover {
  border-color: #e2e8f0;
}

.status-opt.active {
  background: #f4f7fe;
  border-color: #4318ff;
  color: #4318ff;
}

/* Comments */
.comment-input-area {
  margin-bottom: 24px;
}

textarea {
  width: 100%;
  padding: 16px;
  border-radius: 16px;
  border: 1px solid #e2e8f0;
  background: #f4f7fe;
  color: #1b2559;
  font-weight: 500;
  font-family: inherit;
  resize: none;
  outline: none;
  box-sizing: border-box;
}

textarea:focus { border-color: #4318ff; }

.send-btn {
  margin-top: 12px;
  background: #4318ff;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 10px;
  font-weight: 700;
  cursor: pointer;
}

.send-btn:disabled { opacity: 0.5; cursor: not-allowed; }

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.comment-item {
  display: flex;
  gap: 16px;
}

.comment-avatar {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  object-fit: cover;
}

.comment-body {
  flex: 1;
  background: #f4f7fe;
  padding: 16px;
  border-radius: 0 16px 16px 16px;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.user-name { font-weight: 800; color: #1b2559; font-size: 14px; }
.user-role { 
  font-size: 10px; font-weight: 800; color: white; 
  background: #a3aed0; padding: 2px 6px; border-radius: 4px; 
}
.comment-time { font-size: 12px; color: #a3aed0; margin-left: auto; }

.comment-text {
  color: #1b2559;
  font-weight: 500;
  line-height: 1.5;
  margin: 0;
}

.delete-comment {
  background: none; border: none; cursor: pointer; padding: 4px; border-radius: 6px;
}
.delete-comment:hover { background: #fee2e2; }

/* Files */
.file-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.file-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #f4f7fe;
  border-radius: 12px;
}

.file-info { flex: 1; display: flex; flex-direction: column; }
.file-name { font-size: 14px; font-weight: 700; color: #1b2559; }
.file-size { font-size: 11px; color: #a3aed0; }

.download-btn {
  background: white; border: none; padding: 6px; border-radius: 6px; cursor: pointer;
}

.upload-btn-mini {
  cursor: pointer; color: #4318ff; font-weight: 700;
}

/* Loading/Error */
.loading-container, .error-container {
  height: 400px; display: flex; flex-direction: column; align-items: center; justify-content: center;
}

.spinner {
  width: 50px; height: 50px; border: 4px solid #f4f7fe; border-top-color: #4318ff; border-radius: 50%;
  animation: spin 1s linear infinite; margin-bottom: 20px;
}

@keyframes spin { to { transform: rotate(360deg); } }

@media (max-width: 1024px) {
  .detail-grid { grid-template-columns: 1fr; }
  .header-banner { padding: 0 20px; }
}
</style>
