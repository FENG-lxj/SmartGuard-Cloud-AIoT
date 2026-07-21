package com.chome.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chome.client.SpugPushClient;
import com.chome.domain.config.CaptchaImageProperties;
import com.chome.domain.base.UserContext;
import com.chome.domain.dto.UserLoginDTO;
import com.chome.domain.dto.UserRegisterDTO;
import com.chome.domain.dto.UserUpdateDTO;
import com.chome.domain.entity.User;
import com.chome.domain.vo.UserLRVO;
import com.chome.mapper.UserMapper;
import com.chome.service.IUserService;
import com.chome.utils.BCryptUtil;
import com.chome.utils.JwtUtil;
import com.chome.utils.OssUtil;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.chome.constants.CHomeConstants.*;
import static com.chome.constants.RedisKeyPrefixes.*;

/**
 * @Description 用户服务实现类
 * @Date 2025/4/3
 * @DAY_NAME_FULL: 星期四
 * @Version 1.0
 */

@RequiredArgsConstructor
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private final StringRedisTemplate redisTemplate;

    private final CaptchaImageProperties captchaImageProperties;

    private final SpugPushClient spugPushClient;

    private final UserMapper userMapper;

    private final OssUtil ossUtil;

    /**
     * 用户登录
     *
     * @param loginDTO 登录参数
     * @return 登录结果
     */
    @Override
    public UserLRVO login(UserLoginDTO loginDTO) {
        String loginType = loginDTO.getLoginType();
        User user = null;
        // 1. 判断登录方式（账号密码登录/手机号登录）
        if (USER_LOGIN_TYPE_USERNAME.equalsIgnoreCase(loginType)) {
            // 验证图形验证码
            String key = PREFIX_IMAGE_CODE + loginDTO.getUuid();
            String realCode = redisTemplate.opsForValue().get(key);
            if (realCode == null) {
                throw new RuntimeException("验证码已过期");
            } else if (!loginDTO.getCode().equalsIgnoreCase(realCode)) {
                throw new RuntimeException("验证码错误");
            }
            user = query().eq("user_name", loginDTO.getUsername()).one();
        } else if (USER_LOGIN_TYPE_PHONE.equalsIgnoreCase(loginType)) {
            user = query().eq("user_phone", loginDTO.getPhone()).one();
        }
        // 2. 构建失败次数 Key（优先使用 userId，否则用输入的账号/手机号）
        String attemptsKey;
        if (user != null) {
            attemptsKey = PREFIX_LOGIN_ATTEMPTS + user.getId();
        } else {
            throw new RuntimeException("登录失败");
        }
        // 3. 检查失败次数
        String attempts = redisTemplate.opsForValue().get(attemptsKey);
        if (attempts != null && Long.parseLong(attempts) >= MAX_LOGIN_ATTEMPTS) {
            redisTemplate.expire(attemptsKey, Duration.ofMinutes(MAX_LOGIN_RETRY_TIME));
            throw new RuntimeException("该账号已被暂时锁定, 请于 " + MAX_LOGIN_RETRY_TIME + " 分钟后再试");
        }
        // 4. 校验（密码/手机验证码）
        if (USER_LOGIN_TYPE_USERNAME.equalsIgnoreCase(loginType)) {
            // 用户名密码登录
            if (ObjectUtil.isEmpty(user) || !BCryptUtil.checkPassword(loginDTO.getPassword(), user.getUserPassword())) {
                redisTemplate.opsForValue().increment(attemptsKey, 1);
                throw new RuntimeException("账号或密码错误");
            }
        }else if (USER_LOGIN_TYPE_PHONE.equalsIgnoreCase(loginType)) {
            // 手机号验证码登录
            if (ObjectUtil.isEmpty(user)) {
                register(new UserRegisterDTO(loginDTO.getPhone(), loginDTO.getSmsCode()), true);
            }
            String key = PREFIX_PHONE_CODE + loginDTO.getPhone();
            String realCode = redisTemplate.opsForValue().get(key);
            if (realCode == null) {
                throw new RuntimeException("验证码已过期");
            } else if (!loginDTO.getSmsCode().equalsIgnoreCase(realCode)) {
                redisTemplate.opsForValue().increment(attemptsKey, 1);
                throw new RuntimeException("验证码错误");
            }
        }
        if (USER_STATUS_FORBIDDEN.equals(user.getUserStatus())) {
            throw new RuntimeException("该用户已被禁用，请联系管理员");
        }

        // 5. 登录成功，重置状态
        user.setUserStatus(USER_STATUS_NORMAL);
        updateById(user);
        // 生成 Token
        String userInfo = JSONUtil.toJsonStr(user);
        String token = JwtUtil.createToken(userInfo);
        redisTemplate.opsForValue().set(PREFIX_TOKEN + user.getId(), token, Duration.ofMillis(JWT_EXPIRATION));
        UserContext.setCurrentUser(user);

        return UserLRVO.builder()
                .userId(user.getId())
                .userName(user.getUserName())
                .userAvatar(user.getUserAvatar())
                .userStatus(user.getUserStatus())
                .token(token)
                .build();
    }

    /**
     * 用户注册
     *
     * @param registerDTO 注册信息
     * @return 用户登录信息
     */
    @Override
    public UserLRVO register(UserRegisterDTO registerDTO, boolean isLogin) {
        if (!isLogin) {
            // 1. 验证验证码
            String key = PREFIX_PHONE_CODE + registerDTO.getPhone();
            // 获取正确的验证码并校验
            String realCode = redisTemplate.opsForValue().get(key);
            if (realCode == null) {
                throw new RuntimeException("验证码已过期");
            } else if (!registerDTO.getSmsCode().equalsIgnoreCase(realCode)) {
                throw new RuntimeException("验证码错误");
            }
            // 2. 比较注册用户手机号信息
            if (query().eq("user_phone", registerDTO.getPhone()).one() != null) {
                throw new RuntimeException("该用户已存在");
            }
        }
        // 3.生成用户名后封装后存入数据库中
        String username = "user_" + UUID.randomUUID().toString().substring(0, 8);
        User user = User.builder()
                .userName(username)
                .userPassword(BCryptUtil.encrypt(DEFAULT_PASSWORD))
                .userStatus(USER_STATUS_NORMAL)
                .userPhone(registerDTO.getPhone())
                .userAvatar(DEFAULT_AVATAR_URL)
                .build();
        user.setCreatedTime(LocalDateTime.now());
        try {
            userMapper.saveUser(user);
        } catch (Exception e) {
            throw new RuntimeException("注册失败");
        }
        // 4. 生成jwt
        String userInfo = JSONUtil.toJsonStr(user);
        String token = JwtUtil.createToken(userInfo);
        //将token存入redis,并设置过期时间
        Duration duration = Duration.ofMillis(JWT_EXPIRATION);
        redisTemplate.opsForValue().set(PREFIX_TOKEN + user.getId(), token, duration);
        //将当前用户存入ThreadLocal中
        UserContext.setCurrentUser(user);
        // 注册成功
        return UserLRVO.builder()
                .userId(user.getId())
                .userName(username)
                .userAvatar(user.getUserAvatar())
                .userStatus(user.getUserStatus())
                .token(token).build();
    }

    /**
     * 更新用户头像
     *
     * @param avatar 头像文件
     */
    @Override
    public void updateAvatar(MultipartFile avatar) {
        //通过 OssUtil 上传图片
        String avatarUrl = ossUtil.uploadFile(avatar);
        Integer userId = UserContext.getCurrentUser().getId();
        //查询用户信息
        User user = getById(userId);
        user.setUserAvatar(avatarUrl);
        user.setUpdateTime(LocalDateTime.now());
        user.setUpdateUser(userId);
        updateById(user);
    }

    /**
     * 动态更新用户信息
     *
     * @param updateDTO 修改信息
     */
    @Override
    public void updateUserInfo(UserUpdateDTO updateDTO) {
        User currentUser = UserContext.getCurrentUser();
        Integer userId = currentUser.getId();
        User user = getById(userId);
        if (ObjectUtil.isNull(user)) {
            throw new RuntimeException("用户未登录，请重新登录!");
        }
        BeanUtil.copyProperties(updateDTO, user);
        user.setUpdateTime(LocalDateTime.now());
        user.setUpdateUser(userId);
        userMapper.updateUserInfo(user);
    }

    /**
     * 用户登出
     */
    @Override
    public void logout() {
        // 1.更新用户状态为已登出
        User currentUser = UserContext.getCurrentUser();
        currentUser.setUserStatus(USER_STATUS_LOGOUT);
        updateById(currentUser);
        // 2.将Redis中的token删除以及context中的用户信息删除
        Boolean deleted = redisTemplate.delete(PREFIX_TOKEN + UserContext.getCurrentUser().getId());
        if (!deleted) {
            throw new RuntimeException("用户退出登录失败!");
        }
        UserContext.clear();
        log.info("用户退出登录");
    }

    /**
     * 手机号(注册/登录)生成短信验证码
     *
     * @param phone 手机号
     * @param ip    用户ip
     */
    @Override
    public void generateSmsCode(String phone, String ip) {
        // 手机号限流 key
        String phoneKey = PREFIX_SMS_CODE_PHONE + phone;
        // IP限流 key
        String ipKey = PREFIX_SMS_CODE_IP + ip;
        // 1. 手机号 + IP 限流
        Long phoneCount = redisTemplate.opsForValue().increment(phoneKey, 1);
        if (phoneCount != null && phoneCount == 1) {
            redisTemplate.expire(phoneKey, SMS_CODE_TIME_WINDOW, TimeUnit.MINUTES);
        }
        if (phoneCount != null && phoneCount > MAX_SMS_CODE_REQUEST_COUNT) {
            throw new RuntimeException("获取验证码频繁，请稍后再试~");
        }
        Long ipCount = redisTemplate.opsForValue().increment(ipKey, 1);
        if (ipCount != null && ipCount == 1) {
            redisTemplate.expire(ipKey, SMS_CODE_TIME_WINDOW, TimeUnit.MINUTES);
        }
        if (ipCount != null && ipCount > MAX_SMS_CODE_IP_REQUEST_COUNT) {
            throw new RuntimeException("获取验证码频繁，请稍后再试~");
        }
        // 2. 发送短信验证码,并存入 redis 中
        String smsCode = spugPushClient.pushSmsCode(phone);
        String key = PREFIX_PHONE_CODE + phone;
        log.info("生成的短信验证码：{}", smsCode);
        redisTemplate.opsForValue().set(key, smsCode, Duration.ofMinutes(CODE_EXPIRATION_TIME));
    }

    /**
     * 生成图片验证码
     *
     * @param uuid     验证码的uuid
     * @param response 响应的图片验证码
     */
    @Override
    public void generateCode(String uuid, HttpServletResponse response) {
        JSONObject jsonObject = JSONUtil.parseObj(uuid);
        String Uuid = jsonObject.getStr("uuid");
        Captcha captcha = new SpecCaptcha(captchaImageProperties.getWidth(),
                captchaImageProperties.getHeight());
        captcha.setLen(captchaImageProperties.getLength());
        captcha.setCharType(Captcha.TYPE_DEFAULT);

        String code = captcha.text();
        log.info("生成的图片验证码：{}", code);
        // 根据uuid拼接前缀得到验证码的key
        String key = PREFIX_IMAGE_CODE + Uuid;
        // 缓存验证码
        redisTemplate.opsForValue().set(key, code);
        // 设置验证码 CODE_EXPIRATION_TIME 分钟后过期
        redisTemplate.expire(key, CODE_EXPIRATION_TIME, TimeUnit.MINUTES);

        try {
            // 设置响应头
            response.setContentType("image/png");
            response.setDateHeader("Expires", 0);
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            // 输出验证码图片
            captcha.out(response.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException("验证码图片生成失败", e);
        } finally {
            // 确保输出流关闭
            try {
                response.flushBuffer();
            } catch (IOException e) {
                log.error("验证码图片输出流关闭失败", e);
            }
        }
    }
}
