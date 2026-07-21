package com.chome.config;

import com.chome.handler.AudioWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.support.WebSocketHandlerMapping;
import org.springframework.web.socket.server.support.WebSocketHttpRequestHandler;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Description WebSocket统一配置类
 * 分为两套WebSocket能力：
 * 1. STOMP协议WebSocket：用于设备状态、告警、环境数据、前端消息推送，带订阅/广播主题
 * 2. 原生WebSocket：独立音频传输通道，传输实时语音流数据
 * @Date 2025/4/18
 * @DAY_NAME_FULL: 星期五
 * @Version 1.0
 */
// Spring配置类，项目启动自动加载
@Configuration
// 开启STOMP消息代理支持，启用STOMP协议WebSocket
@EnableWebSocketMessageBroker
// 日志注解，自动生成log对象打印连接、消息日志
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * 注册STOMP连接接入端点
     * 前端连接地址：ws://域名/CHome，兼容SockJS降级（浏览器不支持ws时自动切换轮询）
     * @param registry STOMP端点注册器
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/CHome")
                .setAllowedOriginPatterns("*") // 允许所有跨域前端连接
                .withSockJS(); // 开启SockJS兼容，适配低版本浏览器
    }

    /**
     * 配置STOMP消息代理、消息路由前缀
     * @param registry 消息代理配置对象
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 开启内置简单内存消息代理，客户端订阅 /topic/xxx 接收广播消息
        registry.enableSimpleBroker("/topic");
        // 客户端发送消息到服务端的接口前缀，匹配四类业务：
        // /app 通用交互、/environment 环境数据、/alarm 告警推送、/deviceOnline 设备上下线
        registry.setApplicationDestinationPrefixes("/app", "/environment", "/alarm", "/deviceOnline");
    }

    /**
     * 原生WebSocket路由Bean：音频实时传输独立通道
     * 路径 /ws/audio 专门处理摄像头/设备语音二进制流，不使用STOMP协议
     * 设置优先级-1，优先匹配该原生ws路由，避免被STOMP拦截
     * @param audioWebSocketHandler 音频消息自定义处理器
     * @return WebSocket路由映射对象
     */
    @Bean
    public WebSocketHandlerMapping audioWebSocketHandlerMapping(AudioWebSocketHandler audioWebSocketHandler) {
        WebSocketHandlerMapping mapping = new WebSocketHandlerMapping();
        // 包装原生WebSocket处理器
        WebSocketHttpRequestHandler handler =
                new WebSocketHttpRequestHandler(audioWebSocketHandler);
        // 绑定URL路径与处理器
        Map<String, Object> urlMap = new LinkedHashMap<>();
        urlMap.put("/ws/audio", handler);
        mapping.setUrlMap(urlMap);
        // 优先级-1，比STOMP拦截器优先级更高，优先匹配音频ws接口
        mapping.setOrder(-1);
        return mapping;
    }
}