import { defineStore } from 'pinia'

export const useMainStore = defineStore('main', {
  state: () => ({
    AIPro: '',
    message: [
      { sender: 'other', content: '亲爱的住户，有什么可以帮助到你的吗？' },
      { sender: 'me', content: '如何提高家庭空气湿度' },
      { sender: 'other', content: '1. 最直接：使用加湿器 优点：快速、可控、适合大面积房间 注意：需定期清洁，避免细菌滋生。2. 低成本：自然蒸发法 优点：无需额外设备，适合临时加湿。3. 植物加湿法 优点：美化环境，同时调节湿度。4. 生活小妙招（临时应急）煮水法：烧一壶开水，让蒸汽自然扩散（可加柠檬/橙皮增香）浴室蒸汽法：用热水拖地，加速水分蒸发。' },
      { sender: 'me', content: '如何降低家庭有毒气体含量' },
      { sender: 'other', content: '通风是最经济有效的空气净化方式，但要注意科学通风。建议每天至少开窗通风2-3次，每次30分钟以上，最好能形成对流。在雾霾天气或冬季，可以配合使用新风系统或空气净化器。选择空气净化器时要重点关注CADR值和CCM值，针对甲醛等气态污染物要选择配备活性炭滤网的产品。某些特殊场合如新装修房屋，可以考虑使用专业的甲醛清除剂，但要注意选择无毒无害的产品。' },
      { sender: 'me', content: '火灾发生怎么办' },
      { sender: 'other', content: '逃生时的正确做法：测试门温：用手背轻触门把手，如果发烫不要开门（门外可能火势猛烈）选择其他出口或待援 防烟措施：用湿毛巾捂住口鼻（减少烟雾吸入）匍匐前进（烟雾上升，地面空气更清新）逃生路线：走楼梯，绝对不要乘电梯（可能断电或成烟囱）如果被困，尽量躲在有窗的房间，用湿布堵门缝，挥舞鲜艳衣物求救 报警与求救 拨打119时：清晰说明详细地址（如XX小区X栋X单元X楼）描述火势情况（如"厨房燃气起火，有爆炸危险"）留下联系方式，并派人到路口引导消防车' },
      { sender: 'me', content: '发生地震该怎么办' },
      { sender: 'other', content: '立即避险：若在室内，迅速躲到 坚固的桌子下方 或 承重墙角落，保护头部（如用枕头、手臂）。远离玻璃窗、吊灯、书架等易倒塌/坠落物。逃生或等待救援若需撤离：带上手机、应急包（建议常备门口），关闭电闸。智能门锁可远程为家人/救援人员临时授权开门。若被困：通过智能设备（如音箱、摄像头）发出求救声或闪光信号。敲击管道、墙壁发出声响（智能家居的声响监测可能捕捉到异常）' }
    ],
    AIid: '',
    hisMessage: [
      '如何提高家庭空气湿度',
      '如何降低家庭有毒气体含量',
      '火灾发生怎么办',
      '发生地震该怎么办'
    ],
    data: {
      wendu: '40',
      shidu: '20%',
      guangxian: '300',
      kongqi: '35',
      youdu: '0.2',
      dizhen: '2',
      huozai: '4',
      num: '0'
    },
    Warn: {
      wenduWarn: 0,
      shiduWarn: 0,
      guangxianWarn: 0,
      kongqiWarn: 0,
      youduWarn: 0,
      dizhenWarn: 0,
      huozaiWarn: 0
    },
    guest: {
      username: 'admin',
      telephone: '',
      address: '',
      youxiang: '',
      token: ''
    },
    dataPic: {
      kongqi: [150, 232, 201, 154, 190, 330, 410],
      wendu: [120, 132, 101, 134, 90, 230, 210],
      shidu: [220, 182, 191, 234, 290, 330, 310],
      guangxian: [82, 93, 90, 93, 129, 133, 132],
      youdu: [320, 332, 301, 334, 390, 330, 320],
      dizhen: [25.6, 76.7, 135.6, 162.2, 32.6, 20.0, 6.4],
      huozai: [21.6, 36.7, 135.6, 162.2, 32.6, 20.0, 6.4]
    },
    weather: [
      { tianqi: '晴天', wendu: '15℃', time: '08:00' },
      { tianqi: '晴天', wendu: '17℃', time: '09:00' },
      { tianqi: '晴天', wendu: '18℃', time: '10:00' },
      { tianqi: '晴天', wendu: '17℃', time: '11:00' },
      { tianqi: '晴天', wendu: '17℃', time: '11:00' },
      { tianqi: '晴天', wendu: '21℃', time: '12:00' }
    ],
    advance: ''
  }),
  getters: {},
  actions: {
    setInputValue(value) {
      this.AIPro = value
    },
    Setmessage(messages) {
      this.message.push(messages)
    },
    sethistory(history) {
      this.hisMessage = []
      if (!history || !Array.isArray(history)) return
      for (const session of history) {
        if (!session.messages) continue
        for (const msg of session.messages) {
          if (msg.role === 'user' && msg.content) {
            this.hisMessage.push(msg.content)
          }
        }
      }
    },
    setAIid(AIid) {
      this.AIid = AIid
    },
    setdata(data) {
      if (!data) return
      const trunc = (val) => val != null ? String(val).substring(0, 4) : '0'
      this.data.kongqi = trunc(data.airQuality)
      this.data.wendu = trunc(data.temperature)
      this.data.shidu = trunc(data.humidity)
      this.data.guangxian = trunc(data.illumination)
      this.data.youdu = trunc(data.gasQuality)
      this.data.dizhen = trunc(data.earthquakeIndex)
      this.data.huozai = trunc(data.fireIndex)
      this.data.num = String(parseFloat(this.data.huozai) + parseFloat(this.data.dizhen)).substring(0, 1)
    },
    setGuest(guest) {
      if (guest.username) this.guest.username = guest.username
      if (guest.token) this.guest.token = guest.token
    },
    setDataPic(dataPic) {
      if (!dataPic) return
      const envKeys = ['kongqi', 'wendu', 'shidu', 'guangxian', 'youdu']
      const envValues = [dataPic.airQuality, dataPic.temperature, dataPic.humidity, dataPic.illumination, dataPic.gasQuality]

      if (this.dataPic.kongqi.length > 6) {
        envKeys.forEach((key) => this.dataPic[key].shift())
        envKeys.forEach((key, i) => this.dataPic[key].push(envValues[i]))
      }

      if (this.dataPic.dizhen.length > 7) {
        this.dataPic.dizhen.shift()
        this.dataPic.huozai.shift()
        this.dataPic.dizhen.push(dataPic.earthquakeIndex)
        this.dataPic.huozai.push(dataPic.fireIndex)
      }
    },
    setadvance(advance) {
      this.advance = advance
    },
    setWarn(warn) {
      this.Warn = warn
    },
    setEnvir() {
      // 环境预警方法预留
    }
  }
})