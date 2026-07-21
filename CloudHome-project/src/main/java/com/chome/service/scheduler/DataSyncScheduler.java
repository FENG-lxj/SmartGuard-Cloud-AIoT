package com.chome.service.scheduler;

import com.chome.domain.dto.InfluxQueryDTO;
import com.chome.domain.vo.InfluxEnvironment;
import com.chome.repository.InfluxRepository;
import com.chome.service.IPowerService;
import com.chome.service.impl.WebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @Description 定时任务
 * @Date 2025/4/26
        * @DAY_NAME_FULL: 星期六
 * @Version 1.0
        */

@Service
@RequiredArgsConstructor
public class DataSyncScheduler {

    private final WebSocketService webSocketService;

    private final InfluxRepository influxRepository;

    private final IPowerService powerService;
    
    /**
     * 定时任务，每10秒执行一次
     */
    @Scheduled(cron = "0/15 * * * * ?")
    public void fetchAggregatedData() {
        // 1. 获取最近10秒的环境数据
        // 1.1 查询设备功能列表，找出环境监测类功能名列表
        List<String> powerNameList = powerService.queryPowerName();
        // 1.2 构建查询参数
        InfluxQueryDTO queryDTO = InfluxQueryDTO.builder()
                .environmentName(powerNameList)
                .startTime("-1h").build();
        List<InfluxEnvironment> environmentList = influxRepository.queryEnvironment(queryDTO);
        webSocketService.sendEnvironmentData(environmentList);
    }

}
