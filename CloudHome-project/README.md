# CloudHome 智能家居项目

## 项目简介

CloudHome 是一个基于 Spring Boot 3.4 的智能家居后端管理系统，集成了物联网设备控制、环境监测、AI对话、天气查询等核心功能。项目采用现代化的技术架构，支持 MQTT 设备通信、InfluxDB 时序数据存储、Redis 缓存等企业级特性。

## 技术栈

### 核心框架
- **Spring Boot 3.4.3** - 主要应用框架
- **Java 17** - 运行环境
- **Maven 3.8+** - 项目构建工具

### 数据存储
- **MySQL 8.0+** - 关系型数据库
- **Redis 6.0+** - 缓存和会话存储
- **MongoDB 4.4+** - 聊天记录和对话历史存储
- **InfluxDB 2.x** - 环境数据时序存储

### 消息通信
- **MQTT 3.1.1** - 物联网设备通信协议
- **WebSocket** - 实时双向通信

### AI 和第三方服务
- **Spring AI 1.0.1** - AI 集成框架
- **DeepSeek API** - 大语言模型服务
- **Coze Bot** - 智能对话机器人
- **高德天气 API** - 天气信息服务
- **阿里云 OSS** - 对象存储服务
- **讯飞星火 ASR/TTS** - 语音识别和合成

### 其他组件
- **MyBatis-Plus 3.5.7** - ORM 框架
- **JWT 0.12.6** - 用户认证
- **Lombok** - 代码简化工具
- **Hutool 5.8.24** - 工具类库

## 系统架构

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   IoT Devices   │────│   MQTT Broker   │────│  CloudHome App  │
│  (智能家居设备)  │    │   (消息代理)     │    │   (后端服务)     │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                                              │
        ┌─────────────┬─────────────┬─────────────┬─────────────┐
        │             │             │             │             │
   ┌────▼────┐   ┌───▼────┐   ┌────▼────┐   ┌────▼────┐   ┌───▼────┐
   │  MySQL  │   │ Redis  │    │MongoDB  │   │InfluxDB │    │第三方API│
   │(用户数据)│    │(缓存)  │    │(聊天记录)│    │(环境数据)│    │(AI/天气)│
   └─────────┘   └────────┘    └─────────┘   └─────────┘    └────────┘
```

## 功能模块

### 🔧 核心功能
- **用户管理**: 注册、登录、权限控制
- **设备控制**: 家具设备远程控制和状态监控
- **环境监测**: 温湿度、空气质量等环境数据采集
- **报警系统**: 异常情况实时告警和通知
- **AI 对话**: 智能语音助手交互
- **天气查询**: 实时天气信息获取

### 📱 接口服务
- RESTful API 接口
- WebSocket 实时通信
- MQTT 设备消息处理

## 环境配置要求

### 基础开发环境

#### 操作系统
- Windows 10/11 (推荐)
- Linux (Ubuntu 20.04+)
- macOS 12+

#### 开发工具
- **IDE**: IntelliJ IDEA 2024.3+
- **JDK**: OpenJDK 17 或 Oracle JDK 17
- **构建工具**: Apache Maven 3.8+
- **版本控制**: Git 2.30+

#### 环境变量配置
```bash
# Java 环境
export JAVA_HOME=/path/to/jdk-17
export PATH=$JAVA_HOME/bin:$PATH

# Maven 环境
export MAVEN_HOME=/path/to/maven
export PATH=$MAVEN_HOME/bin:$PATH
```

### 必需中间件服务

#### 1. 数据库服务

**MySQL 8.0+**
```sql
-- 创建数据库
CREATE DATABASE `WiseGuardCloudHome-Project` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

**启动方式**:
- Windows: 通过 MySQL Installer 或 XAMPP
- Linux: `sudo systemctl start mysql`
- Docker: `docker run -p 3306:3306 --name mysql -e MYSQL_ROOT_PASSWORD=123 -d mysql:8.0`

#### 2. 缓存服务

**Redis 6.0+**
```bash
# 启动 Redis
redis-server

# 测试连接
redis-cli ping  # 应返回 PONG
```

**Docker 启动**:
```bash
docker run -p 6379:6379 --name redis -d redis:6.2-alpine
```

#### 3. 文档数据库

**MongoDB 4.4+**
```bash
# 启动 MongoDB
mongod --dbpath /path/to/db

# 或使用 Docker
docker run -p 27017:27017 --name mongodb -d mongo:4.4
```

#### 4. 时序数据库

**InfluxDB 2.x**
```bash
# 下载并启动 InfluxDB
# 访问 http://localhost:8086 进行初始配置

# 创建组织和 Bucket
influx setup \
  --org CHome \
  --bucket CHome_Environment \
  --username admin \
  --password your_password \
  --retention 0 \
  --token kTCnhx0uHOOKaRNV9H0AU3d2kuDXcM4bS3a_59gnbPkHrUd52rPYwK2rJ_cl3leGrC3SlU0-v6Os0MhdmyXYnA==
```

