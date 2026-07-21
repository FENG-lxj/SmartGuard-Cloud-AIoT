<template>
  <div id="app">
    <!-- 主应用容器 -->
    <div class="app-container">
      <!-- 顶部导航栏 -->
      <header class="main-header" v-if="$route.path !== '/login' && $route.path !== '/register'">
        <div class="header-content">
          <!-- 左侧：项目名称和Logo -->
          <div class="header-left" @click="setAlart()">
            <img src="./assets/logo.png" alt="智护云家" class="app-logo">
            <span class="app-title">智护云家</span>
          </div>
          
          <!-- 中间：时间显示 -->
          <div class="header-center">
            <HeaderTime inline />
          </div>
          
          <!-- 右侧：用户信息和操作按钮 -->
          <div class="header-right">
            <!-- 3D视图快速访问 -->
            <button class="quick-access-btn" @click="navigateToExternalSite">
              <i class="iconfont icon-3d"></i>
              <span>3D视图</span>
            </button>
            
            <!-- 用户信息 -->
            <div class="user-profile">
              <div class="user-avatar">
                <i class="iconfont icon-user"></i>
              </div>
              <span class="user-name">张三</span>
            </div>
          </div>
        </div>
        
        <!-- 消息提示框 -->
        <transition name="alert-fade">
          <div class="alert-container" v-show="alart">
            <messBox></messBox>
          </div>
        </transition>
      </header>
      
      <!-- 主要内容区域 -->
      <main class="content-area">
        <router-view/>
      </main>
    </div>
  </div>
</template>

<script>
import HeaderTime from '@/components/HeaderTime.vue'
import SockJS from 'sockjs-client'
import { Stomp } from '@stomp/stompjs'

export default {
  name: 'App',
  components: {
    messBox: () => import('@/components/messageBox.vue'),
    HeaderTime
  },
  data () {
    return {
      alart: false
    }
  },
  mounted () {
    this.initWebSocket()
  },
  methods: {
    navigateToExternalSite () {
      this.$router.push('/monitor')
    },
    setAlart () {
      this.alart = !this.alart
    },
    initWebSocket () {
      try {
        const socket = new SockJS('http://localhost:8023/CHome', null, { transport: ['websocket'] })
        const stompClient = Stomp.over(socket)

        stompClient.connect({}, () => {
          stompClient.subscribe('/topic/cameraAlarm', this.onSocketMessage)
        }, (error) => {
          console.error('STOMP连接失败:', error)
          this.$message.error('STOMP连接失败: ' + error.message)
        })

        socket.onclose = this.onSocketClose
        socket.onerror = this.onSocketError
      } catch (error) {
        console.error('WebSocket初始化失败:', error)
        this.$message.error('WebSocket初始化失败: ' + error.message)
      }
    },
    onSocketMessage (event) {
      console.log('警告消息:', event)
      this.setAlart()
    },
    onSocketClose (event) {
      console.log('WebSocket连接已关闭', event)
      this.$message.warning('WebSocket连接已关闭，代码: ' + event.code)
    },
    onSocketError (error) {
      console.log('WebSocket连接出错', error)
      this.$message.error('WebSocket连接出错: ' + error.type)
    }
  }
}
</script>

<style>
  /* 基础样式重置，确保页面占满整个浏览器窗口 */
  * {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
  }
  
  html, body {
    height: 100%;
    overflow: hidden;
    margin: 0;
    padding: 0;
  }
  
  #app {
    height: 100vh;
    overflow: hidden;
    background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
    transition: background 0.5s ease;
  }
  #app .Big1{    padding: 5px;    background-color: rgba(255, 255, 255, 0.9);    height: 100%;    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);    border-radius: 8px;    /* height: 700px; */  }  li {    list-style: none;  }    /* 顶部样式统一 */    #app .Big1 .top1 {        height: 80px;        position: fixed;        top: 0;        left: 0;        right: 0;        z-index: 1000;        max-width: 100%;        overflow: hidden;    }    #app .Big1 {        padding-top: 80px;        position: relative;    }
    #app .top2{
        display: flex;
        justify-content: center;
        align-items: center;
        position: relative;
        height: 80px;
    }
    /* 顶部左侧 */
    #app .top1 .left{
        font-size: 24px;
        font-weight: bold;
        color: #ffffff;
        text-shadow: 1px 1px 3px rgba(26, 58, 143, 0.2);
        display: flex;
        align-items: center;
    }

    #app .top1 img{
      width: 40px;
      height: 40px;
    }

    /* 顶部右侧的按钮，后期换图标 */
    #app .top1 .right{
        display: flex;
        align-items: center;
    }
    #app .top1 .right .shouye,.tuichu{
        background: linear-gradient(135deg, #4a6bef 0%, #6b8bff 100%);
        width: 100px;
        height: 40px;
        border-radius: 20px;
        font-size: 16px;
        color: #fff;
        margin: 10px;
        transition: all 0.3s ease;
        box-shadow: 0 4px 12px rgba(74, 107, 239, 0.25);
        letter-spacing: 1px;
        border: none;
        cursor: pointer;
    }
    #app .top1 .right .tuichu{
      background: linear-gradient(135deg, #3a56c7 0%, #5a7bff 100%);
        width: 100px;
        height: 40px;
        border-radius: 20px;
        font-size: 16px;
        color: #fff;
        margin: 10px;
        transition: all 0.3s ease;
        box-shadow: 0 4px 12px rgba(58, 86, 199, 0.2);
        letter-spacing: 1px;
        border: none;
        cursor: pointer;
    }
    #app .top1 .right .shouye:hover,.tuichu:hover {
        background: linear-gradient(135deg, #3a56d7 0%, #5a7bff 100%);
        transform: translateY(-2px);
        box-shadow: 0 6px 16px rgba(74, 107, 239, 0.35);
        transition: all 0.3s ease;
    }
    button {
        border: none;
    }
    .van-tabbar {
      display: flex;
      justify-content: space-around;
      height: 100px;
    }
    .van-tabbar-item {
      cursor: pointer;
    }
    .mongolian-header {
  width: 100%;
  height: 64px;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(6px);
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  box-sizing: border-box;
  border-bottom: 1px solid rgba(30, 92, 168, 0.15);
  box-shadow: 0 8px 24px rgba(31, 38, 135, 0.08);
}
.mongolian-header .header-time {
  margin: 0 12px;
}
/* 时间显示样式 */
.time-display {
color: #1a3a6e;
font-size: 16px;
font-weight: 500;
text-shadow: 1px 1px 1px rgba(255,255,255,0.5);
text-align: center;
flex: 1;
}

