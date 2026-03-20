<template>
  <div class="notifications-page">
    <div class="page-header">
      <h2>Thông báo</h2>
      <div class="header-actions">
        <button 
          v-if="user?.role === 'MENTOR' || user?.role === 'ADMIN'"
          @click="showCreateModal = true"
          class="create-btn"
        >
          ➕ Tạo thông báo
        </button>
        <button 
          v-if="notifications.some(n => !n.isRead)" 
          @click="handleMarkAllAsRead" 
          class="mark-all-btn"
          :disabled="loading"
        >
          Đánh dấu tất cả là đã đọc
        </button>
      </div>
    </div>

    <!-- Create Notification Modal -->
    <div v-if="showCreateModal" class="modal-overlay" @click.self="showCreateModal = false">
      <div class="modal-content">
        <div class="modal-header">
          <h3>Tạo thông báo mới</h3>
          <button class="close-btn" @click="showCreateModal = false">&times;</button>
        </div>
        <form @submit.prevent="handleCreateNotification" class="create-form">
          <div class="form-body">
            <div class="form-main">
              <div class="form-group">
                <label for="title">Tiêu đề</label>
                <input 
                  id="title" 
                  v-model="newNotification.title" 
                  type="text" 
                  placeholder="Nhập tiêu đề thông báo" 
                  required
                />
              </div>
              <div class="form-group flex-1">
                <label for="content">Nội dung</label>
                <textarea 
                  id="content" 
                  v-model="newNotification.content" 
                  placeholder="Nhập nội dung thông báo" 
                  rows="12"
                  required
                ></textarea>
              </div>
            </div>

            <!-- Recipient Selection -->
            <div class="recipient-selection">
              <div class="selection-header">
                <label>Người nhận ({{ selectedInternIds.length }}/{{ interns.length }})</label>
                <div class="selection-filters">
                  <input 
                    v-model="searchQuery" 
                    type="text" 
                    placeholder="Tìm tên..." 
                    class="search-input"
                  />
                  <div class="mentor-filters">
                    <span class="filter-label">Lọc theo Mentor:</span>
                    <div class="custom-dropdown" ref="dropdownContainer">
                      <button 
                        type="button" 
                        class="dropdown-trigger"
                        @click.stop="showMentorDropdown = !showMentorDropdown"
                      >
                        <span v-if="selectedMentorIds.length === 0">Chọn Mentor...</span>
                        <span v-else>Đã chọn {{ selectedMentorIds.length }} Mentor</span>
                        <i :class="['arrow', { open: showMentorDropdown }]"></i>
                      </button>
                      <div v-if="showMentorDropdown" class="dropdown-menu">
                        <div 
                          v-for="mentor in mentors" 
                          :key="mentor.id"
                          class="dropdown-item"
                          @click.stop="toggleMentorSelection(mentor.id)"
                        >
                          <label class="checkbox-container mini" @click.stop>
                            <input 
                              type="checkbox" 
                              :checked="selectedMentorIds.includes(mentor.id)"
                              @change="toggleMentorSelection(mentor.id)"
                            />
                            <span class="checkmark"></span>
                            {{ mentor.name }}
                          </label>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <div class="intern-list-container">
                <div class="list-actions">
                  <label class="checkbox-container">
                    <input 
                      type="checkbox" 
                      :checked="isAllSelected" 
                      @change="toggleSelectAll"
                    />
                    <span class="checkmark"></span>
                    Chọn tất cả (đang hiển thị)
                  </label>
                </div>
                <div class="intern-items">
                  <div 
                    v-for="intern in filteredInterns" 
                    :key="intern.id"
                    class="intern-select-item"
                  >
                    <label class="checkbox-container">
                      <input 
                        type="checkbox" 
                        :value="intern.accountId" 
                        v-model="selectedInternIds"
                      />
                      <span class="checkmark"></span>
                      <div class="intern-info">
                        <span class="intern-name">{{ intern.name }}</span>
                        <span class="intern-mentor" v-if="intern.mentorName"> - Mentor: {{ intern.mentorName }}</span>
                      </div>
                    </label>
                  </div>
                  <div v-if="filteredInterns.length === 0" class="no-results">
                    Không tìm thấy thực tập sinh nào phù hợp.
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="form-actions">
            <button type="button" class="cancel-btn" @click="showCreateModal = false">Hủy</button>
            <button type="submit" class="submit-btn" :disabled="submitting">
              {{ submitting ? 'Đang gửi...' : 'Gửi thông báo' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <div v-if="loading" class="loading-state">
      <div class="spinner"></div>
      <p>Đang tải thông báo...</p>
    </div>

    <div v-else-if="notifications.length === 0" class="empty-state">
      <div class="empty-icon">🔔</div>
      <h3>Không có thông báo nào</h3>
      <p>Bạn sẽ nhận được thông báo khi có cập nhật mới về task hoặc phản hồi từ mentor.</p>
    </div>

    <div v-else class="notifications-list">
      <div 
        v-for="notification in notifications" 
        :key="notification.id" 
        class="notification-item"
        :class="{ unread: !notification.isRead }"
        @click="handleMarkAsRead(notification)"
      >
        <div class="status-dot" v-if="!notification.isRead"></div>
        <div class="notification-content">
          <h4 class="notification-title">{{ notification.title }}</h4>
          <p class="notification-text">{{ notification.content }}</p>
          <span class="notification-time">{{ formatDate(notification.createdAt) }}</span>
        </div>
        <div class="notification-actions" v-if="!notification.isRead">
          <button @click.stop="handleMarkAsRead(notification)" class="read-btn" title="Đánh dấu là đã đọc">
            ✓
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue';
import { notificationService } from '../../services/notificationService';
import { internService } from '../../services/internService';
import { mentorService } from '../../services/mentorService';
import { useAuthStore } from '../../store/authStore';

const authStore = useAuthStore();
const user = authStore.user;

const notifications = ref([]);
const interns = ref([]);
const mentors = ref([]);
const loading = ref(true);
const submitting = ref(false);
const showCreateModal = ref(false);
const showMentorDropdown = ref(false);
const dropdownContainer = ref(null);

const searchQuery = ref('');
const selectedMentorIds = ref([]);
const selectedInternIds = ref([]);

const newNotification = ref({
  title: '',
  content: ''
});

const fetchNotifications = async () => {
  try {
    loading.value = true;
    const data = await notificationService.getNotifications();
    notifications.value = data.map(n => ({
      ...n,
      isRead: n.isRead !== undefined ? n.isRead : n.read
    }));
  } catch (error) {
    console.error('Lỗi khi tải thông báo:', error);
  } finally {
    loading.value = false;
  }
};

const fetchDataForModal = async () => {
  try {
    const [internData, mentorData] = await Promise.all([
      internService.getAllInterns({ size: 1000 }),
      mentorService.getAllMentors({ size: 100 })
    ]);
    interns.value = internData.content;
    mentors.value = mentorData.content;
  } catch (error) {
    console.error('Lỗi khi tải dữ liệu cho modal:', error);
  }
};

const toggleMentorSelection = (mentorId) => {
  const index = selectedMentorIds.value.indexOf(mentorId);
  if (index > -1) {
    selectedMentorIds.value.splice(index, 1);
  } else {
    selectedMentorIds.value.push(mentorId);
  }
};

const handleClickOutside = (event) => {
  if (dropdownContainer.value && !dropdownContainer.value.contains(event.target)) {
    showMentorDropdown.value = false;
  }
};

onMounted(() => {
  fetchNotifications();
  window.addEventListener('click', handleClickOutside);
});

watch(showCreateModal, (newVal) => {
  if (newVal) {
    if (interns.value.length === 0) fetchDataForModal();
    selectedInternIds.value = [];
    selectedMentorIds.value = [];
    searchQuery.value = '';
    showMentorDropdown.value = false;
    newNotification.value = { title: '', content: '' };
  }
});

const filteredInterns = computed(() => {
  return interns.value.filter(intern => {
    const matchesSearch = intern.name.toLowerCase().includes(searchQuery.value.toLowerCase());
    const matchesMentor = selectedMentorIds.value.length === 0 || selectedMentorIds.value.includes(intern.mentorId);
    return matchesSearch && matchesMentor;
  });
});

const isAllSelected = computed(() => {
  const accountIds = filteredInterns.value.map(i => i.accountId);
  return accountIds.length > 0 && accountIds.every(id => selectedInternIds.value.includes(id));
});

const toggleSelectAll = () => {
  const accountIds = filteredInterns.value.map(i => i.accountId);
  if (isAllSelected.value) {
    selectedInternIds.value = selectedInternIds.value.filter(id => !accountIds.includes(id));
  } else {
    const combined = [...new Set([...selectedInternIds.value, ...accountIds])];
    selectedInternIds.value = combined;
  }
};

const handleMarkAsRead = async (notification) => {
  if (notification.isRead) return;
  
  try {
    await notificationService.markAsRead(notification.id);
    notification.isRead = true;
  } catch (error) {
    console.error('Lỗi khi đánh dấu đã đọc:', error);
  }
};

const handleMarkAllAsRead = async () => {
  try {
    loading.value = true;
    await notificationService.markAllAsRead();
    notifications.value.forEach(n => n.isRead = true);
  } catch (error) {
    console.error('Lỗi khi đánh dấu tất cả đã đọc:', error);
  } finally {
    loading.value = false;
  }
};

const handleCreateNotification = async () => {
  if (!newNotification.value.title || !newNotification.value.content) return;
  
  const payload = {
    ...newNotification.value,
    accountIds: selectedInternIds.value.length > 0 ? selectedInternIds.value : null
  };
  
  try {
    submitting.value = true;
    await notificationService.createNotification(payload);
    showCreateModal.value = false;
    alert(selectedInternIds.value.length > 0 
      ? `Đã gửi thông báo tới ${selectedInternIds.value.length} người nhận!`
      : 'Thông báo đã được gửi tới tất cả thực tập sinh!');
    fetchNotifications();
  } catch (error) {
    console.error('Lỗi khi tạo thông báo:', error);
    alert('Có lỗi xảy ra khi gửi thông báo. Vui lòng thử lại.');
  } finally {
    submitting.value = false;
  }
};

const formatDate = (dateString) => {
  const date = new Date(dateString);
  const now = new Date();
  const diffInMs = now - date;
  const diffInMins = Math.floor(diffInMs / (1000 * 60));
  const diffInHours = Math.floor(diffInMs / (1000 * 60 * 60));
  const diffInDays = Math.floor(diffInMs / (1000 * 60 * 60 * 24));

  if (diffInMins < 60) return `${diffInMins} phút trước`;
  if (diffInHours < 24) return `${diffInHours} giờ trước`;
  if (diffInDays < 7) return `${diffInDays} ngày trước`;
  
  return date.toLocaleDateString('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
};
</script>

<style scoped>
.notifications-page {
  padding: 0;
  max-width: 800px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-header h2 {
  font-size: 24px;
  font-weight: 700;
  color: #1b2559;
  margin: 0;
}

.header-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.create-btn {
  background: #4318ff;
  color: white;
  border: none;
  font-weight: 700;
  font-size: 14px;
  cursor: pointer;
  padding: 10px 16px;
  border-radius: 10px;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 8px;
}

.create-btn:hover {
  background: #3311cc;
  transform: translateY(-1px);
}

.mark-all-btn {
  background: transparent;
  color: #3965ff;
  border: none;
  font-weight: 600;
  font-size: 14px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 8px;
  transition: all 0.2s;
}

/* Modal Styles */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  backdrop-filter: blur(4px);
}

.modal-content {
  background: white;
  border-radius: 20px;
  width: 95%;
  max-width: 900px;
  max-height: 90vh;
  padding: 24px;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
  display: flex;
  flex-direction: column;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-shrink: 0;
}

.modal-header h3 {
  margin: 0;
  color: #1b2559;
  font-size: 20px;
  font-weight: 700;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  color: #a3aed0;
  cursor: pointer;
}

.create-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-body {
  display: flex;
  gap: 24px;
}

.form-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group.flex-1 {
  flex: 1;
}

.form-group label {
  font-weight: 600;
  color: #1b2559;
  font-size: 14px;
}

.form-group input, .form-group textarea {
  padding: 12px;
  border-radius: 12px;
  border: 1px solid #e0e5f2;
  outline: none;
  font-family: inherit;
  font-size: 14px;
  transition: border-color 0.2s;
  width: 100%;
}

.form-group textarea {
  resize: none;
}

.form-group input:focus, .form-group textarea:focus {
  border-color: #4318ff;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 8px;
}

.cancel-btn {
  padding: 10px 20px;
  border-radius: 10px;
  border: 1px solid #e0e5f2;
  background: white;
  color: #707ebe;
  font-weight: 600;
  cursor: pointer;
}

.submit-btn {
  padding: 10px 20px;
  border-radius: 10px;
  border: none;
  background: #4318ff;
  color: white;
  font-weight: 700;
  cursor: pointer;
}

.submit-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

/* Recipient Selection Styles */
.recipient-selection {
  border: 1px solid #e0e5f2;
  border-radius: 16px;
  padding: 16px;
  background: #f8faff;
  width: 400px;
  display: flex;
  flex-direction: column;
}

.selection-header {
  margin-bottom: 12px;
  flex-shrink: 0;
}

.selection-header label {
  display: block;
  font-weight: 700;
  color: #1b2559;
  margin-bottom: 12px;
}

.selection-filters {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.search-input {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #e0e5f2;
  border-radius: 8px;
  font-size: 14px;
}

.mentor-filters {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.filter-label {
  font-size: 12px;
  font-weight: 600;
  color: #a3aed0;
}

.custom-dropdown {
  position: relative;
  width: 100%;
}

.dropdown-trigger {
  width: 100%;
  padding: 10px 14px;
  background: white;
  border: 1px solid #e0e5f2;
  border-radius: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
  color: #1b2559;
  font-weight: 600;
  cursor: pointer;
}

.arrow {
  border: solid #a3aed0;
  border-width: 0 2px 2px 0;
  display: inline-block;
  padding: 3px;
  transform: rotate(45deg);
  transition: transform 0.2s;
}

.arrow.open {
  transform: rotate(-135deg);
}

.dropdown-menu {
  position: absolute;
  top: calc(100% + 4px);
  left: 0;
  right: 0;
  background: white;
  border: 1px solid #e0e5f2;
  border-radius: 12px;
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
  max-height: 200px;
  overflow-y: auto;
  z-index: 10;
  padding: 8px 0;
}

.dropdown-item {
  padding: 8px 14px;
  cursor: pointer;
  transition: background 0.2s;
}

.dropdown-item:hover {
  background: #f4f7fe;
}

.checkbox-container.mini {
  padding-left: 24px;
  font-size: 13px;
  color: #1b2559;
}

.checkbox-container.mini .checkmark {
  height: 16px;
  width: 16px;
}

.checkbox-container.mini .checkmark:after {
  left: 5px;
  top: 1px;
  width: 4px;
  height: 9px;
}

.intern-list-container {
  background: white;
  border: 1px solid #e0e5f2;
  border-radius: 12px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.intern-items {
  max-height: 320px;
  overflow-y: auto;
}

.intern-items::-webkit-scrollbar {
  width: 6px;
}

.intern-items::-webkit-scrollbar-track {
  background: #f4f7fe;
  border-radius: 10px;
}

.intern-items::-webkit-scrollbar-thumb {
  background: #a3aed0;
  border-radius: 10px;
}

.intern-items::-webkit-scrollbar-thumb:hover {
  background: #4318ff;
}

.list-actions {
  padding: 10px 12px;
  border-bottom: 1px solid #f4f7fe;
  background: #f4f7fe;
  border-top-left-radius: 12px;
  border-top-right-radius: 12px;
}

.intern-items {
  overflow-y: auto;
  flex: 1;
}

.intern-select-item {
  padding: 8px 12px;
  border-bottom: 1px solid #f4f7fe;
}

.intern-select-item:hover {
  background: #f9faff;
}

.intern-info {
  display: flex;
  flex-direction: column;
}

.intern-name {
  font-weight: 600;
  color: #1b2559;
  font-size: 14px;
}

.intern-mentor {
  font-size: 11px;
  color: #a3aed0;
}

/* Custom Checkbox */
.checkbox-container {
  display: flex;
  align-items: center;
  position: relative;
  padding-left: 28px;
  cursor: pointer;
  font-size: 14px;
  user-select: none;
  color: #707ebe;
}

.checkbox-container input {
  position: absolute;
  opacity: 0;
  cursor: pointer;
  height: 0;
  width: 0;
}

.checkmark {
  position: absolute;
  top: 50%;
  left: 0;
  transform: translateY(-50%);
  height: 18px;
  width: 18px;
  background-color: #eee;
  border-radius: 4px;
  transition: all 0.2s;
}

.checkbox-container:hover input ~ .checkmark {
  background-color: #ccc;
}

.checkbox-container input:checked ~ .checkmark {
  background-color: #4318ff;
}

.checkmark:after {
  content: "";
  position: absolute;
  display: none;
}

.checkbox-container input:checked ~ .checkmark:after {
  display: block;
}

.checkbox-container .checkmark:after {
  left: 6px;
  top: 2px;
  width: 5px;
  height: 10px;
  border: solid white;
  border-width: 0 2px 2px 0;
  transform: rotate(45deg);
}

.no-results {
  padding: 20px;
  text-align: center;
  color: #a3aed0;
  font-size: 13px;
}

.mark-all-btn:hover {
  background: #f4f7fe;
}

.mark-all-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.notifications-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.notification-item {
  background: white;
  border-radius: 16px;
  padding: 20px;
  display: flex;
  gap: 16px;
  align-items: flex-start;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.03);
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
  border: 1px solid transparent;
}

.notification-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(0, 0, 0, 0.05);
  border-color: #e0e5f2;
}

.notification-item.unread {
  background: #f9faff;
  border-color: #ebf0ff;
}

.status-dot {
  width: 10px;
  height: 10px;
  background: #3965ff;
  border-radius: 50%;
  margin-top: 6px;
  flex-shrink: 0;
}

.notification-content {
  flex: 1;
}

.notification-title {
  font-size: 16px;
  font-weight: 700;
  color: #1b2559;
  margin: 0 0 6px 0;
  overflow-wrap: break-word;
  word-break: break-word;
}

.unread .notification-title {
  color: #3965ff;
}

.notification-text {
  font-size: 14px;
  color: #707ebe;
  margin: 0 0 10px 0;
  line-height: 1.5;
  overflow-wrap: break-word;
  word-break: break-word;
}

.notification-time {
  font-size: 12px;
  color: #a3aed0;
  font-weight: 500;
}

.notification-actions {
  display: flex;
  align-items: center;
}

.read-btn {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #f4f7fe;
  color: #3965ff;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.2s;
}

.read-btn:hover {
  background: #3965ff;
  color: white;
  transform: scale(1.1);
}

.loading-state, .empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 0;
  text-align: center;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 20px;
  opacity: 0.5;
}

.empty-state h3 {
  color: #1b2559;
  margin-bottom: 8px;
}

.empty-state p {
  color: #a3aed0;
  max-width: 300px;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #3965ff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>
