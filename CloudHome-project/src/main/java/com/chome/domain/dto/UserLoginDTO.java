package com.chome.domain.dto;
 
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Description 用户登录请求参数
 * @Date 2025/4/15
 * @DAY_NAME_FULL: 星期二
 * @Version 1.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDTO implements Serializable {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不允许为空", groups = {UsernameLogin.class})
    private String username;
    /**
     * 密码
     */
    @NotBlank(message = "密码不允许为空", groups = {UsernameLogin.class})
    private String password;
    /**
     * 手机号
     */
    @NotBlank(message = "手机号不允许为空", groups = {PhoneLogin.class})
    private String phone;
    /**
     * 短信验证码
     */
    @NotBlank(message = "短信验证码不允许为空", groups = {PhoneLogin.class})
    private String smsCode;
    /**
     * 图片验证码
     */
    @NotBlank(message = "图片验证码不允许为空", groups = {UsernameLogin.class})
    private String code;
    /**
     * uuid
     */
    @NotBlank(message = "uuid不允许为空", groups = {UsernameLogin.class})
    private String uuid;
    /**
     * 登录方式
     */
    @NotBlank(message = "请选择登录方式")
    private String loginType;

    private static final long serialVersionUID = 1L;

    // 校验分组接口
    public interface UsernameLogin {}
    public interface PhoneLogin {}
}