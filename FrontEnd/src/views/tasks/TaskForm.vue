<template>
  <div class="task-form-page">
    <div class="page-header">
      <div class="header-text">
        <button class="back-btn" @click="router.back()">
          <span>⬅️</span> Quay lại
        </button>
        <h2>{{ isEdit ? 'Chỉnh sửa Công việc' : 'Giao Công việc Mới' }}</h2>
        <p>{{ isEdit ? 'Cập nhật nội dung và hướng dẫn cho task.' : 'Phân công nhân sự và thời hạn hoàn thành.' }}</p>
      </div>
    </div>

    <div class="form-container card">
      <form @submit.prevent="handleSubmit">
        <div class="form-grid">
          <!-- Title -->
          <div class="form-group span-2">
            <label for="title">Tiêu đề công việc</label>
            <input 
              type="text" 
              id="title" 
              v-model="form.title" 
              placeholder="VD: Nghiên cứu tài liệu React..." 
              required
            />
          </div>

          <!-- Description -->
          <div class="form-group span-2">
            <label for="description">Mô tả chi tiết</label>
            <textarea 
              id="description" 
              v-model="form.description" 
              rows="5" 
              placeholder="Yêu cầu cụ thể, tài liệu tham khảo..."
            ></textarea>
          </div>

          <!-- Intern -->
          <div class="form-group">
            <label for="intern">Thực tập sinh phụ trách</label>
            <select id="intern" v-model="form.internId" required>
              <option value="">Chọn thực tập sinh</option>
              <option v-for="intern in interns" :key="intern.id" :value="intern.id">
                {{ intern.name }}
              </option>
            </select>
          </div>

          <!-- Mentor -->
          <div class="form-group">
            <label for="mentor">Người hướng dẫn</label>
            <select id="mentor" v-model="form.mentorId" required>
              <option value="">Chọn người hướng dẫn</option>
              <option v-for="mentor in mentors" :key="mentor.id" :value="mentor.id">
                {{ mentor.name }}
              </option>
            </select>
          </div>

          <!-- Priority -->
          <div class="form-group">
            <label for="priority">Mức độ ưu tiên</label>
            <select id="priority" v-model="form.priority" required>
              <option value="HIGH">Cao</option>
              <option value="MEDIUM">Trung bình</option>
              <option value="LOW">Thấp</option>
            </select>
          </div>

          <!-- Due Date -->
          <div class="form-group">
            <label for="deadline">Hạn chót</label>
            <input 
              type="datetime-local" 
              id="deadline" 
              v-model="form.deadline" 
              required
            />
          </div>
        </div>

        <div class="form-actions">
          <button type="button" class="cancel-btn" @click="router.back()">Hủy bỏ</button>
          <button type="submit" class="submit-btn" :disabled="submitting">
            {{ submitting ? 'Đang lưu...' : (isEdit ? 'Cập nhật task' : 'Giao việc ngay') }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { taskService } from '../../services/taskService';
import { internService } from '../../services/internService';
import { mentorService } from '../../services/mentorService';

const route = useRoute();
const router = useRouter();
const isEdit = computed(() => !!route.params.id);

const form = reactive({
  title: '',
  description: '',
  status: 'PENDING',
  priority: 'MEDIUM',
  deadline: '',
  internId: '',
  mentorId: ''
});

const interns = ref([]);
const mentors = ref([]);
const submitting = ref(false);

const fetchData = async () => {
  try {
    const [internData, mentorData] = await Promise.all([
      internService.getAllInterns({ size: 100 }),
      mentorService.getAllMentors({ size: 100 })
    ]);
    interns.value = internData.content;
    mentors.value = mentorData.content;

    if (isEdit.value) {
      const task = await taskService.getTaskById(route.params.id);
      Object.assign(form, {
        ...task,
        // Convert ISO date to datetime-local format (YYYY-MM-DDThh:mm)
        deadline: task.deadline ? new Date(task.deadline).toISOString().slice(0, 16) : ''
      });
    }
  } catch (error) {
    console.error("Lỗi khi tải dữ liệu:", error);
  }
};

const handleSubmit = async () => {
  submitting.value = true;
  try {
    const submitData = {
      ...form,
      internId: form.internId || null,
      mentorId: form.mentorId || null
    };

    if (isEdit.value) {
      await taskService.updateTask(route.params.id, submitData);
    } else {
      await taskService.createTask(submitData);
    }
    router.push('/tasks');
  } catch (error) {
    alert("Không thể lưu thông tin. Vui lòng kiểm tra lại!");
  } finally {
    submitting.value = false;
  }
};

onMounted(fetchData);
</script>

<style scoped>
.task-form-page {
  max-width: 900px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 32px;
}

.back-btn {
  background: none;
  border: none;
  color: #707eae;
  font-weight: 700;
  cursor: pointer;
  padding: 0;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-text h2 {
  font-size: 24px;
  font-weight: 800;
  color: #1b2559;
  margin: 0;
}

.header-text p {
  color: #4a5568;
  margin-top: 4px;
  font-weight: 500;
}

.card {
  background: white;
  border-radius: 20px;
  padding: 32px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
}

.span-2 {
  grid-column: span 2;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

label {
  font-size: 14px;
  font-weight: 700;
  color: #1b2559;
}

input, select, textarea {
  background: #f4f7fe;
  border: 1px solid #e2e8f0;
  padding: 12px 16px;
  border-radius: 14px;
  color: #1b2559;
  font-weight: 600;
  font-family: inherit;
  outline: none;
  transition: all 0.2s;
}

input:focus, select:focus, textarea:focus {
  border-color: #4318ff;
  background: white;
}

textarea {
  resize: vertical;
}

option {
  color: #1b2559;
  background: white;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  margin-top: 40px;
  padding-top: 24px;
  border-top: 1px solid #f4f7fe;
}

.cancel-btn {
  background: #f4f7fe;
  color: #1b2559;
  border: none;
  padding: 12px 24px;
  border-radius: 12px;
  font-weight: 700;
  cursor: pointer;
}

.submit-btn {
  background: #4318ff;
  color: white;
  border: none;
  padding: 12px 32px;
  border-radius: 12px;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 4px 14px rgba(67, 24, 255, 0.2);
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
