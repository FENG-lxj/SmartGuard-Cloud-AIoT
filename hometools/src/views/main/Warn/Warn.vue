<template>
  <div class="Warn-index">
    <!-- 顶部导航 -->
    <div class="top-navigation">
      <div class="header-left">
        <BackButton :to="'/first'" />
        <h1 class="page-title">
          <i class="iconfont icon-warning"></i>
          家庭环境预警系统
        </h1>
      </div>
      <div class="header-actions">
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 左侧区域 -->
      <div class="left-section">
        <!-- 实时数据卡片 -->
        <div class="card card-shadow">
          <div class="card-header">
            <h3 class="title">实时环境数据</h3>
          </div>
          <div class="card-body">
            <div class="data-grid">
              <!-- 温度 -->
              <div 
                class="data-item" 
                :class="getTemperatureClass(wendu)"
                :title="`当前温度: ${wendu}°C`"
              >
                <div class="status-indicator" :class="getStatusLevel(wendu)"></div>
                <div class="data-icon temperature-icon">
                  <i class="iconfont icon-temperature-high"></i>
                </div>
                <div class="data-name">温度</div>
                <div class="data-value">{{ wendu }}°C</div>
              </div>
              
              <!-- 湿度 -->
              <div 
                class="data-item" 
                :class="getHumidityClass(shidu)"
                :title="`当前湿度: ${shidu}%`"
              >
                <div class="status-indicator" :class="getStatusLevel(shidu)"></div>
                <div class="data-icon humidity-icon">
                  <i class="iconfont icon-tint"></i>
                </div>
                <div class="data-name">湿度</div>
                <div class="data-value">{{ shidu }}%</div>
              </div>
              
              <!-- 光线 -->
              <div 
                class="data-item" 
                :class="getLightClass(guangxian)"
                :title="`当前光线强度: ${guangxian} lx`"
              >
                <div class="status-indicator" :class="getStatusLevel(guangxian)"></div>
                <div class="data-icon light-icon">
                  <i class="iconfont icon-sun-o"></i>
                </div>
                <div class="data-name">光线</div>
                <div class="data-value">{{ guangxian }} lx</div>
              </div>
              
              <!-- 空气质量 -->
              <div 
                class="data-item" 
                :class="getAirQualityClass(kongqi)"
                :title="`当前空气质量: ${kongqi} AQI`"
              >
                <div class="status-indicator" :class="getStatusLevel(kongqi)"></div>
                <div class="data-icon air-icon">
                  <i class="iconfont icon-cloud"></i>
                </div>
                <div class="data-name">空气质量</div>
                <div class="data-value">{{ kongqi }} AQI</div>
              </div>
              
              <!-- 毒气 -->
              <div 
                class="data-item" 
                :class="getGasClass(youdu)"
                :title="`当前毒气含量: ${youdu} ppm`"
              >
                <div class="status-indicator" :class="getStatusLevel(youdu)"></div>
                <div class="data-icon gas-icon">
                  <i class="iconfont icon-warning-sign"></i>
                </div>
                <div class="data-name">毒气含量</div>
                <div class="data-value">{{ youdu }} ppm</div>
              </div>
              
              <!-- 地震 -->
              <div 
                class="data-item" 
                :class="getEarthquakeClass(dizhen)"
                :title="`当前地震指数: ${dizhen} 级`"
              >
                <div class="status-indicator" :class="getStatusLevel(dizhen)"></div>
                <div class="data-icon earthquake-icon">
                  <i class="iconfont icon-exclamation-triangle"></i>
                </div>
                <div class="data-name">地震指数</div>
                <div class="data-value">{{ dizhen }} 级</div>
              </div>
              
              <!-- 火灾 -->
              <div 
                class="data-item" 
                :class="getFireClass(huozai)"
                :title="`当前火灾指数: ${huozai} 级`"
              >
                <div class="status-indicator" :class="getStatusLevel(huozai)"></div>
                <div class="data-icon fire-icon">
                  <i class="iconfont icon-fire"></i>
                </div>
                <div class="data-name">火灾指数</div>
                <div class="data-value">{{ huozai }} 级</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 天气预警卡片 -->
        <div class="card card-shadow">
          <div class="card-header">
            <h3 class="title">今日天气预报</h3>
          </div>
          <div class="card-body">
            <div class="weather-container">
              <div 
                v-for="(item, index) in weather" 
                :key="index" 
                class="weather-item"
                :title="`${item.time} ${item.tianqi} ${item.wendu}`"
              >
                <div class="weather-time">{{ item.time }}</div>
                <div class="weather-icon-wrapper">
                  <i class="iconfont" :class="[selectWeather(item.tianqi), getWeatherIconColor(item.tianqi)]"></i>
                </div>
                <div class="weather-condition">{{ item.tianqi }}</div>
                <div class="weather-temp">{{ item.wendu }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 中间区域 -->
      <div class="center-section">
        <!-- 预警统计图表卡片 -->
        <div class="card card-shadow">
          <div class="card-header">
            <h3 class="title">预警统计分析</h3>
          </div>
          <div class="card-body">
            <div class="chart-container">
              <canvas ref="myChart"></canvas>
            </div>
          </div>
        </div>

        <!-- 搜索和AI建议卡片 -->
        <div class="card card-shadow">
          <div class="card-header">
            <h3 class="title">智能环境建议</h3>
          </div>
          <div class="card-body">
            <div class="search-container">
              <input 
                v-model="searchInput" 
                type="text" 
                class="search-input" 
                placeholder="输入您的环境问题..."
              />
              <button class="ai-button" @click="serchAI">
                <i class="iconfont icon-robot"></i>
                AI搜索建议
              </button>
            </div>
            <div class="suggestion-box">
              <div class="suggestion-content" v-if="advance">
                {{ advance }}
              </div>
              <div class="suggestion-placeholder" v-else>
                输入问题获取AI智能建议
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧区域 -->
      <div class="right-section">
        <!-- 灾难预警卡片 -->
        <div class="card card-shadow danger-card">
          <div class="card-header danger-header">
            <h3 class="title">
              <i class="iconfont icon-exclamation-triangle"></i>
              灾难预警
            </h3>
          </div>
          <div class="card-body">
            <div class="warning-list" v-if="disaster.length > 0">
              <div 
                v-for="(item, index) in disaster" 
                :key="index" 
                class="warning-item animated-fade-in"
                :data-index="index"
              >
                <i class="iconfont icon-exclamation-circle warning-icon"></i>
                <span>{{ item }}</span>
              </div>
            </div>
            <div class="no-warning" v-else>
              <i class="iconfont icon-check-circle success-icon"></i>
              当前无灾难预警信息
            </div>
          </div>
        </div>

        <!-- 环境预警卡片 -->
        <div class="card card-shadow">
          <div class="card-header">
            <h3 class="title">
              <i class="iconfont icon-attention-circle"></i>
              环境预警
            </h3>
          </div>
          <div class="card-body">
            <div class="environment-list" v-if="envir.length > 0">
              <div 
                v-for="(item, index) in envir" 
                :key="index" 
                class="environment-item animated-fade-in"
                :data-index="index"
              >
                <i class="iconfont icon-warning environment-icon"></i>
                <span>{{ item }}</span>
              </div>
            </div>
            <div class="no-warning" v-else>
              <i class="iconfont icon-check-circle success-icon"></i>
              当前无环境预警信息
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Chart from 'chart.js/auto'
import BackButton from '@/components/BackButton.vue'

export default {
  name: 'WarnIndex',
  components: { BackButton },
  data () {
    return {
      searchInput: '',
      disaster: [
        '地震指数为1级，请提前做好防警。',
        'fired指数为2级，请提前做好防警。',
        '地震指数为1级，请提前做好防警。'
      ], // 地震fired预警
      // 本地数据缓存，避免直接访问store，设置默认值以确保页面正常显示
      wendu: 28,
      shidu: 45,
      guangxian: 300,
      kongqi: 31,
      youdu: 0.5,
      dizhen: 1,
      huozai: 2,
      weather: [
        { time: '上午', tianqi: '晴天', wendu: '28°C' },
        { time: '中午', tianqi: '多云', wendu: '30°C' },
        { time: '下午', tianqi: '阴天', wendu: '27°C' },
        { time: '晚上', tianqi: '多云', wendu: '25°C' }
      ],
      
      envir: [
        '当前温度较高。',
        '当前湿度适中。',
        '空气质量良好',
        '光线强度适中'
      ], // 环境预警
      warn: {
        wenduWarn: 0,
        shiduWarn: 0,
        guangxianWarn: 0,
        kongqiWarn: 0,
        youduWarn: 0,
        dizhenWarn: 0,
        huozaiWarn: 0
      }, // 预警次数
      advance: '使用智能温控器自动调节空调/暖气，避免温差过大。冬季可通过厚窗帘、地毯减少热量流失；夏季用遮光帘反射阳光。室内外温差建议控制在5-8℃内，避免感冒。潮湿时：用除湿机或空调除湿功能，卫生间安装排风扇，避免积水。监测：配备湿度计（如电子温湿度传感器）。阅读区建议300-500勒克斯（lx），可用护眼台灯（无频闪）。避免直射强光，使用漫反射灯具或灯罩。空气净化器（选HEPA滤芯+活性炭吸附型号）。绿植辅助（如龟背竹、绿萝，但效果有限）。', // Ai建议
      myChart: null
    }
  },
  created () {
    // 组件创建时安全地获取store数据
    this.updateStoreData()
  },
  mounted() {
    // 在组件挂载后初始化图表
    this.initChart()
    // 添加滚动动画
    this.setupScrollAnimations()
  },
  beforeUnmount() {
    // 在组件卸载前销毁图表实例，避免内存泄漏
    if (this.myChart) {
      this.myChart.destroy()
    }
    
    // 在组件卸载时移除滚动事件监听
    if (this._animateOnScroll) {
      window.removeEventListener('scroll', this._animateOnScroll)
      this._animateOnScroll = null
    }
  },
  
  // 监听store数据变化，及时更新本地数据
  watch: {
    '$store.state.data': {
      handler () {
        if (this.$store && this.$store.state) {
          this.updateStoreData()
        }
      },
      deep: true
    },
    
    '$store.state.weather': {
      handler () {
        if (this.$store && this.$store.state && this.$store.state.weather) {
          this.weather = this.$store.state.weather || []
        }
      },
      deep: true
    }
  },
  methods: {
    // 返回首页
    goHome() {
      this.$router.push('/first')
    },
    
    // 设置滚动动画
    setupScrollAnimations() {
      const animateOnScroll = () => {
        const elements = document.querySelectorAll('.animated-fade-in')
        elements.forEach((element) => {
          const rect = element.getBoundingClientRect()
          const isVisible = (rect.top <= window.innerHeight - 100)
          if (isVisible) {
            element.style.opacity = '1'
            element.style.transform = 'translateY(0)'
          }
        })
      }
      
      // 初始检查
      animateOnScroll()
      // 添加滚动事件监听
      window.addEventListener('scroll', animateOnScroll)
      
      // 保存事件处理函数引用，以便在组件卸载时移除
      this._animateOnScroll = animateOnScroll
    },
    
    // 初始化图表
    initChart() {
      if (this.$refs.myChart) {
        try {
          // 获取canvas上下文
          const ctx = this.$refs.myChart.getContext('2d')
          
          // 销毁已存在的图表实例
          if (this.myChart) {
            this.myChart.destroy()
          }
          
          // 创建图表实例
          this.myChart = new Chart(ctx, {
            type: 'pie',
            data: {
              labels: ['温度', '湿度', '光线强度', '毒气含量', '地震', 'fired', '空气质量'],
              datasets: [{
                data: [1, 7, 5, 4, 5, 4, 3],
                backgroundColor: [
                  '#FF6384',
                  '#36A2EB',
                  '#FFCE56',
                  '#4BC0C0',
                  '#9966FF',
                  '#FF9F40',
                  '#8AC14A'
                ],
                borderWidth: 2,
                borderColor: '#ffffff',
                hoverBorderWidth: 4,
                hoverBackgroundColor: '#ffffff',
                hoverBorderColor: '#333333'
              }]
            },
            options: {
              responsive: true,
              maintainAspectRatio: false,
              animation: {
                animateRotate: true,
                animateScale: true,
                duration: 2000,
                easing: 'easeOutQuart'
              },
              plugins: {
                legend: {
                  position: 'right',
                  labels: {
                    font: {
                      size: 13,
                      family: 'Segoe UI, Tahoma, Geneva, Verdana, sans-serif'
                    },
                    padding: 20,
                    usePointStyle: true,
                    pointStyle: 'circle'
                  }
                },
                tooltip: {
                  backgroundColor: 'rgba(0, 0, 0, 0.8)',
                  titleFont: {
                    size: 14,
                    weight: 'bold'
                  },
                  bodyFont: {
                    size: 13
                  },
                  padding: 12,
                  cornerRadius: 8,
                  displayColors: true,
                  callbacks: {
                    label: function(context) {
                      const label = context.label || ''
                      const value = context.parsed || 0
                      const total = context.dataset.data.reduce((a, b) => a + b, 0)
                      const percentage = Math.round((value / total) * 100)
                      return `${label}: ${value} (${percentage}%)`
                    }
                  }
                },
                title: {
                  display: true,
                  text: '今日预警分布统计',
                  font: {
                    size: 18,
                    weight: 'bold',
                    family: 'Segoe UI, Tahoma, Geneva, Verdana, sans-serif'
                  },
                  padding: {
                    bottom: 20
                  },
                  color: '#333333'
                }
              }
            }
          })
        } catch (error) {
          console.error('图表初始化失败:', error)
        }
      }
    },
    
    // 获取状态级别
    getStatusLevel(value) {
      if (value >= 7) return 'critical'
      if (value >= 5) return 'high'
      if (value >= 3) return 'medium'
      if (value > 0) return 'low'
      return 'safe'
    },
    
    // 温度样式类
    getTemperatureClass(temp) {
      if (temp > 35) return 'temp-critical'
      if (temp > 30) return 'temp-high'
      if (temp > 25) return 'temp-medium'
      if (temp > 15) return 'temp-safe'
      return 'temp-low'
    },
    
    // 光线样式类
    getLightClass(light) {
      if (light > 1000) return 'light-high'
      if (light > 500) return 'light-medium'
      if (light > 100) return 'light-safe'
      return 'light-low'
    },
    
    // 毒气样式类
    getGasClass(gas) {
      if (gas > 2) return 'gas-critical'
      if (gas > 1) return 'gas-high'
      return 'gas-safe'
    },
    
    // 湿度样式类
    getHumidityClass(humidity) {
      if (humidity > 80) return 'humidity-high'
      if (humidity > 60) return 'humidity-medium'
      if (humidity > 30) return 'humidity-safe'
      return 'humidity-low'
    },
    
    // 地震样式类
    getEarthquakeClass(quake) {
      if (quake > 7) return 'quake-critical'
      if (quake > 5) return 'quake-high'
      if (quake > 3) return 'quake-medium'
      return 'quake-safe'
    },
    
    // 火灾样式类
    getFireClass(fire) {
      if (fire > 3) return 'fire-critical'
      if (fire > 1) return 'fire-high'
      return 'fire-safe'
    },
    
    // 空气质量样式类
    getAirQualityClass(air) {
      if (air > 200) return 'air-critical'
      if (air > 100) return 'air-high'
      if (air > 50) return 'air-medium'
      return 'air-safe'
    },
    
    // 获取天气图标颜色
    getWeatherIconColor(tianqi) {
      switch (tianqi) {
        case '晴天':
          return 'weather-sunny'
        case '多云':
          return 'weather-cloudy'
        case '阴天':
          return 'weather-cloudy'
        case '下雨':
          return 'weather-rainy'
        case '雪':
          return 'weather-snowy'
        default:
          return 'weather-sunny'
      }
    },
    
    // 安全地从store获取数据并更新本地变量
    updateStoreData () {
      if (this.$store && this.$store.state) {
        // 更新环境数据
        if (this.$store.state.data) {
          this.wendu = this.$store.state.data.wendu || 0
          this.shidu = this.$store.state.data.shidu || 0
          this.guangxian = this.$store.state.data.guangxian || 0
          this.kongqi = this.$store.state.data.kongqi || 0
          this.youdu = this.$store.state.data.youdu || 0
          this.dizhen = this.$store.state.data.dizhen || 0
          this.huozai = this.$store.state.data.huozai || 0
          
          // 判断预警条件
          this.checkWarnings()
        }
        
        // 更新天气数据
        if (this.$store.state.weather) {
          this.weather = this.$store.state.weather || []
        }
        
        // 如果有数据，提交warn到store
        if (this.warn) {
          this.$store.commit('setWarn', this.warn)
        }
      }
    },
    
    // 检查预警条件
    checkWarnings () {
      // 清空现有预警，避免重复添加
      this.disaster = []
      this.envir = []
      
      // 判断地震指数大于0就加入到灾难预警中
      if (parseInt(this.dizhen) > 0) {
        this.warn.dizhenWarn++
        this.disaster.push(`地震指数为${this.dizhen}级，请提前做好防警。`)
      }
      // 判断fired指数大于0就加入到灾难预警中
      if (parseInt(this.huozai) > 0) {
        this.warn.huozaiWarn++
        this.disaster.push(`fired指数为${this.huozai}级，请提前做好防警。`)
      }
      
      // 环境预警判断
      if (parseInt(this.wendu) > 30) {
        this.warn.wenduWarn++
        this.envir.push('当前温度过高')
      } else if (parseInt(this.wendu) < 10) {
        this.warn.wenduWarn++
        this.envir.push('当前温度过低')
      } else {
        this.envir.push('当前温度适宜')
      }
      
      if (parseInt(this.shidu) > 80) {
        this.warn.shiduWarn++
        this.envir.push('当前空气过于潮湿')
      } else if (parseInt(this.shidu) < 30) {
        this.warn.shiduWarn++
        this.envir.push('当前空气过于干燥')
      } else {
        this.envir.push('当前湿度适中')
      }
      
      if (parseInt(this.kongqi) > 200) {
        this.warn.kongqiWarn++
        this.envir.push('当前空气质量较差')
      } else if (parseInt(this.kongqi) > 100) {
        this.envir.push('当前空气质量一般')
      } else {
        this.envir.push('当前空气质量良好')
      }
      
      if (parseInt(this.guangxian) > 1000) {
        this.warn.guangxianWarn++
        this.envir.push('当前光线强度过高')
      } else if (parseInt(this.guangxian) < 100) {
        this.warn.guangxianWarn++
        this.envir.push('当前光线强度过低')
      } else {
        this.envir.push('光线强度适中')
      }
      
      if (parseFloat(this.youdu) > 1) {
        this.warn.youduWarn++
        this.envir.push('当前有毒气体含量过高，请注意通风')
      }
    },
    
    openWarning () {
      this.$message({
        showClose: true,
        message: '家中有异常!',
        type: 'warning',
        customClass: 'red-warning-message', // 自定义类名用于覆盖默认样式
        duration: 0 // 设置为 0 表示消息框持续展示
      })
    },
    
    serchAI () {
      // 修改Vuex中的输入框问题，方便传入AI
      if (this.$store) {
        this.$store.commit('setInputValue', this.searchInput || '环境优化建议')
        console.log(this.$store.state && this.$store.state.AIPro ? this.$store.state.AIPro : 'AIPro not available')
      }
      console.log(this.searchInput)
      this.$router.push('/AI')
    },
    
    selectWeather (tianqi) {
      switch (tianqi) {
        case '晴天':
          return 'icon-qing'
        case '多云':
          return 'icon-duoyun'
        case '阴天':
          return 'icon-yintian'
        case '下雨':
          return 'icon-xiayu'
        case '雪':
          return 'icon-xue'
        default:
          return 'icon-qing'
      }
    }
  }
}
</script>

<style scoped>
/* 全局样式重置和基础设置 */
* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

/* 主容器样式 */
.Warn-index {
  min-height: 100vh;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  color: #1e293b;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  padding: 2rem;
  position: relative;
  overflow-x: hidden;
  /* 添加高级背景装饰 */
  background-image: 
    radial-gradient(circle at 20% 20%, rgba(76, 81, 191, 0.05) 0%, transparent 25%),
    radial-gradient(circle at 80% 80%, rgba(72, 187, 120, 0.05) 0%, transparent 25%),
    radial-gradient(circle at 40% 60%, rgba(239, 68, 68, 0.05) 0%, transparent 30%),
    /* 添加动态粒子效果的模拟 */
    linear-gradient(135deg, rgba(255, 255, 255, 0.2) 0%, transparent 100%);
  background-attachment: fixed;
}

/* 添加模糊的背景圆形装饰 */
.Warn-index::before {
  content: '';
  position: fixed;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle at center, rgba(76, 81, 191, 0.08) 0%, transparent 60%);
  animation: floatBackground 15s ease-in-out infinite;
  z-index: 0;
  pointer-events: none;
}

