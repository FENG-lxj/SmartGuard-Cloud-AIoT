package com.chome.tool;

import com.chome.domain.dto.FurnitureOperationDTO;
import com.chome.domain.dto.PageRequest;
import com.chome.domain.vo.FurniturePowerVO;
import com.chome.domain.vo.FurnitureVO;
import com.chome.domain.vo.PageVO;
import com.chome.service.IFurnitureService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description Spring AI 自定义Function Call工具类 - 智能家居设备操作工具
 * 注册为大模型可调用函数工具，支持自然语言交互实现设备查询、设备控制下发
 * 所有标注@Tool的方法会被Spring AI自动扫描注册，大模型可根据用户语义自动触发调用
 * 业务约束：执行设备控制前，必须先查询设备可用功能，防止非法功能操作
 * @Date 2025/8/4
 * @DAY_NAME_FULL: 星期一
 * @Version 1.0
 */
// 将工具类交由Spring容器管理，供AI自动配置注入、扫描加载
@Component
// lombok构造注入，自动注入所有final修饰的业务服务
@RequiredArgsConstructor
// lombok日志注解，自动生成log对象，记录大模型工具调用日志便于排查
@Slf4j
public class DeviceControlTool {

    // 家居设备业务服务，封装设备查询、下发控制指令底层逻辑
    private final IFurnitureService furnitureService;

    /**
     * AI函数工具：下发智能家居设备控制指令
     * @Tool 标记为大模型可调用工具，定义工具名称与功能描述，大模型会依据描述判断何时调用
     * 业务规则：操作设备前必须先调用queryDevicePower获取设备支持的合法功能
     * @param deviceId 硬件设备唯一标识ID
     * @param powerName 待操作的设备功能名称（灯光、窗帘、空调开关等）
     * @param value 控制数值，0=开启，1=关闭
     * @return 操作结果文本，返回给大模型，由大模型整理话术回复前端用户
     */
    @Tool(name = "deviceControl", description = "控制智能家居设备。调用前必须先使用 queryDevicePower 获取该设备支持的功能，并确保参数正确")
    public String deviceControl(
            @ToolParam(description = "设备唯一ID") String deviceId,
            @ToolParam(description = "设备功能名称，如灯光、空调、窗帘") String powerName,
            @ToolParam(description = "功能控制值 0代表开启;1代表关闭") Double value) {
        // 构建设备操作传输对象，封装控制参数
        FurnitureOperationDTO furnitureOperationDTO = FurnitureOperationDTO.builder()
                .deviceId(deviceId)
                .powerName(powerName)
                .value(value)
                .build();
        // 打印工具调用日志，记录AI触发的设备操作，用于线上问题追溯
        log.info("AI调用设备控制工具: deviceId={}, powerName={}, value={}", deviceId, powerName, value);

        try {
            // 调用业务层下发控制指令，底层发送MQTT报文至硬件终端
            furnitureService.operationDevice(furnitureOperationDTO);
        } catch (Exception e) {
            // 捕获控制流程异常，返回异常文本给大模型，告知用户操作失败
            return String.format("设备操作失败: %s", e.getMessage());
        }
        // 指令下发成功，返回成功提示文本
        return String.format("设备 %s 的 %s 操作成功", deviceId, powerName);
    }

    /**
     * AI函数工具：查询系统内全部智能家居设备基础信息
     * 用户询问家中所有设备时，大模型自动调用该工具拉取全量设备列表
     * @return 格式化拼接后的所有设备文本信息，直接交付大模型处理
     */
    @Tool(
            name = "queryAllDevice",
            description = "获取当前系统中所有智能家居设备完整信息"
    )
    public String queryAllDevice() {
        log.info("AI调用全量设备查询工具");
        // 查询设备总条数
        int totalDeviceNum = furnitureService.query().list().size();
        // 分页参数设置：第一页、每页数量等于设备总数，一次性查出全部设备
        PageVO<FurnitureVO> furniturePageVO = furnitureService.queryFurnitureByType(new PageRequest(1, totalDeviceNum, totalDeviceNum, null));
        // 字符串拼接标准化设备信息文本
        StringBuilder sb = new StringBuilder("当前系统中所有设备信息：\n");
        for (FurnitureVO furniture : furniturePageVO.getData()) {
            sb.append(String.format("- ID:%s  设备名称:%s  安装位置:%s 在线状态:%s\n",
                    furniture.getDeviceId(),
                    furniture.getFurnitureName(),
                    furniture.getLocationName(),
                    furniture.getFurnitureStatus()));
        }
        // 附加提示，约束大模型避免重复调用该工具
        return sb + "已返回全部设备信息，无需重复调用工具";
    }

    /**
     * AI函数工具：查询单台设备全部可操作功能列表
     * 前置校验工具：执行deviceControl控制前，必须调用本接口确认功能合法可用
     * @param deviceId 目标硬件设备唯一ID
     * @return 格式化文本，包含设备所有功能名称、当前数值、功能分类
     */
    @Tool(
            name = "queryDevicePower",
            description = "查询指定设备的全部可操作功能（开关、亮度、温湿度等）。调用 deviceControl 之前必须使用本工具获取功能，仅控制类功能支持下发操作。"
    )
    public String queryDevicePower(
            @ToolParam(description = "待查询的设备唯一ID") String deviceId) {
        log.info("AI调用设备功能查询工具, deviceId:{}", deviceId);
        // 根据设备ID查询该设备绑定的全部功能项
        List<FurniturePowerVO> powerList = furnitureService.getPowerForDevice(deviceId);
        // 设备不存在或无功能，返回提示文本给大模型
        if (powerList == null || powerList.isEmpty()) {
            return String.format("未查询到设备ID为 '%s' 的设备或该设备无可用操作功能", deviceId);
        }
        // 拼接标准化功能信息文本
        StringBuilder sb = new StringBuilder("该设备可操作功能列表：\n");
        for (FurniturePowerVO power : powerList) {
            sb.append(String.format("- 设备ID:%s  功能名称:%s  当前数值:%s  功能分类:%s\n",
                    power.getDeviceId(),
                    power.getPowerName(),
                    power.getPowerValue(),
                    power.getPowerType()));
        }
        return sb.toString();
    }

}