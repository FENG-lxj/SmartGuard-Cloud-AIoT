package com.chome.handler;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.chome.domain.dto.FurnitureOnlineDTO;
import com.chome.domain.dto.FurniturePowerDTO;
import com.chome.domain.entity.AlarmMessage;
import com.chome.service.IAlarmService;
import com.chome.service.IFurnitureService;
import com.chome.service.IPowerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @Description MQTT上行消息统一消费处理器
 * 基于Spring Integration消息通道机制，分流处理硬件上报的三类MQTT报文：
 * 1. onlineChannel：设备上下线状态消息，更新设备在线状态
 * 2. functionStatusChannel：设备功能状态变更（开关/亮度/温度等），同步数据库或缓存Redis
 * 3. cameraAlarmChannel：摄像头AI跌倒告警消息，持久化告警记录并推送前端
 * @Date 2025/4/25
 * @DAY_NAME_FULL: 星期五
 * @Version 1.0
 */
// 注册为Spring组件，容器自动管理
@Component
// 构造注入所有final业务服务，替代@Autowired
@RequiredArgsConstructor
// 自动生成日志对象，打印MQTT报文与异常信息
@Slf4j
public class ReceiverMessageHandler {
    // 家居设备基础业务服务
    private final IFurnitureService furnitureService;
    // 设备功能状态（灯光、温控、开关）业务服务
    private final IPowerService powerService;
    // 告警记录持久化服务
    private final IAlarmService alarmService;

    /**
     * 监听onlineChannel通道，处理设备上线MQTT消息
     * 硬件上电后推送上线报文，更新数据库设备在线标识、上线时间
     * @param message Spring Integration消息载体，payload为MQTT原始JSON字符串
     */
    @ServiceActivator(inputChannel = "onlineChannel")
    public void onlineMessage(Message<?> message) {
        log.info("\n检测到设备上线：{}", message.getPayload());
        // 取出MQTT原始报文
        String payload = message.getPayload().toString();
        // JSON字符串转为设备上线传输对象
        FurnitureOnlineDTO furnitureOnline = JSON.parseObject(payload, FurnitureOnlineDTO.class);
        try {
            // 执行设备在线状态更新逻辑
            furnitureService.onlineUpdate(furnitureOnline);
        } catch (Exception e) {
            // 捕获业务异常，单条消息失败不阻塞整条消息通道
            log.error("业务异常：{}", e.getMessage());
        }
    }

    /**
     * 监听functionStatusChannel通道，处理设备功能状态变更消息
     * 硬件上报功能数值变化，分两种处理逻辑：
     * 1. 数据库无该设备：缓存状态至Redis，设备上线后再同步入库，避免离线状态丢失
     * 2. 数据库存在该设备：直接更新设备功能最新状态
     * @param message MQTT状态变更报文消息
     */
    @ServiceActivator(inputChannel = "functionStatusChannel")
    public void functionStatusMessage(Message<?> message) {
        log.info("\n检测到设备功能状态变化：{}", message.getPayload());
        String string = message.getPayload().toString();
        FurniturePowerDTO powerDTO = JSON.parseObject(string, FurniturePowerDTO.class);
        // 判断设备是否已录入数据库
        if (!furnitureService.isDeviceExists(powerDTO.getDeviceId())) {
            powerDTO.setUpdateTime(LocalDateTime.now());
            // 设备未注册，临时缓存到Redis
            powerService.cacheUnregisteredPower(powerDTO);
            return;
        }
        powerDTO.setUpdateTime(LocalDateTime.now());
        // 设备已存在，更新数据库功能状态
        powerService.updateFurniturePower(powerDTO);
    }

    /**
     * 监听cameraAlarmChannel通道，处理摄像头跌倒告警消息
     * 摄像头AI识别人体跌倒后推送MQTT告警，解析报文并持久化告警记录
     * @param message 摄像头告警原始报文
     */
    @ServiceActivator(inputChannel = "cameraAlarmChannel")
    public void cameraAlarmMessage(Message<?> message) {
        log.info("识别到跌倒：{}", message.getPayload());
        String payload = message.getPayload().toString();
        // 将报文数据转为告警实体类
        AlarmMessage alarmMessage = BeanUtil.copyProperties(payload, AlarmMessage.class);
        // 保存告警信息到数据库，同时通过STOMP推送前端实时弹窗
        alarmService.saveMessage(alarmMessage);
    }
}