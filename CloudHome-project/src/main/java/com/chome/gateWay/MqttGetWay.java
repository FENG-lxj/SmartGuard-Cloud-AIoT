package com.chome.gateWay;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;

/**
 * @Description MQTT消息发送网关接口
 * 基于Spring Integration实现，无需手写接口实现类，直接注入调用即可发送MQTT消息
 * 统一封装两种发送方式：默认QoS、自定义QoS，自动将主题、QoS存入消息头，交由mqttOutboundChannel通道推送至MQTT服务端
 * @Date 2025/4/25
 * @DAY_NAME_FULL: 星期五
 * @Version 1.0
 */
// Spring Integration消息网关注解，自动生成接口代理实现类
// defaultRequestChannel：指定消息出站通道，所有发送消息统一投递到此通道
@MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
public interface MqttGetWay {
    /**
     * 发送MQTT消息，使用MQTT默认QoS等级
     * @param topic MQTT发布目标主题，通过@Header标记存入消息头MqttHeaders.TOPIC
     * @param payload 消息正文内容（字符串格式）
     */
    void sendToMqtt(@Header(MqttHeaders.TOPIC) String topic, String payload);

    /**
     * 自定义QoS等级发送MQTT消息，可控消息投递可靠性
     * @param topic MQTT发布目标主题
     * @param qos 消息投递质量等级：
     *            0：最多一次，消息可能丢失，无重试
     *            1：至少一次，保证送达，可能重复接收
     *            2：恰好一次，重传+去重，仅接收一次
     * @param payload 消息正文内容（字符串格式）
     */
    void sendToMqtt(@Header(MqttHeaders.TOPIC) String topic, @Header(MqttHeaders.QOS) int qos, String payload);
}