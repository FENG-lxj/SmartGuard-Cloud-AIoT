package com.chome.constants;

/**
 * @Description 项目常量类
 * @Date 2025/4/18
 * @DAY_NAME_FULL: 星期五
 * @Version 1.0
 */

public class CHomeConstants {

    /**
     * 项目名————CHome 智护云家
     */
    public static final String PROJECT_NAME = "智护云家";
    /**
     * 用户禁用状态
     */
    public static final String USER_STATUS_FORBIDDEN = "0";
    /**
     * 用户正常状态
     */
    public static final String USER_STATUS_NORMAL = "1";
    /**
     * 用户登出状态
     */
    public static final String USER_STATUS_LOGOUT = "2";
    /**
     * 默认用户头像地址
     */
    public static final String DEFAULT_AVATAR_URL = "https://chome-test.oss-cn-beijing.aliyuncs.com/baseAvatar/user_avatar.jpg";
    /**
     * 默认用户密码
     */
    public static final String DEFAULT_PASSWORD = "123456";
    /**
     * 用户名密码方式登录
     */
    public static final String USER_LOGIN_TYPE_USERNAME = "USERNAME";
    /**
     * 手机号短信验证码方式登录
     */
    public static final String USER_LOGIN_TYPE_PHONE = "PHONE";
    /**
     * 请求IP地址的属性名
     */
    public static final String REQUEST_IP_ATTR = "CLIENT_IP";
    /**
     * 验证码过期时间
     */
    public static final Integer CODE_EXPIRATION_TIME = 5;
    /**
     * 短信验证码限流时间窗口
     */
    public static final Integer SMS_CODE_TIME_WINDOW = 5;
    /**
     * 同一手机号短信验证码短时最大获取次数
     */
    public static final Integer MAX_SMS_CODE_REQUEST_COUNT = 10;
    /**
     * 同一IP短信验证码短时最大获取次数
     */
    public static final Integer MAX_SMS_CODE_IP_REQUEST_COUNT = 3;
    /**
     * jwt过期时间
     * 毫秒*分钟*小时*天 = 一周
     **/
    public static final long JWT_EXPIRATION = 1000 * 60 * 60 * 24 * 7;
    /**
     * 最大登录次数
     */
    public static final Integer MAX_LOGIN_ATTEMPTS = 3;
    /**
     * 超出最大登录次数后重置时间
     */
    public static final Integer MAX_LOGIN_RETRY_TIME = 5;

    /**
     * AI文本聊天模式
     */
    public static final String CHAT_MODEL = "chat";

    /**
     * AI语音聊天模式
     */
    public static final String AUDIO_MODEL = "audio";

    /**
     * AI聊天记忆长度 条数 x 2 (问题 + 回答)
     */
    public static final Integer MEMORY_LENGTH = 5 * 2;

    /**
     *
     * 实时建议默认会话id
     */
    public static final String REALTIME_SUGGESTION = "realTimeSuggestion";
    /**
     * 会话过期时间 单位:天
     */
    public static final Integer CONVERSATION_EXPIRATION_TIME = 7;
    /**
     * 会话对话角色:用户
     */
    public static final String USER_ROLE = "USER";
    /**
     * 会话对话角色:AI
     */
    public static final String AI_ROLE = "ASSISTANT";
    /**
     * BCrypt密码加密的盐值 值越大加密后的字符串越长
     */
    public static final Integer SALT_VALUE = 11;

    // 环境数据变化阈值定义
    // 温度允许变动 ±2℃
    public static final double TEMP_THRESHOLD = 2.0;
    // 湿度允许 ±10%
    public static final double HUMIDITY_THRESHOLD = 10.0;
    // 光照强度 ±100 lux
    public static final double ILLUM_THRESHOLD = 100.0;
    // 空气质量指数 ±15
    public static final double AIR_QUALITY_THRESHOLD = 15.0;
    /**
     * 每 FLUX_SECOND 秒判断一次环境数据是否变化需要分析建议
     */
    public static final Integer FLUX_SECOND = 5 * 60;

    /**
     * 告警标题
     */
    public static final String ALARM_TITLE = "【智护云家-系统告警】";
    /**
     * 告警类型：环境告警
     */
    public static final String ENVIRONMENT_ALARM_TYPE = "environment_alarm";
    /**
     * 告警模式：电话+wx公众号
     */
    public static final String ALL_ALARM_MODE = "all_alarm";
    /**
     * 告警模式：单 wx公众号
     */
    public static final String WX_ALARM_MODE = "wx_alarm";

    /**
     * 设备能力种类 (一)：控制类能力
     */
    public static final Integer CONTROL_ABILITY = 1;
    /**
     * 设备能力种类 (二)：环境监测类能力
     */
    public static final Integer ENVIRONMENT_ABILITY = 2;
    /**
     * 设备上线信息过期时间(分钟)
     */
    public static final Integer DEVICE_ONLINE_EXPIRATION_TIME = 10;
    /**
     * 设备逻辑删除标识
     */
    public static final String DEVICE_DELETE_FLAG = "1";
    /**
     * 警报状态：已确认
     */
    public static final Integer ALERT_CONFIRMED = 1;
}

