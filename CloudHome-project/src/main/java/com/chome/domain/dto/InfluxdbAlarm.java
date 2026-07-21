package com.chome.domain.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @Description influxdb告警通知数据
 * @Date 2025/9/3
 * @DAY_NAME_FULL: 星期三
 * @Version 1.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InfluxdbAlarm {

    private Map<String, Double> metrics;

    @JsonProperty("_level")
    private String alarmLevel;

    @JsonProperty("_message")
    private String alarmText;

    @JsonProperty("_time")
    private String alarmTime;

    @JsonProperty("deviceId")
    private String alarmSourceId;

    private String location;

    @JsonAnySetter
    public void setMetric(String key, Object value) {
        // 只接收数值型字段到 metrics,确保动态接受不同传感器的数据
        if (value instanceof Number) {
            metrics.put(key, ((Number) value).doubleValue());
        }
    }
}
