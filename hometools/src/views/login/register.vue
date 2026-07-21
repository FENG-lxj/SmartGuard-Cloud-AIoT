<template>
    <div class="login-register">
      <div class="title">
        <div class="text">用户注册</div>

      </div>
      <div class="content">
        <span class="big">用户名注册</span>
        <div class="small" @click="goEnter">已注册？点我登录</div>
        <ul>
          <li><input type="text" placeholder="请输入用户名" v-model="phone"></li>
          <li>
            <input type="password" placeholder="请输入密码" v-model="msgCode">
          </li>
          <li>
            <!-- 判断用户名是否为空 -->
            <input type="text" placeholder="请输入图形验证码" @click="judegePhone()" v-model="Code">
            <!-- 点击修改验证码 -->
            <img :src="picUrl" alt="" class="verify" @click="sendUUID()">
          </li>

        </ul>
        <button class="entry" @click="login()">注册</button>
      </div>
      <div style="height: 200px; width: 200px;" src="192.168.139.194:8081">

      </div>
    </div>
</template>

<script>
import { codeRegister } from '@/api/login'
import { v4 as uuidv4 } from 'uuid'
import axios from 'axios'
import { showError } from '@/utils/errorToast.js'

export default {
  name: 'LoginIndex',
  data () {
    return {
      picUrl: '',
      phone: '',
      Code: '',
      msgCode: '',
      id: ''
    }
  },
  async created () {
    this.sendUUID()
  },
  methods: {
    goEnter () {
      this.$router.push('/login')
    },
    async sendUUID () {
      try {
        this.id = uuidv4().substr(0, 8)
        const res = await axios({
          url: 'http://47.120.65.85:8080/captcha/generate',
          method: 'post',
          responseType: 'blob',
          data: { uuid: this.id, type: 'register' }
        })
        return new Promise((resolve, reject) => {
          const reader = new FileReader()
          reader.readAsDataURL(res.data)
          reader.onload = () => {
            this.picUrl = reader.result
            resolve()
          }
          reader.onerror = (error) => {
            showError('图片加载失败')
            reject(error)
          }
        })
      } catch (error) {
        showError('获取验证码失败')
        console.error(error)
      }
    },
    judegePhone () {
      if (this.phone === '') {
        showError('用户名不能为空')
      }
    },
    validFn () {
      if (this.phone.length > 12 || this.phone.length < 4) {
        showError('用户名为4-12位')
        return false
      }
      if (!/^\w{4}$/.test(this.Code)) {
        showError('图形验证码不正确')
        return false
      }
      return true
    },
    async login () {
      if (!this.phone || !this.msgCode) {
        showError('用户名和密码不能为空')
        return
      }
      if (!this.validFn()) return
      if (!/^\d{6}$/.test(this.msgCode)) {
        showError('密码不正确')
        return
      }

      if (!/^[a-zA-Z0-9]{4,12}$/.test(this.phone)) {
        showError('用户名必须为4-12位字母或数字')
        return
      }
      if (!/^[a-zA-Z0-9]{6,18}$/.test(this.msgCode)) {
        showError('密码必须为6-18位字母或数字')
        return
      }

      try {
        const res = await codeRegister(this.phone, this.msgCode, this.Code, this.id)
        if (res && res.data?.code === 200 && res.data) {
          showError('注册成功')
          this.goEnter()
        } else {
          showError(res?.msg || '注册失败，请重试')
        }
      } catch (error) {
        console.error('注册失败:', error)
        let errorMessage = '注册失败'
        if (error?.response) {
          const { status, data } = error.response
          if (status === 400) errorMessage = data.message || '请求参数错误'
          else if (status === 409) errorMessage = '用户名已存在'
          else if (status >= 500) errorMessage = '服务器错误，请稍后再试'
        } else if (error?.request) {
          errorMessage = '网络错误，请检查网络连接'
        }
        showError(errorMessage)
      }
    }
  }
}
</script>

  <style>
    .login-register{
        height: 550px;
        width: 450px;
        border-radius: 16px;
        padding: 30px;
        margin: 30px auto;
        background: linear-gradient(135deg, #e0e8f9 0%, #a5b8da 100%);
        box-shadow: 0 8px 32px rgba(0,0,0,0.15);
    }
    .login-register .title{
      display: flex;
      justify-content: center;
      height: 35px;
      font-size: 18px;
      font-family: 宋体;
      border-bottom: 2px solid #f4eeee;
      padding-top: 5px;
    }
    .login-register .content{
      padding: 0 15px;
    }
    .login-index .countent input{
        line-height: 30px;
        height: 30px;
    }
    .login-register .content li input{
      width: 80%;
      height: 40px;
      border: 1px solid #ccc;
      border-radius: 8px;
      padding: 0 15px;
      margin: 10px 0;
      font-size: 16px;
      transition: all 0.3s ease;
    }
    .login-register .content .big{
      display: block;
      margin-top: 20px;
      margin-bottom: 5px;
      font-size: 30px;
    }
    .login-register .content .small{
      display: block;
      color: #999;
      margin-bottom: 30px;
    }
    .login-register .content .small:hover{
      color: rgb(80, 144, 255);
      cursor: pointer;
    }
    .login-register .content li{
      line-height: 55px;
      border-bottom: #eee 1px solid;
      color: #999;
      position: relative;
    }
    .login-register .content li input{
      width: 80%;
      height: 40px;
      border: 1px solid #e0e8f9;
      border-radius: 8px;
      padding: 0 15px;
      /* margin: 10px 0; */
      font-size: 16px;
      transition: all 0.3s ease;
    }

    .login-register .content li input:focus {
      border-color: rgb(80, 144, 255);
      box-shadow: 0 0 5px rgba(80, 144, 255, 0.2);
    }
    .login-register .content li .verify{
      width: 30%;
      height: 40px;
      vertical-align: middle;
      position: absolute;
      right: 10px;
      top: 10px;
    }
    .login-register .content li button{
      height: 40px;
      background-color: #fff;
      border: 0;
      position: absolute;
      right: 10px;
      color: coral;
      margin-top: 8px;
    }
    .login-register .content .entry{
      background: linear-gradient(135deg, #4a8bf5 0%, #1e5fd9 100%);
      /* border: 0px;
      color: #fff;
      height: 44px;
      width: 100%;
      margin-top: 30px;
      border-radius: 22px;
      cursor: pointer;
      margin-left: 10px;
      transition: all 0.3s ease; */
      border: 0px;
      color: #fff;
      height: 44px;
      width: 100%;
      margin-top: 30px;
      border-radius: 22px;
      cursor: pointer;
      font-size: 16px;
      font-weight: 500;
      box-shadow: 0 4px 12px rgba(255,126,95,0.3);
      transition: all 0.3s ease;

    }
    .login-register .content .entry:hover {
      background: linear-gradient(135deg, #3a7be5 0%, #0e4fc9 100%);
      box-shadow: 0 4px 12px rgba(30, 95, 217, 0.3);
    }
    input:focus{
        outline: none;
    }
    button:focus{
        cursor: pointer;
    }
    .van-toast {
        position: absolute;
        bottom: 80px;
        left: 48%;
        transform: translateX(-50%);
    }
  </style>
