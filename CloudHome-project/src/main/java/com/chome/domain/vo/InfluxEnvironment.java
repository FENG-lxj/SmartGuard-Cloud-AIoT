package com.chome.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Description 环境数据
 * @Date 2025/4/25
 * @DAY_NAME_FULL: 星期五
 * @Version 1.0
 */

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class InfluxEnvironment {
    /**
     * 环境参数名称
     */
    public String environmentName;
    /**
     * 环境参数值
     */
    public String environmentValue;
    /**
     * 所属设备Id
     */
    public String deviceId;
    /**
     * 所属房间名称
     */
    public String locationName;
    /**
     * 时间戳
     */
    public LocalDateTime timeStamp;
}
