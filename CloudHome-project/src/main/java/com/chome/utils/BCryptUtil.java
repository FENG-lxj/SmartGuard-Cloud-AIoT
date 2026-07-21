package com.chome.utils;

import org.springframework.security.crypto.bcrypt.BCrypt;

import static com.chome.constants.CHomeConstants.SALT_VALUE;

/**
 * @Description BCrypt加密工具类
 * @Date 2025/4/26
 * @DAY_NAME_FULL: 星期六
 * @Version 1.0
 */

public class BCryptUtil {

    /**
     * 对密码进行 BCrypt 加密
     *
     * @param password 原始密码
     * @return 加密密码
     */
    public static String encrypt(String password) {
        String salt = BCrypt.gensalt(SALT_VALUE);
        return BCrypt.hashpw(password, salt);
    }

    /**
     * 对密码进行 BCrypt 验证
     *
     * @param plaintextPassword 原始密码
     * @param hashedPassword    加密密码
     * @return true/false
     */
    public static boolean checkPassword(String plaintextPassword, String hashedPassword) {
        return BCrypt.checkpw(plaintextPassword, hashedPassword);
    }

}
