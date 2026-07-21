package com.chome.service.impl.voice.coze;

import com.chome.service.IAsrService;
import com.coze.openapi.client.websocket.event.downstream.ErrorEvent;
import com.coze.openapi.client.websocket.event.downstream.TranscriptionsMessageCompletedEvent;
import com.coze.openapi.client.websocket.event.downstream.TranscriptionsMessageUpdateEvent;
import com.coze.openapi.client.websocket.event.model.InputAudio;
import com.coze.openapi.client.websocket.event.model.TranscriptionsUpdateEventData;
import com.coze.openapi.service.service.CozeAPI;
import com.coze.openapi.service.service.websocket.audio.transcriptions.WebsocketsAudioTranscriptionsCallbackHandler;
import com.coze.openapi.service.service.websocket.audio.transcriptions.WebsocketsAudioTranscriptionsClient;
import com.coze.openapi.service.service.websocket.audio.transcriptions.WebsocketsAudioTranscriptionsCreateReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * @Description Coze ASR 实现
 * @Date 2025/9/29
 * @DAY_NAME_FULL: 星期一
 * @Version 1.0
 */


@RequiredArgsConstructor
@Slf4j
public class CozeAsrServiceImpl implements IAsrService {

    private final CozeAPI coze;

    private final AtomicBoolean started = new AtomicBoolean(false);

    private volatile WebsocketsAudioTranscriptionsClient client;

    private Consumer<String> textCallback;

    @Override
    public void startAsrStream(Consumer<String> callback) {
        if (started.get()) {
            log.warn("ASR 已经启动，忽略重复调用");
            return;
        }
        this.textCallback = callback;
        this.started.set(true);

        try {
            WebsocketsAudioTranscriptionsCreateReq req = new WebsocketsAudioTranscriptionsCreateReq(
                    new WebsocketsAudioTranscriptionsCallbackHandler() {
                        private final StringBuilder fullText = new StringBuilder();

                        @Override
                        public void onTranscriptionsMessageUpdate(WebsocketsAudioTranscriptionsClient client,
                                                                  TranscriptionsMessageUpdateEvent event) {
                            String text = event.getData() != null ? event.getData().getContent() : null;
                            if (text != null && !text.isBlank()) {
                                text = text.substring(fullText.length());
                                fullText.append(text);
                            }
                        }

                        @Override
                        public void onTranscriptionsMessageCompleted(WebsocketsAudioTranscriptionsClient client,
                                                                     TranscriptionsMessageCompletedEvent event) {
                            String finalText = fullText.toString().trim();
                            log.info("✅ ASR 完成: {}", finalText);
                            if (!finalText.isEmpty() && textCallback != null) {
                                textCallback.accept(finalText);
                            }
                            close(); // 自动关闭连接
                        }

                        @Override
                        public void onError(WebsocketsAudioTranscriptionsClient client, ErrorEvent event) {
                            log.error("❌ ASR 错误: {}", event);
                        }
                    });

            this.client = coze.websockets().audio().transcriptions().create(req);

            InputAudio inputAudio = InputAudio.builder()
                    .sampleRate(24000)
                    .codec("pcm")
                    .format("pcm")
                    .channel(1)
                    .build();

            client.transcriptionsUpdate(new TranscriptionsUpdateEventData(inputAudio));
            log.info("✅ Coze ASR 会话启动成功");
        } catch (Exception e) {
            log.error("❌ ASR 启动失败", e);
        }
    } 

    /**
     * 接收音频流（前端发来的 PCM bytes）
     */
    @Override
    public void sendAudio(byte[] audio) {
        if (!started.get() || client == null) {
            log.warn("ASR 未启动或 client 未就绪，忽略音频帧");
            return;
        }
        try {
            client.inputAudioBufferAppend(audio);
        } catch (Exception e) {
            log.error("❌ 发送音频失败", e);
        }
    }

    /**
     * 前端发送 end 后调用 —— 等待最后一片 append 到达且“安静”一段时间后再发 complete
     */
    @Override
    public void stopAsr() {
        if (!started.get() || client == null) {
            log.warn("ASR 未启动或 client 未就绪");
            return;
        }
        try {
            client.inputAudioBufferComplete();
            log.info("📤 已发送 input_audio_buffer.complete");
        } catch (Exception e) {
            log.error("❌ 结束 ASR 输入失败", e);
        } finally {
            started.set(false);
        }
    }

    /**
     * 关闭释放
     */
    @Override
    public void close() {
        try {
            if (client != null) {
                client.close();
                log.info("🧹 ASR 会话关闭");
            }
        } catch (Exception e) {
            log.warn("关闭 ASR client 失败", e);
        } finally {
            started.set(false);
        }
    }
}
