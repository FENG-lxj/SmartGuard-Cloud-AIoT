package com.chome.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.chome.domain.Enum.AlarmLevel;
import com.chome.domain.entity.AlarmMessage;
import com.chome.domain.vo.InfluxEnvironment;
import com.influxdb.query.FluxRecord;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import static com.chome.constants.CHomeConstants.ENVIRONMENT_ALARM_TYPE;

/**
 * @Description 用于实时建议，分析环境数据变化量
 * @Date 2025/7/11
 * @DAY_NAME_FULL: 星期五
 * @Version 1.0
 */

@Slf4j
public class EnvironmentUtil {

    /**
     * 判断环境数据是否有 significantChange
     *
     * @param beforeEvnt 之前的环境数据
     * @param nowEvnt    现在的环境数据
     * @return 是否 significantChange
     */
    public static boolean isSignificantChange(List<InfluxEnvironment> beforeEvnt, List<InfluxEnvironment> nowEvnt) {
        // TODO 待实现
        // 判断环境属性变化是否超过了阈值
        for (int i = 0; i < nowEvnt.size(); i++) {
        }
        return false;
    }

    /**
     * 将 FluxTable 转换为 InfluxEnvironment
     *
     * @param fluxRecord FluxRecord
     * @return InfluxEnvironment
     */
    public static InfluxEnvironment convertToInfluxEnvironment(FluxRecord fluxRecord) {
        Map<String, Object> values = fluxRecord.getValues();
        return InfluxEnvironment.builder()
                .deviceId(values.get("deviceId").toString())
                .environmentName(values.get("_field").toString())
                .environmentValue(values.get("_value").toString())
                .locationName(values.get("locationName").toString())
                .timeStamp(fluxRecord.getTime().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .build();
    }

    /**
     * 将 AlarmMessage 转换为 AlarmMessage
     *
     * @param infAlarmMessage AlarmMessage
     * @return AlarmMessage
     */
    public static AlarmMessage convertToAlarmMessage(String infAlarmMessage) {
        JSONObject entries = JSONUtil.parseObj(infAlarmMessage);
        // 拼接警报消息文本
        StringBuilder alarmText = new StringBuilder();
        String checkName = entries.get("_check_name").toString();
        String checkValue = entries.get(checkName).toString();
        alarmText.append(entries.get("_message").toString()).
                append(" ").append(checkName).append(": ").append(checkValue);
        String checkTime = entries.get("_time").toString();
        return AlarmMessage.builder()
                .alarmSourceId(entries.get("deviceId").toString())
                .alarmLevel(AlarmLevel.fromEnglishDic(entries.get("_level").toString()))
                .alarmText(alarmText.toString())
                .alarmStatus(0)
                .alarmType(ENVIRONMENT_ALARM_TYPE)
                .alarmTime(ZonedDateTime.parse(checkTime).toLocalDateTime())
                .build();
    }

    /**
     * 检查时间参数的格式和范围
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 检查结果
     */
    public static boolean checkTimeRange(String start, String end) {
        // 获取当前时间
        ZonedDateTime now = ZonedDateTime.now();
        try {
            // 初始化时间参数
            ZonedDateTime startTime = null;
            ZonedDateTime endTime = null;
            // 如果 start 不为空，解析并验证它在当前时间之前
            if (StrUtil.isNotBlank(start)) {
                startTime = ZonedDateTime.parse(start);
                if (!startTime.isBefore(now)) {
                    return false;
                }
            }
            // 如果 end 不为空，解析并验证它在当前时间之前
            if (StrUtil.isNotBlank(end)) {
                endTime = ZonedDateTime.parse(end);
                if (!endTime.isBefore(now)) {
                    return false;
                }
            }
            // 如果 start 和 end 都不为空，验证 start 在 end 之前
            if (startTime != null && endTime != null) {
                return startTime.isBefore(endTime);
            }
            return true;
        } catch (Exception e) {
            // 如果解析失败，返回false
            log.warn("时间参数解析失败: 开始时间:{}, 结束时间:{}, 错误:{}", start, end, e.getMessage());
            return false;
        }
    }
}
