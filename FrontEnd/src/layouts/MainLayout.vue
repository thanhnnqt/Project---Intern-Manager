<template>
  <div class="layout-container">
    <aside class="sidebar">
      <div class="sidebar-header">
        <span class="logo-icon">🏢</span>
        <h2>Intern Manager</h2>
      </div>
      <nav class="sidebar-nav">
        <router-link to="/dashboard" class="nav-item">
          <span class="icon">📊</span> Dashboard
        </router-link>
        <router-link v-if="user?.role !== 'USER'" to="/interns" class="nav-item">
          <span class="icon">👥</span> Thực tập sinh
        </router-link>
        <router-link v-if="user?.role !== 'USER'" to="/mentors" class="nav-item">
          <span class="icon">👨‍🏫</span> Người hướng dẫn
        </router-link>
        <router-link to="/tasks" class="nav-item">
          <span class="icon">📋</span> Công việc
        </router-link>
        <router-link to="/profile" class="nav-item">
          <span class="icon">👤</span> Cá nhân
        </router-link>
        <router-link to="/notifications" class="nav-item">
          <span class="icon">🔔</span> Thông báo
        </router-link>
      </nav>
      <div class="sidebar-footer">
        <button @click="handleLogout" class="logout-btn">
          <span class="icon">🚪</span> Đăng xuất
        </button>
      </div>
    </aside>

    <main class="main-content">
      <header class="top-bar">
        <div class="breadcrumb">{{ currentRouteName }}</div>
        <div class="user-profile" v-if="user">
          <div class="user-info">
            <span class="user-name">{{ user.fullName || user.username }}</span>
            <span class="user-role">{{ user.role }}</span>
          </div>
          <img :src="user.avatar || 'https://ui-avatars.com/api/?name=' + (user.fullName || 'User')" class="avatar" />
        </div>
      </header>

      <div class="page-content">
        <router-view />
      </div>
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useAuthStore } from '../store/authStore';

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();
const user = computed(() => authStore.user);

const currentRouteName = computed(() => {
  return route.name || 'Trang chủ';
});

const handleLogout = () => {
  authStore.logout();
  router.push('/login');
};
</script>

<style scoped>
.layout-container {
  display: flex;
  height: 100vh;
  overflow: hidden;
  background-color: #f4f7fe;
  color: #2b3674;
}

/* Sidebar */
.sidebar {
  width: 280px;
  background: white;
  display: flex;
  flex-direction: column;
  border-right: 1px solid #e2e8f0;
  position: sticky;
  top: 0;
  height: 100vh;
  z-index: 100;
}

.sidebar-header {
  padding: 32px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.sidebar-header h2 {
  font-size: 24px;
  font-weight: 700;
  margin: 0;
  color: #1b2559;
}

.logo-icon {
  font-size: 24px;
}

.sidebar-nav {
  flex: 1;
  padding: 0 16px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  text-decoration: none;
  color: #707eae;
  font-weight: 700;
  border-radius: 12px;
  margin-bottom: 4px;
  transition: all 0.3s;
}

.nav-item:hover, .nav-item.router-link-active {
  background-color: #f4f7fe;
  color: #4318ff;
}

.nav-item.router-link-active .icon {
  color: #4318ff;
}

.sidebar-footer {
  padding: 32px 16px;
}

.logout-btn {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: none;
  border: none;
  color: #ff5b5b;
  font-weight: 700;
  cursor: pointer;
  border-radius: 12px;
  transition: background 0.3s;
}

.logout-btn:hover {
  background: #fff5f5;
}

/* Main Content */
.main-content {
  flex: 1;
  padding: 16px 24px;
  overflow-y: scroll;
  scrollbar-width: none; /* Firefox */
  -ms-overflow-style: none; /* IE/Edge */
}

.main-content::-webkit-scrollbar {
  display: none; /* Chrome/Safari */
}

.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.breadcrumb {
  color: #1b2559;
  font-weight: 700;
  text-transform: capitalize;
  opacity: 0.8;
}

.user-profile {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-info {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.user-name {
  font-weight: 700;
  color: #1b2559;
}

.user-role {
  font-size: 12px;
  color: #707eae;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  background-color: #e2e8f0;
  object-fit: cover;
}

.page-content {
  /* No min-height needed, layout handles scrolling */
}
</style>
