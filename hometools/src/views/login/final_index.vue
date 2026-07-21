<template>
  <div class="login-container">
    <!-- 左侧智能家居展示区 -->
    <div class="left-section">
      <!-- 品牌标识 -->
      <div class="brand">
        <div class="logo">安</div>
        <div>
          <h2>安智棠</h2>
          <p>智能养老管理系统</p>
        </div>
      </div>

      <!-- 智能家居3D展示 -->
      <div class="home-display">
        <div class="home-frame">
          <div class="home-bg">
            <div class="roof"></div>
            <div class="chimney"></div>
            <div class="door"></div>
            <div class="window window-left"></div>
            <div class="window window-right"></div>
            <div class="wall-decoration"></div>
          </div>
        </div>
      </div>

      <!-- 智能设备控制面板 -->
      <div class="device-panel">
        <div class="device-item">
          <div class="device-icon">💡</div>
          <p>照明</p>
        </div>
        <div class="device-item">
          <div class="device-icon">🔒</div>
          <p>安防</p>
        </div>
        <div class="device-item">
          <div class="device-icon">🌡️</div>
          <p>温控</p>
        </div>
        <div class="device-item">
          <div class="device-icon">❤️</div>
          <p>健康</p>
        </div>
      </div>

      <!-- 简洁的功能说明 -->
      <div class="feature-desc">
        <h3>智能联动 · 贴心守护</h3>
        <p>一键控制全屋设备，让科技温暖每个家庭</p>
      </div>
    </div>

    <!-- 右侧登录表单 -->
    <div class="right-section">
      <!-- 标题和标志 -->
      <div class="form-header">
        <div class="form-logo">
          <div class="logo">安</div>
          <div>
            <h1>安智棠管理系统</h1>
            <p>智能养老 · 贴心守护</p>
          </div>
        </div>
      </div>

      <!-- 登录表单 -->
      <form class="login-form" @submit.prevent="handleLogin">
        <!-- 用户名输入 -->
        <div class="form-group" :class="{ 'has-error': usernameError }">
          <label for="username" class="form-label">用户名</label>
          <div class="input-wrapper">
            <div class="input-icon">👤</div>
            <input
              type="text"
              id="username"
              placeholder="请输入用户名"
              v-model="username"
              @input="validateUsername"
              @focus="focusInput('username')"
              @blur="blurInput('username')"
              :class="{ 'is-focus': focusState.username }"
              class="form-input"
              autocomplete="username"
            >
          </div>
          <div v-if="usernameError" class="error-message">{{ usernameError }}</div>
          <div v-else-if="focusState.username && username.length >= 4 && username.length <= 12" class="success-message">✓ 用户名格式正确</div>
        </div>

        <!-- 密码输入 -->
        <div class="form-group" :class="{ 'has-error': passwordError }">
          <label for="password" class="form-label">密码</label>
          <div class="input-wrapper">
            <div class="input-icon">🔒</div>
            <input
              type="password"
              id="password"
              placeholder="请输入密码"
              v-model="password"
              @input="validatePassword"
              @focus="focusInput('password')"
              @blur="blurInput('password')"
              :class="{ 'is-focus': focusState.password }"
              class="form-input"
              autocomplete="current-password"
            >
            <button type="button" class="toggle-password" @click="togglePasswordVisibility">
              {{ showPassword ? '👁️' : '👁️‍🗨️' }}
            </button>
          </div>
          <div v-if="passwordError" class="error-message">{{ passwordError }}</div>
          <div v-else-if="focusState.password && password.length >= 6 && password.length <= 18" class="success-message">✓ 密码格式正确</div>
        </div>

        <!-- 验证码输入 -->
        <div class="form-group" :class="{ 'has-error': captchaError }">
          <label for="captcha" class="form-label">验证码</label>
          <div class="captcha-wrapper">
            <div class="input-wrapper">
              <div class="input-icon">🔤</div>
              <input
                type="text"
                id="captcha"
                placeholder="请输入验证码"
                v-model="Code"
                @input="validateCaptcha"
                @focus="focusInput('captcha')"
                @blur="blurInput('captcha')"
                :class="{ 'is-focus': focusState.captcha }"
                class="form-input captcha-input"
                maxlength="4"
                autocomplete="off"
              >
            </div>
            <div class="captcha-image">
              <img 
                :src="picUrl" 
                alt="验证码" 
                class="captcha-img" 
                @click="refreshCaptcha"
                title="点击刷新验证码"
              >
              <button 
                type="button" 
                class="refresh-captcha"
                @click="refreshCaptcha"
                title="刷新验证码"
              >
                🔄
              </button>
            </div>
          </div>
          <div v-if="captchaError" class="error-message">{{ captchaError }}</div>
        </div>

        <!-- 记住我和忘记密码 -->
        <div class="remember-section">
          <div class="remember-me">
            <input
              type="checkbox"
              id="remember-me"
              v-model="rememberMe"
              class="custom-checkbox"
            >
            <label for="remember-me" class="checkbox-label">记住我</label>
          </div>
          <a href="#" class="forgot-password" @click.prevent="showForgotPassword">忘记密码？</a>
        </div>

        <!-- 登录按钮 -->
        <button 
          type="submit" 
          class="login-btn"
          :disabled="isSubmitting"
        >
          <span v-if="isSubmitting" class="loading-spinner">⏳</span>
          <span v-else>登录系统</span>
        </button>
        
        <!-- 全局错误提示 -->
        <div v-if="globalError" class="global-error-message">
          {{ globalError }}
          <button type="button" class="close-error" @click="clearGlobalError">×</button>
        </div>
      </form>

      <!-- 快捷登录方式 -->
      <div class="quick-login">
        <p>快捷登录方式</p>
        <div class="social-icons">
          <button class="social-icon wechat">💚</button>
          <button class="social-icon qq">💙</button>
        </div>
      </div>

      <!-- 帮助信息 -->
      <div class="help-info">
        <p>需要帮助？请联系客服 <span class="service-number">400-888-6666</span></p>
      </div>

      <!-- 测试异常处理器 -->
      <div class="test-error-handler">
        <button @click="navigateToErrorTest" class="test-btn">测试异常处理器</button>
      </div>

      <!-- 底部版权信息 -->
      <div class="copyright">
        <p>© 2024 安智棠科技有限公司. 用心守护每一个家庭</p>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import { useMainStore } from '@/store'
