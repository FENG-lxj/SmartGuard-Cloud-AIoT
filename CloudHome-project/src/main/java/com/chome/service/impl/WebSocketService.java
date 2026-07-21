package com.chome.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.chome.domain.dto.AlarmDTO;
import com.chome.domain.dto.FurnitureDTO;
import com.chome.domain.entity.AlarmMessage;
import com.chome.domain.vo.InfluxEnvironment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description websocket服务
 * @Date 2025/4/13
 * @DAY_NAME_FULL: 星期日
 * @Version 1.0
 */

@RequiredArgsConstructor
@Service
@Slf4j
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 推送设备接入提醒到前端
     *
     * @param furnitureDTO 新设备信息
     */
    public void sendNewDeviceAlert(FurnitureDTO furnitureDTO) {
        Map<String, Object> message = new HashMap<>();
        message.put("msg", "发现新的设备可连接！");
        message.put("deviceId", furnitureDTO.getDeviceId());
        message.put("deviceName", furnitureDTO.getFurnitureName());
        message.put("deviceType", furnitureDTO.getFurnitureType());
        message.put("onlineTime", System.currentTimeMillis());
        // 发送到前端订阅的主题
        messagingTemplate.convertAndSend("/topic/deviceOnline", message);
    }

    /**
     * 推送环境数据到前端
     *
     * @param environmentData 环境数据
     */
    public void sendEnvironmentData(List<InfluxEnvironment> environmentData) {
        // 发送到前端订阅的主题
        messagingTemplate.convertAndSend("/topic/environment", environmentData);
    }

    /**
     * 推送告警信息到前端
     *
     * @param alarmMessage 告警信息
     */
    public void sendAlarmData(AlarmMessage alarmMessage) {
        AlarmDTO alarmDTO = BeanUtil.copyProperties(alarmMessage, AlarmDTO.class);
        //TODO:发送邮件
        messagingTemplate.convertAndSend("/topic/Alarm", alarmDTO);
    }
}