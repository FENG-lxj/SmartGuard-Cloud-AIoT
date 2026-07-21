package com.chome.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 报警信息DTO
 * @Date 2025/7/19
 * @DAY_NAME_FULL: 星期六
 * @Version 1.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlarmDTO {

    /**
     * 报警ID
     */
    private String alarmId;
    /**
     * 报警类型
     */
    private String alarmType;
    /**
     * 报警内容
     */
    private String alarmText;
    /**
     * 报警等级
     */
    private Integer alarmLevel;
    /**
     * 关联设备id
     */
    private Integer alarmSourceId;
    /**
     * 设备名称
     */
    private String deviceName;
}
