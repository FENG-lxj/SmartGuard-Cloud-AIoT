<template>
  <div class="player-container">
    <video ref="videoRef" class="video" preload="auto" muted autoplay playsinline></video>
    <div v-if="loading" class="loading-overlay">
      <div class="loading-spinner"></div>
      <div class="loading-text">视频加载中...</div>
    </div>
    <div v-if="error" class="error-overlay">
      <div class="error-icon">⚠️</div>
      <div class="error-text">{{ errorMessage }}</div>
      <button class="retry-button" @click="retry">重试</button>
    </div>
    <div v-if="noVideo" class="no-video-overlay">
      <div class="no-video-icon">📹</div>
      <div class="no-video-text">暂无视频信号</div>
      <div class="no-video-subtext">请检查摄像头和服务器连接状态</div>
      <button class="retry-button" @click="retry">重新连接</button>
    </div>
  </div>
</template>

<script>
// 尝试引入flv.js和hls.js库，如果不存在则使用备选方案
let flv = null
let Hls = null

// 动态加载库，避免构建错误
try {
  flv = require('flv.js')
} catch (e) {
  console.warn('flv.js库未找到，将尝试使用备选方案')
}

try {
  Hls = require('hls.js')
} catch (e) {
  console.warn('hls.js库未找到，将尝试使用备选方案')
}

