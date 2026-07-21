<template>
  <div class="error-test-container">
    <h3>全局异常处理器测试</h3>
    <div class="test-buttons">
      <button @click="test404Error" class="test-btn">测试404错误</button>
      <button @click="test500Error" class="test-btn">测试500错误</button>
      <button @click="testNetworkError" class="test-btn">测试网络错误</button>
      <button @click="testBusinessError" class="test-btn">测试业务错误</button>
    </div>
  </div>
</template>

<script setup>
import request from '../utils/request'

async function test404Error() {
  try {
    await request.get('/api/nonexistent-endpoint')
  } catch (error) {
    console.log('捕获到404错误:', error)
  }
}

async function test500Error() {
  try {
    await request.get('/api/trigger-500')
  } catch (error) {
    console.log('捕获到500错误:', error)
  }
}

async function testNetworkError() {
  try {
    const instance = request.create({
      baseURL: 'http://nonexistent-domain-123456.com',
      timeout: 2000
    })
    await instance.get('/api/test')
  } catch (error) {
    console.log('捕获到网络错误:', error)
  }
}

async function testBusinessError() {
  try {
    await request.post('/api/business-error', { invalidParam: true })
  } catch (error) {
    console.log('捕获到业务错误:', error)
  }
}
</script>

<style scoped>
.error-test-container {
  padding: 20px;
  border-radius: 8px;
  background-color: #f5f5f5;
  margin: 20px;
}

h3 {
  margin-bottom: 20px;
  color: #333;
}

.test-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.test-btn {
  padding: 10px 15px;
  background-color: #165DFF;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.test-btn:hover {
  background-color: #0E42D2;
}
</style>