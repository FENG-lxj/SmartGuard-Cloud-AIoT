<template>
    <div class="Control-index">
      <!-- 返回首页按钮 -->
  <!-- 返回首页按钮移入头部左侧 -->
      
      <!-- 控制页头部导航与场景切换 -->
      <div class="control-header">
        <div class="header-left">
          <BackButton :to="'/first'" />
          <h1 class="header-title">设备控制</h1>
          <p class="header-subtitle">选择场景或浏览房间</p>
        </div>
        <div class="header-actions">
          <div class="global-segments">
            <button class="seg-button" :class="{ active: topRightBtn1 }" @click="topHome">在家</button>
            <button class="seg-button" :class="{ active: topRightBtn2 }" @click="topAway">外出</button>
            <button class="seg-button" :class="{ active: topRightBtn3 }" @click="topSleep">睡眠</button>
          </div>
          <div class="header-status">
            <div class="status-item time">{{ currentTimeStr }}</div>
            <div class="status-item network" :class="{ online: isNetworkOnline }">网络：{{ isNetworkOnline ? '在线' : '离线' }}</div>
            <div class="status-item devices">设备在线：{{ onlineDevicesCount }}/{{ totalDevicesCount }}</div>
          </div>
        </div>
      </div>
      <!-- 正在使用的设备·小卡片 -->
      <div class="active-devices">
        <div v-for="item in activeDevicesList" :key="item.key" class="active-chip" :class="{ on: item.on }" @click="openDeviceDrawer(item.key)">
          <icon :class="item.icon" style="font-size: 18px;"></icon>
          <span class="label">{{ item.label }}</span>
          <span class="value" v-if="item.value">{{ item.value }}</span>
        </div>
      </div>
      <!-- 数据分析卡片：设备使用占比 -->
      <div class="control-overview">
        <div class="overview-card">
          <div class="overview-header">
            <div class="ov-title">设备使用分析</div>
            <div class="ov-badges">
              <span class="ov-badge on">正在使用 {{ use }}</span>
              <span class="ov-badge off">未使用 {{ unuse }}</span>
            </div>
          </div>
          <e-charts ref="chart" class="overview-chart" :option="option" />
        </div>
      </div>
      
      <!-- 设备详情抽屉/底部弹窗 -->
      <div v-if="showDeviceDrawer" class="device-drawer" @click.self="closeDeviceDrawer">
        <div class="drawer-content">
          <div class="drawer-header">
            <div class="drawer-title">{{ modalDeviceMeta.name }} · 高级设置</div>
            <button class="drawer-close" @click="closeDeviceDrawer">×</button>
          </div>
          <div class="drawer-body">
            <div class="drawer-row">状态：{{ modalDeviceMeta.online ? '在线' : '离线' }} · {{ modalDeviceMeta.valueLabel }}</div>
            <div class="drawer-row">最后更新时间：{{ formatTimeAgo(modalDeviceMeta.lastUpdated) }}</div>
            <div class="drawer-actions">
              <button class="primary" @click="openSceneTimer(modalDeviceId)">场景定时器</button>
              <button class="secondary" @click="viewLogs(modalDeviceId)">查看日志</button>
            </div>
          </div>
        </div>
      </div>

      <div class="bottom">
        <!-- 左侧部分 -->
        <div class="left">
          <!-- 左侧的三个大框 卧室-->
          <div class="left-item">
            <!-- 大图标 -->
            <div class="big">
              <icon style="font-size: 80px;" class="iconfont icon-a-2zhangdanrenchuang"></icon>
            </div>
            <div class="small">
              <div class="title">
                卧室
              </div>
              <!-- 放小图标的地方 -->
              <div class="pic">
                <div class="pic-item" @click="leftWoPic1">
                  <div :class="leftWo1 ? 'leftKai' : 'leftGuan'">
                    <icon style="font-size: 20px;" class="iconfont icon-deng"></icon>
                  </div>
                </div>

                <div class="pic-item" @click="leftWoPic2">
                  <div :class="leftWo2 ? 'leftKai' : 'leftGuan'">
                    <icon style="font-size: 30px;" class="iconfont icon-fengshan1"></icon>
                  </div>
                </div>

                <div class="pic-item" @click="leftWoPic3">
                  <div :class="leftWo3 ? 'leftKai' : 'leftGuan'">
                    <icon style="font-size: 20px;" class="iconfont icon-chuanglian1"></icon>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 客厅 -->
          <div class="left-item">
            <div class="big">
              <icon style="font-size: 80px;" class="iconfont icon-keting"></icon>
            </div>
            <div class="small">
              <div class="title">
                客厅
              </div>

              <div class="pic">
                <div class="pic-item" @click="leftKePic1">
                  <div :class="leftKe1 ? 'leftKai' : 'leftGuan'">
                    <icon style="font-size: 20px;" class="iconfont icon-deng"></icon>
                  </div>
                </div>

                <div class="pic-item" @click="leftKePic2">
                  <div :class="leftKe2 ? 'leftKai' : 'leftGuan'">
                    <icon style="font-size: 20px;" class="iconfont icon-chuanglian1"></icon>
                  </div>
                </div>

                <div class="pic-item" @click="leftKePic3">
                  <div :class="leftKe3 ? 'leftKai' : 'leftGuan'">
                    <icon style="font-size: 25px;" class="iconfont icon-wifi-router"></icon>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="left-item">
            <div class="big">
              <icon style="font-size: 80px;" class="iconfont icon-yushi"></icon>
            </div>
            <div class="small">
              <div class="title">
                浴室
              </div>

              <div class="pic">
                <div class="pic-item" @click="leftYuPic1">
                  <div :class="leftYu1 ? 'leftKai' : 'leftGuan'">
                    <icon style="font-size: 20px;" class="iconfont icon-deng"></icon>
                  </div>
                </div>

                <div class="pic-item" @click="leftYuPic2">
                  <div :class="leftYu2 ? 'leftKai' : 'leftGuan'">
                    <icon style="font-size: 20px;" class="iconfont icon-chuanglian1"></icon>
                  </div>
                </div>
              </div>
            </div>

          </div>
        </div>
        <!--控制家居部分 -->
        <div class="count">
          <!-- 上三部分 -->
          <div class="up">

            <!-- 门锁 -->
            <div class="up-item-a">
              <div class="up-pic">
                <icon style="font-size: 180px;" class="iconfont icon-zhaipaibaoliu"></icon>
              </div>
              <div class="up-under">
                <div class="title">
                  门锁
                </div>
                <div class="but">
                  <goodBtn :status="doorLockStatus" @toggle="toggleDoorLock"></goodBtn>
                </div>
              </div>
            </div>

            <!-- 灯 -->
            <div class="up-item-b">
              <div class="zi-left">
                <div class="title">灯</div>
                <div class="status-row">
                  <span class="state" :class="{ online: lightOnline }">{{ lightOnline ? '在线' : '离线' }}</span>
                  <span class="sep">·</span>
                  <span class="value">亮度 {{ lightBrightness }}%</span>
                  <span class="sep">·</span>
                  <span class="time">{{ formatTimeAgo(lastUpdated && lastUpdated.light) }}</span>
                </div>
                <div class="btn">
                  <goodBtn :status="lightStatus" @toggle="toggleLight"></goodBtn>
                </div>
                <div class="slider-row">
                  <input type="range" min="0" max="100" v-model.number="lightBrightness" @input="onSliderInput('light', lightBrightness)" @change="onSliderCommit('light', lightBrightness)" />
                </div>
                <button class="more-btn" @click="openDeviceDrawer('light')">···</button>
              </div>
              <div class="right-pic">
                <icon style="font-size: 220px;" class="iconfont icon-ladideng"></icon>
                <span v-if="isPending('light')" class="pending-dot"></span>
              </div>
            </div>

            <!-- 窗帘 -->
            <div class="up-item-c">
              <div class="up-pic">
                <icon style="font-size: 180px;" class="iconfont icon-chuanglian2"></icon>
              </div>

              <div class="up-under">
                <div class="title">
                  窗帘
                </div>
                <div class="btn">
                  <goodBtn :status="curtainStatus" @toggle="toggleCurtain"></goodBtn>
                </div>
             </div>
            </div>
          </div>

          <!-- 下三部分 -->
          <div class="under">

            <div class="under-item-a">
              <div class="zi-left">
                <div class="title">风扇</div>
                <div class="status-row">
                  <span class="state" :class="{ online: fanOnline }">{{ fanOnline ? '在线' : '离线' }}</span>
                  <span class="sep">·</span>
                  <span class="value">风速 {{ fanSpeed }}%</span>
                  <span class="sep">·</span>
                  <span class="time">{{ formatTimeAgo(lastUpdated && lastUpdated.fan) }}</span>
                </div>
                <div class="btn">
                  <goodBtn :status="fanStatus" @toggle="toggleFan"></goodBtn>
                </div>
                <div class="slider-row">
                  <input type="range" min="0" max="100" v-model.number="fanSpeed" @input="onSliderInput('fan', fanSpeed)" @change="onSliderCommit('fan', fanSpeed)" />
                </div>
                <button class="more-btn" @click="openDeviceDrawer('fan')">···</button>
              </div>

              <div class="right-pic">
                <icon style="font-size: 140px;" class="iconfont icon-fengshan"></icon>
                <span v-if="isPending('fan')" class="pending-dot"></span>
              </div>
            </div>

            <div class="under-item-b">
              <div class="zi-left">
                <div class="title">
                  水壶
                </div>
                <div class="btn">
                  <goodBtn :status="kettleStatus" @toggle="toggleKettle"></goodBtn>
                </div>
              </div>

              <div class="right-pic">
                <icon style="font-size: 140px;" class="iconfont icon-a-leixingreshuihu"></icon>
              </div>
            </div>

            <div class="under-item-c">
              <div class="zi-left">
                <div class="title">
                  wifi
                </div>
                <div class="btn">
                  <goodBtn :status="wifiStatus" @toggle="toggleWifi"></goodBtn>
                </div>
              </div>

              <div class="right-pic">
                <icon style="font-size: 130px;" class="iconfont icon-mti-WIFItanzhen"></icon>
              </div>
            </div>
          </div>
        </div>
      </div>

    </div>
