package com.chome.service.impl.voice;

import com.chome.constants.CHomeConstants;
import com.chome.domain.dto.AiQuestionDTO;
import com.chome.domain.vo.AIChatResponse;
import com.chome.service.AIEngineService;
import com.chome.service.IAsrService;
import com.chome.service.ITtsService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Description 模型语音管道会话
 * @Date 2025/5/10
 * @DAY_NAME_FULL: 星期六
 * @Version 1.0
 */

@Slf4j
@RequiredArgsConstructor
public class AudioPipelineSession {

    private final IAsrService asrService;

    private final AIEngineService aiService;

    private final ITtsService ttsService;

    private final String conversationId;

    @Getter
    private String fragmentId;

    private final AtomicBoolean asrActive = new AtomicBoolean(false);

    private final AtomicBoolean ttsActive = new AtomicBoolean(false);

    /**
     * 新一轮会话
     *
     * @param fragmentId 片段Id
     */
    public void newRound(String fragmentId) {
        this.fragmentId = fragmentId;
        this.asrActive.set(false);
        this.ttsActive.set(false);
    }

    /**
     * 启动 ASR 服务
     */
    public void startAsr() {
        if (!asrActive.compareAndSet(false, true)) {
            return;
        }
        asrService.startAsrStream(this::onAsrText);
        log.info("🎙️ ASR 启动 -> fragmentId={}, conversationId={}", fragmentId, conversationId);
    }

    /**
     * 接收到前端音频块
     *
     * @param audio 音频数据
     */
    public void receiveAudio(byte[] audio) {
        asrService.sendAudio(audio);
    }

    /**
     * 前端发 end，结束 ASR 输入
     */
    public void finishAsrInput() {
        asrService.stopAsr();
        log.info("🛑 ASR 输入结束 (conversationId={}, fragmentId={})", conversationId, fragmentId);
    }

    /**
     * ASR 回调
     *
     * @param asrText ASR 识别  文本
     */
    private void onAsrText(String asrText) {
        if (asrText == null || asrText.trim().isEmpty()) {
            return;
        }
        // 保证只触发一次 chat→tts
        if (!ttsActive.compareAndSet(false, true)) {
            log.warn("TTS 已启动，忽略重复 ASR 回调");
            return;
        }
        StringBuffer sb = new StringBuffer();

        // 构造 ai 对话请求
        AiQuestionDTO dto = new AiQuestionDTO();
        dto.setQuestion(asrText);
        dto.setConversationId(conversationId);
        dto.setChatMode(CHomeConstants.AUDIO_MODEL);

        ttsService.startStream();
        log.info("🎤 TTS 启动 -> fragmentId={}, conversationId={}", fragmentId, conversationId);

        Flux<AIChatResponse> flux = aiService.communication(dto)
                .doOnNext(resp -> {
                    String text = resp.getText();
                    if (text != null && !text.isBlank()) {
                        sb.append(text);
                        ttsService.appendText(text);
                    }
                })
                .doOnComplete(() -> {
                    log.info("✅ Chat 输出完成:{}", sb);
                    ttsService.markInputEnd();
                })
                .doOnError(e -> log.error("❌ Chat/TTS 流异常: {}", e.getMessage()));

        flux.subscribe();
    }

    /**
     * 关闭并释放资源
     */
    public void closeAll() {
        try {
            asrService.close();
        } catch (Exception e) {
            log.warn("关闭 ASR 失败", e);
        }
        log.info("会话 {} 完全关闭", fragmentId);
    }
}
