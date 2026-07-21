<template>
  <div class="First-index">
    <!-- 所有数据展示都是从pinia中的data中获取出来的 -->
    <div class="left">
      <div class="environment-title">
        <h3>实时环境</h3>
      </div>
      <div class="environment-monitor">
        <div class="environment-item">
          <i class="iconfont icon-wendu" style="font-size: 24px;"></i>
          <div class="environment-data">
            <div class="environment-value">{{ store.data.wendu }}</div>
            <div class="environment-label">温度</div>
          </div>
        </div>
        <div class="environment-item">
          <i class="iconfont icon-shidu" style="font-size: 24px;"></i>
          <div class="environment-data">
            <div class="environment-value">{{ store.data.shidu }}</div>
            <div class="environment-label">湿度</div>
          </div>
        </div>
        <div class="environment-item">
          <i class="iconfont icon-kongqi" style="font-size: 24px;"></i>
          <div class="environment-data">
            <div class="environment-value">{{ store.data.kongqi }}</div>
            <div class="environment-label">空气质量</div>
          </div>
        </div>
        <div class="environment-item">
          <i class="iconfont icon-guangxian" style="font-size: 24px;"></i>
          <div class="environment-data">
            <div class="environment-value">{{ store.data.guangxian }}</div>
            <div class="environment-label">光线</div>
          </div>
        </div>
      </div>
      <div class="under">
        <e-charts
            class="chart"
            :option="option"
            :autoresize="true"
        ></e-charts>
      </div>
    </div>

    <div class="center">
      <div class="serch">
        <input
            v-model="inputValue"
            type="text"
            placeholder="搜索家庭智能助手"
            class="input"
            @keyup.enter="search"
        />
        <button class="AIButton" :disabled="!inputValue" @click="search">
          <icon class="iconfont icon-robot"></icon>
          <span>AI助手</span>
        </button>
      </div>

      <div class="title" style="text-align: center; font-size: 22px; font-weight: bold; color: #1e5ca8; margin-bottom: 10px;">
        智能家居控制中心
      </div>

      <div class="tool">
        <div class="title" style="display: none;">
          家居智能控制
        </div>
        <div class="content">
          <div class="device-grid">
            <!-- 设备控制中心 -->
            <div class="device-card device-card-blue" @click="navigateTo('control')">
              <div class="device-icon">
                <svg-icon icon-name="设备" size="32" />
              </div>
              <div class="device-info">
                <h4 class="device-name">设备控制中心</h4>
                <span class="device-status">管理所有设备</span>
              </div>
            </div>
            
            <!-- AI助手 -->
            <div class="device-card device-card-purple" @click="navigateTo('ai')">
              <div class="device-icon">
                <svg-icon icon-name="ai对话" className="device-svg-icon" />
              </div>
              <div class="device-info">
                <h4 class="device-name">AI助手</h4>
                <span class="device-status">智能对话助手</span>
              </div>
            </div>
            
            <!-- 数据分析 -->
            <div class="device-card device-card-green" @click="navigateTo('dataShow')">
              <div class="device-icon">
                <svg-icon icon-name="数据分析" className="device-svg-icon" />
              </div>
              <div class="device-info">
                <h4 class="device-name">数据分析</h4>
                <span class="device-status">环境数据监控</span>
              </div>
            </div>
            
            <!-- 安全监控 -->
            <div class="device-card device-card-red" @click="navigateTo('monitor')">
              <div class="device-icon">
                <svg-icon icon-name="安全监控" className="device-svg-icon" />
              </div>
              <div class="device-info">
                <h4 class="device-name">安全监控</h4>
                <span class="device-status">实时视频监控</span>
              </div>
            </div>
            
            <!-- 消息预警 -->
            <div class="device-card device-card-cyan" @click="navigateTo('warn')">
              <div class="device-icon">
                <svg-icon icon-name="告警消息" className="device-svg-icon" />
              </div>
              <div class="device-info">
                <h4 class="device-name">消息预警</h4>
                <span class="device-status">异常通知中心</span>
              </div>
            </div>
            
            <!-- 3D视图 -->
            <div class="device-card device-card-indigo" @click="navigateTo('3D')">
              <div class="device-icon">
                <svg-icon icon-name="3D视图" className="device-svg-icon" />
              </div>
              <div class="device-info">
                <h4 class="device-name">3D视图</h4>
                <span class="device-status">智能家庭3D展示</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="right">
      <!-- 智能通知 -->
      <div class="up">
        <div class="notifications">
          <h4 class="notification-title">智能通知</h4>
          <div class="notification-item">
            <div class="notification-icon">
              <i class="iconfont icon-tongzhi"></i>
            </div>
            <div class="notification-content">
              <div class="notification-header">
                <span class="notification-subject">系统提醒</span>
                <span class="notification-time">10:30</span>
              </div>
              <div class="notification-text">
                客厅灯光已开启3小时，建议关闭节约能源
              </div>
            </div>
          </div>
          <div class="notification-item">
            <div class="notification-icon">
              <i class="iconfont icon-tongzhi"></i>
            </div>
            <div class="notification-content">
              <div class="notification-header">
                <span class="notification-subject">安全提醒</span>
                <span class="notification-time">昨天</span>
              </div>
              <div class="notification-text">
                检测到门窗异常，已通知管理员
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 建议区域 -->
      <div class="under">
        <div class="title">
          <i class="iconfont icon-zhidao"></i>
          <span>智能建议</span>
        </div>
        <div class="content">
          <div class="advice-item" v-for="(item, index) in adviceList" :key="index">
            <div class="advice-header">
              <i class="iconfont icon-tishi"></i>
              <span>{{ item.title }}</span>
            </div>
            <div class="advice-text">
              {{ item.content }}
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部弹出式导航栏 -->
    <div class="nav-container">
      <div class="nav-hint">
        鼠标靠近显示导航栏
      </div>
      <div class="nav-bar">
        <div class="nav-item" @click="$router.push('/control')">
          <i class="iconfont icon-shebei"></i>
          <span>设备控制</span>
        </div>
        <div class="nav-item" @click="$router.push('/dataShow')">
          <i class="iconfont icon-yibiaopan"></i>
          <span>数据</span>
        </div>
        <div class="nav-item" @click="showAddAppMenu">
          <i class="iconfont icon-tianjia"></i>
          <span>添加</span>
        </div>
        <div class="nav-item" @click="$router.push('/warn')">
          <i class="iconfont icon-xiaoxi"></i>
          <span>消息</span>
        </div>
        <div class="nav-item" @click="$router.push('/ai')">
          <i class="iconfont icon-robot"></i>
          <span>AI助手</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { useMainStore } from '@/store/index.js'

