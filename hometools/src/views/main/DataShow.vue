<template>
    <div class="DataShow-index">
      <!-- 返回首页按钮 -->
  <BackButton :to="'/first'" :absolute="true" placement="left" />
      
      <!-- 左侧 -->
      <div class="left">
        <!-- 顶部，将各数据分为不同的优良等级 -->
        <div class="top">
          <!-- 每个放数据的小模块 -->
          <div class="topbox">
            <!-- 左侧放数据 -->
            <div class="left">
              <div class="title">
                温湿度
              </div>
              <div class="content">
                {{ wsgrade }}
              </div>
            </div>
            <!-- 右侧放图标 -->
            <div class="right"> 
              <icon style="font-size: 100px; color: rgb(35, 154, 194);" class="iconfont icon-wenshidu"></icon>
            </div>
          </div>
          <div class="topbox">
            <div class="left">
              <div class="title">
                光线强度
              </div>
              <div class="content">
                {{ guanggrade }}
              </div>
            </div>
            <div class="right">
              <icon style="font-size: 95px; color: rgb(206, 101, 15);" class="iconfont icon-guangzhaoqiangdu1"></icon>
            </div>
          </div>
          <div class="topbox">
            <div class="left">
              <div class="title">
                空气质量
              </div>
              <div class="content">
                {{ konggrade }}
              </div>
            </div>
            <div class="right">
              <icon style="font-size: 95px; color: rgb(25, 206, 15);" class="iconfont icon-kongqizhiliang"></icon>
            </div>
          </div>
          <div class="topbox">
            <div class="left">
              <div class="title">
                有毒气体含量
              </div>
              <div class="content">
                {{ dugrade }}
              </div>
            </div>
            <div class="right">
              <icon style="font-size: 95px; color: rgb(206, 190, 15);" class="iconfont icon-youduqiti2"></icon>
            </div>
          </div>
        </div>

        <!-- 灾难指数图像 -->
        <div class="two">
          <div class="shuzhu">
            <e-charts
              class="chart"
              :option="optionshuzhu"
            />
          </div>
          <!-- 预警次数图像 -->
          <div class="hengzhu">
            <!-- <div class="title">
              异常数据统计
            </div> -->
            <e-charts
              class="chart"
              :option="optionhengzhu"
            />
          </div>
        </div>
        <div class="three">
          <div class="zhe">
            <e-charts
              class="chart"
              :option="optionzhe"
            />
          </div>
          <div class="shuju">
            <div class="title">
              今日数据
            </div>
            <div class="content">
              <div class="conbox">
                {{ this.$store.state.data.wendu }}
                <div class="tit">
                  温度
                </div>
              </div>
              <div class="conbox">
                {{ this.$store.state.data.shidu }}
                <div class="tit">
                  湿度
                </div>
              </div>
              <div class="conbox">
                {{ this.$store.state.data.guangxian }}
                <div class="tit">
                  光照强度
                </div>
              </div>
              <div class="conbox">
                {{ this.$store.state.data.kongqi }}
                <div class="tit">
                  空气质量
                </div>
              </div>
              <div class="conbox">
                {{ this.$store.state.data.youdu }}
                <div class="tit">
                  毒气含量
                </div>
              </div>
              <div class="conbox">
                {{ this.$store.state.data.huozai }}
                <div class="tit">
                  火灾指数
                </div>
              </div>
              <div class="conbox">
                {{ this.$store.state.data.dizhen }}
                <div class="tit">
                  地震指数
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="right">
        <div class="up">
          <e-charts
              class="chart"
              :option="optionbing"
            />
        </div>
        <div class="under">
          <!-- <div class="title">
            今日数据
          </div> -->
          <table>
            <tr class="first">
              <td>温度</td>
              <td>湿度</td>
              <td>空气质量</td>
              <td>毒气含量</td>
              <td>光照强度</td>
            </tr>
            <tr>
              <td>24.0</td>
              <td>50%</td>
              <td>250</td>
              <td>28</td>
              <td>0.1</td>
            </tr>
            <tr>
              <td>23.0</td>
              <td>55%</td>
              <td>400</td>
              <td>30</td>
              <td>0.15</td>
            </tr>
            <tr>
              <td>23.0</td>
              <td>60%</td>
              <td>500</td>
              <td>45</td>
              <td>0.3</td>
            </tr>
            <tr>
              <td>28.0</td>
              <td>65%</td>
              <td>800</td>
              <td>20</td>
              <td>0.05</td>
            </tr>
            <tr>
              <td>18.0</td>
              <td>75%</td>
              <td>1000</td>
              <td>25</td>
              <td>0.08</td>
            </tr>
            <tr>
              <td>26.5</td>
              <td>40%</td>
              <td>12000</td>
              <td>38</td>
              <td>0.12</td>
            </tr>
            <tr>
              <td>20.0</td>
              <td>85%</td>
              <td>3000</td>
              <td>55</td>
              <td>0.25</td>
            </tr>
            <tr>
              <td>19.5</td>
              <td>70%</td>
              <td>5000</td>
              <td>15</td>
              <td>0.03</td>
            </tr>

          </table>
        </div>
      </div>

    </div>
