package com.chome.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chome.config.TCPConnectionFactory;
import com.chome.domain.base.UserContext;
import com.chome.domain.dto.*;
import com.chome.domain.entity.Furniture;
import com.chome.domain.entity.FurniturePower;
import com.chome.domain.entity.Location;
import com.chome.domain.entity.User;
import com.chome.domain.vo.FurniturePowerVO;
import com.chome.domain.vo.FurnitureVO;
import com.chome.domain.vo.PageVO;
import com.chome.mapper.FurnitureMapper;
import com.chome.service.IFurnitureService;
import com.chome.service.IPowerService;
import com.chome.utils.CodeTransferUtil;
import com.chome.utils.TranscodingUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.chome.constants.CHomeConstants.*;
import static com.chome.constants.RedisKeyPrefixes.*;

/**
 * @Description 设备控制服务实现类
 * @Date 2025/4/3
 * @DAY_NAME_FULL: 星期四
 * @Version 1.0
 */

@RequiredArgsConstructor
@Service
@Slf4j
public class FurnitureServiceImpl extends ServiceImpl<FurnitureMapper, Furniture> implements IFurnitureService {

    private final IPowerService powerService;

    private final StringRedisTemplate redisTemplate;

    private final TCPConnectionFactory tcpFactory;

    private final WebSocketService webSocketService;

    private final FurnitureMapper furnitureMapper;

    /**
     * 设备上线时更新数据库信息
     *
     * @param furnitureOnline 设备上线信息
     */
    @Override
    public void onlineUpdate(FurnitureOnlineDTO furnitureOnline) {
        // 校验用户
        User currentUser = UserContext.getCurrentUser();
        if (ObjectUtil.isNull(currentUser)) {
            throw new RuntimeException("用户未登录，无法接入设备");
        }
        // 1. 解析数据
        String deviceId = furnitureOnline.getDeviceId();
        Integer furnitureType = furnitureOnline.getFurnitureType();
        Integer furnitureStatus = furnitureOnline.getFurnitureStatus();
        List<FurniturePower> powers = furnitureOnline.getPowers();

        // 2. 判断数据库中是否有该设备
        Furniture furniture = query().eq("device_Id", deviceId).one();
        if (ObjectUtil.isEmpty(furniture)) {
            // 2.1. 新设备，判断此设备是否存在扩展功能
            if (powers != null && !powers.isEmpty()) {
                powers.forEach(power -> power.setUpdateTime(LocalDateTime.now()));
                // 2.2.有扩展功能，暂存功能信息至Redis中,5分钟后删除
                redisTemplate.opsForValue().set(PREFIX_DEVICE_POWERS + deviceId,
                        JSON.toJSONString(powers),
                        DEVICE_ONLINE_EXPIRATION_TIME, TimeUnit.MINUTES);
            }
            // 2.3.推送至前端，填写自定义设备信息后保存到数据库中
            // 保存已上线待注册的设备标识
            redisTemplate.opsForValue().set(PREFIX_UNREGISTERED + deviceId, deviceId,
                    DEVICE_ONLINE_EXPIRATION_TIME, TimeUnit.MINUTES);
            webSocketService.sendNewDeviceAlert(FurnitureDTO.builder()
                    .deviceId(deviceId)
                    .furnitureType(furnitureType)
                    .furnitureStatus(furnitureStatus).build());
        } else {
            // 2.4.已有设备，更新数据库信息
            Integer userId = currentUser.getId();
            furniture.setUpdateTime(LocalDateTime.now());
            furniture.setUpdateUser(userId);
            updateById(furniture);
        }
    }

    /**
     * 前端返回新设备信息，保存至数据库中
     *
     * @param furnitureDTO 新设备信息
     */
    @Override
    @Transactional
    public void addNewFurniture(FurnitureDTO furnitureDTO) {
        // 1.校验设备上线信息是否过期
        if (!redisTemplate.hasKey(PREFIX_DEVICE_POWERS + furnitureDTO.getDeviceId())) {
            throw new RuntimeException("设备信息已过期!");
        }
        Furniture furniture = BeanUtil.copyProperties(furnitureDTO, Furniture.class);
        furniture.setCreatedTime(LocalDateTime.now());
        furniture.setCreatedUser(UserContext.getCurrentUser().getId());
        furniture.setFurnitureName(TranscodingUtil.unicodeToUtf8(furniture.getFurnitureName()));
        save(furniture);
        // 2.获取Redis中保存的扩展功能信息
        String powers = redisTemplate.opsForValue().get(PREFIX_DEVICE_POWERS + furnitureDTO.getDeviceId());
        if (powers != null) {
            List<FurniturePower> powerList = JSON.parseArray(powers, FurniturePower.class);
            // 转码power中的功能名字
            powerList.forEach(power -> {
                // 如果是环境功能，则将存入Mysql中的power中的值置空(值需存入InfluxDB中，Mysql中不保存)
                if (ENVIRONMENT_ABILITY.equals(power.getT())) {
                    power.setV(null);
                }
                power.setN(TranscodingUtil.unicodeToUtf8(power.getN()));
                power.setDeviceId(furnitureDTO.getDeviceId());
            });
            powerService.batchInsertFurniturePower(powerList);
        }

        //3.检查是否有未处理的 power 消息缓存
        String pendingKey = PREFIX_UNREGISTERED_POWERS + furnitureDTO.getDeviceId();
        // 3.1.获取缓存中的 power 消息
        List<String> powerList = redisTemplate.opsForList().range(pendingKey, 0, -1);
        if (CollectionUtils.isNotEmpty(powerList)) {
            powerList.forEach(msg -> {
                FurniturePowerDTO powerDTO = JSON.parseObject(msg, FurniturePowerDTO.class);
                powerService.updateFurniturePower(powerDTO);
            });
            // 清理缓存
            redisTemplate.delete(pendingKey);
        }
    }

