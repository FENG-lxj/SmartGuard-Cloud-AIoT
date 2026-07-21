<template>
  <div :class="svgClass" v-if="iconName" :style="containerStyle">
    <img :src="iconUrl" :alt="iconName" class="svg-icon">
  </div>
</template>

<script>
export default {
  name: 'SvgIcon',
  props: {
    iconName: {
      type: String,
      required: true
    },
    className: {
      type: String,
      default: ''
    },
    size: {
      type: [Number, String],
      default: 35
    }
  },
  computed: {
    iconUrl() {
      // 直接返回SVG图标的路径
      const iconMap = {
        'ai对话': require('./ai对话.svg'),
        '告警消息': require('./告警消息.svg'),
        '安全监控': require('./安全监控.svg'),
        '数据分析': require('./数据分析.svg'),
        '设备': require('./设备.svg'),
        '3D视图': require('./3D视图.svg')
      }
      return iconMap[this.iconName] || ''
    },
    svgClass() {
      return [
        'svg-icon-container',
        this.className
      ].filter(Boolean).join(' ');
    },
    containerStyle() {
      // 使用传入的size属性设置容器大小
      if (this.size) {
        return {
          width: `${this.size}px`,
          height: `${this.size}px`
        }
      }
      return {}
    }
  }
}
</script>

<style scoped>
.svg-icon-container {
  display: flex;
  align-items: center;
  justify-content: center;
}

.svg-icon {
  width: 100%;
  height: 100%;
  object-fit: contain;
  display: block;
  fill: currentColor;
}
</style>