</template>

<script>
import { Icon } from 'vant'

import BackButton from '@/components/BackButton.vue'
export default {
  name: 'DataShowIndex',
  data () {
    return {
      optionshuzhu: {
        title: {
          text: '灾难指数',
          subtext: '危险程度'
        },
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['火灾', '地震']
        },
        toolbox: {
          show: true,
          feature: {
            // dataView: { show: true, readOnly: false },
            magicType: { show: true, type: ['line', 'bar'] },
            restore: { show: true }
            // saveAsImage: { show: true }
          }
        },
        calculable: true,
        xAxis: [
          {
            type: 'category',
            // prettier-ignore
            data: ['10:00', '12:00', '14:00', '16:00', '18:00', '20:00', '22:00', '24:00']
          }
        ],
        yAxis: [
          {
            type: 'value'
          }
        ],
        series: [
          {
            name: '火灾',
            type: 'bar',
            data: [
              this.$store.state.dataPic.huozai
              // 25.6, 76.7, 135.6, 162.2, 32.6, 20.0, 6.4, 3.3
            ]
            // markPoint: {
            //   data: [
            //     { type: 'max', name: 'Max' },
            //     { type: 'min', name: 'Min' }
            //   ]
            // },
            // markLine: {
            //   data: [{ type: 'average', name: 'Avg' }]
            // }
          },
          {
            name: '地震',
            type: 'bar',
            data: [
              this.$store.state.dataPic.dizhen
              // 28.7, 70.7, 175.6, 182.2, 48.7, 18.8, 6.0, 2.3
            ]
            // markPoint: {
            //   data: [
            //     { name: 'Max', value: 182.2, xAxis: 7, yAxis: 183 },
            //     { name: 'Min', value: 2.3, xAxis: 11, yAxis: 3 }
            //   ]
            // },
            // markLine: {
            //   data: [{ type: 'average', name: 'Avg' }]
            // }
          }
        ]
      },
      optionhengzhu: {
        title: {
          text: '预警次数'
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        grid: {
          left: 0,
          top: '8%',
          right: '3%',
          bottom: '8%',
          containLabel: true
        },
        xAxis: {
          type: 'value',
          axisLabel: {
            show: false // 不显示x轴标签
          },
          axisLine: {
            // x轴线的颜色以及宽度
            show: true,
            lineStyle: { color: 'rgba(255,255,255,0.1)' }
          },
          axisTick: {
            show: false // x轴刻度线
          },
          splitLine: { // x轴网格线
            show: true,
            lineStyle: { color: 'rgba(255,255,255,0.1)' }
          }
        },
        yAxis: {
          type: 'category',
          axisTick: { show: false }, // y轴刻度线
          axisLabel: { color: '#999999' }, // y轴文字的配置
          axisLine: {
            // x轴线的颜色
            show: true,
            lineStyle: { color: 'rgba(255,255,255,0.1)' }
          },
          data: ['温度', '湿度', '光线强度', '有毒气体含量', '空气质量']
        },
        series: [
          {
            name: '人数',
            type: 'bar',
            stack: '总量',
            label: {
              normal: {
                show: true,
                position: 'inside', // 显示在柱子内部
                textStyle: { color: '#fff' },
                formatter: '{c}次' // 单位
              }
            },
            itemStyle: {
              color: {
                colorStops: [ // 柱子的渐变色
                  {
                    offset: 0,
                    color: 'rgba(5, 80, 57, 1)' // 0% 处的颜色
                  },
                  {
                    offset: 1,
                    color: 'rgba(13, 253, 178, 1)' // 100% 处的颜色
                  }
                ],
                global: false
              }
            },
            barWidth: 20, // 柱子的宽度
            data: [8, 5, 3, 3, 6]
          }
        ]
      },
      optionzhe: {
        title: {
          text: '环境指数'
        },
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['温度', '湿度', '空气质量', '毒气含量', '光线强度']
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        // toolbox: {
        //   feature: {
        //     saveAsImage: {}
        //   }
        // },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '温度',
            type: 'line',
            stack: 'Total',
            data: this.$store.state.dataPic.wendu
            // [120, 132, 101, 134, 90, 230, 210]
          },
          {
            name: '湿度',
            type: 'line',
            stack: 'Total',
            data: this.$store.state.dataPic.shidu
            // [220, 182, 191, 234, 290, 330, 310]
          },
          {
            name: '空气质量',
            type: 'line',
            stack: 'Total',
            data: this.$store.state.dataPic.kongqi
            // [150, 232, 201, 154, 190, 330, 410]
          },
          {
            name: '毒气含量',
            type: 'line',
            stack: 'Total',
            data: this.$store.state.dataPic.youdu
            // [320, 332, 301, 334, 390, 330, 320]
          },
          {
            name: '光线强度',
            type: 'line',
            stack: 'Total',
            data: this.$store.state.dataPic.guangxian
            // [82, 93, 90, 93, 129, 133, 132]
          }
        ]
      },
      optionbing: {
        title: {
          text: '预警占比'
          // subtext: 'Fake Data',
          // left: 'center'
        },
        tooltip: {
          trigger: 'item'
        },
        legend: {
          orient: 'vertical',
          left: 'right',
          top: '2%'
        },
        series: [
          {
            top: '8%',
            name: '今日预警',
            type: 'pie',
            radius: '50%',
            data: [
              { value: 1, name: '温度' },
              { value: 7, name: '湿度' },
              { value: 5, name: '光线强度' },
              { value: 4, name: '毒气含量' },
              { value: 3, name: '空气质量' }
            ],
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      },
      wsgrade: '良',
      guanggrade: '良',
      konggrade: '良',
      dugrade: '良'
    }
  },
  async created () {
    this.judgeGrade()
    this.updateZaiChart()
    this.updateHuanChart()
  },

  watch: {
    '$store.state.dataPic.huozai': {
      handler () {
        this.updateZaiChart()
      },
      deep: true
    },
    '$store.state.dataPic.dizhen': {
      handler () {
        this.updateZaiChart()
      },
      deep: true
    },
    '$store.state.dataPic.guangxian': {
      handler () {
        this.updateHuanChart()
      },
      deep: true
    },
    '$store.state.dataPic.youdu': {
      handler () {
        this.updateHuanChart()
      },
      deep: true
    },
    '$store.state.dataPic.wendu': {
      handler () {
        this.updateHuanChart()
      },
      deep: true
    },
    '$store.state.dataPic.shidu': {
      handler () {
        this.updateHuanChart()
      },
      deep: true
    },
    '$store.state.dataPic.kongqi': {
      handler () {
        this.updateHuanChart()
      },
      deep: true
    }
  },
  methods: {
    judgeGrade () {
      if (this.$store.state.data.wendu > 25 || this.$store.state.data.shidu > 30) {
        this.wsgrade = '差'
      } else if (this.$store.state.data.wendu < 25 && this.$store.state.data.wendu > 15 && this.$store.state.data.shidu < 30) {
        this.wsgrade = '优'
      } else if (this.$store.state.data.wendu < 15) {
        this.wsgrade = '良'
      }
      if (this.$store.state.data.guangxian > 200) {
        this.guanggrade = '差'
      } else if (this.$store.state.data.guangxian < 200 && this.$store.state.data.guangxian > 150) {
        this.guanggrade = '优'
      } else if (this.$store.state.data.guangxian < 150 && this.$store.state.data.guangxian > 100) {
        this.guanggrade = '良'
      }
      if (this.$store.state.data.kongqi > 200) {
        this.konggrade = '差'
      } else if (this.$store.state.data.guangxian < 200 && this.$store.state.data.guangxian > 100) {
        this.konggrade = '良'
      } else if (this.$store.state.data.guangxian < 100) {
        this.konggrade = '优'
      }
      if (this.$store.state.data.youdu > 200) {
        this.dugrade = '差'
      } else if (this.$store.state.data.youdu < 200 && this.$store.state.data.youdu > 100) {
        this.dugrade = '良'
      } else if (this.$store.state.data.youdu < 100) {
        this.dugrade = '优'
      }
    },
    updateZaiChart () {
      this.optionshuzhu.series[0].data = this.$store.state.dataPic.huozai
      this.optionshuzhu.series[1].data = this.$store.state.dataPic.dizhen
      this.optionshuzhu = { ...this.optionshuzhu }
    },
    updateHuanChart () {
      this.optionzhe.series[0].data = this.$store.state.dataPic.wendu
      this.optionzhe.series[1].data = this.$store.state.dataPic.shidu
      this.optionzhe.series[2].data = this.$store.state.dataPic.kongqi
      this.optionzhe.series[3].data = this.$store.state.dataPic.youdu
      this.optionzhe.series[4].data = this.$store.state.dataPic.guangxian
      this.optionzhe = { ...this.optionzhe }
    }
  },
  components: { BackButton, Icon }
}
</script>

