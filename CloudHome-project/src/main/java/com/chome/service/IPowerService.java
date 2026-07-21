package com.chome.service;

import com.chome.domain.dto.FurniturePowerDTO;
import com.chome.domain.entity.FurniturePower;

import java.util.List;

/**
 * @Description 设备功能接口
 * @Date 2025/8/8
 * @DAY_NAME_FULL: 星期五
 * @Version 1.0
 */

public interface IPowerService {

    /**
     * 获取设备功能参数列表
     *
     * @param deviceId  设备ID(必须)
     * @param powerName 功能名称(可选)
     * @return 功能参数列表
     */
    List<FurniturePower> queryPower(String deviceId, String powerName);

    /**
     * 缓存未注册的功能参数
     *
     * @param dto 功能参数DTO
     */
    void cacheUnregisteredPower(FurniturePowerDTO dto);

    /**
     * 批量动态保存设备能力
     *
     * @param powers 设备能力列表
     */
    void batchInsertFurniturePower(List<FurniturePower> powers);

    /**
     * 更新设备功能参数
     *
     * @param powerDTO 设备功能参数
     */
    void updateFurniturePower(FurniturePowerDTO powerDTO);

    /**
     * 查询设备功能参数名称
     *
     * @return 功能参数名称列表
     */
    List<String> queryPowerName();
}
