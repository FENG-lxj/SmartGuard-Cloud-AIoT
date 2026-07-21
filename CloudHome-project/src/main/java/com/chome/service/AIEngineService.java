package com.chome.service;

import com.chome.domain.dto.AiQuestionDTO;
import com.chome.domain.entity.AIChatMessage;
import com.chome.domain.entity.AIConversation;
import com.chome.domain.vo.AIChatResponse;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @Description AI服务接口
 * @Date 2025/4/13
 * @DAY_NAME_FULL: 星期日
 * @Version 1.0
 */

public interface AIEngineService {

    /**
     * 获取AI历史记录
     *
     * @return AI历史记录
     */
    List<AIConversation> getAiHistory();

    /**
     * 获取详细历史记录内容
     *
     * @param conversationId 历史记录ID
     * @return 详细历史记录内容
     */
    List<AIChatMessage> getHistoryInfo(String conversationId);

    /**
     * 新建一个会话
     *
     * @param userId 用户ID
     * @return 新建的会话ID
     */
    String createNewConversion(String userId);

    /**
     * 聊天功能
     *
     * @return 聊天结果
     */
    Flux<AIChatResponse> communication(AiQuestionDTO aiQuestionDTO);

    /**
     * 根据环境给出建议
     *
     * @return 建议
     */
    Flux<AIChatResponse> answerByEnvironment();

    /**
     * 删除历史记录
     *
     * @param conversationId 历史记录ID
     */
    void delHistory(String conversationId);

}
