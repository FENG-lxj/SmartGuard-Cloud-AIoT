<template>
  <div class="message-box-wrapper">
    <el-alert
      title="摔倒警告"
      type="warning"
      effect="dark"
      show-icon
      :closable="true"
    >
      <template #description>
        请小心行走，避免发生意外事故。
      </template>
    </el-alert>
  </div>
</template>

<style scoped>
.message-box-wrapper {
  display: flex;
  justify-content: center;
  width: 100%;
  margin-top: 24px;
  animation: fadeInUp 0.4s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.el-alert {
  width: 600px;
  max-width: 95%;
  border-radius: 16px;
  box-shadow: 0 6px 20px rgba(255, 183, 0, 0.18);
  border: none;
  overflow: hidden;
  position: relative;
  padding: 16px 20px 16px 56px;
  background: linear-gradient(135deg, #fffdf8 0%, #fff9e6 100%);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.el-alert:hover {
  box-shadow: 0 8px 24px rgba(255, 183, 0, 0.25);
  transform: translateY(-1px);
}

.el-alert__title {
  font-size: 18px;
  font-weight: 700;
  color: #b35c00;
  margin-bottom: 8px;
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  letter-spacing: 0.02em;
}

.el-alert__description {
  font-size: 15px;
  color: #995500;
  line-height: 1.6;
  margin: 0;
}

.el-alert__closebtn {
  font-size: 20px;
  color: #d97706;
  opacity: 0.7;
  transition: all 0.3s ease;
  padding: 8px;
  border-radius: 50%;
}

.el-alert__closebtn:hover {
  transform: scale(1.2);
  opacity: 1;
  background-color: rgba(255, 183, 0, 0.1);
}

@media (max-width: 768px) {
  .el-alert {
    width: 100%;
    padding: 14px 16px 14px 52px;
  }
  .el-alert__title {
    font-size: 16px;
  }
  .el-alert__description {
    font-size: 14px;
  }
}

@media (max-width: 480px) {
  .el-alert {
    padding: 12px 14px 12px 48px;
    border-radius: 14px;
  }
  .el-alert__title {
    font-size: 15px;
    margin-bottom: 6px;
  }
  .el-alert__description {
    font-size: 13px;
  }
}
</style>