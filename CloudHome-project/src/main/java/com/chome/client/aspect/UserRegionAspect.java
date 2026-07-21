package com.chome.client.aspect;

import com.chome.annotation.UpdateUserRegion;
import com.chome.domain.base.Result;
import com.chome.domain.vo.UserLRVO;
import com.chome.mapper.UserMapper;
import com.chome.utils.IpUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * AOP切面：拦截标记@UpdateUserRegion注解的接口，登录成功后自动解析客户端IP归属区域并更新用户库
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class UserRegionAspect {

    // 用户数据库Mapper，用于更新用户IP区域字段
    private final UserMapper userMapper;

    /**
     * 后置通知：接口正常返回结果后执行
     * 切点：所有标注@UpdateUserRegion自定义注解的方法
     * @param joinPoint 切点方法元数据
     * @param result 接口返回的统一响应对象
     */
    @AfterReturning(pointcut = "@annotation(com.chome.annotation.UpdateUserRegion)", returning = "result")
    public void updateUserRegion(JoinPoint joinPoint, Object result) {
        try {
            // 从上下文获取当前Http请求对象
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return;
            }
            HttpServletRequest request = attributes.getRequest();
            // 解析请求IP对应的省市区域
            String region = IpUtils.streamGetRegion(request);
            // 判断返回结果是否为统一返回类Result
            if (result instanceof Result<?> res) {
                Object data = res.getData();
                // 判断返回数据为登录VO，从中获取用户ID
                if (data instanceof UserLRVO userVO) {
                    Integer userId = userVO.getUserId();
                    // 根据用户ID更新IP归属区域
                    userMapper.updateIpRegion(userId, region);
                }
            }
        } catch (Exception e) {
            // 捕获所有异常，避免IP解析失败阻断主业务流程
            log.error("获取用户ip所属区域信息失败: {}", e.getMessage());
        }
    }
}