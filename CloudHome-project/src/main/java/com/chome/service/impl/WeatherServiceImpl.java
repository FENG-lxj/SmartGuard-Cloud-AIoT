package com.chome.service.impl;

import com.chome.client.GaodeWeatherClient;
import com.chome.domain.dto.QueryWeatherDTO;
import com.chome.domain.vo.WeatherResponse;
import com.chome.service.IWeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Description 天气查询服务实现类
 * @Date 2025/8/4
 * @DAY_NAME_FULL: 星期一
 * @Version 1.0
 */

@RequiredArgsConstructor
@Service
@Slf4j
public class WeatherServiceImpl implements IWeatherService {

    private final GaodeWeatherClient gaodeWeatherClient;

    /**
     * 查询天气
     *
     * @param queryWeatherDTO 查询天气参数
     * @return 天气数据
     */
    @Override
    public WeatherResponse getWeather(QueryWeatherDTO queryWeatherDTO) {
        long start = System.currentTimeMillis();
        try {
            WeatherResponse response = gaodeWeatherClient.queryWeather(queryWeatherDTO);
            log.info("天气查询成功 city={}, extensions={}, 耗时={}ms",
                    queryWeatherDTO.getCity(), queryWeatherDTO.getExtensions(),
                    System.currentTimeMillis() - start);
            return response;
        } catch (Exception e) {
            log.error("天气查询失败 city={}, extensions={}, 耗时={}ms, 错误={}",
                    queryWeatherDTO.getCity(), queryWeatherDTO.getExtensions(),
                    System.currentTimeMillis() - start, e.getMessage(), e);
            throw new RuntimeException("天气查询失败，请稍后再试", e);
        }
    }
}