import { codeLogin } from '@/api/login'
import { showError, showSuccess } from '@/utils/errorToast.js'
import { v4 as uuidv4 } from 'uuid';
import request from '@/utils/request';

export default {
  setup() {
    // 响应式数据
    const username = ref('');
    const password = ref('');
    const Code = ref('');
    const id = ref('');
    const picUrl = ref('');
    const router = useRouter();
    
    // 表单交互状态
    const focusState = ref({ username: false, password: false, captcha: false });
    const usernameError = ref('');
    const passwordError = ref('');
    const captchaError = ref('');
    const globalError = ref('');
    const showPassword = ref(false);
    const isSubmitting = ref(false);
    const rememberMe = ref(false);

    // 自适应3D小屋大小
    // 使用ref替代document.querySelector以避免DOM访问错误
    function getScaleFactor() {
      if (!homeDisplay.value) return 1;
      const containerWidth = homeDisplay.value.clientWidth || 400;
      const containerHeight = homeDisplay.value.clientHeight || 220;
      return Math.min(containerWidth/400, containerHeight/220, 1);
    }

    // 使用ref替代document.querySelector以避免DOM访问错误
    function adjustHomeSize() {
      if (!homeBg.value || !homeDisplay.value) return;
      const scale = getScaleFactor();
      homeBg.value.style.transform = `translate(-50%, -50%) rotateX(5deg) scale(${scale})`;
    }

    // 生成UUID和获取验证码
    function refreshCaptcha() {
      try {
        id.value = uuidv4().substr(0, 8);

        request({
          url: '/user/captcha',
          method: 'get',
          responseType: 'blob', // 告诉axios期望接收二进制数据
          data: {
            uuid: id.value
          }
        }).then(response => {
          if (!response || !response.data) {

            showError('获取验证码失败：响应数据为空');
            return;
          }

          if (response.data instanceof Blob) {

            const reader = new FileReader();
            reader.readAsDataURL(response.data);
            reader.onload = () => {
              picUrl.value = reader.result;

            };
            reader.onerror = (error) => {
              showError('验证码加载失败');

            };
          } else {

            showError('获取验证码失败：数据格式不正确');
          }
        }).catch(error => {
          const errorMsg = error?.message || '未知错误';
          showError('获取验证码失败: ' + errorMsg);
          
        });
      } catch (error) {
        showError('获取验证码失败');
        
      }
    }

    // 表单验证
    function validForm() {
      if (!username.value || !password.value) {
        showError('用户名和密码不能为空');
        return false;
      }
      if (username.value.length < 4 || username.value.length > 12) {
        showError('用户名为4-12位');
        return false;
      }
      if (password.value.length < 6 || password.value.length > 18) {
        showError('密码为6-18位');
        return false;
      }
      if (!/^\w{4}$/.test(Code.value)) {
        showError('图形验证码不正确');
        return false;
      }
      return true;
    }

    // 导航到异常测试页面
    function navigateToErrorTest() {
      router.push('/error-test')
    }

    // 登录处理
    async function handleLogin() {
      if (!validForm()) {
        return;
      }

      try {
        // 使用已有的验证码UUID
        const res = await codeLogin(username.value, password.value, Code.value, id.value);

        if (res && res.data.code === 200) {
          // 打印响应数据结构，用于调试
          console.log('登录成功响应数据:', res.data);
          
          // 存储用户信息到localStorage
          localStorage.setItem('userInfo', JSON.stringify(res.data));
          
          // 使用Pinia设置token和用户信息
          const mainStore = useMainStore();
          
          // 打印设置前的store状态
          console.log('设置前的store状态:', {
            guest: { ...mainStore.guest }
          });
          
          // 使用setGuest方法设置用户信息
          mainStore.setGuest({ 
            username: res.data.username || '用户', 
            token: res.data.token 
          });
          
          // 打印设置后的store状态
          console.log('设置后的store状态:', {
            guest: { ...mainStore.guest }
          });
          
          showSuccess('登录成功');
          // 调试阶段：禁用跳转，仅显示成功提示
        } else {
          // 登录失败处理
          const errorMsg = res?.msg || '登录失败，请检查用户名和密码';
          globalError.value = errorMsg;
          
          // 如果是验证码错误，自动刷新验证码
          if (errorMsg.includes('验证码')) {
            refreshCaptcha();
          }
        }
      } catch (error) {
        console.error('登录失败:', error);
        globalError.value = '登录失败，请稍后重试';
      } finally {
        isSubmitting.value = false;
      }
    }

    // 初始化ref
    const homeDisplay = ref(null);
    const homeBg = ref(null);
    
    // 初始化 - 检查是否记住了用户
    function initForm() {
      const remember = localStorage.getItem('rememberMe');
      const lastUsername = localStorage.getItem('lastUsername');
      
      if (remember === 'true' && lastUsername) {
        username.value = lastUsername;
        rememberMe.value = true;
      }
    }

    // 初始化
      onMounted(() => {
        // 为ref赋值
        homeDisplay.value = document.querySelector('.home-display');
        homeBg.value = document.querySelector('.home-bg');

        refreshCaptcha();
        adjustHomeSize();
        window.addEventListener('resize', adjustHomeSize);
        
        // 初始化表单
        initForm();

        // 3D小屋交互效果
        if (homeBg.value) {
          const mouseEnterHandler = function() {
            this.style.transform = `translate(-50%, -50%) rotateX(0deg) rotateY(5deg) scale(${getScaleFactor()})`;
            this.style.boxShadow = '0 15px 30px rgba(0, 0, 0, 0.25)';
          };

          const mouseLeaveHandler = function() {
            this.style.transform = `translate(-50%, -50%) rotateX(5deg) scale(${getScaleFactor()})`;
            this.style.boxShadow = '0 10px 25px rgba(0, 0, 0, 0.2)';
          };

          homeBg.value.addEventListener('mouseenter', mouseEnterHandler);
          homeBg.value.addEventListener('mouseleave', mouseLeaveHandler);

          // 保存事件处理器以便后续移除
          homeBg.value._mouseEnterHandler = mouseEnterHandler;
          homeBg.value._mouseLeaveHandler = mouseLeaveHandler;
        }
      });

      // 清理
      onUnmounted(() => {
        window.removeEventListener('resize', adjustHomeSize);

        if (homeBg.value) {
          homeBg.value.removeEventListener('mouseenter', homeBg.value._mouseEnterHandler);
          homeBg.value.removeEventListener('mouseleave', homeBg.value._mouseLeaveHandler);
        }
      });

    return {
      username,
      password,
      Code,
      id,
      picUrl,
      refreshCaptcha,
      handleLogin,
      focusState,
      usernameError,
      passwordError,
      captchaError,
      globalError,
      showPassword,
      isSubmitting,
      rememberMe,
      validateUsername,
      validatePassword,
      validateCaptcha,
      focusInput,
      blurInput,
      togglePasswordVisibility,
      showForgotPassword,
      clearGlobalError
    };
  }
};
</script>

