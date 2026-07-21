package com.chome.constants;

/**
 * @Description MQTT常量类
 * @Date 2025/4/18
 * @DAY_NAME_FULL: 星期五
 * @Version 1.0
 */

public class MQTTConstants {

    // MQTT 订阅Sub主题名称
    /**
     * 设备上线主题
     */
    public static final String MQTT_ONLINE_TOPIC = "CHome/sub/furniture/online";
    /**
     * 设备的功能状态更新主题
     */
    public static final String MQTT_FUNCTION_STATUS_TOPIC = "CHome/sub/furniture/powerStatus";
    /**
     * 监控设备报警主题
     */
    public static final String MQTT_ALARM_TOPIC = "CHome/sub/camera/alarm";
    /**
     * 监控视频报警视频片段推流主题
     */
    public static final String MQTT_ALARM_VIDEO_TOPIC = "CHome/sub/camera/alarmVideo";

}
