package com.chome.filter;

import com.chome.utils.IpUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.chome.constants.CHomeConstants.REQUEST_IP_ATTR;

/**
 * @Description 客户端IP前置过滤器
 * 全局拦截所有HTTP请求，自动解析真实客户端IP并存入request域，后续Controller/拦截器/Service可直接获取
 * @Date 2025/9/15
 * @DAY_NAME_FULL: 星期一
 * @Version 1.0
 */
// 注册为Spring Bean，全局自动生效
@Component
public class ClientIpFilter extends HttpFilter {

    /**
     * 过滤处理逻辑，所有请求进入Controller前执行
     * @param req 请求对象
     * @param res 响应对象
     * @param chain 过滤器链，放行请求到下一级
     */
    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        // 工具类解析真实客户端IP（兼容Nginx反向代理、负载均衡场景）
        String clientIp = IpUtils.getClientIp(req);
        // 将IP存入request域属性，常量统一管理key，避免硬编码
        req.setAttribute(REQUEST_IP_ATTR, clientIp);
        // 放行过滤器链，继续执行拦截器、Controller业务逻辑
        chain.doFilter(req, res);
    }
}