/* 添加渐变边框动画效果 */
@keyframes borderGradient {
  0% { border-color: rgba(76, 81, 191, 0.2); }
  50% { border-color: rgba(72, 187, 120, 0.2); }
  100% { border-color: rgba(76, 81, 191, 0.2); }
}

.card {
  animation: borderGradient 5s infinite ease-in-out;
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(203, 213, 225, 0.5);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

/* 增强数据项的视觉效果 */
.data-item {
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.data-item:hover {
  transform: translateY(-5px) translateZ(10px);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

.data-value {
  transition: all 0.3s ease;
}

.data-item:hover .data-value {
  transform: scale(1.05) translateY(-2px);
}

/* 添加动态网格背景 */
.Warn-index::after {
  content: '';
  position: fixed;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle at 50% 50%, rgba(76, 81, 191, 0.1) 0%, transparent 60%);
  animation: floatBackground 30s infinite ease-in-out;
  z-index: -1;
  opacity: 0.7;
}

/* 新增悬浮装饰元素 */
.Warn-index::before {
  content: '';
  position: fixed;
  top: 30%;
  left: 10%;
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, rgba(72, 187, 120, 0.05) 0%, transparent 70%);
  border-radius: 50%;
  filter: blur(40px);
  animation: floatBackground 20s infinite ease-in-out reverse;
  z-index: -1;
}

/* 顶部导航样式 */
.top-navigation {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  padding-bottom: 0.8rem;
  border-bottom: 2px solid rgba(203, 213, 225, 0.5);
  position: relative;
  z-index: 2;
  padding: 12px 24px;
  margin: 16px 24px 12px;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(6px);
  border: 1px solid rgba(30, 92, 168, 0.2);
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(31, 38, 135, 0.12);
}

/* 头部左侧与返回按钮的排版优化 */
.top-navigation .header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.top-navigation::before {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 100%;
  height: 2px;
  background: linear-gradient(90deg, rgba(76, 81, 191, 0.6), rgba(72, 187, 120, 0.6));
  transform: scaleX(0);
  transform-origin: center;
  transition: transform 0.5s ease;
}

.top-navigation:hover::before {
  transform: scaleX(1);
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  color: #1e5ca8;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 0.6rem;
  text-shadow: none;
  animation: fadeInSlideDown 0.8s ease-out;
  position: relative;
  z-index: 1;
}

.page-title i {
  font-size: 2rem;
  color: #ff6b6b;
  filter: drop-shadow(0 2px 4px rgba(255, 107, 107, 0.4));
  transition: all 0.3s ease;
}

.page-title:hover i {
  transform: scale(1.1) rotate(5deg);
  filter: drop-shadow(0 2px 8px rgba(255, 107, 107, 0.6));
}

.nav-actions {
  display: flex;
  gap: 1rem;
}



/* 主内容布局 */
.main-content {
  display: grid;
  grid-template-columns: 1fr 1.5fr 1fr;
  gap: 2rem;
  animation: fadeIn 1s ease-out;
  position: relative;
  z-index: 1;
  perspective: 1000px;
}

/* 卡片通用样式 */
.card {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(15px);
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  margin-bottom: 2rem;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 1;
  transform-style: preserve-3d;
  transform: translateZ(0);
}

.card::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #4c51bf, #48bb78);
  transform: scaleX(0);
  transition: transform 0.5s ease;
  transform-origin: left;
  z-index: 2;
}

