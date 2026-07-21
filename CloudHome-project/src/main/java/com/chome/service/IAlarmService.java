package com.chome.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chome.domain.entity.AlarmMessage;
import com.chome.domain.vo.AlarmVO;

import java.util.List;

/**
 * @Description 警报服务
 * @Date 2025/7/18
 * @DAY_NAME_FULL: 星期五
 * @Version 1.0
 */

public interface IAlarmService extends IService<AlarmMessage> {

    /**
     * 获取所有警报信息
     *
     * @return 警报信息列表
     */
    List<AlarmVO> listAlarm();

    /**
     * 获取指定id的警报信息
     *
     * @param alarmId 警报id
     * @return 警报信息
     */
    AlarmMessage getAlarmInfo(Integer alarmId);

    /**
     * 确认/取消指定id的警报信息
     *
     * @param alarmId 警报id
     */
    void confirmAlarm(String alarmId);

    /**
     * 保存警报信息
     *
     * @param alarmMessage 警报信息
     */
    void saveMessage(AlarmMessage alarmMessage);
}
