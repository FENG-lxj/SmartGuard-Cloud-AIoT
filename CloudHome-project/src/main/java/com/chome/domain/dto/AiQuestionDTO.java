package com.chome.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 智能助手问题DTO
 * @Date 2025/4/14
 * @DAY_NAME_FULL: 星期一
 * @Version 1.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiQuestionDTO {

    /**
     * 问题内容
     */
    @NotBlank(message = "问题不能为空")
    private String question;
    /**
     * 答案的创意性（0~1） 默认值0.5
     */
    private Double temperature;
    /**
     * 会话id
     */
    private String conversationId;
    /**
     * 聊天类型
     */
    @NotBlank(message = "请选择聊天类型")
    private String chatMode;
    /**
     * 语音聊天时存入userId
     */
    private Integer currentUserId;
}
