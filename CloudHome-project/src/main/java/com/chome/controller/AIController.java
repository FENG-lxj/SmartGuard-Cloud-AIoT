package com.chome.controller;

import com.chome.domain.base.Result;
import com.chome.domain.dto.AiQuestionDTO;
import com.chome.domain.entity.AIChatMessage;
import com.chome.domain.entity.AIConversation;
import com.chome.domain.vo.AIChatResponse;
import com.chome.factory.AiEngineFactory;
import com.chome.service.AIEngineService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @Description Ai服务
 * @Date 2025/4/13
 * @DAY_NAME_FULL: 星期日
 * @Version 1.0
 */

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
@ApiResponse(description = "AI功能接口")
@Slf4j
public class AIController implements IController{

    private final AiEngineFactory aiEngine;
    
    private AIEngineService aiService;


    /**
     * 获取AI聊天记录
     *
     * @return 聊天记录
     */
    @GetMapping("/getHistory")
    public Result<List<AIConversation>> getAiHistory() {
        return Result.success(aiService.getAiHistory());
    }

    /**
     * 获取AI聊天记录详情
     *
     * @param conversationId 会话ID
     * @return 聊天记录详情
     */
    @GetMapping("/getHistoryInfo")
    public Result<List<AIChatMessage>> getHistoryInfo(@RequestParam String conversationId) {
        validate(conversationId);
        return Result.success(aiService.getHistoryInfo(conversationId));
    }

    /**
     * AI聊天
     *
     * @param aiQuestionDTO 聊天内容
     * @return 聊天结果
     */
    @PostMapping("/question")
    public Flux<AIChatResponse> chat(@RequestBody @Validated AiQuestionDTO aiQuestionDTO) {
        validate(aiQuestionDTO);
        return aiService.communication(aiQuestionDTO);
    }

    /**
     * 删除历史记录
     *
     * @param conversationId 会话ID
     * @return 删除结果
     */
    @PostMapping("/delHistory")
    public Result<Object> delHistory(@RequestParam("conversationId") String conversationId) {
        validate(conversationId);
        aiService.delHistory(conversationId);
        return Result.success();
    }

    /**
     * 根据实时环境数据给出实时建议
     *
     * @return 响应结果
     */
    @PostMapping("/realtimeTip")
    public Flux<AIChatResponse> answerByEnvironment() {
        return aiService.answerByEnvironment();
    }

    /**
     * 设置AI源
     *
     * @param source AI 源
     * @return 响应结果
     */
    @PostMapping("/aiSource")
    public Result<Object> aiSource(@RequestParam("source") String source) {
        this.aiService = aiEngine.get(source.trim());
        return Result.success();
    }
}