export default {
  data () {
    return {
      inputValue: '',
      adviceList: [
        {
          title: '光线调节',
          content: '当前室内光线较强，建议适当调暗灯光以节约能源。'
        },
        {
          title: '空气净化',
          content: '空气指数偏低，建议开启空气净化器以改善空气质量。'
        },
        {
          title: '温度适宜',
          content: '当前室内温度适宜，无需调整空调设置。'
        }
      ],
      option: {
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['温度', '湿度']
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: ['00:00', '03:00', '06:00', '09:00', '12:00', '15:00', '18:00', '21:00']
        },
        yAxis: {
          type: 'value',
          axisLabel: {
            formatter: '{value}'
          }
        },
        series: [
          {
            name: '温度',
            type: 'line',
            stack: '总量',
            areaStyle: {},
            emphasis: {
              focus: 'series'
            },
            lineStyle: {
              width: 3
            },
            symbolSize: 8,
            data: [18, 19, 17, 16, 20, 22, 21, 20]
          },
          {
            name: '湿度',
            type: 'line',
            stack: '总量',
            areaStyle: {},
            emphasis: {
              focus: 'series'
            },
            lineStyle: {
              width: 3
            },
            symbolSize: 8,
            data: [65, 68, 70, 75, 65, 60, 62, 64]
          }
        ]
      },
      timer: null,
      isOnline: true
    }
  },

  computed: {
    store() {
      return useMainStore()
    }
  },

  watch: {
    'store.data': {
      handler (newValue) {
        // 监听数据变化，可以在这里添加数据处理逻辑
        this.updateAdviceList()
      },
      deep: true
    }
  },

  mounted () {
    // 模拟数据更新
    this.timer = setInterval(() => {
      // 随机更新一些数据
      const randomUpdate = Math.random()
      if (randomUpdate > 0.5) {
        const newData = {
          temperature: (Math.random() * 10 + 15).toFixed(1),
          humidity: Math.floor(Math.random() * 30 + 50),
          illumination: Math.floor(Math.random() * 1000 + 100),
          airQuality: Math.floor(Math.random() * 50 + 50),
          gasQuality: (Math.random() * 0.1).toFixed(3),
          earthquakeIndex: '0',
          fireIndex: '0'
        }
        this.store.setdata(newData)
      }
      // 随机改变在线状态
      if (Math.random() > 0.95) {
        this.isOnline = !this.isOnline
      }
    }, 30000)
  },

  methods: {
    // 导航到对应功能页面
    navigateTo (pageName) {
      // 根据页面名称跳转到相应的功能页面
      console.log(`导航到${this.getPageName(pageName)}功能页面`)
      // 根据页面名称跳转
      switch(pageName) {
        case 'control':
          this.$router.push('/control')
          break
        case 'ai':
          this.$router.push('/ai')
          break
        case 'dataShow':
          this.$router.push('/dataShow')
          break
        case 'monitor':
          this.$router.push('/monitor')
          break
        case 'warn':
          this.$router.push('/warn')
          break
        case '3D':
          this.$router.push('/3D')
          break
        default:
          // 默认跳转到设备控制页面
          this.$router.push('/control')
      }
    },
    
    // 获取页面名称
    getPageName (pageCode) {
      const pageMap = {
        control: '设备控制中心',
        ai: 'AI助手',
        dataShow: '数据分析',
        monitor: '安全监控',
        warn: '消息预警'
      }
      return pageMap[pageCode] || pageCode
    },
    
    // 显示添加应用菜单
    showAddAppMenu() {
      this.$message.success('添加应用功能已触发')
      // 这里可以实现弹出添加应用的菜单或对话框
      // 例如：
      // this.addAppDialogVisible = true
    },



    // AI搜索功能
    search () {
      if (!this.inputValue.trim()) {
        this.$message.warning('请输入搜索内容')
        return
      }
      // 模拟AI响应
      this.$message({
        message: '正在处理您的请求...',
        type: 'info'
      })
      // 实际项目中这里应该调用API
      setTimeout(() => {
        this.$message.success('搜索完成，相关结果已显示')
        this.inputValue = ''
      }, 1500)
    },

    // 更新智能建议列表
    updateAdviceList () {
      // 根据最新数据更新建议
      const newAdvice = []
      
      // 温度建议
      const temp = parseFloat(this.store.data.wendu)
      if (temp > 26) {
        newAdvice.push({
          title: '温度过高',
          content: `当前室内温度${this.store.data.wendu}，建议开启空调降温。`
        })
      } else if (temp < 18) {
        newAdvice.push({
          title: '温度过低',
          content: `当前室内温度${this.store.data.wendu}，建议开启暖气升温。`
        })
      }
      
      // 湿度建议
      const humidity = parseInt(this.store.data.shidu)
      if (humidity > 70) {
        newAdvice.push({
          title: '湿度过高',
          content: `当前室内湿度${this.store.data.shidu}，建议开启除湿机降低湿度。`
        })
      } else if (humidity < 40) {
        newAdvice.push({
          title: '湿度过低',
          content: `当前室内湿度${this.store.data.shidu}，建议使用加湿器增加湿度。`
        })
      }
      
      // 空气质量建议
      const airQuality = parseInt(this.store.data.kongqi)
      if (airQuality < 60) {
        newAdvice.push({
          title: '空气质量较差',
          content: `当前空气质量指数${airQuality}，建议开启空气净化器并关闭门窗。`
        })
      }
      
      // 光线建议
      const light = parseInt(this.store.data.guangxian)
      if (light > 800 && this.store.data.light1) {
        newAdvice.push({
          title: '光线充足',
          content: '当前室内光线充足，建议关闭不必要的灯光以节约能源。'
        })
      } else if (light < 200 && !this.store.data.light1) {
        newAdvice.push({
          title: '光线较暗',
          content: '当前室内光线较暗，建议开启灯光以提高舒适度。'
        })
      }
      
      // 毒气警告
      const toxic = parseFloat(this.store.data.youdu)
      if (toxic > 0.05) {
        newAdvice.push({
          title: '警告：有毒气体超标',
          content: `检测到有毒气体浓度${toxic}，建议立即开窗通风并开启空气净化器。`
        })
      }
      
      // 如果有新建议，更新建议列表
      if (newAdvice.length > 0) {
        this.adviceList = newAdvice
      }
    },

    // 退出登录
    logout () {
      this.$confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 这里可以添加退出登录的逻辑
        this.$router.push('/login')
      }).catch(() => {
        // 用户取消操作
      })
    },


  },

  // 清除定时器的函数
  beforeDestroy () {
    clearInterval(this.timer)
  },

  name: 'FirstIndex'
}
</script>

