package com.chome.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chome.domain.entity.AlarmMessage;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Description 警报信息Mapper
 * @Date 2025/7/18
 * @DAY_NAME_FULL: 星期五
 * @Version 1.0
 */

@Mapper
public interface AlarmMapper extends BaseMapper<AlarmMessage> {

}
