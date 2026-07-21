package com.chome.config.MQTTConfig;

import com.chome.domain.config.MqttConfigurationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;


@Configuration
@RequiredArgsConstructor
public class MqttOutboundConfiguration {

    private final MqttConfigurationProperties mqttConfigurationProperties;

    private final MqttPahoClientFactory pahoClientFactory;

    @Bean
    public MessageChannel mqttOutboundChannel() {    //消息发送通道
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutboundMassageHandler() {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(
                mqttConfigurationProperties.getUrl(),
                mqttConfigurationProperties.getPubClientId(),
                pahoClientFactory);  //发送
        messageHandler.setAsync(true);  //异步发送
        messageHandler.setDefaultQos(0); //默认消息等级
        messageHandler.setDefaultTopic("default");  //默认主题
        return messageHandler;
    }
}
