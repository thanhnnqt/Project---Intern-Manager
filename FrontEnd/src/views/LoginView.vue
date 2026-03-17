<template>
  <div class="login-wrapper">
    <div class="login-box">
      <div class="header">
        <div class="logo-icon">🎓</div>
        <h1>Quản Lý Thực Tập Sinh</h1>
        <p>Chào mừng bạn quay lại! Vui lòng đăng nhập.</p>
      </div>

      <!-- Hiển thị lỗi login -->
      <div v-if="errorMessage" class="error-alert">
        <span class="error-icon">⚠️</span>
        {{ errorMessage }}
      </div>

      <form @submit.prevent="handleLogin" class="login-form" novalidate>
        <!-- Email input -->
        <div class="input-group">
          <label for="email">Email</label>
          <div class="input-wrapper" :class="{ 'input-error': errors.email }">
            <span class="icon">✉️</span>
            <input
              id="email"
              v-model="email"
              type="email"
              placeholder="Nhập địa chỉ email của bạn"
              :disabled="loading"
              @input="errors.email = ''"
            />
          </div>
          <p v-if="errors.email" class="field-error">{{ errors.email }}</p>
        </div>

        <!-- Password input -->
        <div class="input-group">
          <label for="password">Mật khẩu</label>
          <div class="input-wrapper" :class="{ 'input-error': errors.password }">
            <span class="icon">🔒</span>
            <input
              id="password"
              v-model="password"
              :type="showPassword ? 'text' : 'password'"
              placeholder="Nhập mật khẩu của bạn"
              :disabled="loading"
              @input="errors.password = ''"
            />
            <button type="button" class="toggle-password" @click="showPassword = !showPassword">
              {{ showPassword ? '🙈' : '👁️' }}
            </button>
          </div>
          <p v-if="errors.password" class="field-error">{{ errors.password }}</p>
        </div>

        <!-- Login button -->
        <button type="submit" class="submit-btn" :disabled="loading">
          <span v-if="!loading">Đăng nhập</span>
          <span v-else class="spinner"></span>
        </button>
      </form>

      <div class="divider">
        <span>Hoặc đăng nhập bằng</span>
      </div>

      <div class="social-login">
        <div class="custom-google-wrapper">
          <span class="google-icon">G</span> Đăng nhập bằng Google
          <div class="hidden-google-btn">
            <GoogleSignInButton
              @success="handleGoogleSuccess"
              @error="handleGoogleError"
              width="340"
            />
          </div>
        </div>
      </div>

      <p class="footer-text">
        Chưa có tài khoản? <a href="#">Liên hệ Quản trị viên</a>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../store/authStore';
import { authService } from '../services/authService';
import { GoogleSignInButton } from 'vue3-google-signin';

const email = ref('');
const password = ref('');
const showPassword = ref(false);
const loading = ref(false);
const errorMessage = ref('');
const router = useRouter();
const authStore = useAuthStore();

const errors = reactive({
  email: '',
  password: '',
});

// Client-side validation
function validate() {
  let valid = true;
  errors.email = '';
  errors.password = '';

  if (!email.value.trim()) {
    errors.email = 'Email là bắt buộc.';
    valid = false;
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.value.trim())) {
    errors.email = 'Địa chỉ email không hợp lệ.';
    valid = false;
  }

  if (!password.value) {
    errors.password = 'Mật khẩu là bắt buộc.';
    valid = false;
  }

  return valid;
}

const handleLogin = async () => {
  if (loading.value) return;
  errorMessage.value = '';
  if (!validate()) return;

  loading.value = true;
  try {
    const data = await authService.login(email.value, password.value);
    authStore.setAuth(data, data.token);
    router.push('/dashboard');
  } catch (error) {
    const msg = error.response?.data?.message;
    errorMessage.value = msg || 'Sai email hoặc mật khẩu. Vui lòng thử lại.';
  } finally {
    loading.value = false;
  }
};

const handleGoogleSuccess = async (response) => {
  loading.value = true;
  errorMessage.value = '';
  try {
    const data = await authService.googleLogin(response.credential);
    authStore.setAuth(data, data.token);
    router.push('/dashboard');
  } catch (error) {
    errorMessage.value = 'Đăng nhập Google thất bại. Vui lòng thử lại.';
  } finally {
    loading.value = false;
  }
};