<style scoped>
/* 基础样式设置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  height: 100vh;
  overflow: hidden;
  margin: 0;
  padding: 0;
  background: linear-gradient(135deg, #f0f4f8 0%, #d9e2ec 100%);
}
.login-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #f0f4f8 0%, #d9e2ec 100%);
  overflow: hidden !important;
  overflow-x: hidden !important;
  overflow-y: hidden !important;
  border-radius: 24px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
  transition: all 0.5s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  width: 100%;
  height: 100vh;
  max-height: 100vh;
  margin: 0;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  transform: translateY(0);
  scrollbar-width: none !important;
  -ms-overflow-style: none !important;
}

.login-container::-webkit-scrollbar {
  display: none !important;
}

/* 额外添加全局滚动控制 */
html, body {
  overflow: hidden !important;
  height: 100vh !important;
  margin: 0 !important;
  padding: 0 !important;
  position: fixed !important;
  width: 100% !important;
  top: 0 !important;
  left: 0 !important;
}

/* 添加更多浏览器特定规则 */
/* IE 10+ */
@media screen and (-ms-high-contrast: active), (-ms-high-contrast: none) {
  .login-container, html, body {
    -ms-overflow-style: none !important;
    overflow: hidden !important;
  }
}

/* Edge */
@supports (-ms-ime-align: auto) {
  .login-container, html, body {
    scrollbar-width: none !important;
    overflow: hidden !important;
  }
}

