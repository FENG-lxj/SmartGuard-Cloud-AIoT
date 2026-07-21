package com.chome.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @Description 统一 RestTemplate 配置类
 * @Date 2025/9/13
 * @DAY_NAME_FULL: 星期六
 * @Version 1.0
 */

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
