package com.chome;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @Description 项目启动类
 * @Date 2025/4/13
 * @DAY_NAME_FULL: 星期一
 * @Version 1.0
 */

@SpringBootApplication
@MapperScan("com.chome.mapper")
@EnableScheduling
@EnableAspectJAutoProxy
public class CloudHomeProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudHomeProjectApplication.class, args);
    }

}
