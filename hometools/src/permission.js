import router from './router'
import { useMainStore } from './store'

const WHITE_LIST = ['/login', '/auth-redirect']

export default function setupPermission() {
  router.beforeEach(async (to, from, next) => {
    document.title = to.meta.title || 'HomeTools'

    const store = useMainStore()
    const hasToken = store.guest.token

    if (hasToken) {
      next(to.path === '/login' ? { path: '/' } : undefined)
    } else {
      next(WHITE_LIST.includes(to.path) ? undefined : `/login?redirect=${to.path}`)
    }
  })
}