/* Firefox */
@-moz-document url-prefix() {
  .login-container, html, body {
    scrollbar-width: none !important;
    overflow: hidden !important;
  }
}

/* Chrome, Safari, Opera */
@media not all and (min-resolution:.001dpcm) {
  @supports (-webkit-appearance: none) {
    .login-container, html, body {
      -webkit-overflow-scrolling: touch !important;
      overflow: hidden !important;
    }
  }
}

/* 平板和桌面设备样式 */
@media (min-width: 768px) {
  .login-container {
    flex-direction: row;
    height: 100vh;
  }

  .left-section {
    display: flex;
    width: 55%;
  }

  .right-section {
    width: 45%;
    border-radius: 0 24px 24px 0;
  }
}

/* 移动设备样式 */
@media (max-width: 767px) {
  .login-container {
    margin: 8px;
    width: calc(100% - 16px);
    border-radius: 16px;
  }

  .right-section {
    height: auto;
    min-height: 100%;
    padding: 24px;
    border-radius: 16px;
  }

  .form-logo h1 {
    font-size: 28px;
  }

  .input-wrapper input {
    padding: 14px 16px 14px 44px;
    border-radius: 12px;
  }

  .login-btn {
    padding: 12px !important;
  }
}

/* 小屏幕手机优化 */
@media (max-width: 480px) {
  .right-section {
    padding: 16px;
  }

  .form-logo h1 {
    font-size: 24px;
  }

  .form-group label {
    font-size: 12px;
  }

  .remember-section {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
}

/* 左侧智能家居展示区 */
.left-section {
  display: none;
  width: 55%;
  height: 100%;
  min-height: 100%;
  background: linear-gradient(135deg, #165DFF 0%, #0E2954 100%);
  border-radius: 24px 0 0 24px;
  box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.15);
  padding: 48px;
  color: white;
  flex-direction: column;
  justify-content: space-between;
  position: relative;
  overflow: hidden;
  transition: all 0.5s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.left-section::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle at center, rgba(255, 255, 255, 0.1) 0%, rgba(255, 255, 255, 0) 70%);
  transform: rotate(45deg);
  animation: gradientFlow 15s linear infinite;
}

@keyframes gradientFlow {
  0% {
    transform: rotate(45deg) translateX(-100%) translateY(-100%);
  }
  100% {
    transform: rotate(45deg) translateX(100%) translateY(100%);
  }
}

/* 品牌标识 */
.brand {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo {
  width: 40px;
  height: 40px;
  background: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #1e40af;
  font-weight: bold;
  font-size: 18px;
}

.brand h2 {
  font-size: 24px;
  font-weight: bold;
  margin: 0;
}

.brand p {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
  margin: 0;
}

/* 智能家居平面展示 */
.home-display {
  position: relative;
  width: 100%;
  height: 300px;
  margin: 40px 0;
  perspective: 1000px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.home-bg {
  position: relative;
  width: 280px;
  height: 220px;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.5) 0%, rgba(30, 64, 175, 0.6) 100%);
  border-radius: 8px;
  border: 2px solid rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(8px);
  transform-style: preserve-3d;
  transform: rotateX(5deg);
  transition: all 0.5s ease;
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.25);
  perspective: 1000px;
  overflow: hidden;
  animation: subtleFloat 6s ease-in-out infinite;
  margin: 0 auto;
}

