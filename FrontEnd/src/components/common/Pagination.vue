<template>
  <div class="pagination-container" v-if="totalPages > 1">
    <div class="pagination-controls">
      <!-- First Page << -->
      <button 
        class="nav-btn" 
        :disabled="modelValue === 0"
        @click="$emit('update:modelValue', 0)"
        title="Trang đầu"
      >
        &laquo;
      </button>

      <!-- Previous Page < -->
      <button 
        class="nav-btn" 
        :disabled="modelValue === 0"
        @click="$emit('update:modelValue', modelValue - 1)"
        title="Trang trước"
      >
        &lsaquo;
      </button>

      <!-- Start Ellipse ... -->
      <span v-if="modelValue > 0" class="ellipsis">...</span>

      <!-- Current Page -->
      <button class="page-num active">
        {{ modelValue + 1 }}
      </button>

      <!-- End Ellipse ... -->
      <span v-if="modelValue < totalPages - 1" class="ellipsis">...</span>

      <!-- Next Page > -->
      <button 
        class="nav-btn" 
        :disabled="modelValue === totalPages - 1"
        @click="$emit('update:modelValue', modelValue + 1)"
        title="Trang sau"
      >
        &rsaquo;
      </button>

      <!-- Last Page >> -->
      <button 
        class="nav-btn" 
        :disabled="modelValue === totalPages - 1"
        @click="$emit('update:modelValue', totalPages - 1)"
        title="Trang cuối"
      >
        &raquo;
      </button>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  modelValue: {
    type: Number,
    required: true
  },
  totalPages: {
    type: Number,
    required: true
  }
});

defineEmits(['update:modelValue']);
</script>

<style scoped>
.pagination-container {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 16px;
}

.pagination-controls {
  display: flex;
  align-items: center;
  gap: 12px;
  background: white;
  padding: 8px 16px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.nav-btn {
  background: #f4f7fe;
  color: #4318ff;
  border: none;
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s;
}

.nav-btn:hover:not(:disabled) {
  background: #e9edfe;
  transform: scale(1.05);
}

.nav-btn:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

.page-num {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: 10px;
  font-weight: 800;
  font-size: 15px;
  background: #4318ff;
  color: white;
  box-shadow: 0 4px 10px rgba(67, 24, 255, 0.2);
}

.ellipsis {
  color: #a3aed0;
  font-weight: 800;
  font-size: 16px;
  letter-spacing: 2px;
}
</style>
