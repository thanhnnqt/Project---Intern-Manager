<template>
  <div class="intern-detail-page" v-if="!loading">
    <!-- Header Section -->
    <div class="header-banner">
      <div class="header-overlay"></div>
      <div class="header-content">
        <div class="profile-main">
          <img :src="intern.avatar || 'https://ui-avatars.com/api/?name=' + intern.name" class="main-avatar" />
          <div class="main-info">
            <h1>{{ intern.name }}</h1>
            <p class="email">{{ intern.email }}</p>
            <span :class="['status-badge', intern.status?.toLowerCase()]">{{ intern.status }}</span>
          </div>
        </div>
        <div class="header-actions">
          <button @click="router.back()" class="back-btn">
            <span class="icon">⬅️</span> Quay lại
          </button>
          <router-link :to="`/interns/${intern.id}/edit`" class="edit-btn">
            <span class="icon">✏️</span> Chỉnh sửa
          </router-link>
        </div>
      </div>
    </div>

    <div class="detail-grid">
      <!-- Left Column: Profile & Info -->
      <div class="column">
        <section class="card profile-info-card">
          <div class="card-header">
            <h3>Thông tin cá nhân</h3>
          </div>
          <div class="card-body">
            <div class="info-item">
              <span class="label">Số điện thoại</span>
              <span class="value">{{ intern.phone || 'Chưa cập nhật' }}</span>
            </div>
            <div class="info-item">
              <span class="label">Trường đại học</span>
              <span class="value">{{ intern.university }}</span>
            </div>
            <div class="info-item">
              <span class="label">Chuyên ngành</span>
              <span class="value">{{ intern.major }}</span>
            </div>
            <div class="divider"></div>
            <div class="info-item">
              <span class="label">Người hướng dẫn (Mentor)</span>
              <div class="mentor-info" v-if="intern.mentorName">
                <span class="value mentor-name">👤 {{ intern.mentorName }}</span>
              </div>
              <span class="value no-data" v-else>Chưa phân công</span>
            </div>
          </div>
        </section>
      </div>

      <!-- Middle Column: Tasks -->
      <div class="column span-2">
        <section class="card tasks-card">
          <div class="card-header">
            <h3>Danh sách công việc</h3>
            <span class="badge">{{ tasks.length }}</span>
          </div>
          <div class="card-body">
            <div class="task-list" v-if="tasks.length > 0">
              <div v-for="task in tasks" :key="task.id" class="task-item">
                <div class="task-icon">📋</div>
                <div class="task-details">
                  <h4>{{ task.title }}</h4>
                  <p class="task-desc">{{ task.description }}</p>
                  <div class="task-meta">
                    <span :class="['priority', task.priority?.toLowerCase()]">{{ task.priority }}</span>
                    <span class="deadline" v-if="task.deadline">📅 {{ formatDate(task.deadline) }}</span>
                  </div>
                </div>
                <div class="task-status">
                  <span :class="['status-dot', task.status?.toLowerCase()]"></span>
                  {{ task.status }}
                </div>
              </div>
            </div>
            <div class="empty-state" v-else>
              <div class="empty-icon">📂</div>
              <p>Chưa có công việc nào được giao.</p>
            </div>
          </div>
        </section>
      </div>

      <!-- Right Column: Files -->
      <div class="column">
        <section class="card files-card">
          <div class="card-header">
            <h3>Tài liệu đính kèm</h3>
            <span class="badge">{{ files.length }}</span>
          </div>
          <div class="card-body">
            <div class="file-list" v-if="files.length > 0">
              <div v-for="file in files" :key="file.id" class="file-item">
                <div class="file-icon">📄</div>
                <div class="file-info">
                  <span class="file-name">{{ file.fileName }}</span>
                  <span class="file-type">{{ file.fileType }}</span>
                </div>
                <button class="download-btn" @click="downloadFile(file)" title="Tải xuống">⬇️</button>
              </div>
            </div>
            <div class="empty-state" v-else>
              <div class="empty-icon">📁</div>
              <p>Chưa có tài liệu nào.</p>
            </div>
          </div>
        </section>
      </div>
    </div>
  </div>

  <!-- Loading State -->
  <div class="loading-container" v-else>
    <div class="spinner"></div>
    <p>Đang tải thông tin đời tư...</p>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { internService } from '../../services/internService';
import { taskService } from '../../services/taskService';
import { fileService } from '../../services/fileService';

const route = useRoute();
const router = useRouter();
const internId = route.params.id;

const intern = ref({});
const tasks = ref([]);
const files = ref([]);
const loading = ref(true);

const fetchAllData = async () => {
  try {
    loading.value = true;
    const [internData, tasksData, filesData] = await Promise.all([
      internService.getInternById(internId),
      taskService.getTasksByInternId(internId),
      fileService.getFilesByInternId(internId)
    ]);
    intern.value = internData;
    tasks.value = tasksData;
    files.value = filesData;
  } catch (error) {
    console.error("Lỗi khi tải dữ liệu thực tập sinh:", error);
  } finally {
    loading.value = false;
  }
};

const formatDate = (dateStr) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return date.toLocaleDateString('vi-VN');
};

const downloadFile = (file) => {
  // Logic for file download would go here - for now just alert
  alert(`Bắt đầu tải xuống: ${file.fileName}`);
};