<style scoped>
/* 全局样式 - 蓝色主题 */
.First-index {
  width: 100%;
  height: 100vh;
  background: linear-gradient(135deg, #f0f7ff, #e6f0ff);
  color: #1e5ca8;
  padding: 25px;
  display: flex;
  gap: 25px;
  box-sizing: border-box;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

/* 左侧区域 */
.First-index .left {
  width: 350px;
  height: 100%;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 8px 32px rgba(30, 92, 168, 0.1);
  backdrop-filter: blur(4px);
  border: 1px solid rgba(144, 202, 249, 0.3);
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 20px;
  transition: all 0.3s ease;
}

.First-index .left:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 40px rgba(30, 92, 168, 0.15);
}

/* 环境监测标题 */
.First-index .left .environment-title {
  text-align: center;
  padding: 10px 0;
  margin-bottom: 10px;
}

.First-index .left .environment-title h3 {
  color: #1e5ca8;
  font-size: 20px;
  font-weight: 600;
  margin: 0;
}

/* 环境监测项目 */
.First-index .left .environment-monitor {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
  padding: 10px;
  background: rgba(240, 247, 255, 0.7);
  border-radius: 15px;
  margin-bottom: 15px;
}

.First-index .left .environment-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 15px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(30, 92, 168, 0.1);
  transition: all 0.3s ease;
  border: 1px solid rgba(144, 202, 249, 0.3);
}

.First-index .left .environment-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(30, 92, 168, 0.15);
  border-color: #90caf9;
}

.First-index .left .environment-item i {
  color: #1e5ca8;
  flex-shrink: 0;
}

