<template>
  <div class="video-monitor-container">
    <div class="video-monitor">
      <!-- 顶部状态栏 -->
      <header class="status-bar">
        <div class="status-left">
          <button class="back-button" @click="goBack" title="返回首页">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="15 18 9 12 15 6"></polyline>
            </svg>
            返回首页
          </button>
          <h1 class="title">视频监控系统</h1>
          <div class="system-status">
            <span class="status-indicator online"></span>
            <span>系统正常运行</span>
          </div>
        </div>
        <div class="status-right">
           <div class="connection-info">
             <span>连接数: {{ connectionCount }}</span>
           </div>
         </div>
      </header>

      <!-- 主内容区域 -->
      <main class="main-content">
        <!-- 左侧数据面板 -->
        <aside class="data-panel">
          <div class="panel-section">
            <h3 class="section-title">监控数据</h3>
            <div class="data-cards">
              <div class="data-card">
                <div class="data-label">帧率</div>
                <div class="data-value">{{ frameRate }} FPS</div>
              </div>
              <div class="data-card">
                <div class="data-label">码率</div>
                <div class="data-value">{{ bitrate }} kbps</div>
              </div>
              <div class="data-card">
                <div class="data-label">分辨率</div>
                <div class="data-value">{{ resolution }}</div>
              </div>
              <div class="data-card">
                <div class="data-label">延迟</div>
                <div class="data-value">{{ latency }} ms</div>
              </div>
            </div>
          </div>
          
          <div class="panel-section">
            <h3 class="section-title">服务器状态</h3>
            <div class="server-stats">
              <div class="stat-item">
                <span class="stat-label">CPU使用率:</span>
                <span class="stat-value">{{ cpuUsage }}%</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">内存占用:</span>
                <span class="stat-value">{{ memoryUsage }}%</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">网络流量:</span>
                <span class="stat-value">{{ networkTraffic }} MB/s</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">运行时间:</span>
                <span class="stat-value">{{ uptime }}</span>
              </div>
            </div>
          </div>
        </aside>

        <!-- 中央视频区域 -->
        <section class="video-section">
          <div class="video-wrapper">
            <div class="video-header">
              <h2 class="camera-name">主摄像头</h2>
              <div class="video-controls">
                <button class="control-btn" @click="toggleFullscreen" title="全屏">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <polyline points="16 18 22 12 16 6"></polyline>
                    <polyline points="8 6 2 12 8 18"></polyline>
                  </svg>
                </button>
                <button class="control-btn" @click="refreshVideo" title="刷新">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <polyline points="23 4 23 10 17 10"></polyline>
                    <polyline points="1 20 1 14 7 14"></polyline>
                    <path d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"></path>
                  </svg>
                </button>
              </div>
            </div>
            <div class="v_box">
              <vPlayer :vID="videoId" :vUrl="videoUrl"></vPlayer>
            </div>
            <div class="video-info">
              <span class="stream-url">流地址: {{ videoUrl }}</span>
            </div>
          </div>
        </section>
      </main>

      <!-- 底部信息栏 -->
      <footer class="footer-info">
        <div class="footer-left">
          <span>系统版本: 1.0.0</span>
          <span>最后更新: {{ lastUpdated }}</span>
        </div>
        <div class="footer-right">
          <span>数据刷新: {{ autoRefresh ? '自动' : '手动' }}</span>
          <span>© 2024 视频监控系统</span>
        </div>
      </footer>
    </div>
    

  </div>
</template>

