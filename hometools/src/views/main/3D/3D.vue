<template>
  <div class="ThreeD">
    <div ref="threeContainer" class="three-container">
      <!-- 返回首页按钮 -->
  <BackButton :to="'/first'" :absolute="true" placement="left" />
      <!-- 左上角家庭数据 -->
      <div class="home-data">
        <div class="title">
          家庭氛围
        </div>
        <div class="data-item" style="background-color: rgba(232, 216, 197, 0.8);">
          <div class="left">
            <icon style="font-size: 20px; color: #26C6DA; margin-right: 5px;" class="iconfont icon-wenshidu"></icon>
            温度
          </div>
          <div class="right">24</div>
        </div>
        <div class="data-item" style="background-color: rgba(189, 205, 226, 0.8);">
          <div class="left">
            <icon style="font-size: 20px; color: #26C6DA; margin-right: 5px" class="iconfont icon-wenshidu"></icon>
            湿度
          </div>
          <div class="right">45%</div>
        </div>
        <div class="data-item" style="background-color: rgba(175, 205, 185, 0.8);">
          <div class="left">
            <icon style="font-size: 18px; color: #5C6BC0; margin-right: 5px" class="iconfont icon-kongqizhiliang"></icon>
            空气
          </div>
          <div class="right">良好</div>
        </div>
        <div class="data-item" style="background-color: rgba(195, 193, 164, 0.8);">
          <div class="left">
            <icon style="font-size: 20px; color: #7E57C2; margin-right: 5px" class="iconfont icon-guangzhaoqiangdu1"></icon>
            光线
          </div>
          <div class="right">良好</div>
        </div>
      </div>
      <div class="tool">
        <div class="title">
          使用设备
        </div>
        <div class="content">
          <!-- 灯 -->
          <div class="tool-item" style="background-color: rgba(207, 239, 245, 0.8);">
            <div class="left">
              <icon style="font-size: 20px; color: rgb(206, 101, 15);" class="iconfont icon-deng"></icon>
              灯
            </div>
            <div class="right">

            </div>
          </div>
          <!-- 风扇 -->
          <div class="tool-item" style="background-color: rgba(207, 245, 227, 0.8);">
            <div class="left">
              <icon style="font-size: 25px; color: rgb(206, 101, 15);" class="iconfont icon-fengshan1"></icon>
              风扇
            </div>
            <div class="right">

            </div>
          </div>
          <!-- 窗帘 -->
          <div class="tool-item" style="background-color: rgba(245, 207, 239, 0.8);">
            <div class="left">
              <icon style="font-size: 20px; color: rgb(206, 101, 15);" class="iconfont icon-chuanglian1"></icon>
              窗帘
            </div>
            <div class="right">

            </div>
          </div>
        </div>
      </div>
      <!-- 左下角数据预警次数 -->
      <div class="hengzhu">
            <!-- <div class="title">
              异常数据统计
            </div> -->
            <e-charts
              class="chart"
              :option="optionhengzhu"
            />
        </div>
        <!-- 右下角预警次数饼状图 -->
        <div class="chart-border-round">
          <e-charts class="chart" :option="optionbing" />
        </div>
    </div>
  </div>
</template>

