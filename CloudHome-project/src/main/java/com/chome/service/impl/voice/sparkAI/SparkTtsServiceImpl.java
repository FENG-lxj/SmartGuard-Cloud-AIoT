package com.chome.service.impl.voice.sparkAI;

import com.chome.service.ITtsService;
import com.chome.utils.SparkRequestUtil;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.sound.sampled.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Description 星火模型TTS实现类
 * @Date 2025/5/12
 * @DAY_NAME_FULL: 星期一
 * @Version 1.0
 */

@Slf4j
public class SparkTtsServiceImpl implements ITtsService {

    private final WebSocketSession clientSession;

    private final SparkRequestUtil sparkRequestUtil;

    private final String hostUrlTts;

    private final OkHttpClient httpClient = new OkHttpClient.Builder().build();

    private WebSocket webSocket;

    private final ByteArrayOutputStream audioBuffer = new ByteArrayOutputStream();

    private final AtomicBoolean ttsActive = new AtomicBoolean(false);

    private final Gson gson = new Gson();

    private boolean firstSegmentSent = false;

    private int seq = 0;

    public SparkTtsServiceImpl(WebSocketSession clientSession,
                               SparkRequestUtil sparkRequestUtil,
                               String hostUrlTts) {
        this.clientSession = clientSession;
        this.sparkRequestUtil = sparkRequestUtil;
        this.hostUrlTts = hostUrlTts;
    }

