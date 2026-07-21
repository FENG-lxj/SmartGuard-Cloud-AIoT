package com.chome.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chome.domain.dto.FurniturePowerDTO;
import com.chome.domain.entity.FurniturePower;
import com.chome.mapper.PowerMapper;
import com.chome.repository.InfluxRepository;
import com.chome.service.IPowerService;
import com.chome.utils.TranscodingUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.chome.constants.CHomeConstants.CONTROL_ABILITY;
import static com.chome.constants.CHomeConstants.ENVIRONMENT_ABILITY;
import static com.chome.constants.RedisKeyPrefixes.PREFIX_POWER_NAMES;
import static com.chome.constants.RedisKeyPrefixes.PREFIX_UNREGISTERED_POWERS;

/**
 * @Description 设备控制服务实现类
 * @Date 2025/4/3
 * @DAY_NAME_FULL: 星期四
 * @Version 1.0
 */

@RequiredArgsConstructor
@Service
@Slf4j
public class PowerServiceImpl extends ServiceImpl<PowerMapper, FurniturePower> implements IPowerService {

    private final PowerMapper powerMapper;

    private final StringRedisTemplate redisTemplate;

    private final InfluxRepository influxRepository;

    /**
     * 查询扩展功能数据
     *
     * @param deviceId  设备ID(必须)
     * @param powerName 功能名称(可选)
     * @return 功能数据
     */
    @Override
    public List<FurniturePower> queryPower(String deviceId, String powerName) {
        // 1. 构建查询条件语句
        QueryChainWrapper<FurniturePower> wrapper = query().eq("device_power_id", deviceId);
        if (powerName != null) {
            wrapper = wrapper.eq("power_name", powerName);
        }
        List<FurniturePower> powerList = wrapper.list();
        if (CollectionUtils.isEmpty(powerList)) {
            throw new RuntimeException("此设备不存在扩展功能");
        }
        // 2. 返回结果
        return powerList;
    }


    /**
     * 缓存未注册的功能数据
     *
     * @param powerDTO 功能数据
     */
    @Override
    public void cacheUnregisteredPower(FurniturePowerDTO powerDTO) {
        FurniturePower power = BeanUtil.copyProperties(powerDTO, FurniturePower.class);
        power.setUpdateTime(LocalDateTime.now());
        String cacheKey = PREFIX_UNREGISTERED_POWERS + power.getDeviceId();
        redisTemplate.opsForList().rightPush(cacheKey, JSON.toJSONString(power));
        // 缓存5分钟, 避免缓存过多数据
        redisTemplate.expire(cacheKey, 5, TimeUnit.MINUTES);
    }

    /**
     * 批量插入扩展功能数据
     *
     * @param powers 扩展功能数据
     */
    @Override
    public void batchInsertFurniturePower(List<FurniturePower> powers) {
        // 1. 插入新扩展功能数据
        powerMapper.batchInsertFurniturePower(powers);
        // 2. 查询最新功能名
        List<String> powerNameList = query()
                .select("power_name")
                .list()
                .stream()
                .map(FurniturePower::getN)
                .distinct()
                .toList();

        // 3. 写入缓存
        redisTemplate.opsForValue().set(
                PREFIX_POWER_NAMES,
                JSON.toJSONString(powerNameList),
                12, TimeUnit.HOURS
        );
    }

    /**
     * 更新扩展功能信息
     *
     * @param powerDTO 功能信息
     */
    @Override
    public void updateFurniturePower(FurniturePowerDTO powerDTO) {
        //1. 根据 furnitureId 查询设备信息获取对应的扩展功能信息
        List<FurniturePower> power = queryPower(powerDTO.getDeviceId(), powerDTO.getN());
        if (ObjectUtil.isEmpty(power)) {
            throw new RuntimeException("此设备不存在此扩展功能");
        }
        FurniturePower furniturePower = power.get(0);
        Integer powerType = furniturePower.getT();

        furniturePower.setN(TranscodingUtil.unicodeToUtf8(furniturePower.getN()));
        BeanUtil.copyProperties(powerDTO, furniturePower);
        if (furniturePower.getUpdateTime() == null) {
            furniturePower.setUpdateTime(LocalDateTime.now());
        }
        if (CONTROL_ABILITY.equals(powerType)) {
            // 2.控制类功能更新mysql中的状态信息
            furniturePower.setV(powerDTO.getV());
            powerMapper.updatePowerByIdAndName(furniturePower);
        } else if (ENVIRONMENT_ABILITY.equals(powerType)) {
            // 3.环境监测类功能数据存入influxdb
            // 3.1 根据deviceId 获取设备信息
            String locationName = powerMapper.getLocationNameByDeviceId(powerDTO.getDeviceId());
            powerDTO.setLocationName(locationName);
            // 3.2 保存数据
            influxRepository.saveEnvironment(powerDTO);
        }
    }

    /**
     * 查询功能名称
     *
     * @return 功能名称
     */
    @Override
    public List<String> queryPowerName() {
        // 1. 获取缓存中的功能数据
        String powerNameJsonStr = redisTemplate.opsForValue().get(PREFIX_POWER_NAMES);
        if (StrUtil.isNotBlank(powerNameJsonStr)) {
            // 缓存命中
            return JSON.parseArray(powerNameJsonStr, String.class);
        }
        // 命中空值，防止缓存穿透
        if ("".equals(powerNameJsonStr)) {
            return List.of();
        }
        // 2. 获取功能名称
        List<FurniturePower> powerList = query().list();
        if (CollectionUtils.isEmpty(powerList)) {
            // 数据库中也没有,短时间内设置为空值
            redisTemplate.opsForValue()
                    .set(PREFIX_POWER_NAMES, "",
                            2, TimeUnit.SECONDS);
            throw new RuntimeException("无功能数据");
        }
        // 去重
        List<String> powerNameList = powerList.stream()
                .filter(Objects::nonNull)
                .map(FurniturePower::getN)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        // 3. 缓存功能名称
        redisTemplate.opsForValue()
                .set(PREFIX_POWER_NAMES, JSON.toJSONString(powerNameList),
                        12, TimeUnit.HOURS);
        return powerNameList;
    }

}
