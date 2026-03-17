<template>
  <div class="mentor-form-container">
    <div class="form-header">
      <button @click="router.back()" class="back-btn">
        <span class="icon">⬅️</span> Quay lại
      </button>
      <h2>{{ isEdit ? 'Cập nhật Người hướng dẫn' : 'Thêm Người hướng dẫn mới' }}</h2>
    </div>

    <form @submit.prevent="handleSubmit" class="mentor-form">
      <div class="form-grid">
        <!-- Left Column: Avatar -->
        <div class="avatar-section">
          <div class="avatar-upload">
            <img :src="formData.avatar || 'https://ui-avatars.com/api/?name=Mentor&size=128'" class="preview-img" />
            <div class="upload-overlay">
              <label for="avatar-input" class="upload-label">
                <span class="icon">📸</span> Thay đổi ảnh
              </label>
              <input
                id="avatar-input"
                type="file"
                accept="image/*"
                @change="handleAvatarUpload"
                hidden
              />
            </div>
          </div>
          <p class="upload-hint">Định dạng: JPG, PNG. Dung lượng tối đa: 2MB</p>
        </div>

        <!-- Right Column: Info -->
        <div class="info-section">
          <div class="input-group">
            <label>Họ và tên <span class="required">*</span></label>
            <input
              v-model="formData.name"
              type="text"
              placeholder="Vd: Nguyễn Văn B"
              :class="{ 'error': errors.name }"
            />
            <span class="error-msg" v-if="errors.name">{{ errors.name }}</span>
          </div>

          <div class="input-row">
            <div class="input-group">
              <label>Email <span class="required">*</span></label>
              <input
                v-model="formData.email"
                type="email"
                autocomplete="off"
                :class="{ 'error': errors.email }"
              />
              <span class="error-msg" v-if="errors.email">{{ errors.email }}</span>
            </div>
            <div class="input-group" v-if="!isEdit">
              <label>Mật khẩu <span class="required">*</span></label>
              <input
                v-model="formData.password"
                type="password"
                autocomplete="new-password"
                :class="{ 'error': errors.password }"
              />
              <span class="error-msg" v-if="errors.password">{{ errors.password }}</span>
            </div>
          </div>

          <div class="input-row">
            <div class="input-group">
              <label>Bộ phận / Phòng ban</label>
              <input v-model="formData.department" type="text" placeholder="Vd: Phòng Kỹ thuật" />
            </div>
            <div class="input-group">
              <label>Chức vụ</label>
              <input v-model="formData.position" type="text" placeholder="Vd: Kỹ sư phần mềm" />
            </div>
          </div>

          <div class="form-actions">
            <button type="button" @click="router.back()" class="cancel-btn">Hủy bỏ</button>
            <button type="submit" class="submit-btn" :disabled="submitting">
              {{ submitting ? 'Đang lưu...' : (isEdit ? 'Lưu thay đổi' : 'Tạo người hướng dẫn') }}
            </button>
          </div>
        </div>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { mentorService } from '../../services/mentorService';

const props = defineProps({
  isEdit: Boolean
});

const router = useRouter();
const route = useRoute();
const submitting = ref(false);

const formData = reactive({
  name: '',
  email: '',
  password: '',
  department: '',
  position: '',
  avatar: ''
});

const errors = reactive({
  name: '',
  email: '',
  password: ''
});

onMounted(async () => {
  if (props.isEdit) {
    try {
      const data = await mentorService.getMentorById(route.params.id);
      Object.assign(formData, {
        name: data.name,
        email: data.email,
        department: data.department,
        position: data.position,
        avatar: data.avatar
      });
    } catch (err) {
      console.error('Lỗi khi tải thông tin người hướng dẫn:', err);
      alert('Không tìm thấy người hướng dẫn này.');
      router.push('/mentors');
    }
  }
});

const validateForm = () => {
  let isValid = true;
  errors.name = '';
  errors.email = '';
  errors.password = '';

  if (!(formData.name || '').trim()) {
    errors.name = 'Họ tên là bắt buộc';
    isValid = false;
  }

  if (!(formData.email || '').trim()) {
    errors.email = 'Email là bắt buộc';
    isValid = false;
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
    errors.email = 'Email không đúng định dạng';
    isValid = false;
  }

  if (!props.isEdit && !(formData.password || '').trim()) {
    errors.password = 'Mật khẩu là bắt buộc khi tạo mới';
    isValid = false;
  } else if (!props.isEdit && (formData.password || '').length < 6) {
    errors.password = 'Mật khẩu phải từ 6 ký tự trở lên';
    isValid = false;
  }

  return isValid;
};

