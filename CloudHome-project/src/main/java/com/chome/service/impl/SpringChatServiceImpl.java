package com.chome.service.impl;

import cn.hutool.json.JSONUtil;
import com.chome.domain.base.UserContext;
import com.chome.domain.dto.AiQuestionDTO;
import com.chome.domain.entity.AIChatMessage;
import com.chome.domain.entity.AIConversation;
import com.chome.domain.vo.AIChatResponse;
import com.chome.domain.vo.InfluxEnvironment;
import com.chome.repository.InfluxRepository;
import com.chome.repository.MongoConversationStore;
import com.chome.service.AIEngineService;
import com.chome.utils.EnvironmentUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.chome.constants.CHomeConstants.*;
import static com.chome.constants.RedisKeyPrefixes.PREFIX_ENVIRONMENT;
import static org.apache.http.HttpStatus.SC_OK;

/**
 * @Description 智能助手服务实现类
 * @Date 2025/4/25
 * @DAY_NAME_FULL: 星期五
 * @Version 1.0
 */

@RequiredArgsConstructor
@Service
@Slf4j
public class SpringChatServiceImpl implements AIEngineService {

    private final OpenAiChatModel chatModel;

    private final ChatMemory chatMemory;

    private final MongoConversationStore conversationStore;

    private final StringRedisTemplate redisTemplate;

    private final InfluxRepository influxRepository;

    @Value("classpath:Prompt_File/AnRanPrompt.txt")
    private Resource systemPromptResource;

    /**
     * 获取AI历史记录列表(只获取每个会话第一条)
     *
     * @return AI历史记录
     */
    @Override
    public List<AIConversation> getAiHistory() {
        Integer userId = UserContext.getCurrentUser().getId();
        List<AIConversation> conversations = conversationStore.loadHistoryByUser(userId);
        if (conversations == null || conversations.isEmpty()) {
            return Collections.emptyList();
        }
        conversations.forEach(conversation -> {
            AIChatMessage aiChatMessage = conversation.getMessages().get(0);
            String title = aiChatMessage.getContent();
            if (aiChatMessage.getContent().length() > 20) {
                title = aiChatMessage.getContent().substring(0, 20) + "...";
            }
            conversation.setTitle(title);
        });
        return conversations;
    }

    /**
     * 获取一条历史记录对话内容
     *
     * @param conversationId 历史记录ID
     * @return 历史记录对话内容
     */
    @Override
    public List<AIChatMessage> getHistoryInfo(String conversationId) {
        Integer userId = UserContext.getCurrentUser().getId();
        List<AIChatMessage> aiChatMessages = conversationStore.loadHistory(conversationId, userId);
        return aiChatMessages != null ? aiChatMessages : Collections.emptyList();
    }

    @Override
    public String createNewConversion(String userId) {
        return "";
    }

    /**
     * 删除历史记录
     *
     * @param conversationId 历史记录ID
     */
    @Override
    public void delHistory(String conversationId) {
        Integer userId = UserContext.getCurrentUser().getId();
        conversationStore.deleteHistory(conversationId, userId);
        // 同时清除短期记忆（Redis）
        chatMemory.clear(conversationId);
    }

    /**
     * AI 聊天
     *
     * @param aiQuestionDTO 聊天参数
     * @return 聊天结果
     */
    @Override
    public Flux<AIChatResponse> communication(AiQuestionDTO aiQuestionDTO) {
        Integer userId = (aiQuestionDTO.getCurrentUserId() == null)
                ? UserContext.getCurrentUser().getId()
                : aiQuestionDTO.getCurrentUserId();
        String conversationId = aiQuestionDTO.getConversationId();
        String chatMode = aiQuestionDTO.getChatMode();

        if (conversationId == null || conversationId.isEmpty()) {
            AIConversation newConv = conversationStore.createConversation(userId);
            conversationId = newConv.getConversationId();
        }

        //  1.1 把用户消息写入持久化（Mongo）
        AIChatMessage userAiMsg = AIChatMessage.builder()
                .content(aiQuestionDTO.getQuestion())
                .role(USER_ROLE)
                .chatModel(chatMode)
                .timestamp(Instant.now())
                .build();
        conversationStore.updateMessage(userAiMsg, conversationId, userId);
        // 1.2 把用户消息写入短期记忆（Redis）
        chatMemory.add(conversationId, new UserMessage(aiQuestionDTO.getQuestion()));

        // 1.3 从短期记忆中读取窗口消息（已包含系统提示）
        List<Message> memoryMessages = chatMemory.get(conversationId);
        List<Message> messages = new ArrayList<>();
        // 1.4 获取系统提示词(人设、技能、工作流)并加入
        messages.add(new SystemMessage(systemPromptResource));
        // 1.5 加入记忆中的消息
        messages.addAll(memoryMessages);
        Prompt prompt = new Prompt(messages);
        // 2.1 使用 StringBuilder 拼接所有 chunk
        StringBuilder sb = new StringBuilder();

        String finalConversationId = conversationId;
        // 2.2 持久化到 Mongo
        // 2.3 写入短期记忆（Redis）
        return chatModel.stream(prompt)
                .map(chatResponse -> {
                    String chunk = chatResponse.getResult().getOutput().getText();
                    synchronized (sb) {
                        sb.append(chunk);
                    }
                    // 2.4 返回响应数据
                    return AIChatResponse.builder()
                            .text(chunk)
                            .status(SC_OK)
                            .metadata(Map.of("conversationId", finalConversationId))
                            .timestamp(LocalDateTime.now())
                            .build();
                })
                .doOnComplete(() -> {
                    // 2.5 流结束：把合并后的完整回复一次性持久化到 Mongo，并写入短期记忆 Redis
                    String finalText;
                    synchronized (sb) {
                        finalText = sb.toString();
                    }
                    if (!finalText.isEmpty()) {
                        AIChatMessage assistantAiMsg = AIChatMessage.builder()
                                .content(finalText)
                                .role(AI_ROLE)
                                .chatModel(chatMode)
                                .timestamp(Instant.now())
                                .build();
                        conversationStore.updateMessage(assistantAiMsg, finalConversationId, userId);
                        chatMemory.add(finalConversationId, new AssistantMessage(finalText));
                    }
                });
    }

    /**
     * 根据环境信息给出实时建议
     *
     * @return 流式返回结果
     */
    @Override
    public Flux<AIChatResponse> answerByEnvironment() {
        String beforeEvntStr = redisTemplate.opsForValue().get(PREFIX_ENVIRONMENT);
        List<InfluxEnvironment> nowEvnt = influxRepository.findLatestBefore(FLUX_SECOND);
        if (beforeEvntStr != null && !beforeEvntStr.isEmpty()) {
            List<InfluxEnvironment> beforeEvnt = JSONUtil.toList(JSONUtil.parseArray(beforeEvntStr), InfluxEnvironment.class);
            if (!EnvironmentUtil.isSignificantChange(beforeEvnt, nowEvnt)) {
                return Flux.empty();
            }
        }
        redisTemplate.opsForValue().set(PREFIX_ENVIRONMENT, JSONUtil.toJsonStr(nowEvnt));
        chatMemory.add(REALTIME_SUGGESTION, new SystemMessage("环境信息：" + nowEvnt));


        return chatModel.stream(new Prompt(new UserMessage("请给出当前环境建议：" + nowEvnt)))
                .map(chatResponse -> {
                    String text = chatResponse.getResult().getOutput().getText();
                    return AIChatResponse.builder()
                            .text(text)
                            .status(SC_OK)
                            .timestamp(LocalDateTime.now())
                            .build();
                });
    }
}
