<template>
  <div class="warn-page">
    <div class="header-bar">
      <h1>预警数据总览</h1>
    </div>

    <div class="warn-panels">
      <div class="warn-card temperature">温度异常</div>
      <div class="warn-card humidity">湿度异常</div>
      <div class="warn-card light">光照强度</div>
      <div class="warn-card air">空气质量</div>
      <div class="warn-card gas">毒气含量</div>
    </div>

    <div class="charts-section">
      <div class="chart-card">⚠ 灾难指数图表区</div>
      <div class="chart-card">⚠ 环境指数图表区</div>
    </div>

    <div class="bottom-section">
      <div class="summary-box">
        <p><strong>今日数据</strong></p>
        <div class="summary-item">温度：40</div>
        <div class="summary-item">湿度：20%</div>
        <div class="summary-item">光照：300</div>
      </div>

      <div class="summary-box">
        <p><strong>警告统计</strong></p>
        <div class="summary-item">空气质量：35</div>
        <div class="summary-item">毒气含量：0.2</div>
        <div class="summary-item">火灾指数：4</div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.warn-page {
  background-color: #fdf6e3;
  padding: 20px;
  font-family: "Helvetica Neue", sans-serif;
  color: #333;
}

.header-bar {
  background: linear-gradient(to right, #2979ff, #00c4ff);
  padding: 15px;
  color: white;
  text-align: center;
  border-radius: 12px;
  margin-bottom: 20px;
}

.warn-panels {
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
  margin-bottom: 20px;
}

.warn-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
  width: 18%;
  height: 80px;
  text-align: center;
  line-height: 80px;
  transition: transform 0.3s ease;
  font-weight: bold;
}

.warn-card:hover {
  transform: scale(1.05);
  background-color: #f1faff;
}

.warn-card.temperature { color: #e53935; }
.warn-card.humidity { color: #039be5; }
.warn-card.light { color: #fdd835; }
.warn-card.air { color: #66bb6a; }
.warn-card.gas { color: #8e24aa; }

.charts-section {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.chart-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
  width: 48%;
  height: 260px;
  text-align: center;
  line-height: 260px;
  color: #444;
  transition: background 0.3s ease;
}

.chart-card:hover {
  background: #f0f7ff;
}

.bottom-section {
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
}

.summary-box {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  width: 48%;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.summary-box p {
  font-size: 18px;
  margin-bottom: 10px;
  color: #2979ff;
}

.summary-item {
  font-size: 16px;
  line-height: 1.8;
  color: #555;
}
</style>