.First-index .left .environment-data {
  flex: 1;
}

.First-index .left .environment-value {
  font-size: 24px;
  font-weight: bold;
  color: #1e5ca8;
  line-height: 1.2;
}

.First-index .left .environment-label {
  font-size: 12px;
  color: #64b5f6;
  margin-top: 2px;
}

.First-index .left .under {
  width: 100%;
  height: 350px;
  background-color: rgba(255, 255, 255, 0.8);
  border-radius: 15px;
  box-shadow: 0 5px 18px rgba(30, 92, 168, 0.05);
  overflow: hidden;
  border: 1px solid rgba(144, 202, 249, 0.3);
}

.First-index .left .under .chart {
  width: 100%;
  height: 100%;
}

/* 右侧区域 */
.First-index .right {
  width: 350px;
  height: 100%;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 10px 30px rgba(30, 92, 168, 0.1);
  backdrop-filter: blur(5px);
  border: 1px solid rgba(144, 202, 249, 0.3);
  display: flex;
  flex-direction: column;
  gap: 25px;
  padding: 25px;
  transition: all 0.4s ease;
  box-sizing: border-box;
}

.First-index .right:hover {
  transform: translateY(-3px);
  box-shadow: 0 15px 40px rgba(30, 92, 168, 0.15);
}

/* 智能通知区域 */
.First-index .right .up {
  width: 100%;
  flex: 1;
  background-color: rgba(255, 255, 255, 0.8);
  border-radius: 15px;
  box-shadow: 0 5px 18px rgba(30, 92, 168, 0.05);
  padding: 30px;
  display: flex;
  flex-direction: column;
  gap: 30px;
  border: 1px solid rgba(144, 202, 249, 0.3);
}

/* 智能通知标题 */
.First-index .right .up .notification-title {
  font-size: 18px;
  font-weight: 600;
  color: #1e5ca8;
  margin: 0 0 20px 0;
  border-bottom: 2px solid #e3f2fd;
  padding-bottom: 10px;
}

