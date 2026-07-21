package com.chome.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 天气查询返回结果详细信息
 * @Date 2025/8/4
 * @DAY_NAME_FULL: 星期一
 * @Version 1.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeatherLives {

    /**
     * 省份名
     */
    private String province;
    /**
     * 城市名
     */
    private String city;
    /**
     * 地区编码
     */
    private String adcode;
    /**
     * 数据发布时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("reporttime")
    private String reportTime;
    /**
     * 天气现象（汉字描述）
     */
    private String weather;
    /**
     * 实时气温，单位：摄氏度
     */
    private String temperature;
    /**
     * 风向描述
     */
    @JsonProperty("winddirection")
    private String windDirection;
    /**
     * 风力级别
     */
    @JsonProperty("windpower")
    private String windPower;
    /**
     * 空气湿度
     */
    private String humidity;

    /**
     * 实时气温，单位：摄氏度 对应浮点数
     */
    @JsonProperty("temperature_float")
    private String temperatureFloat;

    @JsonProperty("humidity_float")
    private String humidityFloat;
}
