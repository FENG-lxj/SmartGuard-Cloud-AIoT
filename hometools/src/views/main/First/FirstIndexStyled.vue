
<template>
    <!-- 用户提供的 template 部分 -->

    <div class="First-index">
      <!-- 所有数据展示都是从vuex中的data中获取出来的 -->
      <div class="left">
        <div class="up">
          <div class="dataitem">
            <div class="title">
              温度
            </div>
            <div class="data">
              {{ this.$store.state.data.wendu }}
            </div>
          </div>
          <div class="dataitem">
            <div class="title">
              光线
            </div>
            <div class="data">
              {{ this.$store.state.data.guangxian }}
            </div>
          </div>
          <div class="dataitem">
            <div class="title">
              空气
            </div>
            <div class="data">
              {{ this.$store.state.data.kongqi }}
            </div>
          </div>
          <div class="dataitem">
            <div class="title">
              毒气
            </div>
            <div class="data">
              {{ this.$store.state.data.youdu }}
            </div>
          </div>
          <div class="dataitem" style="width: 96%;">
            <div class="title">
              湿度
            </div>
            <div class="data">
              {{ this.$store.state.data.shidu }}
            </div>
          </div>
        </div>
        <div class="under">
          <e-charts
              class="chart"
              :option="option"
            />
        </div>
      </div>

      <div class="center">
        <!-- 这里是搜索框和搜索按钮 -->
        <div class="serch">
            <input class="input" ref="input" type="text" placeholder="请输入问题">
            <button class="AIButton" @click="serchAI">AI一下</button>
        </div>
        <div class="tool">
          <div class="title">
            所有家具
          </div>
          <div class="content">
            <div class="conitem">
              <div class="bgc">
                <icon style="font-size: 45px; color: rgb(206, 101, 15);" class="iconfont icon-deng"></icon>
              </div>
              <div class="bgc">
                <icon style="font-size: 45px; color: rgb(206, 101, 15);" class="iconfont icon-fengshan1"></icon>
              </div>
              <div class="bgc">
                <icon style="font-size: 45px; color: rgb(206, 101, 15);" class="iconfont icon-mti-WIFItanzhen"></icon>
              </div>
              <div class="bgc">
                <icon style="font-size: 45px; color: rgb(206, 101, 15);" class="iconfont icon-chuanglian1"></icon>
              </div>
            </div>
            <div class="conitem">
              <div class="bgc">
                <icon style="font-size: 45px; color: rgb(15, 95, 206);" class="iconfont icon-deng"></icon>
              </div>
              <div class="bgc">
                <icon style="font-size: 45px; color: rgb(15, 95, 206);" class="iconfont icon-fengshan1"></icon>
              </div>
              <div class="bgc">
                <icon style="font-size: 45px; color: rgb(15, 95, 206);" class="iconfont icon-mti-WIFItanzhen"></icon>
              </div>
              <div class="bgc">
                <icon style="font-size: 45px; color: rgb(15, 95, 206);" class="iconfont icon-chuanglian1"></icon>
              </div>
            </div>
            <div class="conitem">
              <div class="bgc">
                <icon style="font-size: 45px; color: rgba(50, 119, 10, 0.605);" class="iconfont icon-deng"></icon>
              </div>
              <div class="bgc">
                <icon style="font-size: 45px; color: rgba(50, 119, 10, 0.605);" class="iconfont icon-fengshan1"></icon>
              </div>
              <div class="bgc">
                <icon style="font-size: 45px; color: rgba(50, 119, 10, 0.605);" class="iconfont icon-mti-WIFItanzhen"></icon>
              </div>
              <div class="bgc">
                <icon style="font-size: 45px; color: rgba(50, 119, 10, 0.605);" class="iconfont icon-chuanglian1"></icon>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="right">
        <div class="up">
          <el-button type="text" @click="dialogFormVisible = true">添加个人信息</el-button>
          <el-dialog title="个人信息" v-model:visible="dialogFormVisible">
            <el-form :model="form">
              <el-form-item label="手机号" :label-width="formLabelWidth">
                <el-input v-model="form.tele" autocomplete="off"></el-input>
              </el-form-item>
              <el-form-item label="地址" :label-width="formLabelWidth">
                <v-distpicker v-model="form.address" province="北京市" city="北京市" area="朝阳区"></v-distpicker>
              </el-form-item>
              <el-form-item label="邮箱" :label-width="formLabelWidth">
                <el-input v-model="form.youxiang" autocomplete="off"></el-input>
              </el-form-item>
            </el-form>
            <template v-slot:footer>
<div  class="dialog-footer">
              <el-button @click="dialogFormVisible = false">取 消</el-button>
              <el-button type="primary" @click="Sumbitmessage()">确 定</el-button>
            </div>
