<template>
    <div class="AI-index">
      <div :class="['left-side', history ? 'history' : 'nohistory', { 'open': mobileMenuOpen }]" @click="toggleHistory">

        <!-- 按钮区域 -->
        <div class="history-buttons">
          <!-- 返回按钮 -->
          <button class="back-btn" title="返回" @click.stop="goBack">
            <i class="iconfont icon-fanhui"></i>
            <span>返回</span>
          </button>
          <!-- 新建对话按钮 -->
          <button class="new-chat-button" title="新建对话" @click.stop="newTalk">
            <i class="iconfont icon-xinjian"></i>
            <span>新建对话</span>
          </button>
        </div>

        <!-- 历史记录内容，循环从hisMessage中获取数据 -->
        <div class="history-content" v-if="history">
          <div 
            class="history-item" 
            v-for="(item, index) in store.hisMessage" 
            :key="index" 
            :class="{ selected: selectedHistoryIndex === index }"
            @click.stop="Hissubmit(item, index)"
          >
            <div class="history-item-content">
                <div class="history-item-title">
                  {{ getTitleFromContent(item) }}
                </div>
                <div class="history-item-preview">
                  {{ getPreviewFromContent(item) }}
                </div>
              </div>
              <!-- 历史记录操作按钮 -->
              <div class="history-item-actions">
                <button 
                  class="history-item-action-btn" 
                  title="编辑对话"
                  @click.stop="editHistoryItem(index)"
                >
                  <i class="iconfont icon-bianji"></i>
                </button>
                <button 
                  class="history-item-action-btn" 
                  title="删除对话"
                  @click.stop="deleteHistoryItem(index)"
                >
                  <i class="iconfont icon-shanchu"></i>
                </button>
              </div>
          </div>
        </div>
      </div>

      <!-- AI对话框 -->
      <div class="right-side now">

        <!-- 移除chat-header div，避免白色横框 -->
        <div class="scroll-container" :class="{ 'voice-mode-bubbles': chatMode === 'voice' }" ref="scrollContainer">
          <!-- 对话内容居中容器 -->
          <div class="chat-content-wrapper">
                <!-- 对话对容器 - 将一问一答作为整体 -->
                <div 
                  class="conversation-pair"
                  v-for="(pair, pairIndex) in conversationPairs"
                  :key="pairIndex"
                >
                  <!-- 对话时间戳 -->
                  <div class="conversation-timestamp" v-if="pair.timestamp">
                    {{ formatTimestamp(pair.timestamp) }}
                  </div>
                  
                  <!-- 用户消息 -->
                  <div class="chat-message chat-message-me" v-if="pair.userMessage">
                    <div class="message-content">
                      <div class="message-bubble message-bubble-me bg-white rounded-2xl shadow-sm px-4 py-3 text-slate-700 max-w-[68%]">
                        {{ pair.userMessage.content }}
                      </div>
                    </div>
                    <div class="chat-avatar chat-avatar-me bg-sky-500 text-white rounded-full w-9 h-9 flex items-center justify-center shadow-sm">
                      我
                    </div>
                  </div>
                  
                  <!-- AI消息 -->
                  <div class="chat-message chat-message-ai" v-if="pair.aiMessage">
                    <div class="chat-avatar chat-avatar-ai bg-slate-900 text-white rounded-full w-9 h-9 flex items-center justify-center shadow-sm">
                      AI
                    </div>
                    <div class="message-content">
                      <div class="message-bubble message-bubble-ai bg-sky-50 rounded-2xl shadow-sm px-4 py-3 text-slate-800 max-w-[68%]">
                        {{ pair.aiMessage.content }}
                      </div>
                      
                    </div>
                  </div>
                </div>
          </div>
        </div>

        <!-- 识别结果区域 -->
        <div v-if="transcript" class="transcript-area">
          <div class="transcript-content">
            <strong>识别结果:</strong> {{ transcript }}
            <button class="use-transcript-btn" @click="useTranscript">使用此文本</button>
          </div>
        </div>
        
        <!-- 音频播放器 -->
        <div v-if="!isRecording && transcript" class="audio-player-area">
          <audio ref="player" controls></audio>
        </div>
        <!-- 隐藏的TTS播放元素，用于语音房间排队播放 -->
        <audio ref="ttsPlayer" style="display:none"></audio>
        
        <!-- 输入框区域 - 固定在底部（仅文字聊天） -->
        <div v-if="chatMode === 'text'" class="input-area">
          <div class="input-container">
            <textarea 
              ref="textarea" 
              v-model="inputMessage" 
              placeholder="请输入问题"
              rows="1"
              @input="adjustTextareaHeight"
              @keyup.enter.ctrl="Submit"
            ></textarea>
            <div class="input-actions">
              <button class="voice-button mic-btn rounded-full w-12 h-12 flex items-center justify-center text-white transition shadow-md relative" @click="startVoiceRoom" :disabled="isConnectingVoiceRoom" :aria-pressed="isConnectingVoiceRoom" :class="{ recording: isRecording, connecting: isConnectingVoiceRoom }" aria-label="语音房间" title="语音房间" data-tip="语音房间">
                 <svg class="mic-icon" viewBox="0 0 24 24" fill="currentColor" aria-hidden="true">
                   <path d="M12 14c1.657 0 3-1.343 3-3V6c0-1.657-1.343-3-3-3S9 4.343 9 6v5c0 1.657 1.343 3 3 3z"></path>
                   <path d="M19 11a7 7 0 01-14 0h2a5 5 0 0010 0h2z"></path>
                   <path d="M11 18h2v3h-2z"></path>
                 </svg>
               </button>
              <button class="send-button" @click="Submit" :disabled="!inputMessage.trim()">
                发送
              </button>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 语音模式：小球对话（不显示文本） -->
      <div v-if="chatMode === 'voice'" class="voice-orb-wrapper">
  <div class="voice-orb" :class="{ 'is-speaking': speaking }" :style="{ '--level': recordLevelEMA }" @click="stopVoiceRoom" title="点击停止">
    <div class="orb-core"></div>
    <div class="orb-wave">
      <span class="ring r1"></span>
      <span class="ring r2"></span>
      <span class="ring r3"></span>
    </div>
    <div class="orb-eq">
      <span class="bar b1"></span>
      <span class="bar b2"></span>
      <span class="bar b3"></span>
      <span class="bar b4"></span>
      <span class="bar b5"></span>
    </div>
    <div v-if="isConnectingVoiceRoom" class="orb-connecting"></div>
  </div>
</div>
<!-- 语音模式渐变叠层：从圆球向上扩展到页面中部逐渐透明 -->
<div v-if="chatMode === 'voice'" class="voice-gradient-overlay" :class="{ 'is-speaking': speaking }"></div>
    </div>
  </template>

<script>
import request from '@/utils/request.js'
import { useMainStore } from '@/store/index.js'

const DEFAULT_HISTORY = [
  '如何优化我的智能家居系统的能源使用效率？',
  '解释一下什么是机器学习算法',
  '创建一个简单的智能家居控制流程',
  '帮我分析上周的能源消耗数据',
  '推荐几款适合家庭使用的智能设备'
]

const DEFAULT_MESSAGES = [
  { sender: 'me', content: '欢迎使用AI助手，请问有什么可以帮助您的？', timestamp: Date.now() },
  { sender: 'other', content: '您好！我是您的AI助手。我可以帮助您解答问题、提供建议或协助您完成各种任务。请随时提出您的问题或需求，我会尽力为您提供帮助。', timestamp: Date.now() }
]