    /**
     * 分页查询所有设备
     * 根据设备类型分页查询获取设备列表(可选:furnitureType)
     *
     * @param pageRequest 分页参数
     * @return 设备列表
     */
    @Override
    public PageVO<FurnitureVO> queryFurnitureByType(PageRequest pageRequest) {
        if (pageRequest.getTotal() <= 0) {
            Long count = query().select("device_Id").count();
            pageRequest.setTotal(count);
        }
        // 1. 创建 page 对象
        Page<Furniture> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize(), pageRequest.getTotal());
        // 2. 判断是否存在查询字段
        // 2.1. 构建初始查询条件
        QueryWrapper<Furniture> queryWrapper = new QueryWrapper<>();
        if (!CollectionUtils.isEmpty(pageRequest.getQueryConditions())) {
            // 2.2 存在查询字段,手动驼峰转换，构建查询条件
            pageRequest.getQueryConditions().forEach((k, v) -> queryWrapper.eq(TranscodingUtil.MysqlCamelCasing(k), v));
        }
        List<Furniture> furnitureList = page(page, queryWrapper).getRecords();
        if (CollectionUtils.isEmpty(furnitureList)) {
            throw new RuntimeException("查询结果不存在");
        }
        // 3. 获取LocationId集合，通过其查询LocationName
        List<Integer> locationIdList = furnitureList.stream()
                .map(Furniture::getLocationId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        // 4. 批量查询所有location信息
        List<Location> locationList = furnitureMapper.getLocationsByIds(locationIdList);
        // 建立 locationId -> roomName 映射
        Map<Integer, String> locationMap = locationList.stream()
                .collect(Collectors.toMap(Location::getId, Location::getRoomName));

        // 5. 转换设备列表为VO,除去已逻辑删除设备,并设置房间名称
        List<FurnitureVO> furnitureVOList = furnitureList.stream()
                .filter(f -> !DEVICE_DELETE_FLAG.equals(f.getIsDelete()))
                .map(f -> {
            FurnitureVO vo = BeanUtil.copyProperties(f, FurnitureVO.class);
            vo.setLocationName(locationMap.get(f.getLocationId()));  // 设置房间名称
            return vo;
        }).toList();
        // 6. 返回分页VO对象
        return PageVO.<FurnitureVO>builder()
                .total(page.getTotal())
                .data(furnitureVOList)
                .build();
    }

    /**
     * 设备操作
     *
     * @param operationDTO 设备操作DTO
     */
    @Override
    public void operationDevice(FurnitureOperationDTO operationDTO) {
        //1. 根据 furnitureId 获取扩展功能信息并校验
        List<FurniturePower> powers = powerService.queryPower(operationDTO.getDeviceId(), operationDTO.getPowerName());
        if (ObjectUtil.isEmpty(powers)) {
            throw new RuntimeException("此设备不存在此扩展功能");
        }
        FurniturePower power = powers.get(0);
        Integer powerType = power.getT();
        if (!CONTROL_ABILITY.equals(powerType)) {
            throw new RuntimeException("此设备功能不支持此操作");
        }
        if (power.getOperationCode().isEmpty()){
            throw new RuntimeException("此设备功能缺少操作码");
        }

        // 2.发送指令给硬件
        byte[] transfer = CodeTransferUtil.transfer(power.getOperationCode(), power.getV());
        try (Socket socket = tcpFactory.createSocket()) {
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(transfer);
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException("连接设备失败：" + e.getMessage());
        }
        FurniturePowerDTO powerDTO = FurniturePowerDTO.builder()
                .deviceId(operationDTO.getDeviceId())
                .n(operationDTO.getPowerName())
                .v(operationDTO.getValue()).build();
        powerDTO.setUpdateTime(LocalDateTime.now());
        powerService.updateFurniturePower(powerDTO);
    }

    /**
     * 获取设备扩展功能
     *
     * @param deviceId 设备ID
     * @return 设备扩展功能列表
     */
    @Override
    public List<FurniturePowerVO> getPowerForDevice(String deviceId) {
        List<FurniturePower> powerList = powerService.queryPower(deviceId, null);
        return powerList.stream()
                .map(power -> BeanUtil.copyProperties(power, FurniturePowerVO.class))
                .toList();
    }

    /**
     * 判断设备是否存在
     *
     * @param deviceId 设备ID
     * @return 设备是否存在
     */
    @Override
    public boolean isDeviceExists(String deviceId) {
        return ObjectUtil.isNull(lambdaQuery()
                .eq(Furniture::getDeviceId, deviceId)
                .one());
    }

}
