package com.chome.controller;

import com.chome.domain.base.Result;
import com.chome.domain.entity.AlarmMessage;
import com.chome.domain.vo.AlarmVO;
import com.chome.service.IAlarmService;
import com.chome.utils.EnvironmentUtil;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description 警报服务
 * @Date 2025/7/21
 * @DAY_NAME_FULL: 星期一
 * @Version 1.0
 */

@RestController
@RequestMapping("/alarm")
@RequiredArgsConstructor
@ApiResponse(description = "警报信息功能")
@Slf4j
public class AlarmController implements IController {

    private final IAlarmService alarmService;

    /**
     * 保存influxdb发送的警报信息并通知
     *
     * @param infAlarmMessage 警报信息
     */
    @PostMapping("/saveAlarm")
    public void saveAlarm(@RequestBody String infAlarmMessage) {
        validate(infAlarmMessage);
        AlarmMessage alarmMessage = EnvironmentUtil.convertToAlarmMessage(infAlarmMessage);
        alarmService.saveMessage(alarmMessage);
    }

    /**
     * 确认警报信息
     *
     * @param alarmId 警报ID
     */
    @PostMapping("/confirmAlarm")
    public Result<Object> confirmAlarm(String alarmId) {
        validate(alarmId);
        alarmService.confirmAlarm(alarmId);
        return Result.success();
    }

    /**
     * 获取所有警报信息
     *
     * @return 警报信息列表
     */
    @GetMapping("/listAlarm")
    public Result<List<AlarmVO>> getAlarmInfoList() {
        return Result.success(alarmService.listAlarm());
    }

    /**
     * 获取详细警报信息
     *
     * @param alarmId 警报ID
     * @return 详细警报信息
     */
    @GetMapping("/getAlarmInfo")
    public Result<AlarmMessage> getAlarmInfo(@RequestParam Integer alarmId) {
        validate(alarmId);
        return Result.success(alarmService.getAlarmInfo(alarmId));
    }

}
