package com.chome.annotation;

import java.lang.annotation.*;

/**
 * 自定义标记注解：用户IP归属区域自动更新注解
 * 功能说明：
 * 1. 仅用于标注Controller层登录相关接口方法；
 * 2. 配合UserRegionAspect切面类使用，当被标注的接口正常执行完成并返回数据后；
 * 3. AOP会自动拦截当前请求，获取客户端真实IP，解析IP对应的省市区域；
 * 4. 从接口返回的登录VO中提取用户ID，自动更新数据库对应用户的IP归属地区字段；
 * 使用规范：
 * 1. 只能添加在方法上方，不支持类、属性、参数上使用；
 * 2. 仅推荐加在登录、自动登录等会返回用户信息的接口；
 * 3. 接口返回值必须为统一包装类Result，且内部data实体为UserLRVO，否则不会执行数据库更新；
 * 4. 接口抛出异常时@AfterReturning不会触发，登录失败不会执行IP区域更新逻辑；
 */
// 元注解@Target：约束该自定义注解的可标注位置
// ElementType.METHOD：仅允许标注在方法上，禁止标注类、成员变量、形参等其他位置
@Target(ElementType.METHOD)
// 元注解@Retention：定义注解的生命周期
// RetentionPolicy.RUNTIME：运行时保留注解信息，JVM加载后可通过反射读取，是AOP识别注解的必要条件
// 若改为SOURCE/CLASS运行时会丢失注解，切面无法生效
@Retention(RetentionPolicy.RUNTIME)
// 元注解@Documented：生成JavaDoc接口文档时，会将本注解说明一并写入文档，方便团队查阅
@Documented
// @interface 是Java定义自定义注解的专属关键字，和普通interface接口完全是两种语法，仅作为标记标签使用，无需类实现
public @interface UpdateUserRegion {
    // 无属性字段，属于标记型注解，仅起到标识作用，不需要传入参数控制逻辑
}