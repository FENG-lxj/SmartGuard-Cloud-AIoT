package com.chome.config.MQTTConfig;

import com.chome.domain.config.MqttConfigurationProperties;
import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;


@Configuration
@RequiredArgsConstructor
public class MqttConfiguration {

    private final MqttConfigurationProperties mqttConfigurationProperties;

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        // 创建客户端工厂
        DefaultMqttPahoClientFactory mqttPahoClientFactory = new DefaultMqttPahoClientFactory();
        // 创建MqttConnectOptions对象
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true); //建立新的连接
        //读取实体类中的配置
        options.setUserName(mqttConfigurationProperties.getUsername());   //用户名
        options.setPassword(mqttConfigurationProperties.getPassword().toCharArray());  //密码
        options.setServerURIs(new String[]{mqttConfigurationProperties.getUrl()});   //Url
        mqttPahoClientFactory.setConnectionOptions(options);   //设置连接参数
        // 返回
        return mqttPahoClientFactory;
    }

}
