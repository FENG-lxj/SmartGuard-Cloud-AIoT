package com.chome.service.impl;

import com.alibaba.fastjson2.JSON;
import com.chome.domain.base.UserContext;
import com.chome.domain.dto.AiQuestionDTO;
import com.chome.domain.entity.AIChatMessage;
import com.chome.domain.entity.AIConversation;
import com.chome.domain.vo.AIChatResponse;
import com.chome.service.AIEngineService;
import com.coze.openapi.client.chat.CreateChatReq;
import com.coze.openapi.client.chat.model.ChatEvent;
import com.coze.openapi.client.chat.model.ChatEventType;
import com.coze.openapi.client.common.pagination.PageResp;
import com.coze.openapi.client.connversations.CreateConversationReq;
import com.coze.openapi.client.connversations.CreateConversationResp;
import com.coze.openapi.client.connversations.ListConversationReq;
import com.coze.openapi.client.connversations.message.ListMessageReq;
import com.coze.openapi.client.connversations.message.model.Message;
import com.coze.openapi.client.connversations.message.model.MessageRole;
import com.coze.openapi.client.connversations.model.Conversation;
import com.coze.openapi.client.exception.CozeApiException;
import com.coze.openapi.service.service.CozeAPI;
import io.reactivex.Flowable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.chome.constants.CHomeConstants.*;
import static com.chome.constants.RedisKeyPrefixes.PREFIX_AI_HISTORY;

/**
 * @Description coze 智能体平台服务实现类
 * @Date 2025/9/3
 * @DAY_NAME_FULL: 星期三
 * @Version 1.0
 */

@RequiredArgsConstructor
@Service
@Slf4j
public class CozeChatServiceImpl implements AIEngineService {

    private final CozeAPI cozeApi;

    private final StringRedisTemplate redisTemplate;

    @Value("${thirdparty.coze.botId}")
    private String botId;

    @Value("${thirdparty.coze.token}")
    private String apiKey;

    /**
     * 创建新的会话
     *
     * @return 会话 id
     */
    @Override
    public String createNewConversion(String userId) {
        CreateConversationReq req = CreateConversationReq.builder()
                .botID(botId)
                .metaData(Map.of("userId", userId)).build();
        CreateConversationResp resp = cozeApi.conversations().create(req);
        return resp.getConversation().getId();
    }

    /**
     * 获取智能体回复
     *
     * @param aiQuestionDTO 聊天参数
     * @return 聊天结果
     */
    @Override
    public Flux<AIChatResponse> communication(AiQuestionDTO aiQuestionDTO) {
        //String userId = UserContext.getCurrentUser().getId().toString();
        String userId = "1";

        String conversationId = aiQuestionDTO.getConversationId();
        // 如果没有会话 id，默认新创建（创建失败直接返回错误 Flux）
        if (conversationId == null || conversationId.isBlank()) {
            try {
                conversationId = createNewConversion(userId);
            } catch (Exception e) {
                return Flux.error(new RuntimeException("创建 Coze 会话失败: " + e.getMessage(), e));
            }
        }

        // 构建请求
        CreateChatReq req = CreateChatReq.builder()
                .botID(botId)
                .userID(userId)
                .conversationID(conversationId)
                .messages(Collections.singletonList(
                        Message.buildUserQuestionText(aiQuestionDTO.getQuestion()))
                )
                .build();

        Flowable<ChatEvent> chatEventFlowable = cozeApi.chat().stream(req);
        return Flux.from(chatEventFlowable)
                // 添加超时控制
                .timeout(Duration.ofSeconds(45))
                // 在收到完成事件时结束流
                .takeUntil(event -> ChatEventType.CONVERSATION_CHAT_COMPLETED.equals(event.getEvent()))
                // 只发出包含文本片段的
                .filter(event -> ChatEventType.CONVERSATION_MESSAGE_DELTA.equals(event.getEvent())
                        && event.getMessage() != null
                        && event.getMessage().getContent() != null
                        && !event.getMessage().getContent().trim().isEmpty())
                .map(event -> AIChatResponse.builder()
                        .text(event.getMessage().getContent())
                        .status(200)
                        .metadata(Map.of(
                                "role", event.getMessage().getRole(),
                                "message_id", event.getMessage().getId(),
                                "content_type", event.getMessage().getContentType()
                        ))
                        .timestamp(LocalDateTime.now())
                        .build())
                .onErrorMap(err -> {
                    // 特别处理 CozeApiException 反序列化问题
                    if (err instanceof CozeApiException) {
                        return new RuntimeException("Coze API 异常: " + err.getMessage(),
                                new Exception("原始错误信息: " + err.getClass().getName()));
                    }
                    return new RuntimeException("聊天异常: " + err.getMessage(), err);
                });
    }

