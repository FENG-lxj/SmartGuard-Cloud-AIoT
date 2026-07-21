package com.chome.repository;

import com.chome.domain.dto.FurniturePowerDTO;
import com.chome.domain.dto.InfluxQueryDTO;
import com.chome.domain.vo.InfluxEnvironment;
import com.chome.utils.EnvironmentUtil;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxTable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.chome.constants.InfluxdbFlux.FLUX_QUERY_AFTER;

/**
 * @Description InfluxDB时序数据库持久层操作类
 * 负责环境时序数据写入、时间段数据查询、多条件自定义范围查询
 * 存储设备温湿度、氮磷钾等环境时序指标，基于InfluxDB 2.x Java SDK实现读写
 * @Date 2025/8/6
 * @DAY_NAME_FULL: 星期三
 * @Version 1.0
 */
// lombok日志注解，统一使用log打印日志
@Slf4j
// Spring持久层标识，交由Spring容器管理
@Repository
// lombok构造注入，自动注入所有final修饰的成员变量
@RequiredArgsConstructor
public class InfluxRepository {

    // InfluxDB客户端连接实例，全局单例，提供读写、查询能力
    private final InfluxDBClient influxdbClient;

    // 从配置文件读取InfluxDB存储桶名称，时序数据存储隔离单元
    @Value("${spring.influx.bucket}")
    private String bucketName;

    /**
     * 写入设备环境时序数据到InfluxDB
     * @param powerDTO 设备上报环境DTO，包含设备ID、位置、监测指标名、指标数值
     */
    public void saveEnvironment(FurniturePowerDTO powerDTO) {
        // 1.构建InfluxDB数据点Point
        // measurement：时序数据表名，统一为environment
        // tag：索引维度字段，用于快速筛选设备、位置，不参与数值计算
        // field：监测指标数值字段，存储环境实测数据
        Point point = Point.measurement("environment")
                // 位置标签，用于按区域筛选数据
                .addTag("locationName", powerDTO.getLocationName())
                // 设备唯一ID标签，区分不同硬件设备
                .addTag("deviceId", powerDTO.getDeviceId())
                // 动态指标字段：key=监测项名称，value=对应监测数值
                .addField(powerDTO.getN(), powerDTO.getV())
                // 设置时间戳，毫秒精度，使用当前系统时间
                .time(System.currentTimeMillis(), WritePrecision.MS);

        // 2.获取异步写入Api，非阻塞写入，提升高并发上报性能
        WriteApi writeApi = influxdbClient.makeWriteApi();
        // 执行单条时序数据写入
        writeApi.writePoint(point);
    }

    /**
     * 查询最近N秒内全部环境时序数据
     * @param seconds 往前查询的时间范围，单位：秒
     * @return 解析封装后的环境数据VO列表
     */
    public List<InfluxEnvironment> findLatestBefore(int seconds) {
        // 拼接Flux时间范围参数，格式：-xxs 代表往前xx秒
        String fluxTimeRange = String.format("-%ds", seconds);
        // 拼接完整预定义Flux查询语句
        String fluxCommand = String.format(FLUX_QUERY_AFTER, fluxTimeRange);
        // 执行Flux查询，返回多表结果集
        List<FluxTable> results = influxdbClient.getQueryApi().query(fluxCommand);

        List<InfluxEnvironment> environmentList = new ArrayList<>();
        // 遍历所有查询表，逐行解析时序记录
        results.forEach(result -> result.getRecords().forEach(record -> {
            InfluxEnvironment environment = new InfluxEnvironment();
            // 获取单条记录全部key-value数据
            Map<String, Object> environmentData = record.getValues();
            // 当前监测指标字段名
            String fieldName = String.valueOf(environmentData.get("_field"));
            // 当前监测指标数值
            Object value = environmentData.get("_value");

            try {
                // 通过反射匹配VO中与_field同名的属性
                Field field = InfluxEnvironment.class.getDeclaredField(fieldName);
                // 解除私有字段访问限制
                field.setAccessible(true);
                // 将数值统一转为Double赋值给VO属性
                field.set(environment, ((Number) value).doubleValue());
            } catch (Exception e) {
                // 反射匹配失败打印警告日志，不中断程序流程
                log.warn("无法匹配InfluxDB字段: [{}], 错误: {}", fieldName, e.getMessage());
            }
            // 赋值时序数据产生时间
            environment.setTimeStamp((LocalDateTime) environmentData.get("_time"));
            environmentList.add(environment);
        }));
        return environmentList;
    }

