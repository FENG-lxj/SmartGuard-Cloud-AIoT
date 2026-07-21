package com.chome.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chome.domain.dto.FurnitureDTO;
import com.chome.domain.dto.FurnitureOnlineDTO;
import com.chome.domain.dto.FurnitureOperationDTO;
import com.chome.domain.dto.PageRequest;
import com.chome.domain.entity.Furniture;
import com.chome.domain.vo.FurniturePowerVO;
import com.chome.domain.vo.FurnitureVO;
import com.chome.domain.vo.PageVO;

import java.util.List;

/**
 * @Description 设备服务接口
 * @Date 2025/4/3
 * @DAY_NAME_FULL: 星期四
 * @Version 1.0
 */

public interface IFurnitureService extends IService<Furniture> {

    /**
     * 设备上线时更新数据库信息
     *
     * @param payload 设备上线信息
     */
    void onlineUpdate(FurnitureOnlineDTO payload);

    /**
     * 分页查询所有设备
     * 根据设备类型分页查询获取设备列表(可选:furnitureType)
     *
     * @param pageRequest 分页参数
     * @return 设备列表
     */
    PageVO<FurnitureVO> queryFurnitureByType(PageRequest pageRequest);

    /**
     * 新增设备
     *
     * @param furnitureDTO 新增设备信息
     */
    void addNewFurniture(FurnitureDTO furnitureDTO);

    /**
     * 设备功能操作
     *
     * @param operationDTO 设备操作DTO
     */
    void operationDevice(FurnitureOperationDTO operationDTO);

    /**
     * 判断设备是否存在
     *
     * @param deviceId 设备ID
     * @return 设备是否存在
     */
    boolean isDeviceExists(String deviceId);

    /**
     * 获取设备功能列表
     *
     * @param deviceId 设备ID
     * @return 设备功能列表
     */
    List<FurniturePowerVO> getPowerForDevice(String deviceId);
}