.card:hover::after {
  transform: scaleX(1);
}

.card:hover {
  transform: translateY(-5px) translateZ(5px);
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.2);
  border-color: rgba(255, 255, 255, 0.15);
  filter: brightness(1.05);
}

.card::before {
  content: '';
  position: absolute;
  top: -1px;
  left: -1px;
  right: -1px;
  bottom: -1px;
  background: linear-gradient(135deg, rgba(76, 81, 191, 0.1), rgba(72, 187, 120, 0.1));
  border-radius: 20px;
  z-index: -1;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.card:hover::before {
  opacity: 1;
}

.card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.2);
}

.card-shadow {
  position: relative;
  overflow: visible;
}

.card-shadow::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.1) 0%, rgba(255, 255, 255, 0) 100%);
  pointer-events: none;
  border-radius: 20px;
  z-index: 0;
}

.card-shadow::after {
  content: '';
  position: absolute;
  top: 10px;
  left: 10px;
  right: -10px;
  bottom: -10px;
  background: linear-gradient(135deg, rgba(76, 81, 191, 0.05), rgba(72, 187, 120, 0.05));
  border-radius: 25px;
  z-index: -2;
  filter: blur(15px);
  transition: all 0.3s ease;
  opacity: 0.5;
}

.card-shadow:hover::after {
  opacity: 0.8;
  transform: translate(-5px, -5px);
}