export default {
  name: 'AIIndex',
  data () {
    return {
      messages: [],
      inputMessage: '',
      history: false,
      currentMessage: '',
      isRecording: false,
      selectedHistoryIndex: -1,
      mobileMenuOpen: false,
      currentChatTitle: '',
      ws: null,
      transcript: '',
      mediaRecorder: null,
      isVoiceRoomActive: false,
      roomId: null,
      audioContext: null,
      sourceNode: null,
      processorNode: null,
      speaking: false,
      lastVoiceTs: 0,
      silenceMsThreshold: 800,
      amplitudeThreshold: 0.015,
      ttsQueue: [],
      isPlayingTTS: false,
      recordLevel: 0,
      recordLevelEMA: 0,
      uiPreviewMode: false,
      isConnectingVoiceRoom: false,
      isStoppingVoiceRoom: false,
      chatMode: 'text'
    }
  },
  async created () {
    this.store = useMainStore()
    this.getHistory()
    this.messages = this.store.message || []

    if (!this.store.hisMessage || this.store.hisMessage.length === 0) {
      this.store.hisMessage = [...DEFAULT_HISTORY]
    }

    if (!this.messages || this.messages.length === 0) {
      this.messages = [...DEFAULT_MESSAGES]
      this.currentChatTitle = '新对话'
    }
  },

  watch: {
    messages: {
      handler () {
        this.scrollToBottom()
      },
      deep: true
    }
  },

  computed: {
    // 将消息分组为对话对
    conversationPairs() {
      const pairs = []
      let currentPair = null
      
      // 防御性编程，如果messages为空，提供默认的模拟数据
      const displayMessages = this.messages && this.messages.length > 0 ? this.messages : [
        {
          sender: 'me',
          content: '欢迎使用AI助手，请问有什么可以帮助您的？',
          timestamp: Date.now()
        },
        {
          sender: 'other',
          content: '您好！我是您的AI助手。我可以帮助您解答问题、提供建议或协助您完成各种任务。请随时提出您的问题或需求，我会尽力为您提供帮助。',
          timestamp: Date.now()
        }
      ]
      
      displayMessages.forEach(message => {
        if (message.sender === 'me') {
          // 如果是用户消息，创建新的对话对
          if (currentPair && currentPair.aiMessage) {
            pairs.push(currentPair)
          }
          currentPair = {
            userMessage: message,
            aiMessage: null,
            timestamp: message.timestamp || Date.now()
          }
        } else if (message.sender === 'other' && currentPair) {
          // 如果是AI消息，添加到当前对话对
          currentPair.aiMessage = message
        }
      })
      
      // 添加最后一个对话对（如果有的话）
      if (currentPair) {
        pairs.push(currentPair)
      }
      
      return pairs
    }
  },

  methods: {

    // 格式化时间戳显示
    formatTimestamp(timestamp) {
      if (!timestamp) return '';
      const date = new Date(timestamp);
      const hours = date.getHours().toString().padStart(2, '0');
      const minutes = date.getMinutes().toString().padStart(2, '0');
      return `${hours}:${minutes}`;
    },

    // 获取历史记录
    async getHistory () {
      try {
        // 使用项目中配置的request实例
        const res = await request.get('/ai/getHistory')
        console.log('后端获取到的历史记录', res.data)
        // 修复：添加空值检查，避免undefined读取length属性的错误
        this.store.sethistory(res?.data || [])
      } catch (error) {
        console.error('获取历史记录失败:', error)
        // 可以添加错误处理逻辑
      }
    },

    // 新建对话，历史记录为空，id为空
    newTalk () {
      this.store.setmessage([])
      this.store.setAIid('')
      this.currentChatTitle = '新对话'
    },

    // 返回首页（强制跳转）
    goBack() {
      this.$router.push('/first')
    },

    // 切换移动端菜单
    toggleMobileMenu() {
      this.mobileMenuOpen = !this.mobileMenuOpen
    },

    // 让输入框可以自动增加高度
    adjustTextareaHeight () {
      const textarea = this.$refs.textarea
      if (textarea.scrollHeight > 70) {
        textarea.style.height = 'auto' // 先设置为auto以获取内容高度
        textarea.style.height = textarea.scrollHeight + 'px' // 设置为内容高度
      } else {
        textarea.style.height = '70px'
      }
    },
    // 点击按钮，发送消息
    async Submit () {
      const value = this.$refs.textarea?.value
      if (!value) return

      const newMessage = {
        sender: 'me',
        content: value,
        timestamp: Date.now()
      }
      this.store.Setmessage(newMessage)

      this.inputMessage = ''
      this.$refs.textarea.style.height = 'auto'
      this.$refs.textarea.style.minHeight = '44px'

      const newAIMessage = {
        sender: 'other',
        content: '',
        timestamp: Date.now()
      }
      this.messages.push(newAIMessage)

      const payload = { question: value }
      if (this.store.AIid) {
        payload.id = this.store.AIid
      }

      try {
        const response = await request.post('/ai/question', payload)
        if (response?.data) {
          newAIMessage.content = response.data.content || 'AI响应内容'
          if (response.data.id) {
            this.store.setAIid(response.data.id)
          }
        }
      } catch (error) {
        console.error('发送问题失败:', error)
      }

      if (!newAIMessage.content) {
        setTimeout(() => {
          newAIMessage.content = '这是AI的响应内容。由于后端服务未连接，这是一个模拟响应。'
        }, 500)
      }

      this.getHistory()
    },
    // 点击之后，将历史记录中的内容传入AI
    Hissubmit (item, index) {
      // 设置选中状态
      this.selectedHistoryIndex = index
      
      const newMessage = {
        sender: 'me',
        content: item
      }
      this.store.Setmessage(newMessage)
      this.messages = [...this.store.message]
    },
    
    // 从历史内容中提取标题（取前30个字符）
    getTitleFromContent(content) {
      if (!content || typeof content !== 'string') {
        return '无标题对话'
      }
      return content.length > 30 ? content.substring(0, 30) + '...' : content
    },
    
    // 从历史内容中提取预览（取30-60个字符）
    getPreviewFromContent(content) {
      if (!content || typeof content !== 'string') {
        return ''
      }
      if (content.length <= 30) {
        return ''
      }
      const previewStart = 30
      const previewEnd = Math.min(60, content.length)
      return content.substring(previewStart, previewEnd) + '...'
    },
    
    // 编辑历史记录项
    editHistoryItem(index) {
      const newContent = prompt('编辑对话内容:', this.store.hisMessage[index])
      if (newContent && newContent.trim() !== '') {
        this.store.hisMessage[index] = newContent.trim()
        // 这里可以添加保存到后端的逻辑
      }
    },
    
    // 删除历史记录项
    deleteHistoryItem(index) {
      if (confirm('确定要删除这条对话历史吗？')) {
        this.store.hisMessage.splice(index, 1)
        // 这里可以添加保存到后端的逻辑
        if (this.selectedHistoryIndex === index) {
          this.selectedHistoryIndex = -1
        }
      }
    },

    // 侧边栏的选择器切换
    toggleHistory () {
      this.history = !this.history
    },

    // 自动滚动到底部
    scrollToBottom () {
      this.$nextTick(() => {
        const scrollContainer = this.$refs.scrollContainer
        if (scrollContainer) {
          scrollContainer.scrollTop = scrollContainer.scrollHeight
        }
      })
    },

    // 语音房间：开关
    toggleVoiceRoom () {
      if (this.isVoiceRoomActive || this.isConnectingVoiceRoom) {
        this.stopVoiceRoom();
        this.chatMode = 'text'
      } else {
        this.startVoiceRoom();
        this.chatMode = 'voice'
      }
    },
    // 预览模式：仅切换本地录音动画与演示效果
    toggleMicPreview () {
      this.isRecording = !this.isRecording
      // 录音时开启预览动画与提示
      this.uiPreviewMode = this.isRecording || this.isVoiceRoomActive
    },
    // 直接切换预览模式显示/隐藏录音条
    togglePreviewMode () {
      this.uiPreviewMode = !this.uiPreviewMode
    },
    
    // WS地址构造（自适应 http/https）
    getWsUrl () {
      const isHttps = window.location.protocol === 'https:';
      const host = window.location.hostname;
      const port = 8079;
      const scheme = isHttps ? 'wss' : 'ws';
      return `${scheme}://${host}:${port}/ws/audio`;
    },
    
    // 语音按钮（旧模式）：开始/停止录音
    async Luyin () {
      if (!this.isRecording) {
        // 开始录音前清空之前的识别结果
        this.transcript = ""
        this.startRecording()
      } else {
        // 停止录音
        this.stopRecording()
      }
    },
    
    // 开启语音房间（持续连接、自动分句一问一答）
    async startVoiceRoom () {
      if (this.isVoiceRoomActive || this.isConnectingVoiceRoom) return;
      try {
        this.isConnectingVoiceRoom = true;
        this.chatMode = 'voice';
        this.roomId = `room-${Date.now()}`;
        this.ws = new WebSocket(this.getWsUrl());
        this.ws.binaryType = "arraybuffer";
        this.ws.onopen = () => {
          this.ws.send(JSON.stringify({ event: "start", roomId: this.roomId, format: "pcm16", sampleRate: 16000 }));
          this.isConnectingVoiceRoom = false;
        };
        this.ws.onmessage = (e) => {
          const msg = JSON.parse(e.data);
          if (msg.type === "partial") {
            this.transcript = msg.text;
          } else if (msg.type === "transcript") {
            if (msg.text && msg.text.trim()) {
              this.messages.push({ sender: 'me', content: msg.text.trim(), timestamp: Date.now() });
            }
          } else if (msg.type === "reply" || msg.type === "tts") {
            if (msg.text && msg.text.trim()) {
              this.messages.push({ sender: 'other', content: msg.text.trim(), timestamp: Date.now() });
            }
            if (msg.data) {
              const audioBlob = new Blob([new Uint8Array(msg.data)], { type: "audio/wav" });
              this.enqueueTTS(audioBlob, msg.text);
            }
          }
        };
        this.ws.onclose = () => {
          this.isVoiceRoomActive = false;
          this.isConnectingVoiceRoom = false;
          this.teardownAudioGraph();
        };
        this.ws.onerror = (err) => {
          console.error('语音房间WS错误:', err);
          this.isVoiceRoomActive = false;
          this.isConnectingVoiceRoom = false;
        };
        await this.setupAudioGraph();
        this.isVoiceRoomActive = true;
      } catch (err) {
        console.error('开启语音房间失败:', err);
        alert('开启语音房间失败，请检查麦克风权限或服务端连接。');
        this.isVoiceRoomActive = false;
        this.isConnectingVoiceRoom = false;
      }
    },
    // 关闭语音房间
    stopVoiceRoom () {
      this.chatMode = 'text';
      this.isVoiceRoomActive = false;
      this.isConnectingVoiceRoom = false;
      this.uiPreviewMode = false;
      this.isStoppingVoiceRoom = true;
      this.stopAllTTS();
      if (this.ws && this.ws.readyState === 1) {
        this.ws.send(JSON.stringify({ event: 'end', roomId: this.roomId }));
      }
      if (this.ws) {
        try { this.ws.close(); } catch (_) {}
        this.ws = null;
      }
      this.teardownAudioGraph();
      this.isStoppingVoiceRoom = false;
    },
    // 构建音频处理图：采集PCM并下采样为16k
    async setupAudioGraph () {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      this.audioContext = new (window.AudioContext || window.webkitAudioContext)();
      const inputSampleRate = this.audioContext.sampleRate; // 典型为48000
      this.sourceNode = this.audioContext.createMediaStreamSource(stream);
      this.processorNode = this.audioContext.createScriptProcessor(4096, 1, 1);
      this.processorNode.onaudioprocess = (event) => {
        const inputBuffer = event.inputBuffer.getChannelData(0);
        let sumSq = 0;
        for (let i = 0; i < inputBuffer.length; i++) sumSq += inputBuffer[i] * inputBuffer[i];
        const rms = Math.sqrt(sumSq / inputBuffer.length);
        // 更新录音条的音量（0-100）
         const level = Math.min(100, Math.round(rms * 200));
         // 指数平滑，减少抖动
         this.recordLevelEMA = Math.min(100, Math.round(this.recordLevelEMA * 0.85 + level * 0.15));
         this.recordLevel = level;
        const now = Date.now();
        if (rms > this.amplitudeThreshold) {
          this.speaking = true;
          this.lastVoiceTs = now;
        } else if (this.speaking && now - this.lastVoiceTs > this.silenceMsThreshold) {
          this.speaking = false;
          if (this.ws && this.ws.readyState === 1) {
            this.ws.send(JSON.stringify({ event: 'end', roomId: this.roomId }));
          }
        }
        const downsampled = this.downsamplePCM(inputBuffer, inputSampleRate, 16000);
        if (downsampled && downsampled.length && this.ws && this.ws.readyState === 1) {
          const pcm16 = this.floatTo16BitPCM(downsampled);
          this.ws.send(pcm16.buffer);
        }
      };
      this.sourceNode.connect(this.processorNode);
      this.processorNode.connect(this.audioContext.destination);
    },
    teardownAudioGraph () {
      try { if (this.processorNode) this.processorNode.disconnect(); } catch (_) {}
      try { if (this.sourceNode) this.sourceNode.disconnect(); } catch (_) {}
      try { if (this.audioContext) this.audioContext.close(); } catch (_) {}
      this.processorNode = null;
      this.sourceNode = null;
      this.audioContext = null;
    },
    // 工具：下采样
    downsamplePCM (buffer, sampleRate, targetRate) {
      if (targetRate === sampleRate) return buffer;
      const ratio = sampleRate / targetRate;
      const newLength = Math.round(buffer.length / ratio);
      const result = new Float32Array(newLength);
      let offsetResult = 0;
      let offsetBuffer = 0;
      while (offsetResult < result.length) {
        const nextOffsetBuffer = Math.round((offsetResult + 1) * ratio);
        let accum = 0, count = 0;
        for (let i = offsetBuffer; i < nextOffsetBuffer && i < buffer.length; i++) {
          accum += buffer[i];
          count++;
        }
        result[offsetResult] = accum / (count || 1);
        offsetResult++;
        offsetBuffer = nextOffsetBuffer;
      }
      return result;
    },
    // 工具：Float32转Int16 PCM
    floatTo16BitPCM (float32Array) {
      const out = new Int16Array(float32Array.length);
      for (let i = 0; i < float32Array.length; i++) {
        let s = Math.max(-1, Math.min(1, float32Array[i]));
        out[i] = s < 0 ? s * 0x8000 : s * 0x7FFF;
      }
      return out;
    },
    // TTS播放队列
    enqueueTTS (blob, text) {
      this.ttsQueue.push({ blob, text });
      if (!this.isPlayingTTS) this.playNextTTS();
    },
    playNextTTS () {
      if (this.ttsQueue.length === 0) {
        this.isPlayingTTS = false;
        return;
      }
      this.isPlayingTTS = true;
      const { blob } = this.ttsQueue.shift();
      const url = URL.createObjectURL(blob);
      const player = this.$refs.ttsPlayer;
      if (!player) {
        const audio = new Audio(url);
        audio.onended = () => {
          URL.revokeObjectURL(url);
          this.isPlayingTTS = false;
          this.playNextTTS();
        };
        audio.play();
        return;
      }
      player.src = url;
      player.onended = () => {
        URL.revokeObjectURL(url);
        this.isPlayingTTS = false;
        this.playNextTTS();
      };
      player.play();
    },

    stopAllTTS () {
      try {
        this.ttsQueue = [];
        const player = this.$refs.ttsPlayer;
        if (player) {
          player.pause();
          player.src = '';
        }
        if (window.speechSynthesis) {
          window.speechSynthesis.cancel();
        }
      } catch (_) {}
      this.isPlayingTTS = false;
    },

    // 使用识别结果填充输入框
    useTranscript () {
      if (this.transcript) {
        this.inputMessage = this.transcript
        this.adjustTextareaHeight()
      }
    },

    // 预览语音播放：使用浏览器 SpeechSynthesis
    previewPlayTTS (text) {
      try {
        if (!window.speechSynthesis) {
          alert('当前浏览器不支持语音合成。')
          return
        }
        const utter = new SpeechSynthesisUtterance(text || '这是语音播放的预览效果。')
        utter.lang = 'zh-CN'
        utter.rate = 1
        utter.pitch = 1
        utter.onstart = () => { this.isPlayingTTS = true }
        utter.onend = () => { this.isPlayingTTS = false }
        window.speechSynthesis.cancel()
        window.speechSynthesis.speak(utter)
      } catch (e) {
        console.error('语音合成失败', e)
        this.isPlayingTTS = false
      }
    },

    // 播放音频
    playMp3FromBase64 (b64) {
      console.log('播放音频')
      try {
        const bin = atob(b64)
        const len = bin.length
        const buf = new Uint8Array(len)
        for (let i = 0; i < len; i++) buf[i] = bin.charCodeAt(i)
        const blob = new Blob([buf], { type: 'audio/mpeg' })
        const url = URL.createObjectURL(blob)
        const audio = new Audio(url)
        audio.play()
      } catch (e) {
        console.error('播放失败', e)
      }
    },

    // 转base64
    async arrayBufferToBase64 (buffer) {
      let binary = ''
      const bytes = new Uint8Array(buffer)
      const len = bytes.byteLength
      for (let i = 0; i < len; i++) {
        binary += String.fromCharCode(bytes[i])
      }
      return window.btoa(binary)
    },

    // 开始录音
    async startRecording () {
      try {
        // 初始化WebSocket连接
        this.ws = new WebSocket(this.getWsUrl());
        this.ws.binaryType = "arraybuffer";

        this.ws.onmessage = (e) => {
          const msg = JSON.parse(e.data);
          if (msg.type === "transcript") {
            this.transcript = msg.text;
          } else if (msg.type === "tts") {
            const audioBlob = new Blob([new Uint8Array(msg.data)], { type: "audio/wav" });
            this.$refs.player.src = URL.createObjectURL(audioBlob);
            this.$refs.player.play();
          }
        };

        this.ws.onopen = () => {
          console.log('WebSocket连接已打开');
        };

        this.ws.onclose = () => {
          console.log('WebSocket连接已关闭');
        };

        this.ws.onerror = (error) => {
          console.error('WebSocket连接错误:', error);
          this.isRecording = false;
          alert('语音识别服务连接失败，请稍后再试。');
        };

        const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
        this.mediaRecorder = new MediaRecorder(stream, { mimeType: "audio/webm" });
        this.mediaRecorder.ondataavailable = (e) => {
          if (e.data.size > 0 && this.ws.readyState === 1) {
            e.data.arrayBuffer().then(buf => {
              this.ws.send(buf);
            });
          }
        };
        this.mediaRecorder.start(250); // 每 250ms 发一段 
        this.isRecording = true;
      } catch (error) {
        console.error('录音失败:', error);
        if (error.name === 'NotAllowedError') {
          alert('麦克风权限被拒绝，请在浏览器设置中允许麦克风访问权限后重试。');
        } else {
          alert('录音功能初始化失败，请检查麦克风是否可用。');
        }
        this.isRecording = false;
      }
    },

    // 停止录音
    stopRecording () {
      if (this.mediaRecorder && this.mediaRecorder.state !== 'inactive') {
        this.mediaRecorder.stop();
        if (this.ws && this.ws.readyState === 1) {
          this.ws.send(JSON.stringify({ event: "end" }));
          // 延迟关闭WebSocket，确保数据发送完成
          setTimeout(() => {
            if (this.ws) {
              this.ws.close();
              this.ws = null;
            }
          }, 500);
        }
        this.isRecording = false;
      }
    }
  }
}
</script>