/* 房子框架 */
.home-frame {
  position: relative;
  width: 340px;
  height: 260px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 16px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.05) 0%, rgba(255, 255, 255, 0.1) 100%);
  backdrop-filter: blur(5px);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
  transform-style: preserve-3d;
  transform: rotateX(3deg);
  overflow: hidden;
}

/* 框架装饰 */
.home-frame::before {
  content: '';
  position: absolute;
  top: 10px;
  left: 10px;
  right: 10px;
  bottom: 10px;
  border: 1px dashed rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  pointer-events: none;
}

/* 额外窗户 */
.window {
  position: absolute;
  background: radial-gradient(circle, rgba(250, 204, 21, 0.3) 0%, rgba(250, 204, 21, 0.1) 70%);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 4px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
  transform-style: preserve-3d;
}

.window-left {
  top: 30%;
  left: 10%;
  width: 20%;
  height: 25%;
  transform: translateZ(15px);
  /* 窗户格子 */
  background-image: linear-gradient(to right, rgba(255, 255, 255, 0.3) 1px, transparent 1px),
                    linear-gradient(to bottom, rgba(255, 255, 255, 0.3) 1px, transparent 1px);
  background-size: 15px 15px;
}

.window-right {
  top: 30%;
  right: 10%;
  width: 20%;
  height: 25%;
  transform: translateZ(15px);
  /* 窗户格子 */
  background-image: linear-gradient(to right, rgba(255, 255, 255, 0.3) 1px, transparent 1px),
                    linear-gradient(to bottom, rgba(255, 255, 255, 0.3) 1px, transparent 1px);
  background-size: 15px 15px;
}

/* 墙壁装饰 */
.wall-decoration {
  position: absolute;
  bottom: 10%;
  left: 50%;
  transform: translateX(-50%) translateZ(5px);
  width: 60%;
  height: 5px;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  border-radius: 2.5px;
}

@keyframes subtleFloat {
  0% { transform: rotateX(5deg) translateY(0); }
  50% { transform: rotateX(5deg) translateY(-10px); }
  100% { transform: rotateX(5deg) translateY(0); }
}

/* 3D小屋组件 */
.home-bg::before {
  content: '';
  position: absolute;
  top: 10%;
  left: 30%;
  width: 40%;
  height: 15%;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.3) 0%, rgba(255, 255, 255, 0.1) 100%);
  border-radius: 8px;
  transform: translateZ(20px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.3);
  /* 窗户格子 */
  background-image: linear-gradient(to right, rgba(255, 255, 255, 0.3) 1px, transparent 1px),
                    linear-gradient(to bottom, rgba(255, 255, 255, 0.3) 1px, transparent 1px);
  background-size: 20px 20px;
}

/* 窗户效果 */
.home-bg::after {
  content: '';
  position: absolute;
  bottom: 20%;
  left: 15%;
  width: 25%;
  height: 30%;
  background: radial-gradient(circle, rgba(250, 204, 21, 0.4) 0%, rgba(250, 204, 21, 0.15) 70%);
  border-radius: 4px;
  transform: translateZ(10px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.2);
  animation: warmGlow 3s ease-in-out infinite;
}

@keyframes warmGlow {
  0% { box-shadow: 0 0 15px rgba(250, 204, 21, 0.3); }
  50% { box-shadow: 0 0 25px rgba(250, 204, 21, 0.5); }
  100% { box-shadow: 0 0 15px rgba(250, 204, 21, 0.3); }
}

