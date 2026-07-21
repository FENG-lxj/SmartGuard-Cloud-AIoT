package com.chome.controller;

import com.chome.domain.base.Result;
import com.chome.domain.dto.FurnitureDTO;
import com.chome.domain.dto.FurnitureOperationDTO;
import com.chome.domain.dto.PageRequest;
import com.chome.domain.vo.FurniturePowerVO;
import com.chome.domain.vo.FurnitureVO;
import com.chome.domain.vo.PageVO;
import com.chome.service.IFurnitureService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description 家具控制层
 * @Date 2025/4/18
 * @DAY_NAME_FULL: 星期五
 * @Version 1.0
 */

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/furniture")
@ApiResponse(description = "设备管理")
@Slf4j
public class FurnitureController implements IController {

    private final IFurnitureService furnitureService;

    /**
     * 新增设备
     *
     * @param furnitureDTO 设备信息
     * @return 新增结果
     */
    @PostMapping("/addNewFurniture")
    public Result<String> addNewFurniture(@RequestBody FurnitureDTO furnitureDTO, @RequestParam boolean isAdd) {
        validate(furnitureDTO);
        if (isAdd) {
            furnitureService.addNewFurniture(furnitureDTO);
            return Result.success("已成功接入设备");
        } else {
            return Result.success("已取消设备接入");
        }
    }

    /**
     * 分页查询所有设备
     * 根据设备类型分页查询获取设备列表(可选:furnitureType)
     *
     * @param pageRequest 分页参数
     * @return 设备列表
     */
    @PostMapping("/getFurnitureList")
    public Result<PageVO<FurnitureVO>> getFurnitureList(@RequestBody PageRequest pageRequest) {
        validate(pageRequest);
        return Result.success(furnitureService.queryFurnitureByType(pageRequest));
    }

    /**
     * 获取此设备所有能力
     *
     * @param deviceId 设备ID
     * @return 设备属性列表
     */
    @GetMapping("/getPowerForDevice")
    public Result<List<FurniturePowerVO>> getPowerForDevice(@RequestParam String deviceId) {
        validate(deviceId);
        return Result.success(furnitureService.getPowerForDevice(deviceId));
    }

    /**
     * 设备操作
     *
     * @param operationDTO 设备操作参数
     * @return 设备操作结果
     */
    @PostMapping("/operationDevice")
    public Result<String> operatingDevice(@RequestBody FurnitureOperationDTO operationDTO) {
        validate(operationDTO);
        furnitureService.operationDevice(operationDTO);
        return Result.success("操作成功");
    }

}
