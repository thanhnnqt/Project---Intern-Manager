<template>
  <div class="profile-page">
    <div class="page-header">
      <button @click="router.back()" class="back-btn">
        <span class="icon">⬅️</span> Quay lại
      </button>
      <h2>Thông tin cá nhân</h2>
    </div>

    <div class="profile-grid">
      <!-- Profile Card -->
      <div class="profile-card card">
        <div class="profile-header">
          <div class="avatar-container">
            <img :src="profileData.avatar || 'https://ui-avatars.com/api/?name=' + profileData.fullName" class="profile-avatar" />
            <label v-if="isEditing" class="avatar-upload">
              <input type="file" @change="handleAvatarUpload" accept="image/*" class="hidden-input" />
              <span class="upload-icon">📷</span>
            </label>
          </div>
          <div class="profile-title">
            <h3>{{ profileData.fullName }}</h3>
            <p>{{ user?.role }}</p>
          </div>
          <button v-if="!isEditing" @click="isEditing = true" class="edit-btn">
            Chỉnh sửa
          </button>
        </div>

        <form @submit.prevent="handleUpdateProfile" class="profile-form">
          <div class="form-grid">
            <div class="form-group">
              <label>Họ và tên</label>
              <input v-model="profileData.fullName" :disabled="!isEditing" type="text" placeholder="Nhập họ tên" required />
            </div>
            <div class="form-group">
              <label>Email</label>
              <input v-model="profileData.email" :disabled="!isEditing" type="email" placeholder="Nhập email" required />
            </div>
            <div class="form-group">
              <label>Số điện thoại</label>
              <input v-model="profileData.phone" :disabled="!isEditing" type="tel" placeholder="Nhập số điện thoại" />
            </div>
          </div>

          <div v-if="isEditing" class="form-actions">
            <button type="button" @click="cancelEdit" class="cancel-btn">Hủy</button>
            <button type="submit" class="save-btn" :disabled="loading">
              {{ loading ? 'Đang lưu...' : 'Lưu thay đổi' }}
            </button>
          </div>
        </form>
      </div>

      <!-- Password Card -->
      <div class="password-card card">
        <div class="card-header">
          <h3>Đổi mật khẩu</h3>
        </div>
        <form @submit.prevent="handleUpdatePassword" class="password-form">
          <div class="form-group">
            <label>Mật khẩu cũ</label>
            <input v-model="passwordForm.oldPassword" type="password" placeholder="••••••••" required />
          </div>
          <div class="form-group">
            <label>Mật khẩu mới</label>
            <input v-model="passwordForm.newPassword" type="password" placeholder="••••••••" required />
          </div>
          <div class="form-group">
            <label>Xác nhận mật khẩu mới</label>
            <input v-model="passwordForm.confirmPassword" type="password" placeholder="••••••••" required />
          </div>
          <button type="submit" class="submit-btn" :disabled="pwdLoading">
            {{ pwdLoading ? 'Đang cập nhật...' : 'Cập nhật mật khẩu' }}
          </button>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../../store/authStore';
import { profileService } from '../../services/profileService';

const router = useRouter();
const authStore = useAuthStore();
const user = authStore.user;

const isEditing = ref(false);
const loading = ref(false);
const pwdLoading = ref(false);

const profileData = reactive({
  username: '',
  fullName: '',
  email: '',
  phone: '',
  avatar: ''
});

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
});

onMounted(async () => {
  try {
    const data = await profileService.getProfile();
    Object.assign(profileData, data);
  } catch (error) {
    console.error('Lỗi khi tải profile:', error);
  }
});

const handleAvatarUpload = (event) => {
  const file = event.target.files[0];
  if (file) {
    if (file.size > 2 * 1024 * 1024) {
      alert('Dung lượng ảnh không được vượt quá 2MB');
      return;
    }
    const reader = new FileReader();
    reader.onload = (e) => {
      profileData.avatar = e.target.result;
    };
    reader.readAsDataURL(file);
  }
};

const handleUpdateProfile = async () => {
  loading.value = true;
  try {
    const updatedUser = await profileService.updateProfile({
      fullName: profileData.fullName,
      email: profileData.email,
      phone: profileData.phone,
      avatar: profileData.avatar
    });
    authStore.setUser(updatedUser);
    isEditing.value = false;
    alert('Cập nhật thông tin thành công!');
  } catch (error) {
    alert(error.response?.data?.message || 'Lỗi khi cập nhật profile');
  } finally {
    loading.value = false;
  }
};

