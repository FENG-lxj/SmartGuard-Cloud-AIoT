package com.chome.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description 天气查询返回结果详细信息
 * @Date 2025/8/4
 * @DAY_NAME_FULL: 星期一
 * @Version 1.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeatherForecast {
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
     * 预报数据 list 结构，元素 cast,按顺序为当天、第二天、第三天的预报数据
     */
    @JsonProperty("casts")
    private List<WeatherCast> weatherCast;
}
