package com.chome.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 修改用户信息 DTO
 * @Date 2025/9/17
 * @DAY_NAME_FULL: 星期三
 * @Version 1.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDTO {

    /**
     * 用户名
     */
    private String userName;
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
     * ip 所处地区
     */
    private String userIpRegion;
    /**
     * 备注
     */
    private String remark;

}