<script>
import * as THREE from 'three'
import { GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader'
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls'

import BackButton from '@/components/BackButton.vue'
export default {
  name: 'SketchfabModelViewer',
  components: { BackButton },
  data () {
    return {
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
      }
    }
  },
  mounted () {
    // 初始化THREE.js对象为实例属性而非响应式数据
    this.scene = null
    this.camera = null
    this.renderer = null
    this.model = null
    this.controls = null
    
    this.initThree()
    this.loadModel()

    // 添加鼠标移动事件监听器
    this.$refs.threeContainer.addEventListener('mousemove', this.handleMouseMove)
    this.$refs.threeContainer.addEventListener('mouseleave', this.resetRotation)
  },

  beforeUnmount () {
    // 移除事件监听器
    this.$refs.threeContainer.removeEventListener('mousemove', this.handleMouseMove)
    this.$refs.threeContainer.removeEventListener('mouseleave', this.resetRotation)
    window.removeEventListener('resize', this.onWindowResize)

    // 组件销毁时清理 Three.js 资源
    if (this.renderer) {
      this.renderer.dispose()
    }
  },
  methods: {
    initThree () {
      // 1. 创建场景
      this.scene = new THREE.Scene()
      this.scene.background = new THREE.Color(0xe1f0d8)

      // 2. 创建相机
      this.camera = new THREE.PerspectiveCamera(
        75,
        this.$refs.threeContainer.clientWidth / this.$refs.threeContainer.clientHeight,
        0.1,
        1000
      )
      this.camera.position.set(0, -5, 4) // 从斜上方俯瞰
      this.camera.lookAt(0, 0, 0) // 确保相机看向场景中心

      // 3. 创建渲染器
      this.renderer = new THREE.WebGLRenderer({ antialias: true })
      this.renderer.setSize(
        this.$refs.threeContainer.clientWidth,
        this.$refs.threeContainer.clientHeight
      )
      this.$refs.threeContainer.appendChild(this.renderer.domElement)

      // 4. 添加控制器（鼠标交互）
      this.controls = new OrbitControls(this.camera, this.renderer.domElement)

      // 5. 添加灯光
      const ambientLight = new THREE.AmbientLight(0xffffff, 0.5)
      this.scene.add(ambientLight)

      const directionalLight = new THREE.DirectionalLight(0xffffff, 0.8)
      directionalLight.position.set(5, 10, 7)
      this.scene.add(directionalLight)

      // 6. 添加窗口大小变化监听
      window.addEventListener('resize', this.onWindowResize)
    },
    onWindowResize () {
      // 检查元素是否存在
      if (!this.$refs.threeContainer) return
      
      // 更新相机宽高比
      this.camera.aspect = this.$refs.threeContainer.clientWidth / this.$refs.threeContainer.clientHeight
      this.camera.updateProjectionMatrix()

      // 更新渲染器大小
      this.renderer.setSize(
        this.$refs.threeContainer.clientWidth,
        this.$refs.threeContainer.clientHeight
      )
    },
    loadModel () {
      const loader = new GLTFLoader()
      loader.load(
        // 模型路径（放在 public/models/）
        '/models/home.glb',
        (gltf) => {
          this.model = gltf.scene
          this.model.scale.set(0.4, 0.4, 0.4) // 调整模型初始大小为原来的0.3倍

          // 遍历模型所有子对象并检查材质
          this.model.traverse((child) => {
            if (child.isMesh) {
              console.log(child.name)
              // 如果模型有原始材质则保留，否则设置默认材质
              if (!child.material) {
                child.material = new THREE.MeshStandardMaterial({
                  color: 0xcccccc,
                  roughness: 0.7,
                  metalness: 0.1
                })
              }
              // 特殊处理玻璃材质
              if (child.name.includes('glass')) {
                child.material.transparent = true
                child.material.opacity = 0.8
              }
            }
          })

          this.scene.add(this.model)
          this.animate()
        },
        undefined,
        (error) => {
          console.error('模型加载失败:', error)
        }
      )
    },
    animate () {
      requestAnimationFrame(this.animate)
      this.renderer.render(this.scene, this.camera)
    },

    handleMouseMove (event) {
      if (!this.model) return

      // 计算鼠标移动距离并转换为旋转角度
      const movementX = event.movementX || event.mozMovementX || event.webkitMovementX || 0
      const rotationSpeed = 0.005 // 旋转速度系数
      this.model.rotation.z += movementX * rotationSpeed
    },

    resetRotation () {
      if (!this.model) return
      // 平滑重置旋转
      const targetRotation = { z: 0 }
      const duration = 1000 // 1秒内完成
      const startRotation = { z: this.model.rotation.z }
      const startTime = Date.now()

      const animateReset = () => {
        const elapsed = Date.now() - startTime
        const progress = Math.min(elapsed / duration, 1)

        // 使用缓动函数使动画更平滑
        this.model.rotation.z = startRotation.z + (targetRotation.z - startRotation.z) *
          (1 - Math.pow(1 - progress, 3))

        if (progress < 1) {
          requestAnimationFrame(animateReset)
        }
      }

      animateReset()
    }
  }
}
</script>

