<template>
  <div class="task-list-page">
    <div class="page-header compact">
      <div class="header-text">
        <h2>Danh sách Công việc</h2>
      </div>
      <button class="create-btn" @click="handleCreate">
        <span class="icon">➕</span> Giao việc mới
      </button>
    </div>

    <!-- Filters Card -->
    <div class="filters-card card">
      <div class="search-input">
        <span class="search-icon">🔍</span>
        <input 
          type="text" 
          v-model="filters.title" 
          placeholder="Tìm theo tiêu đề..." 
          @input="debounceSearch"
        />
      </div>
      
      <div class="filter-group">
        <select v-model="filters.status" @change="fetchTasks">
          <option value="">Tất cả trạng thái</option>
          <option value="PENDING">Chờ xử lý</option>
          <option value="IN_PROGRESS">Đang thực hiện</option>
          <option value="COMPLETED">Hoàn thành</option>
        </select>

        <select v-model="filters.internId" @change="fetchTasks">
          <option value="">Tất cả Intern</option>
          <option v-for="intern in interns" :key="intern.id" :value="intern.id">
            {{ intern.name }}
          </option>
        </select>

        <select v-model="filters.mentorId" @change="fetchTasks">
          <option value="">Tất cả Mentor</option>
          <option v-for="mentor in mentors" :key="mentor.id" :value="mentor.id">
            {{ mentor.name }}
          </option>
        </select>
      </div>
    </div>

    <!-- Task Table -->
    <div class="table-card card">
      <div class="table-container">
        <table class="data-table">
          <thead>
            <tr>
              <th>Công việc</th>
              <th>Intern / Mentor</th>
              <th>Mức độ</th>
              <th>Trạng thái</th>
              <th>Hạn chót</th>
              <th>Hành động</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="task in tasks" :key="task.id">
              <td class="task-cell">
                <span class="task-title">{{ task.title }}</span>
                <span class="task-desc" v-if="task.description">{{ truncate(task.description) }}</span>
              </td>
              <td>
                <div class="users-info">
                  <div class="user-row">
                    <span class="user-label">Intern:</span>
                    <span class="user-name">{{ task.internName || '---' }}</span>
                  </div>
                  <div class="user-row">
                    <span class="user-label">Mentor:</span>
                    <span class="user-name sub">{{ task.mentorName || '---' }}</span>
                  </div>
                </div>
              </td>
              <td>
                <span :class="['priority-badge', task.priority?.toLowerCase()]">
                  {{ formatPriority(task.priority) }}
                </span>
              </td>
              <td>
                <span :class="['status-badge', task.status?.toLowerCase()]">
                  {{ formatStatus(task.status) }}
                </span>
              </td>
              <td>
                <span class="date-text">{{ formatDate(task.deadline) }}</span>
              </td>
              <td>
                <div class="actions">
                  <button class="text-btn view" @click="viewTask(task.id)">Chi tiết</button>
                  <button class="text-btn edit" @click="handleEdit(task.id)">Sửa</button>
                  <button class="text-btn delete" @click="confirmDelete(task.id)">Xóa</button>
                </div>
              </td>
            </tr>
            <tr v-if="tasks.length === 0 && !loading">
              <td colspan="6" class="empty-state">Không tìm thấy công việc nào.</td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Pagination -->
      <Pagination 
        v-model="page" 
        :totalPages="totalPages" 
        @update:modelValue="fetchTasks"
      />
    </div>

    <!-- Loading Overlay -->
    <div v-if="loading" class="loading-overlay">
      <div class="spinner"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { taskService } from '../../services/taskService';
import { internService } from '../../services/internService';
import { mentorService } from '../../services/mentorService';
import Pagination from '../../components/common/Pagination.vue';

const router = useRouter();
const tasks = ref([]);
const interns = ref([]);
const mentors = ref([]);
const loading = ref(true);
const page = ref(0);
const pageSize = ref(5);
const totalPages = ref(0);

const filters = reactive({
  title: '',
  status: '',
  internId: '',
  mentorId: ''
});

let debounceTimer = null;

const fetchTasks = async () => {
  loading.value = true;
  try {
    const params = {
      page: page.value,
      size: pageSize.value,
      title: filters.title || undefined,
      status: filters.status || undefined,
      internId: filters.internId || undefined,
      mentorId: filters.mentorId || undefined,
      sort: 'id,desc'
    };
    const data = await taskService.getAllTasks(params);
    tasks.value = data.content;
    totalPages.value = data.totalPages;
  } catch (error) {
    console.error("Lỗi khi tải danh sách task:", error);
  } finally {
    loading.value = false;
  }
};

const fetchInitialData = async () => {
  try {
    // We need simple lists for filters
    const internData = await internService.getAllInterns({ size: 100 });
    interns.value = internData.content;
    const mentorData = await mentorService.getAllMentors({ size: 100 });
    mentors.value = mentorData.content;
  } catch (error) {
    console.error("Lỗi khi tải dữ liệu khởi tạo:", error);
  }
};

const debounceSearch = () => {
  clearTimeout(debounceTimer);
  debounceTimer = setTimeout(() => {
    page.value = 0;
    fetchTasks();
  }, 500);
};


const handleCreate = () => {
  router.push('/tasks/create');
};

const handleEdit = (id) => {
  router.push(`/tasks/${id}/edit`);
};

