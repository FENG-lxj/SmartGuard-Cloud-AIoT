<template>
  <div class="AI-index">
    <div :class="history ? 'history' : 'nohistory'" @click="toggleHistory">

      <!-- 按钮用于控制是否展示历史记录 -->
      <button class="hisBtn" title="历史记录">历史记录</button>
      <button class="hisBtn" title="新建对话" @click="newTalk">新建对话</button>

      <!-- 历史记录内容，循环从hisMessage中获取数据 -->
      <div class="history-content" v-if="history">
        <!-- <div class="history-item"
        v-for="(item, index) in this.$store.state.hisMessage" :key = "index"
        @click="Hissubmit(item)">
          {{ item }}
        </div> -->

        <div class="history-item">
          如何让室内不那么干燥
        </div>
        <div class="history-item">
          如何提高家庭空气湿度
        </div>
        <div class="history-item">
          如何降低家庭有毒气体含量
        </div>
        <div class="history-item">
          火灾发生怎么办
        </div>
        <div class="history-item">
          发生地震该怎么办
        </div>
      </div>
    </div>

    <!-- AI对话框 -->
    <div class="now">
      <div class="scroll-container" ref="scrollContainer">
        <div class="count">
              <!-- 消息整体 -->
          <div :class="message.sender === 'me' ? 'chat-message-me' : 'chat-message-other'"
              :style="{'padding-bottom': messages.length - 1 === index ? '2rem' : 'none'}"
              v-for="(message, index) in messages"
              :key="index">

            <!-- 消息 -->
            <div :class="message.sender === 'me' ? 'message-me-asWhole-right' : 'message-other-asWhole-right'">
              <!-- 消息内容 -->
              <div :class="message.sender === 'me' ? 'message-me' : 'message-other'">
                {{ message.content }}
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 输入框和上传按钮 -->

      <div class="awser">
        <textarea ref="textarea" v-model="inputMessage" placeholder="请输入问题" rows="1"
        @input="adjustTextareaHeight" @keyup.enter = "Submit"></textarea>
        <div class="extend">
          <button @click="Submit">上传</button>
          <button @click="Luyin">语音</button>

          <!-- 动态录音条 -->
          <!-- <div v-if="isRecording" class="voice-visualizer">
            <div class="voice-bar"></div>
            <div class="voice-bar"></div>
            <div class="voice-bar"></div>
            <div class="voice-bar"></div>
            <div class="voice-bar"></div>
          </div> -->

        </div>
      </div>
    </div>
    <!-- 动态录音条 父相子绝 -->
    <div v-if="isRecording" class="voice-visualizer">
            <div class="voice-bar"></div>
            <div class="voice-bar"></div>
            <div class="voice-bar"></div>
            <div class="voice-bar"></div>
            <div class="voice-bar"></div>
            <div class="voice-bar"></div>
            <div class="voice-bar"></div>
            <div class="voice-bar"></div>
            <div class="voice-bar"></div>
            <div class="voice-bar"></div>
            <div class="voice-bar"></div>
            <div class="voice-bar"></div>
            <div class="voice-bar"></div>
            <div class="voice-bar"></div>
            <div class="voice-bar"></div>
            <div class="voice-bar"></div>
            <div class="voice-bar"></div>
          </div>
  </div>
</template>

<script>
import { arrayBufferToBase64 } from '@/utils/encode.js'
// import { ref, watch, nextTick } from 'vue'
import axios from 'axios'
import SockJS from 'sockjs-client'
import { Stomp } from '@stomp/stompjs'

