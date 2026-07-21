import { Tabbar, TabbarItem, NavBar, Dialog, Toast } from 'vant'

export default function installVant(app) {
  app.use(Tabbar)
  app.use(TabbarItem)
  app.use(NavBar)
  app.use(Dialog)
  // 将Toast挂载到全局属性上
  app.config.globalProperties.$toast = Toast
}

export { Toast }
