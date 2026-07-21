<template>
  <span class="header-time" :class="{ inline }">
    <i class="iconfont icon-shijian"></i>
    <span class="time">{{ currentTime }}</span>
    <span class="date">{{ currentDate }}</span>
  </span>
</template>

<script>
export default {
  name: 'HeaderTime',
  props: {
    inline: { type: Boolean, default: false }
  },
  data() {
    return {
      currentTime: '',
      currentDate: '',
      timeTimer: null
    }
  },
  created() {
    this.updateTime()
    this.timeTimer = setInterval(this.updateTime, 1000)
  },
  beforeUnmount() {
    if (this.timeTimer) {
      clearInterval(this.timeTimer)
      this.timeTimer = null
    }
  },
  methods: {
    updateTime() {
      const now = new Date()
      const pad = (n) => String(n).padStart(2, '0')
      const weekMap = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
      this.currentTime = `${pad(now.getHours())}:${pad(now.getMinutes())}:${pad(now.getSeconds())}`
      this.currentDate = `${now.getFullYear()}年${pad(now.getMonth() + 1)}月${pad(now.getDate())}日 ${weekMap[now.getDay()]}`
    }
  }
}
</script>

<style scoped>
.header-time {
  position: absolute;
  top: 20px;
  left: 20px;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: linear-gradient(to right, #FFF8E8, #f5f0e1);
  color: #1a3a6e;
  border: 2px solid #D4AF37;
  border-radius: 20px;
  padding: 10px 14px;
  box-shadow: 0 4px 10px rgba(58, 95, 11, 0.2);
  z-index: 100;
  white-space: nowrap;
  font-variant-numeric: tabular-nums;
}

.header-time .iconfont {
  font-size: 18px;
  color: #1E50B5;
}

.header-time .time {
  font-size: 18px;
  font-weight: bold;
  color: #1a3a6e;
  letter-spacing: 1px;
  line-height: 1.2;
}

.header-time .date {
  font-size: 12px;
  color: #3A5F0B;
  opacity: 0.9;
  line-height: 1.2;
}

.header-time.inline {
  position: static;
  display: inline-flex;
  align-items: center;
  background: linear-gradient(135deg, #fff8e1, #f3efe2);
  border: 1px solid rgba(212, 175, 55, 0.6);
  box-shadow: 0 2px 6px rgba(30, 92, 168, 0.15);
  padding: 6px 10px;
  border-radius: 16px;
}

.header-time.inline .iconfont {
  font-size: 16px;
  color: #1E50B5;
}

.header-time.inline .time {
  font-size: 16px;
}

.header-time.inline .date {
  font-size: 12px;
}
</style>