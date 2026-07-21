package com.chome.domain.entity;

import cn.hutool.core.annotation.Alias;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Description 设备能力表
 * @Date 2025/4/23
 * @DAY_NAME_FULL: 星期三
 * @Version 1.0
 */

@TableName("CHome_Furniture_Power")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class FurniturePower implements Serializable {

    /**
     * 能力 Id
     */
    @TableId
    private Integer powerId;
    /**
     * 相关联设备类型
     */
    @TableField("device_power_id")
    private String deviceId;
    /**
     * 能力名称
     */
    @TableField("power_name")
    @Alias("powerName")
    private String n;
    /**
     * 能力值
     */
    @TableField("power_value")
    @Alias("powerValue")
    private Double v;
    /**
     * 能力单位
     */
    @TableField("unit")
    @Alias("unit")
    private String u;
    /**
     * 能力类型
     */
    @TableField("power_type")
    @Alias("powerType")
    private Integer t;
    /**
     * 操作码
     */
    @TableField("operation_code")
    private String operationCode;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    /**
     * 序列化版本号
     */
    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
