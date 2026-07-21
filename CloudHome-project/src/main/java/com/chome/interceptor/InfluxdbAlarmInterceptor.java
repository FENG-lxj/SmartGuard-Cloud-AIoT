package com.chome.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @Description InfluxDB告警推送接口鉴权拦截器
 * 作用：拦截InfluxDB服务主动推送告警数据的请求，校验请求携带的Bearer密钥，防止非法外部调用
 * 仅放行携带正确密钥的告警推送请求，非法请求直接返回401未授权
 * @Date 2025/9/9
 * @DAY_NAME_FULL: 星期二
 * @Version 1.0
 */
@Component
@Slf4j
public class InfluxdbAlarmInterceptor implements HandlerInterceptor {

    // 配置文件中配置的InfluxDB告警接口访问密钥 spring.influx.secret
    @Value("${spring.influx.secret}")
    private String secret;

    /**
     * 请求进入Controller之前执行鉴权校验
     * @param request 请求对象，用于读取请求头Authorization
     * @param response 响应对象，校验失败返回401状态码
     * @param handler 请求处理器
     * @return true：校验通过，放行请求；false：校验失败，拦截请求
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求头中的Authorization鉴权字段
        String authorization = request.getHeader("Authorization");
        // 判断请求头存在且以Bearer标准前缀开头
        if (authorization != null && authorization.startsWith("Bearer ")) {
            // 截取Bearer后面的密钥字符串，前缀"Bearer "共7个字符
            String token = authorization.substring(7);
            // 和配置文件内置密钥比对，一致则鉴权通过
            if (token.equals(secret)) {
                return true;
            }
        }
        // 密钥不匹配/无鉴权头，打印警告日志
        log.warn("InfluxDB 告警身份校验失败，Authorization: {}", authorization);
        // 设置HTTP响应状态码401 未授权
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        // 拦截请求，禁止进入业务接口
        return false;
    }
}