/* 智能通知项 */
.First-index .right .up .notification-item {
  background: rgba(255, 255, 255, 0.85);
  border-radius: 12px;
  padding: 15px;
  margin-bottom: 15px;
  box-shadow: 0 3px 10px rgba(30, 92, 168, 0.05);
  transition: all 0.4s ease;
  border-left: 4px solid #1e5ca8;
  border: 1px solid rgba(144, 202, 249, 0.3);
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.First-index .right .up .notification-item:hover {
  transform: translateX(8px);
  box-shadow: 0 5px 15px rgba(30, 92, 168, 0.1);
  border-color: #90caf9;
}

/* 智能通知图标 */
.First-index .right .up .notification-icon {
  color: #1e5ca8;
  font-size: 20px;
  margin-right: 12px;
  flex-shrink: 0;
}

/* 智能通知内容 */
.First-index .right .up .notification-content {
  flex: 1;
}

/* 智能通知头部 */
.First-index .right .up .notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

/* 智能通知主题 */
.First-index .right .up .notification-subject {
  font-weight: 600;
  color: #1e5ca8;
  font-size: 14px;
}

/* 智能通知时间 */
.First-index .right .up .notification-time {
  font-size: 12px;
  color: #64b5f6;
}

/* 智能通知文本 */
.First-index .right .up .notification-text {
  color: #1e5ca8;
  font-size: 13px;
  line-height: 1.5;
  word-break: break-word;
}

/* 建议区域 */
.First-index .right .under {
  width: 100%;
  height: 350px;
  background-color: rgba(255, 255, 255, 0.6);
  border-radius: 15px;
  box-shadow: 0 5px 18px rgba(30, 92, 168, 0.05);
  padding: 25px;
  display: flex;
  flex-direction: column;
  border: 1px solid rgba(144, 202, 249, 0.3);
}

.First-index .right .under .title {
  height: 60px;
  display: flex;
  align-items: center;
  font-size: 20px;
  font-weight: bold;
  color: #1e5ca8;
  margin-bottom: 20px;
  border-bottom: 2px solid #e3f2fd;
  padding-bottom: 15px;
}

.First-index .right .under .content {
  flex: 1;
  overflow-y: auto;
  padding-right: 5px;
}

.First-index .right .under .content::-webkit-scrollbar {
  width: 8px;
}

.First-index .right .under .content::-webkit-scrollbar-track {
  background: #FFEBCD;
  border-radius: 4px;
}

.First-index .right .under .content::-webkit-scrollbar-thumb {
  background: #DEB887;
  border-radius: 4px;
}

.First-index .right .under .content::-webkit-scrollbar-thumb:hover {
  background: #CD853F;
}

/* 建议项 */
.First-index .right .under .advice-item {
  background: rgba(255, 255, 255, 0.85);
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 3px 10px rgba(30, 92, 168, 0.05);
  transition: all 0.4s ease;
  border-left: 4px solid #1e5ca8;
  border: 1px solid rgba(144, 202, 249, 0.3);
}

.First-index .right .under .advice-item:hover {
  transform: translateX(8px);
  box-shadow: 0 5px 15px rgba(30, 92, 168, 0.1);
  border-color: #90caf9;
}

.First-index .right .under .advice-header {
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 600;
  color: #1e5ca8;
  margin-bottom: 10px;
  font-size: 15px;
}

.First-index .right .under .advice-text {
  color: #1e5ca8;
  font-size: 14px;
  line-height: 1.6;
  word-break: break-word;
}

/* 中心区域 */
    .First-index .center {
      flex: 1;
      border-radius: 20px;
      background: rgba(255, 255, 255, 0.95);
      box-shadow: 0 10px 30px rgba(30, 92, 168, 0.1);
      backdrop-filter: blur(5px);
      border: 1px solid rgba(144, 202, 249, 0.3);
      padding: 25px;
      display: flex;
      flex-direction: column;
      gap: 25px;
      transition: all 0.4s ease;
    }

.First-index .center:hover {
  transform: translateY(-3px);
  box-shadow: 0 15px 40px rgba(30, 92, 168, 0.15);
}

/* 搜索栏 */
.First-index .serch {
  height: 50px;
  display: flex;
  align-items: center;
  border-radius: 25px;
  background: rgba(255, 255, 255, 0.85);
  box-shadow: 0 5px 25px rgba(30, 92, 168, 0.1);
  overflow: hidden;
  transition: all 0.4s ease;
}

.First-index .serch:hover {
  box-shadow: 0 8px 30px rgba(30, 92, 168, 0.15);
}

.First-index .serch .input {
  height: 100%;
  flex: 1;
  padding: 0 30px;
  border: none;
  outline: none;
  font-size: 18px;
  background: transparent;
  color: #1e5ca8;
}

.First-index .serch .input::placeholder {
  color: #64b5f6;
}

.First-index .serch .AIButton {
  height: 100%;
  width: 150px;
  background: linear-gradient(to right, #1e5ca8 0%, #3949ab 100%);
  color: white;
  font-size: 16px;
  font-weight: 600;
  border: none;
  cursor: pointer;
  transition: all 0.4s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.First-index .serch .AIButton:hover:not(:disabled) {
  background: linear-gradient(to right, #3949ab 0%, #1e5ca8 100%);
  transform: translateX(-3px);
  box-shadow: 0 8px 20px rgba(30, 92, 168, 0.3);
}

.First-index .serch .AIButton:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 家具控制区域 */
.First-index .center .tool {
  flex: 1;
  background-color: rgba(255, 255, 255, 0.9);
  border-radius: 15px;
  box-shadow: 0 5px 18px rgba(0, 0, 0, 0.05);
  padding: 25px;
}

.First-index .center .tool .title {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 25px;
  text-align: center;
  letter-spacing: 0.5px;
}

.First-index .center .tool .content {
  height: 100%;
}

/* 设备网格布局 */
.First-index .center .tool .device-grid {
  display: grid;
  /* 固定列宽并在空间不足时自动收起空列，使整体居中 */
  grid-template-columns: repeat(auto-fit, minmax(220px, 220px));
  /* 保持横向与纵向间距一致 */
  gap: 20px;
  /* 将整个网格居中，而不是靠左 */
  justify-content: center;
  align-content: start;
  height: 100%;
}

/* 设备卡片 - 现代化APP风格 */
.First-index .center .tool .device-card {
  background: linear-gradient(135deg, #f1f5f9 0%, #e2e8f0 100%);
  border-radius: 20px;
  padding: 20px 15px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(226, 232, 240, 0.8);
  position: relative;
  overflow: hidden;
  height: 140px;
}

/* 卡片背景装饰 */
.First-index .center .tool .device-card::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.8) 0%, rgba(255, 255, 255, 0) 70%);
  opacity: 0;
  transform: scale(0.8);
  transition: all 0.4s ease;
  z-index: 1;
}

.First-index .center .tool .device-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.12);
  border-color: rgba(30, 92, 168, 0.3);
  z-index: 10;
}

.First-index .center .tool .device-card:hover::before {
  opacity: 1;
  transform: scale(1);
}