const handleAvatarUpload = (event) => {
  const file = event.target.files[0];
  if (file) {
    if (file.size > 2 * 1024 * 1024) {
      alert('Dung lượng ảnh không được vượt quá 2MB');
      return;
    }
    const reader = new FileReader();
    reader.onload = (e) => {
      formData.avatar = e.target.result;
    };
    reader.readAsDataURL(file);
  }
};

const handleSubmit = async () => {
  if (!validateForm()) return;

  submitting.value = true;
  try {
    if (props.isEdit) {
      await mentorService.updateMentor(route.params.id, formData);
    } else {
      await mentorService.createMentor(formData);
    }
    router.push('/mentors');
  } catch (err) {
    console.error('Lỗi khi lưu thông tin:', err);
    if (err.response?.data?.message) {
      alert('Lỗi: ' + err.response.data.message);
    } else {
      alert('Có lỗi xảy ra. Vui lòng thử lại.');
    }
  } finally {
    submitting.value = false;
  }
};
</script>

<style scoped>
.mentor-form-container {
  padding: 12px 24px;
  max-width: 900px;
  margin: 0 auto;
}

.form-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
}

.back-btn {
  background: white;
  border: 1px solid #e2e8f0;
  padding: 8px 16px;
  border-radius: 10px;
  color: #2b3674;
  font-weight: 700;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.2s;
}

.back-btn:hover {
  background: #f4f7fe;
  border-color: #cbd5e1;
}

.form-header h2 {
  font-size: 20px;
  font-weight: 700;
  color: #1b2559;
  margin: 0;
}

.mentor-form {
  background: white;
  padding: 24px;
  border-radius: 20px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
}

.form-grid {
  display: grid;
  grid-template-columns: 160px 1fr;
  gap: 32px;
}

@media (max-width: 768px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
  .avatar-section {
    display: flex;
    flex-direction: column;
    align-items: center;
  }
}

/* Avatar Upload */
.avatar-section {
  text-align: center;
}

.avatar-upload {
  position: relative;
  width: 130px;
  height: 130px;
  margin: 0 auto 12px;
  border-radius: 20px;
  overflow: hidden;
  border: 2px dashed #e2e8f0;
  transition: all 0.3s;
}

.avatar-upload:hover {
  border-color: #4318ff;
}

.preview-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.upload-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(0, 0, 0, 0.5);
  padding: 8px;
  opacity: 0;
  transition: opacity 0.3s;
}

.avatar-upload:hover .upload-overlay {
  opacity: 1;
}

.upload-label {
  color: white;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

.upload-hint {
  font-size: 12px;
  color: #a3aed0;
  line-height: 1.4;
}

/* Form Info */
.info-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.input-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.input-group {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.input-group label {
  font-size: 14px;
  font-weight: 700;
  color: #1b2559;
}

.required {
  color: #ff5b5b;
}

input, select {
  padding: 10px 14px;
  background-color: #f4f7fe;
  border: 1px solid transparent;
  border-radius: 10px;
  outline: none;
  color: #1b2559;
  font-weight: 600;
  transition: all 0.2s;
}

input:focus, select:focus {
  border-color: #4318ff;
  background-color: white;
  box-shadow: 0 0 0 4px rgba(67, 24, 255, 0.05);
}

input.error {
  border-color: #ff5b5b;
  background-color: #fff5f5;
}

.error-msg {
  color: #ff5b5b;
  font-size: 12px;
  font-weight: 600;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  margin-top: 8px;
}

.cancel-btn {
  padding: 10px 24px;
  background: #f4f7fe;
  border: none;
  border-radius: 12px;
  color: #707eae;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s;
}

.cancel-btn:hover {
  background: #e2e8f0;
  color: #1b2559;
}

.submit-btn {
  padding: 10px 24px;
  background: #4318ff;
  border: none;
  border-radius: 12px;
  color: white;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 4px 14px rgba(67, 24, 255, 0.2);
  transition: all 0.2s;
}

.submit-btn:hover:not(:disabled) {
  background: #3311cc;
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(67, 24, 255, 0.3);
}

.submit-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}
</style>
