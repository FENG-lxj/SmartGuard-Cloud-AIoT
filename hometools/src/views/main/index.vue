<template>
  <div class="first-index">
    <router-view></router-view>
    <van-tabbar
      v-model="active"
      active-color="#1e5ca8"
      inactive-color="#666"
      v-if="$route.path !== '/video/monitor' && $route.path !== '/monitor'"
      :ellipsis="false"
    >
      <van-tabbar-item replace to="/monitor" icon="icon-kongqizhiliang">监控</van-tabbar-item>
      <van-tabbar-item replace to="/ai" icon="icon-robot">AI助手</van-tabbar-item>
      <van-tabbar-item replace to="/dataShow" icon="icon-guangzhaoqiangdu">数据监控</van-tabbar-item>
      <van-tabbar-item replace to="/control" icon="icon-shidu">设备控制</van-tabbar-item>
      <van-tabbar-item replace to="/warn" icon="icon-jinggao">消息预警</van-tabbar-item>
      <van-tabbar-item replace to="/3D" icon="icon-jiance-guangzhaoqiangdu">3D视图</van-tabbar-item>
      <van-tabbar-item replace to="/first" icon="icon-tianjia">应用</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script>
export default {
  name: 'MainIndex',
  data() {
    return {
      active: 0
    }
  },
  async created() {
    if (this.$route.path !== '/monitor') {
      this.$router.push('/monitor')
    }
  }
}
</script>

<style scoped>
.first-index {
  padding-bottom: 46px;
  min-height: 100vh;
  position: relative;
}

.van-tabbar {
  position: fixed !important;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 46px;
  background: linear-gradient(to right, #ffffff, #e3f2fd);
  box-shadow: 0 -4px 12px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  border-top: 1px solid #ddd;
  transition: all 0.3s ease-in-out;
}

.van-tabbar-item {
  color: #607d8b;
  font-weight: 500;
  font-size: 18px;
  transition: color 0.3s;
  line-height: 46px;
}

.van-tabbar-item:hover {
  background-color: rgba(0, 0, 0, 0.03);
}

.van-tabbar-item--active {
  color: #1976d2 !important;
  background-color: rgba(25, 118, 210, 0.08);
  border-radius: 8px;
  margin: 2px 6px;
}
</style>