package com.chome.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 用户登录注册返回信息
 * @Date 2025/7/15
 * @DAY_NAME_FULL: 星期二
 * @Version 1.0
 */

/**
 * @Description 用户登录注册返回信息
 * @Date 2025/4/25
 * @DAY_NAME_FULL: 星期五
 * @Version 1.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserLRVO {

    /**
     * 用户id
     */
    private Integer userId;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 用户头像
     */
    private String userAvatar;
    /**
     * 用户状态
     */
    private String userStatus;
    /**
     * token
     */
    private String token;
}
