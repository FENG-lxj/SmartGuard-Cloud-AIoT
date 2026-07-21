package com.chome.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Description 位置信息实体类
 * @Date 2025/8/9
 * @DAY_NAME_FULL: 星期六
 * @Version 1.0
 */

@TableName("CHome_Location")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Location implements Serializable {

    /**
     * 位置ID
     */
    @TableId
    public Integer id;
    /**
     * 所处房间名称
     */
    public String roomName;
    /**
     * 创建时间
     */
    public LocalDateTime createdTime;
    /**
     * 序列化版本号
     */
    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
