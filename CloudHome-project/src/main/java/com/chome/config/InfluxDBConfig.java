package com.chome.config;

import com.chome.domain.config.InfluxDBProperties;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.InfluxDBClientOptions;
import com.influxdb.client.WriteApi;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;


@Configuration
@RequiredArgsConstructor
public class InfluxDBConfig {

    private final InfluxDBProperties influxDBProperties;

    @Bean
    public InfluxDBClient influxDBClient() {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS);

        // 创建配置项，注入 OkHttpClient.Builder
        InfluxDBClientOptions options = InfluxDBClientOptions.builder()
                .url(influxDBProperties.getUrl())
                .authenticateToken(influxDBProperties.getToken().toCharArray())
                .org(influxDBProperties.getOrg())
                .bucket(influxDBProperties.getBucket())
                .okHttpClient(okHttpClientBuilder)
                .build();

        return InfluxDBClientFactory.create(options);
    }

    @Bean
    public WriteApi writeApi(InfluxDBClient client) {
        return client.makeWriteApi(); // 创建异步WriteApi单例
    }
}
