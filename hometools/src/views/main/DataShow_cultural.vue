<template>
  <div class="DataShow-index">
    <!-- 返回首页按钮 -->
  <div class="page-header">
     <BackButton :to="'/first'" :absolute="false" />
     <h1 class="page-title">数据中心</h1>
   </div>
    
    
    <!-- 顶部装饰性标题 -->
    <!-- <div class="mongolian-header">
      <div class="title-wrapper">
        <div class="horse-head-icon"></div>
        <h1>草原环境监测系统</h1>
        <div class="cloud-pattern"></div>
      </div>
    </div> -->

    <div class="data">
        <!-- 左侧 -->
    <div class="left">
      <!-- 顶部数据卡片 -->
      <div class="top">
        <div class="top-card" v-for="item in topCards" :key="item.key">
          <div class="card-icon">
            <icon :class="item.iconClass" :style="{ fontSize: item.iconSize || '80px', color: item.iconColor }" />
          </div>
          <div class="card-content">
            <div class="card-title">{{ item.title }}</div>
            <div class="card-value">
              <span :class="gradeClass(item.value)">{{ item.value }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 图表区域 -->
      <div class="two">
        <div class="shuzhu">
          <div class="chart-border">
            <div class="overview-header">
              <span class="ov-title">灾难指数</span>
              <div class="ov-badges">
                <span class="ov-badge ov-ok">火灾</span>
                <span class="ov-badge ov-info">地震</span>
              </div>
            </div>
            <e-charts class="chart" :option="optionshuzhu" :autoresize="true" />
          </div>
        </div>
        <div class="hengzhu">
          <div class="chart-border">
            <div class="overview-header">
              <span class="ov-title">预警次数</span>
              <div class="ov-badges">
                <span class="ov-badge ov-ok">今日</span>
                <span class="ov-badge ov-muted">单位：次</span>
              </div>
            </div>
            <e-charts class="chart" :option="optionhengzhu" :autoresize="true" />
          </div>
        </div>
      </div>

      <div class="three">
        <div class="zhe">
          <div class="chart-border">
            <div class="overview-header">
              <span class="ov-title">环境指数</span>
              <div class="ov-badges">
                <span class="ov-badge ov-muted">温湿度</span>
                <span class="ov-badge ov-muted">空气质量</span>
                <span class="ov-badge ov-muted">毒气/光照</span>
              </div>
            </div>
            <e-charts class="chart" :option="optionzhe" :autoresize="true" />
          </div>
        </div>
        <div class="shuju" @click="add()">
          <div class="title">
            今日数据
          </div>
          <div class="content">
            <!-- <div class="conbox" v-for="(data, index) in currentData" :key="index">
              {{ data.value }}
              <div class="tit">
                <i :class="'iconfont ' + data.icon"></i> {{ data.name }}
              </div>
            </div> -->
            <div class="conbox conbox-temp">
                {{ wendu }}
                <div class="tit">
                  温度
                </div>
              </div>
              <div class="conbox conbox-humidity">
                {{ shidu }}
                <div class="tit">
                  湿度
                </div>
              </div>
              <div class="conbox conbox-light">
                {{ guangxian }}
                <div class="tit">
                  光照强度
                </div>
              </div>
              <div class="conbox conbox-air">
                {{ kongqi }}
                <div class="tit">
                  空气质量
                </div>
              </div>
              <div class="conbox conbox-gas">
                {{ youdu }}
                <div class="tit">
                  毒气含量
                </div>
              </div>
              <div class="conbox conbox-alarm">
                {{ this.num }}
                <div class="tit">
                  报警次数
                </div>
              </div>
              <!-- <div class="conbox">
                {{ this.$store.state.data.dizhen }}
                <div class="tit">
                  地震指数
                </div>
              </div> -->
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧边栏 -->
    <div class="right">
      <div class="up">
        <div class="chart-border-round">
          <div class="overview-header">
            <span class="ov-title">预警占比</span>
            <div class="ov-badges">
              <span class="ov-badge ov-ok">总计</span>
              <span class="ov-badge ov-muted">饼图</span>
            </div>
          </div>
          <e-charts class="chart" :option="optionbing" :autoresize="true" :style="{ width: '100%', height: '330px' }" :key="JSON.stringify(optionbing)" />
        </div>
      </div>
      <div class="under">
        <table>
          <thead>
            <tr>
              <th>温度</th>
              <th>湿度</th>
              <th>空气质量</th>
              <th>毒气含量</th>
              <th>光照强度</th>
            </tr>
          </thead>
          <tbody>
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
          <!-- <tr class="first">
            <td v-for="(header, index) in tableHeaders" :key="index">
              <i :class="'iconfont ' + header.icon"></i> {{ header.name }}
            </td>
          </tr>
          <tr v-for="(row, index) in tableData" :key="'row'+index">
            <td v-for="(cell, cellIndex) in row" :key="'cell'+cellIndex">{{ cell }}</td>
          </tr> -->
        </tbody>
        </table>
      </div>
    </div>
    </div>


  </div>
</template>

<script>

import BackButton from '@/components/BackButton.vue'
import * as echarts from 'echarts'
export default {
  name: 'DataShowIndex',
  components: { BackButton,},
  computed: {
    topCards() {
      return [
        { key: 'ws', title: '温湿度', value: this.wsgrade, iconClass: 'iconfont icon-wenshidu', iconColor: '#26C6DA' },
        { key: 'gx', title: '光线强度', value: this.guanggrade, iconClass: 'iconfont icon-guangzhaoqiangdu1', iconColor: '#7E57C2' },
        { key: 'kq', title: '空气质量', value: this.konggrade, iconClass: 'iconfont icon-kongqizhiliang', iconColor: '#5C6BC0' },
        { key: 'dq', title: '毒气含量', value: this.dugrade, iconClass: 'iconfont icon-youduqiti2', iconColor: 'rgb(206, 190, 15)' },
      ]
    }
  },
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
              25.6, 76.7, 135.6, 162.2, 32.6, 20.0, 6.4, 3.3
              // 初始数据，在created()中会被更新
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
              28.7, 70.7, 175.6, 182.2, 48.7, 18.8, 6.0, 2.3
              // 初始数据，在created()中会被更新
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
          left: '3%',
          top: '10%',
          right: '4%',
          bottom: '6%',
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
        series: [          {
            name: '毒气含量',
            type: 'line',
            stack: 'Total',
            data: [120, 132, 101, 134, 90, 230, 210]
            // 初始数据，在created()中会被更新
          },
          {
            name: '湿度',
            type: 'line',
            stack: 'Total',
            data: [220, 182, 191, 234, 290, 330, 310]
            // 初始数据，在created()中会被更新
          },
          {
            name: '温度',
            type: 'line',
            stack: 'Total',
            data: [150, 232, 201, 154, 190, 330, 410]
            // 初始数据，在created()中会被更新
          },
          {
            name: '空气质量',
            type: 'line',
            stack: 'Total',
            data: [320, 332, 301, 334, 390, 330, 320]
            // 初始数据，在created()中会被更新
          },
          {
            name: '光线强度',
            type: 'line',
            stack: 'Total',
            data: [82, 93, 90, 93, 129, 133, 132]
            // 初始数据，在created()中会被更新
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
      dugrade: '良',
      num: 0,
      wendu: 24,
      shidu: 50,
      guangxian: 300,
      kongqi: 150,
      youdu: 50
    }
  },
  async created () {
     this.updateStoreData()
     this.judgeGrade()
     this.updateZaiChart()
     this.updateHuanChart()
     this.applyAnalysisStyle()
   },

   mounted() {
     // 确保ECharts在容器尺寸就绪后进行一次自适应
     this.$nextTick(() => {
       try {
         window.dispatchEvent(new Event('resize'))
       } catch (e) {}
     })
   },

  watch: {
    // 使用计算属性替代直接监听store，避免监听不到变化的问题
    '$store.state.dataPic': {
      handler () {
        if (this.$store && this.$store.state && this.$store.state.dataPic) {
          this.updateStoreData()
          this.updateZaiChart()
          this.updateHuanChart()
          this.judgeGrade()
          this.applyAnalysisStyle()
        }
      },
      deep: true
    },
    '$store.state.data': {
      handler () {
        if (this.$store && this.$store.state && this.$store.state.data) {
          this.updateStoreData()
          this.judgeGrade()
          // 环境折线数据也可能依赖最新值时同步刷新
          this.updateHuanChart()
          this.applyAnalysisStyle()
        }
      },
      deep: true
    }
  },
  methods: {
  gradeClass(g) {
    const map = { '优': 'grade-good', '良': 'grade-ok', '中': 'grade-warn', '差': 'grade-bad' }
    return 'grade-badge ' + (map[g] || 'grade-ok')
  },
    add () {
      this.num += 1
    },
    updateStoreData() {
      // 安全地从store获取数据，如果store不存在则使用默认值
      if (this.$store && this.$store.state && this.$store.state.data) {
        this.wendu = this.$store.state.data.wendu || 24
        this.shidu = this.$store.state.data.shidu || 50
        this.guangxian = this.$store.state.data.guangxian || 300
        this.kongqi = this.$store.state.data.kongqi || 150
        this.youdu = this.$store.state.data.youdu || 50
      }
    },
    judgeGrade () {
      if (this.wendu > 25 || this.shidu > 100) {
        this.wsgrade = '差'
      } else if (this.wendu < 25 && this.wendu > 15 && this.shidu > 30) {
        this.wsgrade = '优'
      } else if (this.wendu < 15) {
        this.wsgrade = '良'
      }
      if (this.guangxian > 600) {
        this.guanggrade = '差'
      } else if (this.guangxian < 600 && this.guangxian > 400) {
        this.guanggrade = '优'
      } else if (this.guangxian < 400 && this.guangxian > 200) {
        this.guanggrade = '良'
      }
      if (this.kongqi > 200) {
        this.konggrade = '差'
      } else if (this.guangxian < 200 && this.guangxian > 100) {
        this.konggrade = '良'
      } else if (this.guangxian < 100) {
        this.konggrade = '优'
      }
      if (this.youdu > 200) {
        this.dugrade = '差'
      } else if (this.youdu < 200 && this.youdu > 100) {
        this.dugrade = '良'
      } else if (this.youdu < 100) {
        this.dugrade = '优'
      }
    },
    updateZaiChart () {
      // 安全地更新图表数据
      if (this.$store && this.$store.state && this.$store.state.dataPic) {
        this.optionshuzhu.series[0].data = this.$store.state.dataPic.huozai || [25.6, 76.7, 135.6, 162.2, 32.6, 20.0, 6.4, 3.3]
        this.optionshuzhu.series[1].data = this.$store.state.dataPic.dizhen || [28.7, 70.7, 175.6, 182.2, 48.7, 18.8, 6.0, 2.3]
        this.optionshuzhu = { ...this.optionshuzhu }
      }
    },
    updateHuanChart () {
      // 安全地更新图表数据
      if (this.$store && this.$store.state && this.$store.state.dataPic) {
        this.optionzhe.series[0].data = this.$store.state.dataPic.youdu || [120, 132, 101, 134, 90, 230, 210]
        this.optionzhe.series[1].data = this.$store.state.dataPic.shidu || [220, 182, 191, 234, 290, 330, 310]
        this.optionzhe.series[2].data = this.$store.state.dataPic.wendu || [150, 232, 201, 154, 190, 330, 410]
        this.optionzhe.series[3].data = this.$store.state.dataPic.kongqi || [320, 332, 301, 334, 390, 330, 320]
        this.optionzhe.series[4].data = this.$store.state.dataPic.guangxian || [82, 93, 90, 93, 129, 133, 132]
        this.optionzhe = { ...this.optionzhe }
      }
    },
    applyAnalysisStyle () {
      const palette = ['#3B82F6', '#10B981', '#F59E0B', '#EF4444', '#8B5CF6']

      // 灾难指数（柱状图）
      this.optionshuzhu.color = ['#F97316', '#4C7AF2']
      this.optionshuzhu.grid = { left: '3%', right: '4%', bottom: '6%', top: '10%', containLabel: true }
      this.optionshuzhu.tooltip = { trigger: 'axis', axisPointer: { type: 'shadow' } }
      this.optionshuzhu.xAxis = {
        type: 'category',
        data: this.optionshuzhu.xAxis[0].data,
        axisLine: { lineStyle: { color: 'rgba(26,58,110,0.25)' } },
        axisTick: { show: false },
        axisLabel: { color: '#5B6B8C' }
      }
      this.optionshuzhu.yAxis = {
        type: 'value',
        splitLine: { show: true, lineStyle: { color: 'rgba(26,58,110,0.12)' } },
        axisLine: { show: false },
        axisLabel: { color: '#5B6B8C' }
      }
      this.optionshuzhu.series = this.optionshuzhu.series.map((s, idx) => ({
        ...s,
        type: 'bar',
        barWidth: 18,
        itemStyle: { borderRadius: [6, 6, 0, 0] },
        emphasis: { focus: 'series' }
      }))
      this.optionshuzhu = { ...this.optionshuzhu }

      // 预警次数（横向条形）
      if (echarts && echarts.graphic) {
        this.optionhengzhu.series = this.optionhengzhu.series.map(s => ({
          ...s,
          type: 'bar',
          barWidth: 20,
          itemStyle: {
            borderRadius: 6,
            color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
              { offset: 0, color: 'rgba(5,80,57,1)' },
              { offset: 1, color: 'rgba(13,253,178,1)' }
            ])
          }
        }))
      }
      this.optionhengzhu.tooltip = { trigger: 'axis', axisPointer: { type: 'shadow' } }
      this.optionhengzhu.xAxis = {
        type: 'value',
        axisLabel: { show: false },
        axisLine: { show: true, lineStyle: { color: 'rgba(26,58,110,0.15)' } },
        splitLine: { show: true, lineStyle: { color: 'rgba(26,58,110,0.12)' } },
        axisTick: { show: false }
      }
      this.optionhengzhu.yAxis = {
        ...this.optionhengzhu.yAxis,
        axisLabel: { color: '#5B6B8C', fontSize: 11, hideOverlap: true },
        axisLine: { show: true, lineStyle: { color: 'rgba(26,58,110,0.15)' } }
      }
      this.optionhengzhu = { ...this.optionhengzhu }

      // 环境指数（折线图）
      this.optionzhe.color = palette
      this.optionzhe.legend = {
        ...this.optionzhe.legend,
        bottom: 8,
        icon: 'circle',
        type: 'scroll',
        itemWidth: 10,
        itemHeight: 10,
        itemGap: 8,
        textStyle: { color: '#5B6B8C', fontSize: 11 }
      }
      this.optionzhe.grid = { left: '3%', right: '4%', bottom: '10%', top: '8%', containLabel: true }
      this.optionzhe.xAxis = {
        type: 'category',
        boundaryGap: false,
        data: this.optionzhe.xAxis.data,
        axisLine: { lineStyle: { color: 'rgba(26,58,110,0.25)' } },
        axisTick: { show: false },
        axisLabel: { color: '#5B6B8C', fontSize: 11, hideOverlap: true, overflow: 'truncate' }
      }
      this.optionzhe.yAxis = {
        type: 'value',
        splitLine: { show: true, lineStyle: { color: 'rgba(26,58,110,0.12)' } },
        axisLine: { show: false },
        axisLabel: { color: '#5B6B8C', fontSize: 11, hideOverlap: true, overflow: 'truncate' }
      }
      this.optionzhe.series = this.optionzhe.series.map((s, i) => ({
        ...s,
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        lineStyle: { width: 2 },
        areaStyle: { opacity: 0.15 }
      }))
      this.optionzhe = { ...this.optionzhe }

      // 预警占比（饼图）- 改为环形图
      const pieData = (this.optionbing.series && this.optionbing.series[0] && this.optionbing.series[0].data) || []
      const total = pieData.reduce((sum, d) => sum + (d.value || 0), 0)
      this.optionbing.legend = { orient: 'vertical', right: 10, top: 10 }
       this.optionbing.tooltip = { trigger: 'item', formatter: '{b}: {d}%' }
      this.optionbing.series = [{
        ...this.optionbing.series[0],
        type: 'pie',
        radius: ['55%', '75%'],
        center: ['50%', '50%'],
        itemStyle: { borderColor: '#fff', borderWidth: 2 },
        label: { show: true, position: 'inside', formatter: '{d}%', fontSize: 12 },
        labelLine: { show: false }
      }]
      this.optionbing.color = ['#1e5ca8', '#3949ab', '#90caf9', '#64b5f6', '#1976d2']
      this.optionbing.graphic = [{

         type: 'text',
         left: 'center',
         top: 'middle',
         style: {
          text: total ? `今日预警\n${total}` : '今日预警\n0',
          fill: '#1e5ca8',
           fontSize: 16,
           fontWeight: 'bold',
           align: 'center'
         }
       }]
      this.optionbing = { ...this.optionbing }
    },
  }
}
</script>

