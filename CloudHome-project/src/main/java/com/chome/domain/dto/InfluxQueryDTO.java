package com.chome.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description influx查询参数
 * @Date 2025/7/31
 * @DAY_NAME_FULL: 星期四
 * @Version 1.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InfluxQueryDTO {

    /**
     * 位置名称
     */
    public String locationName;
    /**
     * 设备ID
     */
    public String deviceId;
    /**
     * 监控点名称
     */
    public List<String> environmentName;
    /**
     * 开始时间
     */
    public String startTime;
    /**
     * 结束时间
     */
    public String endTime;
}
