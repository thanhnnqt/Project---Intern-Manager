<template>
  <div class="dashboard-content" v-if="!loading">
    <!-- Statistic Cards -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon purple">👥</div>
        <div class="stat-details">
          <h3>Tổng Thực Tập Sinh</h3>
          <p class="stat-count">{{ stats.totalInterns }}</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon blue">👨‍🏫</div>
        <div class="stat-details">
          <h3>Tổng số Người hướng dẫn</h3>
          <p class="stat-count">{{ stats.totalMentors }}</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon orange">📋</div>
        <div class="stat-details">
          <h3>Tổng Công Việc</h3>
          <p class="stat-count">{{ stats.totalTasks }}</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon green">✅</div>
        <div class="stat-details">
          <h3>Công Việc Hoàn Thành</h3>
          <p class="stat-count">{{ stats.completedTasks }}</p>
        </div>
      </div>
    </div>

    <div class="data-grid">
      <!-- Recent Tasks -->
      <section class="data-section">
        <div class="section-header">
          <h3>Công việc gần đây</h3>
          <router-link to="/tasks" class="view-all">Xem tất cả</router-link>
        </div>
        <div class="table-container">
          <table class="data-table">
            <thead>
              <tr>
                <th>Tiêu đề</th>
                <th>Thực tập sinh</th>
                <th>Trạng thái</th>
                <th>Ưu tiên</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="task in stats.recentTasks" :key="task.id">
                <td>{{ task.title }}</td>
                <td>{{ task.internName }}</td>
                <td>
                  <span :class="['status-badge', task.status.toLowerCase()]">
                    {{ formatTaskStatus(task.status) }}
                  </span>
                </td>
                <td>
                  <span :class="['priority-badge', task.priority?.toLowerCase()]">
                    {{ formatPriority(task.priority) }}
                  </span>
                </td>
              </tr>
              <tr v-if="stats.recentTasks.length === 0">
                <td colspan="4" class="empty-state">Không có dữ liệu</td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>

      <!-- Recent Interns -->
      <section class="data-section">
        <div class="section-header">
          <h3>Thực tập sinh mới</h3>
          <router-link to="/interns" class="view-all">Xem tất cả</router-link>
        </div>
        <div class="table-container">
          <table class="data-table">
            <thead>
              <tr>
                <th>Họ tên</th>
                <th>Trường học</th>
                <th>Ngành học</th>
                <th>Trạng thái</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="intern in stats.recentInterns" :key="intern.id">
                <td class="user-cell">
                  <img :src="intern.avatar || 'https://ui-avatars.com/api/?name=' + intern.name" class="table-avatar" />
                  {{ intern.name }}
                </td>
                <td>{{ intern.university }}</td>
                <td>{{ intern.major }}</td>
                <td>
                    <span :class="['status-badge', intern.status.toLowerCase()]">
                        {{ formatInternStatus(intern.status) }}
                    </span>
                </td>
              </tr>
              <tr v-if="stats.recentInterns.length === 0">
                <td colspan="4" class="empty-state">Không có dữ liệu</td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>
    </div>
  </div>
  
  <div class="loading-state" v-else>
     <div class="spinner"></div>
     <p>Đang tải dữ liệu...</p>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { dashboardService } from '../services/dashboardService';

const loading = ref(true);

const stats = ref({
    totalInterns: 0,
    totalMentors: 0,
    totalTasks: 0,
    completedTasks: 0,
    recentInterns: [],
    recentTasks: []
});

onMounted(async () => {
    try {
        const data = await dashboardService.getDashboardData();
        stats.value = data;
    } catch (error) {
        console.error("Failed to fetch dashboard data", error);
    } finally {
        loading.value = false;
    }
});

const formatTaskStatus = (status) => {
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

const formatInternStatus = (status) => {
    const map = {
        'PENDING': 'Chờ duyệt',
        'ACTIVE': 'Đang thực tập',
        'COMPLETED': 'Đã hoàn thành'
    };
    return map[status] || status;
};
</script>

<style scoped>
/* Stats */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  background: white;
  padding: 24px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.stat-icon.purple { background: #f4f7fe; color: #4318ff; }
.stat-icon.blue { background: #e7faf3; color: #05cd99; }
.stat-icon.orange { background: #fff5e9; color: #ffb547; }
.stat-icon.green { background: #e9edfe; color: #3965ff; }

.stat-details h3 {
  font-size: 14px;
  color: #a3aed0;
  margin: 0;
  font-weight: 500;
}

.stat-count {
  font-size: 24px;
  font-weight: 700;
  margin: 4px 0 0;
  color: #1b2559;
}

/* Data Sections */
.data-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
}

@media (max-width: 1200px) {
  .data-grid {
    grid-template-columns: 1fr;
  }
}

.data-section {
  background: white;
  padding: 24px;
  border-radius: 20px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h3 {
  font-size: 20px;
  font-weight: 700;
  margin: 0;
  color: #1b2559;
}

.view-all {
  color: #4318ff;
  text-decoration: none;
  font-weight: 700;
  font-size: 14px;
}

.table-container {
  overflow-x: auto;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th {
  text-align: center;
  color: #a3aed0;
  font-size: 11px;
  font-weight: 600;
  padding: 8px 4px;
  text-transform: uppercase;
  border-bottom: 1px solid #f4f7fe;
}

.data-table td {
  padding: 8px 4px;
  font-size: 12px;
  font-weight: 600;
  color: #1b2559;
  text-align: center;
  white-space: nowrap;
}

.user-cell {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  min-width: 120px;
}

.table-avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  flex-shrink: 0;
}

.status-badge {
  padding: 4px 12px;
  border-radius: 100px;
  font-size: 12px;
  font-weight: 700;
}

.status-badge.in_progress, .status-badge.active { background: #e7faf3; color: #05cd99; }
.status-badge.pending { background: #fff5e9; color: #ffb547; }
.status-badge.completed { background: #e9edfe; color: #3965ff; }

.priority-badge {
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 11px;
}

.empty-state {
  text-align: center;
  padding: 40px !important;
  color: #a3aed0;
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 300px;
  color: #a3aed0;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #4318ff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>
