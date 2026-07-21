package com.chome.service;

import com.chome.domain.dto.QueryWeatherDTO;
import com.chome.domain.vo.WeatherResponse;

/**
 * @Description 天气查询接口
 * @Date 2025/8/4
 * @DAY_NAME_FULL: 星期一
 * @Version 1.0
 */

public interface IWeatherService {

    /**
     * 获取天气信息
     *
     * @param queryWeatherDTO 查询天气参数
     * @return WeatherResponse 天气信息
     */
    public WeatherResponse getWeather(QueryWeatherDTO queryWeatherDTO);
}
