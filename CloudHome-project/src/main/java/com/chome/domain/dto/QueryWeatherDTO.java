package com.chome.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 天气查询DTO
 * @Date 2025/8/4
 * @DAY_NAME_FULL: 星期一
 * @Version 1.0
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryWeatherDTO {
    /**
     * 城市编码
     */
    private String city;
    /**
     * 气象类型(base:实况/all:预报)
     */
    private String extensions;
}
