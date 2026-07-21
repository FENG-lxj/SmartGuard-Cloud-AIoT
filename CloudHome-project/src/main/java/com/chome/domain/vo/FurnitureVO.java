package com.chome.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Description 设备信息VO
 * @Date 2025/4/21
 * @DAY_NAME_FULL: 星期一
 * @Version 1.0
 */

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class FurnitureVO {
    /**
     * id
     */
    private Integer furnitureId;
    /**
     * 设备id
     */
    private String deviceId;
    /**
     * 设备名称
     */
    private String furnitureName;
    /**
     * 设备类型
     */
    private Integer furnitureType;
    /**
     * 设备状态
     */
    private String furnitureStatus;
    /**
     * 设备位置
     */
    private String locationName;
    /**
     * 备注
     */
    private String remark;
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    /**
     * 创建用户
     */
    private Integer createdUser;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    /**
     * 更新用户
     */
    private Integer updateUser;

}