/* 卡片头部样式 */
.card-header {
  padding: 1.5rem;
  border-bottom: 1px solid rgba(203, 213, 225, 0.5);
  background: rgba(255, 255, 255, 0.8);
  position: relative;
  transition: background-color 0.3s ease;
}

.card:hover .card-header {
  background: rgba(255, 255, 255, 0.9);
}

.danger-header {
  background: linear-gradient(135deg, rgba(239, 68, 68, 0.1) 0%, rgba(239, 68, 68, 0.05) 100%);
  border-bottom-color: rgba(239, 68, 68, 0.2);
}

.title {
  font-size: 1.3rem;
  font-weight: 600;
  color: #0f172a;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 0.8rem;
}

.danger-header .title i {
  color: #ef4444;
  font-size: 1.5rem;
}

/* 卡片主体样式 */
.card-body {
  padding: 1.5rem;
}

/* 数据网格布局 */
.data-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 1.2rem;
  margin-top: 0.5rem;
}

/* 数据项样式 */
.data-item {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 16px;
  padding: 1.2rem 0.8rem;
  text-align: center;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  border: 1px solid rgba(203, 213, 225, 0.5);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.04);
  cursor: pointer;
  transform-style: preserve-3d;
  z-index: 1;
  backdrop-filter: blur(10px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.data-item::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  transform: translate(-50%, -50%);
  transition: width 0.6s, height 0.6s;
  z-index: -1;
}

