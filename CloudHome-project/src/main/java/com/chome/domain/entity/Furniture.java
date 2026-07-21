package com.chome.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chome.domain.base.SystemBase;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Description 系统基础类
 * @Date 2025/4/15
 * @DAY_NAME_FULL: 星期二
 * @Version 1.0
 */

@EqualsAndHashCode(callSuper = true)
@TableName("CHome_Furniture")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Furniture extends SystemBase implements Serializable {

    /**
     * Id
     */
    @TableId(value = "furniture_id", type = IdType.AUTO)
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
    private Integer furnitureStatus;
    /**
     * 设备位置
     */
    private Integer locationId;
    /**
     * 逻辑删除标识
     */
    private String isDelete;
    /**
     * 序列化版本号
     */
    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}
