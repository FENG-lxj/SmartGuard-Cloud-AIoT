import { createRouter, createWebHistory } from 'vue-router'
import AI from '@/views/AI'
import Login from '@/views/login/final_index.vue'
import ErrorTest from '@/components/ErrorTest.vue'
import Main from '@/views/main'
import DataShow from '@/views/main/DataShow_cultural.vue'
import Warn from '@/views/main/Warn/Warn.vue'
import First from '@/views/main/First/First.vue'
import Register from '@/views/login/register.vue'
import Monitor from '@/views/video/monitor.vue'
import VideoTest from '@/views/video/VideoTest.vue'
import Home3D from '@/views/main/3D/3D.vue'
import Control from '@/views/main/Control/Control.vue'
import DataShowColored from '@/views/main/DataShow_colored.vue'
import FirstStyled from '@/views/main/First/First_styled.vue'
import WarnText from '@/views/main/Warn/Warn_text.vue'
import Talk from '@/views/AI/Talk.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'Main',
      component: Main
    },
    {
      path: '/ai',
      name: 'AI',
      component: AI
    },
    {
      path: '/login',
      name: 'Login',
      component: Login
    },
    {
      path: '/register',
      name: 'Register',
      component: Register
    },
    {
      path: '/control',
      name: 'Control',
      component: Control
    },
    {
      path: '/dataShow',
      name: 'DataShow',
      component: DataShow
    },
    {
      path: '/warn',
      name: 'Warn',
      component: Warn
    },
    {
      path: '/first',
      name: 'First',
      component: First
    },
    {
      path: '/video-test',
      name: 'VideoTest',
      component: VideoTest
    },
    {
      path: '/video/monitor',
      name: 'VideoMonitor',
      component: Monitor
    },
    {
      path: '/monitor',
      name: 'MonitorAlias',
      redirect: '/video/monitor'
    },
    {
      path: '/3D',
      name: '3D',
      component: Home3D
    },
    {
      path: '/fir',
      name: 'fir',
      component: FirstStyled
    },
    {
      path: '/war',
      name: 'war',
      component: WarnText
    },
    {
      path: '/talk',
      name: 'talk',
      component: Talk
    },
    {
      path: '/error-test',
      name: 'ErrorTest',
      component: ErrorTest
    },
    {
      path: '/data',
      name: 'data',
      component: DataShowColored
    }
  ]
})

export default router