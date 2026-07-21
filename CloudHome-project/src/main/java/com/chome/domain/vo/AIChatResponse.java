package com.chome.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ai.image.Image;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @Description 系统基础类
 * @Date 2025/5/13
 * @DAY_NAME_FULL: 星期二
 * @Version 1.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AIChatResponse {

    /**
     * 内容块
     */
    private String text;
    /**
     * 文生图的结果
     */
    private Image image;
    /**
     * 状态码
     */
    private int status;
    /**
     * 元数据（如会话 ID）
     */
    private Map<String, Object> metadata;
    /**
     * 时间戳
     */
    private LocalDateTime timestamp;
}