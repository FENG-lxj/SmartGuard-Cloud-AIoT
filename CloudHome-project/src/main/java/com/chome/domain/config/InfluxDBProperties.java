package com.chome.domain.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Description influxDB配置属性
 * @Date 2025/4/18
 * @DAY_NAME_FULL: 星期五
 * @Version 1.0
 */

@Data
@Component
@ConfigurationProperties(prefix = "spring.influx")
public class InfluxDBProperties {

    /**
     * token
     */
    private String token;
    /**
     * org
     */
    private String org;
    /**
     * bucket
     */
    private String bucket;
    /**
     * url
     */
    private String url;

}