    /**
     * 多条件自定义时间范围查询环境时序数据
     * 支持起止时间、位置、设备ID、多监测指标筛选，无时间范围默认取最新一条指标数据
     * @param queryDTO 前端查询条件封装DTO
     * @return 转换完成的环境VO集合
     */
    public List<InfluxEnvironment> queryEnvironment(InfluxQueryDTO queryDTO) {
        // 拼接Flux查询语句字符串
        StringBuilder fluxQuery = new StringBuilder();
        // 基础：指定数据存储桶
        fluxQuery.append("from(bucket: \"").append(bucketName).append("\")")
                .append(" |> range(start: ");

        // 分支处理时间区间逻辑
        if (queryDTO.getStartTime() != null && !queryDTO.getStartTime().isEmpty()) {
            // 存在起始时间
            fluxQuery.append(queryDTO.getStartTime());
            if (queryDTO.getEndTime() != null && !queryDTO.getEndTime().isEmpty()) {
                // 起止时间都存在，查询闭区间数据
                fluxQuery.append(", stop: ").append(queryDTO.getEndTime());
            } else {
                // 仅起始时间，查询起始时间至当前实时数据
                fluxQuery.append(", stop: now()");
            }
        } else if (queryDTO.getEndTime() != null && !queryDTO.getEndTime().isEmpty()) {
            // 仅结束时间，查询0时刻至结束时间全部数据
            fluxQuery.append("-0, stop: ").append(queryDTO.getEndTime());
        } else {
            // 无起止时间，查询全量时间范围
            fluxQuery.append("-0");
        }
        fluxQuery.append(")");

        // 过滤measurement，仅查询环境数据表
        fluxQuery.append(" |> filter(fn: (r) => r._measurement == \"environment\")");

        // 按位置名称tag过滤
        if (queryDTO.getLocationName() != null && !queryDTO.getLocationName().isEmpty()) {
            fluxQuery.append(" |> filter(fn: (r) => r.location == \"")
                    .append(queryDTO.getLocationName())
                    .append("\")");
        }

        // 按设备ID tag过滤
        if (queryDTO.getDeviceId() != null && !queryDTO.getDeviceId().isEmpty()) {
            fluxQuery.append(" |> filter(fn: (r) => r.deviceId == \"")
                    .append(queryDTO.getDeviceId())
                    .append("\")");
        }

        // 多监测指标筛选，多条件or拼接
        if (queryDTO.getEnvironmentName() != null && !queryDTO.getEnvironmentName().isEmpty()) {
            fluxQuery.append(" |> filter(fn: (r) => ");
            for (int i = 0; i < queryDTO.getEnvironmentName().size(); i++) {
                if (i > 0) {
                    fluxQuery.append(" or ");
                }
                fluxQuery.append("r._field == \"")
                        .append(queryDTO.getEnvironmentName().get(i))
                        .append("\"");
            }
            fluxQuery.append(")");
        }

        // 无自定义时间范围：分组取每个指标最新一条数据
        if (queryDTO.getStartTime() == null && queryDTO.getEndTime() == null) {
            fluxQuery.append(" |> group(columns: [\"_field\"])")
                    .append(" |> last()");
        } else {
            // 有时间区间：按时间升序排序展示时序曲线
            fluxQuery.append(" |> sort(columns: [\"_time\"])");
        }

        // 执行Flux查询，流式处理结果，通过工具类统一转换VO并收集返回
        return influxdbClient.getQueryApi()
                .query(fluxQuery.toString())
                .stream()
                .flatMap(table -> table.getRecords().stream())
                .map(EnvironmentUtil::convertToInfluxEnvironment)
                .collect(Collectors.toList());
    }

}