    /**
     * 获取智能体环境分析回复
     *
     * @return 环境分析结果
     */
    @Override
    public Flux<AIChatResponse> answerByEnvironment() {
        return null;
    }

    /**
     * 获取历史会话
     *
     * @return 历史会话
     */
    @Override
    public List<AIConversation> getAiHistory() {
        String currentUserId = UserContext.getCurrentUser().getId().toString();
        String cacheKey = PREFIX_AI_HISTORY + currentUserId;
        // 1.1. 先从缓存 PREFIX_AI_HISTORY 取
        String cache = redisTemplate.opsForValue().get(cacheKey);
        if (cache != null) {
            return JSON.parseArray(cache, AIConversation.class);
        }
        // 1.2. 获取所有会话
        PageResp<Conversation> conversations = cozeApi.conversations()
                .list(ListConversationReq.of(botId));
        // 2.2. 遍历会话，匹配第一条消息作为标题
        List<AIConversation> aiConversations = conversations.getItems().stream()
                .filter(item -> currentUserId.equals(item.getMetaData().get("userId")))
                .map(item -> {
                    String conversationId = item.getId();
                    return AIConversation.builder()
                            .conversationId(conversationId)
                            .createTime(Instant.ofEpochSecond(item.getCreatedAt()))
                            .userId(Integer.valueOf(item.getMetaData().get("userId")))
                            .title("会话 #" + conversationId)
                            .build();
                })
                .toList();
        // 缓存ai会话记录，10分钟后过期
        redisTemplate.opsForValue().set(cacheKey, JSON.toJSONString(aiConversations), Duration.ofMinutes(10));
        return aiConversations;
    }

    /**
     * 获取历史会话消息
     *
     * @param conversationId 会话id
     * @return 历史会话消息
     */
    @Override
    public List<AIChatMessage> getHistoryInfo(String conversationId) {
        ListMessageReq req =
                ListMessageReq.builder().conversationID(conversationId).build();
        PageResp<Message> messagePageResp = cozeApi.conversations().messages().list(req);
        return messagePageResp.getItems().stream()
                .map(item -> AIChatMessage.builder()
                        .id(item.getId())
                        .content(item.getContent())
                        .role(item.getRole().equals(MessageRole.USER) ? USER_ROLE : AI_ROLE)
                        .chatModel(CHAT_MODEL)
                        .timestamp(Instant.ofEpochSecond(item.getCreatedAt()))
                        .build())
                .toList();
    }


    /**
     * 删除历史会话
     *
     * @param conversationId 历史会话id
     */
    @Override
    public void delHistory(String conversationId) {
        String userId = UserContext.getCurrentUser().getId().toString();
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.coze.cn/v1/conversations/" + conversationId))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .DELETE()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            boolean success = response.body().contains("\"code\":0");
            if (success) {
                // 删除缓存中的该用户会话列表，强制刷新
                redisTemplate.delete(PREFIX_AI_HISTORY + userId);
            }
        } catch (Exception e) {
            throw new RuntimeException("删除历史会话失败: " + e.getMessage(), e);
        }
    }
}