export default {
  props: {
    vUrl: {
      type: String,
      required: true
    },
    vID: {
      type: Number,
      default: 1
    }
  },
  data () {
    return {
      player: null,
      playerType: null, // 'flv' or 'hls'
      loading: true,
      error: false,
      noVideo: false,
      errorMessage: '',
      videoCheckTimer: null,
      lastStatsCheck: 0,
      dataReceived: false,
      reconnectAttempts: 0,
      maxReconnectAttempts: 3
    }
  },
  mounted () {
    this.initPlayer()

    // 使用 bind 保证 this 正确
    this._onFocus = this.handleWindowFocus.bind(this);
    window.addEventListener('focus', this._onFocus)
  },
  beforeUnmount () {
    window.removeEventListener('focus', this._onFocus)
    this.destroyPlayer()
    if (this.videoCheckTimer) {
      clearInterval(this.videoCheckTimer)
    }
  },
  watch: {
      vUrl: {
        handler (newUrl, oldUrl) {
          // 只有当URL真正变化时才重新初始化播放器
          if (newUrl && newUrl !== oldUrl) {
            console.log('检测到URL变化，重新初始化播放器:', oldUrl, '->', newUrl)
            this.destroyPlayer()
            this.initPlayer()
          }
        },
        immediate: true
      },
      vID: {
        handler (newID, oldID) {
          if (newID !== oldID) {
            console.log('检测到ID变化，重新初始化播放器:', oldID, '->', newID)
            this.destroyPlayer()
            this.initPlayer()
          }
        }
      }
  },
  methods: {
    initPlayer () {
      this.loading = true
        this.error = false
        this.noVideo = false
        this.dataReceived = false
        this.reconnectAttempts = 0
        
        try {
          const videoElement = this.$refs.videoRef
          const videoUrl = this.vUrl || 'http://47.120.65.85:8080/live/stream.flv'
          
          console.log('初始化播放器，URL:', videoUrl)
          
          // 清除之前的定时器
          if (this.videoCheckTimer) {
            clearInterval(this.videoCheckTimer)
          }
          
          // 启动视频流检测定时器（10秒后检查是否有数据）
          this.videoCheckTimer = setInterval(() => {
            if (!this.loading && !this.error && !this.noVideo && !this.dataReceived) {
              console.warn('视频流连接已建立但未收到数据，标记为无视频信号')
              this.noVideo = true
            }
          }, 10000)
          
          // 先检查URL是否可访问
          this.checkUrlAccessibility(videoUrl).then(accessible => {
            if (!accessible) {
              console.error('视频流URL不可访问:', videoUrl)
              this.handleError('无法连接到视频流服务器，请检查网络连接')
              return
            }
            
            // 根据URL后缀判断视频格式
            if (videoUrl.endsWith('.m3u8')) {
              console.log('检测到M3U8格式，使用HLS播放器')
              this.initHlsPlayer(videoElement, videoUrl)
            } else if (videoUrl.endsWith('.flv')) {
              console.log('检测到FLV格式，使用FLV播放器')
              this.initFlvPlayer(videoElement, videoUrl)
            } else {
              console.log('未知格式，尝试使用原生播放器')
              // 默认尝试作为普通视频播放
              this.initNativePlayer(videoElement, videoUrl)
            }
          }).catch(err => {
            console.error('检查URL可访问性时出错:', err)
            this.handleError('检查视频流连接时出错: ' + err.message)
          })
        } catch (e) {
          console.error('初始化播放器失败:', e)
          this.handleError('播放器初始化失败: ' + e.message)
        }
      },
      
      async checkUrlAccessibility(url) {
        try {
          const controller = new AbortController()
          const timeoutId = setTimeout(() => controller.abort(), 5000) // 5秒超时
          
          const response = await fetch(url, {
            method: 'HEAD',
            mode: 'cors',
            signal: controller.signal,
            headers: {
              'Access-Control-Allow-Origin': '*'
            }
          })
          
          clearTimeout(timeoutId)
          console.log('URL访问检查结果:', response.status, response.statusText)
          return response.ok
        } catch (error) {
          console.error('URL访问检查失败:', error)
          return false
        }
      },
      
      initFlvPlayer (videoElement, videoUrl) {
        console.log('开始初始化FLV播放器')
        if (flv && flv.isSupported()) {
          console.log('浏览器支持FLV播放')
          this.playerType = 'flv'
          
          try {
            console.log('创建FLV播放器实例')
            const player = flv.createPlayer(
              {
                type: 'flv',
                url: videoUrl,
                isLive: true,
                hasAudio: true,
                hasVideo: true,
                cors: true,
                withCredentials: false
              },
              {
                enableStashBuffer: false,
                stashInitialSize: 128,
                autoCleanupSourceBuffer: true,
                lazyLoadMaxDuration: 3 * 60,
                lazyLoadRecoverDuration: 30,
                statisticsInfoReportInterval: 1000
              }
            )
    
            console.log('附加到视频元素')
            player.attachMediaElement(videoElement)
            
            console.log('开始加载视频流')
            player.load()
            
            // 绑定事件监听器
            player.on(flv.Events.ERROR, (errType, errDetail) => {
              console.error('FLV播放错误:', errType, errDetail)
              let errorMessage = '视频播放失败'
              if (errType === flv.ErrorTypes.NETWORK_ERROR) {
                errorMessage += ' (网络连接问题，请检查服务器和网络设置)'
                // 尝试自动重连
                this.handleReconnect(player)
              } else if (errType === flv.ErrorTypes.MEDIA_ERROR) {
                errorMessage += ' (媒体解码问题，请检查视频流格式)'
              }
              this.handleError(errorMessage)
            })
            
            player.on(flv.Events.MEDIA_INFO, () => {
              console.log('FLV媒体信息到达')
              this.loading = false
            })
            
            player.on(flv.Events.STATISTICS_INFO, (stats) => {
              console.log('FLV统计信息:', stats)
              // 检测是否有实际数据传输
              if (stats.droppedFrames > 0 || stats.currentSpeed > 0) {
                console.log('视频流有数据传输，droppedFrames:', stats.droppedFrames, 'speed:', stats.currentSpeed)
                this.dataReceived = true
              }
            })
            
            player.on(flv.Events.BUFFER_EMPTY, () => {
              console.warn('FLV缓冲区为空')
              this.loading = true
            })
            
            player.on(flv.Events.BUFFER_FULL, () => {
              console.log('FLV缓冲区已满')
              this.loading = false
            })
            
            setTimeout(() => {
              console.log('尝试播放视频')
              player.play().catch(error => {
                console.error('FLV播放启动失败:', error)
                this.handleError('无法播放视频流: ' + error.message)
                // 自动尝试恢复
                this.handlePlaybackError(player)
              })
            }, 1000)
            
            this.player = player
            
            // 处理进度
            videoElement.addEventListener('progress', () => {
              if (!this.player || !this.player.buffered || this.player.buffered.length === 0) return
    
              const endTime = this.player.buffered.end(0)
              const difference = endTime - this.player.currentTime
    
              console.log('进度更新: 缓冲结束时间', endTime, '当前时间', this.player.currentTime, '差值', difference)
              
              if (difference > 10 || difference < 0) {
                console.log('调整播放位置到缓冲末尾')
                this.player.currentTime = endTime - 1
              } else {
                videoElement.playbackRate = difference > 1 ? 1.1 : 1
              }
            })
          } catch (e) {
            console.error('FLV播放器创建失败:', e)
            this.handleError('创建FLV播放器失败: ' + e.message)
            // 如果FLV失败，尝试使用原生播放器
            this.fallbackToNativePlayer(videoElement, videoUrl)
          }
        } else {
          console.error('浏览器不支持FLV播放')
          this.handleError('您的浏览器不支持FLV格式视频')
          // 如果FLV不可用，尝试使用原生播放器
          this.fallbackToNativePlayer(videoElement, videoUrl)
        }
      },
      
      handlePlaybackError(player) {
        console.log('尝试恢复播放...')
        if (player && !this.error) {
          setTimeout(() => {
            if (player) {
              player.play().catch(err => {
                console.error('播放恢复失败:', err)
              })
            }
          }, 2000)
        }
      },
      
      handleReconnect(player) {
        if (this.reconnectAttempts < this.maxReconnectAttempts) {
          this.reconnectAttempts++
          console.log(`尝试第 ${this.reconnectAttempts} 次重连...`)
          
          setTimeout(() => {
            if (player) {
              try {
                player.unload()
                player.load()
                player.play()
              } catch (e) {
                console.error('重连失败:', e)
              }
            }
          }, 3000 * this.reconnectAttempts) // 递增的重连间隔
        } else {
          console.error('已达到最大重连次数')
        }
      },
      
      fallbackToNativePlayer(videoElement, videoUrl) {
        console.log('尝试使用原生播放器作为备选方案')
        // 清除之前可能的设置
        if (this.player) {
          this.destroyPlayer()
        }
        
        this.loading = true
        this.initNativePlayer(videoElement, videoUrl)
      },
      
      initHlsPlayer (videoElement, videoUrl) {
        if (Hls && Hls.isSupported()) {
          this.playerType = 'hls'
          const player = new Hls({
            enableWorker: true,
            liveBackBufferLength: 90,
            maxBufferSize: 0,
            maxBufferLength: 3,
            startLevel: 0,
            autoStartLoad: true,
            abrEwmaFastLive: 3.0,
            abrEwmaSlowLive: 9.0
          })
    
          player.loadSource(videoUrl)
          player.attachMedia(videoElement)
          
          player.on(Hls.Events.MANIFEST_PARSED, () => {
            this.loading = false
            videoElement.play().catch(e => {
              console.error('播放请求失败', e)
              this.handleError('播放请求失败: ' + e.message)
            })
          })
          
          player.on(Hls.Events.ERROR, (event, data) => {
            console.error('HLS播放错误:', event, data)
            if (data.fatal) {
              switch (data.type) {
                case Hls.ErrorTypes.NETWORK_ERROR:
                  this.handleError('网络错误，请检查您的网络连接')
                  // 尝试自动重连
                  this.handleReconnect(player)
                  break
                case Hls.ErrorTypes.MEDIA_ERROR:
                  this.handleError('媒体解码错误')
                  break
                default:
                  this.handleError('视频播放失败')
                  break
              }
            }
          })
          
          this.player = player
        } else {
          // 如果浏览器不支持HLS，但原生支持M3U8
          if (videoElement.canPlayType('application/vnd.apple.mpegurl')) {
            this.initNativePlayer(videoElement, videoUrl)
          } else {
            this.handleError('您的浏览器不支持M3U8格式视频')
          }
        }
      },
      
      initNativePlayer (videoElement, videoUrl) {
        this.playerType = 'native'
        videoElement.src = videoUrl
        
        // 添加额外的错误处理
        videoElement.onerror = (e) => {
          console.error('原生播放失败', e)
          this.handleError('视频播放失败')
        }
        
        // 添加loadeddata事件监听
        videoElement.onloadeddata = () => {
          console.log('视频数据已加载')
          this.loading = false
          this.dataReceived = true
        }
        
        // 添加stalled事件监听（数据加载中断）
        videoElement.onstalled = () => {
          console.warn('视频加载中断')
          this.loading = true
        }
        
        // 添加waiting事件监听（缓冲）
        videoElement.onwaiting = () => {
          console.log('视频正在缓冲')
          this.loading = true
        }
        
        // 添加playing事件监听（开始播放）
        videoElement.onplaying = () => {
          console.log('视频开始播放')
          this.loading = false
        }
        
        // 添加ended事件监听（播放结束）
        videoElement.onended = () => {
          console.log('视频播放结束')
          this.noVideo = true
        }
        
        // 尝试播放视频
        videoElement.play().catch(e => {
          console.error('播放请求失败', e)
          this.handleError('播放请求失败: ' + e.message)
        })
      },
      
      handleWindowFocus () {
        if (!this.player || this.error) return
        
        const videoElement = this.$refs.videoRef
        if (this.playerType === 'flv' && this.player.buffered && this.player.buffered.length > 0) {
          this.player.currentTime = this.player.buffered.end(0) - 1
        } else if (this.playerType === 'hls') {
          // HLS流通常是实时的，不需要特别处理
        } else if (this.playerType === 'native' && videoElement.buffered && videoElement.buffered.length > 0) {
          videoElement.currentTime = videoElement.buffered.end(0) - 1
        }
      },
      
      destroyPlayer () {
        if (!this.player) return
        
        const videoElement = this.$refs.videoRef
        
        if (this.playerType === 'flv') {
          this.player.pause()
          this.player.unload()
          this.player.detachMediaElement()
          this.player.destroy()
        } else if (this.playerType === 'hls') {
          this.player.destroy()
        } else if (this.playerType === 'native' && videoElement) {
          videoElement.pause()
          videoElement.src = ''
          videoElement.load()
        }
        
        this.player = null
        this.playerType = null
      },
      
      handleError (message) {
        this.loading = false
        this.error = true
        this.errorMessage = message
      },
      
      retry () {
        this.destroyPlayer()
        this.initPlayer()
      }
    }
  }
</script>

<style lang="scss" scoped>
.player-container {
  position: relative;
  width: 100%;
  height: 100%;
}

.video {
  width: 100%;
  height: 100%;
  background-color: #000;
  display: block;
  object-fit: contain;
}

.loading-overlay,
.error-overlay,
.no-video-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.7);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: #fff;
  z-index: 10;
}

.loading-spinner {
  width: 50px;
  height: 50px;
  border: 3px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  border-top-color: #fff;
  animation: spin 1s ease-in-out infinite;
  margin-bottom: 20px;
}

.loading-text,
.error-text,
.no-video-text {
  font-size: 16px;
  margin-bottom: 15px;
  font-weight: 500;
}

.no-video-subtext {
  font-size: 14px;
  margin-bottom: 20px;
  color: rgba(255, 255, 255, 0.8);
}

.error-icon,
.no-video-icon {
  font-size: 48px;
  margin-bottom: 15px;
}

.retry-button {
  padding: 10px 20px;
  background-color: #165DFF;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}

.retry-button:hover {
  background-color: #0E42D2;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>