#### 5. 消息代理

**MQTT Broker (Mosquitto/Eclipse)**
```bash
# 安装 Mosquitto
# Ubuntu: sudo apt-get install mosquitto mosquitto-clients
# Windows: 下载 Mosquitto 安装包

# 启动服务
mosquitto -c /etc/mosquitto/mosquitto.conf

# 测试连接
mosquitto_sub -h localhost -t "test/topic" -u admin -P THbxmh5119824
```

**Docker 启动**:
```bash
docker run -d \
  --name mosquitto \
  -p 1883:1883 \
  -p 9001:9001 \
  -v /mosquitto/config:/mosquitto/config \
  -v /mosquitto/data:/mosquitto/data \
  -v /mosquitto/log:/mosquitto/log \
  eclipse-mosquitto:2.0
```

## 项目配置

### application.yml 核心配置

```yaml
server:
  port: 8023
  address: 0.0.0.0

spring:
  # 数据库配置
  datasource:
    url: jdbc:mysql://localhost:3306/WiseGuardCloudHome-Project?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: root
    password: 123
  
  # Redis 配置
  data:
    redis:
      host: localhost
      port: 6379
      database: 0
    
    # MongoDB 配置
    mongodb:
      uri: mongodb://localhost:27017/CHome_chat_memory
  
  # InfluxDB 配置
  influx:
    token: your_influx_token
    org: CHome
    bucket: CHome_Environment
    url: http://localhost:8086
  
  # MQTT 配置
  mqtt:
    username: admin
    password: your_mqtt_password
    url: tcp://localhost:1883
```

### 第三方服务配置

在 `application.yml` 中配置以下第三方服务密钥：

```yaml
# AI 服务配置
spring:
  ai:
    openai:
      api-key: your_deepseek_api_key
      base-url: https://api.deepseek.com

# 其他第三方配置
thirdparty:
  spark:  # 讯飞星火
    ai:
      appid: your_appid
      appsecret: your_appsecret
      appkey: your_appkey
  
  coze:   # Coze 机器人
    token: your_coze_token
    botId: your_bot_id
  
  gaode:  # 高德地图
    key: your_gaode_key
  
  aliyun:
    oss:    # 阿里云 OSS
      endpoint: oss-cn-beijing.aliyuncs.com
      access-key-id: your_access_key_id
      access-key-secret: your_access_key_secret
```

## 快速开始

### 1. 克隆项目
```bash
git clone <repository-url>
cd CloudHome-project
```

### 2. 初始化数据库
```bash
# 执行 SQL 脚本创建表结构
mysql -u root -p WiseGuardCloudHome-Project < init.sql
```

### 3. 启动中间件服务
```bash
# 按顺序启动各服务
redis-server &
mongod &
influxd &
mosquitto &
```

### 4. 构建和运行
```bash
# 清理并编译项目
mvn clean compile

# 运行测试
mvn test

# 打包应用
mvn package

# 运行应用
java -jar target/CloudHome-project-0.1.0.jar

# 或者直接运行
mvn spring-boot:run
```

### 5. 验证服务
访问以下地址验证服务是否正常运行：
- 应用主页: http://localhost:8023
- 健康检查: http://localhost:8023/actuator/health
- API 文档: http://localhost:8023/swagger-ui.html (如果集成了 Swagger)

## API 接口文档

### 主要接口列表

| 接口 | 方法 | 路径 | 描述 |
|------|------|------|------|
| 用户注册 | POST | `/api/user/register` | 用户账号注册 |
| 用户登录 | POST | `/api/user/login` | 用户身份认证 |
| 设备控制 | POST | `/api/furniture/control` | 家具设备远程控制 |
| 环境数据 | GET | `/api/alarm/environment` | 获取环境监测数据 |
| AI 对话 | POST | `/api/ai/chat` | 与 AI 助手对话 |
| 天气查询 | GET | `/api/weather/current` | 查询当前天气信息 |

### 请求示例
```bash
# 用户登录
curl -X POST http://localhost:8023/api/user/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "123456"}'

# 设备控制
curl -X POST http://localhost:8023/api/furniture/control \
  -H "Authorization: Bearer your_jwt_token" \
  -H "Content-Type: application/json" \
  -d '{"deviceId": "lamp001", "action": "on"}'
```
## 故障排除

### 常见问题

**1. 数据库连接失败**
- 检查 MySQL 服务是否启动
- 验证数据库连接配置
- 确认防火墙端口开放

**2. Redis 连接超时**
- 检查 Redis 服务状态
- 验证网络连通性
- 查看 Redis 内存使用情况

**3. MQTT 连接异常**
- 确认 MQTT Broker 地址和端口
- 验证用户名密码正确性
- 检查防火墙设置

**4. InfluxDB 写入失败**
- 验证 Token 权限
- 检查 Bucket 是否存在
- 确认时间戳格式正确

**注意**: 请根据实际项目情况修改配置信息和服务地址，并确保敏感信息不被提交到版本控制系统中。