<script>
import vPlayer from '@/views/video/vPlayer.vue'

 export default {
   name: 'VideoMonitor',
   components: {
    vPlayer
   },
  data() {
    return {
      // 导航栏选中项
      active: '',
      // 视频相关
      videoUrl: 'http://47.120.65.85:8080/live/stream.flv',
      videoId: 1,
      selectedCamera: '1',
      videoQuality: 'medium',
      streamStatus: 'connecting', // connecting, connected, disconnected
      // 备选视频流地址列表
      alternativeUrls: [
        'http://47.120.65.85:8080/live/stream.flv',
        'http://demo.unified-streaming.com/k8s/features/stable/video/tears-of-steel/tears-of-steel.ism/.m3u8',
        'https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8'
      ],
      // 自定义视频流输入
      customUrl: '',
      
      // 系统信息
      connectionCount: 0,
      lastUpdated: '',
      
      // 视频参数
      frameRate: 0,
      bitrate: 0,
      resolution: '0x0',
      latency: 0,
      
      // 服务器状态
      cpuUsage: 0,
      memoryUsage: 0,
      networkTraffic: 0,
      uptime: '00:00:00',
      
      // 设置
      autoRefresh: true,
      autoReconnect: true,
      volume: 50,
      
      // 定时器
      statsInterval: null
    };
  },
  computed: {
    streamStatusClass() {
      switch(this.streamStatus) {
        case 'connected':
          return 'online';
        case 'disconnected':
          return 'offline';
        default:
          return 'connecting';
      }
    },
    streamStatusText() {
      switch(this.streamStatus) {
        case 'connected':
          return '已连接';
        case 'disconnected':
          return '未连接';
        default:
          return '连接中';
      }
    }
  }
  
,
  mounted() {
    this.startMonitoring();
    this.updateStats();
  },
  beforeUnmount() {
    if (this.statsInterval) {
      clearInterval(this.statsInterval);
    }
  },
  methods: {
      goBack() {
        this.$router.push('/first');
      },
      
      // 切换到选中的备选视频流地址
      switchVideoUrl(event) {
        const selectedUrl = event.target.value;
        if (selectedUrl) {
          this.videoUrl = selectedUrl;
          this.refreshVideo();
        }
      },
      
      // 应用自定义视频流地址
      applyCustomUrl() {
        if (this.customUrl.trim()) {
          this.videoUrl = this.customUrl.trim();
          // 将自定义URL添加到备选列表中（如果不存在）
          if (!this.alternativeUrls.includes(this.videoUrl)) {
            this.alternativeUrls.push(this.videoUrl);
          }
          this.refreshVideo();
        }
      },
    

    
    startMonitoring() {
      console.log('开始监控页面数据更新机制');
      // 模拟监控连接状态变化
      setTimeout(() => {
        console.log('更新连接状态为已连接');
        this.connectionCount = 1;
        this.streamStatus = 'connected';
      }, 2000);
      
      // 立即更新一次数据
      this.updateStats();
      
      // 定期更新统计数据
      this.statsInterval = setInterval(() => {
        console.log('定时器触发数据更新');
        this.updateStats();
      }, 5000);
      
      console.log('监控页面数据更新机制已启动');
    },
    
    updateStats() {
      // 生成模拟数据
      this.frameRate = Math.floor(Math.random() * 15) + 15; // 15-30 FPS
      this.bitrate = Math.floor(Math.random() * 500) + 500; // 500-1000 kbps
      this.latency = Math.floor(Math.random() * 200) + 50; // 50-250 ms
      this.cpuUsage = Math.floor(Math.random() * 30) + 5; // 5-35%
      this.memoryUsage = Math.floor(Math.random() * 40) + 20; // 20-60%
      this.networkTraffic = (Math.random() * 5 + 1).toFixed(2); // 1-6 MB/s
      
      // 模拟分辨率
      const resolutions = ['1920x1080', '1280x720', '854x480'];
      this.resolution = resolutions[Math.floor(Math.random() * resolutions.length)];
      
      // 模拟运行时间增加
      const timeParts = this.uptime.split(':');
      let hours = parseInt(timeParts[0]);
      let minutes = parseInt(timeParts[1]);
      let seconds = parseInt(timeParts[2]);
      
      seconds += 5; // 每5秒更新一次
      if (seconds >= 60) {
        seconds -= 60;
        minutes++;
      }
      if (minutes >= 60) {
        minutes -= 60;
        hours++;
      }
      
      this.uptime = `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
      this.lastUpdated = new Date().toLocaleTimeString('zh-CN');
      
      console.log('数据已更新:', {
        frameRate: this.frameRate,
        bitrate: this.bitrate,
        latency: this.latency,
        cpuUsage: this.cpuUsage,
        memoryUsage: this.memoryUsage,
        networkTraffic: this.networkTraffic,
        resolution: this.resolution,
        uptime: this.uptime
      });
    },
    
    toggleFullscreen() {
      const element = this.$el.querySelector('.video-wrapper');
      if (!document.fullscreenElement) {
        element.requestFullscreen().catch(err => {
          console.error(`全屏请求失败: ${err.message}`);
        });
      } else {
        if (document.exitFullscreen) {
          document.exitFullscreen();
        }
      }
    },
    
    refreshVideo() {
      this.videoId++;
      this.streamStatus = 'connecting';
      setTimeout(() => {
        this.streamStatus = 'connected';
      }, 1500);
    },
    
    setQuality(quality) {
      this.videoQuality = quality;
      // 这里可以根据画质调整视频URL或参数
      // 模拟画质变化
      this.refreshVideo();
    }
  },
  watch: {
    selectedCamera(newVal) {
      // 切换摄像头时刷新视频
      this.videoId = parseInt(newVal);
      this.refreshVideo();
    }
  }
}
</script>

<style lang="scss" scoped>
.video-monitor-container {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #1e5ca8 0%, #3949ab 50%, #1e5ca8 100%);
  background-size: 400% 400%;
  animation: gradientBG 15s ease infinite;
  padding-bottom: 60px; /* 预留tabbar高度，避免内容被覆盖 */
}

@keyframes gradientBG {
  0% {background-position: 0% 50%;}
  50% {background-position: 100% 50%;}
  100% {background-position: 0% 50%;}
}

.video-monitor {
  height: 100vh;
  width: 100vw;
  display: flex;
  flex-direction: column;
  color: #333;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  position: relative;
}

/* 底部导航栏样式 */
.van-tabbar {
  position: fixed !important;
  bottom: 0;
  left: 0;
  width: 100%;
  background: linear-gradient(to right, #ffffff, #e3f2fd);
  box-shadow: 0 -4px 12px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  border-top: 1px solid #ddd;
  transition: all 0.3s ease-in-out;
}

.van-tabbar-item {
  color: #607d8b;
  font-weight: 500;
  transition: color 0.3s;
}

.van-tabbar-item:hover {
  background-color: rgba(0, 0, 0, 0.03);
}

.van-tabbar-item--active {
  color: #1976d2 !important;
  background-color: rgba(25, 118, 210, 0.08);
  border-radius: 12px;
  margin: 2px 8px;
}

/* 顶部状态栏 */
.status-bar {
  height: 60px;
  background: rgba(255, 255, 255, 0.9);
  border-bottom: 1px solid rgba(30, 92, 168, 0.3);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.status-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

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

.title {
  font-size: 20px;
  font-weight: 600;
  color: #1e5ca8;
  margin: 0;
}

.system-status {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #666;
}

.status-indicator {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #4caf50;
}

.status-indicator.online {
  background: #4caf50;
  animation: pulse 2s infinite;
}

.status-right {
  display: flex;
  align-items: center;
  gap: 24px;
  font-size: 14px;
  color: #666;
}

/* 主内容区域 */
.main-content {
  flex: 1;
  display: flex;
  overflow: hidden;
  position: relative;
}

/* 数据面板 */
.data-panel, .control-panel {
  width: 280px;
  background: rgba(255, 255, 255, 0.9);
  border-right: 1px solid rgba(30, 92, 168, 0.2);
  padding: 20px;
  overflow-y: auto;
  flex-shrink: 0;
  box-shadow: 4px 0 12px rgba(0, 0, 0, 0.05);
}

.control-panel {
  border-right: none;
  border-left: 1px solid rgba(30, 92, 168, 0.2);
  box-shadow: -4px 0 12px rgba(0, 0, 0, 0.05);
}

.panel-section {
  margin-bottom: 32px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #1e5ca8;
  margin: 0 0 16px 0;
  padding-bottom: 8px;
  border-bottom: 1px solid rgba(30, 92, 168, 0.2);
}

.data-cards {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.data-card {
  background: rgba(255, 255, 255, 0.7);
  padding: 12px;
  border-radius: 8px;
  text-align: center;
  border: 1px solid rgba(30, 92, 168, 0.2);
  transition: all 0.3s ease;
}

.data-card:hover {
  background: rgba(255, 255, 255, 0.9);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.data-label {
  font-size: 12px;
  color: #666;
  margin-bottom: 4px;
}

.data-value {
  font-size: 16px;
  font-weight: 600;
  color: #1e5ca8;
}

.server-stats {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid rgba(30, 92, 168, 0.1);
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.stat-value {
  font-size: 14px;
  font-weight: 600;
  color: #1e5ca8;
}

/* 视频区域 */
.video-section {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  position: relative;
}

.video-wrapper {
  width: 100%;
  max-width: 1200px;
  max-height: calc(100vh - 160px);
  display: flex;
  flex-direction: column;
  box-shadow: 0 8px 32px rgba(30, 92, 168, 0.15);
  margin: auto;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 12px;
  overflow: hidden;
}

.video-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding: 12px 16px;
  background: linear-gradient(135deg, #1e5ca8, #3949ab);
  color: white;
}

.camera-name {
  font-size: 18px;
  font-weight: 600;
  color: #ffffff;
  margin: 0;
}

.video-controls {
  display: flex;
  gap: 8px;
}

.control-btn {
  width: 36px;
  height: 36px;
  border: none;
  background: rgba(255, 255, 255, 0.2);
  color: #ffffff;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  justify-content: center;
  align-items: center;
  transition: all 0.2s ease;
}

.control-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: scale(1.1);
}

.v_box {
  width: 100%;
  height: 0;
  padding-bottom: 56.25%; /* 16:9 比例 */
  position: relative;
  background: #000;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
  border: 3px solid rgba(30, 92, 168, 0.3);
}

.v_box >>> .player-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.video-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
  padding: 8px 12px;
  background: rgba(255, 255, 255, 0.7);
  font-size: 12px;
  color: #666;
}

.stream-status {
  display: flex;
  align-items: center;
  gap: 6px;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #ff9800;
}

.status-dot.online {
  background: #4caf50;
}

.status-dot.offline {
  background: #f44336;
}

.status-dot.connecting {
  background: #ff9800;
  animation: pulse 2s infinite;
}

/* 控制面板 */
.control-group {
  margin-bottom: 20px;
}

.control-label {
  display: block;
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.control-select {
  width: 100%;
  padding: 8px 12px;
  background: rgba(255, 255, 255, 0.7);
  border: 1px solid rgba(30, 92, 168, 0.3);
  border-radius: 6px;
  color: #333;
  font-size: 14px;
  transition: all 0.3s ease;
}

.control-select:focus {
  outline: none;
  border-color: #1e5ca8;
  box-shadow: 0 0 0 2px rgba(30, 92, 168, 0.1);
}

.quality-controls {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 8px;
}

.quality-btn {
  padding: 8px 12px;
  background: rgba(255, 255, 255, 0.7);
  border: 1px solid rgba(30, 92, 168, 0.3);
  border-radius: 6px;
  color: #666;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.quality-btn:hover {
  background: rgba(255, 255, 255, 0.9);
  color: #1e5ca8;
  border-color: #1e5ca8;
}

.quality-btn.active {
  background: linear-gradient(135deg, #1e5ca8, #3949ab);
  border-color: #1e5ca8;
  color: #ffffff;
}

.switch-control {
  position: relative;
  display: inline-block;
  width: 48px;
  height: 24px;
}

.switch-control input {
  opacity: 0;
  width: 0;
  height: 0;
}

.switch-label {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(30, 92, 168, 0.2);
  transition: .4s;
  border-radius: 24px;
}

.switch-label:before {
  position: absolute;
  content: "";
  height: 18px;
  width: 18px;
  left: 3px;
  bottom: 3px;
  background-color: white;
  transition: .4s;
  border-radius: 50%;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.2);
}

input:checked + .switch-label {
  background: linear-gradient(135deg, #1e5ca8, #3949ab);
}

input:checked + .switch-label:before {
  transform: translateX(24px);
}

.volume-slider {
  width: 100%;
  height: 4px;
  background: rgba(30, 92, 168, 0.2);
  outline: none;
  border-radius: 2px;
  -webkit-appearance: none;
}

.volume-slider::-webkit-slider-thumb {
  -webkit-appearance: none;
  appearance: none;
  width: 16px;
  height: 16px;
  background: #1e5ca8;
  cursor: pointer;
  border-radius: 50%;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);
}

.volume-slider::-moz-range-thumb {
  width: 16px;
  height: 16px;
  background: #1e5ca8;
  cursor: pointer;
  border-radius: 50%;
  border: none;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);
}

/* 底部状态栏 */
.footer-info {
  height: 40px;
  background: rgba(255, 255, 255, 0.9);
  border-top: 1px solid rgba(30, 92, 168, 0.2);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  font-size: 12px;
  color: #666;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.05);
}

.footer-left,
.footer-right {
  display: flex;
  gap: 16px;
}

/* 动画 */
@keyframes pulse {
  0% { opacity: 1; }
  50% { opacity: 0.5; }
  100% { opacity: 1; }
}

/* 自定义视频流输入控件样式 */
.custom-url-input {
  width: 100%;
  padding: 8px 12px;
  background: rgba(255, 255, 255, 0.7);
  border: 1px solid rgba(30, 92, 168, 0.3);
  border-radius: 6px;
  color: #333;
  font-size: 14px;
  margin-bottom: 8px;
  transition: border-color 0.3s;
}

.custom-url-input:focus {
  outline: none;
  border-color: #1e5ca8;
  box-shadow: 0 0 0 2px rgba(30, 92, 168, 0.1);
}

.apply-custom-url-btn {
  width: 100%;
  padding: 8px 16px;
  background: linear-gradient(135deg, #1e5ca8, #3949ab);
  color: #ffffff;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.3s;
  box-shadow: 0 2px 8px rgba(30, 92, 168, 0.3);
}

.apply-custom-url-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, #1a4d91, #2c3a99);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(30, 92, 168, 0.4);
}

.apply-custom-url-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

/* 响应式设计 */
@media (max-width: 1400px) {
  .data-panel,
  .control-panel {
    width: 240px;
  }
}

@media (max-width: 1200px) {
  .main-content {
    flex-direction: column;
  }
  
  .data-panel,
  .control-panel {
    width: 100%;
    height: auto;
    border-right: none;
    border-left: none;
    border-bottom: 1px solid rgba(30, 92, 168, 0.2);
    padding: 16px 20px;
  }
  
  .control-panel {
    border-bottom: none;
    border-top: 1px solid rgba(30, 92, 168, 0.2);
  }
  
  .video-section {
    padding: 16px;
  }
  
  .video-wrapper {
    max-height: calc(100vh - 320px);
  }
}

@media (max-width: 768px) {
  .status-bar {
    padding: 0 16px;
    height: 50px;
  }
  
  .title {
    font-size: 16px;
  }
  
  .status-left,
  .status-right {
    gap: 12px;
  }
  
  .system-status,
  .status-right {
    font-size: 12px;
  }
  
  .data-cards {
    grid-template-columns: 1fr;
  }
  
  .video-section {
    padding: 12px;
  }
  
  .camera-name {
    font-size: 16px;
  }
  
  .footer-info {
    padding: 0 16px;
    font-size: 11px;
  }
  
  .footer-left,
  .footer-right {
    gap: 8px;
  }
}
</style>
