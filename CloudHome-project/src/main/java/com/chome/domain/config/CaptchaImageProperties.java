package com.chome.domain.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Description 验证码图片属性
 * @Date 2025/4/18
 * @DAY_NAME_FULL: 星期五
 * @Version 1.0
 */

@Data
@Component
@ConfigurationProperties(prefix = "captcha.image")
public class CaptchaImageProperties {

    /**
     * 验证码宽度
     */
    private Integer width;

    /**
     * 验证码高度
     */
    private Integer height;

    /**
     * 验证码的字符长度
     */
    private Integer length;
}