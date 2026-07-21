package com.chome.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Description 设备能力DTO
 * @Date 2025/7/13
 * @DAY_NAME_FULL: 星期日
 * @Version 1.0
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FurniturePowerDTO {

    /**
     * 设备id
     */
    private String deviceId;
    /**
     * 能力名称
     */
    private String n;
    /**
     * 能力值
     */
    private Double v;
    /**
     * 能力类型
     */
    private Integer t;
    /**
     * 所属房间名称
     */
    private String locationName;
    /**
     * 时间戳
     */
    private LocalDateTime updateTime;
}