.data-item:hover::after {
  width: 300px;
  height: 300px;
}

.data-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 4px;
  background: linear-gradient(90deg, #4c51bf, #48bb78);
  transform: scaleX(0);
  transform-origin: left;
  transition: transform 0.3s ease;
}

.data-item:hover {
  background: rgba(255, 255, 255, 0.12);
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.data-item:hover::before {
  transform: scaleX(1);
}

.data-icon {
  font-size: 2rem;
  margin-bottom: 0.8rem;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.2));
  transition: transform 0.3s ease, filter 0.3s ease;
  display: inline-block;
  transform: translateZ(10px);
}

.data-item:hover .data-icon {
  transform: scale(1.1) rotate(5deg) translateZ(15px);
  filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.3));
}

.data-value {
  font-size: 1.8rem;
  font-weight: 700;
  color: #1e5ca8;
  line-height: 1.2;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease, text-shadow 0.3s ease;
  transform: translateZ(5px);
}

.data-item:hover .data-value {
  transform: scale(1.05) translateZ(10px);
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  color: #3949ab;
}

/* 状态指示器 */
.status-indicator {
  position: absolute;
  top: 12px;
  right: 12px;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  box-shadow: 0 0 0 2px rgba(255, 255, 255, 0.2), 0 0 10px currentColor;
  animation: pulse 2s infinite;
  transition: all 0.3s ease;
  z-index: 2;
  transform: translateZ(20px);
}

.data-item:hover .status-indicator {
  transform: scale(1.2) translateZ(20px);
  box-shadow: 0 0 0 2px rgba(255, 255, 255, 0.3), 0 0 15px currentColor;
}