/* 设备卡片点击效果 */
.First-index .center .tool .device-card:active {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

/* 不同颜色的设备卡片 */
.First-index .center .tool .device-card-blue {
  background: linear-gradient(135deg, #e0e7ff 0%, #e0f2fe 100%);
}

.First-index .center .tool .device-card-purple {
  background: linear-gradient(135deg, #ede9fe 0%, #ddd6fe 100%);
}

.First-index .center .tool .device-card-green {
  background: linear-gradient(135deg, #dcfce7 0%, #bbf7d0 100%);
}

.First-index .center .tool .device-card-red {
  background: linear-gradient(135deg, #fee2e2 0%, #fecaca 100%);
}

.First-index .center .tool .device-card-cyan {
  background: linear-gradient(135deg, #ecfeff 0%, #cffafe 100%);
}

.First-index .center .tool .device-card-indigo {
  background: linear-gradient(135deg, #e0e7ff 0%, #c7d2fe 100%);
}

/* 设备图标 */
.First-index .center .tool .device-icon {
  width: 55px;
  height: 55px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  margin-bottom: 12px;
  box-shadow: 0 3px 9px rgba(0, 0, 0, 0.08);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  z-index: 1;
}

/* 图标发光效果 */
.First-index .center .tool .device-icon::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.3);
  transform: translate(-50%, -50%);
  transition: all 0.6s ease;
}

.First-index .center .tool .device-card:hover .device-icon::after {
  width: 100px;
  height: 100px;
}

/* 不同颜色的设备图标背景 */
.First-index .center .tool .device-card-blue .device-icon {
  background: linear-gradient(135deg, #1a4b96, #3140a5);
  box-shadow: 0 0 25px rgba(30, 92, 168, 0.4);
}

.First-index .center .tool .device-card-purple .device-icon {
  background: linear-gradient(135deg, #5a0daf, #1e65e8);
  box-shadow: 0 0 25px rgba(106, 17, 203, 0.4);
}

.First-index .center .tool .device-card-green .device-icon {
  background: linear-gradient(135deg, #0d8a7d, #2fc76c);
  box-shadow: 0 0 25px rgba(17, 153, 142, 0.4);
}

.First-index .center .tool .device-card-red .device-icon {
  background: linear-gradient(135deg, #e083e8, #e64a5c);
  box-shadow: 0 0 25px rgba(240, 147, 251, 0.4);
}

.First-index .center .tool .device-card-cyan .device-icon {
  background: linear-gradient(135deg, #409dfc, #00d4fc);
  box-shadow: 0 0 25px rgba(79, 172, 254, 0.4);
}

.First-index .center .tool .device-card-indigo .device-icon {
  background: linear-gradient(135deg, #4338ca, #6366f1);
  box-shadow: 0 0 25px rgba(99, 102, 241, 0.4);
}

.First-index .center .tool .device-card:hover .device-icon {
  transform: scale(1.15) rotate(5deg);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
}

.First-index .center .tool .device-icon .device-svg-icon {
  width: 28px;
  height: 28px;
}

.First-index .center .tool .device-icon .device-svg-icon img {
  width: 28px;
  height: 28px;
  object-fit: contain;
  color: white;
  z-index: 2;
  transition: all 0.4s ease;
  filter: drop-shadow(0 1px 3px rgba(0, 0, 0, 0.15));
}

.First-index .center .tool .device-card:hover .device-icon i {
  transform: scale(1.1);
}

/* 设备信息 */
.First-index .center .tool .device-info {
  text-align: center;
  position: relative;
  z-index: 2;
}

.First-index .center .tool .device-name {
  font-size: 18px;
  font-weight: 700;
  color: #1e5ca8;
  margin: 0 0 8px 0;
  line-height: 1.2;
  transition: all 0.3s ease;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.First-index .center .tool .device-card:hover .device-name {
  color: #3949ab;
  transform: translateY(-2px);
}

.First-index .center .tool .device-status {
  font-size: 14px;
  color: #64748b;
  font-weight: 500;
  opacity: 0.9;
  transition: all 0.3s ease;
}

.First-index .center .tool .device-card:hover .device-status {
  opacity: 1;
  color: #475569;
}

/* APP风格的角标提示 - 增强版 */
.First-index .center .tool .device-card::after {
  content: attr(data-badge);
  position: absolute;
  top: 15px;
  right: 15px;
  min-width: 24px;
  height: 24px;
  padding: 0 8px;
  border-radius: 12px;
  background: linear-gradient(135deg, #f44336, #d32f2f);
  color: white;
  font-size: 12px;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transform: scale(0.8);
  transition: all 0.3s ease;
  box-shadow: 0 4px 10px rgba(244, 67, 54, 0.3);
  z-index: 3;
}

.First-index .center .tool .device-card:nth-child(1)::after,
.First-index .center .tool .device-card:nth-child(3)::after {
  content: '1';
  opacity: 1;
  transform: scale(1);
  animation: pulse 2s infinite;
}

.First-index .center .tool .device-card:nth-child(4)::after {
  content: '2';
  opacity: 1;
  transform: scale(1);
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% { transform: scale(1); opacity: 1; box-shadow: 0 4px 10px rgba(244, 67, 54, 0.3); }
  50% { transform: scale(1.15); opacity: 0.9; box-shadow: 0 6px 15px rgba(244, 67, 54, 0.4); }
  100% { transform: scale(1); opacity: 1; box-shadow: 0 4px 10px rgba(244, 67, 54, 0.3); }
}

/* 自定义对话框样式 */
.First-index .custom-dialog .el-dialog__header {
  background: linear-gradient(135deg, #CD853F, #A0522D);
  color: white;
  border-radius: 15px 15px 0 0;
}

.First-index .custom-dialog .el-dialog__title {
  color: white;
  font-size: 18px;
  font-weight: 600;
}

.First-index .custom-dialog .el-dialog__headerbtn .el-dialog__close {
  color: white;
}

.First-index .custom-dialog .el-dialog__body {
  padding: 25px;
  background-color: #FFF8DC;
}

.First-index .custom-dialog .el-form-item__label {
  font-weight: 600;
  color: #8B4513;
}

.First-index .custom-dialog .el-input__inner {
  border-radius: 8px;
  border-color: #F5DEB3;
  transition: all 0.4s ease;
  background-color: white;
}

.First-index .custom-dialog .el-input__inner:focus {
  border-color: #CD853F;
  box-shadow: 0 0 0 2px rgba(205, 133, 63, 0.2);
}

/* 底部导航栏 - 实现鼠标不在附近时缩回效果 */
    .nav-container {
        position: fixed;
        bottom: -80px; /* 默认缩回到底部外 */
        left: 50%;
        transform: translateX(-50%);
        width: auto;
        background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(240, 244, 248, 0.95) 100%);
        border: 1px solid rgba(226, 232, 240, 0.8);
        border-radius: 30px;
        display: flex;
        justify-content: center;
        align-items: center;
        box-shadow: 0 8px 25px rgba(0, 0, 0, 0.12);
        z-index: 1000;
        padding: 10px 20px;
        backdrop-filter: blur(15px);
        transition: bottom 0.4s cubic-bezier(0.34, 1.56, 0.64, 1),
                    box-shadow 0.4s cubic-bezier(0.4, 0, 0.2, 1);
    }

    /* 当页面底部区域有鼠标移动时，导航栏弹出 */
    .First-index:hover .nav-container,
    .nav-container:hover {
        bottom: 15px; /* 弹出到正常位置 */
        box-shadow: 0 12px 35px rgba(0, 0, 0, 0.15);
    }

    /* 导航栏提示 */
    .nav-hint {
        position: absolute;
        bottom: 100%;
        left: 50%;
        transform: translateX(-50%);
        background: linear-gradient(135deg, rgba(30, 92, 168, 0.9) 0%, rgba(57, 73, 171, 0.9) 100%);
        color: white;
        padding: 6px 14px;
        border-radius: 20px;
        font-size: 12px;
        opacity: 0;
        pointer-events: none;
        transition: opacity 0.3s ease, transform 0.3s ease;
        white-space: nowrap;
        margin-bottom: 8px;
        box-shadow: 0 4px 12px rgba(30, 92, 168, 0.3);
    }

    /* 当导航栏弹出时显示提示 */
    .nav-container:hover .nav-hint {
        opacity: 1;
        transform: translateX(-50%) translateY(-2px);
    }

    .nav-bar {
        display: flex;
        justify-content: space-around;
        align-items: center;
        gap: 18px;
        background: transparent;
        border: none;
        box-shadow: none;
        padding: 0;
        transform: none;
    }

    /* 导航项样式 - 更现代的APP风格 */
    .nav-item {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 4px;
        color: #64748b;
        cursor: pointer;
        transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
        padding: 12px 20px;
        border-radius: 16px;
        position: relative;
        background-color: transparent;
        overflow: visible;
    }

    /* 导航按钮渐变背景 */
    .nav-item::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: linear-gradient(135deg, rgba(30, 92, 168, 0.1) 0%, rgba(57, 73, 171, 0.1) 100%);
        border-radius: 16px;
        opacity: 0;
        transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
        z-index: -1;
    }

    /* 活动导航项样式 */
    .nav-item.active {
        color: #1e5ca8;
    }

    .nav-item.active::before {
        opacity: 1;
    }

    /* 鼠标移动到按钮上时突出显示 */
    .nav-item:hover {
        background-color: rgba(30, 92, 168, 0.15);
        color: #1e5ca8;
        transform: translateY(-4px);
    }

    .nav-item:hover::before {
        opacity: 0.7;
    }

    .nav-item i {
        font-size: 26px;
        transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
        position: relative;
    }

    /* 图标发光效果 */
    .nav-item.active i::after,
    .nav-item:hover i::after {
        content: '';
        position: absolute;
        top: 50%;
        left: 50%;
        width: 40px;
        height: 40px;
        border-radius: 50%;
        background: radial-gradient(circle, rgba(30, 92, 168, 0.4) 0%, rgba(30, 92, 168, 0) 70%);
        transform: translate(-50%, -50%);
        z-index: -1;
        animation: glowPulse 2s infinite;
    }

    @keyframes glowPulse {
        0% { transform: translate(-50%, -50%) scale(1); opacity: 0.4; }
        50% { transform: translate(-50%, -50%) scale(1.2); opacity: 0.3; }
        100% { transform: translate(-50%, -50%) scale(1); opacity: 0.4; }
    }

    .nav-item:hover i {
        transform: scale(1.2);
        color: #3949ab;
    }

    .nav-item span {
        font-size: 13px;
        font-weight: 600;
        white-space: nowrap;
        opacity: 0.8;
        transition: all 0.3s ease;
        text-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
    }

    .nav-item:hover span {
        opacity: 1;
        color: #1e5ca8;
        transform: translateY(-1px);
    }

    /* 添加按钮（中间按钮）突出显示 */
    .nav-item:nth-child(3) {
        background: linear-gradient(135deg, #1e5ca8, #3949ab);
        color: white;
        padding: 18px;
        border-radius: 50%;
        transform: translateY(-18px);
        box-shadow: 0 8px 20px rgba(30, 92, 168, 0.5);
        z-index: 1;
        position: relative;
        overflow: hidden;
        transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
    }

    /* 添加按钮发光效果 */
    .nav-item:nth-child(3)::after {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: linear-gradient(135deg, rgba(255, 255, 255, 0.1) 0%, rgba(255, 255, 255, 0) 50%);
        z-index: 1;
    }

    .nav-item:nth-child(3):hover {
        background: linear-gradient(135deg, #256dc1, #445ebd);
        transform: translateY(-22px) scale(1.05);
        box-shadow: 0 10px 25px rgba(30, 92, 168, 0.6);
    }

    .nav-item:nth-child(3):active {
        transform: translateY(-22px) scale(0.98);
        box-shadow: 0 6px 15px rgba(30, 92, 168, 0.4);
    }

    .nav-item:nth-child(3) i {
        font-size: 26px;
        color: white;
        z-index: 2;
        transition: all 0.3s ease;
    }

    .nav-item:nth-child(3):hover i {
        transform: rotate(180deg);
        color: white;
    }

    /* 响应式调整 */
    @media (max-width: 768px) {
        .nav-container {
            padding: 5px 12px;
            width: calc(100% - 30px);
        }
        
        .nav-bar {
            gap: 15px;
            width: 100%;
            justify-content: space-between;
        }
        
        .nav-item {
            padding: 8px 12px;
        }
        
        .nav-item i {
            font-size: 20px;
        }
        
        .nav-item span {
            font-size: 10px;
        }
        
        .nav-item:nth-child(3) {
            padding: 14px;
            transform: translateY(-15px);
        }
        
        .nav-item:nth-child(3) i {
            font-size: 24px;
        }
    }

/* 响应式设计 */
@media (max-width: 1400px) {
  .First-index {
    flex-wrap: wrap;
  }
  
  .First-index .left,
  .First-index .right {
    width: 100%;
    height: auto;
    flex: none;
  }
  
  .First-index .center {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .First-index {
    padding: 15px;
    gap: 15px;
    height: calc(100vh - 15px);
  }
  
  .First-index .left .up .dataitem {
    width: calc(50% - 8px);
    height: 120px;
  }
  
  .First-index .center .tool .conitem {
    flex-wrap: wrap;
    gap: 20px;
  }
  
  .First-index .center .tool .device-card {
    width: 100px;
    height: 100px;
  }
  
  .nav-bar {
    padding: 15px 0;
  }
  
  .nav-item {
    padding: 8px 15px;
  }
  
  .nav-item i {
    font-size: 20px;
  }
  
  .nav-item span {
    font-size: 12px;
  }
}

/* 设备状态指示器样式 */
.device-status-indicator {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: linear-gradient(135deg, #4caf50, #45a049);
  display: inline-block;
  margin-right: 6px;
  position: relative;
  box-shadow: 0 0 0 3px rgba(76, 175, 80, 0.2);
  z-index: 1;
}

.device-status-indicator::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: linear-gradient(135deg, #4caf50, #45a049);
  transform: translate(-50%, -50%);
  animation: statusPulse 2s infinite;
}

@keyframes statusPulse {
  0% { transform: translate(-50%, -50%) scale(1); opacity: 1; }
  70% { transform: translate(-50%, -50%) scale(2.5); opacity: 0; }
  100% { transform: translate(-50%, -50%) scale(1); opacity: 0; }
}

/* 警告状态指示器 */
.device-status-indicator.warning {
  background: linear-gradient(135deg, #ff9800, #f57c00);
  box-shadow: 0 0 0 3px rgba(255, 152, 0, 0.2);
}

.device-status-indicator.warning::after {
  background: linear-gradient(135deg, #ff9800, #f57c00);
}

/* 危险状态指示器 */
.device-status-indicator.danger {
  background: linear-gradient(135deg, #f44336, #d32f2f);
  box-shadow: 0 0 0 3px rgba(244, 67, 54, 0.2);
}

.device-status-indicator.danger::after {
  background: linear-gradient(135deg, #f44336, #d32f2f);
  animation: statusPulse 1s infinite; /* 危险状态动画更快 */
}

/* 动画效果 */
@keyframes pulse {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.08);
  }
  100% {
    transform: scale(1);
  }
}

.status-warning .data {
  animation: pulse 2s infinite;
  color: #1e5ca8 !important;
}

.status-danger .data {
  animation: pulse 1s infinite;
  color: #3949ab !important;
}

/* 滚动条样式 */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: #e3f2fd;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb {
  background: #90caf9;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: #1e5ca8;
}
</style>
