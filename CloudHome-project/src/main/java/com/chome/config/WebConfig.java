package com.chome.config;

import com.chome.interceptor.InfluxdbAlarmInterceptor;
import com.chome.interceptor.JwtAuthenticationTokenInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Description Spring MVC全局Web配置类
 * 1. 注册自定义拦截器，配置拦截/放行路径：JWT登录鉴权拦截器、InfluxDB告警接口密钥校验拦截器
 * 2. 全局跨域CORS统一配置，解决前后端分离跨域请求报错
 * @Date 2025/4/18
 * @DAY_NAME_FULL: 星期五
 * @Version 1.0
 */
// 标记为Spring配置类，项目启动自动加载
@Configuration
// lombok自动注入所有final修饰的拦截器实例
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    // JWT登录鉴权拦截器：校验前端请求携带的登录token
    private final JwtAuthenticationTokenInterceptor jwtAuthenticationTokenInterceptor;

    // InfluxDB告警推送接口专用鉴权拦截器：校验InfluxDB推送请求的密钥
    private final InfluxdbAlarmInterceptor influxdbAlarmInterceptor;

    /**
     * 注册拦截器，配置拦截规则、放行白名单
     * @param registry 拦截器注册对象
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 1. JWT登录拦截器配置
        registry.addInterceptor(jwtAuthenticationTokenInterceptor)
                .addPathPatterns("/**") // 拦截所有接口请求
                // 放行无需登录的接口：登录、注册、验证码、短信验证码、InfluxDB告警推送接口
                .excludePathPatterns("/user/login", "/user/register", "/user/captcha", "/user/smsCode","/alarm/saveAlarm");

        // 2. InfluxDB告警鉴权拦截器配置
        registry.addInterceptor(influxdbAlarmInterceptor)
                .addPathPatterns("/alarm/saveAlarm"); // 仅单独拦截告警推送接口，做密钥校验
    }

    /**
     * 全局跨域配置，允许前端页面跨域访问后端接口
     * @param registry 跨域注册配置对象
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")               // 所有接口统一应用跨域规则
                .allowedOrigins("*")              // 允许所有来源域名访问
                .allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS") // 放行全部常用请求方式
                .allowedHeaders("*");             // 允许携带任意请求头（Authorization、token等）
    }

}