<style>
  /* 全局样式 - 蓝色主题 */
  .DataShow-index{
    width: 100%;
    min-height: 100vh;
    background: linear-gradient(135deg, #1e5ca8 0%, #3949ab 50%, #1e5ca8 100%);
    background-size: 400% 400%;
    animation: gradientBG 15s ease infinite;
    display: flex;
    padding: 25px;
    box-sizing: border-box;
    color: #1e5ca8;
    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
    position: relative;
  }
  
  @keyframes gradientBG {
    0% {background-position: 0% 50%;}
    50% {background-position: 100% 50%;}
    100% {background-position: 0% 50%;}
  }
  
  /* 返回首页按钮样式 */
  .back-to-home {
    position: absolute;
    top: 15px;
    right: 15px;
    background: linear-gradient(135deg, #1e5ca8, #3949ab);
    color: white;
    padding: 8px 16px;
    border-radius: 8px;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 5px;
    font-size: 12px;
    box-shadow: 0 2px 8px rgba(30, 92, 168, 0.3);
    transition: all 0.3s ease;
    border: none;
    z-index: 100;
  }
  
  .back-to-home:hover {
    background: linear-gradient(135deg, #1a4d91, #2c3a99);
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(30, 92, 168, 0.4);
  }
  
  .back-to-home:active {
    transform: translateY(0);
    box-shadow: 0 2px 6px rgba(30, 92, 168, 0.3);
  }
  
  .back-to-home i {
    font-size: 14px;
  }
  
  /* 左侧区域 */
  .DataShow-index .left{
    width: 1160px;
    display: flex;
    flex-direction: column;
    gap: 20px;
    flex: 1;
  }
  
  /* 顶部数据等级展示 */
  .DataShow-index .left .top{
    width: 100%;
    height: 135px;
    display: flex;
    justify-content: space-around;
    margin-bottom: 20px;
  }
  
  /* 数据卡片样式 */
  .DataShow-index .left .top .topbox{
    height: 120px;
    width: 265px;
    display: flex;
    justify-content: space-around;
    background-color: rgba(255, 255, 255, 0.95);
    border-radius: 16px;
    box-shadow: 0 8px 32px rgba(30, 92, 168, 0.1);
    backdrop-filter: blur(4px);
    border: 1px solid rgba(144, 202, 249, 0.3);
    transition: all 0.3s ease;
  }
  
  .DataShow-index .left .top .topbox:hover {
    transform: translateY(-2px);
    box-shadow: 0 12px 40px rgba(30, 92, 168, 0.15);
  }
  
  .DataShow-index .left .top .topbox .title{
    margin-top: 15px;
    margin-left: 10px;
    margin-bottom: 15px;
    color: #1e5ca8;
    font-size: 16px;
    font-weight: bold;
  }
  
  .DataShow-index .left .top .topbox .content{
    font-size: 28px;
    margin-left: 25px;
    color: #1e5ca8;
    font-weight: bold;
  }
  
  .DataShow-index .left .top .topbox .left{
    width: 140px;
    height: 100%;
  }
  
  .DataShow-index .left .top .topbox .right{
    width: 200px;
    height: 120px;
    display: flex;
    justify-content: center;
    align-items: center;
  }
  
  /* 图表容器通用样式 */
  .DataShow-index .left .shuzhu, 
  .DataShow-index .left .two .hengzhu, 
  .DataShow-index .left .three .zhe, 
  .DataShow-index .left .three .shuju, 
  .DataShow-index .box, 
  .DataShow-index .right .up, 
  .DataShow-index .right .under {
    background-color: rgba(255, 255, 255, 0.95);
    border-radius: 16px;
    box-shadow: 0 8px 32px rgba(30, 92, 168, 0.1);
    backdrop-filter: blur(4px);
    border: 1px solid rgba(144, 202, 249, 0.3);
    transition: all 0.3s ease;
  }
  
  .DataShow-index .left .shuzhu:hover, 
  .DataShow-index .left .two .hengzhu:hover, 
  .DataShow-index .left .three .zhe:hover, 
  .DataShow-index .left .three .shuju:hover, 
  .DataShow-index .box:hover, 
  .DataShow-index .right .up:hover, 
  .DataShow-index .right .under:hover {
    transform: translateY(-2px);
    box-shadow: 0 12px 40px rgba(30, 92, 168, 0.15);
  }
  
  /* 图表尺寸 */
  .DataShow-index .left .shuzhu{
    height: 300px;
    width: 660px;
    padding: 15px;
    box-sizing: border-box;
  }
  
  /* 布局样式 */
  .DataShow-index .left .two{
    display: flex;
    justify-content: space-around;
    gap: 20px;
  }
  
  .DataShow-index .left .two .hengzhu{
    height: 300px;
    width: 455px;
    padding: 15px;
    box-sizing: border-box;
  }
  
  .DataShow-index .left .three{
    display: flex;
    height: 300px;
    justify-content: space-around;
    gap: 20px;
  }
  
  .DataShow-index .left .three .zhe{
    width: 660px;
    padding: 15px;
    box-sizing: border-box;
  }
  
  .DataShow-index .left .three .shuju{
    width: 455px;
    padding: 15px;
    box-sizing: border-box;
  }
  
  /* 标题样式 */
  .DataShow-index .left .hengzhu .title, 
  .DataShow-index .left .three .shuju .title {
    height: 30px;
    margin-top: 10px;
    margin-left: 10px;
    font-size: 20px;
    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
    font-weight: bold;
    color: #1e5ca8;
    margin-bottom: 10px;
  }
  
  /* 数据展示样式 */
  .DataShow-index .left .three .shuju .content{
    width: 450px;
    display: flex;
    flex-wrap: wrap;
    justify-content: space-around;
    margin-top: 10px;
  }
  
  .DataShow-index .left .three .shuju .content .conbox{
    width: 100px;
    height: 100px;
    text-align: center;
    display: flex;
    flex-direction: column;
    justify-content: center;
    font-size: 28px;
    color: #1e5ca8;
    font-weight: bold;
    background-color: rgba(144, 202, 249, 0.1);
    border-radius: 12px;
    transition: all 0.3s ease;
  }
  
  .DataShow-index .left .three .shuju .content .conbox:hover {
    background-color: rgba(144, 202, 249, 0.2);
    transform: scale(1.05);
  }
  
  .DataShow-index .left .three .shuju .content .tit{
    font-size: 18px;
    color: #64b5f6;
  }
  
  /* 右侧区域 */
  .DataShow-index .right{
    width: 390px;
    display: flex;
    justify-content: space-around;
    flex-direction: column;
    gap: 20px;
  }
  
  .DataShow-index .right .up{
    height: 345px;
    padding: 15px;
    box-sizing: border-box;
  }
  
  .DataShow-index .right .under{
    height: 400px;
    padding: 15px;
    box-sizing: border-box;
  }
  
  /* 表格样式 */
  .DataShow-index .right .under table{
    width: 92%;
    margin-top: 10px;
    border-collapse: collapse;
    margin-left: 4%;
  }
  
  .DataShow-index .right .under table tr{
    height: 40px;
    transition: background-color 0.3s ease;
  }
  
  .DataShow-index .right .under table tr:hover {
    background-color: rgba(144, 202, 249, 0.1);
  }
  
  .DataShow-index .right .under table td{
    text-align: center;
    border-bottom: 1px solid rgba(144, 202, 249, 0.3);
    height: 40px;
    color: #1e5ca8;
  }
  
  .DataShow-index .right .under table .first{
    width: 385px;
  }
  
  /* 图表容器样式 */
  .DataShow-index .left .two .shuzhu .chart{
    margin-top: 25px;
  }
  
  .DataShow-index .left .two .hengzhu .chart{
    margin-left: 10px;
    margin-top: 15px;
    margin-right: 20px;
    width: 440px;
  }
  
  .DataShow-index .left .three .zhe .chart{
    margin-top: 10px;
    height: 290px;
  }
  
  .DataShow-index .right .up .chart{
    margin-top: 20px;
    height: 335px;
  }
  
  /* 响应式设计 */
  @media (max-width: 1200px) {
    .DataShow-index {
      flex-direction: column;
      height: auto;
    }
    
    .DataShow-index .left, 
    .DataShow-index .left .top {
      width: 100%;
    }
    
    .DataShow-index .right {
      width: 100%;
    }
  }
  
  @media (max-width: 768px) {
    .DataShow-index {
      padding: 15px;
      gap: 15px;
    }
    
    .back-to-home {
      top: 10px;
      right: 10px;
      padding: 6px 12px;
      font-size: 11px;
    }
    
    .back-to-home i {
      font-size: 12px;
    }
    
    .DataShow-index .left .top {
      height: auto;
      flex-wrap: wrap;
      gap: 15px;
    }
    
    .DataShow-index .left .top .topbox {
      width: 200px;
      height: 100px;
    }
    
    .DataShow-index .left .top .topbox .title {
      font-size: 14px;
      margin-top: 10px;
    }
    
    .DataShow-index .left .top .topbox .content {
      font-size: 24px;
    }
    
    .DataShow-index .left .two, 
    .DataShow-index .left .three {
      flex-direction: column;
      height: auto;
      gap: 15px;
    }
    
    .DataShow-index .left .shuzhu, 
    .DataShow-index .left .two .hengzhu, 
    .DataShow-index .left .three .zhe, 
    .DataShow-index .left .three .shuju {
      width: 100%;
      height: 250px;
    }
    
    .DataShow-index .left .three .shuju .content {
      width: 100%;
    }
    
    .DataShow-index .left .three .shuju .content .conbox {
      width: 80px;
      height: 80px;
      margin: 6px;
    }
    
    .DataShow-index .left .three .shuju .content .conbox .tit {
      font-size: 16px;
    }
    
    .DataShow-index .right .up, 
    .DataShow-index .right .under {
      height: 300px;
    }
    
    .DataShow-index .right .under table {
      width: 100%;
      margin-left: 0;
      font-size: 12px;
    }
  }
  
  @media (max-width: 480px) {
    .DataShow-index {
      padding: 10px;
    }
    
    .DataShow-index .left .top .topbox {
      width: 160px;
      height: 90px;
    }
    
    .DataShow-index .left .top .topbox .title {
      font-size: 12px;
    }
    
    .DataShow-index .left .top .topbox .content {
      font-size: 20px;
      margin-left: 15px;
    }
    
    .DataShow-index .left .three .shuju .content .conbox {
      width: 70px;
      height: 70px;
      font-size: 24px;
    }
    
    .DataShow-index .left .three .shuju .content .conbox .tit {
      font-size: 14px;
    }
  }
</style>
