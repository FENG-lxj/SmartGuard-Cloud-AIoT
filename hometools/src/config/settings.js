export default {
  title: 'HomeTools',
  showSettings: true,
  tagsView: true,
  fixedHeader: false,
  sidebarLogo: true,
  environment: process.env.NODE_ENV || 'development',
  baseApi: process.env.VUE_APP_BASE_API || '/api'
}