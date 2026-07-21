<template>
  <button class="back-button" :class="absoluteClass" @click="go" :title="title">
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
      <polyline points="15 18 9 12 15 6"></polyline>
    </svg>
    <span>{{ text }}</span>
  </button>
</template>

<script>
export default {
  name: 'BackButton',
  props: {
    to: { type: [String, Object], default: '/first' },
    text: { type: String, default: '返回首页' },
    title: { type: String, default: '返回首页' },
    absolute: { type: Boolean, default: false },
    placement: { type: String, default: 'right', validator: v => ['left', 'right'].includes(v) }
  },
  computed: {
    absoluteClass() {
      if (!this.absolute) return ''
      return this.placement === 'left' ? 'absolute-left' : 'absolute-right'
    }
  },
  methods: {
    go() {
      this.$router.push(this.to)
    }
  }
}
</script>

<style scoped>
.back-button {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: linear-gradient(135deg, #1e5ca8, #3949ab);
  color: #ffffff;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(30, 92, 168, 0.3);
}

.back-button:hover {
  background: linear-gradient(135deg, #1a4d91, #2c3a99);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(30, 92, 168, 0.4);
}

.back-button:active {
  transform: translateY(0);
  box-shadow: 0 2px 6px rgba(30, 92, 168, 0.3);
}

.absolute-right {
  position: absolute;
  top: 20px;
  right: 20px;
  z-index: 100;
}

.absolute-left {
  position: absolute;
  top: 20px;
  left: 20px;
  z-index: 100;
}
</style>