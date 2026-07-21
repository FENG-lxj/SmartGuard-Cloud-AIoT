package com.chome.controller;

import com.chome.domain.base.Result;
import com.chome.domain.dto.FurnitureOperationDTO;
import com.chome.domain.dto.InfluxQueryDTO;
import com.chome.domain.dto.PageRequest;
import com.chome.domain.entity.AlarmMessage;
import com.chome.domain.entity.Furniture;
import com.chome.domain.vo.*;
import com.chome.repository.InfluxRepository;
import com.chome.service.IAlarmService;
import com.chome.service.IFurnitureService;
import com.chome.service.IPowerService;
import com.chome.utils.EnvironmentUtil;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description Coze 插件入口
 * @Date 2025/9/5
 * @DAY_NAME_FULL: 星期五
 * @Version 1.0
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/cozeApi")
@ApiResponse(description = "Coze 插件")
@Slf4j
public class CozePlugin {

    private final InfluxRepository influxRepository;

    private final IPowerService powerService;

    private final IAlarmService alarmService;

    private final IFurnitureService furnitureService;

    /**
     * 获取指定条件下的家庭环境信息列表(如果时间范围内无数据就返回最新的一组数据)
     *
     * @param queryDTO 查询参数包含以下可选参数：
     *                 - startTime: 开始时间 (ISO 8601格式，如 2025-09-21T14:18:51Z)
     *                 - endTime: 结束时间 (ISO 8601格式，如 2025-09-21T14:18:51Z)
     *                 - locationName: 位置名称
     *                 - deviceId: 设备ID
     *                 - environmentName: 环境参数名称列表
     *                 时间范围验证规则：
     *                 1. 如果提供了startTime和endTime，必须满足startTime < endTime且都在当前时间之前
     *                 2. 如果只提供startTime或endTime，需要在当前时间之前
     *                 3. 如果时间范围内无数据就返回最新的一组数据
     * @return 环境信息列表
     */
    @GetMapping("/getHomeEnvironment")
    public Result<List<InfluxEnvironment>> getHomeEnvironment(InfluxQueryDTO queryDTO) {
        // 1.1. 查询设备功能列表，找出环境监测类功能名列表
        List<String> powerNameList = powerService.queryPowerName();
        String start = queryDTO.getStartTime();
        String end = queryDTO.getEndTime();
        boolean checked = EnvironmentUtil.checkTimeRange(start, end);
        // 1.2. 只有在时间范围有效时才设置时间参数
        if (checked) {
            if (start != null && !start.isEmpty()) {
                queryDTO.setStartTime(start);
            }
            if (end != null && !end.isEmpty()) {
                queryDTO.setEndTime(end);
            }
        } else {
            // 1.3. 时间范围无效时，查询最新的数据
            log.info("时间范围无效，返回最新一组数据");
        }
        if (powerNameList != null && !powerNameList.isEmpty()) {
            queryDTO.setEnvironmentName(powerNameList);
        }
        // 2. 返回查询的环境信息
        return Result.success(influxRepository.queryEnvironment(queryDTO));
    }

    /**
     * 获取所有警报详细信息(聚合所有警报消息)
     *
     * @return 警报信息列表
     */
    @GetMapping("/listAlarm")
    public Result<List<AlarmMessage>> getAlarmInfoList() {
        List<Integer> alarmIdList = alarmService.listAlarm().stream()
                .map(AlarmVO::getAlarmId)
                .toList();
        List<AlarmMessage> alarmMessages = alarmService.listByIds(alarmIdList);
        return Result.success(alarmMessages);
    }

    /**
     * 设备控制工具
     *
     * @param deviceId  设备ID
     * @param powerName 功能名称
     * @param value     功能值
     * @return 设备控制结果
     */
    @PostMapping("/deviceControl")
    public Result<Object> deviceControl(@RequestParam String deviceId,
                                        @RequestParam String powerName,
                                        @RequestParam Double value) {
        // 构建功能操作参数
        FurnitureOperationDTO furnitureOperationDTO = FurnitureOperationDTO.builder()
                .deviceId(deviceId)
                .powerName(powerName)
                .value(value)
                .build();
        log.info("AI调用设备控制工具: deviceId={}, powerName={}, value={}", deviceId, powerName, value);
        Furniture device = furnitureService.query().eq("device_id", deviceId).one();
        try {
            furnitureService.operationDevice(furnitureOperationDTO);
        } catch (Exception e) {
            return Result.error("设备操作失败:" + e.getMessage());
        }
        return Result.success("设备 " + device.getFurnitureName() + " 的 " + powerName + " 操作成功");
    }

    /**
     * 查询所有设备
     *
     * @return 设备列表
     */
    @GetMapping("/queryAllDevice")
    public Result<List<FurnitureVO>> queryAllDevice() {
        int size = furnitureService.query().list().size();
        PageVO<FurnitureVO> furniturePageVO = furnitureService.queryFurnitureByType(new PageRequest(1, size, size, null));
        return Result.success(furniturePageVO.getData());
    }

    /**
     * 查询设备功能
     *
     * @param deviceId 设备ID
     * @return 设备功能列表
     */
    @GetMapping("/queryDevicePower")
    public Result<List<FurniturePowerVO>> queryDevicePower(@RequestParam String deviceId) {
        List<FurniturePowerVO> powers = furnitureService.getPowerForDevice(deviceId);
        if (powers == null || powers.isEmpty()) {
            return Result.error("未找到设备ID为 '" + deviceId + "' 的设备");
        }
        return Result.success(powers);
    }

}
