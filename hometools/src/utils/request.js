import axios from 'axios'
import { showError as showCustomError } from './errorToast.js'
import { useMainStore } from '@/store/index.js'

const HTTP_ERROR_MAP = {
  400: '请求参数错误',
  401: '未授权访问，请先登录',
  403: '权限不足，无法访问该资源',
  404: '请求资源不存在',
  500: '服务器内部错误，请稍后重试',
  502: '网关错误，请稍后重试',
  503: '服务不可用，请稍后重试'
}

function handleHttpError(error) {
  if (!error.response) {
    return error.code === 'ECONNABORTED' ? '请求超时，请稍后重试' : '网络连接错误，请检查网络后重试'
  }
  const status = error.response.status
  return HTTP_ERROR_MAP[status]
    ? `${HTTP_ERROR_MAP[status]}: ${error.response.data?.message || ''}`
    : `请求失败 (状态码: ${status})`
}

const instance = axios.create({
  baseURL: 'http://localhost:8023',
  timeout: 10000,
  headers: { platform: 'H5' }
})

instance.interceptors.request.use(
  (config) => {
    const store = useMainStore()
    const token = store.guest?.token
    if (token) {
      config.headers.Authorization = 'CHome ' + token
    }
    return config
  },
  (error) => {
    const errorMsg = handleHttpError(error)
    showCustomError(errorMsg, 5000)
    return Promise.reject(new Error(errorMsg))
  }
)

instance.interceptors.response.use(
  (response) => {
    if (response.data instanceof Blob) {
      return response
    }
    const res = response.data
    if (res.status !== undefined && res.status !== 200) {
      const errorMsg = res.message || `业务处理失败 (错误码: ${res.status})`
      showCustomError(errorMsg, 5000)
      return Promise.reject(new Error(errorMsg))
    }
    return res
  },
  (error) => {
    const errorMsg = handleHttpError(error)
    showCustomError(errorMsg, 5000)
    return Promise.reject(new Error(errorMsg))
  }
)

export default instance