package com.chome.domain.base;

import com.chome.domain.entity.User;

/**
 * @Description 用户上下文工具类
 * 基于ThreadLocal实现登录用户信息线程隔离存储，同一请求链路内全局获取登录用户
 * @Date 2025/4/15
 * @DAY_NAME_FULL: 星期二
 * @Version 1.0
 * 核心特性：每条请求对应独立线程，线程间用户数据互不干扰；请求结束必须调用clear释放，避免内存泄漏
 */
public class UserContext {

    /**
     * ThreadLocal线程本地变量
     * 作用：绑定当前请求线程与登录用户对象，同一个线程内任意位置均可读取用户信息
     * static final：全局唯一实例，所有请求共用该ThreadLocal容器，内部自动按线程隔离数据
     */
    private static final ThreadLocal<User> currentUser = new ThreadLocal<>();

    /**
     * 将登录用户存入当前请求线程上下文
     * @param user 登录用户实体对象，包含用户ID、账号、权限等信息
     */
    public static void setCurrentUser(User user) {
        currentUser.set(user);
    }

    /**
     * 获取当前请求线程绑定的登录用户
     * @return 已登录用户实体；未登录/未设置时返回null
     */
    public static User getCurrentUser() {
        return currentUser.get();
    }

    /**
     * 清空当前线程存储的用户信息
     * 重要：请求执行完毕后必须执行该方法，防止线程池复用线程导致用户信息串号、内存泄漏
     */
    public static void clear() {
        currentUser.remove();
    }
}