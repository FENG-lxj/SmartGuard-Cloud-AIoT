package com.chome.domain.dto;

import com.chome.domain.entity.FurniturePower;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description 设备上线信息DTO
 * @Date 2025/5/4
 * @DAY_NAME_FULL: 星期日
 * @Version 1.0
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FurnitureOnlineDTO {
    /**
     * 设备id
     */
    private String deviceId;
    /**
     * 设备类型
     */
    private Integer furnitureType;
    /**
     * 设备状态
     */
    private Integer furnitureStatus;
    /**
     * 扩展功能
     */
    private List<FurniturePower> powers;
}