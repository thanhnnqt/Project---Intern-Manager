<template>
  <div class="intern-list-page">
    <div class="page-header">
      <div class="header-content">
        <h1>Danh sách Thực tập sinh</h1>
        <p>Quản lý và theo dõi thông tin các thực tập sinh trong hệ thống.</p>
      </div>
      <router-link v-if="user?.role === 'ADMIN'" to="/interns/create" class="create-btn" id="add-intern-btn">
        <span class="icon">➕</span> Thêm thực tập sinh
      </router-link>
    </div>

    <!-- Filters & Search -->
    <div class="filters-card">
      <div class="search-box">
        <span class="search-icon">🔍</span>
        <input 
          v-model="filters.name" 
          type="text" 
          placeholder="Tìm theo tên..." 
          @input="debounceSearch"
        />
      </div>
      
      <div class="filter-group">
        <select v-model="filters.university" @change="fetchInterns">
          <option value="">Tất cả trường học</option>
          <option v-for="uni in universities" :key="uni" :value="uni">{{ uni }}</option>
        </select>

        <select v-model="filters.mentorId" @change="fetchInterns">
          <option value="">Tất cả Mentor</option>
          <option v-for="mentor in mentors" :key="mentor.id" :value="mentor.id">{{ mentor.name }}</option>
        </select>

        <select v-model="filters.status" @change="fetchInterns">
          <option value="">Tất cả trạng thái</option>
          <option value="PENDING">Chờ duyệt</option>
          <option value="ACTIVE">Đang thực tập</option>
          <option value="COMPLETED">Đã hoàn thành</option>
        </select>

        <select v-model="sortBy" @change="fetchInterns">
          <option value="id">Sắp xếp: Mới nhất</option>
          <option value="fullName">Sắp xếp: Tên (A-Z)</option>
        </select>
      </div>
    </div>

    <!-- Intern Table -->
    <div class="table-card">
      <div class="table-container" v-if="!loading">
        <table class="intern-table">
          <thead>
            <tr>
              <th>Họ tên</th>
              <th>Email</th>
              <th>Số điện thoại</th>
              <th>Trường đại học</th>
              <th>Mentor</th>
              <th>Trạng thái</th>
              <th class="text-center">Hành động</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="intern in interns" :key="intern.id">
              <td class="user-cell">
                <img :src="intern.avatar || 'https://ui-avatars.com/api/?name=' + intern.name" class="table-avatar" />
                <span class="name">{{ intern.name }}</span>
              </td>
              <td>{{ intern.email }}</td>
              <td>{{ intern.phone }}</td>
              <td>{{ intern.university }}</td>
              <td>
                <span v-if="intern.mentorName" class="mentor-badge">
                   👤 {{ intern.mentorName }}
                </span>
                <span v-else class="no-mentor">Chưa phân công</span>
              </td>
              <td>
                <span :class="['status-badge', intern.status.toLowerCase()]">
                  {{ formatStatus(intern.status) }}
                </span>
              </td>
              <td class="actions-cell">
                <button class="text-btn view" @click="viewIntern(intern.id)">Chi tiết</button>
                <button class="text-btn edit" @click="editIntern(intern.id)">Sửa</button>
                <button class="text-btn delete" @click="confirmDelete(intern)">Xóa</button>
              </td>
            </tr>
            <tr v-if="interns.length === 0">
              <td colspan="7" class="empty-state">
                <div class="empty-content">
                  <span class="empty-icon">📂</span>
                  <p>Không tìm thấy thực tập sinh nào phù hợp.</p>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Loading State -->
      <div class="loading-overlay" v-else>
        <div class="spinner"></div>
        <p>Đang tải danh sách...</p>
      </div>

      <!-- Pagination -->
      <div class="pagination" v-if="totalPages > 1">
        <button 
          :disabled="page === 0" 
          @click="changePage(page - 1)" 
          class="page-btn"
        >
          Trước
        </button>
        <div class="page-numbers">
          <button 
            v-for="p in totalPages" 
            :key="p" 
            :class="['page-num', { active: page === p - 1 }]"
            @click="changePage(p - 1)"
          >
            {{ p }}
          </button>
        </div>
        <button 
          :disabled="page === totalPages - 1" 
          @click="changePage(page + 1)" 
          class="page-btn"
        >
          Sau
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { internService } from '../../services/internService';
import { mentorService } from '../../services/mentorService';
import { useAuthStore } from '../../store/authStore';
import api from '../../services/api';

const authStore = useAuthStore();
const user = authStore.user;

const router = useRouter();
const interns = ref([]);
const loading = ref(true);
const page = ref(0);
const pageSize = ref(10);
const totalPages = ref(0);
const sortBy = ref('id');

const filters = reactive({
  name: '',
  university: '',
  mentorId: '',
  status: ''
});

const universities = ref(['Đại học Bách Khoa', 'Đại học FPT', 'Đại học Công nghệ', 'Đại học Kinh tế']);
const mentors = ref([]);

let debounceTimer = null;

const fetchInterns = async () => {
  loading.value = true;
  try {
    const params = {
      page: page.value,
      size: pageSize.value,
      sortBy: sortBy.value,
      name: filters.name || undefined,
      university: filters.university || undefined,
      mentorId: filters.mentorId || undefined,
      status: filters.status || undefined
    };
    const data = await internService.getAllInterns(params);
    interns.value = data.content;
    totalPages.value = data.totalPages;
  } catch (error) {
    console.error("Lỗi khi tải danh sách thực tập sinh:", error);
  } finally {
    loading.value = false;
  }
};

