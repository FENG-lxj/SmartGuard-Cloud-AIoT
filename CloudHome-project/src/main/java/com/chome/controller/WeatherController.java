package com.chome.controller;

import com.chome.domain.base.Result;
import com.chome.domain.dto.QueryWeatherDTO;
import com.chome.domain.vo.WeatherResponse;
import com.chome.service.IWeatherService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description 警报服务
 * @Date 2025/7/21
 * @DAY_NAME_FULL: 星期一
 * @Version 1.0
 */

@RestController
@RequestMapping("/weather")
@RequiredArgsConstructor
@ApiResponse(description = "天气查询功能")
@Slf4j
public class WeatherController {

    private final IWeatherService weatherService;

    /**
     * 查询天气
     *
     * @param queryWeatherDTO 查询参数
     * @return 天气信息
     */
    @GetMapping("/queryWeather")
    public Result<WeatherResponse> getAlarmInfoList(@RequestBody QueryWeatherDTO queryWeatherDTO) {
        return Result.success(weatherService.getWeather(queryWeatherDTO));
    }


}
