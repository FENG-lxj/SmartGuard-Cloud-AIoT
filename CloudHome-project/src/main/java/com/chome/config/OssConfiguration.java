package com.chome.config;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class OssConfiguration {
    /**
     * 从配置文件中读取endPoint
     */
    @Value("${thirdparty.aliyun.oss.endpoint}")
    private String ossEndpoint;
    /**
     * 从配置文件中读取KeyId
     */
    @Value("${thirdparty.aliyun.oss.access-key-id}")
    private String keyId;
    /**
     * 从配置文件中读取KeySecret
     */
    @Value("${thirdparty.aliyun.oss.access-key-secret}")
    private String keySecret;

    @Bean
    public OSSClient ossClient() {
        return (OSSClient) new OSSClientBuilder().build(ossEndpoint, keyId, keySecret);
    }

}