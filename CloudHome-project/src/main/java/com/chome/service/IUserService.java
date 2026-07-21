package com.chome.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.chome.domain.dto.UserLoginDTO;
import com.chome.domain.dto.UserRegisterDTO;
import com.chome.domain.dto.UserUpdateDTO;
import com.chome.domain.entity.User;
import com.chome.domain.vo.UserLRVO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Description 用户服务
 * @Date 2025/4/13
 * @DAY_NAME_FULL: 星期日
 * @Version 1.0
 */

public interface IUserService extends IService<User> {

    /**
     * 用户登录
     *
     * @param loginDTO 登录参数
     * @return 登录结果
     */
    UserLRVO login(UserLoginDTO loginDTO);

    /**
     * 用户注册
     *
     * @param registerDTO 注册信息
     * @param isLogin 是否登录时未注册——直接注册
     * @return 用户登录信息
     */
    UserLRVO register(UserRegisterDTO registerDTO, boolean isLogin);

    /**
     * 手机(注册/登录)生成短信验证码
     *
     * @param phone 手机号
     * @param ip 用户请求的ip
     */
    void generateSmsCode(String phone, String ip);

    /**
     * 生成图片验证码
     *
     * @param uuid 验证码的uuid
     * @param response 响应的图片验证码
     */
    void generateCode(String uuid, HttpServletResponse response);

    /**
     * 修改用户头像
     *
     * @param avatar 头像文件
     */
    void updateAvatar(MultipartFile avatar);

    /**
     * 修改用户信息
     *
     * @param updateDTO 修改信息
     */
    void updateUserInfo(UserUpdateDTO updateDTO);

    /**
     * 退出登录
     */
    void logout();

}