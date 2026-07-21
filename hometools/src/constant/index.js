export const API_URL = {
  LOGIN: '/api/login',
  REGISTER: '/api/register',
  CAPTCHA: '/api/captcha/generate',
  USER_INFO: '/api/user/info'
};

export const REGEX = {
  PHONE: /^1[3-9]\d{9}$/,
  USERNAME: /^[a-zA-Z0-9_]{4,16}$/,
  PASSWORD: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[^]{8,16}$/,
  CODE: /^\w{4}$/
};

export const PAGE_SIZE = {
  DEFAULT: 10,
  SMALL: 5,
  LARGE: 20
};

export const STATUS_CODE = {
  SUCCESS: 200,
  ERROR: 500,
  UNAUTHORIZED: 401,
  FORBIDDEN: 403
};