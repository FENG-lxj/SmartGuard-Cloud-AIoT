package com.chome.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 设备信息DTO
 * @Date 2025/4/21
 * @DAY_NAME_FULL: 星期一
 * @Version 1.0
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FurnitureDTO {
    /**
     * 设备id
     */
    @NotBlank(message = "设备信息不存在")
    private String deviceId;
    /**
     * 设备名称
     */
    @NotBlank(message = "设备名称不能为空")
    private String furnitureName;
    /**
     * 设备类型
     */
    @NotBlank(message = "设备类型不能为空")
    private Integer furnitureType;
    /**
     * 设备位置
     */
    @NotBlank(message = "设备位置不能为空")
    private Integer furnitureLocation;
    /*
     * 设备操作码
     */
    @NotBlank(message = "设备操作码不能为空")
    private String operationCode;
    /**
     * 设备状态
     */
    private Integer furnitureStatus;
    /**
     * 备注
     */
    private String remark;
}