const debounceSearch = () => {
  clearTimeout(debounceTimer);
  debounceTimer = setTimeout(() => {
    page.value = 0;
    fetchInterns();
  }, 500);
};

const changePage = (newPage) => {
  page.value = newPage;
  fetchInterns();
};

const fetchMentors = async () => {
  try {
    const data = await mentorService.getAllMentors();
    mentors.value = data;
  } catch (error) {
    console.error("Không thể tải danh sách Mentor:", error);
  }
};

onMounted(() => {
  const queryMentorId = router.currentRoute.value.query.mentorId;
  if (queryMentorId) {
    filters.mentorId = Number(queryMentorId);
  }
  fetchInterns();
  fetchMentors();
});

const viewIntern = (id) => router.push(`/interns/${id}`);
const editIntern = (id) => router.push(`/interns/${id}/edit`);

const confirmDelete = async (intern) => {
  if (confirm(`Bạn có chắc chắn muốn xóa thực tập sinh ${intern.name}?`)) {
    try {
      await internService.deleteIntern(intern.id);
      fetchInterns();
    } catch (error) {
      alert("Xóa không thành công. Vui lòng thử lại.");
    }
  }
};

const formatStatus = (status) => {
  const map = {
    'PENDING': 'Chờ duyệt',
    'ACTIVE': 'Đang thực tập',
    'COMPLETED': 'Đã hoàn thành'
  };
  return map[status] || status;
};
</script>

<style scoped>
.intern-list-page {
  /* No top padding as it's handled by main-content in MainLayout */
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

.header-content h1 {
  font-size: 28px;
  font-weight: 700;
  color: #1b2559;
  margin: 0;
}

.header-content p {
  color: #4a5568;
  margin-top: 4px;
  font-weight: 500;
}

.create-btn {
  background-color: #4318ff;
  color: white;
  padding: 12px 24px;
  border-radius: 12px;
  text-decoration: none;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: background 0.3s;
}

.create-btn:hover {
  background-color: #3311cc;
}

/* Filters */
.filters-card {
  background: white;
  padding: 20px;
  border-radius: 20px;
  margin-bottom: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
}

.search-box {
  flex: 1;
  position: relative;
  max-width: 400px;
}

.search-icon {
  position: absolute;
  left: 16px;
  top: 50%;
  transform: translateY(-50%);
  color: #a3aed0;
}

.search-box input {
  width: 100%;
  padding: 12px 16px 12px 48px;
  background-color: #f4f7fe;
  border: none;
  border-radius: 12px;
  outline: none;
  color: #1b2559;
}

.search-box input::placeholder {
  color: #1b2559;
  opacity: 0.6;
}

.filter-group {
  display: flex;
  gap: 12px;
}

select {
  padding: 12px 16px;
  background-color: #f4f7fe;
  border: none;
  border-radius: 12px;
  color: #1b2559;
  font-weight: 600;
  outline: none;
  cursor: pointer;
}

option {
  color: #1b2559;
  background: white;
}

/* Table Card */
.table-card {
  background: white;
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
  min-height: 400px;
  position: relative;
}

.table-container {
  overflow-x: auto;
}

.intern-table {
  width: 100%;
  border-collapse: collapse;
}

.intern-table th {
  text-align: center;
  color: #1b2559;
  font-size: 11px;
  font-weight: 800;
  padding: 10px 6px;
  border-bottom: 2px solid #f4f7fe;
  text-transform: uppercase;
  opacity: 0.8;
}

.intern-table td {
  padding: 10px 6px;
  color: #2b3674;
  font-weight: 600;
  border-bottom: 1px solid #f4f7fe;
  text-align: center;
  white-space: nowrap;
  font-size: 12px;
}

.user-cell {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  min-width: 160px;
}

.table-avatar {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  flex-shrink: 0;
}

.mentor-badge {
  background: #f4f7fe;
  color: #4318ff;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 11px;
  display: inline-block;
}

.no-mentor {
  color: #a3aed0;
  font-style: italic;
  font-size: 13px;
}

.status-badge {
  padding: 4px 10px;
  border-radius: 100px;
  font-size: 11px;
  font-weight: 800;
  text-transform: capitalize;
  display: inline-block;
}

.status-badge.active, .status-badge.đang_thực_tập { background: #e7faf3; color: #05cd99; }
.status-badge.pending, .status-badge.chờ_duyệt { background: #fff5e9; color: #ffb547; }
.status-badge.completed, .status-badge.đã_hoàn_thành { background: #e9edfe; color: #3965ff; }

.actions-cell {
  display: flex;
  justify-content: center;
  gap: 6px;
}

.text-btn {
  padding: 2px 6px;
  border-radius: 4px;
  border: 1px solid #e2e8f0;
  background: white;
  color: #2b3674;
  font-size: 11px;
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

/* Pagination */
.pagination {
  margin-top: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-btn {
  padding: 10px 20px;
  border: none;
  background: #f4f7fe;
  color: #4318ff;
  font-weight: 700;
  border-radius: 10px;
  cursor: pointer;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-numbers {
  display: flex;
  gap: 8px;
}

.page-num {
  width: 40px;
  height: 40px;
  border: none;
  background: white;
  color: #a3aed0;
  font-weight: 700;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s;
}

.page-num.active {
  background: #4318ff;
  color: white;
}

.loading-overlay {
  padding: 60px 0;
  text-align: center;
  color: #a3aed0;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #4318ff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.empty-state {
  text-align: center;
  padding: 60px !important;
}

.empty-icon {
  font-size: 48px;
}
</style>