.status-indicator.safe {
  background-color: #48bb78;
  color: #48bb78;
  box-shadow: 0 0 0 2px rgba(72, 187, 120, 0.3), 0 0 15px rgba(72, 187, 120, 0.5);
}

.status-indicator.low {
  background-color: #4299e1;
  color: #4299e1;
  box-shadow: 0 0 0 2px rgba(66, 153, 225, 0.3), 0 0 15px rgba(66, 153, 225, 0.5);
}

.status-indicator.medium {
  background-color: #f6ad55;
  color: #f6ad55;
  box-shadow: 0 0 0 2px rgba(246, 173, 85, 0.3), 0 0 15px rgba(246, 173, 85, 0.5);
}

.status-indicator.high {
  background-color: #f56565;
  color: #f56565;
  box-shadow: 0 0 0 2px rgba(245, 101, 101, 0.3), 0 0 15px rgba(245, 101, 101, 0.5);
}

.status-indicator.critical {
  background-color: #fc8181;
  color: #fc8181;
  box-shadow: 0 0 0 2px rgba(252, 129, 129, 0.3), 0 0 15px rgba(252, 129, 129, 0.5);
  animation: criticalPulse 1s infinite;
}

/* 数据项状态样式 */
.data-item.temp-critical {
  background-color: rgba(252, 129, 129, 0.2);
  border-color: rgba(252, 129, 129, 0.3);
}