onMounted(() => {
  fetchAllData();
});
</script>

<style scoped>
.intern-detail-page {
  padding: 0;
}

/* Header Banner */
.header-banner {
  height: 200px;
  background: linear-gradient(135deg, #4318ff 0%, #3965ff 100%);
  position: relative;
  border-radius: 0 0 30px 30px;
  margin-bottom: 80px;
}

.header-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: radial-gradient(circle at 2px 2px, rgba(255, 255, 255, 0.1) 1px, transparent 0);
  background-size: 24px 24px;
}

.header-content {
  position: absolute;
  bottom: -60px;
  left: 40px;
  right: 40px;
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
}

.profile-main {
  display: flex;
  align-items: flex-end;
  gap: 24px;
}

.main-avatar {
  width: 140px;
  height: 140px;
  border-radius: 30px;
  border: 6px solid white;
  background: white;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
  object-fit: cover;
}

.main-info h1 {
  font-size: 32px;
  font-weight: 800;
  margin: 0;
  color: #1b2559;
}

.email {
  color: #707eae;
  margin: 4px 0 12px;
  font-weight: 500;
}

.status-badge {
  padding: 6px 16px;
  border-radius: 100px;
  font-size: 13px;
  font-weight: 800;
  text-transform: capitalize;
  display: inline-block;
}

.status-badge.active, .status-badge.đang_thực_tập { background: #e7faf3; color: #05cd99; }
.status-badge.pending, .status-badge.chờ_duyệt { background: #fff5e9; color: #ffb547; }
.status-badge.completed { background: #e9edfe; color: #3965ff; }

.back-btn, .edit-btn {
  background: white;
  padding: 12px 24px;
  border-radius: 12px;
  color: #1b2559;
  font-weight: 700;
  border: none;
  cursor: pointer;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
  transition: all 0.2s;
}

.edit-btn {
  color: #4318ff;
  text-decoration: none;
}

.back-btn:hover, .edit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(0, 0, 0, 0.1);
}

/* Detail Grid */
.detail-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
  padding: 0 40px 40px;
}

.column {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.span-2 {
  grid-column: span 2;
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
  padding-bottom: 15px;
}

.card-header h3 {
  font-size: 18px;
  font-weight: 700;
  margin: 0;
  color: #1b2559;
}

.badge {
  background: #f4f7fe;
  color: #4318ff;
  padding: 4px 10px;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 700;
}

/* Profile Info */
.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
  margin-bottom: 16px;
}

.info-item .label {
  color: #a3aed0;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
}

.info-item .value {
  color: #1b2559;
  font-weight: 700;
}

.divider {
  height: 1px;
  background: #f4f7fe;
  margin: 16px 0;
}

.mentor-name {
  color: #4318ff;
}

.no-data {
  font-style: italic;
  font-weight: 500 !important;
  color: #a3aed0 !important;
}

/* Tasks */
.task-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.task-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #f4f7fe;
  border-radius: 16px;
  transition: transform 0.2s;
}

.task-item:hover {
  transform: translateX(10px);
}

.task-icon {
  font-size: 24px;
}

.task-details {
  flex: 1;
}

.task-details h4 {
  margin: 0;
  font-size: 16px;
  font-weight: 700;
  color: #1b2559;
}

.task-desc {
  font-size: 13px;
  color: #707eae;
  margin: 4px 0 8px;
}

.task-meta {
  display: flex;
  gap: 12px;
  font-size: 11px;
}

.priority {
  padding: 2px 8px;
  border-radius: 4px;
  font-weight: 700;
  text-transform: uppercase;
}

.priority.high { background: #fff5f5; color: #ff5b5b; }
.priority.medium { background: #fff5e9; color: #ffb547; }
.priority.low { background: #e7faf3; color: #05cd99; }

.task-status {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 700;
  color: #1b2559;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.status-dot.completed { background: #05cd99; }
.status-dot.in_progress { background: #3965ff; }
.status-dot.pending { background: #ffb547; }

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
  border: 1px solid #f4f7fe;
  border-radius: 12px;
}

.file-icon {
  font-size: 20px;
}

.file-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.file-name {
  font-size: 14px;
  font-weight: 700;
  color: #1b2559;
  word-break: break-all;
}

.file-type {
  font-size: 11px;
  color: #a3aed0;
}

.download-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 8px;
  border-radius: 8px;
  transition: background 0.2s;
}

.download-btn:hover {
  background: #f4f7fe;
}

/* States */
.empty-state {
  text-align: center;
  padding: 40px 0;
}

.empty-icon {
  font-size: 40px;
  margin-bottom: 12px;
  opacity: 0.5;
}

.empty-state p {
  color: #a3aed0;
  font-weight: 500;
}

.loading-container {
  height: 400px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #707eae;
}

.spinner {
  width: 50px;
  height: 50px;
  border: 4px solid #f4f7fe;
  border-top-color: #4318ff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 20px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

@media (max-width: 1400px) {
  .detail-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .detail-grid {
    grid-template-columns: 1fr;
  }
  .span-2 {
    grid-column: span 1;
  }
  .header-content {
    flex-direction: column;
    align-items: center;
    bottom: -100px;
    gap: 20px;
  }
  .header-banner {
    margin-bottom: 120px;
  }
  .profile-main {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
}
</style>
