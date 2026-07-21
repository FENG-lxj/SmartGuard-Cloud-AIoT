<template>
    <div @click="$emit('toggle')" :class="status ? 'Btng' : 'Btnk'">
        <div :class="status ? 'btn-right' : 'btn-left'">
            <icon style="font-size: 30px;" class="iconfont icon-yuan1" :class="status ? 'kai' : 'guan'"></icon>
        </div>
    </div>
  </template>

<script>
export default {
  name: 'goodBtn',

  props: {
    status: {
      type: Boolean,
      required: true
    }
  },

  data () {
    return {
      swi: false
    }
  },
  methods: {
    btn () {
      this.swi = !this.swi
      // this.$emit('btn', this.swi)
    }
  }
}
</script>

  <style>
  .Btnk {
    width: 70px;
    height: 30px;
    border-radius: 20px;
    background-color: #fff;
    position: relative;
    box-shadow: 1px 1px 5px rgba(0, 0, 0, 0.1);
    transition: transform 0.3s ease, box-shadow 0.3s ease;
  }
  .Btnk:hover {
    transform: scale(1.05);
    box-shadow: 2px 4px 12px rgba(0, 0, 0, 0.2);
  }

  .Btng {
    width: 70px;
    height: 30px;
    border-radius: 20px;
    background-color: #fff;
    position: relative;
    box-shadow: 1px 1px 5px rgba(0, 0, 0, 0.1);
    transition: transform 0.3s ease, box-shadow 0.3s ease;
  }
  .Btng:hover {
    transform: scale(1.05);
    box-shadow: 2px 4px 12px rgba(0, 0, 0, 0.2);
  }

  .btn-left{
    position: absolute;
    left: 0;
  }
  .btn-right{
    position: absolute;
    right: 0;
  }
  .guan{
    color: rgb(9, 7, 57);
  }
  .kai{
    color: rgb(28, 150, 93);
  }
  </style>