const handleUpdatePassword = async () => {
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    alert('Mật khẩu xác nhận không khớp');
    return;
  }
  
  pwdLoading.value = true;
  try {
    await profileService.updatePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    });
    alert('Đổi mật khẩu thành công!');
    passwordForm.oldPassword = '';
    passwordForm.newPassword = '';
    passwordForm.confirmPassword = '';
  } catch (error) {
    alert(error.response?.data?.message || 'Mật khẩu cũ không chính xác');
  } finally {
    pwdLoading.value = false;
  }
};

const cancelEdit = () => {
  isEditing.value = false;
  // Restore original data
  const original = authStore.user;
  Object.assign(profileData, {
    fullName: original.fullName,
    email: original.email,
    phone: original.phone,
    avatar: original.avatar
  });
};
</script>

<style scoped>
.profile-page {
  padding: 0;
  max-width: 1200px;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 32px;
}

.page-header h2 {
  font-size: 24px;
  font-weight: 700;
  color: #1b2559;
  margin: 0;
}

.back-btn {
  background: white;
  padding: 10px 20px;
  border-radius: 12px;
  color: #2b3674;
  font-weight: 700;
  border: none;
  cursor: pointer;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.2s;
}

.back-btn:hover {
  background: #f4f7fe;
  transform: translateX(-5px);
}

.profile-grid {
  display: grid;
  grid-template-columns: 1.5fr 1fr;
  gap: 24px;
}

@media (max-width: 1024px) {
  .profile-grid {
    grid-template-columns: 1fr;
  }
}

.card {
  background: white;
  border-radius: 20px;
  padding: 32px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
}

.profile-header {
  display: flex;
  align-items: center;
  gap: 24px;
  margin-bottom: 32px;
  padding-bottom: 24px;
  border-bottom: 1px solid #f4f7fe;
  position: relative;
}

.avatar-container {
  position: relative;
  width: 100px;
  height: 100px;
}

.profile-avatar {
  width: 100px;
  height: 100px;
  border-radius: 20px;
  object-fit: cover;
}

.avatar-upload {
  position: absolute;
  bottom: -10px;
  right: -10px;
  background: #3965ff;
  width: 36px;
  height: 36px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 4px 10px rgba(57, 101, 255, 0.3);
  transition: all 0.2s;
}

.avatar-upload:hover {
  transform: scale(1.1);
}

.hidden-input {
  display: none;
}

.upload-icon {
  font-size: 18px;
}

.profile-title {
  flex: 1;
}

.profile-title h3 {
  font-size: 22px;
  font-weight: 800;
  color: #1b2559;
  margin: 0;
}

.profile-title p {
  color: #a3aed0;
  font-weight: 600;
  margin: 4px 0 0;
  text-transform: capitalize;
}

.edit-btn {
  padding: 8px 16px;
  border-radius: 10px;
  background: #f4f7fe;
  color: #3965ff;
  font-weight: 700;
  border: none;
  cursor: pointer;
  transition: all 0.2s;
}

.edit-btn:hover {
  background: #3965ff;
  color: white;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

@media (max-width: 640px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  font-size: 12px;
  color: #1b2559;
  font-weight: 700;
  margin-bottom: 8px;
  text-transform: uppercase;
}

.form-group input {
  width: 100%;
  padding: 12px 16px;
  border-radius: 14px;
  border: 1px solid #e0e5f2;
  font-size: 14px;
  color: #2b3674;
  font-weight: 500;
  transition: all 0.2s;
}

.form-group input:focus {
  outline: none;
  border-color: #3965ff;
}

.form-group input:disabled {
  background: #f4f7fe;
  border-color: transparent;
  cursor: not-allowed;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
}

.cancel-btn {
  padding: 10px 24px;
  border-radius: 12px;
  background: #f4f7fe;
  color: #1b2559;
  font-weight: 700;
  border: none;
  cursor: pointer;
}

.save-btn, .submit-btn {
  padding: 10px 24px;
  border-radius: 12px;
  background: #3965ff;
  color: white;
  font-weight: 700;
  border: none;
  cursor: pointer;
  box-shadow: 0 4px 10px rgba(57, 101, 255, 0.2);
}

.save-btn:hover, .submit-btn:hover {
  background: #2b52e1;
}

.save-btn:disabled, .submit-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.password-card .card-header {
  margin-bottom: 24px;
}

.password-card h3 {
  font-size: 18px;
  font-weight: 700;
  color: #1b2559;
  margin: 0;
}

.submit-btn {
  width: 100%;
  margin-top: 12px;
}
</style>
