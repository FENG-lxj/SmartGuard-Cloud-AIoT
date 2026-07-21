package com.chome.domain.Enum;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * @Description 告警等级枚举类
 * @Date 2025/9/12
 * @DAY_NAME_FULL: 星期五
 * @Version 1.0
 */

@Getter
@AllArgsConstructor
public enum AlarmLevel {

    /**
     * 中等
     */
    MEDIUM("info", "中等",  1),
    /**
     * 高
     */
    HIGH("warn", "严重", 2),
    /**
     * 危急
     */
    SEVERE("crit", "危急", 3);
    /**
     * 英文字典
     */
    private final String englishDic;
    /**
     * 描述
     */
    private final String description;
    /**
     * 等级
     */
    // 使用 @EnumValue 注解告诉 MyBatisPlus 需要映射的字段
    @EnumValue
    private final Integer level;

    /**
     * 根据 englishDic 获取枚举
     *
     * @param englishDic 英文字典
     */
    public static AlarmLevel fromEnglishDic(String englishDic) {
        for (AlarmLevel level : AlarmLevel.values()) {
            if (Objects.equals(level.englishDic, englishDic)) {
                return level;
            }
        }
        throw new IllegalArgumentException("未定级的告警等级 englishDic: " + englishDic);
    }

    /**
     * 根据 level 获取枚举
     *
     * @param level 等级
     */
    public static AlarmLevel fromLevel(Integer level) {
        for (AlarmLevel value : AlarmLevel.values()) {
            if (Objects.equals(value.level, level)) {
                return value;
            }
        }
        throw new IllegalArgumentException("未定级的告警等级 level: " + level);
    }
}