.data-item.temp-critical::before {
  background: linear-gradient(90deg, #fc8181, #f56565);
}

.data-item.temp-high {
  background-color: rgba(246, 173, 85, 0.2);
  border-color: rgba(246, 173, 85, 0.3);
}

.data-item.temp-high::before {
  background: linear-gradient(90deg, #f6ad55, #ed8936);
}

.data-item.temp-medium {
  background-color: rgba(42, 157, 143, 0.2);
  border-color: rgba(42, 157, 143, 0.3);
}

.data-item.temp-medium::before {
  background: linear-gradient(90deg, #2a9d8f, #264653);
}

.data-item.temp-safe {
  background-color: rgba(72, 187, 120, 0.2);
  border-color: rgba(72, 187, 120, 0.3);
}

.data-item.temp-safe::before {
  background: linear-gradient(90deg, #48bb78, #38a169);
}

.data-item.temp-low {
  background-color: rgba(66, 153, 225, 0.2);
  border-color: rgba(66, 153, 225, 0.3);
}

.data-item.temp-low::before {
  background: linear-gradient(90deg, #4299e1, #3182ce);
}

/* 其他状态样式 */
.data-item.light-high, .data-item.gas-high, .data-item.humidity-high,
.data-item.quake-high, .data-item.fire-high, .data-item.air-high {
  background-color: rgba(246, 173, 85, 0.2);
  border-color: rgba(246, 173, 85, 0.3);
}

.data-item.light-high::before, .data-item.gas-high::before, .data-item.humidity-high::before,
.data-item.quake-high::before, .data-item.fire-high::before, .data-item.air-high::before {
  background: linear-gradient(90deg, #f6ad55, #ed8936);
}

.data-item.light-critical, .data-item.gas-critical, .data-item.humidity-critical,
.data-item.quake-critical, .data-item.fire-critical, .data-item.air-critical {
  background-color: rgba(252, 129, 129, 0.2);
  border-color: rgba(252, 129, 129, 0.3);
}

.data-item.light-critical::before, .data-item.gas-critical::before, .data-item.humidity-critical::before,
.data-item.quake-critical::before, .data-item.fire-critical::before, .data-item.air-critical::before {
  background: linear-gradient(90deg, #fc8181, #f56565);
}

/* 天气样式 */
.weather-container {
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 1rem;
}

.weather-item {
  flex: 1;
  min-width: 80px;
  text-align: center;
  padding: 1rem 0.5rem;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 12px;
  transition: all 0.3s ease;
  cursor: pointer;
  border: 1px solid rgba(203, 213, 225, 0.5);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.weather-item:hover {
  background: rgba(255, 255, 255, 0.95);
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
  border-color: rgba(148, 163, 184, 0.8);
}

.weather-item:hover::before {
  opacity: 1;
}

.weather-icon-wrapper i {
  font-size: 2.5rem;
  filter: drop-shadow(0 2px 6px rgba(0, 0, 0, 0.1));
  transition: transform 0.3s ease;
}

.weather-item:hover .weather-icon-wrapper i {
  transform: scale(1.1) rotate(5deg);
}

.weather-time {
  font-size: 0.9rem;
  color: rgba(75, 85, 99, 0.7);
  margin-bottom: 0.8rem;
  font-weight: 500;
}

.weather-icon-wrapper {
  margin: 0.8rem 0;
}

.weather-icon-wrapper i {
  font-size: 2.8rem;
  filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.15));
  transition: transform 0.3s ease;
  transform: translateZ(10px);
}

.weather-condition {
  font-size: 1rem;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 0.5rem;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s ease;
}

.weather-item:hover .weather-condition {
  transform: translateY(-2px);
}

.weather-temp {
  font-size: 1.3rem;
  font-weight: 700;
  color: #0f172a;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s ease;
}

.weather-item:hover .weather-temp {
  transform: translateY(-2px) scale(1.05);
}

/* 天气图标颜色 */
.weather-sunny {
  color: #ffd166;
  filter: drop-shadow(0 2px 8px rgba(255, 209, 102, 0.4));
}

.weather-cloudy {
  color: #90cdf4;
  filter: drop-shadow(0 2px 8px rgba(144, 205, 244, 0.4));
}

.weather-rainy {
  color: #63b3ed;
  filter: drop-shadow(0 2px 8px rgba(99, 179, 237, 0.4));
}

.weather-snowy {
  color: #bee3f8;
  filter: drop-shadow(0 2px 8px rgba(190, 227, 248, 0.4));
}

/* 图表容器 */
.chart-container {
  height: 350px;
  position: relative;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  padding: 1.2rem;
  border: 1px solid rgba(203, 213, 225, 0.8);
  transition: all 0.3s ease;
  z-index: 1;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  backdrop-filter: blur(12px);
  overflow: hidden;
}

.chart-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: radial-gradient(circle at 50% 50%, rgba(76, 81, 191, 0.05) 0%, transparent 70%);
  z-index: -1;
}

.chart-container:hover {
  background: rgba(255, 255, 255, 0.98);
  border-color: rgba(148, 163, 184, 0.9);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.chart-container canvas {
  max-height: 100% !important;
  max-width: 100% !important;
  position: relative;
  z-index: 1;
}

/* 搜索容器样式增强 */
.search-container {
  display: flex;
  gap: 0;
  border-radius: 16px;
  overflow: hidden;
  background-color: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(203, 213, 225, 0.8);
  margin-bottom: 1.5rem;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  backdrop-filter: blur(12px);
  position: relative;
  z-index: 1;
}

.search-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, rgba(76, 81, 191, 0.05) 0%, rgba(72, 187, 120, 0.05) 100%);
  z-index: -1;
}

.search-container:focus-within {
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08);
  border-color: rgba(76, 81, 191, 0.5);
  transform: translateY(-3px);
}

.search-input {
  flex: 1;
  padding: 1rem 1.5rem;
  border: none;
  outline: none;
  font-size: 1rem;
  background-color: transparent;
  color: #1e293b;
  transition: all 0.3s ease;
}

.search-input::placeholder {
  color: rgba(107, 114, 128, 0.6);
}

.search-input:focus {
  background-color: rgba(241, 245, 249, 0.8);
}

.ai-button {
  padding: 0 2rem;
  background: linear-gradient(135deg, #4c51bf, #48bb78);
  color: white;
  border: none;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 600;
  transition: all 0.3s ease;
  white-space: nowrap;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  position: relative;
  overflow: hidden;
  z-index: 1;
  transform-style: preserve-3d;
  transform: translateZ(0);
}

.ai-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s;
  z-index: -1;
}

.ai-button:hover {
  background: linear-gradient(135deg, #434190, #38a169);
  box-shadow: 0 4px 20px rgba(76, 81, 191, 0.5);
  transform: translateX(2px) translateZ(5px);
  border-radius: 0 12px 12px 0;
}

.ai-button:hover::before {
  left: 100%;
}

.ai-button i {
  transition: transform 0.3s ease;
}

.ai-button:hover i {
  transform: scale(1.1) rotate(10deg) translateZ(10px);
}

.warning-item, .environment-item {
  padding: 1rem 1.2rem;
  margin-bottom: 0.8rem;
  border-radius: 12px;
  display: flex;
  align-items: flex-start;
  gap: 0.8rem;
  font-size: 0.95rem;
  line-height: 1.5;
  opacity: 0;
  transform: translateY(20px);
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
  border-left: 4px solid transparent;
  position: relative;
  overflow: hidden;
  cursor: pointer;
  backdrop-filter: blur(10px);
  transform-style: preserve-3d;
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(203, 213, 225, 0.5);
}

.warning-item {
  border-left-color: #ef4444;
  color: #374151;
  box-shadow: 0 2px 8px rgba(239, 68, 68, 0.08);
}

.environment-item {
  border-left-color: #10b981;
  color: #1e293b;
  box-shadow: 0 2px 8px rgba(16, 185, 129, 0.15);
  background: rgba(255, 255, 255, 0.9);
  padding: 12px 16px;
  margin-bottom: 8px;
  border-radius: 8px;
  border: 1px solid rgba(203, 213, 225, 0.5);
}

.warning-item:hover, .environment-item:hover {
  transform: translateY(-4px) translateX(5px) translateZ(5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08);
  filter: brightness(1.02);
  background: rgba(255, 255, 255, 0.95);
}

.warning-item::before, .environment-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, rgba(241, 245, 249, 0.7) 0%, transparent 100%);
  transform: translateX(-100%);
  transition: transform 0.5s ease;
  z-index: 0;
}

.warning-item:hover::before, .environment-item:hover::before {
  transform: translateX(100%);
}

.warning-icon, .environment-icon {
  font-size: 1.3rem;
  flex-shrink: 0;
  margin-top: 0.1rem;
  transition: transform 0.3s ease, filter 0.3s ease;
  transform: translateZ(10px);
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
}

.warning-item .warning-icon {
  color: #ef4444;
}

.environment-item .environment-icon {
  color: #10b981;
}

.warning-item:hover .warning-icon {
  transform: scale(1.2) rotate(5deg) translateZ(15px);
  filter: drop-shadow(0 4px 8px rgba(239, 68, 68, 0.2));
}

.environment-item:hover .environment-icon {
  transform: scale(1.2) rotate(-5deg) translateZ(15px);
  filter: drop-shadow(0 4px 8px rgba(16, 185, 129, 0.2));
}

/* 动画定义 */
@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes fadeInSlideDown {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeInSlideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes pulse {
  0%, 100% {
    opacity: 0.7;
    box-shadow: 0 0 0 2px rgba(255, 255, 255, 0.2), 0 0 10px currentColor;
  }
  50% {
    opacity: 1;
    box-shadow: 0 0 0 2px rgba(255, 255, 255, 0.2), 0 0 20px currentColor;
  }
}

@keyframes criticalPulse {
  0%, 100% {
    opacity: 0.8;
    box-shadow: 0 0 0 2px rgba(255, 255, 255, 0.3), 0 0 15px currentColor;
  }
  50% {
    opacity: 1;
    box-shadow: 0 0 0 2px rgba(255, 255, 255, 0.3), 0 0 30px currentColor;
  }
}

@keyframes pulseSoft {
  0%, 100% {
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  }
  50% {
    box-shadow: 0 8px 40px rgba(255, 107, 107, 0.15);
  }
}

/* 新增动画 */
@keyframes floatBackground {
  0%, 100% {
    transform: translate(0, 0) scale(1);
  }
  25% {
    transform: translate(5%, 5%) scale(1.05);
  }
  50% {
    transform: translate(0, 10%) scale(1);
  }
  75% {
    transform: translate(-5%, 5%) scale(0.95);
  }
}

@keyframes gridMove {
  0% {
    background-position: 0px 0px;
  }
  100% {
    background-position: 40px 40px;
  }
}

@keyframes bounceIcon {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-5px);
  }
}

@keyframes pulseIcon {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}

/* 为animated-fade-in元素添加延迟动画 */
.animated-fade-in {
  opacity: 0;
  transform: translateY(20px);
  transition: all 0.6s cubic-bezier(0.4, 0, 0.2, 1);
}

/* 根据data-index添加不同的动画延迟，创建级联效果 */
.animated-fade-in[data-index="0"] {
  transition-delay: 0.1s;
}

.animated-fade-in[data-index="1"] {
  transition-delay: 0.2s;
}

.animated-fade-in[data-index="2"] {
  transition-delay: 0.3s;
}

.animated-fade-in[data-index="3"] {
  transition-delay: 0.4s;
}

.animated-fade-in[data-index="4"] {
  transition-delay: 0.5s;
}

/* 当元素可见时触发的样式 */
.animated-fade-in.visible {
  opacity: 1;
  transform: translateY(0);
}

/* 为预警列表添加数字前缀 */
.warning-item span::before, .environment-item span::before {
  content: attr(data-index);
  display: inline-block;
  width: 1.5rem;
  height: 1.5rem;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  text-align: center;
  line-height: 1.5rem;
  margin-right: 0.5rem;
  font-size: 0.8rem;
  font-weight: 600;
}

/* 为不同危险级别添加颜色变化 */
.warning-item:nth-child(1) span::before {
  background: rgba(255, 107, 107, 0.3);
}

.warning-item:nth-child(2) span::before {
  background: rgba(255, 183, 77, 0.3);
}

.warning-item:nth-child(3) span::before {
  background: rgba(255, 209, 102, 0.3);
}

/* 增强数据项的状态过渡效果 */
.data-item.temp-critical, .data-item.gas-critical, .data-item.quake-critical, .data-item.fire-critical, .data-item.air-critical {
  background: linear-gradient(135deg, rgba(252, 129, 129, 0.2) 0%, rgba(252, 129, 129, 0.05) 100%);
  border-color: rgba(252, 129, 129, 0.3);
  animation: criticalPulseSoft 2s infinite;
}

@keyframes criticalPulseSoft {
  0%, 100% {
    box-shadow: 0 4px 15px rgba(252, 129, 129, 0.1);
  }
  50% {
    box-shadow: 0 4px 25px rgba(252, 129, 129, 0.2);
  }
}

/* 增强危险卡片的视觉效果 */
.danger-card {
  background: linear-gradient(135deg, rgba(255, 107, 107, 0.1) 0%, rgba(255, 107, 107, 0.05) 100%);
  border-color: rgba(255, 107, 107, 0.2);
  animation: pulseSoft 3s infinite;
  position: relative;
  overflow: hidden;
}

.danger-card::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #ff6b6b, #fc8181);
  transform: scaleX(0);
  animation: dangerBarPulse 2s infinite alternate;
}