<style scoped>
/* 全局样式 */
.AI-index {
  height: 100vh;
  background: #f9fafb;
  display: flex;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Microsoft YaHei', sans-serif;
  position: relative;
  overflow: hidden;
}

/* 历史记录面板 */
.history {
  width: 280px;
  height: 100%;
  background: #ffffff;
  border: none; /* 移除所有边框 */
  position: absolute;
  left: 0;
  transition: all 0.2s ease-out;
  display: flex;
  flex-direction: column;
  padding: 12px;
  box-shadow: 2px 0 10px rgba(0, 0, 0, 0.05);
}

.nohistory {
  width: 64px;
  height: 100%;
  background: #ffffff;
  position: absolute;
  left: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  padding: 12px 0;
  border: none; /* 移除所有边框 */
  box-shadow: 2px 0 10px rgba(0, 0, 0, 0.05);
}

/* 历史记录按钮区域 */
.history-buttons {
  margin-bottom: 12px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 0; /* 移除内边距，避免产生多余的白色空间 */
  background: transparent; /* 确保背景透明 */
}

/* 历史记录按钮样式 */
.AI-index .history-btn {
  background-color: #f3f4f6;
  color: #374151;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
  padding: 12px 16px;
  display: flex;
  align-items: center;
  gap: 8px;
  justify-content: flex-start;
  width: 100%;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  transform: translateY(0);
}

