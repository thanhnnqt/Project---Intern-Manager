<template>
  <div class="mentor-list-page">
    <div class="page-header">
      <div class="header-text">
        <h2>Danh sách Người hướng dẫn</h2>
        <p>Quản lý đội ngũ hướng dẫn và theo dõi số lượng thực tập sinh.</p>
      </div>
      <button class="create-btn" @click="router.push('/mentors/create')">
        <span class="icon">➕</span> Thêm mới
      </button>
    </div>

    <!-- Search & Filter Bar -->
    <div class="filter-bar card">
      <div class="search-input">
        <span class="search-icon">🔍</span>
        <input
          type="text"
          v-model="searchQuery"
          placeholder="Tìm kiếm theo tên hoặc email..."
        />
      </div>
    </div>

    <!-- Mentor Table -->
    <div class="table-card card">
      <div class="table-container">
        <table class="data-table">
          <thead>
            <tr>
              <th>Người hướng dẫn</th>
              <th>Phòng ban</th>
              <th>Chức vụ</th>
              <th>Số thực tập sinh</th>
              <th>Hành động</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="mentor in filteredMentors" :key="mentor.id">
              <td>
                <div class="user-cell">
                  <img :src="mentor.avatar || 'https://ui-avatars.com/api/?name=' + mentor.name" class="table-avatar" />
                  <div class="user-info">
                    <span class="user-name">{{ mentor.name }}</span>
                    <span class="user-email">{{ mentor.email }}</span>
                  </div>
                </div>
              </td>
              <td>{{ mentor.department || '---' }}</td>
              <td>{{ mentor.position || '---' }}</td>
              <td>
                <button
                  class="count-badge-btn"
                  @click="router.push({ path: '/interns', query: { mentorId: mentor.id } })"
                  title="Xem danh sách thực tập sinh"
                >
                  <span class="count-badge">{{ mentor.internCount }}</span>
                </button>
              </td>
              <td>
                <div class="actions">
                  <button class="text-btn edit" @click="router.push(`/mentors/${mentor.id}/edit`)">Sửa</button>
                  <button class="text-btn delete" @click="handleDelete(mentor.id)">Xóa</button>
                </div>
              </td>
            </tr>
            <tr v-if="filteredMentors.length === 0 && !loading">
              <td colspan="5" class="empty-state">Không tìm thấy người hướng dẫn nào.</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="loading-overlay">
      <div class="spinner"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { mentorService } from '../../services/mentorService';

const router = useRouter();
const mentors = ref([]);
const loading = ref(true);
const searchQuery = ref('');

const fetchMentors = async () => {
  try {
    loading.value = true;
    mentors.value = await mentorService.getAllMentors();
  } catch (error) {
    console.error("Lỗi khi tải danh sách mentor:", error);
  } finally {
    loading.value = false;
  }
};

const filteredMentors = computed(() => {
  const query = searchQuery.value.toLowerCase();
  return mentors.value.filter(m =>
    m.name.toLowerCase().includes(query) ||
    m.email.toLowerCase().includes(query)
  );
});

const handleDelete = async (id) => {
  if (confirm("Bạn có chắc chắn muốn xóa người hướng dẫn này không?")) {
    try {
      await mentorService.deleteMentor(id);
      await fetchMentors();
    } catch (error) {
      alert("Không thể xóa người hướng dẫn này!");
    }
  }
};

onMounted(fetchMentors);
</script>

<style scoped>
.mentor-list-page {
  padding: 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

.header-text h2 {
  font-size: 24px;
  font-weight: 800;
  color: #1b2559;
  margin: 0;
}

.header-text p {
  color: #a3aed0;
  margin: 4px 0 0;
  font-weight: 500;
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

.create-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(67, 24, 255, 0.3);
}

.card {
  background: white;
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
}

.filter-bar {
  margin-bottom: 24px;
}

.search-input {
  display: flex;
  align-items: center;
  background: #f4f7fe;
  padding: 12px 16px;
  border-radius: 14px;
  width: 100%;
  max-width: 400px;
}

.search-icon {
  margin-right: 12px;
  opacity: 0.5;
}

.search-input input {
  background: transparent;
  border: none;
  width: 100%;
  outline: none;
  font-weight: 500;
  color: #1b2559;
}

/* Table Styles */
.table-container {
  overflow-x: auto;
}

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
  text-align: center;
  white-space: nowrap;
  font-size: 12px;
}

.user-cell {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.table-avatar {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  object-fit: cover;
}

.user-name {
  display: block;
  font-weight: 700;
  color: #1b2559;
  font-size: 12px;
}

.user-email {
  font-size: 11px;
  color: #a3aed0;
  font-weight: 500;
}
.count-badge-btn {
  background: none;
  border: none;
  padding: 0;
  cursor: pointer;
  transition: transform 0.2s;
}

.count-badge-btn:hover {
  transform: scale(1.1);
}

.count-badge {
  background: #e9edfe;
  color: #4318ff;
  padding: 4px 12px;
  border-radius: 8px;
  font-weight: 700;
  font-size: 14px;
}

.actions {
  display: flex;
  gap: 6px;
  justify-content: center;
}

.text-btn {
  padding: 2px 8px;
  border-radius: 4px;
  border: 1px solid #e2e8f0;
  background: white;
  color: #1b2559;
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

.empty-state {
  text-align: center;
  padding: 40px;
  color: #a3aed0;
}

/* Spinner */
.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f4f7fe;
  border-top-color: #4318ff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin { to { transform: rotate(360deg); } }

.loading-overlay {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  display: flex; align-items: center; justify-content: center;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 20px;
}
</style>
