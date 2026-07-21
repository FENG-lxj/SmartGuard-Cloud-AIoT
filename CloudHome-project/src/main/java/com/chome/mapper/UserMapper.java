package com.chome.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chome.domain.dto.UserUpdateDTO;
import com.chome.domain.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description 用户Mapper
 * @Date 2025/4/25
 * @DAY_NAME_FULL: 星期五
 * @Version 1.0
 */

@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 保存用户并将生成的用户id赋值到user对象中
     *
     * @param user 用户对象
     */
    void saveUser(@Param("user") User user);

    /**
     * 动态更新用户信息
     *
     * @param user 用户信息
     */
    void updateUserInfo(User user);

    /**
     * 切面更新用户 ip 地址
     * @param userId  用户id
     * @param region  ip地址
     */
    void updateIpRegion(Integer userId, String region);
}
