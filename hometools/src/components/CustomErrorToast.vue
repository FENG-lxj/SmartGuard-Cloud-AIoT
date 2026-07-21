<template>
  <div v-if="visible" class="custom-error-toast" :class="type">
    <div class="error-content">
      <span>{{ message }}</span>
      <button class="close-btn" @click="close">×</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const props = defineProps({
  message: { type: String, required: true },
  duration: { type: Number, default: 5000 },
  type: { type: String, default: 'error', validator: v => ['error', 'success'].includes(v) }
})

const emit = defineEmits(['close'])
const visible = ref(true)
let timer = null

onMounted(() => {
  if (props.duration > 0) {
    timer = setTimeout(close, props.duration)
  }
})

onUnmounted(() => {
  if (timer) {
    clearTimeout(timer)
    timer = null
  }
})

function close() {
  const toastElement = document.querySelector('.custom-error-toast')
  if (toastElement) {
    toastElement.classList.add('fade-out')
  }
  setTimeout(() => {
    visible.value = false
    emit('close')
  }, 300)
}
</script>

<style scoped>
.custom-error-toast {
  position: fixed;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 9999;
  padding: 16px 24px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  display: flex;
  justify-content: center;
  animation: fadeIn 0.3s ease-out;
}

.custom-error-toast.error {
  border: 1px solid #f43f5e;
}

.custom-error-toast.error .error-content span {
  color: #f43f5e;
}

.custom-error-toast.success {
  border: 1px solid #22c55e;
}

.custom-error-toast.success .error-content span {
  color: #22c55e;
}

.error-content {
  max-width: 300px;
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.error-content span {
  font-size: 14px;
  font-weight: 500;
}

.close-btn {
  background: transparent;
  border: none;
  color: inherit;
  font-size: 18px;
  cursor: pointer;
  padding: 0 5px;
  line-height: 1;
}

.close-btn:hover {
  opacity: 0.7;
}

.fade-out {
  animation: fadeOut 0.3s ease-in;
  opacity: 0;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateX(-50%) translateY(-20px) scale(0.9);
  }
  to {
    opacity: 1;
    transform: translateX(-50%) translateY(0) scale(1);
  }
}

@keyframes fadeOut {
  from {
    opacity: 1;
  }
  to {
    opacity: 0;
  }
}
</style>