/* 返回按钮样式 - 红色渐变 */
.AI-index .back-btn {
  background: linear-gradient(135deg, #ef4444, #dc2626);
  color: white;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
  padding: 12px 16px;
  display: flex;
  align-items: center;
  gap: 8px;
  justify-content: flex-start;
  width: 100%;
  box-shadow: 0 1px 3px rgba(239, 68, 68, 0.2);
  transform: translateY(0);
}

.nohistory .history-btn {
  width: 44px;
  height: 44px;
  border-radius: 10px;
  padding: 0;
  font-size: 16px;
  justify-content: center;
}

.nohistory .back-btn {
  width: 44px;
  height: 44px;
  border-radius: 10px;
  padding: 0;
  font-size: 16px;
  justify-content: center;
}

/* 修复收缩时按钮文字溢出问题 */
.nohistory .history-btn span {
  display: none;
}

/* 收回侧边栏时隐藏返回按钮文字 */
.nohistory .back-btn span {
  display: none;
}

.AI-index .history-btn:hover {
  background-color: #e5e7eb;
  color: #1f2937;
  transform: translateY(-1px);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.AI-index .back-btn:hover {
  background: linear-gradient(135deg, #dc2626, #b91c1c);
  transform: translateY(-1px);
  box-shadow: 0 2px 6px rgba(239, 68, 68, 0.3);
}

.AI-index .history-btn:active {
  background-color: #d1d5db;
  transform: translateY(0);
}

.AI-index .back-btn:active {
  background: #b91c1c;
  transform: translateY(0);
}


/* 新建对话按钮样式 */
.AI-index .new-chat-button {
  background: linear-gradient(135deg, #0ea5e9, #0284c7);
  color: white;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
  padding: 12px 16px;
  display: flex;
  align-items: center;
  gap: 8px;
  justify-content: flex-start;
  width: 100%;
  box-shadow: 0 2px 4px rgba(14, 165, 233, 0.2);
  transform: translateY(0);
}

.nohistory .new-chat-button {
  width: 44px;
  height: 44px;
  border-radius: 10px;
  padding: 0;
  justify-content: center;
}

/* 修复收缩时按钮文字溢出问题 */
.nohistory .new-chat-button span {
  display: none;
}

.AI-index .new-chat-button:hover {
  background: linear-gradient(135deg, #0284c7, #0369a1);
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(14, 165, 233, 0.3);
}

.AI-index .new-chat-button:active {
  transform: translateY(0);
  box-shadow: 0 2px 4px rgba(14, 165, 233, 0.2);
}

/* 历史记录内容 */
.AI-index .history-content {
  flex: 1;
  overflow-y: auto;
  margin-top: 8px;
  padding: 0;
}

.AI-index .history-content::-webkit-scrollbar {
  width: 4px;
}

.AI-index .history-content::-webkit-scrollbar-track {
  background: #f9fafb;
  border-radius: 2px;
}

.AI-index .history-content::-webkit-scrollbar-thumb {
  background: #d1d5db;
  border-radius: 2px;
}

.AI-index .history-content::-webkit-scrollbar-thumb:hover {
  background: #9ca3af;
}

/* 历史记录项 */
.AI-index .history-item {
  width: 100%;
  margin-bottom: 4px;
  padding: 12px 16px;
  border-radius: 6px;
  background: #ffffff;
  border: none; /* 移除边框，避免可能的白色线条 */
  color: #374151;
  font-size: 14px;
  line-height: 1.5;
  cursor: pointer;
  transition: all 0.15s ease;
  word-break: break-word;
  white-space: normal;
  display: flex;
  align-items: flex-start;
  gap: 8px;
  min-height: 44px;
  position: relative;
}

/* 对话图标 */
.AI-index .history-item::before {
  content: '💬';
  font-size: 16px;
  flex-shrink: 0;
  margin-top: 2px;
}

/* 对话内容容器 */
.AI-index .history-item-content {
  flex: 1;
  overflow: hidden;
}

/* 对话标题 */
.AI-index .history-item-title {
  font-weight: 500;
  color: #1f2937;
  margin-bottom: 2px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 对话预览 */
.AI-index .history-item-preview {
  font-size: 12px;
  color: #6b7280;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 悬停状态 */
.AI-index .history-item:hover {
  background: #f9fafb;
  border-color: #e5e7eb;
  transform: none;
}

/* 选中状态 */
.AI-index .history-item.selected {
  background: #f0f9ff;
  border-color: #bae6fd;
  color: #0369a1;
}

.AI-index .history-item.selected .history-item-title {
  color: #0369a1;
}

.AI-index .history-item.selected .history-item-preview {
  color: #0ea5e9;
}

/* 历史记录操作按钮样式 */
.history-item-actions {
  display: none;
  gap: 4px;
  margin-left: auto;
}

/* 鼠标悬停时显示操作按钮 */
.AI-index .history-item:hover .history-item-actions {
  display: flex;
}

/* 单个操作按钮样式 */
.history-item-action-btn {
  width: 24px;
  height: 24px;
  border: none;
  background: transparent;
  color: #6b7280;
  cursor: pointer;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.15s ease;
  font-size: 14px;
}

.history-item-action-btn:hover {
  background: #f3f4f6;
  color: #374151;
}

.history-item-action-btn:active {
  background: #e5e7eb;
}

/* 移动端隐藏历史记录项的内容，只显示图标 */
.nohistory .history-content {
  display: none;
}

/* 历史记录面板分割线 */
.history-divider {
  height: 1px;
  background: transparent; /* 移除分割线背景，避免白色横框 */
  margin: 8px 0;
}

/* 历史记录面板顶部标题 */
.history-header {
  padding: 8px 12px;
  font-weight: 600;
  color: #374151;
  font-size: 13px;
  text-transform: uppercase;
  letter-spacing: 0.025em;
  user-select: none;
}



/* 空状态提示 */
.empty-history {
  padding: 32px 16px;
  text-align: center;
  color: #6b7280;
}

.empty-history-icon {
  font-size: 48px;
  margin-bottom: 16px;
  opacity: 0.5;
}

.empty-history-text {
  font-size: 14px;
  line-height: 1.5;
}

/* 左侧面板布局 - 统一管理 */
.AI-index .left-side {
  position: fixed;
  top: 0;
  left: 0;
  bottom: 0;
  z-index: 100;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  padding-top: 90px; /* 进一步增加顶部内边距，确保历史记录完全不被上方信息栏挡住 */
  background-color: #ffffff;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.08);
  border: none; /* 移除所有边框 */
}

/* 右侧内容区域布局 - 统一管理 */
.AI-index .right-side {
  flex: 1;
  display: flex;
  flex-direction: column;
  position: relative;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  min-height: 100vh;
  box-sizing: border-box;
  margin-left: 64px; /* 默认基于精简面板宽度 */
  background-color: #f9fafb;
  border: none; /* 确保右侧内容区域没有边框 */
  /* 修正：让子元素横向拉伸为全宽，确保滚动条在最右 */
  align-items: stretch;
}

/* 当显示完整历史记录面板时，右侧内容区域留出空间 */
.AI-index .left-side.history + .right-side {
  margin-left: 280px;
}

/* 当左侧面板隐藏时（移动端或强制隐藏），右侧内容区域占满宽度 */
.AI-index .left-side:not(.open) + .right-side {
  margin-left: 0;
}

/* 主聊天区域 */
.AI-index .now {
  flex: 1;
  display: flex;
  flex-direction: column;
  position: relative;
  min-height: 100vh;
}

/* 聊天标题区域 */
.chat-header {
  display: flex;
  align-items: center;
  padding: 16px 24px;
  border-bottom: none; /* 移除下边框，避免白色横框 */
  background: white;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.chat-title {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
}

/* 滚动容器 - 确保内容不会被遮挡 */
.AI-index .scroll-container {
  flex: 1;
  overflow-y: auto;
  position: relative;
  padding: 24px 20px;
  padding-bottom: 120px; /* 确保内容不会被输入区域遮挡 */
  scroll-behavior: smooth;
  background: #f9fafb;
  /* 全宽，滚动条在页面最右侧 */
  max-width: none;
  margin: 0;
  display: block;
  width: 100%;
}

/* 对话内容居中容器 - 优化滚动条样式，确保滚动条在最右侧 */
.chat-content-wrapper {
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 24px;
  background: transparent;
  border: none;
}

/* 自定义滚动条样式 - 使其更细并位于最右侧 */
.AI-index .scroll-container::-webkit-scrollbar {
  width: 6px;
}

.AI-index .scroll-container::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.AI-index .scroll-container::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.AI-index .scroll-container::-webkit-scrollbar-thumb:hover {
  background: #a1a1a1;
}

/* 对话对容器 - 将一问一答作为整体 */
.conversation-pair {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 对话时间戳样式 */
.conversation-timestamp {
  text-align: center;
  font-size: 12px;
  color: #6b7280;
  margin: 16px 0;
  position: relative;
}

.conversation-timestamp::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 0;
  width: 100%;
  height: 1px;
  background: transparent; /* 移除横线背景，避免白色横框 */
  z-index: 1;
}

.conversation-timestamp span {
  position: relative;
  background: #f9fafb;
  padding: 0 12px;
  z-index: 2;
}

/* 消息基础样式 */
.chat-message {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  animation: fadeIn 0.3s ease-in;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(4px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 用户消息样式 - 靠右对齐 */
.chat-message-me {
  justify-content: flex-end;
}

/* AI消息样式 - 靠左对齐 */
.chat-message-ai {
  justify-content: flex-start;
}

/* 头像区域 - 圆形显示 */
.chat-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
  color: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* 用户头像 - 蓝色 */
.chat-avatar-me {
  background: linear-gradient(135deg, #0ea5e9, #0284c7);
}

/* AI头像 - 绿色 */
.chat-avatar-ai {
  background: linear-gradient(135deg, #10b981, #059669);
}

/* 消息内容容器 */
.message-content {
  max-width: 75%;
}

/* 消息气泡基础样式 */
.message-bubble {
  padding: 12px 16px;
  border-radius: 18px;
  font-size: 14px;
  line-height: 1.5;
  word-wrap: break-word;
  position: relative;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

/* 用户消息气泡 - 蓝色 */
.message-bubble-me {
  background: linear-gradient(135deg, #0ea5e9, #0284c7);
  color: white;
  border-bottom-right-radius: 8px;
  margin-right: 8px;
  box-shadow: 0 1px 2px rgba(14, 165, 233, 0.2); /* 降低阴影强度 */
}

/* AI消息气泡 - 灰白色 */
.message-bubble-ai {
  background: #ffffff;
  color: #374151;
  border: none; /* 移除边框，避免白色横框 */
  border-bottom-left-radius: 8px;
  margin-left: 8px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

/* 输入区域 - 固定在底部 */
.input-area {
  position: fixed;
  bottom: 0;
  left: 64px; /* 默认基于精简面板宽度 */
  right: 0;
  background: white;
  padding: 16px 24px;
  border-top: none; /* 移除上边框，避免白色横框 */
  box-shadow: 0 -4px 20px rgba(0, 0, 0, 0.05);
  box-sizing: border-box;
  z-index: 99;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

/* 当显示完整历史记录面板时，调整输入区域宽度 */
.AI-index .left-side.history ~ .input-area {
  left: 280px;
}

/* 当左侧面板隐藏时（移动端或强制隐藏），输入区域占满宽度 */
.AI-index .left-side:not(.open) ~ .input-area {
  left: 0;
  width: 100%;
}

/* 输入框容器 */
.input-container {
  display: flex;
  align-items: flex-end;
  gap: 12px;
  max-width: 800px;
  margin: 0 auto;
  width: 100%;
}

/* 文本输入框样式 */
.input-container textarea {
  flex: 1;
  min-height: 44px;
  max-height: 120px;
  background: #f9fafb;
  border: none; /* 移除边框，避免白色横框 */
  border-radius: 16px;
  padding: 12px 16px;
  font-size: 14px;
  color: #374151;
  resize: none;
  outline: none;
  transition: all 0.2s ease;
  font-family: inherit;
  margin: 0;
  box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.05);
}

.input-container textarea::placeholder {
  color: #9ca3af;
}

.input-container textarea:focus {
  border-color: #0ea5e9;
  box-shadow: 0 0 0 3px rgba(14, 165, 233, 0.1);
  background: white;
}

/* 输入框操作按钮区域 */
.input-actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

/* 语音按钮样式（与 .mic-btn 配合，保持圆形与渐变） */
.voice-button {
  background: transparent;
  color: inherit;
  border: none;
  padding: 0;
  border-radius: 9999px;
  cursor: pointer;
  transition: transform 0.2s ease, filter 0.2s ease;
  outline: none;
  white-space: nowrap;
}

.voice-button:hover:not(:disabled) {
  transform: translateY(-1px);
  filter: saturate(1.05);
}

.voice-button.recording {
  background: transparent;
  color: inherit;
}

/* 发送按钮样式 */
.send-button {
  background: linear-gradient(135deg, #0ea5e9, #0284c7);
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 16px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
  outline: none;
  white-space: nowrap;
  box-shadow: 0 2px 4px rgba(14, 165, 233, 0.2);
  transform: translateY(0);
}

.send-button:hover:not(:disabled) {
  background: linear-gradient(135deg, #0284c7, #0369a1);
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(14, 165, 233, 0.3);
}

.send-button:hover:not(:disabled) {
  background: #0284c7;
}

.send-button:disabled {
  background: #e5e7eb;
  cursor: not-allowed;
  opacity: 0.6;
}

/* 返回首页按钮样式 - 移动到历史记录侧边栏内 */
.AI-index .back-to-home {
  position: relative;
  padding: 8px 12px;
  background-color: transparent;
  color: #374151;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  margin: 8px 12px;
  transition: all 0.2s ease;
}

.AI-index .back-to-home:hover {
  background-color: #f3f4f6;
  color: #1f2937;
}

.AI-index .back-to-home:active {
  background-color: #e5e7eb;
}

/* 调整用户消息的布局，确保头像在右侧 */
.chat-message-me {
  justify-content: flex-end;
  flex-direction: row;
}

.chat-message-me .message-content {
  order: 1;
}

.chat-message-me .chat-avatar {
  order: 2;
}

/* 新建对话按钮样式 - 优化交互体验 */
.AI-index .new-chat-btn {
  background-color: #f3f4f6;
  color: #374151;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
  padding: 10px 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  margin: 8px 12px;
  width: auto;
  height: auto;
  min-width: auto;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  transform: translateY(0);
}

.AI-index .new-chat-btn:hover {
  background-color: #e5e7eb;
  color: #1f2937;
  transform: translateY(-1px);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

/* 录音指示器 - 简化样式 */
.recording-indicator {
  position: fixed;
  left: 50%;
  bottom: 100px;
  transform: translateX(-50%);
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 20px;
  background: white;
  border-radius: 24px;
  border: 1px solid #e5e7eb;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  z-index: 100;
}

.recording-dot {
  width: 8px;
  height: 8px;
  background: #ef4444;
  border-radius: 50%;
  animation: pulse 1.5s infinite;
}

@keyframes pulse {
  0% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.7;
    transform: scale(1.2);
  }
  100% {
    opacity: 1;
    transform: scale(1);
  }
}

.stop-recording-button {
  background: #ef4444;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.2s ease;
}

.stop-recording-button:hover {
  background: #dc2626;
}

/* 响应式设计 */
/* 移动端菜单按钮 - 默认隐藏 */
.mobile-menu-button {
  display: none;
  background: none;
  border: none;
  color: #6b7280;
  cursor: pointer;
  padding: 8px;
  margin-right: 8px;
  border-radius: 6px;
  transition: all 0.2s ease;
}

.mobile-menu-button:hover {
  background: #f3f4f6;
  color: #374151;
}

/* 主响应式设计 */
@media (max-width: 768px) {
  .AI-index .left-side {
    display: none;
    position: fixed;
    top: 0;
    left: 0;
    bottom: 0;
    width: 280px;
    z-index: 1000;
    box-shadow: 2px 0 10px rgba(0, 0, 0, 0.1);
  }
  
  .AI-index .left-side.open {
    display: flex;
  }
  
  .AI-index .right-side {
    left: 0;
    width: 100%;
  }
  
  .AI-index .chat-header {
    padding-left: 16px;
    padding-right: 16px;
  }
  
  .mobile-menu-button {
    display: flex;
  }
  
  .chat-content-wrapper {
    max-width: 95%;
  }
  
  /* 移动端滚动与安全区适配 */
  .AI-index .scroll-container {
    padding: 16px 12px;
    padding-bottom: calc(160px + env(safe-area-inset-bottom));
  }
  
  .message-content {
    max-width: 88%;
  }
  
  .input-container {
    max-width: 100%;
  }
  
  .input-area {
    left: 0;
    padding: 12px 16px;
    padding-bottom: calc(8px + env(safe-area-inset-bottom));
  }
  
  /* 底部控件与语音球位置适配 */
  .record-bar {
    width: 92vw;
    height: 12px;
    bottom: calc(76px + env(safe-area-inset-bottom));
  }
  .voice-orb-wrapper {
    bottom: calc(88px + env(safe-area-inset-bottom));
  }
  .voice-gradient-overlay {
    bottom: calc(90px + env(safe-area-inset-bottom));
    height: 90vh;
    width: 100vw;
  }
  
  /* 移动端麦克风按钮尺寸微调 */
  .mic-btn {
    width: 48px;
    height: 48px;
  }
  
  /* 历史记录操作按钮 - 移动端始终显示 */
  .history-item-actions {
    opacity: 1 !important;
  }
  
  /* 移动端按钮样式优化 */
  .AI-index .history-btn,
  .AI-index .new-chat-button {
    position: relative;
    padding: 8px 12px;
    border-radius: 8px;
    font-size: 13px;
    margin: 4px 8px;
    width: auto !important;
    height: auto !important;
    min-width: auto !important;
    max-width: none !important;
    justify-content: center !important;
  }

  /* 移动端保留返回按钮的高亮样式 */
  .AI-index .back-btn {
    display: inline-flex !important;
    align-items: center;
    gap: 6px;
    padding: 8px 12px !important;
    border-radius: 10px !important;
    background: linear-gradient(135deg, #ef4444, #dc2626) !important;
    color: #fff !important;
    box-shadow: 0 2px 6px rgba(239, 68, 68, 0.3) !important;
  }

  .history-buttons {
    flex-direction: row !important;
    flex-wrap: wrap;
    justify-content: center;
    gap: 4px !important;
    padding: 4px 8px !important;
  }

  /* 确保图标按钮在移动端正确显示 */
  .nohistory .history-btn,
  .nohistory .back-btn,
  .nohistory .new-chat-button {
    width: 36px !important;
    height: 36px !important;
  }
}

/* 小屏幕设备优化 */
@media (max-width: 480px) {
  .chat-avatar {
    width: 32px;
    height: 32px;
    font-size: 12px;
  }
  
  .message-bubble {
    padding: 10px 14px;
    font-size: 13px;
  }
  
  .input-container textarea {
    min-height: 36px;
    font-size: 13px;
    padding: 8px 12px;
  }
  
  .input-actions button {
    padding: 6px 10px;
    font-size: 12px;
  }
}
    /* 识别结果区域样式 */
    .transcript-area {
      background: #f3f4f6;
      border-radius: 8px;
      padding: 12px 16px;
      margin: 12px;
      border: 1px solid #e5e7eb;
    }
    
    .transcript-content {
      display: flex;
      justify-content: space-between;
      align-items: center;
      gap: 12px;
    }
    
    .use-transcript-btn {
      background: #16a34a;
      color: white;
      border: none;
      padding: 6px 12px;
      border-radius: 4px;
      font-size: 12px;
      cursor: pointer;
      transition: background-color 0.2s;
    }
    
    .use-transcript-btn:hover {
      background: #15803d;
    }
    
    /* 音频播放器区域 */
    .audio-player-area {
      padding: 0 12px 12px;
    }
    
    .audio-player-area audio {
      width: 100%;
      max-width: 400px;
    }

    /* 录音条 - 美化 */
     .record-bar {
       position: fixed;
       left: 50%;
       bottom: 100px;
       transform: translateX(-50%);
       width: 64%;
       max-width: 560px;
       height: 14px;
       background: rgba(243, 244, 246, 0.9);
       border: 1px solid #e5e7eb;
       border-radius: 999px;
       overflow: hidden;
       box-shadow: 0 6px 20px rgba(14, 165, 233, 0.08), inset 0 1px 2px rgba(0, 0, 0, 0.06);
       backdrop-filter: saturate(1.2) blur(2px);
     }
 
     .record-bar__fill {
        height: 100%;
        background: linear-gradient(90deg, #0ea5e9 0%, #22d3ee 60%, #22c55e 100%);
        width: 0%;
        transition: width 140ms linear;
        transform-origin: left;
      }

      /* 预览模式：自动动画，不依赖音频输入 */
      .record-bar.demo .record-bar__fill {
        animation: barDemo 2.8s ease-in-out infinite;
      }
      @keyframes barDemo {
        0%   { transform: scaleX(0.10); }
        25%  { transform: scaleX(0.85); }
        50%  { transform: scaleX(0.35); }
        75%  { transform: scaleX(0.90); }
        100% { transform: scaleX(0.20); }
      }
      .record-bar.demo .record-bar__glow {
        animation-duration: 2.4s; /* 略快一点 */
      }
 
     .record-bar__glow {
       position: absolute;
       inset: -12px 0 -12px 0;
       background: radial-gradient(closest-side, rgba(34, 211, 238, 0.18), transparent 70%);
       animation: glowPulse 2.2s ease-in-out infinite;
       pointer-events: none;
     }
 
     @keyframes glowPulse {
       0% { opacity: 0.5 }
       50% { opacity: 0.9 }
       100% { opacity: 0.5 }
     }
 
     .record-bar__content {
       position: absolute;
       top: 50%;
       left: 12px;
       transform: translateY(-50%);
       display: flex;
       align-items: center;
       gap: 8px;
       color: #0f172a;
       font-size: 12px;
       font-weight: 600;
     }
 
     .record-bar__content .iconfont {
       color: #0ea5e9;
       font-size: 14px;
     }
    /* 语音模式：小球对话 */
    .voice-orb-wrapper {
      position: fixed;
      left: 50%;
      bottom: 110px;
      transform: translateX(-50%);
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 10px;
      z-index: 100;
    }
    .voice-orb {
      position: relative;
      width: 96px;
      height: 96px;
      border-radius: 50%;
      background: radial-gradient(circle at 30% 30%, #22d3ee, #0ea5e9 70%);
      box-shadow: 0 12px 34px rgba(14, 165, 233, 0.38), inset 0 -5px 14px rgba(2, 6, 23, 0.12);
      animation: orbPulse 2.2s ease-in-out infinite;
      display: inline-flex;
      align-items: center;
      justify-content: center;
      overflow: visible;
      cursor: pointer;
    }
    .voice-orb.is-speaking {
      background: radial-gradient(circle at 30% 30%, #34d399, #0ea5e9 70%);
      box-shadow: 0 12px 36px rgba(16, 185, 129, 0.35), inset 0 -4px 12px rgba(2, 6, 23, 0.12);
      animation: orbSpeak 1.2s ease-in-out infinite;
    }
    @keyframes orbPulse {
      0% { transform: scale(1); filter: drop-shadow(0 0 0 rgba(14,165,233,.1)); }
      50% { transform: scale(1.08); filter: drop-shadow(0 10px 24px rgba(14,165,233,.35)); }
      100% { transform: scale(1); filter: drop-shadow(0 0 0 rgba(14,165,233,.1)); }
    }
    @keyframes orbSpeak {
      0% { transform: scale(1.02); }
      50% { transform: scale(1.12); }
      100% { transform: scale(1.02); }
    }
    /* 新增：核心小球与环形波纹、均衡器动画 */
    .orb-core {
      width: 88px;
      height: 88px;
      border-radius: 50%;
      background: radial-gradient(circle at 30% 30%, #7dd3fc, #1d4ed8 75%);
      box-shadow: inset 0 -7px 14px rgba(2, 6, 23, 0.10);
      transform: scale(calc(1 + (var(--level, 0) / 180)));
      transition: transform 80ms ease-out;
    }
    .voice-orb.is-speaking .orb-core {
      background: radial-gradient(circle at 30% 30%, #86efac, #22c55e 75%);
    }
    .orb-wave {
      position: absolute;
      inset: 0;
      pointer-events: none;
    }
    .orb-wave .ring {
      position: absolute;
      left: 50%;
      top: 50%;
      width: 116px;
      height: 116px;
      transform: translate(-50%, -50%);
      border-radius: 9999px;
      border: 2px solid rgba(14, 165, 233, 0.35);
      animation: ringPulse 2.6s ease-out infinite;
    }
    .voice-orb.is-speaking .orb-wave .ring {
      border-color: rgba(16, 185, 129, 0.35);
    }
    .orb-wave .r2 { animation-delay: .35s; }
    .orb-wave .r3 { animation-delay: .7s; }

    @keyframes ringPulse {
      0% { transform: translate(-50%, -50%) scale(1); opacity: .5; }
      100% { transform: translate(-50%, -50%) scale(2.2); opacity: 0; }
    }

    .orb-eq {
      position: absolute;
      bottom: -14px;
      left: 50%;
      transform: translateX(-50%);
      display: flex;
      gap: 3px;
      filter: drop-shadow(0 2px 6px rgba(2, 6, 23, 0.25));
    }
    .orb-eq .bar {
      width: 4px;
      height: calc(6px + var(--level, 0) * 0.25px);
      background: linear-gradient(180deg, rgba(99, 102, 241, .85), rgba(56, 189, 248, .85));
      border-radius: 3px;
      transform-origin: bottom;
      animation: eqBeat 1s ease-in-out infinite;
    }
    .voice-orb.is-speaking .orb-eq .bar {
      background: linear-gradient(180deg, rgba(34, 197, 94, .9), rgba(59, 130, 246, .9));
    }
    .orb-eq .b2 { animation-delay: .1s; }
    .orb-eq .b3 { animation-delay: .2s; }
    .orb-eq .b4 { animation-delay: .3s; }
    .orb-eq .b5 { animation-delay: .4s; }

    @keyframes eqBeat {
      0%, 100% { transform: scaleY(.9); }
      50% { transform: scaleY(1.15); }
    }

    /* 连接中指示 */
    .orb-connecting {
      position: absolute;
      inset: -4px;
      border-radius: 9999px;
      border: none;
      animation: spin 1.2s linear infinite;
      pointer-events: none;
    }
    @keyframes spin {
      0% { transform: rotate(0deg); }
      100% { transform: rotate(360deg); }
    }

    .voice-orb-status { display: none; }
     /* 美化覆盖：玻璃态微光 + 柔和波纹 */
     .voice-orb {
       width: 96px;
       height: 96px;
       border-radius: 50%;
       backdrop-filter: blur(8px);
       -webkit-backdrop-filter: blur(8px);
       border: 1px solid rgba(255, 255, 255, .28);
       box-shadow: 0 10px 28px rgba(14, 165, 233, 0.22), inset 0 -4px 10px rgba(2, 6, 23, 0.10);
       background: radial-gradient(circle at 30% 30%, rgba(125, 211, 252, .40), rgba(14, 165, 233, .40) 70%);
       cursor: pointer;
     }
     .voice-orb.is-speaking {
       box-shadow: 0 8px 22px rgba(16, 185, 129, 0.22), inset 0 -3px 8px rgba(2, 6, 23, 0.12);
       background: radial-gradient(circle at 30% 30%, rgba(134, 239, 172, .35), rgba(14, 165, 233, .35) 70%);
     }
     .orb-core {
       width: 86px;
       height: 86px;
       border-radius: 50%;
       background: radial-gradient(circle at 30% 30%, rgba(125, 211, 252, .6), rgba(29, 78, 216, .4) 75%);
       box-shadow: inset 0 -6px 12px rgba(2, 6, 23, 0.10);
       transform: scale(calc(1 + (var(--level, 0) / 220)));
       transition: transform 80ms ease-out;
     }
     .voice-orb.is-speaking .orb-core {
       background: radial-gradient(circle at 30% 30%, rgba(134, 239, 172, .6), rgba(34, 197, 94, .4) 75%);
     }
     .orb-wave .ring {
       border-width: 2px;
       border-color: rgba(14, 165, 233, 0.24);
       animation: ringPulse 3.4s ease-out infinite;
     }
     .voice-orb.is-speaking .orb-wave .ring {
       border-color: rgba(16, 185, 129, 0.22);
     }
     .orb-eq { display: none; }
     .orb-connecting { display: none; }

      /* 语音模式：从圆球向上扩展的渐变叠层 */
      .voice-gradient-overlay {
        position: fixed;
        left: 0;
        right: 0;
        bottom: 110px;
        width: 100vw;
        height: 86vh;
        pointer-events: none;
        z-index: 85;
        background: radial-gradient(ellipse at center,
          rgba(14, 165, 233, 0.28) 0%,
          rgba(14, 165, 233, 0.18) 24%,
          rgba(14, 165, 233, 0.10) 40%,
          rgba(14, 165, 233, 0.05) 60%,
          rgba(14, 165, 233, 0.0) 100%);
        filter: blur(3px);
      }
      .voice-gradient-overlay.is-speaking {
        background: radial-gradient(ellipse at center,
          rgba(34, 197, 94, 0.30) 0%,
          rgba(34, 197, 94, 0.20) 24%,
          rgba(34, 197, 94, 0.12) 40%,
          rgba(34, 197, 94, 0.06) 60%,
          rgba(34, 197, 94, 0.0) 100%);
      }

      /* 语音模式下气泡仍显示：微调列表层级和边距 */
      .voice-mode-bubbles .chat-content-wrapper {
        position: relative;
        z-index: 90;
      }
      .voice-mode-bubbles .conversation-pair {
        margin-top: 10px;
        margin-bottom: 10px;
      }
    /* 麦克风按钮美化 */
    .mic-btn {
      background: linear-gradient(135deg, #38bdf8 0%, #6366f1 100%);
      box-shadow: 0 8px 20px rgba(2, 6, 23, 0.12), inset 0 -2px 6px rgba(2, 6, 23, 0.15);
      border: 1px solid rgba(226, 232, 240, 0.7);
    }
    .mic-btn:hover {
      transform: translateY(-1px);
      box-shadow: 0 12px 28px rgba(2, 6, 23, 0.16), inset 0 -2px 6px rgba(2, 6, 23, 0.18);
      filter: saturate(1.05);
    }
    .mic-btn i.iconfont {
      font-size: 18px;
    }
    .mic-icon {
      width: 20px;
      height: 20px;
      display: block;
    }
    /* 光晕效果 */
    .mic-btn::before {
      content: '';
      position: absolute;
      inset: -3px;
      border-radius: 9999px;
      background: conic-gradient(from 180deg at 50% 50%, rgba(56,189,248,.45), rgba(99,102,241,.45), rgba(56,189,248,.45));
      opacity: 0;
      transition: opacity .2s ease, filter .2s ease;
      filter: blur(6px);
      pointer-events: none;
    }
    .mic-btn:hover::before { opacity: .35; }
    /* 录音脉冲 */
    .mic-btn.recording::after {
      content: '';
      position: absolute;
      inset: -6px;
      border-radius: 9999px;
      box-shadow: 0 0 0 6px rgba(14,165,233,.25), 0 0 24px rgba(14,165,233,.45);
      animation: micPulse 1.6s ease-in-out infinite;
      pointer-events: none;
    }
    @keyframes micPulse {
      0%   { transform: scale(1); }
      50%  { transform: scale(1.08); }
      100% { transform: scale(1); }
    }

    /* 胶囊按钮与音柱增强 */
    .mic-pill { padding-right: 10px; }
    .mic-text { letter-spacing: .02em; }
    .mic-bars {
      display: inline-flex;
      align-items: flex-end;
      gap: 2px;
      margin-left: 6px;
      height: 14px;
    }
    .mic-bars .bar {
      width: 2px;
      border-radius: 9999px;
      background: rgba(255,255,255,.85);
      box-shadow: 0 0 10px rgba(255,255,255,.25);
      animation: micBar 1s ease-in-out infinite;
    }
    .mic-bars .bar:nth-child(1) { height: 6px; animation-delay: 0s; }
    .mic-bars .bar:nth-child(2) { height: 10px; animation-delay: .2s; }
    .mic-bars .bar:nth-child(3) { height: 8px; animation-delay: .4s; }
    @keyframes micBar {
      0%, 100% { transform: scaleY(1); }
      50% { transform: scaleY(1.4); }
    }

    /* 悬浮气泡提示（不与现有 before/after 冲突） */
    .voice-button[data-tip]::after {
      content: attr(data-tip);
      position: absolute;
      bottom: calc(100% + 8px);
      left: 50%;
      transform: translateX(-50%);
      background: rgba(17, 24, 39, 0.92);
      color: #fff;
      padding: 4px 8px;
      border-radius: 6px;
      font-size: 12px;
      white-space: nowrap;
      opacity: 0;
      pointer-events: none;
      transition: opacity .15s ease, transform .15s ease;
    }
    .voice-button:hover::after { opacity: .95; transform: translateX(-50%) translateY(-2px); }
    .voice-button[data-tip]::before {
      content: '';
      position: absolute;
      bottom: calc(100% + 2px);
      left: 50%;
      transform: translateX(-50%);
      border: 6px solid transparent;
      border-top-color: rgba(17, 24, 39, 0.92);
      opacity: 0;
      transition: opacity .15s ease;
      pointer-events: none;
    }
    .voice-button:hover::before { opacity: .95; }
    /* 气泡对齐修正 */
    .chat-message { width: 100%; }
    .message-content { flex: 1; display: flex; }
    .chat-message-me .message-content { justify-content: flex-end; }
    .chat-message-ai .message-content { justify-content: flex-start; }
    /* 限制气泡实际宽度，避免因头像与间距导致换行错位 */
    .message-bubble { max-width: 68%; }
  </style>