<style>
.ThreeD {
  height: 100vh;
  width: 100%;
  background: linear-gradient(135deg, #f5f9f1, #e1f0d8);
}
.three-container {
  width: 100%;
  height: 100%;
  margin: 0 auto;
  position: relative;
  background: linear-gradient(135deg, #f5f9f1, #e1f0d8);
}
.ThreeD .home-data {
  position: absolute;
  top: 10px;
  left: 20px;
  /* transform: translate(-50%, -50%); */
  color: white;
  font-size: 20px;
  position: absolute;
  height: 300px;
  width: 250px;
  background-color: rgba(255, 255, 255, 0.6); /* 添加80%不透明度（20%透明度） */
  color: rgb(45, 41, 41);
  border-radius: 10px;
  /* background-color: rgba(255, 248, 232, 0.8); */
    border-radius: 8px 50px 8px 50px;
    border: 2px solid #3A5F0B;
    background-color: rgba(225, 240, 216, 0.8);
}
.ThreeD .home-data .title{
  margin-top: 10px;
  margin-left: 10px;
  margin-bottom: 20px;
}
.ThreeD .home-data .data-item{
  margin-left: 10px;
  height: 40px;
  width: 220px;
  margin-bottom: 15px;
  border-radius: 10px;
  font-size: 15px;
  display: flex;
}
.ThreeD .home-data .data-item .left{
  height: 100%;
  width: 55px;
  margin-left: 10px;
  display: flex;
  align-items: center;
}

.ThreeD .home-data .data-item .right{
  height: 100%;
  width: 140px;
  display: flex;
  align-items: center;
  justify-content: end;
  padding-right: 15px;
  font-size: 20px;
}

.ThreeD .tool{
  height: 250px;
  width: 320px;
  background-color: rgba(225, 240, 216, 0.8);
  position: absolute;
  right: 20px;
  top: 10px;
  border-radius: 10px;
  /* background-color: rgba(255, 248, 232, 0.8); */
    border-radius: 50px 8px 50px 8px;
    border: 2px solid #3A5F0B;
    background-color: rgba(225, 240, 216, 0.8);
}
.ThreeD .tool .title{
  font-size: 20px;
  margin-top: 10px;
  margin-left: 10px;
  margin-bottom: 5px;
}
.ThreeD .tool .content{
  padding: 10px;
}
.ThreeD .tool .content .tool-item{
  margin-bottom: 20px;
  margin-left: 10px;
  margin-right: 10px;
  height: 40px;
  border-radius: 10px;
  display: flex;
}
.ThreeD .tool .content .tool-item .left{
  height: 100%;
  width: 65px;
  display: flex;
  align-items: center;
  margin-left: 15px;
  justify-content: space-around;
}
.ThreeD .hengzhu{
    height: 270px;
    width: 300px;
    border: 1px solid #e0e0e0;
    background-color: rgba(255, 255, 255, 0.6);
    /* border-radius: 8px; */
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    position: absolute;
    left: 10px;
    bottom: 20px;
    /* background-color: rgba(255, 248, 232, 0.8); */
    border-radius: 8px 50px 8px 50px;
    border: 2px solid #3A5F0B;
    background-color: rgba(225, 240, 216, 0.8);
  }
.ThreeD .chart-border-round {
width: 300px;
height: 300px;
padding: 15px;
/* background-color: rgba(255, 248, 232, 0.8); */
border-radius: 50px 8px 50px 8px;
border: 2px solid #3A5F0B;
position: absolute;
right: 10px;
bottom: 20px;
background-color: rgba(225, 240, 216, 0.8);
}

/* 返回首页按钮样式 */
.back-to-home {
  position: absolute;
  top: 20px;
  right: 20px;
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 10px 20px;
  background: linear-gradient(135deg, #1e5ca8, #3949ab);
  color: white;
  border: none;
  border-radius: 20px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  box-shadow: 0 4px 10px rgba(30, 92, 168, 0.3);
  transition: all 0.3s ease;
  z-index: 9999; /* 大幅提高z-index确保在所有元素之上 */
  border: 2px solid rgba(255, 255, 255, 0.8); /* 添加白色边框增强可见性 */
}

.back-to-home:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(30, 92, 168, 0.4);
  background: linear-gradient(135deg, #256dc1, #445ebd);
}

.back-to-home i {
  font-size: 16px;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .back-to-home {
    top: 10px;
    right: 10px;
    padding: 6px 12px;
    font-size: 12px;
  }
  
  .back-to-home i {
    font-size: 14px;
  }
}
</style>
