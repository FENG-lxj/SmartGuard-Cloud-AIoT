package com.chome.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chome.domain.entity.Furniture;
import com.chome.domain.entity.Location;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Description 设备Mapper
 * @Date 2025/4/3
 * @DAY_NAME_FULL: 星期四
 * @Version 1.0
 */

@Mapper
public interface FurnitureMapper extends BaseMapper<Furniture> {

    /**
     * 获取设备位置名称
     *
     * @param locationIdList 位置ID列表
     */
    List<Location> getLocationsByIds(List<Integer> locationIdList);
}
