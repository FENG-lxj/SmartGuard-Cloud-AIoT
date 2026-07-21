package com.chome.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.chome.constants.CHomeConstants.JWT_EXPIRATION;

/**
 * @Description JWT工具类
 * @Date 2025/7/11
 * @DAY_NAME_FULL: 星期五
 * @Version 1.0
 */

@Component
public class JwtUtil {

    private static String SECRET;

    private static String BEARER_PREFIX;

    /**
     * 从请求头获取token
     *
     * @param request 请求
     * @return token
     */
    public static String getTokenFromHeader(HttpServletRequest request) {
        String Authorization = request.getHeader("Authorization");
        if (Authorization != null && Authorization.startsWith(BEARER_PREFIX)) {
            return Authorization.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    /**
     * 生成用户token,设置token超时时间
     *
     * @param userInfo 用户信息
     * @return token
     */
    public static String createToken(String userInfo) {
        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("typ", "JWT");
        headerMap.put("alg", "HS256");
        return  JWT.create()
                .withHeader(headerMap)// 添加头部
                //将基本信息放到claims中
                .withClaim("userInfo", userInfo)// userInfo
//                .withClaim("authList", JSON.toJSONString(authList))// authList
                .withExpiresAt(new Date(System.currentTimeMillis() + JWT_EXPIRATION)) //超时设置,设置过期的日期
                .withIssuer("CHome") //设置签发人
                .withIssuedAt(new Date()) //签发时间
                .sign(Algorithm.HMAC256(SECRET)); //使用HMAC256签名
    }

    /**
     * 校验token
     *
     * @param token 待校验的token
     * @return 验证成功返回true,否则返回false
     */
    public static boolean verifyToken(String token) {
        try {
            DecodedJWT jwt = JWT.require(Algorithm.HMAC256(SECRET)).build().verify(token);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 从 token 中获取 user 信息
     *
     * @param token 登录的 token
     * @return userInfo
     * @throws JWTDecodeException 解析失败
     */
    public static String getUserInfoForToken(String token) throws JWTDecodeException {
        return JWT.decode(token).getClaim("userInfo").asString();
    }

    @Value("${jwt.secret}")
    public void setSecret(String secret) {
        SECRET = secret;
    }

    @Value("${jwt.bearer-prefix}")
    public void setBearerPrefix(String bearerPrefix) {
        BEARER_PREFIX = bearerPrefix;
    }

}