    @Override
    public synchronized void startStream() {
        if (webSocket != null && ttsActive.get()) {
            // 已有活动会话
            return;
        }
        audioBuffer.reset();
        firstSegmentSent = false;
        seq = 0;

        try {
            String url = sparkRequestUtil.getAuthUrl(hostUrlTts);
            Request req = new Request.Builder().url(url).build();
            this.webSocket = httpClient.newWebSocket(req, new WebSocketListener() {
                @Override
                public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
                    ttsActive.set(true);
                    log.info("✅ Spark TTS websocket 开启");
                    // 发送 tts_start 标识给前端（与 Coze 行为保持一致）
                    sendBinaryToClient("tts_start".getBytes());
                }

                @Override
                public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
                    handleTextMessage(text);
                }

                @Override
                public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, Response response) {
                    log.error("❌ Spark TTS websocket 失败", t);
                    ttsActive.set(false);
                }

                @Override
                public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                    log.info("Spark TTS websocket 关闭: {} {}", code, reason);
                    ttsActive.set(false);
                }
            });

            log.info("✅ Spark TTS 会话启动成功");
        } catch (Exception e) {
            log.error("❌ 启动 Spark TTS 失败", e);
            ttsActive.set(false);
        }
    }

    @Override
    public synchronized void appendText(String text) {
        try {
            if (!ttsActive.get() || webSocket == null) {
                startStream();
            }
            if (text == null) {
                return;
            }

            int status;
            if (!firstSegmentSent) {
                status = 0;
                firstSegmentSent = true;
            } else {
                status = 1;
            }
            // 由 SparkRequestUtil 构建发送帧（需返回 String JSON）
            String frame = sparkRequestUtil.buildTtsRequest(text, status, seq++);
            boolean ok = webSocket.send(frame);
            if (!ok) {
                log.warn("SparkTts send frame 返回 false");
            }
            log.debug("💬 SparkTts appendText status={} seq={} textLen={}", status, seq, text.length());
        } catch (Exception e) {
            log.error("❌ SparkTts appendText 失败", e);
        }
    }

    @Override
    public synchronized void markInputEnd() {
        try {
            if (!ttsActive.get() || webSocket == null) {
                return;
            }
            // 发送结束帧（status=2），告诉服务端输入完成
            String endFrame = sparkRequestUtil.buildTtsRequest("", 2, seq++);
            webSocket.send(endFrame);
            log.info("📤 SparkTts 已发送结束帧 (status=2)");
            // 不立即 close，等待服务端把所有音频片段推回（在 onMessage 中处理 header.status==2）
        } catch (Exception e) {
            log.error("❌ SparkTts markInputEnd 失败", e);
        }
    }

    /**
     * 处理 Spark 返回的文本消息（里面包含 base64 audio）
     */
    private void handleTextMessage(String text) {
        try {
            // 解析为统一结构
            JsonParse json = gson.fromJson(text, JsonParse.class);
            if (json == null || json.header == null) {
                return;
            }

            if (json.header.code != 0) {
                log.warn("Spark TTS 返回错误 code={} sid={}", json.header.code, json.header.sid);
                return;
            }

            // payload.audio.audio 包含 base64 的 pcm 片段
            if (json.payload != null && json.payload.audio != null && json.payload.audio.audio != null) {
                byte[] decoded = Base64.getDecoder().decode(json.payload.audio.audio);
                synchronized (audioBuffer) {
                    try {
                        audioBuffer.write(decoded);
                    } catch (IOException e) {
                        log.error("写入 audioBuffer 失败", e);
                    }
                }
                // 将片段实时发给前端（binary）
                sendBinaryToClient(decoded);
            }

            // header.status==2 表示 TTS 服务端已经全部发送完毕（与 Coze 语义对齐）
            if (json.header.status == 2) {
                log.info("✅ 收到 Spark TTS 完成事件，合并并发送 tts_end");
                ttsActive.set(false);

                // 发送 tts_end 控制标识给前端
                sendBinaryToClient("tts_end".getBytes());

                // 可选：保存为 wav 文件以便调试
                byte[] finalAudio;
                synchronized (audioBuffer) {
                    finalAudio = audioBuffer.toByteArray();
                }
                if (finalAudio.length > 0) {
                    saveAsWav(finalAudio);
                    log.info("💾 Spark TTS 已保存完整音频，长度={} bytes", finalAudio.length);
                }

                // 关闭 websocket 连接
                try {
                    if (webSocket != null) {
                        webSocket.close(1000, "tts finished");
                    }
                } catch (Exception e) {
                    log.warn("关闭 Spark TTS websocket 失败", e);
                }
            }
        } catch (Exception e) {
            log.error("解析 Spark TTS onMessage 失败", e);
        }
    }

    private void sendBinaryToClient(byte[] bytes) {
        if (clientSession == null || !clientSession.isOpen()) {
            return;
        }
        try {
            clientSession.sendMessage(new BinaryMessage(ByteBuffer.wrap(bytes)));
        } catch (IOException e) {
            log.error("发送音频到前端失败", e);
        }
    }

    private void saveAsWav(byte[] pcmBytes) {
        // 将 PCM16LE(24000) -> WAV 保存，供本地调试
        File file = new File("spark_tts_output.wav");
        try (ByteArrayInputStream bais = new ByteArrayInputStream(pcmBytes)) {
            // 24000 Hz, 16 bit, mono, little-endian
            AudioFormat format = new AudioFormat(24000f, 16, 1, true, false);
            AudioInputStream ais = new AudioInputStream(bais, format, pcmBytes.length / 2);
            AudioSystem.write(ais, AudioFileFormat.Type.WAVE, file);
        } catch (Exception e) {
            log.warn("保存 Spark TTS wav 失败", e);
        }
    }

    /**
     * 完全关闭并释放资源
     */
    public synchronized void close() {
        try {
            if (webSocket != null) {
                webSocket.close(1000, "client close");
            }
            httpClient.dispatcher().executorService().shutdown();
        } catch (Exception e) {
            log.warn("关闭 SparkTtsServiceImpl 失败", e);
        } finally {
            ttsActive.set(false);
        }
    }

    // JSON 解析类（按 Spark 返回结构调整）
    private static class JsonParse {
        Header header;
        Payload payload;
    }
    private static class Header {
        int code;
        String sid;
        int status;
    }
    private static class Payload {
        Audio audio;
    }
    private static class Audio {
        String audio; // base64 音频片段
        int seq;
    }
}