/* 用户信息显示样式 */
.user-info-display {
color: #1e5ca8;
font-size: 14px;
font-weight: 500;
display: flex;
align-items: center;
gap: 10px;
text-shadow: none;
}

.user-icon {
width: 30px;
height: 30px;
border-radius: 50%;
background-color: rgba(26, 58, 110, 0.1);
display: flex;
align-items: center;
justify-content: center;
font-size: 18px;
}

.user-name {
white-space: nowrap;
}

#app .mess{
  position: absolute;
  left: 36%;
  top: 120%;
}
    /* 3D视图快速访问按钮样式 */
    .quick-access {
      display: flex;
      align-items: center;
    }
    
    .3d-view-button {
      display: flex;
      align-items: center;
      padding: 8px 16px;
      background: linear-gradient(135deg, #3a56c7 0%, #5a7bff 100%);
      color: white;
      border: none;
      border-radius: 20px;
      cursor: pointer;
      transition: all 0.3s ease;
      box-shadow: 0 4px 8px rgba(58, 86, 199, 0.2);
    }
    
    .3d-view-button:hover {
      transform: translateY(-2px);
      box-shadow: 0 6px 12px rgba(58, 86, 199, 0.3);
    }
    
    .3d-view-button i {
      margin-right: 6px;
      font-size: 16px;
    }
    
    .3d-view-button span {
      font-size: 14px;
      font-weight: 500;
    }
/* 覆盖首页头部左侧标题颜色为黑色 */
.mongolian-header .left {
  color: #000 !important;
}

/* 美化主头部样式 */
.main-header {
  background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
  backdrop-filter: blur(16px);
  border-bottom: 1px solid rgba(203, 213, 225, 0.5);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.07);
  position: sticky;
  top: 0;
  z-index: 1000;
  height: 76px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.main-header:hover {
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.1);
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  padding: 0 28px;
  max-width: 1440px;
  margin: 0 auto;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 14px;
  cursor: pointer;
  transition: transform 0.25s ease, opacity 0.25s ease;
}

.header-left:hover {
  transform: scale(1.03);
  opacity: 0.95;
}

.app-logo {
  width: 44px;
  height: 44px;
  border-radius: 10px;
  object-fit: cover;
  border: 2px solid rgba(229, 231, 235, 0.8);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.header-left:hover .app-logo {
  box-shadow: 0 6px 16px rgba(22, 93, 255, 0.2);
  border-color: rgba(22, 93, 255, 0.3);
}

.app-title {
  font-size: 22px;
  font-weight: 700;
  color: #1a3a6e;
  letter-spacing: -0.02em;
  text-shadow: 0 1px 3px rgba(255, 255, 255, 0.5);
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

.header-center {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 24px;
}

.quick-access-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: linear-gradient(135deg, #165DFF 0%, #0E2954 100%);
  color: white;
  border: none;
  border-radius: 16px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 5px 15px rgba(22, 93, 255, 0.35);
  position: relative;
  overflow: hidden;
}

.quick-access-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s ease;
}

.quick-access-btn:hover::before {
  left: 100%;
}

.quick-access-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(22, 93, 255, 0.45);
}

.quick-access-btn:active {
  transform: translateY(-1px);
  box-shadow: 0 4px 10px rgba(22, 93, 255, 0.4);
}

.quick-access-btn i {
  font-size: 18px;
}

.user-profile {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 16px;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  border-radius: 16px;
  border: 1px solid rgba(203, 213, 225, 0.7);
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.03);
}

.user-profile:hover {
  background: linear-gradient(135deg, #ffffff 0%, #f1f5f9 100%);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
  border-color: rgba(22, 93, 255, 0.3);
}

.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: linear-gradient(135deg, #165DFF 0%, #0E2954 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
  box-shadow: 0 3px 8px rgba(22, 93, 255, 0.35);
  position: relative;
}

.user-avatar::after {
  content: '';
  position: absolute;
  bottom: 1px;
  right: 1px;
  width: 10px;
  height: 10px;
  background-color: #10B981;
  border-radius: 50%;
  border: 2px solid white;
}

.user-name {
  font-size: 15px;
  font-weight: 600;
  color: #1a3a6e;
  white-space: nowrap;
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

/* 内容区域样式优化 */
.content-area {
  min-height: calc(100vh - 76px);
  padding: 24px;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  transition: background 0.5s ease;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .main-header {
    height: 68px;
  }
  
  .header-content {
    padding: 0 16px;
  }
  
  .app-title {
    font-size: 18px;
  }
  
  .header-right {
    gap: 16px;
  }
  
  .quick-access-btn {
    padding: 8px 16px;
    font-size: 13px;
  }
  
  .user-profile {
    padding: 8px 12px;
  }
  
  .user-name {
    display: none;
  }
  
  .content-area {
    padding: 16px;
  }
}
</style>
