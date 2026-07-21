package com.chome.domain.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Description MQTT配置属性
 * @Date 2025/4/18
 * @DAY_NAME_FULL: 星期五
 * @Version 1.0
 */

@Data
@Component
@ConfigurationProperties(prefix = "spring.mqtt")
public class MqttConfigurationProperties {
    /**
     * MQTT 连接用户名
     */
    private String username;
    /**
     * MQTT 连接密码
     */
    private String password;
    /**
     * MQTT 服务器地址和端口，使用 TCP 协议
     */
    private String url;
    /**
     * 订阅客户端 ID，用于标识订阅消息的客户端
     */
    private String subClientId ;
    /**
     * 订阅的主题，客户端将监听该主题的消息
     */
    private String subTopic ;
    /**
     * 发布客户端 ID，用于标识发布消息的客户端
     */
    private String pubClientId ;

}
