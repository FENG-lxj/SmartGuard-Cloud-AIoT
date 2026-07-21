<template>
    <div class="Control-index">
      <!-- 顶部，包含饼状图和离开在家睡眠三种状态 -->
      <div class="top">
        <e-charts
              class="chart"
              :option="option"
            />
            <div class="top-right">
              <div class="top-right-item" @click="topHome">
                <!-- 用于点击前和点击后的样式的控制 -->
                <div :class="topRightBtn1 ? 'topKai' : 'topGuan'">
                  <icon style="font-size: 90px;" class="iconfont icon-yiruzhu"></icon>
                  <div class="tit">
                    Home
                  </div>
                </div>
              </div>

              <div class="top-right-item" @click="topAway">
                <div :class="topRightBtn2 ? 'topKai' : 'topGuan'">
                  <icon style="font-size: 90px;" class="iconfont icon-jinriyuli-"></icon>
                  <div class="tit">
                    Away
                  </div>
                </div>
              </div>

              <div class="top-right-item" @click="topSleep">
                <div :class="topRightBtn3 ? 'topKai' : 'topGuan'">
                  <icon style="font-size: 90px;" class="iconfont icon-LyingDown"></icon>
                  <div class="tit">
                    Sleep
                  </div>
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
                <div class="title">
                  灯
                </div>
                <div class="btn">
                  <goodBtn :status="lightStatus" @toggle="toggleLight"></goodBtn>
                </div>
              </div>
              <div class="right-pic">
                <icon style="font-size: 260px;" class="iconfont icon-ladideng"></icon>
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
                <div class="title">
                  风扇
                </div>
                <div class="btn">
                  <goodBtn :status="fanStatus" @toggle="toggleFan"></goodBtn>
                </div>
              </div>

              <div class="right-pic">
                <icon style="font-size: 140px;" class="iconfont icon-fengshan"></icon>
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
export default {
  name: 'ControlIndex',
  components: {
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
  async created () {
    this.option.series[0].data[0].value = this.use
    this.option.series[0].data[1].value = this.unuse
    this.$forceUpdate()
    this.$nextTick(() => {
      this.$refs.chart && this.$refs.chart.setOption(this.option)
    })
  },
  watch: {
    use (newVal) {
      this.option.series[0].data[0].value = newVal
      this.$forceUpdate()
      this.$nextTick(() => {
        this.$refs.chart && this.$refs.chart.setOption(this.option)
      })
    },
    unuse (newVal) {
      this.option.series[0].data[1].value = newVal
      this.$forceUpdate()
      this.$nextTick(() => {
        this.$refs.chart && this.$refs.chart.setOption(this.option)
      })
    }
  },
  methods: {
    topHome () {
      this.topRightBtn1 = !this.topRightBtn1
      if (this.topRightBtn1) {
        this.doorLockStatus = true
        this.lightStatus = true
        this.wifiStatus = true
      } else {
        this.doorLockStatus = false
        this.lightStatus = false
        this.wifiStatus = false
      }
      this.doorLockStatus = true
      this.lightStatus = true
      this.wifiStatus = true
      console.log(this.doorLockStatus)
      if (this.topRightBtn1) {
        this.topRightBtn2 = false
        this.topRightBtn3 = false
      }
    },
    topAway () {
      this.topRightBtn2 = !this.topRightBtn2
      this.doorLockStatus = false
      this.lightStatus = false
      this.curtainStatus = false
      this.fanStatus = false
      this.kettleStatus = false
      this.wifiStatus = false
      console.log(this.topRightBtn2)
      if (this.topRightBtn2) {
        this.topRightBtn1 = false
        this.topRightBtn3 = false
      }
    },
    topSleep () {
      this.topRightBtn3 = !this.topRightBtn3
      if (this.topRightBtn3) {
        this.curtainStatus = true
        this.lightStatus = false
        this.kettleStatus = false
        this.doorLockStatus = true
      } else {
        this.curtainStatus = false
        this.doorLockStatus = false
      }

      if (this.topRightBtn3) {
        this.topRightBtn1 = false
        this.topRightBtn2 = false
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

    toggleDoorLock () {
      this.doorLockStatus = !this.doorLockStatus
    },
    toggleLight () {
      this.lightStatus = !this.lightStatus
    },
    toggleCurtain () {
      this.curtainStatus = !this.curtainStatus
    },
    toggleFan () {
      this.fanStatus = !this.fanStatus
    },
    toggleKettle () {
      this.kettleStatus = !this.kettleStatus
    },
    toggleWifi () {
      this.wifiStatus = !this.wifiStatus
    }
  }
}
</script>

<style scoped>
.Control-index {
  background: linear-gradient(135deg, #eef2f3, #c9d6ff);
  padding: 20px;
  color: #333;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.chart {
  flex: 1;
  max-width: 600px;
}

.top-right {
  display: flex;
  gap: 20px;
}

.top-right-item {
  cursor: pointer;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.top-right-item:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(57, 73, 171, 0.3);
}

.topKai {
  background-color: #3949AB;
  color: white;
  border-radius: 12px;
  padding: 10px;
  text-align: center;
  box-shadow: 0 4px 10px rgba(57, 73, 171, 0.4);
}

.topGuan {
  background-color: #E3EAF2;
  color: #607D8B;
  border-radius: 12px;
  padding: 10px;
  text-align: center;
  box-shadow: inset 0 0 5px rgba(0,0,0,0.05);
}

.iconfont {
  margin-bottom: 5px;
  color: inherit;
}

.tit {
  font-size: 18px;
  font-weight: bold;
}

.bottom {
  display: flex;
  gap: 20px;
}

.left,
.right {
  flex: 1;
}

/* .left-item, */
.right-item {
  background: #ffffff;
  border-radius: 12px;
  padding: 15px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(57, 73, 171, 0.1);
}

.left-item{
  height: 185px;
}
</style>
