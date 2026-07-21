package com.chome.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chome.domain.entity.FurniturePower;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description 设备Mapper
 * @Date 2025/4/3
 * @DAY_NAME_FULL: 星期四
 * @Version 1.0
 */

@Mapper
public interface PowerMapper extends BaseMapper<FurniturePower> {

    /**
     * 批量动态保存设备能力
     *
     * @param powers 设备能力列表
     */
    void batchInsertFurniturePower(List<FurniturePower> powers);

    /**
     * 根据设备id和设备名更新设备能力信息
     *
     * @param power 设备能力信息
     */
    void updatePowerByIdAndName(FurniturePower power);

    /**
     * 根据家具id和能力名称查询具体能力
     *
     * @param deviceId 设备id
     * @param powerName 能力名称
     * @return 具体能力
     */
    FurniturePower queryByIdAndName(@Param("deviceId") String deviceId,@Param("powerName") String powerName);

    /**
     * 根据设备id查询位置名称
     *
     * @param deviceId 设备id
     * @return 位置名称
     */
    String getLocationNameByDeviceId(String deviceId);
}
