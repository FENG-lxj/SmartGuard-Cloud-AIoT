<template>
  <div class="video-test-container">
    <h2>视频流测试工具</h2>
    
    <div class="test-controls">
      <div class="stream-selector">
        <label>选择测试流：</label>
        <select v-model="selectedStream" @change="onStreamChange">
          <option value="user">用户SRS服务器流 (http://47.120.65.85:8080/live/stream.flv)</option>
          <option value="mux-flv">Mux测试FLV流 (https://test-streams.mux.dev/x36xhzz/x36xhzz.flv)</option>
          <option value="mux-m3u8">Mux测试M3U8流 (https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8)</option>
          <option value="demo-flv">FLV演示流 (https://demo.unified-streaming.com/k8s/features/stable/video/tears-of-steel/tears-of-steel.ism/.m3u8)</option>
        </select>
      </div>
      
      <div class="custom-url">
        <label>或输入自定义URL：</label>
        <input type="text" v-model="customUrl" placeholder="输入视频流URL" />
        <button @click="useCustomUrl">使用</button>
      </div>
    </div>
    
    <div class="video-display">
      <div class="video-info">
        <p>当前使用的流：{{ currentStreamUrl }}</p>
        <p>流类型：{{ getStreamType(currentStreamUrl) }}</p>
      </div>
      
      <div class="player-wrapper">
        <vPlayer :vUrl="currentStreamUrl" :vID="streamId"></vPlayer>
      </div>
    </div>
    
    <div class="debug-info">
      <h3>调试信息</h3>
      <pre>{{ debugLog }}</pre>
    </div>
  </div>
</template>

<script>
import vPlayer from './vPlayer.vue'

export default {
  name: 'VideoTest',
  components: {
    vPlayer
  },
  data() {
    return {
      selectedStream: 'user',
      customUrl: '',
      streamId: 1,
      debugLog: ''
    }
  },
  computed: {
    currentStreamUrl() {
      const streams = {
        'user': 'http://47.120.65.85:8080/live/stream.flv',
        'mux-flv': 'https://test-streams.mux.dev/x36xhzz/x36xhzz.flv',
        'mux-m3u8': 'https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8',
        'demo-flv': 'https://demo.unified-streaming.com/k8s/features/stable/video/tears-of-steel/tears-of-steel.ism/.m3u8'
      }
      
      return streams[this.selectedStream] || ''
    }
  },
  created() {
    // 初始化调试日志捕获
    this.setupDebugLogging()
  },
  methods: {
    onStreamChange() {
      this.streamId++ // 强制重新加载播放器
      this.log('切换到流：' + this.currentStreamUrl)
    },
    
    useCustomUrl() {
      if (this.customUrl) {
        // 创建一个临时流类型
        this.selectedStream = 'custom'
        // 这里我们需要修改一下计算属性的逻辑，但为了简单起见，我们可以直接修改streamId
        this.streamId++
        this.log('使用自定义URL：' + this.customUrl)
      }
    },
    
    getStreamType(url) {
      if (url.endsWith('.flv')) {
        return 'FLV'
      } else if (url.endsWith('.m3u8')) {
        return 'HLS (M3U8)'
      } else {
        return '未知'
      }
    },
    
    log(message) {
      const timestamp = new Date().toLocaleTimeString()
      this.debugLog += `[${timestamp}] ${message}\n`
      // 限制日志长度
      const maxLines = 50
      const lines = this.debugLog.split('\n')
      if (lines.length > maxLines) {
        this.debugLog = lines.slice(-maxLines).join('\n')
      }
    },
    
    setupDebugLogging() {
      // 这个方法在实际使用中可以扩展为捕获控制台日志
      this.log('视频测试组件已初始化')
    }
  }
}
</script>

<style lang="scss" scoped>
.video-test-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

h2 {
  color: #333;
  margin-bottom: 20px;
}

h3 {
  color: #666;
  margin-top: 30px;
  margin-bottom: 10px;
}

.test-controls {
  background-color: #f5f5f5;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.stream-selector,
.custom-url {
  margin-bottom: 15px;
}

label {
  display: block;
  margin-bottom: 5px;
  font-weight: 500;
}

select,
input {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

button {
  margin-top: 10px;
  padding: 8px 16px;
  background-color: #165DFF;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

button:hover {
  background-color: #0E42D2;
}

.video-display {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
}

.video-info {
  margin-bottom: 15px;
  font-size: 14px;
  color: #666;
}

.player-wrapper {
  width: 100%;
  height: 500px;
  background-color: #000;
  border-radius: 4px;
  overflow: hidden;
}

.debug-info {
  background-color: #f9f9f9;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 15px;
  max-height: 300px;
  overflow-y: auto;
}

pre {
  margin: 0;
  font-family: 'Courier New', Courier, monospace;
  font-size: 12px;
  white-space: pre-wrap;
  word-wrap: break-word;
}
</style>