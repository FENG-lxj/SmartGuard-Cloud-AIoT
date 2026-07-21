package com.chome.tool;

import com.chome.domain.dto.QueryWeatherDTO;
import com.chome.domain.vo.WeatherResponse;
import com.chome.service.IWeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

/**
 * @Description  天气、空气质量、新闻等外部服务方法
 * @Date 2025/8/9
 * @DAY_NAME_FULL: 星期六
 * @Version 1.0
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalApiTool {

    private final IWeatherService weatherService;

    @Tool(name = "checkWeather", description = "中国境内（实时/预报）天气信息查询")
    public String checkWeather(
            @ToolParam(description = "城市名称") String cityName,
            @ToolParam(description = "查询类型 base:实况/all:预报") String queryType) {
        // 构建天气查询参数
        QueryWeatherDTO queryWeatherDTO = QueryWeatherDTO.builder()
                .city(cityName)
                .extensions(queryType)
                .build();

        WeatherResponse weatherResponse = weatherService.getWeather(queryWeatherDTO);
        return String.format("查询 %s 的天气成功:【%s】", cityName, weatherResponse);
    }
}
