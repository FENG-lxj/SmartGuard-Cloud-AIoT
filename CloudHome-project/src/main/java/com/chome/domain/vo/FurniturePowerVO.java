package com.chome.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Description 设备能力 VO 类
 * @Date 2025/4/23
 * @DAY_NAME_FULL: 星期三
 * @Version 1.0
 */

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class FurniturePowerVO {
    /**
     * 能力 Id
     */
    private Integer powerId;
    /**
     * 相关联设备类型
     */
    private String deviceId;
    /**
     * 能力名称
     */
    private String powerName;
    /**
     * 能力值
     */
    private Double powerValue;
    /**
     * 能力单位
     */
    private String unit;
    /**
     * 能力类型
     */
    private Integer powerType;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