const viewTask = (id) => {
  router.push(`/tasks/${id}`);
};

const confirmDelete = async (id) => {
  if (confirm("Bạn có chắc chắn muốn xóa task này không?")) {
    try {
      await taskService.deleteTask(id);
      fetchTasks();
    } catch (error) {
      alert("Xóa không thành công!");
    }
  }
};

const formatDate = (dateStr) => {
  if (!dateStr) return '---';
  const date = new Date(dateStr);
  return date.toLocaleDateString('vi-VN', { 
    day: '2-digit', 
    month: '2-digit', 
    year: 'numeric' 
  });
};

const formatPriority = (priority) => {
  const map = {
    'HIGH': 'Cao',
    'MEDIUM': 'Trung bình',
    'LOW': 'Thấp'
  };
  return map[priority] || priority;
};

const formatStatus = (status) => {
  const map = {
    'PENDING': 'Chờ xử lý',
    'IN_PROGRESS': 'Đang thực hiện',
    'COMPLETED': 'Hoàn thành'
  };
  return map[status] || status;
};

const truncate = (text) => {
  if (!text) return '';
  return text.length > 50 ? text.substring(0, 47) + '...' : text;
};

onMounted(() => {
  fetchTasks();
  fetchInitialData();
});
</script>

<style scoped>
.task-list-page {
  padding: 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.page-header.compact h2 {
  font-size: 24px;
  font-weight: 800;
  color: #1b2559;
  margin: 0;
}

.create-btn {
  background: #4318ff;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 12px;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 4px 14px rgba(67, 24, 255, 0.2);
  transition: all 0.2s;
}

.card {
  background: white;
  border-radius: 16px;
  padding: 12px 20px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
}

.filters-card {
  margin-bottom: 12px;
  display: flex;
  justify-content: space-between;
  gap: 20px;
}

.search-input {
  display: flex;
  align-items: center;
  background: #f4f7fe;
  padding: 12px 16px;
  border-radius: 14px;
  flex: 1;
  max-width: 300px;
}

.search-icon {
  margin-right: 12px;
}

.search-input input {
  background: transparent;
  border: none;
  width: 100%;
  outline: none;
  color: #1b2559;
  font-weight: 600;
}

.search-input input::placeholder {
  color: #1b2559;
  opacity: 0.6;
}

.filter-group {
  display: flex;
  gap: 12px;
}

select {
  background: #f4f7fe;
  border: 1px solid #e2e8f0;
  padding: 10px 16px;
  border-radius: 12px;
  color: #1b2559;
  font-weight: 600;
  outline: none;
}

option {
  color: #1b2559;
  background: white;
}

/* Table */
.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th {
  text-align: center;
  padding: 10px 6px;
  color: #a3aed0;
  font-size: 11px;
  font-weight: 700;
  text-transform: uppercase;
  border-bottom: 1px solid #f4f7fe;
}

.data-table td {
  padding: 10px 6px;
  border-bottom: 1px solid #f4f7fe;
  color: #1b2559;
  font-weight: 600;
  text-align: center;
  white-space: nowrap;
  font-size: 12px;
}

.task-cell {
  min-width: 180px;
  display: flex;
  flex-direction: column;
  gap: 4px;
  align-items: center;
  justify-content: center;
}

.task-title {
  display: block;
  font-weight: 700;
  margin-bottom: 2px;
  font-size: 12px;
}

.task-desc {
  font-size: 10px;
  font-weight: 500;
  opacity: 0.6;
}

.users-info {
  font-size: 13px;
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 150px;
}

.user-row {
  display: flex;
  gap: 8px;
  margin-bottom: 2px;
  justify-content: center;
}

.user-label {
  opacity: 0.5;
  width: 45px;
  text-align: right;
  font-size: 11px;
}

.user-name {
  font-size: 13px;
}

.user-name.sub {
  color: #4318ff;
  font-size: 12px;
}

.priority-badge {
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 11px;
  font-weight: 800;
  text-transform: uppercase;
}

.priority-badge.high { background: #fff5f5; color: #ff5b5b; }
.priority-badge.medium { background: #fff5e9; color: #ffb547; }
.priority-badge.low { background: #e7faf3; color: #05cd99; }

.status-badge {
  padding: 4px 12px;
  border-radius: 100px;
  font-size: 11px;
  font-weight: 800;
}

.status-badge.pending { background: #f4f7fe; color: #a3aed0; }
.status-badge.in_progress { background: #e9edfe; color: #4318ff; }
.status-badge.completed { background: #e7faf3; color: #05cd99; }

.date-text {
  font-size: 13px;
}

.actions {
  display: flex;
  gap: 6px;
  justify-content: center;
}

.text-btn {
  padding: 2px 6px;
  border-radius: 4px;
  border: 1px solid #e2e8f0;
  background: white;
  color: #1b2559;
  font-size: 10px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s;
}

.text-btn:hover {
  background: #f4f7fe;
  border-color: #4318ff;
  color: #4318ff;
}

.text-btn.delete:hover {
  background: #fff5f5;
  border-color: #ff5b5b;
  color: #ff5b5b;
}


.loading-overlay {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(255, 255, 255, 0.5);
  display: flex; align-items: center; justify-content: center;
  z-index: 1000;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f4f7fe;
  border-top-color: #4318ff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin { to { transform: rotate(360deg); } }
</style>
