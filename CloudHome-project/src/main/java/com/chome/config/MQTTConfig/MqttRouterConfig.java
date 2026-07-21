package com.chome.config.MQTTConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.Router;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.router.AbstractMappingMessageRouter;
import org.springframework.integration.router.HeaderValueRouter;
import org.springframework.messaging.MessageChannel;

import static com.chome.constants.MQTTConstants.*;


@Configuration
public class MqttRouterConfig {

    @Bean
    @Router(inputChannel = "mqttInboundChannel")
    public AbstractMappingMessageRouter topicRouter() {
        // 根据消息头中的主题路由
        HeaderValueRouter router = new HeaderValueRouter("mqtt_receivedTopic");
        router.setChannelMapping(MQTT_ONLINE_TOPIC, "onlineChannel");
        router.setChannelMapping(MQTT_ALARM_TOPIC, "cameraAlarmChannel");
        router.setChannelMapping(MQTT_FUNCTION_STATUS_TOPIC, "functionStatusChannel");
        return router;
    }

    @Bean
    public MessageChannel onlineChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel functionStatusChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel cameraAlarmChannel() {
        return new DirectChannel();
    }

}