/* 门效果 */
.home-bg .door {
  position: absolute;
  bottom: 0;
  right: 25%;
  width: 20%;
  height: 40%;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.2) 0%, rgba(255, 255, 255, 0.1) 100%);
  border-radius: 4px 4px 0 0;
  transform: translateZ(5px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

/* 门把手 */
.home-bg .door::after {
  content: '';
  position: absolute;
  top: 50%;
  right: 15%;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-50%);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

/* 烟囱效果 */
.home-bg .chimney {
  position: absolute;
  top: -15%;
  right: 20%;
  width: 10%;
  height: 30%;
  background: linear-gradient(to bottom, rgba(30, 64, 175, 0.6) 0%, rgba(30, 64, 175, 0.4) 100%);
  border-radius: 4px 4px 0 0;
  transform: translateZ(30px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.25);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

/* 烟囱顶部 */
.home-bg .chimney::after {
  content: '';
  position: absolute;
  top: 0;
  left: -10%;
  width: 120%;
  height: 15%;
  background: rgba(30, 64, 175, 0.7);
  border-radius: 4px;
  box-shadow: 0 -2px 4px rgba(0, 0, 0, 0.2);
}

/* 屋顶效果 */
.home-bg .roof {
  position: absolute;
  top: -12%;
  left: 0;
  width: 100%;
  height: 35%;
  background: linear-gradient(135deg, rgba(220, 38, 38, 0.4) 0%, rgba(153, 27, 27, 0.5) 100%);
  clip-path: polygon(0 100%, 50% 0, 100% 100%);
  transform: translateZ(40px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.25);
  border: 1px solid rgba(255, 255, 255, 0.1);
  /* 屋顶纹理 */
  background-image: linear-gradient(to right, transparent 0%, rgba(255, 255, 255, 0.1) 50%, transparent 100%);
  background-size: 40px 100%;
}

/* 屋顶瓦片效果 */
.home-bg .roof::before {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 60%;
  background-image: linear-gradient(to top, rgba(0, 0, 0, 0.1) 1px, transparent 1px);
  background-size: 100% 10px;
}

.home-bg:hover {
  transform: rotateX(0deg) scale(1.02);
}

.room-1 {
  position: absolute;
  top: 25%;
  left: 25%;
  width: 96px;
  height: 96px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  transition: all 0.5s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  animation: pulse 3s infinite alternate;
}

.room-inner {
  width: 64px;
  height: 64px;
  background: rgba(59, 130, 246, 0.3);
  border-radius: 8px;
  position: relative;
  overflow: hidden;
}

.room-inner::after {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(45deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transform: rotate(45deg);
  animation: shine 3s infinite;
}

.room-2 {
  position: absolute;
  top: 25%;
  right: 25%;
  width: 64px;
  height: 64px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 16px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  transition: all 0.5s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  animation: pulse 3s infinite alternate 0.5s;
}

.room-3 {
  position: absolute;
  bottom: 25%;
  left: 33%;
  width: 80px;
  height: 64px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 16px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  transition: all 0.5s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  animation: pulse 3s infinite alternate 1s;
}

.room-4 {
  position: absolute;
  bottom: 25%;
  right: 33%;
  width: 64px;
  height: 80px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 16px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  transition: all 0.5s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  animation: pulse 3s infinite alternate 1.5s;
}

.room-1:hover, .room-2:hover, .room-3:hover, .room-4:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-5px);
}

@keyframes pulse {
  0% {
    transform: scale(1);
    opacity: 0.8;
  }
  100% {
    transform: scale(1.05);
    opacity: 1;
  }
}

@keyframes shine {
  0% {
    transform: translateX(-100%) rotate(45deg);
  }
  100% {
    transform: translateX(100%) rotate(45deg);
  }
}

/* 智能设备控制面板 */
.device-panel {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 40px;
  perspective: 1000px;
}

.device-item {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  padding: 16px;
  text-align: center;
  transition: all 0.5s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  transform-style: preserve-3d;
  transform: translateY(0);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  cursor: pointer;
}

.device-item:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-8px) rotateX(5deg);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.device-item:active {
  transform: translateY(-4px) rotateX(3deg);
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.device-icon {
  width: 50px;
  height: 50px;
  background: rgba(250, 204, 21, 0.2);
  border-radius: 50%;
  margin: 0 auto 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  color: rgba(250, 204, 21, 0.8);
  transition: all 0.3s ease;
  transform: translateZ(10px);
}

.device-item:hover .device-icon {
  transform: scale(1.1) translateZ(15px);
}

.device-item:nth-child(2) .device-icon {
  background: rgba(74, 222, 128, 0.2);
  color: rgba(74, 222, 128, 0.8);
}

.device-item:nth-child(3) .device-icon {
  background: rgba(249, 115, 22, 0.2);
  color: rgba(249, 115, 22, 0.8);
}

.device-item:nth-child(4) .device-icon {
  background: rgba(219, 39, 119, 0.2);
  color: rgba(219, 39, 119, 0.8);
}

.device-item p {
  margin: 0;
  font-size: 16px;
  font-weight: 500;
  transform: translateZ(5px);
}

/* 功能说明 */
.feature-desc {
  text-align: center;
  color: rgba(255, 255, 255, 0.9);
  margin-top: -20px;
}

.feature-desc h3 {
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 8px;
}

.feature-desc p {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
  margin: 0;
}

/* 右侧登录表单 */
.right-section {
  width: 100%;
  height: 100%;
  min-height: 100%;
  background: white;
  border-radius: 24px;
  box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.15);
  padding: 48px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  position: relative;
  overflow: hidden;
  transition: all 0.5s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.right-section::before {
  content: '';
  position: absolute;
  top: 0;
  right: 0;
  width: 200px;
  height: 200px;
  background: linear-gradient(135deg, rgba(30, 64, 175, 0.05) 0%, rgba(67, 56, 202, 0.05) 100%);
  border-radius: 50%;
  transform: translate(50%, -50%);
  z-index: 0;
}

/* 表单标题和标志 */
.form-header {
  text-align: center;
  margin-bottom: 60px;
  position: relative;
  z-index: 1;
}

.form-logo {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 16px;
}

.form-logo .logo {
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, #1e40af 0%, #4338ca 100%);
  color: white;
  font-size: 24px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.form-logo .logo:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
}

.form-logo h1 {
  font-size: 32px;
  font-weight: 700;
  margin: 0;
  color: #1f2937;
  background: linear-gradient(135deg, #165DFF 0%, #0E2954 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: -0.5px;
}

.form-logo p {
  font-size: 14px;
  color: #64748B;
  margin: 0;
  font-weight: 500;
}

/* 登录表单样式 */
.login-form {
  display: flex;
  flex-direction: column;
  gap: 24px;
  position: relative;
  z-index: 1;
}

/* 表单组样式 */
.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
  position: relative;
}

.form-group.has-error label {
  color: #ef4444;
}

.form-group label {
  font-size: 14px;
  font-weight: 500;
  color: #4b5563;
  transition: all 0.3s ease;
}

.input-wrapper {
  position: relative;
  border-radius: 8px;
  overflow: visible;
  z-index: 10;
  background: white;
  border: 2px solid #e2e8f0;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.02);
}

.form-group.has-error .input-wrapper {
  border-color: #ef4444;
  box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.1);
}