<style scoped>
/* 蒙古族风格主容器 */
.DataShow-index {
 width: 100%;
 min-height: 100vh;
 /* min-height: 100vh; */
 background: linear-gradient(135deg, #f0f7ff, #e6f0ff);
 padding: 20px;
 color: #1e5ca8;
 font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
 position: relative;
  overflow-x: hidden;
  --primary: #1e5ca8;
  --accent: #3949ab;
  --card-bg: rgba(255, 255, 255, 0.95);
   --border: rgba(30, 80, 181, 0.25);
  --text: #1e5ca8;
   --muted-text: #5B6B8C;
  --shadow: 0 8px 32px rgba(30, 92, 168, 0.1);
 }

.page-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 0;
  margin-bottom: 10px;
}
.page-header .back-button {
  background: transparent;
  color: #1e5ca8;
  border: 1px solid rgba(144, 202, 249, 0.6);
  border-radius: 8px;
  box-shadow: none;
  padding: 6px 12px;
}
.page-header .back-button:hover {
  background: rgba(144, 202, 249, 0.12);
  transform: none;
  box-shadow: none;
}
.page-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--text);
}
.DataShow-index .data{
   display: flex;
   gap: 16px;
   flex-wrap: nowrap;
   align-items: flex-start; /* 两列顶部对齐，避免错层 */
 }
 
 /* 蒙古族风格头部 */
