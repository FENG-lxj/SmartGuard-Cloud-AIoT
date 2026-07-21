package com.chome.domain.entity;

import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * @Description 聊天记录
 * @Date 2025/4/30
 * @DAY_NAME_FULL: 星期三
 * @Version 1.0
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AIChatMessage implements Serializable {

    /**
     * 聊天记录ID
     */
    @Id
    private String id = UUID.randomUUID().toString();
    /**
     * 聊天内容
     */
    private String content;
    /**
     * 角色
     */
    private String role;
    /**
     * 模式
     */
    private String chatModel;
    /**
     * 时间戳
     */
    private Instant timestamp;
    /**
     * 序列化版本号
     */
    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