const handleGoogleError = () => {
  errorMessage.value = 'Lỗi xác thực Google.';
};
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap');

.login-wrapper {
  font-family: 'Inter', sans-serif;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f3f4f6;
  padding: 20px;
  box-sizing: border-box;
}

.login-box {
  background: #ffffff;
  border-radius: 12px;
  padding: 30px;
  width: 100%;
  max-width: 380px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.header {
  margin-bottom: 24px;
}

.logo-icon {
  font-size: 2.5rem;
  margin-bottom: 8px;
}

h1 {
  font-size: 1.5rem;
  font-weight: 700;
  color: #1f2937;
  margin: 0;
}

.header p {
  color: #6b7280;
  margin-top: 6px;
  font-size: 0.875rem;
}

/* Error alert */
.error-alert {
  background-color: #fef2f2;
  border: 1px solid #fecaca;
  border-radius: 8px;
  padding: 10px 14px;
  color: #dc2626;
  font-size: 0.875rem;
  text-align: left;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.error-icon {
  font-size: 1rem;
  flex-shrink: 0;
}

/* Form */
.login-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.input-group {
  text-align: left;
}

label {
  display: block;
  font-size: 0.875rem;
  font-weight: 600;
  color: #374151;
  margin-bottom: 6px;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.icon {
  position: absolute;
  left: 12px;
  font-size: 1rem;
  color: #9ca3af;
  pointer-events: none;
}

input {
  width: 100%;
  padding: 11px 40px 11px 38px;
  background: #f9fafb;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  color: #1b2559;
  font-size: 0.95rem;
  transition: all 0.2s ease;
  outline: none;
  box-sizing: border-box;
  font-family: 'Inter', sans-serif;
}

input:focus {
  background: #ffffff;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.2);
}

input::placeholder {
  color: #9ca3af;
}

.input-error input,
.input-error input:focus {
  border-color: #ef4444;
  box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.15);
}

.field-error {
  color: #dc2626;
  font-size: 0.78rem;
  margin: 5px 0 0;
}

.toggle-password {
  position: absolute;
  right: 12px;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
  font-size: 1rem;
  color: #9ca3af;
  transition: color 0.2s;
}

.toggle-password:hover {
  color: #6b7280;
}

.submit-btn {
  margin-top: 4px;
  background-color: #3b82f6;
  color: white;
  border: none;
  border-radius: 8px;
  padding: 12px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s;
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  font-family: 'Inter', sans-serif;
}

.submit-btn:hover:not(:disabled) {
  background-color: #2563eb;
}

.submit-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.divider {
  margin: 20px 0;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.divider::before {
  content: '';
  position: absolute;
  width: 100%;
  height: 1px;
  background: #e5e7eb;
}

.divider span {
  background: #ffffff;
  padding: 0 10px;
  color: #6b7280;
  font-size: 0.8rem;
  z-index: 1;
}

.social-login {
  display: flex;
  justify-content: center;
  position: relative;
  width: 100%;
}

.custom-google-wrapper {
  position: relative;
  width: 100%;
  height: 44px;
  background-color: #ef4444;
  border-radius: 8px;
  display: flex;
  justify-content: center;
  align-items: center;
  color: white;
  font-weight: 600;
  font-size: 0.95rem;
  overflow: hidden;
  transition: background-color 0.2s;
  cursor: pointer;
}

.custom-google-wrapper:hover {
  background-color: #dc2626;
}

.google-icon {
  margin-right: 8px;
  font-size: 1.1rem;
  font-weight: 800;
}

.hidden-google-btn {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  opacity: 0;
  z-index: 10;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
}

.footer-text {
  margin-top: 20px;
  color: #6b7280;
  font-size: 0.85rem;
}

.footer-text a {
  color: #3b82f6;
  text-decoration: none;
  font-weight: 600;
}

.footer-text a:hover {
  text-decoration: underline;
}

.spinner {
  width: 18px;
  height: 18px;
  border: 2px solid rgba(255, 255, 255, 0.35);
  border-radius: 50%;
  border-top-color: white;
  animation: spin 0.8s linear infinite;
  display: inline-block;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>
