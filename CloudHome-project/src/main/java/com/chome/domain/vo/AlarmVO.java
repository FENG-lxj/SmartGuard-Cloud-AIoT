package com.chome.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Description 报警信息VO
 * @Date 2025/7/21
 * @DAY_NAME_FULL: 星期一
 * @Version 1.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlarmVO {

    /**
     * 报警信息ID
     */
    private Integer alarmId;
    /**
     * 报警类型
     */
    private String alarmType;
    /**
     * 报警等级
     */
    private String alarmLevel;
    /**
     * 是否确认
     */
    private Integer isConfirm;
    /**
     * 报警时间
     */
    private LocalDateTime alarmTime;
}
