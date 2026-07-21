package com.chome.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description 会话管理
 * @Date 2025/9/28
 * @DAY_NAME_FULL: 星期日
 * @Version 1.0
 */

@Data
public class SessionManager {

    /**
     * 用户会话
     */
    private static final Map<String, UserSession> sessions = new ConcurrentHashMap<>();

    /**
     * 获取用户会话
     *
     * @param sessionId 会话ID
     * @return 用户会话
     */
    public static UserSession get(String sessionId) {
        return sessions.get(sessionId);
    }

    /**
     * 添加用户会话
     *
     * @param sessionId 会话ID
     * @param session 用户会话
     */
    public static void put(String sessionId, UserSession session) {
        sessions.put(sessionId, session);
    }

    /**
     * 删除用户会话
     *
     * @param sessionId 会话ID
     */
    public static void remove(String sessionId) {
        sessions.remove(sessionId);
    }
}
