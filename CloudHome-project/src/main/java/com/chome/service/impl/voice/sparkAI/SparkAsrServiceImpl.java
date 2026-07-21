package com.chome.service.impl.voice.sparkAI;

import com.chome.service.IAsrService;
import com.chome.utils.SparkRequestUtil;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * @Description 星火模型服务
 * @Date 2025/5/10
 * @DAY_NAME_FULL: 星期六
 * @Version 1.0
 */

@Slf4j
public class SparkAsrServiceImpl implements IAsrService {

    private final SparkRequestUtil sparkRequestUtil;

    private final String hostUrlAsr;

    private final OkHttpClient httpClient;

    private WebSocket webSocket;

    private volatile boolean connected = false;

    private final AtomicInteger seq = new AtomicInteger(1);

    private Consumer<String> textCallback;

    private final Gson gson = new Gson();

    private final Object lock = new Object();

    public SparkAsrServiceImpl(SparkRequestUtil sparkRequestUtil, String hostUrlAsr) {
        this.sparkRequestUtil = sparkRequestUtil;
        this.hostUrlAsr = hostUrlAsr;
        this.httpClient = new OkHttpClient.Builder().build();
    }

    @Override
    public void startAsrStream(Consumer<String> textCallback) {
        this.textCallback = textCallback;
        synchronized (lock) {
            if (connected && webSocket != null) {
                return;
            }
            try {
                String url = sparkRequestUtil.getAuthUrl(hostUrlAsr);
                Request req = new Request.Builder().url(url).build();
                webSocket = httpClient.newWebSocket(req, new WebSocketListener() {
                    @Override
                    public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
                        log.info("Spark ASR websocket opened");
                        connected = true;
                        seq.set(1);
                    }

                    @Override
                    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
                        handleMessage(text);
                    }

                    @Override
                    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, Response response) {
                        log.error("Spark ASR websocket failure", t);
                        connected = false;
                    }

                    @Override
                    public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                        log.info("Spark ASR websocket closing {} {}", code, reason);
                        connected = false;
                    }

                    @Override
                    public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                        log.info("Spark ASR websocket closed {} {}", code, reason);
                        connected = false;
                    }
                });
            } catch (Exception e) {
                log.error("启动 Spark ASR 失败", e);
            }
        }
    }

    @Override
    public void sendAudio(byte[] audio) {
        if (!connected || webSocket == null) {
            log.warn("ASR 未连接，忽略音频");
            return;
        }
        try {
            String frame;
            if (seq.get() == 1) {
                frame = sparkRequestUtil.buildASRFirstFrame(audio);
            } else {
                frame = sparkRequestUtil.buildASRFrame(audio, 1, seq.get());
            }
            boolean ok = webSocket.send(frame);
            if (!ok) {
                log.warn("ASR send 返回 false");
            }
            seq.incrementAndGet();
        } catch (Exception e) {
            log.error("ASR 发送音频失败", e);
        }
    }

    @Override
    public void stopAsr() {
        if (!connected || webSocket == null) {
            return;
        }
        try {
            String last = sparkRequestUtil.buildASRFrame(new byte[0], 2, seq.get());
            webSocket.send(last);
            // 让服务端主动返回最终结果后关闭；但如果需要我们可主动 close
        } catch (Exception e) {
            log.error("ASR stop 失败", e);
        }
    }

    @Override
    public void close() {
        try {
            if (webSocket != null) {
                webSocket.close(1000, "客户端关闭");
            }
            httpClient.dispatcher().executorService().shutdown();
        } catch (Exception e) {
            log.warn("关闭 ASR 连接错误", e);
        } finally {
            connected = false;
        }
    }

    // 解析服务端回包并回调文本（partial/final）
    private void handleMessage(String text) {
        try {
            JsonParse json = gson.fromJson(text, JsonParse.class);
            if (json == null || json.header == null) {
                return;
            }
            if (json.header.code != 0) {
                log.warn("ASR 返回错误 code={} msg={}", json.header.code, json.header.message);
                return;
            }
            if (json.payload != null && json.payload.result != null) {
                if (json.payload.result.text != null) {
                    byte[] decoded = Base64.getDecoder().decode(json.payload.result.text);
                    String decodedText = new String(decoded, StandardCharsets.UTF_8);
                    JsonParseText parseText = gson.fromJson(decodedText, JsonParseText.class);
                    // 把每个 cw.w 回调（可改为 accumulate）
                    for (Ws ws : parseText.ws) {
                        for (Cw cw : ws.cw) {
                            if (textCallback != null) {
                                textCallback.accept(cw.w);
                            }
                        }
                    }
                }

                if (json.payload.result.status == 2) {
                    // 最终结果通知（可约定特殊标识或把累积文本传回）
                    if (textCallback != null) {
                        textCallback.accept("[ASR_FINAL_END]");
                    }
                }
            }
        } catch (Exception e) {
            log.error("解析 ASR 回包失败", e);
        }
    }

    private static class JsonParse { Header header; Payload payload; }
    private static class Header { int code; String message; String sid; int status; }
    private static class Payload { Result result; }
    private static class Result { String text; int status; }
    private static class JsonParseText { List<Ws> ws; }
    private static class Ws { List<Cw> cw; }
    private static class Cw { String w; }
}
