package com.chome.mapper;

import org.apache.ibatis.annotations.Mapper;

/**
 * @Description 城市编码 Mapper
 * @Date 2025/8/9
 * @DAY_NAME_FULL: 星期六
 * @Version 1.0
 */

@Mapper
public interface CityMapper {

    /**
     * 根据城市名称获取城市编码
     *
     * @param cityName 城市名称
     * @return 城市编码
     */
    String getCityCodeByName(String cityName);
}