.error-message {
  margin-top: 6px;
  font-size: 13px;
  color: #ef4444;
  line-height: 1.4;
  display: flex;
  align-items: center;
}

.error-message::before {
  content: "⚠️";
  margin-right: 6px;
  font-size: 14px;
}

.global-error-message {
  margin-top: 16px;
  padding: 12px 16px;
  background: rgba(239, 68, 68, 0.1);
  border: 1px solid rgba(239, 68, 68, 0.3);
  border-radius: 8px;
  color: #ef4444;
  font-size: 14px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  animation: shake 0.5s ease-in-out;
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  10%, 30%, 50%, 70%, 90% { transform: translateX(-3px); }
  20%, 40%, 60%, 80% { transform: translateX(3px); }
}

.close-error {
  background: none;
  border: none;
  font-size: 20px;
  color: #ef4444;
  cursor: pointer;
  padding: 0;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.2s ease;
}

.close-error:hover {
  background: rgba(239, 68, 68, 0.2);
  transform: scale(1.1);
}

.input-wrapper input {
  width: 100%;
  padding: 16px 20px 16px 48px;
  border: none;
  outline: none;
  background: transparent;
  font-size: 16px;
  color: #1a3a6e;
  transition: all 0.3s ease;
}

.input-wrapper input::placeholder {
  color: #94a3b8;
}

.input-wrapper input.is-focus {
  color: #165DFF;
}

.input-wrapper:focus-within {
  border-color: #165DFF;
  box-shadow: 0 0 0 3px rgba(22, 93, 255, 0.15);
  transform: translateY(-1px);
}

.toggle-password {
  position: absolute;
  right: 16px;
  top: 50%;
  transform: translateY(-50%);
  padding: 10px;
  border: none;
  background: transparent;
  cursor: pointer;
  font-size: 18px;
  color: #94a3b8;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  z-index: 11;
}

.toggle-password:hover {
  color: #165DFF;
  background-color: rgba(22, 93, 255, 0.05);
}

/* 验证码样式 */
.captcha-wrapper {
  display: flex;
  gap: 12px;
  align-items: center;
}

.captcha-image {
  display: flex;
  align-items: center;
  gap: 8px;
}

.captcha-img {
  width: 120px;
  height: 48px;
  border-radius: 12px;
  object-fit: cover;
  cursor: pointer;
  border: 1px solid #E2E8F0;
}

.refresh-captcha {
  font-size: 18px;
  cursor: pointer;
  color: #64748B;
  transition: all 0.3s ease;
}

.refresh-captcha:hover {
  color: #165DFF;
  transform: rotate(180deg);
}

.input-icon {
  position: absolute;
  left: 16px;
  top: 50%;
  transform: translateY(-50%);
  color: #9ca3af;
  transition: all 0.3s ease;
  z-index: 11;
  font-size: 18px;
  width: 24px;
  text-align: center;
}