@keyframes dangerBarPulse {
  0% {
    transform: scaleX(0);
  }
  100% {
    transform: scaleX(1);
  }
}

/* 添加图表容器增强效果 */
.chart-container {
  height: 350px;
  position: relative;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 12px;
  padding: 1rem;
  border: 1px solid rgba(255, 255, 255, 0.05);
  transition: all 0.3s ease;
  z-index: 1;
}

.chart-container:hover {
  background: rgba(255, 255, 255, 0.05);
  border-color: rgba(255, 255, 255, 0.1);
}

.chart-container canvas {
  max-height: 100% !important;
  max-width: 100% !important;
  position: relative;
  z-index: 1;
}

/* 无预警状态样式优化 */
.no-warning {
  text-align: center;
  padding: 3rem 1rem;
  color: rgba(255, 255, 255, 0.7);
  font-size: 1rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
  position: relative;
  z-index: 1;
  transition: color 0.3s ease;
}

.no-warning:hover {
  color: rgba(255, 255, 255, 0.9);
}

.success-icon {
  font-size: 3rem;
  color: #48bb78;
  filter: drop-shadow(0 2px 8px rgba(72, 187, 120, 0.3));
  transition: transform 0.3s ease;
}

.no-warning:hover .success-icon {
  transform: scale(1.1) rotate(5deg);
}

/* 数据项状态样式增强 */
.data-item.temp-critical::after, 
.data-item.gas-critical::after, 
.data-item.quake-critical::after, 
.data-item.fire-critical::after, 
.data-item.air-critical::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(252, 129, 129, 0.1) 0%, transparent 100%);
  opacity: 0.7;
  pointer-events: none;
  z-index: -1;
}

/* 数据项渐变边框效果 */
.data-item, .weather-item {
  position: relative;
}

.data-item::after, .weather-item::after {
  content: '';
  position: absolute;
  top: -1px;
  left: -1px;
  right: -1px;
  bottom: -1px;
  border-radius: inherit;
  background: linear-gradient(135deg, #4c51bf, #48bb78);
  z-index: -2;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.data-item:hover::after, .weather-item:hover::after {
  opacity: 0.5;
}

.data-item::before, .weather-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: inherit;
  border-radius: inherit;
  z-index: -1;
}
</style>