package com.chome.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Description 天气预报
 * @Date 2025/8/4
 * @DAY_NAME_FULL: 星期一
 * @Version 1.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeatherCast {

    /**
     * 日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String date;
    /**
     * 星期几
     */
    private String week;
    /**
     * 白天天气现象
     */
    @JsonProperty("dayweather")
    private String dayWeather;
    /**
     * 夜间天气现象
     */
    @JsonProperty("nightweather")
    private String nightWeather;
    /**
     * 白天温度
     */
    @JsonProperty("daytemp")
    private String dayTemp;
    /**
     * 夜间温度
     */
    @JsonProperty("nighttemp")
    private String nightTemp;
    /**
     * 白天风向
     */
    @JsonProperty("daywind")
    private String dayWind;
    /**
     * 夜间风向
     */
    @JsonProperty("nightwind")
    private String nightWind;
    /**
     * 白天风力
     */
    @JsonProperty("daypower")
    private String dayPower;
    /**
     * 夜间风力
     */
    @JsonProperty("nightpower")
    private String nightPower;
}
