package com.chome.service.impl.voice;

import com.chome.factory.AiEngineFactory;
import com.chome.factory.AudioPipelineFactory;
import com.chome.service.AIEngineService;
import com.chome.service.IAsrService;
import com.chome.service.ITtsService;
import com.chome.service.impl.voice.coze.CozeAsrServiceImpl;
import com.chome.service.impl.voice.coze.CozeTtsServiceImpl;
import com.coze.openapi.service.service.CozeAPI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description 语音管道服务类
 * @Date 2025/5/10
 * @DAY_NAME_FULL: 星期六
 * @Version 1.0
 */

@RequiredArgsConstructor
@Service
@Slf4j
public class  AudioPipelineService {

    private final Map<String, AudioPipelineSession> conversationSessions = new ConcurrentHashMap<>();

    private final CozeAPI coze;

    private final AudioPipelineFactory audioPipelineFactory;

    /**
     * 启动ASR处理 ——onAsrResult——> endAsrAudio
     *
     * @param wsSession WebSocketSession
     */
    public void startSession(WebSocketSession wsSession) {
        String sessionId = wsSession.getId();
        String providerId = wsSession.getAttributes().get("providerId").toString();
        String conversationId = wsSession.getAttributes().get("conversationId").toString();
        String fragmentId = wsSession.getAttributes().get("fragmentId").toString();

        AudioPipelineSession session = conversationSessions.computeIfAbsent(sessionId, key -> {
            AudioPipelineComponents components = audioPipelineFactory.create(providerId, wsSession);
            AudioPipelineSession s = new AudioPipelineSession(
                    components.asr(),
                    components.ai(),
                    components.tts(),
                    conversationId);
            log.info("🧠 新建 AudioPipelineSession (sessionId={})", sessionId);
            return s;
        });
        session.newRound(fragmentId);
        session.startAsr();
    }

    /**
     * 输入音频数据
     *
     * @param sessionId 会话ID
     * @param audio     音频数据
     */
    public void feedAudio(String sessionId, byte[] audio) {
        AudioPipelineSession s = conversationSessions.get(sessionId);
        if (s != null) {
            s.receiveAudio(audio);
        } else {
            log.warn("❗ 未找到 sessionId={} 的 pipeline，丢弃音频", sessionId);
        }
    }

    /**
     * 停止数据输入 ASR
     *
     * @param wsSession WebSocketSession
     */
    public void endSessionInput(WebSocketSession wsSession) {
        String sessionId = wsSession.getId();
        String fragmentId = wsSession.getAttributes().get("fragmentId").toString();
        AudioPipelineSession session = conversationSessions.get(sessionId);
        if (session != null && fragmentId.equals(session.getFragmentId())) {
            session.finishAsrInput();
        }
    }

    /**
     * 关闭会话
     *
     * @param sessionId 会话ID
     */
    public void closeSession(String sessionId) {
        AudioPipelineSession s = conversationSessions.remove(sessionId);
        if (s != null) {
            s.closeAll();
            log.info("🧹 sessionId={} 已完全关闭", sessionId);
        }
    }
}
