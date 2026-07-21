package com.chome.service.impl.voice.coze;

import com.chome.service.ITtsService;
import com.coze.openapi.client.websocket.event.downstream.*;
import com.coze.openapi.client.websocket.event.model.*;
import com.coze.openapi.service.service.CozeAPI;
import com.coze.openapi.service.service.websocket.audio.speech.WebsocketsAudioSpeechCallbackHandler;
import com.coze.openapi.service.service.websocket.audio.speech.WebsocketsAudioSpeechClient;
import com.coze.openapi.service.service.websocket.audio.speech.WebsocketsAudioSpeechCreateReq;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Description 语音合成服务接口实现类
 * @Date 2025/9/29
 * @DAY_NAME_FULL: 星期一
 * @Version 1.0
 */

@Slf4j
public class CozeTtsServiceImpl implements ITtsService {

    private final WebSocketSession clientSession;

    private final CozeAPI coze;

    private WebsocketsAudioSpeechClient ttsClient;

    private final ByteArrayOutputStream audioBuffer = new ByteArrayOutputStream();

    private final AtomicBoolean ttsActive = new AtomicBoolean(false);

    public CozeTtsServiceImpl(WebSocketSession clientSession, CozeAPI coze) {
        this.clientSession = clientSession;
        this.coze = coze;
    }

    /**
     * 启动 TTS 流式会话
     */
    @Override
    public synchronized void startStream() {
        if (ttsClient != null) {
            return;
        }
        // 清空之前 buffer，避免残留
        synchronized (audioBuffer) {
            audioBuffer.reset();
        }
        try {
            WebsocketsAudioSpeechCreateReq req = getReq();

            // 启动 Coze TTS 流
            this.ttsClient = coze.websockets().audio().speech().create(req);

            // 输出配置
            OutputAudio outputAudio = OutputAudio.builder()
                    .codec("pcm")
                    .speechRate(1)
                    // 湾湾小何
                    .voiceId("7426720361753968677")
                    .pcmConfig(PCMConfig.builder().sampleRate(24000).build())
                    .build();
            sendBinaryToClient("tts_start".getBytes());
            ttsClient.speechUpdate(new SpeechUpdateEventData(outputAudio));
            ttsActive.set(true);
            // 开始时向前端发送 start 标识
            clientSession.sendMessage(new BinaryMessage(ByteBuffer.wrap(new byte[]{1})));
            log.info("✅ TTS 流式会话启动成功");
        } catch (Exception e) {
            log.error("❌ 启动 TTS 流失败", e);
        }
    }

    /**
     * 结束流式会话,TTS 会话结束
     */
    @Override
    public void markInputEnd() {
        try {
            if (ttsClient != null && ttsActive.get()) {
                ttsClient.inputTextBufferComplete();
                ttsActive.set(false);
                log.info("✅ TTS 会话结束");
            }
        } catch (Exception e) {
            log.error("❌ 关闭 TTS 流失败", e);
        }
    }

    /**
     * 获取 TTS 流式会话创建请求
     *
     * @return TTS 流式会话创建请求
     */
    @NotNull
    private WebsocketsAudioSpeechCreateReq getReq() {
        WebsocketsAudioSpeechCallbackHandler handler = new WebsocketsAudioSpeechCallbackHandler() {
            @Override
            public void onSpeechAudioUpdate(WebsocketsAudioSpeechClient client, SpeechAudioUpdateEvent event) {
                byte[] delta = event.getDelta();
                if (delta != null && delta.length > 0) {
                    synchronized (audioBuffer) {
                        try {
                            audioBuffer.write(delta);
                        } catch (IOException e) {
                            log.error("写入音频缓冲失败", e);
                        }
                    }

                    sendBinaryToClient(delta);
                }
            }

            @Override
            public void onSpeechAudioCompleted(WebsocketsAudioSpeechClient client, SpeechAudioCompletedEvent event) {
                try {
                    log.info("✅ 收到音频生成完成事件，TTS 会话结束");
                    ttsActive.set(false);
                    // tts流程结束,发送 tts_end 标识
                    sendBinaryToClient("tts_end".getBytes());

                    client.close();
                    byte[] finalAudio = audioBuffer.toByteArray();
                    saveAsWav(finalAudio);
                    log.info("💾 已保存完整音频文件，长度={}字节", finalAudio.length);
                } catch (Exception e) {
                    log.error("❌ 处理音频完成事件失败", e);
                }
            }

            @Override
            public void onError(WebsocketsAudioSpeechClient client, ErrorEvent event) {
                log.error("❌ Coze TTS 错误: {}", event.getData().getLogID());
            }
        };
        // 创建请求
        return new WebsocketsAudioSpeechCreateReq(handler);
    }

    /**
     * 追加文本到流式 TTS 会话中
     *
     * @param text 要追加的文本片段
     */
    @Override
    public void appendText(String text) {
        if (ttsClient == null || !ttsActive.get()) {
            startStream();
        }
        try {
            if (text != null && !text.isBlank()) {
                ttsClient.inputTextBufferAppend(text);
                log.debug("💬 追加文本到 TTS: {}", text);
            }
        } catch (Exception e) {
            log.error("❌ 追加文本失败", e);
        }
    }

    private void saveAsWav(byte[] pcmBytes) {
        File file = new File("output.wav");
        try (ByteArrayInputStream bais = new ByteArrayInputStream(pcmBytes)) {
            AudioFormat format = new AudioFormat(24000, 2 * 8, 1, true, false);
            AudioInputStream ais = new AudioInputStream(bais, format, pcmBytes.length / 2);
            AudioSystem.write(ais, AudioFileFormat.Type.WAVE, file);
            log.info("🎧 已保存语音到文件: {}", file.getAbsolutePath());
        } catch (Exception e) {
            log.error("❌ 保存 WAV 文件失败", e);
        }
    }

    /**
     * 发送二进制数据到前端
     *
     * @param bytes 二进制数据
     */
    private void sendBinaryToClient(byte[] bytes) {
        if (clientSession == null || !clientSession.isOpen()) {
            return;
        }
        try {
            BinaryMessage audioSegment = new BinaryMessage(ByteBuffer.wrap(bytes));
            clientSession.sendMessage(audioSegment);
        } catch (IOException e) {
            log.error("发送音频到前端失败", e);
        }
    }

}
