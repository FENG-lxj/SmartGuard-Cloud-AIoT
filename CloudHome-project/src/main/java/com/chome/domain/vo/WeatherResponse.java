package com.chome.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description 天气查询返回结果
 * @Date 2025/8/4
 * @DAY_NAME_FULL: 星期一
 * @Version 1.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeatherResponse {

    /*
     * 返回状态
     */
    private String status;
    /*
     * 返回结果总数目
     */
    private String count;
    /*
     * 返回的状态信息
     */
    private String info;
    /*
     * 返回状态说明,10000 代表正确
     */
    private String infocode;
    /*
     * 详细信息（实时）
     */
    private List<WeatherLives> lives;
    /*
     * 详细信息（预报）
     */
    private List<WeatherForecast> forecasts;

}