</template>

<script>
import goodBtn from '@/views/main/Control/Btn.vue'
import BackButton from '@/components/BackButton.vue'
export default {
  name: 'ControlIndex',
  components: { BackButton,
    goodBtn
  },
  data () {
    return {
      topRightBtn1: false,
      topRightBtn2: false,
      topRightBtn3: false,
      leftWo1: false,
      leftWo2: false,
      leftWo3: false,
      leftKe1: false,
      leftKe2: false,
      leftKe3: false,
      leftYu1: false,
      leftYu2: false,
      doorLockStatus: false,
      lightStatus: false,
      curtainStatus: false,
      fanStatus: false,
      kettleStatus: false,
      wifiStatus: false,
      // 全局统计/时间
      totalDevicesCount: 6,
      currentTimeStr: '',
      // 请求与ACK管理
      pendingRequests: {},
      // 最近更新时间
      lastUpdated: { doorLock: null, light: null, curtain: null, fan: null, kettle: null, wifi: null },
      // 滑块值与状态
      lightBrightness: 60,
      fanSpeed: 50,
      sliderState: { light: { debounceTimer: null, lastSentAt: 0 }, fan: { debounceTimer: null, lastSentAt: 0 } },
      // 抽屉
      showDeviceDrawer: false,
      modalDeviceId: null,
      use: 0,
      unuse: 0,
      option: {
        tooltip: {
          trigger: 'item'
        },
        legend: {
          top: '5%',
          left: 'center'
        },
        series: [
          {
            // name: 'Access From',
            type: 'pie',
            radius: ['40%', '70%'],
            avoidLabelOverlap: false,
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: 40,
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: [
              { value: this.use, name: '正在使用的设备' },
              { value: this.unuse, name: '未使用的设备' }

            ]
          }
        ]
      }
    }
  },
  mounted () {
    this.option.series[0].data[0].value = this.use
    this.applyAnalysisStyle()
    this.$nextTick(() => {
      this.$refs.chart && this.$refs.chart.setOption(this.option, true)
    })
    // 时间显示
    this.updateTime()
    this.timeTimer = setInterval(this.updateTime, 1000)
    // 监听后端ACK事件（需后端在window上派发 CustomEvent）
    window.addEventListener('device-ack', this.onDeviceAck)
    window.addEventListener('scene-ack', this.onSceneAck)
  },
  beforeUnmount () {
    if (this.timeTimer) clearInterval(this.timeTimer)
    window.removeEventListener('device-ack', this.onDeviceAck)
    window.removeEventListener('scene-ack', this.onSceneAck)
  },
  watch: {
    use () {
      this.applyAnalysisStyle()
      this.$nextTick(() => {
        if (this.$refs && this.$refs.chart && typeof this.$refs.chart.setOption === 'function') {
          this.$refs.chart.setOption(this.option, true)
        }
      })
    },
    unuse () {
      this.applyAnalysisStyle()
      this.$nextTick(() => {
        if (this.$refs && this.$refs.chart && typeof this.$refs.chart.setOption === 'function') {
          this.$refs.chart.setOption(this.option, true)
        }
      })
    }
  },
  computed: {
    isNetworkOnline () {
      return this.wifiStatus
    },
    onlineDevicesCount () {
      const keys = ['doorLockStatus','lightStatus','curtainStatus','fanStatus','kettleStatus','wifiStatus']
      return keys.reduce((acc, k) => acc + (this[k] ? 1 : 0), 0)
    },
    lightOnline () {
      return !!this.lightStatus
    },
    fanOnline () {
      return !!this.fanStatus
    },
    // 小卡片数据：仅显示当前使用中的设备
    activeDevicesList () {
      const list = [
        { key: 'doorLock', label: '门锁', value: this.doorLockStatus ? '已上锁' : '未上锁', on: !!this.doorLockStatus, icon: 'iconfont icon-zhaipaibaoliu' },
        { key: 'light', label: '灯', value: (this.lightStatus && this.lightBrightness > 0) ? `亮度 ${this.lightBrightness}%` : '关闭', on: !!(this.lightStatus && this.lightBrightness > 0), icon: 'iconfont icon-ladideng' },
        { key: 'curtain', label: '窗帘', value: this.curtainStatus ? '开启' : '关闭', on: !!this.curtainStatus, icon: 'iconfont icon-chuanglian2' },
        { key: 'fan', label: '风扇', value: (this.fanStatus && this.fanSpeed > 0) ? `风速 ${this.fanSpeed}%` : '关闭', on: !!(this.fanStatus && this.fanSpeed > 0), icon: 'iconfont icon-fengshan1' },
        { key: 'kettle', label: '水壶', value: this.kettleStatus ? '加热中' : '空闲', on: !!this.kettleStatus, icon: 'iconfont icon-shuihu' },
        { key: 'wifi', label: '网络', value: this.wifiStatus ? '在线' : '离线', on: !!this.wifiStatus, icon: 'iconfont icon-wifi-router' },
      ]
      return list
    },
    modalDeviceMeta () {
      const id = this.modalDeviceId
      const map = {
        light: { name: '灯', online: this.lightStatus, valueLabel: `亮度 ${this.lightBrightness}%`, lastUpdated: this.lastUpdated ? this.lastUpdated.light : null },
        fan: { name: '风扇', online: this.fanStatus, valueLabel: `风速 ${this.fanSpeed}%`, lastUpdated: this.lastUpdated ? this.lastUpdated.fan : null }
      }
      return map[id] || { name: '设备', online: false, valueLabel: '', lastUpdated: null }
    }
  },
  methods: {
    // 场景切换：乐观更新 + toast + ACK/回滚
    topHome () { this.sendSceneChange('Home') },
    topAway () { this.sendSceneChange('Away') },
    topSleep () { this.sendSceneChange('Sleep') },

    // 美化数据分析图表：颜色、中心文字、半径、tooltip/legend
    applyAnalysisStyle () {
      const total = (this.use || 0) + (this.unuse || 0)
      const percent = total ? Math.round((this.use / total) * 100) : 0
      if (this.option && this.option.series && this.option.series[0]) {
        this.option.series[0].data = [
          { value: this.use, name: '正在使用的设备' },
          { value: this.unuse, name: '未使用的设备' }
        ]
        this.option.series[0].radius = ['55%', '75%']
        this.option.series[0].label = { show: false }
        this.option.series[0].labelLine = { show: false }
      }
      this.option.color = ['#22c55e', '#e5e7eb']
      this.option.tooltip = {
        trigger: 'item',
        formatter: function (params) {
          return `${params.marker} ${params.name}: ${params.value} (${params.percent}%)`
        }
      }
      this.option.legend = {
        bottom: 8,
        left: 'center',
        icon: 'circle',
        itemWidth: 8,
        itemHeight: 8,
        textStyle: { color: '#475569', fontSize: 12 }
      }
      this.option.graphic = [
        {
          type: 'text', left: 'center', top: 'center',
          style: { text: percent + '%', fontSize: 24, fontWeight: 600, fill: '#0f172a' }
        },
        {
          type: 'text', left: 'center', top: '56%',
          style: { text: '使用率', fontSize: 12, fill: '#64748b' }
        }
      ]
    },
    
    sendSceneChange (mode) {
      const reqId = this.genRequestId()
      // 乐观更新UI
      this.topRightBtn1 = mode === 'Home'
      this.topRightBtn2 = mode === 'Away'
      this.topRightBtn3 = mode === 'Sleep'
      if (mode === 'Home') {
        this.doorLockStatus = true
        this.lightStatus = true
        this.wifiStatus = true
        this.lastUpdated.doorLock = Date.now()
        this.lastUpdated.light = Date.now()
        this.lastUpdated.wifi = Date.now()
      } else if (mode === 'Away') {
        this.doorLockStatus = false
        this.lightStatus = false
        this.curtainStatus = false
        this.fanStatus = false
        this.kettleStatus = false
        this.wifiStatus = false
        const t = Date.now()
        this.lastUpdated.doorLock = t
        this.lastUpdated.light = t
        this.lastUpdated.curtain = t
        this.lastUpdated.fan = t
        this.lastUpdated.kettle = t
        this.lastUpdated.wifi = t
      } else if (mode === 'Sleep') {
        this.curtainStatus = true
        this.lightStatus = false
        this.kettleStatus = false
        this.doorLockStatus = true
        const t = Date.now()
        this.lastUpdated.curtain = t
        this.lastUpdated.light = t
        this.lastUpdated.kettle = t
        this.lastUpdated.doorLock = t
      }
      // 记录请求与定时器
      this.pendingRequests[reqId] = {
        type: 'scene', mode,
        start: Date.now(),
        waitingTimer: setTimeout(() => { /* 3s 未ACK显示等待 */ }, 3000),
        rollbackTimer: setTimeout(() => {
          // 10s 未ACK回滚
          this.rollbackScene(mode)
          this.$toast && this.$toast({ message: '场景切换失败，已回滚', type: 'fail' })
          delete this.pendingRequests[reqId]
        }, 10000)
      }
      // 发送事件（留给后端对接）
      const payload = { event: 'scene.change', mode, requestId: reqId, timestamp: Date.now() }
      this.$emit('scene-change', payload)
      this.$toast && this.$toast({ message: `切换场景：${mode}`, type: 'loading', duration: 1000 })
    },
    rollbackScene (mode) {
      // 简单回滚为之前的状态：全部取消激活
      this.topRightBtn1 = false
      this.topRightBtn2 = false
      this.topRightBtn3 = false
    },
    onSceneAck (e) {
      const { requestId, success } = e.detail || {}
      const req = this.pendingRequests[requestId]
      if (!req || req.type !== 'scene') return
      clearTimeout(req.waitingTimer)
      clearTimeout(req.rollbackTimer)
      delete this.pendingRequests[requestId]
      if (!success) {
        this.rollbackScene(req.mode)
        this.$toast && this.$toast({ message: '场景切换失败，已回滚', type: 'fail' })
      } else {
        this.$toast && this.$toast({ message: '场景切换完成', type: 'success' })
      }
    },
    leftWoPic1 () {
      this.leftWo1 = !this.leftWo1
      console.log(this.leftWo1)
    },
    leftWoPic2 () {
      this.leftWo2 = !this.leftWo2
    },
    leftWoPic3 () {
      this.leftWo3 = !this.leftWo3
    },
    leftKePic1 () {
      this.leftKe1 = !this.leftKe1
      console.log(this.leftKe1)
    },
    leftKePic2 () {
      this.leftKe2 = !this.leftKe2
      console.log(this.leftKe1)
    },
    leftKePic3 () {
      this.leftKe3 = !this.leftKe3
      console.log(this.leftKe1)
    },
    leftYuPic1 () {
      this.leftYu1 = !this.leftYu1
      console.log(this.leftKe1)
    },
    leftYuPic2 () {
      this.leftYu2 = !this.leftYu2
      console.log(this.leftKe1)
    },

    // 设备控制：乐观更新 + device.control + ACK/回滚
    genRequestId () {
      return 'req_' + Math.random().toString(36).slice(2) + Date.now()
    },
    isPending (deviceId) {
      return Object.values(this.pendingRequests).some(r => r.type === 'device' && r.deviceId === deviceId)
    },
    sendDeviceControl (deviceId, action, payload = {}) {
      const reqId = this.genRequestId()
      const ts = Date.now()
      // 乐观更新
      if (action === 'toggle') {
        const key = deviceId + 'Status'
        this[key] = !this[key]
      }
      if (deviceId === 'light' && (action === 'delta' || action === 'commit')) {
        this.lastUpdated.light = ts
      }
      if (deviceId === 'fan' && (action === 'delta' || action === 'commit')) {
        this.lastUpdated.fan = ts
      }
      // 记录请求
      this.pendingRequests[reqId] = {
        type: 'device', deviceId, action, start: ts,
        waitingTimer: setTimeout(() => { /* 3s 未ACK显示等待 */ }, 3000),
        rollbackTimer: setTimeout(() => {
          // 回滚
          if (action === 'toggle') {
            const key = deviceId + 'Status'
            this[key] = !this[key]
          }
          this.$toast && this.$toast({ message: '设备控制失败，已回滚', type: 'fail' })
          delete this.pendingRequests[reqId]
        }, 10000)
      }
      // 发事件（后端接入点）
      const evt = { event: 'device.control', deviceId, action, payload, requestId: reqId, timestamp: ts }
      this.$emit('device-control', evt)
    },
    onDeviceAck (e) {
      const { requestId, success } = e.detail || {}
      const req = this.pendingRequests[requestId]
      if (!req || req.type !== 'device') return
      clearTimeout(req.waitingTimer)
      clearTimeout(req.rollbackTimer)
      delete this.pendingRequests[requestId]
      if (!success) {
        // 回滚
        if (req.action === 'toggle') {
          const key = req.deviceId + 'Status'
          this[key] = !this[key]
        }
        this.$toast && this.$toast({ message: '设备控制失败，已回滚', type: 'fail' })
      } else {
        this.$toast && this.$toast({ message: '设备控制完成', type: 'success' })
      }
    },
    toggleDoorLock () {
      this.sendDeviceControl('doorLock', 'toggle')
      this.lastUpdated.doorLock = Date.now()
    },
    toggleLight () {
      this.sendDeviceControl('light', 'toggle')
      this.lastUpdated.light = Date.now()
    },
    toggleCurtain () {
      this.sendDeviceControl('curtain', 'toggle')
      this.lastUpdated.curtain = Date.now()
    },
    toggleFan () {
      this.sendDeviceControl('fan', 'toggle')
      this.lastUpdated.fan = Date.now()
    },
    toggleKettle () {
      this.sendDeviceControl('kettle', 'toggle')
      this.lastUpdated.kettle = Date.now()
    },
    toggleWifi () {
      this.sendDeviceControl('wifi', 'toggle')
      this.lastUpdated.wifi = Date.now()
    },
    onSliderInput (deviceId, value) {
      const s = this.sliderState[deviceId]
      if (s.debounceTimer) clearTimeout(s.debounceTimer)
      s.debounceTimer = setTimeout(() => {
        const now = Date.now()
        if (now - s.lastSentAt >= 250) {
          this.sendDeviceControl(deviceId, 'delta', { value })
          s.lastSentAt = now
        }
      }, 200)
    },
    onSliderCommit (deviceId, value) {
      this.sendDeviceControl(deviceId, 'commit', { value })
    },
    openDeviceDrawer (deviceId) {
      this.modalDeviceId = deviceId
      this.showDeviceDrawer = true
    },
    closeDeviceDrawer () {
      this.showDeviceDrawer = false
      this.modalDeviceId = null
    },
    openSceneTimer (deviceId) {
      this.$toast && this.$toast('打开场景定时器设置')
    },
    viewLogs (deviceId) {
      this.$toast && this.$toast('查看设备日志')
    },
    updateTime () {
      const d = new Date()
      const pad = n => String(n).padStart(2, '0')
      this.currentTimeStr = `${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
    },
    formatTimeAgo (ts) {
      if (!ts) return '—'
      const sec = Math.floor((Date.now() - ts) / 1000)
      if (sec < 5) return '刚刚'
      if (sec < 60) return `${sec} 秒前`
      const min = Math.floor(sec / 60)
      if (min === 1) return '1 分钟前'
      return `${min} 分钟前`
    }
  }
}
</script>

<style>
  .Control-index {
    width: 100%;
    height: 100%;
    background: #f5f7fb;
  }

  /* 顶部部分 */
  .Control-index .top {
    width: 96%;
    height: 200px;
    background: rgba(255, 255, 255, 0.9);
    border: 1px solid rgba(30, 92, 168, 0.3);
    padding: 20px;
    position: relative;
    margin-bottom: 30px;
    box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.15);
    backdrop-filter: blur(8px);
    -webkit-backdrop-filter: blur(8px);
    border-radius: 15px;
  }
  
  /* 顶部饼状图部分 */
  .Control-index .top .chart {
    height: 200px;
    width: 300px;
    position: absolute;
    left: 0px;
  }
  
  /* 顶部右侧部分 */
  .Control-index .top .top-right {
    width: 770px;
    height: 200px;
    display: flex;
    position: absolute;
    right: 10px;
  }
  
  /* 顶部右侧部分子项 */
  .Control-index .top .top-right .top-right-item {
    margin: 5px;
    width: 250px;
    background: rgba(255, 255, 255, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    flex-direction: column;
    border-radius: 12px;
    transition: all 0.3s ease;
    cursor: pointer;
    border: 1px solid rgba(30, 92, 168, 0.3);
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  }

  .Control-index .top .top-right .top-right-item:hover {
    transform: scale(1.03);
    box-shadow: 0 4px 12px rgba(33, 150, 243, 0.2);
  }

  /* 顶部右侧部分子项点击后的开关 */
  .Control-index .topKai {
    background: linear-gradient(135deg, #1e5ca8 0%, #3949ab 100%);
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    flex-direction: column;
    border-radius: 12px;
  }
  
  .Control-index .topGuan {
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    flex-direction: column;
    border-radius: 12px;
  }
  
  /* 底部左侧部分子项 */
  .Control-index .bottom .left .left-item {
    height: 180px;
    width: 100%;
    background: rgba(255, 255, 255, 0.9);
    border: 1px solid rgba(30, 92, 168, 0.3);
    margin-bottom: 15px;
    border-radius: 15px;
    align-items: center;
    display: flex;
    box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.15);
    backdrop-filter: blur(8px);
    -webkit-backdrop-filter: blur(8px);
    transition: all 0.3s ease;
  }
  
  .Control-index .bottom .left .left-item:hover {
    transform: translateY(-5px);
    box-shadow: 0 12px 40px 0 rgba(31, 38, 135, 0.2);
  }
  
  /* 底部左侧部分子项的大图标 */
  .Control-index .bottom .left .left-item .big {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 150px;
    width: 150px;
    background: linear-gradient(135deg, #1e5ca8, #3949ab);
    border-radius: 100%;
    margin-right: 10px;
    color: white;
    box-shadow: 0 4px 15px rgba(30, 92, 168, 0.3);
  }
  
  /* 统一标题样式 */
  .Control-index .bottom .left .left-item .small .title {
    margin-bottom: 15px;
    font-size: 20px;
    font-weight: bold;
    color: #1e5ca8;
  }
  
  /* 底部左侧部分子项小图标的背景 */
  .Control-index .small .pic .pic-item {
    background: linear-gradient(135deg, #1e5ca8, #3949ab);
    height: 40px;
    width: 40px;
    border-radius: 100%;
    margin-right: 10px;
    display: flex;
    justify-content: center;
    align-items: center;
    color: white;
    box-shadow: 0 2px 8px rgba(30, 92, 168, 0.3);
  }
  
  /* 底部左侧部分子项小图标的开关 */
  .Control-index .small .pic .pic-item .leftKai{
    background: linear-gradient(135deg, #219a3e, #17652c);
    height: 100%;
    width: 100%;
    border-radius: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    color: white;
    box-shadow: 0 2px 8px rgba(33, 154, 62, 0.3);
  }
  
  .Control-index .small .pic .pic-item .leftGuan{
    background: linear-gradient(135deg, #1e5ca8, #3949ab);
    height: 100%;
    width: 100%;
    border-radius: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    color: white;
    box-shadow: 0 2px 8px rgba(30, 92, 168, 0.3);
  }
  
  /* 统一标题颜色 */
  .Control-index .bottom .count .title{
    color: #1e5ca8;
    font-weight: bold;
    font-size: 24px;
  }
  
  /* 底部右侧上三个控制项 */
  .Control-index .bottom .count .up .up-item-a {
    width: 265px;
    box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.15);
    border-radius: 15px;
    background: rgba(255, 255, 255, 0.9);
    border: 1px solid rgba(30, 92, 168, 0.3);
    transition: all 0.3s ease;
  }
  
  .Control-index .bottom .count .up .up-item-a:hover {
    transform: translateY(-5px);
    box-shadow: 0 12px 40px 0 rgba(31, 38, 135, 0.2);
  }
  
  .Control-index .bottom .count .up .up-item-b {
    width: 395px;
    box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.15);
    border-radius: 15px;
    display: flex;
    background: rgba(255, 255, 255, 0.9);
    border: 1px solid rgba(30, 92, 168, 0.3);
    transition: all 0.3s ease;
  }
  
  .Control-index .bottom .count .up .up-item-b:hover {
    transform: translateY(-5px);
    box-shadow: 0 12px 40px 0 rgba(31, 38, 135, 0.2);
  }
  
  .Control-index .bottom .count .up .up-item-c {
    width: 265px;
    box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.15);
    border-radius: 15px;
    background: rgba(255, 255, 255, 0.9);
    border: 1px solid rgba(30, 92, 168, 0.3);
    transition: all 0.3s ease;
  }
  
  .Control-index .bottom .count .up .up-item-c:hover {
    transform: translateY(-5px);
    box-shadow: 0 12px 40px 0 rgba(31, 38, 135, 0.2);
  }
  
  /* 底部底侧三个子项 */
  .Control-index .bottom .count .under .under-item-a {
    width: 265px;
    box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.15);
    border-radius: 15px;
    display: flex;
    background: rgba(255, 255, 255, 0.9);
    border: 1px solid rgba(30, 92, 168, 0.3);
    transition: all 0.3s ease;
  }
  
  .Control-index .bottom .count .under .under-item-a:hover {
    transform: translateY(-5px);
    box-shadow: 0 12px 40px 0 rgba(31, 38, 135, 0.2);
  }
  
  /* 水壶 */
  .Control-index .bottom .count .under .under-item-b {
    width: 395px;
    box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.15);
    border-radius: 15px;
    display: flex;
    background: rgba(255, 255, 255, 0.9);
    border: 1px solid rgba(30, 92, 168, 0.3);
    transition: all 0.3s ease;
  }
  
  .Control-index .bottom .count .under .under-item-b:hover {
    transform: translateY(-5px);
    box-shadow: 0 12px 40px 0 rgba(31, 38, 135, 0.2);
  }
  
  /* wifi */
  .Control-index .bottom .count .under .under-item-c {
    width: 265px;
    box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.15);
    border-radius: 15px;
    display: flex;
    background: rgba(255, 255, 255, 0.9);
    border: 1px solid rgba(30, 92, 168, 0.3);
    transition: all 0.3s ease;
  }
  
  .Control-index .bottom .count .under .under-item-c:hover {
    transform: translateY(-5px);
    box-shadow: 0 12px 40px 0 rgba(31, 38, 135, 0.2);
  }
  
  /* 门锁的右侧放置图标 */
  .Control-index .bottom .count .up .right-pic{
    display: flex;
    justify-content: center;
    align-items: center;
    color: #1e5ca8;
  }
  
  /* 三个子项的右侧放置图标 */
  .Control-index .bottom .count .under .right-pic{
    display: flex;
    justify-content: center;
    align-items: center;
    color: #1e5ca8;
  }
  
  /* 底部右侧上三个控制项的在上侧放置图标的地方 */
  .Control-index .bottom .count .up .up-pic {
    height: 250px;
    display: flex;
    justify-content: center;
    align-items: center;
    color: #1e5ca8;
  }

  .Control-index .bottom .count .up .up-under {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
  }

  .Control-index .bottom .count .up .up-under .title {
    margin-bottom: 15px;
  }

  /* 底部右侧上三个控制项 */
  .Control-index .bottom .count .up .up-item-a {
    width: 265px;
    box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.15);
    border-radius: 15px;
    display: flex;
    background: rgba(255, 255, 255, 0.9);
    border: 1px solid rgba(30, 92, 168, 0.3);
    transition: all 0.3s ease;
  }

  .Control-index .bottom .count .up .up-item-a:hover {
    transform: translateY(-5px);
    box-shadow: 0 12px 40px 0 rgba(31, 38, 135, 0.2);
  }

  .Control-index .bottom .count .up .up-item-b {
    width: 395px;
    box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.15);
    border-radius: 15px;
    display: flex;
    background: rgba(255, 255, 255, 0.9);
    border: 1px solid rgba(30, 92, 168, 0.3);
    transition: all 0.3s ease;
  }

  .Control-index .bottom .count .up .up-item-b:hover {
    transform: translateY(-5px);
    box-shadow: 0 12px 40px 0 rgba(31, 38, 135, 0.2);
  }

  .Control-index .bottom .count .up .up-item-c {
    width: 265px;
    box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.15);
    border-radius: 15px;
    display: flex;
    background: rgba(255, 255, 255, 0.9);
    border: 1px solid rgba(30, 92, 168, 0.3);
    transition: all 0.3s ease;
  }

  .Control-index .bottom .count .up .up-item-c:hover {
    transform: translateY(-5px);
    box-shadow: 0 12px 40px 0 rgba(31, 38, 135, 0.2);
  }
  
  /* 底部底侧三个子项 */
  .Control-index .bottom .count .under .under-item-a {
    width: 265px;
    box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.15);
    border-radius: 15px;
    display: flex;
    background: rgba(255, 255, 255, 0.9);
    border: 1px solid rgba(30, 92, 168, 0.3);
    transition: all 0.3s ease;
  }

  .Control-index .bottom .count .under .under-item-a:hover {
    transform: translateY(-5px);
    box-shadow: 0 12px 40px 0 rgba(31, 38, 135, 0.2);
  }
  
  /* 水壶 */
  .Control-index .bottom .count .under .under-item-b {
    width: 395px;
    box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.15);
    border-radius: 15px;
    display: flex;
    background: rgba(255, 255, 255, 0.9);
    border: 1px solid rgba(30, 92, 168, 0.3);
    transition: all 0.3s ease;
  }

  .Control-index .bottom .count .under .under-item-b:hover {
    transform: translateY(-5px);
    box-shadow: 0 12px 40px 0 rgba(31, 38, 135, 0.2);
  }
  
  /* wifi */
  .Control-index .bottom .count .under .under-item-c {
    width: 265px;
    box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.15);
    border-radius: 15px;
    display: flex;
    background: rgba(255, 255, 255, 0.9);
    border: 1px solid rgba(30, 92, 168, 0.3);
    transition: all 0.3s ease;
  }

  .Control-index .bottom .count .under .under-item-c:hover {
    transform: translateY(-5px);
    box-shadow: 0 12px 40px 0 rgba(31, 38, 135, 0.2);
  }
  
  /* 门锁的右侧放置图标 */
  .Control-index .bottom .count .up .right-pic{
    display: flex;
    justify-content: center;
    align-items: center;
    color: #1e5ca8;
  }
  
  /* 三个子项的右侧放置图标 */
  .Control-index .bottom .count .under .right-pic{
    display: flex;
    justify-content: center;
    align-items: center;
    color: #1e5ca8;
  }
  
  /* 底部右侧上三个控制项的在上侧放置图标的地方 */
  .Control-index .bottom .count .up .up-pic {
    height: 250px;
    display: flex;
    justify-content: center;
    align-items: center;
    color: #1e5ca8;
  }

  .Control-index .bottom .count .up .up-under {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
  }

  .Control-index .bottom .count .up .up-under .title {
    margin-bottom: 15px;
  }

  /* 底部右侧上三个控制项 */
  .Control-index .bottom .count .up .up-item-a {
    width: 265px;
    box-shadow: 1px 1px 1px 1px rgba(0,0,0,0.2);
    border-radius: 10px;
    background-color: #FFF8E1;
  }

  .Control-index .bottom .count .up .up-item-b {
    width: 395px;
    box-shadow: 1px 1px 1px 1px rgba(0,0,0,0.2);
    border-radius: 10px;
    display: flex;
    background-color: #FFF8E1;
  }

    /* 底部右侧上三个控制项的在左侧放置按钮的地方 */
  .Control-index .bottom .count .up .up-item-b .zi-left{
    width: 125px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    position: relative;
  }

  /* 底部右侧上三个控制项的在右侧放置图标的地方 */
  .Control-index .bottom .count .up .up-item-b .right-pic{
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .Control-index .bottom .count .up .up-item-b .zi-left .title {
    position: absolute;
    top: 15px;
  }

  .Control-index .bottom .count .up .up-item-b .zi-left .btn{
    margin-top: 20px;
  }

  .Control-index .bottom .count .up .up-item-c {
    width: 265px;
    box-shadow: 1px 1px 1px 1px rgba(0,0,0,0.2);
    border-radius: 10px;
    background-color: #FFF8E1;
  }

  /* 底部底侧 */
  .Control-index .bottom .count .under {
    display: flex;
    height: 180px;
    justify-content: space-around;
  }

  /* 底部底侧三个子项 */
  .Control-index .bottom .count .under .under-item-a {
    width: 265px;
    box-shadow: 1px 1px 1px 1px rgba(0,0,0,0.2);
    border-radius: 10px;
    display: flex;
    background-color: #FFF8E1;
  }
  /* 风扇的左侧包含标题和开关 */
  .Control-index .bottom .count .under .under-item-a .zi-left{
    width: 125px;
    display: flex;
    justify-content:space-around;
    align-items: center;
    flex-direction: column;
  }

  /* 水壶 */
  .Control-index .bottom .count .under .under-item-b {
    width: 395px;
    box-shadow: 1px 1px 1px 1px rgba(0,0,0,0.2);
    border-radius: 10px;
    display: flex;
    background-color: #FFF8E1;
  }
/* 水壶的左侧包含标题和开关 */
  .Control-index .bottom .count .under .under-item-b .zi-left{
    width: 225px;
    display: flex;
    justify-content:space-around;
    align-items: center;
    flex-direction: column;
  }

  /* wifi */
  .Control-index .bottom .count .under .under-item-c {
    width: 265px;
    box-shadow: 1px 1px 1px 1px rgba(0,0,0,0.2);
    border-radius: 10px;
    display: flex;
    background-color: #FFF8E1;
  }
/* wifi的左侧包含标题和开关 */
  .Control-index .bottom .count .under .under-item-c .zi-left{
    width: 125px;
    display: flex;
    justify-content:space-around;
    align-items: center;
    flex-direction: column;
  }

  /* 三个子项的右侧放置图标 */
  .Control-index .bottom .count .under .right-pic{
    display: flex;
    justify-content: center;
    align-items: center;
  }
  
  /* 返回首页按钮样式 */
  .back-to-home {
    position: absolute;
    top: 20px;
    right: 20px;
    display: flex;
    align-items: center;
    gap: 5px;
    padding: 8px 16px;
    background: linear-gradient(135deg, #1e5ca8, #3949ab);
    color: white;
    border: none;
    border-radius: 20px;
    cursor: pointer;
    font-size: 14px;
    font-weight: 500;
    box-shadow: 0 4px 10px rgba(30, 92, 168, 0.3);
    transition: all 0.3s ease;
    z-index: 100;
  }
  
  .back-to-home:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 15px rgba(30, 92, 168, 0.4);
    background: linear-gradient(135deg, #256dc1, #445ebd);
  }
  
  .back-to-home i {
    font-size: 16px;
  }
  
  /* 响应式调整 */
  @media (max-width: 768px) {
    .back-to-home {
      top: 10px;
      right: 10px;
      padding: 6px 12px;
      font-size: 12px;
    }
    
    .back-to-home i {
      font-size: 14px;
    }
  }
/* 控制页头部导航与场景切换 */
.Control-index .control-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 24px;
  margin: 16px 24px 12px;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(6px);
  border: 1px solid rgba(30, 92, 168, 0.2);
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(31, 38, 135, 0.12);
}
.Control-index .header-left .header-title {
  font-size: 20px;
  font-weight: 600;
  color: #1e5ca8;
  margin: 0;
}
.Control-index .header-left .header-subtitle {
  font-size: 12px;
  color: #666;
  margin: 2px 0 0;
}
.Control-index .header-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}
.Control-index .global-segments {
  display: inline-flex;
  padding: 6px;
  gap: 6px;
  border-radius: 12px;
  background: rgba(15, 23, 42, 0.06);
  border: 1px solid rgba(15, 23, 42, 0.1);
}
.Control-index .seg-button {
  padding: 8px 16px;
  border-radius: 10px;
  border: 1px solid rgba(15, 23, 42, 0.12);
  background: #ffffff;
  color: #0f172a;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
}
.Control-index .seg-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 16px rgba(15, 23, 42, 0.14);
}
.Control-index .seg-button.active {
  background: linear-gradient(135deg, #2563eb, #1d4ed8);
  color: #fff;
  border-color: rgba(29, 78, 216, 0.8);
  box-shadow: 0 6px 16px rgba(29, 78, 216, 0.28);
}

/* 正在使用的小卡片区域 */
.Control-index .active-devices {
  margin: 0 24px 12px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.Control-index .active-chip {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 10px;
  border: 1px solid rgba(15, 23, 42, 0.12);
  border-radius: 10px;
  background: #fff;
  color: #0f172a;
  font-size: 12px;
  box-shadow: 0 2px 8px rgba(2, 6, 23, 0.06);
  cursor: pointer;
}
.Control-index .active-chip.on {
  background: #f0fdf4;
  border-color: rgba(22, 163, 74, 0.4);
  color: #14532d;
}
.Control-index .active-chip .label { font-weight: 600; }
.Control-index .active-chip .value { color: #475569; }

/* 数据分析卡片样式 */
.Control-index .control-overview { margin: 0 24px 12px; }
.Control-index .overview-card {
  border-radius: 12px;
  background: #fff;
  border: 1px solid rgba(15, 23, 42, 0.12);
  box-shadow: 0 4px 12px rgba(15, 23, 42, 0.12);
}
.Control-index .overview-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 12px;
  border-bottom: 1px solid rgba(15, 23, 42, 0.06);
}
.Control-index .ov-title { font-size: 14px; font-weight: 600; color: #0f172a; }
.Control-index .ov-badges { display: flex; gap: 8px; }
.Control-index .ov-badge { font-size: 12px; padding: 4px 8px; border-radius: 999px; border: 1px solid rgba(15, 23, 42, 0.12); }
.Control-index .ov-badge.on { background: #f0fdf4; color: #166534; border-color: rgba(22, 163, 74, 0.4); }
.Control-index .ov-badge.off { background: #f8fafc; color: #334155; }
.Control-index .overview-chart {
  width: 100%;
  height: 180px;
  background: transparent;
  box-shadow: none;
  border: none;
  border-radius: 0 0 12px 12px;
}

/* 主体布局：左侧房间导航 + 右侧设备内容 */
.Control-index .bottom {
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: 24px;
  align-items: start;
  padding: 0 24px 24px;
}
.Control-index .bottom .left {
  position: sticky;
  top: 16px;
}

@media (max-width: 1024px) {
  .Control-index .bottom {
    grid-template-columns: 1fr;
  }
  .Control-index .bottom .left {
    position: static;
  }
}
/* 头部左侧与返回按钮的排版优化 */
.Control-index .control-header .header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* 统一设备卡片风格：白底+细边+轻阴影 */
.Control-index .bottom .count .up > div,
.Control-index .bottom .count .under > div {
  background: #fff !important;
  border: 1px solid rgba(30, 92, 168, 0.15);
  box-shadow: 0 6px 16px rgba(31, 38, 135, 0.1);
  border-radius: 12px;
  padding: 12px;
  width: auto;
}

/* 设备网格布局：更整齐的三列栅格，响应式换行 */
.Control-index .bottom .count .up,
.Control-index .bottom .count .under {
  display: grid;
  grid-template-columns: repeat(3, minmax(240px, 1fr));
  gap: 16px;
}

/* 去除设备项的固定宽度以适配网格 */
.Control-index .bottom .count .up .zi-left,
.Control-index .bottom .count .under .zi-left {
  width: auto;
}

/* 统一背景（覆盖旧的米色/杂色） */
.Control-index .bottom .count .up .up-item-a,
.Control-index .bottom .count .up .up-item-b,
.Control-index .bottom .count .up .up-item-c,
.Control-index .bottom .count .under .under-item-a,
.Control-index .bottom .count .under .under-item-b,
.Control-index .bottom .count .under .under-item-c {
  background-color: #fff !important;
}

/* 标题与图标更清晰的对齐与间距 */
.Control-index .bottom .count .zi-left .font,
.Control-index .bottom .count .zi-left .font-color {
  color: #1e293b;
}
.Control-index .bottom .count .zi-left {
  gap: 8px;
}

/* 右侧图示区域最小高度，避免拥挤 */
.Control-index .bottom .count .right-pic {
  min-height: 120px;
}

/* 响应式：窄屏降为两列、一列 */
@media (max-width: 1200px) {
  .Control-index .bottom .count .up,
  .Control-index .bottom .count .under {
    grid-template-columns: repeat(2, minmax(240px, 1fr));
  }
}
@media (max-width: 768px) {
  .Control-index .bottom .count .up,
  .Control-index .bottom .count .under {
    grid-template-columns: 1fr;
  }
}
/* 左侧房间卡片统一风格 */
.Control-index .bottom .left .left-item {
  background: #fff !important;
  border: 1px solid rgba(30, 92, 168, 0.15);
  border-radius: 12px;
  box-shadow: 0 6px 16px rgba(31, 38, 135, 0.1);
  padding: 12px;
}

/* 左侧小图标样式弱化为更现代的胶囊色 */
.Control-index .small .pic .pic-item {
  background: #f7f9fc !important;
  border: 1px solid rgba(30, 92, 168, 0.15);
  height: 40px;
  width: 40px;
  border-radius: 20px;
  color: #1e293b;
  box-shadow: none !important;
}
.Control-index .small .pic .pic-item .leftKai {
  background: linear-gradient(135deg, #16a34a, #15803d) !important;
  color: #fff !important;
  box-shadow: none !important;
}
.Control-index .small .pic .pic-item .leftGuan {
  background: #f7f9fc !important;
  color: #1e293b !important;
  box-shadow: none !important;
}

/* 设备卡片标题统一为中性可读 */
.Control-index .bottom .count .title {
  color: #0f172a !important;
  font-weight: 600 !important;
  font-size: 18px !important;
}

/* 设备图标颜色与卡片内边距统一 */
.Control-index .bottom .count .right-pic,
.Control-index .bottom .count .up .up-pic {
  color: #64748b !important;
}
.Control-index .bottom .count .up > div,
.Control-index .bottom .count .under > div {
  padding: 16px !important;
}

/* 卡片悬停弱化，轻微浮动即可 */
.Control-index .bottom .count .up > div:hover,
.Control-index .bottom .count .under > div:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(31, 38, 135, 0.12);
}

/* 房间标题区与元素间距优化 */
.Control-index .left .title {
  font-size: 16px !important;
  color: #0f172a !important;
  font-weight: 600 !important;
}
.Control-index .small {
  gap: 8px !important;
}

/* 修复左侧房间卡片溢出与下方越界问题 */
.Control-index { min-height: 100vh; }
.Control-index .bottom { grid-template-columns: 340px 1fr !important; }
.Control-index .bottom .left { position: static !important; overflow: auto; max-height: calc(100vh - 280px); padding-right: 6px; }
.Control-index .bottom .left .left-item {
  height: auto !important;
  min-height: 140px !important;
  overflow: hidden;
}
.Control-index .bottom .left .left-item .big {
  height: 120px !important;
  width: 120px !important;
}
.Control-index .bottom .left .left-item .small { display: flex; flex-direction: column; }
.Control-index .bottom .left .left-item .small .pic {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.Control-index .small .pic .pic-item { margin-bottom: 8px; }
</style>

<style>
/* 底部整体布局：左侧房间列表 + 右侧控制面板 */
.Control-index .bottom {
  display: grid;
  grid-template-columns: 340px 1fr;
  gap: 20px;
  align-items: start;
}

/* 左侧房间列为纵向列表 */
.Control-index .bottom .left {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

/* 右侧控制面板的上/下区域统一使用网格布局 */
.Control-index .bottom .count .up {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 20px;
}

.Control-index .bottom .count .under {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 20px;
  height: auto;
  justify-content: initial;
}

/* 取消固定宽度，让卡片自适应网格列 */
.Control-index .bottom .count .up .up-item-a,
.Control-index .bottom .count .up .up-item-b,
.Control-index .bottom .count .up .up-item-c,
.Control-index .bottom .count .under .under-item-a,
.Control-index .bottom .count .under .under-item-b,
.Control-index .bottom .count .under .under-item-c {
  width: auto;
}
  
  .Control-index .bottom .count .under .under-item-a:hover {
    transform: translateY(-5px);
    box-shadow: 0 12px 40px 0 rgba(31, 38, 135, 0.2);
  }
  
  /* 水壶 */
  .Control-index .bottom .count .under .under-item-b {
    width: 395px;
    box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.15);
    border-radius: 15px;
    display: flex;
    background: rgba(255, 255, 255, 0.9);
    border: 1px solid rgba(30, 92, 168, 0.3);
    transition: all 0.3s ease;
  }
  
  .Control-index .bottom .count .under .under-item-b:hover {
    transform: translateY(-5px);
    box-shadow: 0 12px 40px 0 rgba(31, 38, 135, 0.2);
  }
  
  /* wifi */
  .Control-index .bottom .count .under .under-item-c {
    width: 265px;
    box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.15);
    border-radius: 15px;
    display: flex;
    background: rgba(255, 255, 255, 0.9);
    border: 1px solid rgba(30, 92, 168, 0.3);
    transition: all 0.3s ease;
  }
  
  .Control-index .bottom .count .under .under-item-c:hover {
    transform: translateY(-5px);
    box-shadow: 0 12px 40px 0 rgba(31, 38, 135, 0.2);
  }
  
  /* 门锁的右侧放置图标 */
  .Control-index .bottom .count .up .right-pic{
    display: flex;
    justify-content: center;
    align-items: center;
    color: #1e5ca8;
  }
  
  /* 三个子项的右侧放置图标 */
  .Control-index .bottom .count .under .right-pic{
    display: flex;
    justify-content: center;
    align-items: center;
    color: #1e5ca8;
  }
  
  /* 底部右侧上三个控制项的在上侧放置图标的地方 */
  .Control-index .bottom .count .up .up-pic {
    height: 250px;
    display: flex;
    justify-content: center;
    align-items: center;
    color: #1e5ca8;
  }

  .Control-index .bottom .count .up .up-under {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
  }

  .Control-index .bottom .count .up .up-under .title {
    margin-bottom: 15px;
  }

  /* 底部右侧上三个控制项 */
  .Control-index .bottom .count .up .up-item-a {
    width: 265px;
    box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.15);
    border-radius: 15px;
    display: flex;
    background: rgba(255, 255, 255, 0.9);
    border: 1px solid rgba(30, 92, 168, 0.3);
    transition: all 0.3s ease;
  }

  .Control-index .bottom .count .up .up-item-a:hover {
    transform: translateY(-5px);
    box-shadow: 0 12px 40px 0 rgba(31, 38, 135, 0.2);
  }

  .Control-index .bottom .count .up .up-item-b {
    width: 395px;
    box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.15);
    border-radius: 15px;
    display: flex;
    background: rgba(255, 255, 255, 0.9);
    border: 1px solid rgba(30, 92, 168, 0.3);
    transition: all 0.3s ease;
  }

  .Control-index .bottom .count .up .up-item-b:hover {
    transform: translateY(-5px);
    box-shadow: 0 12px 40px 0 rgba(31, 38, 135, 0.2);
  }

  .Control-index .bottom .count .up .up-item-c {
    width: 265px;
    box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.15);
    border-radius: 15px;
    display: flex;
    background: rgba(255, 255, 255, 0.9);
    border: 1px solid rgba(30, 92, 168, 0.3);
    transition: all 0.3s ease;
  }

  .Control-index .bottom .count .up .up-item-c:hover {
    transform: translateY(-5px);
    box-shadow: 0 12px 40px 0 rgba(31, 38, 135, 0.2);
  }
  
  /* 底部底侧三个子项 */
  .Control-index .bottom .count .under .under-item-a {
    width: 265px;
    box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.15);
    border-radius: 15px;
    display: flex;
    background: rgba(255, 255, 255, 0.9);
    border: 1px solid rgba(30, 92, 168, 0.3);
    transition: all 0.3s ease;
  }

  .Control-index .bottom .count .under .under-item-a:hover {
    transform: translateY(-5px);
    box-shadow: 0 12px 40px 0 rgba(31, 38, 135, 0.2);
  }
  
  /* 水壶 */
  .Control-index .bottom .count .under .under-item-b {
    width: 395px;
    box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.15);
    border-radius: 15px;
    display: flex;
    background: rgba(255, 255, 255, 0.9);
    border: 1px solid rgba(30, 92, 168, 0.3);
    transition: all 0.3s ease;
  }

  .Control-index .bottom .count .under .under-item-b:hover {
    transform: translateY(-5px);
    box-shadow: 0 12px 40px 0 rgba(31, 38, 135, 0.2);
  }
  
  /* wifi */
  .Control-index .bottom .count .under .under-item-c {
    width: 265px;
    box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.15);
    border-radius: 15px;
    display: flex;
    background: rgba(255, 255, 255, 0.9);
    border: 1px solid rgba(30, 92, 168, 0.3);
    transition: all 0.3s ease;
  }

  .Control-index .bottom .count .under .under-item-c:hover {
    transform: translateY(-5px);
    box-shadow: 0 12px 40px 0 rgba(31, 38, 135, 0.2);
  }
  
  /* 门锁的右侧放置图标 */
  .Control-index .bottom .count .up .right-pic{
    display: flex;
    justify-content: center;
    align-items: center;
    color: #1e5ca8;
  }
  
  /* 三个子项的右侧放置图标 */
  .Control-index .bottom .count .under .right-pic{
    display: flex;
    justify-content: center;
    align-items: center;
    color: #1e5ca8;
  }
  
  /* 底部右侧上三个控制项的在上侧放置图标的地方 */
  .Control-index .bottom .count .up .up-pic {
    height: 250px;
    display: flex;
    justify-content: center;
    align-items: center;
    color: #1e5ca8;
  }

  .Control-index .bottom .count .up .up-under {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
  }

  .Control-index .bottom .count .up .up-under .title {
    margin-bottom: 15px;
  }

  /* 底部右侧上三个控制项 */
  .Control-index .bottom .count .up .up-item-a {
    width: 265px;
    box-shadow: 1px 1px 1px 1px rgba(0,0,0,0.2);
    border-radius: 10px;
    background-color: #FFF8E1;
  }

  .Control-index .bottom .count .up .up-item-b {
    width: 395px;
    box-shadow: 1px 1px 1px 1px rgba(0,0,0,0.2);
    border-radius: 10px;
    display: flex;
    background-color: #FFF8E1;
  }

    /* 底部右侧上三个控制项的在左侧放置按钮的地方 */
  .Control-index .bottom .count .up .up-item-b .zi-left{
    width: 125px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    position: relative;
  }

  /* 底部右侧上三个控制项的在右侧放置图标的地方 */
  .Control-index .bottom .count .up .up-item-b .right-pic{
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .Control-index .bottom .count .up .up-item-b .zi-left .title {
    position: absolute;
    top: 15px;
  }

  .Control-index .bottom .count .up .up-item-b .zi-left .btn{
    margin-top: 20px;
  }

  .Control-index .bottom .count .up .up-item-c {
    width: 265px;
    box-shadow: 1px 1px 1px 1px rgba(0,0,0,0.2);
    border-radius: 10px;
    background-color: #FFF8E1;
  }

  /* 底部底侧 */
  .Control-index .bottom .count .under {
    display: flex;
    height: 180px;
    justify-content: space-around;
  }

  /* 底部底侧三个子项 */
  .Control-index .bottom .count .under .under-item-a {
    width: 265px;
    box-shadow: 1px 1px 1px 1px rgba(0,0,0,0.2);
    border-radius: 10px;
    display: flex;
    background-color: #FFF8E1;
  }
  /* 风扇的左侧包含标题和开关 */
  .Control-index .bottom .count .under .under-item-a .zi-left{
    width: 125px;
    display: flex;
    justify-content:space-around;
    align-items: center;
    flex-direction: column;
  }

  /* 水壶 */
  .Control-index .bottom .count .under .under-item-b {
    width: 395px;
    box-shadow: 1px 1px 1px 1px rgba(0,0,0,0.2);
    border-radius: 10px;
    display: flex;
    background-color: #FFF8E1;
  }
/* 水壶的左侧包含标题和开关 */
  .Control-index .bottom .count .under .under-item-b .zi-left{
    width: 225px;
    display: flex;
    justify-content:space-around;
    align-items: center;
    flex-direction: column;
  }

  /* wifi */
  .Control-index .bottom .count .under .under-item-c {
    width: 265px;
    box-shadow: 1px 1px 1px 1px rgba(0,0,0,0.2);
    border-radius: 10px;
    display: flex;
    background-color: #FFF8E1;
  }
/* wifi的左侧包含标题和开关 */
  .Control-index .bottom .count .under .under-item-c .zi-left{
    width: 125px;
    display: flex;
    justify-content:space-around;
    align-items: center;
    flex-direction: column;
  }

  /* 三个子项的右侧放置图标 */
  .Control-index .bottom .count .under .right-pic{
    display: flex;
    justify-content: center;
    align-items: center;
  }
  
  /* 返回首页按钮样式 */
  .back-to-home {
    position: absolute;
    top: 20px;
    right: 20px;
    display: flex;
    align-items: center;
    gap: 5px;
    padding: 8px 16px;
    background: linear-gradient(135deg, #1e5ca8, #3949ab);
    color: white;
    border: none;
    border-radius: 20px;
    cursor: pointer;
    font-size: 14px;
    font-weight: 500;
    box-shadow: 0 4px 10px rgba(30, 92, 168, 0.3);
    transition: all 0.3s ease;
    z-index: 100;
  }
  
  .back-to-home:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 15px rgba(30, 92, 168, 0.4);
    background: linear-gradient(135deg, #256dc1, #445ebd);
  }
  
  .back-to-home i {
    font-size: 16px;
  }
  
  /* 响应式调整 */
  @media (max-width: 768px) {
    .back-to-home {
      top: 10px;
      right: 10px;
      padding: 6px 12px;
      font-size: 12px;
    }
    
    .back-to-home i {
      font-size: 14px;
    }
  }

/* 设备卡片的右侧图标容器用于定位等待指示器 */
.Control-index .bottom .count .right-pic,
.Control-index .bottom .count .under .right-pic {
  position: relative;
}

/* 等待指示器：在进行中的控制请求显示脉冲小圆点 */
.pending-dot {
  position: absolute;
  top: 8px;
  right: 8px;
  width: 10px;
  height: 10px;
  background: #f59e0b;
  border-radius: 50%;
  box-shadow: 0 0 0 0 rgba(245, 158, 11, 0.7);
  animation: pulse 1.5s infinite;
}
@keyframes pulse {
  0% { box-shadow: 0 0 0 0 rgba(245, 158, 11, 0.7); }
  70% { box-shadow: 0 0 0 10px rgba(245, 158, 11, 0); }
  100% { box-shadow: 0 0 0 0 rgba(245, 158, 11, 0); }
}

/* 状态行样式：展示在线/离线、数值与更新时间 */
.status-row {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #64748b;
}
.status-row .state {
  padding: 2px 8px;
  border-radius: 999px;
  background: #f1f5f9;
  border: 1px solid #e2e8f0;
}
.status-row .state.online {
  background: #dcfce7;
  border-color: #86efac;
  color: #166534;
}
.status-row .sep {
  color: #cbd5e1;
}
.status-row .time {
  color: #94a3b8;
}

/* 更多按钮样式：简洁的轻边胶囊按钮 */
.more-btn {
  margin-top: 8px;
  padding: 6px 10px;
  border: 1px solid rgba(30, 92, 168, 0.25);
  border-radius: 8px;
  background: #f7f9fc;
  color: #1e5ca8;
  cursor: pointer;
  transition: all 0.2s ease;
}
.more-btn:hover {
  background: #eef3fb;
  transform: translateY(-1px);
  box-shadow: 0 6px 16px rgba(31, 38, 135, 0.12);
}

/* 滑杆样式：统一外观 */
.slider-row input[type="range"] {
  width: 100%;
  accent-color: #1e5ca8;
  height: 4px;
  border-radius: 999px;
}

/* 全局卡片阴影与边框更轻 */
.Control-index .bottom .count .up > div,
.Control-index .bottom .count .under > div {
  box-shadow: 0 4px 12px rgba(2, 6, 23, 0.06) !important;
  border: 1px solid rgba(15, 23, 42, 0.08) !important;
}

/* 统一设备图标尺寸 */
.Control-index .bottom .count .right-pic .iconfont,
.Control-index .bottom .count .up .up-pic .iconfont {
  font-size: 140px !important;
}

/* 顶部卡片阴影更轻 */
.Control-index .top {
  box-shadow: 0 6px 16px rgba(2, 6, 23, 0.08) !important;
  border: 1px solid rgba(15, 23, 42, 0.08) !important;
  background: #ffffffcc !important;
}

/* 控制页头风格更简洁 */
.Control-index .control-header {
  background: #ffffffcc !important;
  border: 1px solid rgba(15, 23, 42, 0.08) !important;
  box-shadow: 0 6px 16px rgba(2, 6, 23, 0.08) !important;
}

/* 主控界面统一栅格与卡片尺寸 */
.Control-index .bottom .count .up,
.Control-index .bottom .count .under {
  display: grid !important;
  grid-template-columns: repeat(3, minmax(280px, 1fr)) !important;
  gap: 16px !important;
  height: auto !important;
  justify-content: initial !important;
}

.Control-index .bottom .count .up .up-item-a,
.Control-index .bottom .count .up .up-item-b,
.Control-index .bottom .count .up .up-item-c,
.Control-index .bottom .count .under .under-item-a,
.Control-index .bottom .count .under .under-item-b,
.Control-index .bottom .count .under .under-item-c {
  width: auto !important;
  min-height: 180px;
  display: grid !important;
  grid-template-columns: 1fr auto; /* 左侧信息 + 右侧图标 */
  align-items: center;
}

.Control-index .bottom .count .up .up-item-b .zi-left,
.Control-index .bottom .count .under .under-item-a .zi-left,
.Control-index .bottom .count .under .under-item-b .zi-left,
.Control-index .bottom .count .under .under-item-c .zi-left {
  width: auto !important;
}

.Control-index .bottom .count .right-pic {
  min-height: 120px !important;
  display: flex;
  align-items: center;
  justify-content: center;
}

.Control-index .bottom .count .up .up-pic {
  min-height: 180px !important;
  display: flex;
  align-items: center;
  justify-content: center;
}

@media (max-width: 1200px) {
  .Control-index .bottom .count .up,
  .Control-index .bottom .count .under {
    grid-template-columns: repeat(2, minmax(280px, 1fr)) !important;
  }
}
@media (max-width: 768px) {
  .Control-index .bottom .count .up,
  .Control-index .bottom .count .under {
    grid-template-columns: 1fr !important;
  }
}

/* 设备详情抽屉样式（底部浮窗） */
.device-drawer {
  position: fixed;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  background: rgba(2, 6, 23, 0.45);
  backdrop-filter: blur(2px);
  display: flex;
  align-items: flex-end; /* 底部弹出 */
  justify-content: center;
  z-index: 1000;
}
.drawer-content {
  width: 100%;
  max-width: 960px;
  background: #fff;
  border-radius: 16px 16px 0 0;
  box-shadow: 0 -8px 24px rgba(2, 6, 23, 0.12);
  border: 1px solid rgba(15, 23, 42, 0.08);
  padding: 16px;
}
.drawer-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}
.drawer-title {
  font-size: 16px;
  font-weight: 600;
  color: #0f172a;
}
.drawer-close {
  border: none;
  background: transparent;
  color: #64748b;
  font-size: 20px;
  cursor: pointer;
}
.drawer-close:hover { color: #0f172a; }
.drawer-body { display: flex; flex-direction: column; gap: 8px; }
.drawer-row { font-size: 13px; color: #475569; }
.drawer-actions { display: flex; gap: 8px; margin-top: 12px; }
.drawer-actions .primary {
  padding: 8px 12px;
  border-radius: 8px;
  background: #0f172a;
  color: #fff;
  border: 1px solid #0f172a;
  cursor: pointer;
}
.drawer-actions .secondary {
  padding: 8px 12px;
  border-radius: 8px;
  background: #f7f9fc;
  color: #0f172a;
  border: 1px solid rgba(15, 23, 42, 0.12);
  cursor: pointer;
}
.drawer-actions .primary:hover { box-shadow: 0 4px 12px rgba(2,6,23,0.18); }
.drawer-actions .secondary:hover { box-shadow: 0 4px 12px rgba(2,6,23,0.12); }
</style>
