package com.chome.constants;

/**
 * @Description redis key前缀
 * @Date 2025/4/18
 * @DAY_NAME_FULL: 星期五
 * @Version 1.0
 */

public class RedisKeyPrefixes {
 
    /**
     * 所有key前面加上服务名
     */
    public static final String PREFIX_BASE = "CHome:";

    /**
     * 图形验证码前缀
     */
    public static final String PREFIX_IMAGE_CODE = PREFIX_BASE + "code:image:";
    /**
     * 短信验证码前缀
     */
    public static final String PREFIX_PHONE_CODE = PREFIX_BASE + "code:phone:";
    /**
     * 短信验证码手机号限流前缀
     */
    public static final String PREFIX_SMS_CODE_PHONE = PREFIX_BASE + "code:sms_phone:";
    /**
     * 短信验证码 ip 限流前缀
     */
    public static final String PREFIX_SMS_CODE_IP = PREFIX_BASE + "code:sms__ip:";
    /**
     * 用户登录尝试次数
     */
    public static final String PREFIX_LOGIN_ATTEMPTS = PREFIX_BASE + "login_attempts:";
    /**
     * 用户登录的token
     */
    public static final String PREFIX_TOKEN = PREFIX_BASE + "login_token:";

    /**
     * 设备已上线待注册
     */
    public static final String PREFIX_UNREGISTERED = PREFIX_BASE + "unregistered_device:";
    /**
     * 设备能力暂存
     */
    public static final String PREFIX_DEVICE_POWERS = PREFIX_BASE + "device_powers:";
    /**
     * 设备能力对应设备不存在缓存
     */
    public static final String PREFIX_UNREGISTERED_POWERS = PREFIX_BASE + "unregistered_device_power:";
    /**
     * 设备能力名称列表缓存
     */
     public static final String PREFIX_POWER_NAMES = PREFIX_BASE + "power_name";

    /**
     * 暂存环境数据用于实时分析
     */
    public static final String PREFIX_ENVIRONMENT = PREFIX_BASE + "cache_environment";

    /**
     * 暂存AI历史记录列表缓存
     */
    public static final String PREFIX_AI_HISTORY = PREFIX_BASE + "ai_history:";

}