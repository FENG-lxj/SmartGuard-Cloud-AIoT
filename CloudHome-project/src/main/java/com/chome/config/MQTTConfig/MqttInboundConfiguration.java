package com.chome.config.MQTTConfig;


import com.chome.domain.config.MqttConfigurationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;



@Configuration
@RequiredArgsConstructor
public class MqttInboundConfiguration {    //接收消息

    private final MqttConfigurationProperties mqttConfigurationProperties ;

    private final MqttPahoClientFactory mqttPahoClientFactory;

    /**
     * 配置消息传输通道
     */
    @Bean
    public MessageChannel mqttInboundChannel() {
        return new DirectChannel();
    }

    /**
     * 配置入站适配器：
     * 设置订阅的主题，以及客户端ID、客户端工厂、以及订阅的主题
     */
    @Bean
    public MessageProducer messageProducer() {
        MqttPahoMessageDrivenChannelAdapter adapter  =
                new MqttPahoMessageDrivenChannelAdapter(
                        mqttConfigurationProperties.getUrl(),
                        mqttConfigurationProperties.getSubClientId(),
                        mqttPahoClientFactory,
                        mqttConfigurationProperties.getSubTopic().split(","));
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(mqttInboundChannel());
        return adapter;
    }

}
