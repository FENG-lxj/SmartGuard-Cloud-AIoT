<template>
    <i class="'icon' + name"></i>
</template>

<script>
export default {
  name: 'FirstIcon',
  props: {
    name: String
  }
}
</script>

<style>
@import url('./iconfont.css');
</style>