.input-wrapper input:focus + .input-icon {
  color: #1e40af;
  transform: translateY(-50%);
}

/* 记住我和忘记密码 */
.remember-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
  z-index: 1;
}

.remember-me {
  display: flex;
  align-items: center;
  gap: 8px;
}

.remember-me input {
  width: 16px;
  height: 16px;
  color: #1e40af;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  transition: all 0.3s ease;
  cursor: pointer;
}

.remember-me input:checked {
  background: #165DFF;
  border-color: #165DFF;
}

.remember-me label {
  font-size: 14px;
  color: #1f2937;
  margin: 0;
  cursor: pointer;
  transition: all 0.3s ease;
}

.remember-me label:hover {
  color: #1e40af;
}

.forgot-password {
  font-size: 14px;
  font-weight: 500;
  color: #1e40af;
  text-decoration: none;
  transition: all 0.3s ease;
  position: relative;
}

.forgot-password:after {
  content: '';
  position: absolute;
  width: 0;
  height: 2px;
  bottom: -2px;
  left: 0;
  background-color: #1e40af;
  transition: width 0.3s ease;
}

.forgot-password:hover {
  color: #1e3a8a;
}

.forgot-password:hover:after {
  width: 100%;
}

/* 登录按钮 */
.login-btn {
  width: 100%;
  padding: 16px;
  background: linear-gradient(135deg, #165DFF 0%, #0E2954 100%);
  color: white;
  border: none;
  border-radius: 16px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.5s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  position: relative;
  overflow: hidden;
  z-index: 1;
  box-shadow: 0 4px 15px rgba(22, 93, 255, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

.login-btn:before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: all 0.6s ease;
  z-index: 0;
}

.login-btn:hover:before {
  left: 100%;
}

.login-btn:hover:not(:disabled) {
  transform: translateY(-3px);
  box-shadow: 0 6px 15px rgba(30, 64, 175, 0.3);
}

.login-btn:active:not(:disabled) {
  transform: translateY(0);
  box-shadow: 0 3px 10px rgba(30, 64, 175, 0.2);
}

.login-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  transform: none;
}

.loading-spinner {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* 快捷登录方式 */
.quick-login {
  text-align: center;
  margin-top: 30px;
  position: relative;
  z-index: 1;
}

.quick-login p {
  font-size: 14px;
  color: #6b7280;
  margin: 0 0 16px;
  position: relative;
}

.quick-login p:before, .quick-login p:after {
  content: '';
  position: absolute;
  top: 50%;
  width: 30%;
  height: 1px;
  background: #e5e7eb;
}

.quick-login p:before {
  left: 0;
}

.quick-login p:after {
  right: 0;
}

.social-icons {
  display: flex;
  justify-content: center;
  gap: 20px;
}

.social-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.wechat {
  background: #e0f2fe;
  color: #0ea5e9;
}

.wechat:hover {
  background: #bae6fd;
  transform: translateY(-5px) scale(1.05);
  box-shadow: 0 6px 20px rgba(14, 165, 233, 0.2);
}

.qq {
  background: #dbeafe;
  color: #3b82f6;
}

.qq:hover {
  background: #bfdbfe;
  transform: translateY(-5px) scale(1.05);
  box-shadow: 0 6px 20px rgba(59, 130, 246, 0.2);
}

/* 帮助信息 */
.help-info {
  text-align: center;
  margin-top: 40px;
}

.help-info p {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
}

.service-number {
  font-weight: 500;
  color: #1e40af;
}

/* 底部版权信息 */
.copyright {
  text-align: center;
  margin-top: auto;
  padding-top: 30px;
}

.copyright p {
  font-size: 12px;
  color: #9ca3af;
  margin: 0;
}

/* 响应式调整 */
@media (min-width: 768px) {
  .login-container {
    flex-direction: row;
    align-items: center;
    justify-content: center;
    padding: 20px;
  }

  .left-section {
    display: flex;
  }

  .right-section {
    width: 50%;
    border-radius: 0 12px 12px 0;
  }
}

@media (max-width: 767px) {
  .login-container {
    padding: 10px;
  }

  .right-section {
    height: auto;
    min-height: 500px;
    margin-top: 20px;
  }
}

/* 响应式调整 */
@media (max-width: 768px) {
  .min-h-screen {
    flex-direction: column;
  }
  .h-[600px] {
    height: auto;
    min-height: 400px;
  }
  .rounded-l-xl, .rounded-r-xl {
    border-radius: 0;
  }
  .rounded-t-xl {
    border-top-left-radius: 0.75rem;
    border-top-right-radius: 0.75rem;
  }
}
</style>