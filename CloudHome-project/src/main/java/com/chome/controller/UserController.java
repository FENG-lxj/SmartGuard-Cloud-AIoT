package com.chome.controller;

import com.chome.annotation.UpdateUserRegion;
import com.chome.domain.base.Result;
import com.chome.domain.dto.UserLoginDTO;
import com.chome.domain.dto.UserRegisterDTO;
import com.chome.domain.dto.UserUpdateDTO;
import com.chome.domain.entity.User;
import com.chome.domain.vo.UserLRVO;
import com.chome.service.IUserService;
import com.chome.utils.IpUtils;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Description 用户接口
 * @Date 2025/4/13
 * @DAY_NAME_FULL: 星期一
 * @Version 1.0
 */

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@ApiResponse(description = "用户管理")
@Slf4j
public class UserController implements IController {

    private final IUserService userService;

    /**
     * 登录
     *
     * @param loginDTO 用户登录信息
     * @return Result 登录结果
     */
    @PostMapping("/login")
    @UpdateUserRegion
    public Result<UserLRVO> login(@RequestBody UserLoginDTO loginDTO) {
        validate(loginDTO);
        return Result.success(userService.login(loginDTO));
    }

    /**
     * 注册
     *
     * @param registerDTO 用户注册信息
     * @return Result 注册结果
     */
    @PostMapping("/register")
    @UpdateUserRegion
    public Result<UserLRVO> register(@RequestBody UserRegisterDTO registerDTO) {
        validate(registerDTO);
        return Result.success(userService.register(registerDTO, false));
    }

    /**
     * 获取图片验证码
     *
     * @param response 响应，其中包含验证码图片
     */
    @GetMapping(value = "/captcha", produces = MediaType.IMAGE_PNG_VALUE)
    public void captcha(@RequestBody @NotBlank(message = "无法获取验证码") String uuid, HttpServletResponse response) {
        validate(uuid);
        userService.generateCode(uuid, response);
    }

    /**
     * 获取短信验证码
     *
     * @param phone 手机号
     * @param request 请求
     * @return Result 短信验证码发送结果
     */
    @GetMapping("/smsCode")
    public Result<Object> smsCode(@RequestParam @NotBlank(message = "请输入手机号") String phone, HttpServletRequest request) {
        validate(phone);
        userService.generateSmsCode(phone, IpUtils.getClientIp(request));
        return Result.success("验证码已发送，请查收!");
    }

    /**
     * 更新用户头像
     *
     * @param avatar 头像文件
     * @return Result 更新结果
     */
    @PostMapping("/avatar")
    public Result<Object> updateAvatar(@RequestParam("avatarFile") @NotBlank(message = "请选择头像") MultipartFile avatar) {
        validate(avatar);
        userService.updateAvatar(avatar);
        return Result.success("头像更新成功!");
    }

    /**
     * 动态更新用户信息
     *
     * @param updateDTO 用户信息
     * @return Result 更新结果
     */
    @PostMapping("/update")
    public Result<Object> updateUserInfo(@RequestBody UserUpdateDTO updateDTO) {
        validate(updateDTO);
        userService.updateUserInfo(updateDTO);
        return Result.success("更新成功!");
    }

    /**
     * 退出登录
     *
     * @return Result 退出结果
     */
    @PostMapping("/logout")
    public Result<Object> logout() {
        userService.logout();
        return Result.success("退出成功!");
    }

}