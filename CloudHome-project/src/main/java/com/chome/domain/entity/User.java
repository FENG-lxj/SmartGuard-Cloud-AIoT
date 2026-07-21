package com.chome.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.chome.domain.base.SystemBase;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Description 用户表
 * @Date 2025/4/15
 * @DAY_NAME_FULL: 星期二
 * @Version 1.0
 */

@EqualsAndHashCode(callSuper = true)
@TableName("CHome_User")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class User extends SystemBase implements Serializable {

    /**
     * 用户id
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    private Integer id;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 密码
     */
    private String userPassword;
    /**
     * 头像
     */
    private String userAvatar;
    /**
     * 性别
     */
    private String userGender;
    /**
     * 年龄
     */
    private String userAge;
    /**
     * 地区
     */
    private String userRegion;
    /**
     * ip 属地
     */
    private String userIpRegion;
    /**
     * 手机号
     */
    private String userPhone;
    /**
     * 状态
     */
    private String userStatus;
    /**
     * 是否删除
     */
    @TableLogic
    private String isDelete;
    /**
     * 序列化版本号
     */
    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
