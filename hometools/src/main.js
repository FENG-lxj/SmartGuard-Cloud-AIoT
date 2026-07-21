import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'
import installVant from '@/plugins/vant-ui'
import ECharts from 'vue-echarts'
import 'echarts'
import ControlIcon from './assets/icon/Control/index.vue'
import DataShowIcon from './assets/icon/dataShow/index.vue'
import FirstIcon from './assets/icon/first/index.vue'
import WeatherIcon from './assets/icon/weather/index.vue'
import SvgIcon from './assets/icon/SvgIcon.vue'
import GoodBtn from '@/views/main/Control/Btn.vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import './assets/tailwind.css'

const app = createApp(App)

// 注册全局组件
app.component('e-charts', ECharts)
app.component('ControlIcon', ControlIcon)
app.component('DataShowIcon', DataShowIcon)
app.component('FirstIcon', FirstIcon)
app.component('WeatherIcon', WeatherIcon)
app.component('SvgIcon', SvgIcon)
app.component('GoodBtn', GoodBtn)

// 注册插件
installVant(app)
app.use(ElementPlus)
app.use(router)
app.use(createPinia())

app.mount('#app')
