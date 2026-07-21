package com.chome.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chome.client.SpugPushClient;
import com.chome.domain.Enum.AlarmLevel;
import com.chome.domain.base.UserContext;
import com.chome.domain.entity.AlarmMessage;
import com.chome.domain.entity.User;
import com.chome.domain.vo.AlarmVO;
import com.chome.mapper.AlarmMapper;
import com.chome.service.IAlarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.chome.constants.CHomeConstants.*;

/**
 * @Description 警报信息功能实现类
 * @Date 2025/7/18
 * @DAY_NAME_FULL: 星期五
 * @Version 1.0
 */

@RequiredArgsConstructor
@Service
@Slf4j
public class AlarmServiceImpl extends ServiceImpl<AlarmMapper, AlarmMessage> implements IAlarmService {

    private final WebSocketService webSocketService;

    private final SpugPushClient spugPushClient;

    /**
     * 确认警报信息
     *
     * @param alarmId 警报Id
     */
    @Override
    public void confirmAlarm(String alarmId) {
        // 1. 检查警报信息状态
        AlarmMessage alarmMessage = getById(alarmId);
        if (ObjectUtil.isEmpty(alarmMessage)) {
            throw new RuntimeException("此警报信息不存在");
        }
        if (ALERT_CONFIRMED.equals(alarmMessage.getAlarmStatus())) {
            throw new RuntimeException("请勿重复确认警报:" + alarmId);
        }
        // 2. 设置确认信息
        alarmMessage.setAlarmStatus(ALERT_CONFIRMED);
        alarmMessage.setConfirmUser(UserContext.getCurrentUser().getId());
        alarmMessage.setConfirmTime(LocalDateTime.now());
        // 3. 更新数据库
        updateById(alarmMessage);
    }

    /**
     * 保存警报信息
     *
     * @param alarmMessage 警报信息
     */
    @Override
    public void saveMessage(AlarmMessage alarmMessage) {
        // 1. 设置 uuid 组合：uuid + 时间戳 (14位)
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String alarmId = now.format(formatter) + IdUtil.fastUUID().substring(0, 6);
        alarmMessage.setAlarmId(alarmId);
        // 2. 保存警报信息
        save(alarmMessage);
        // 3. WebSocket 发送通知
        webSocketService.sendAlarmData(alarmMessage);
        // 4. 判断告警等级，以决定告警方式(电话+wx公众号 / wx公众号)
        User user = UserContext.getCurrentUser();
        if (alarmMessage.getAlarmLevel().equals(AlarmLevel.HIGH) && user.getUserPhone() != null) {
            spugPushClient.pushAlarm(alarmMessage, ALL_ALARM_MODE, user.getUserPhone());
        } else {
            spugPushClient.pushAlarm(alarmMessage, WX_ALARM_MODE, null);
        }
    }

    /**
     * 获取所有警报信息
     *
     * @return 警报信息列表
     */
    @Override
    public List<AlarmVO> listAlarm() {
        // 1.查询数据库，获取警报信息列表
        List<AlarmMessage> alarmMessages = list();
        return alarmMessages.stream().map(alarmMessage -> {
            AlarmVO alarmVO = new AlarmVO();
            BeanUtil.copyProperties(alarmMessage, alarmVO);
            alarmVO.setAlarmLevel(alarmMessage.getAlarmLevel().getDescription());
            return alarmVO;
        }).toList();
    }

    /**
     * 获取指定id的警报信息
     *
     * @param alarmId 警报id
     * @return 警报信息
     */
    @Override
    public AlarmMessage getAlarmInfo(Integer alarmId) {
        // 1. 查询数据库，获取指定id的警报信息
        AlarmMessage alarmMessage = getById(alarmId);
        if (ObjectUtil.isNull(alarmMessage)) {
            throw new RuntimeException("未找到的此警报信息");
        }
        return alarmMessage;
    }

}
