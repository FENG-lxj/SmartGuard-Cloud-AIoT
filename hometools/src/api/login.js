import request from '@/utils/request'

export const getPicCode = () => {
  return request.get('/captcha/generate')
}

export const codeLogin = (username, password, code, uuid) => {
  return request.post('/user/login', {
    username,
    password,
    code,
    uuid
  }).catch(error => {
    console.error('登录请求失败:', error)
    let errorMessage = '登录失败'

    if (error.response) {
      const { status, data } = error.response
      if (status === 400) {
        errorMessage = data.message || '请求参数错误'
      } else if (status === 401) {
        errorMessage = '用户名或密码错误'
      } else if (status === 500) {
        errorMessage = '服务器内部错误，请稍后再试'
      }
    } else if (error.request) {
      errorMessage = '网络错误，请检查网络连接'
    }

    throw new Error(errorMessage)
  })
}

export const codeRegister = (username, password, code, uuid) => {
  return request.post('/user/register', {
    username,
    password,
    code,
    uuid
  })
}