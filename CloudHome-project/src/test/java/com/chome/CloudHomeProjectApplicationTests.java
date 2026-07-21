package com.chome;

import com.chome.domain.dto.AiQuestionDTO;
import com.chome.service.ITtsService;
import com.chome.constants.CHomeConstants;
import com.chome.service.impl.CozeChatServiceImpl;
import com.chome.service.impl.voice.coze.CozeTtsServiceImpl;
import com.coze.openapi.service.service.CozeAPI;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.web.socket.*;
import reactor.core.publisher.Flux;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@Slf4j
@SpringBootTest
public class CloudHomeProjectApplicationTests {

    @Autowired
    private CozeChatServiceImpl aiService; // 你的 AI 对话服务（例如 Spark / Coze Chat）

    @Autowired
    private CozeAPI coze; // Coze SDK 主对象

    /**
     * 测试 Chat → TTS 语音合成流程
     */
    @Test
    public void testChatToTts() throws Exception {
        // 用于保存输出音频的伪 WebSocketSession
        FakeWebSocketSession fakeSession = new FakeWebSocketSession("output_test.wav");

        ITtsService ttsService = new CozeTtsServiceImpl(fakeSession, coze);
        ttsService.startStream();

        AiQuestionDTO dto = new AiQuestionDTO();
        dto.setQuestion("你好，请介绍一下你自己，并祝我今天开心。");
        dto.setChatMode(CHomeConstants.AUDIO_MODEL);

        final StringBuilder accumulated = new StringBuilder();

        Flux<?> flux = aiService.communication(dto)
                .doOnNext(resp -> {
                    String text = resp.getText();
                    if (text != null && !text.isBlank()) {
                        String newPart = text;
                        if (text.startsWith(accumulated.toString())) {
                            newPart = text.substring(accumulated.length());
                        }
                        if (!newPart.isBlank()) {
                            log.info("🗣️ 新增文本：{}", newPart);
                            ttsService.appendText(newPart);
                        }
                        accumulated.setLength(0);
                        accumulated.append(text);
                    }
                })
                .doOnComplete(() -> {
                    log.info("✅ Chat 输出完成，关闭 TTS");
                    ttsService.markInputEnd();
                })
                .doOnError(e -> log.error("❌ Chat/TTS 流异常: {}", e.getMessage()));

        flux.blockLast(); // 阻塞等待结束

        Thread.sleep(2000);
        fakeSession.close();

        log.info("🎧 音频生成完成：{}", new File("output_test.wav").getAbsolutePath());
    }

    /**
     * 模拟 WebSocketSession（不继承，只实现接口）
     */
    static class FakeWebSocketSession implements WebSocketSession {
        private final ByteArrayOutputStream pcmBuffer = new ByteArrayOutputStream();
        private final String outputFileName;

        public FakeWebSocketSession(String outputFileName) {
            this.outputFileName = outputFileName;
            File file = new File(outputFileName);
            if (file.exists()) file.delete();
        }

        @Override
        public void sendMessage(WebSocketMessage<?> message) throws IOException {
            if (message instanceof BinaryMessage binaryMessage) {
                byte[] bytes = binaryMessage.getPayload().array();
                pcmBuffer.write(bytes);
            } else if (message instanceof TextMessage textMessage) {
                log.info("🔤 收到文本消息：{}", textMessage.getPayload());
            }
        }

        @Override
        public void close(CloseStatus status) throws IOException {
            saveAsWav(pcmBuffer.toByteArray(), outputFileName);
        }

        @Override
        public void close() throws IOException {
            saveAsWav(pcmBuffer.toByteArray(), outputFileName);
        }

        private void saveAsWav(byte[] pcmBytes, String fileName) {
            File file = new File(fileName);
            try (ByteArrayInputStream bais = new ByteArrayInputStream(pcmBytes)) {
                // 注意：参数必须与 TTS 返回音频完全一致（采样率24000, 16bit, 单声道）
                AudioFormat format = new AudioFormat(24000, 16, 1, true, false);
                AudioInputStream ais = new AudioInputStream(bais, format, pcmBytes.length / 2);
                AudioSystem.write(ais, AudioFileFormat.Type.WAVE, file);
                log.info("🎧 已保存测试语音到文件: {}", file.getAbsolutePath());
            } catch (Exception e) {
                log.error("❌ 保存测试 WAV 文件失败", e);
            }
        }

        @Override
        public boolean isOpen() {
            return true;
        }

        // 其他方法可直接返回默认值
        @Override public String getId() { return "fake-session"; }
        @Override public URI getUri() { return URI.create("ws://localhost/fake"); }
        @Override public HttpHeaders getHandshakeHeaders() { return new HttpHeaders(); }
        @Override public Map<String, Object> getAttributes() { return Map.of(); }
        @Override public Principal getPrincipal() { return null; }
        @Override public InetSocketAddress getLocalAddress() { return null; }
        @Override public InetSocketAddress getRemoteAddress() { return null; }
        @Override public String getAcceptedProtocol() { return null; }
        @Override public void setTextMessageSizeLimit(int limit) {}
        @Override public int getTextMessageSizeLimit() { return 0; }
        @Override public void setBinaryMessageSizeLimit(int limit) {}
        @Override public int getBinaryMessageSizeLimit() { return 0; }
        @Override public List<WebSocketExtension> getExtensions() { return List.of(); }
    }

}