export default {
  name: 'AIIndex',
  data () {
    return {
      messages: [],
      inputMessage: '',
      history: false,
      currentMessage: '',
      isRecording: false // 录音状态
    }
  },
  async created () {
    // this.getHistory()
    this.messages = this.$store.state.message
  },

  // setup () {
  //   const scrollContainer = ref(null)

  //   return {
  //     scrollContainer
  //   }
  // },

  watch: {
    messages: {
      handler () {
        this.scrollToBottom()
      },
      deep: true
    }
  },

  methods: {

    // 获取历史记录
    async getHistory () {
      const res = await axios.get('http://47.120.65.85:8023/ai/getHistory', {
        method: 'get',
        // 向服务器发送 POST 请求，并且数据是以 JSON 格式传递
        headers: {
          // 'Content-Type': 'application/json',
          Authorization: 'CHome ' + this.$store.state.guest.token
        }
      })
      console.log('后端获取到的历史记录', res.data)
      this.$store.commit('sethistory', res.data.data)
    },

    // 新建对话，历史记录为空，id为空
    newTalk () {
      this.$store.commit('setmessage', [])
      this.$store.commit('setAIid', '')
    },

    // 让输入框可以自动增加高度
    adjustTextareaHeight () {
      const textarea = this.$refs.textarea
      console.log(textarea.scrollHeight)
      if (textarea.scrollHeight > 70) {
        textarea.style.height = 'auto' // 先设置为auto以获取内容高度
        textarea.style.height = textarea.scrollHeight + 'px' // 设置为内容高度
      } else {
        textarea.style.height = '70px'
      }
    },
    // 点击按钮，发送消息
    async Submit () {
      console.log(this.$refs.textarea.value)
      if (this.$refs.textarea.value !== '') {
        console.log(this.$refs.textarea.value)

        const newMessage = {
          sender: 'me',
          content: this.$refs.textarea.value
        }
        this.$store.commit('Setmessage', newMessage)

        // 清空输入框
        this.inputMessage = ''
        this.$refs.textarea.style.height = '50px' // 重置输入框高度

        // 发送请求并处理流式响应
        this.currentMessage = ''
        const newAIMessage = {
          sender: 'other',
          content: ''
        }
        this.messages.push(newAIMessage)

        try {
          let response
          if (this.$store.state.AIid !== '') {
            response = await fetch('http://47.120.65.85:8023/ai/question', {
              method: 'POST',
              // 向服务器发送 POST 请求，并且数据是以 JSON 格式传递
              headers: {
                'Content-Type': 'application/json',
                Authorization: 'CHome ' + this.$store.state.guest.token
              },
              // 将 JavaScript 对象或数组转换为 JSON 格式的字符串。
              body: JSON.stringify({
                question: this.$refs.textarea.value,
                id: this.$store.state.AIid,
                type: 'chat'
              })
            })
          } else {
            response = await fetch('http://47.120.65.85:8023/ai/question', {
              method: 'POST',
              id: this.$store.state.AIid,
              // 向服务器发送 POST 请求，并且数据是以 JSON 格式传递
              headers: {
                'Content-Type': 'application/json',
                Authorization: 'CHome ' + this.$store.state.guest.token
              },
              // 将 JavaScript 对象或数组转换为 JSON 格式的字符串。
              body: JSON.stringify({
                question: this.$refs.textarea.value
              })
            })
          }

          const reader = response.body.getReader()
          const decoder = new TextDecoder()
          let fullResponse = ''
          let textContent = ''

          // 当有id传过来时，将id保存起来
          if (response.id) {
            const AIid = response.id
            this.$store.commit('setAIid', AIid)
          }

          while (true) {
            const { done, value } = await reader.read()
            if (done) {
              console.log('流式响应结束')
              break
            }

            const chunk = decoder.decode(value, { stream: true })
            fullResponse += chunk

            try {
              // 解析完整的JSON数组
              const jsonArray = JSON.parse(fullResponse)
              if (Array.isArray(jsonArray)) {
                // 提取每个对象的text字段
                const newText = jsonArray
                  .filter(item => item.text)
                  .map(item => item.text)
                  .join('')

                // 逐字显示
                if (newText.length > textContent.length) {
                  for (let i = textContent.length; i < newText.length; i++) {
                    await new Promise(resolve => setTimeout(resolve, 30))
                    this.currentMessage = newText.substring(0, i + 1)
                    this.$set(newAIMessage, 'content', this.currentMessage)
                  }
                  textContent = newText
                }
              }
            } catch (e) {
              console.log('等待完整JSON响应')
            }
          }
        } catch (error) {
          console.error('请求错误:', error)
        }
      }
      this.getHistory()
    },
    // 点击之后，将历史记录中的内容传入AI
    Hissubmit (item) {
      console.log(item)
      const newMessage = {
        sender: 'me',
        content: item
      }
      this.$store.commit('Setmessage', newMessage)
      this.messages.push(newMessage)
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

    // 语音
    async Luyin () {
      this.isRecording = !this.isRecording
      if (this.isRecording) {
        // 初始化WebSocket连接
        const sessionId = Date.now() + '-' + Math.random().toString(36).substr(2)
        // const sessionId = 1
        const sock = new SockJS('http://localhost:8023/CHome')
        this.stompClient = Stomp.over(sock)

        this.stompClient.connect({}, () => {
          // 订阅完整MP3推送
          this.stompClient.subscribe('/topic/audio/' + sessionId, msg => {
            const body = msg.body
            if (body === 'end') return
            this.playMp3FromBase64(body)
          })

          // 开始录音
          this.startRecording(sessionId)
          this.isRecording = true
        }, (error) => {
          console.error('WebSocket连接失败:', error)
          this.isRecording = false
        })
      } else {
        // 停止录音
        this.stopRecording()
        this.isRecording = false
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
    async startRecording (sessionId) {
      try {
        const stream = await navigator.mediaDevices.getUserMedia({ audio: true })
        this.mediaRecorder = new MediaRecorder(stream)

        this.mediaRecorder.ondataavailable = e => {
          if (e.data.size > 0) {
            e.data.arrayBuffer().then(buf => {
              const base64String = arrayBufferToBase64(buf)

              // console.log(e.data)
              this.stompClient.send('/app/audioChat', { sessionId }, base64String)
            })
          }
        }

        this.mediaRecorder.onstop = () => {
          this.stompClient.send('/app/audioEnd', { sessionId }, {})
        }

        this.mediaRecorder.start(200)
      } catch (error) {
        console.error('录音失败:', error)
        if (error.name === 'NotAllowedError') {
          alert('麦克风权限被拒绝，请在浏览器设置中允许麦克风访问权限后重试。')
        } else {
          alert('录音功能初始化失败，请检查麦克风是否可用。')
        }
      }
    },

    // 停止录音
    stopRecording () {
      if (this.mediaRecorder && this.mediaRecorder.state !== 'inactive') {
        this.mediaRecorder.stop()
      }
    }
  }
}
</script>

<style>
.chat-message-other {
display: flex;
padding-left: 1px;
padding-top: 1px;
margin-bottom: 20px;
}

.chat-message-me {
display: flex;
padding-right: 1rem;
padding-top: 1rem;
flex-direction: row-reverse;
margin-bottom: 20px;
}

.message-me-asWhole-headPortrait {
padding: 3px;
}

.message-other-asWhole-headPortrait {
padding: 3px;
}

.message-me-asWhole-right {
display: flex;
flex-direction: column;
margin-left: 0.1rem;
}

.message-other-asWhole-right {
display: flex;
flex-direction: column;
margin-left: 0.1rem;
}

.message-me-asWhole-top {
padding: 3px;
font-family: 微软雅黑;
color: #666;
text-align: right;
}

.message-other-asWhole-top {
padding: 3px;
font-family: 微软雅黑;
color: #666;
}

.message-me {
background: linear-gradient(135deg, #e0f7fa, #b2ebf2);
max-width: 580px;
word-wrap: break-word;
word-break: break-all;
display: inline-block;
width: auto;
padding: 10px 15px;
border-radius: 18px;
box-shadow: 0 2px 4px rgba(0,0,0,0.1);
color: #333;
}

.message-other {
background: linear-gradient(135deg, #f5f5f5, #e0e0e0);
max-width: 580px;
word-wrap: break-word;
word-break: break-all;
display: inline-block;
width: auto;
padding: 10px 15px;
border-radius: 18px;
box-shadow: 0 2px 4px rgba(0,0,0,0.1);
color: #333;
}

.examineeFace_logo_style {
width: 30px;
}

.AI-index{
background: linear-gradient(to bottom right, #FFF8E8, #f9f4e6); border: 2px solid #D4AF37; /* 输入区使用哈达白+金色边框区分 */
position: relative;
height: 740px;
display: flex;
}

.scroll-container {
width: 800px;
height: 640px;
margin-left: 350px;
overflow: hidden;
position: relative;
}

.AI-index .count{
width: 800px;
height: 620px;
/* margin-left: 450px; */
overflow: hidden;
overflow-y: scroll;
margin-bottom: 20px;
}

.AI-index .count::-webkit-scrollbar {
display: none; /* Chrome, Safari and Opera */
}

/* .awser{
position: absolute;
} */

.AI-index .awser textarea{
width: 800px;
height: 70px;
margin-left: 350px;
background: linear-gradient(135deg, rgba(200, 230, 255, 0.95), rgba(180, 220, 255, 0.9));
border-top-right-radius: 10px;
border-top-left-radius: 10px;
border: none;
position: absolute;
bottom: 40px;
resize: none; /* 禁止调整大小 */
padding: 10px; /* 添加内边距 */
box-sizing: border-box; /* 确保内边距和边框包含在宽度内 */
overflow: hidden; /* 隐藏溢出内容 */
outline: none;
transition: all 0.3s ease;
}

.AI-index .awser textarea:focus {
box-shadow: 0 0 15px rgba(41, 182, 246, 0.4);
background: linear-gradient(135deg, rgba(180, 220, 255, 1), rgba(160, 210, 255, 0.95));
}
.AI-index .awser .extend{
width: 800px;
height: 30px;
margin-left: 350px;
bottom: 10px;
border-bottom-left-radius: 10px;
border-bottom-right-radius: 10px;
position: absolute;
/* background-color:  aliceblue; */
background: linear-gradient(135deg, rgba(200, 230, 255, 0.95), rgba(180, 220, 255, 0.9));
display: flex;
justify-content: flex-end;
/* padding-right: 10px; */
}
.AI-index .awser .extend button{
margin-left: 10px;
background: linear-gradient(135deg, #1E50B5, #153a85); /* 按钮使用蒙古蓝深色渐变 */
color: white;
border: none;
padding: 8px 16px;
border-radius: 20px;
cursor: pointer;
transition: all 0.3s ease;
box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.AI-index .awser .extend button:hover {
background: linear-gradient(135deg, #29b6f6, #039be5);
transform: translateY(-2px);
box-shadow: 0 4px 8px rgba(0,0,0,0.2);
}
.nohistory{
/* width: 100px; */
height: 100%;
/* background-color: rgb(130, 168, 168); */

position: absolute;
left: 0;
}
.history{
width: 300px;
height: 100%;
background: linear-gradient(135deg, rgba(245, 249, 241, 0.8), rgba(225, 240, 216, 0.6)); /* 历史区使用主背景色变体区分 */
position: absolute;
left: 0;
border-right: 1px solid rgba(0, 0, 0, 0.1);
backdrop-filter: blur(5px);
transition: all 0.3s ease;
}

.AI-index .hisBtn{
margin-bottom: 20px;
background: linear-gradient(135deg, #3A5F0B, #2D4A08);
color: white;
border: none;
padding: 8px 16px;
border-radius: 20px;
cursor: pointer;
transition: all 0.3s ease;
box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.AI-index .hisBtn:hover {
background: linear-gradient(135deg, #29b6f6, #039be5);
transform: translateY(-2px);
box-shadow: 0 4px 8px rgba(0,0,0,0.2);
}

.AI-index .history-item{
width: 280px;
margin: 0 10px 15px 10px;
font-size: 16px;
overflow: hidden;
padding: 10px;
border-radius: 8px;
background-color: rgba(255, 248, 232, 0.7); box-shadow: 0 2px 4px rgba(58, 95, 11, 0.1); /* 增加阴影区分历史项 */
transition: all 0.3s ease;
box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.AI-index .history-content{
padding-bottom: 15px;
overflow-y: scroll;
&::-webkit-scrollbar {
  display: none;
}
}

.AI-index .history-item:hover{
background-color: rgba(153, 233, 233, 0.7);
cursor: pointer;
transform: translateY(-2px);
box-shadow: 0 4px 8px rgba(0,0,0,0.1);
}

.voice-visualizer {
display: flex;
align-items: center;
gap: 6px;
margin-left: 10px;
position: absolute;
left: 650px;
bottom: 120px;
}

.voice-bar {
width: 6px;
height: 25px;
background: linear-gradient(180deg, #2196F3 0%, #64B5F6 100%); /* 蓝色渐变背景 */
border-radius: 3px;
animation: voice-wave 1.2s ease-in-out infinite;
transform-origin: bottom;
box-shadow: 0 2px 4px rgba(33, 150, 243, 0.2); /* 添加柔和阴影 */
}

.voice-bar:nth-child(1) { animation-delay: 0.1s; }
.voice-bar:nth-child(2) { animation-delay: 0.2s; }
.voice-bar:nth-child(3) { animation-delay: 0.3s; }
.voice-bar:nth-child(4) { animation-delay: 0.4s; }
.voice-bar:nth-child(5) { animation-delay: 0.5s; }
.voice-bar:nth-child(6) { animation-delay: 0.6s; }
.voice-bar:nth-child(7) { animation-delay: 0.7s; }
.voice-bar:nth-child(8) { animation-delay: 0.8s; }
.voice-bar:nth-child(9) { animation-delay: 0.6s; }
.voice-bar:nth-child(10) { animation-delay: 0.7s; }
.voice-bar:nth-child(11) { animation-delay: 0.8s; }
.voice-bar:nth-child(12) { animation-delay: 0.2s; }
.voice-bar:nth-child(13) { animation-delay: 0.3s; }
.voice-bar:nth-child(14) { animation-delay: 0.4s; }

@keyframes voice-wave {
0%, 100% { height: 12px; transform: scaleY(0.8); }
50% { height: 30px; transform: scaleY(1.2); }
80% { height: 22px; transform: scaleY(1.0); }
}
</style>