.mongolian-header {
width: 100%;
height: 80px;
background: linear-gradient(to right, #1E50B5, #3A5F0B);
margin-bottom: 30px;
border-radius: 0 0 15px 15px;
position: relative;
overflow: hidden;
}

.mongolian-header::before {
content: "";
position: absolute;
top: 0;
left: 0;
right: 0;
height: 5px;
background: linear-gradient(to right, #D4AF37, #9C2B2B);
}

.title-wrapper {
display: flex;
align-items: center;
justify-content: center;
padding: 15px;
}

.horse-head-icon {
width: 40px;
height: 40px;
background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill="%23D4AF37" d="M12 2L4 12L12 22L20 12L12 2Z"/></svg>');
background-size: contain;
margin-right: 15px;
}

.mongolian-header h1 {
color: #FFF8E8;
font-size: 24px;
text-shadow: 2px 2px 4px rgba(0,0,0,0.3);
letter-spacing: 2px;
}

.cloud-pattern {
width: 100%;
height: 20px;
background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 20"><path fill="%23D4AF37" d="M0 10 Q25 20 50 10 T100 10" stroke="none"/></svg>');
background-repeat: repeat-x;
position: absolute;
bottom: 0;
}

/* 左侧内容区 */
.DataShow-index .left {
  width: calc(100% - 406px);
  height: auto; /* 取消固定高度，避免底部截断 */
  min-height: 823px; /* 保持大屏初始高度 */
  background-color: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(30, 92, 168, 0.1);
  border: 1px solid rgba(144, 202, 249, 0.3);
  flex: 1 1 auto;
  min-width: 0;
  padding: 12px;
  box-sizing: border-box;
  overflow: visible; /* 允许内容完整显示 */
}

.DataShow-index .top {
  width: 100%;
  min-height: 135px;
  display: grid;
  grid-template-columns: repeat(4, minmax(240px, 1fr));
  gap: 6px; /* 原 8px -> 更紧凑 */
  margin-bottom: 18px; /* 原 18px -> 略收紧 */
}

/* 顶部卡片 */
.top-card {
  display: flex;
  align-items: center;
  gap: 10px; /* 原 12px，卡片内部更紧凑 */
  height: 130px;
  padding: 12px 14px; /* 原 14px 16px */
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95), rgba(240, 244, 248, 0.95));
  border-radius: 14px;
  border: 1px solid rgba(144, 202, 249, 0.35);
  box-shadow: 0 5px 18px rgba(30, 92, 168, 0.08);
  transition: transform .2s ease, box-shadow .2s ease;
  box-sizing: border-box;
  overflow: hidden;
}
.top-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 10px 22px rgba(30, 92, 168, 0.15);
}
.card-icon {
  width: 92px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.card-content {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.card-title {
  font-size: 15px;
  color: var(--text);
  font-weight: 600;
}
.card-value {
  font-size: 22px;
}
.grade-badge {
  display: inline-block;
  min-width: 40px;
  text-align: center;
  padding: 2px 10px;
  border-radius: 12px;
  border: 1px solid transparent;
  font-weight: 600;
}
.grade-good { background: rgba(16, 185, 129, 0.12); color: #0f766e; border-color: rgba(16, 185, 129, 0.3); }
.grade-ok   { background: rgba(59, 130, 246, 0.12); color: #1d4ed8; border-color: rgba(59, 130, 246, 0.3); }
.grade-warn { background: rgba(245, 158, 11, 0.16); color: #b45309; border-color: rgba(245, 158, 11, 0.35); }
.grade-bad  { background: rgba(239, 68, 68, 0.16); color: #b91c1c; border-color: rgba(239, 68, 68, 0.35); }

.DataShow-index .left .top .topbox {
height: 120px;
width: 265px;
display: flex;
justify-content: space-around;
 background: linear-gradient(135deg, rgba(255, 255, 255, 0.95), rgba(240, 244, 248, 0.95));
 border-radius: 12px;
 box-shadow: 0 5px 18px rgba(30, 92, 168, 0.05);
 border: 1px solid rgba(144, 202, 249, 0.3);
 position: relative;
 overflow: hidden;
 transition: all 0.3s ease;
}

.topbox:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(30, 92, 168, 0.15);
}



.DataShow-index .left .top .topbox .left {
width: 140px;
height: 100%;
padding: 15px;
}

.DataShow-index .left .top .topbox .title {
margin-top: 5px;
margin-bottom: 10px;
font-size: 15px;
font-weight: bold;
color: #1a3a6e;
position: relative;
padding-left: 25px;
}



.DataShow-index  .topbox .content {
font-size: 28px;
 color: #1e5ca8;
font-weight: bold;
text-shadow: 1px 1px 2px rgba(0,0,0,0.1);
}

.DataShow-index .left .top .topbox .right {
width: 200px;
height: 120px;
display: flex;
justify-content: center;
align-items: center;
}

.DataShow-index  .iconfont {
font-size: 80px !important;
 color: #1e5ca8 !important;
text-shadow: 2px 2px 4px rgba(0,0,0,0.1);
}

/* 图表区域 */
.overview-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 36px;
  padding: 0 12px;
  border-bottom: 1px solid rgba(30, 80, 181, 0.15);
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.96), rgba(240, 244, 248, 0.9));
  border-top-left-radius: 12px;
  border-top-right-radius: 12px;
}

.ov-title {
  font-size: 14px;
  font-weight: 600;
  color: #1a3a6e;
}

.ov-badges { display: flex; gap: 8px; }

.ov-badge {
  font-size: 12px;
  line-height: 20px;
  padding: 0 8px;
  border-radius: 10px;
  border: 1px solid rgba(26, 58, 110, 0.2);
}

.ov-ok   { background: rgba(16, 185, 129, 0.12); color: #0f766e; border-color: rgba(16, 185, 129, 0.3); }
.ov-info { background: rgba(59, 130, 246, 0.12); color: #1d4ed8; border-color: rgba(59, 130, 246, 0.3); }
.ov-muted{ background: rgba(144, 202, 249, 0.12);  color: #1e5ca8; border-color: rgba(144, 202, 249, 0.35); }

.DataShow-index .left .two{
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  margin-bottom: 20px;
  align-items: stretch; /* 保证两列等高 */
}

.DataShow-index .left .three{
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  min-height: 360px;
  height: auto;
}

.DataShow-index  .chart-border {
  width: 100%;
  height: 100%;
  background-color: rgba(255, 248, 232, 0.9);
  border-radius: 12px;
  border: 1px solid rgba(30, 80, 181, 0.25);
  box-shadow: 0 4px 12px rgba(26, 58, 110, 0.08);
  position: relative;
  padding: 8px 12px 12px;
  box-sizing: border-box;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.DataShow-index  .chart-border::before {
content: "";
position: absolute;
top: 6px;
left: 6px;
right: 6px;
bottom: 6px;
 border: 1px dashed rgba(144, 202, 249, 0.35);
 border-radius: 8px;
 pointer-events: none;
}

.DataShow-index  .chart-border-round {
width: 100%;
height: 100%;
padding: 8px 12px 12px;
 background-color: rgba(255, 255, 255, 0.95);
 border-radius: 20px;
 border: 1px solid rgba(144, 202, 249, 0.3);
 overflow: hidden;
 background-color: rgba(255, 255, 255, 0.95);
 border-radius: 16px;
 box-shadow: 0 8px 24px rgba(30, 92, 168, 0.12);
 border: 1px solid rgba(144, 202, 249, 0.30);
 padding: 12px 14px;
 transition: all 0.25s ease;
}

.DataShow-index .right .under:hover{
  transform: translateY(-2px);
  box-shadow: 0 12px 36px rgba(30, 92, 168, 0.18);
}

/* 表格基础样式 */
.DataShow-index .right .under table{
  width: 100%;
  border-collapse: collapse;
  table-layout: fixed;
  margin-top: 6px;
}

.DataShow-index .right .under thead th{
  text-align: center;
  color: #1e5ca8;
  font-weight: 600;
  font-size: 14px;
  background: rgba(144, 202, 249, 0.18);
  padding: 10px 8px;
}

.DataShow-index .right .under thead th:first-child{
  border-top-left-radius: 10px;
}
.DataShow-index .right .under thead th:last-child{
  border-top-right-radius: 10px;
}

.DataShow-index .right .under tbody tr{
  height: 40px;
  transition: background-color 0.25s ease;
}

/* 斑马条纹与悬停效果 */
.DataShow-index .right .under tbody tr:nth-child(odd){
  background-color: rgba(144, 202, 249, 0.08);
}
.DataShow-index .right .under tbody tr:hover{
  background-color: rgba(144, 202, 249, 0.16);
}

.DataShow-index .right .under tbody td{
  text-align: center;
  color: #1e5ca8;
  border-bottom: 1px solid rgba(144, 202, 249, 0.25);
  padding: 8px 6px;
}

.DataShow-index .left .two .shuzhu .chart{
  margin-top: 12px;
  height: 280px;
}

.DataShow-index .left .two .hengzhu .chart{
  margin: 12px 0 0 0;
  width: 100%;
  height: 280px;
}

.DataShow-index .left .three .zhe .chart{
  margin-top: 8px;
  height: 300px;
  flex: 1 1 auto; /* 让图表填充剩余空间 */
  min-height: 0;   /* 解决flex子元素高度计算问题 */
}

.DataShow-index .right .up .chart{
    margin-top: 20px;
    height: 330px;
    width: 100%;
  }

/* 数据展示区 */
.DataShow-index  .shuju {
width: 455px;
/* padding: 15px; */
 background-color: rgba(255, 255, 255, 0.85);
 border-radius: 12px;
 border: 1px solid rgba(144, 202, 249, 0.3);
 /* border: 2px solid #1E50B5; */
}

.DataShow-index .shuju .title {
height: 30px;
margin-bottom: 15px;
font-size: 20px;
font-weight: bold;
color: #1a3a6e;
position: relative;
padding-left: 30px;
display: flex;
align-items: center; /* 垂直居中标题文本 */
line-height: 30px; /* 保障在固定高度下居中 */
}

.DataShow-index .content {
display: flex;
flex-wrap: wrap;
justify-content: center; /* 由space-around改为居中，避免不均匀间距 */
gap: 12px; /* 统一间距 */
}

.DataShow-index .conbox {
width: 100%;
height: 100px;
margin: 6px; /* 原 10px，更紧凑 */
display: grid; /* 改为grid更好居中 */
place-items: center; /* 完全居中数字与标题 */
row-gap: 6px; /* 数字与标题间距 */
box-sizing: border-box;
font-size: 28px;
color: #1E50B5;
font-weight: bold;
background-color: rgba(212, 175, 55, 0.1);
border-radius: 8px;
border: 1px solid rgba(58, 95, 11, 0.3);
transition: all 0.3s ease;
}

.DataShow-index .conbox:hover {
background-color: rgba(212, 175, 55, 0.2);
transform: scale(1.05);
}

.DataShow-index .tit {
font-size: 16px;
color: #3A5F0B;
margin-top: 0; /* 去掉额外上边距，保证垂直居中视觉 */
}

/* 右侧边栏取消固定高度避免截断 */
.DataShow-index .right {
  width: 390px;
  height: auto;
  min-height: 823px;
  background-color: rgba(255, 248, 232, 0.7);
  border-radius: 10px;
  box-shadow: 0 0 15px rgba(26, 58, 110, 0.1);
  border: 1px solid rgba(212, 175, 55, 0.3);
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 10px;
  flex-shrink: 0;
  min-width: 0;
  overflow: visible;
}

/* 今日数据卡片-美化 */
.DataShow-index  .shuju {
  width: 100%;
  /* padding: 15px; */
  background-color: rgba(255, 255, 255, 0.85);
  border-radius: 12px;
  border: 1px solid rgba(144, 202, 249, 0.3);
  /* border: 2px solid #1E50B5; */
}

.DataShow-index .shuju .title {
height: 30px;
margin-bottom: 12px;
font-size: 18px;
font-weight: 700;
color: #1a3a6e;
position: relative;
padding-left: 36px;
display: flex;
align-items: center;
line-height: 30px;
}

.DataShow-index .shuju .title::before{
  content: "";
  position: absolute;
  left: 14px;
  top: 50%;
  transform: translateY(-50%);
  width: 6px;
  height: 60%;
  border-radius: 6px;
  background: linear-gradient(180deg, rgba(30,80,181,0.9), rgba(144,202,249,0.6));
}

/* 覆盖并优化今日数据内容布局 */
.DataShow-index .shuju .content{
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  justify-items: center;
  gap: 12px;
}

/* 今日数据-单项卡片 */
.DataShow-index .conbox {
  width: 100px;
  height: 100px;
  margin: 0; /* 由6px改为由父grid负责间距 */
  display: grid;
  place-items: center;
  row-gap: 6px;
  box-sizing: border-box;
  font-size: 26px;
  color: #1E50B5;
  font-weight: bold;
  background: linear-gradient(180deg, rgba(212, 175, 55, 0.12), rgba(212, 175, 55, 0.06));
  border-radius: 10px;
  border: 1px solid rgba(58, 95, 11, 0.30);
  box-shadow: 0 2px 10px rgba(26, 58, 110, 0.08);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.DataShow-index .conbox:hover {
  background: linear-gradient(180deg, rgba(212, 175, 55, 0.2), rgba(212, 175, 55, 0.12));
  transform: translateY(-2px) scale(1.03);
  box-shadow: 0 6px 16px rgba(26, 58, 110, 0.14);
}

.DataShow-index .tit {
  font-size: 14px;
  color: #3A5F0B;
  margin-top: 0;
  opacity: 0.9;
}

/* 调整今日数据小卡片尺寸 */
.DataShow-index .conbox {
  width: 120px;
  height: 120px;
  font-size: 30px;
}

/* 为各指标提供对应渐变色主题 */
.DataShow-index .conbox-temp {
  background: linear-gradient(180deg, rgba(255, 183, 77, 0.22), rgba(255, 138, 101, 0.12));
  border-color: rgba(255, 145, 0, 0.35);
}
.DataShow-index .conbox-humidity {
  background: linear-gradient(180deg, rgba(59, 130, 246, 0.22), rgba(125, 211, 252, 0.12));
  border-color: rgba(59, 130, 246, 0.35);
}
.DataShow-index .conbox-light {
  background: linear-gradient(180deg, rgba(126, 87, 194, 0.22), rgba(250, 175, 64, 0.12));
  border-color: rgba(126, 87, 194, 0.35);
}
.DataShow-index .conbox-air {
  background: linear-gradient(180deg, rgba(16, 185, 129, 0.22), rgba(100, 221, 23, 0.12));
  border-color: rgba(16, 185, 129, 0.35);
}
.DataShow-index .conbox-gas {
  background: linear-gradient(180deg, rgba(206, 190, 15, 0.22), rgba(255, 235, 59, 0.12));
  border-color: rgba(206, 190, 15, 0.35);
}
.DataShow-index .conbox-alarm {
  background: linear-gradient(180deg, rgba(244, 63, 94, 0.22), rgba(244, 63, 94, 0.12));
  border-color: rgba(244, 63, 94, 0.35);
}
</style>
