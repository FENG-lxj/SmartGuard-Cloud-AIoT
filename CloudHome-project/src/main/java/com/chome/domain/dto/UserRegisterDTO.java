package com.chome.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 用户注册请求参数
 * @Date 2025/9/16
 * @DAY_NAME_FULL: 星期二
 * @Version 1.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterDTO {

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不允许为空")
    private String phone;
    /**
     * 短信验证码
     */
    @NotBlank(message = "短信验证码不允许为空")
    private String smsCode;

}
