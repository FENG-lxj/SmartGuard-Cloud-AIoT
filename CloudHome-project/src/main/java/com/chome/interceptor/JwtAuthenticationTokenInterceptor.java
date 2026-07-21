package com.chome.interceptor;

import cn.hutool.json.JSONUtil;
import com.chome.domain.base.UserContext;
import com.chome.domain.entity.User;
import com.chome.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.chome.constants.RedisKeyPrefixes.PREFIX_TOKEN;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;

/**
 * @Description JWT登录认证拦截器
 * 拦截所有需要登录鉴权的接口，完成Token校验、Redis有效性校验、用户上下文绑定
 * @Date 2025/4/25
 * @DAY_NAME_FULL: 星期五
 * @Version 1.0
 * 执行时机：Controller接口执行前preHandle拦截，未携带有效Token直接拒绝访问
 */
@Component
// lombok注入构造器，自动注入final修饰的RedisTemplate
@RequiredArgsConstructor
public class JwtAuthenticationTokenInterceptor implements HandlerInterceptor {

    // Redis字符串模板，用于校验服务端缓存的登录Token
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 请求前置拦截方法，所有接口进入Controller之前执行
     * @param request HTTP请求对象，用于读取请求头、参数等
     * @param response HTTP响应对象，用于设置返回状态码、响应数据
     * @param handler 当前请求处理器对象，代表目标Controller方法
     * @return true：校验通过，放行接口；false：校验失败，拦截请求
     */
    @Override
    public boolean preHandle(@NotNull HttpServletRequest request,
                             @NotNull HttpServletResponse response,
                             @NotNull Object handler) {
        // 1. 先判断当前线程上下文是否已存在登录用户
        // 多拦截器/多次拦截场景下避免重复解析Token，直接放行
        if (UserContext.getCurrentUser() != null) {
            return true;
        }

        // 2. 从请求Header中提取Authorization头部的JWT Token
        String token = JwtUtil.getTokenFromHeader(request);
        // 判断Token非空，并且Token签名、有效期校验通过
        if (token != null && JwtUtil.verifyToken(token)) {
            // 3. 解析Token载荷，获取存储的用户JSON字符串
            String userInfo = JwtUtil.getUserInfoForToken(token);
            // 将JSON字符串转为User实体对象，封装用户ID、账号、权限等信息
            User user = JSONUtil.toBean(userInfo, User.class);

            // 4. 校验Redis登录缓存，判断当前Token是否已失效（登出/过期会删除Redis key）
            // Redis Key规则：PREFIX_TOKEN + 用户ID
            if (!stringRedisTemplate.hasKey(PREFIX_TOKEN + user.getId())) {
                // Redis无有效登录凭证，标记状态码，拦截请求
                response.setStatus(SC_OK);
                return false;
            }

            // 5. 将当前登录用户存入ThreadLocal全局上下文，供Controller/Service业务代码直接获取
            UserContext.setCurrentUser(user);
            // 所有校验逻辑全部通过，放行接口访问
            return true;
        }

        // 无Token、Token过期、Token签名非法，直接拦截请求
        return false;
    }
}