</template>
          </el-dialog>
          <div class="pic">
            <icon style="font-size: 190px" class="iconfont icon-yonghutouxiang1"></icon>
          </div>
          <div class="info">
            <div class="infoitem">登出</div>
            <div class="infoitem">在线</div>
            <div class="infoitem">{{ this.$store.state.guest.username }}</div>
          </div>
        </div>
        <div class="under">
          <div class="title">
            最新提议
          </div>
          <div class="content">
            {{ this.advance }}
          </div>
        </div>
      </div>
      <div class="mongolian-footer">
        <div class="traditional-pattern"></div>
      </div>
    </div>
</template>

<script>
// 保留原始 script 内容
import axios from 'axios'
import SockJS from 'sockjs-client'
import { Stomp } from '@stomp/stompjs'
import '@/utils/vant-ui.js'
import VDistpicker from 'v-distpicker'

export default { /* 内容略... */ }
</script>

<style>

.First-index {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #f1f8f4, #e3efd9);
  color: #1a3a6e;
  padding: 10px;
  display: flex;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

/* 左侧区域 */
.First-index .left {
  width: 315px;
  height: 100%;
  margin-right: 10px;
  border-radius: 10px;
  background-color: rgba(250, 255, 248, 0.85);
  box-shadow: 0 0 12px rgba(26, 58, 110, 0.08);
  border: 1px solid rgba(212, 175, 55, 0.3);
}

/* 左侧上方每个数据框 */
.First-index .left .up .dataitem {
  height: 135px;
  width: 135px;
  border-radius: 10px;
  background: linear-gradient(to bottom right, #fdf5e6, #fffbe8);
  box-shadow: 0 4px 8px rgba(58, 95, 11, 0.1);
  border: 2px solid #D4AF37;
  transition: all 0.3s ease;
}
.First-index .left .up .dataitem:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
}
.First-index .left .up .data {
  height: 85px;
  width: 100%;
  font-size: 30px;
  display: flex;
  justify-content: center;
  align-items: center;
}

/* 右侧区域 */
.First-index .right {
  width: 315px;
  height: 100%;
  margin-left: 10px;
  border-radius: 10px;
  background-color: rgba(250, 255, 248, 0.85);
  box-shadow: 0 0 12px rgba(26, 58, 110, 0.08);
  border: 1px solid rgba(212, 175, 55, 0.3);
}

/* 公共上部背景 */
.First-index .up {
  padding-top: 10px;
  width: 100%;
  height: 440px;
  background-color: rgba(52, 39, 39, 0.07);
  margin-bottom: 10px;
  border-radius: 10px;
}

/* 中心搜索栏 */
.First-index .serch {
  height: 65px;
  width: 890px;
  margin-bottom: 10px;
  display: flex;
}
.First-index .serch .AIButton {
  background: linear-gradient(to right, #4facfe 0%, #00f2fe 100%);
  border-top-right-radius: 15px;
  border-bottom-right-radius: 15px;
  width: 85px;
  height: 65px;
  color: white;
  font-weight: bold;
  transition: all 0.3s ease;
}
.First-index .serch .AIButton:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}
.First-index .serch input {
  height: 65px;
  width: 800px;
  border-radius: 15px 0 0 15px;
  border: none;
}

/* 中间部分背景与设备模块 */
.First-index .center {
  padding-left: 1px;
  background-color: rgba(250, 255, 248, 0.85);
  border-radius: 10px;
  box-shadow: 0 0 12px rgba(26, 58, 110, 0.08);
  border: 1px solid rgba(212, 175, 55, 0.3);
}
.First-index .center .tool {
  height: 710px;
  width: 890px;
  background-color: rgba(52, 39, 39, 0.07);
  border-radius: 10px;
  padding-top: 1px;
}
.First-index .center .tool .title {
  margin: 40px;
  font-size: 26px;
  font-weight: bold;
  color: #254d3d;
}
.First-index .center .conitem {
  margin-left: 140px;
  display: flex;
  margin-bottom: 80px;
}
.First-index .center .conitem .bgc {
  background-color: #fff;
  width: 85px;
  height: 85px;
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 10px;
  margin-right: 100px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

/* 右侧头像与信息 */
.First-index .right .up .pic {
  height: 240px;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  padding-left: 20px;
}
.First-index .right .up .info {
  text-align: center;
  width: 100%;
  padding-left: 20px;
  margin-top: 20px;
}
.First-index .right .up .info .infoitem {
  height: 30px;
  width: 180px;
  background-color: #ffffff;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 20px;
  margin-left: 40px;
  border-radius: 10px;
  color: #2b4c6f;
}

/* 最新建议样式 */
.First-index .right .under {
  width: 100%;
  height: 330px;
  background-color: rgba(52, 39, 39, 0.07);
  border-radius: 10px;
}
.First-index .right .under .title {
  height: 50px;
  display: flex;
  font-size: 25px;
  margin-top: 10px;
  font-weight: bold;
  color: #3b403f;
}
.First-index .right .under .content {
  padding: 10px;
  height: 260px;
  overflow-y: scroll;
  color: #304050;
}
.First-index .right .under .content::-webkit-scrollbar {
  display: none;
}
</style>
