package com.chome.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Description 设备操作信息DTO
 * @Date 2025/7/13
 * @DAY_NAME_FULL: 星期日
 * @Version 1.0
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FurnitureOperationDTO {

    /**
     * 设备id
     */
    @NotBlank(message = "设备信息不存在")
    private String deviceId;
    /**
     * 能力名称
     */
    @NotBlank(message = "设备信息错误")
    private String powerName;
    /**
     * 能力值
     */
    @NotNull(message = "设备信息错误")
    private Double value;
}
