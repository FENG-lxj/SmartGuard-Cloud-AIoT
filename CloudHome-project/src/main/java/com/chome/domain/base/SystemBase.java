package com.chome.domain.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Description 系统基础类
 * @Date 2025/4/15
 * @DAY_NAME_FULL: 星期二
 * @Version 1.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SystemBase {

    /**
     * 备注
     */
    private String remark;
    /**
     * 创建人
     */
    private Integer createdUser;
    /**
     * 删除人
     */
    private String deleteUser;
    /**
     * 更新人
     */
    private Integer updateUser;
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    /**
     * 删除时间
     */
    private LocalDateTime deleteTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
