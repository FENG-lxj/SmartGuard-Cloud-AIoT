package com.chome.handler;

import com.chome.domain.entity.SessionManager;
import com.chome.domain.entity.UserSession;
import com.chome.service.impl.voice.AudioPipelineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.nio.ByteBuffer;

/**
 * @Description 语音对讲原生WebSocket处理器
 * 专门处理 /ws/audio 音频长连接，区分二进制音频流、文本控制指令
 * 负责会话创建、音频分片接收、启停语音识别、会话资源清理全生命周期管理
 * @Date 2025/9/28
 * @DAY_NAME_FULL: 星期日
 * @Version 1.0
 */
// Spring组件，由WebSocketHandlerMapping绑定/ws/audio路径
@Component
// 自动注入AudioPipelineService语音流水线服务
@RequiredArgsConstructor
// 日志注解，打印连接、音频流、异常日志
@Slf4j
// 继承BinaryWebSocketHandler，优先支持二进制音频消息，同时兼容文本指令
public class AudioWebSocketHandler extends BinaryWebSocketHandler {

    // 语音流水线核心服务：ASR语音识别、AI对话、TTS语音合成统一处理
    private final AudioPipelineService audioPipelineService;

    /**
     * WebSocket客户端建立连接成功回调
     * 作用：创建用户会话对象存入全局会话管理器，标记语音通道在线
     * @param session 当前客户端独立WebSocket会话
     */
    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) {
        // 封装当前连接会话信息
        UserSession userSession = new UserSession(session);
        // 以会话唯一ID为key存入全局会话管理容器
        SessionManager.put(session.getId(), userSession);
        log.info("🎧 WebSocket 语音通道已建立: {}", session.getId());
    }

    /**
     * 接收客户端上传的二进制音频分片（麦克风录音数据流）
     * @param session 当前语音会话
     * @param message 二进制消息，承载PCM音频字节流
     */
    @Override
    protected void handleBinaryMessage(WebSocketSession session, @NotNull BinaryMessage message) {
        // 获取二进制缓冲区
        ByteBuffer bb = message.getPayload();
        // 分配字节数组存储音频数据
        byte[] audio = new byte[bb.remaining()];
        bb.get(audio);
        // 将音频分片送入语音流水线进行流式ASR识别
        audioPipelineService.feedAudio(session.getId(), audio);
    }

    /**
     * 接收前端下发的JSON文本控制指令，控制语音流程启停
     * 三种指令：start开始收音识别、end暂停音频流、session_end销毁会话
     * @param session 当前语音会话
     * @param message 前端发送的文本控制指令
     */
    @Override
    protected void handleTextMessage(@NotNull WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
        String sessionId = session.getId();
        try {
            // 前端发送start指令：初始化ASR识别流水线，开始接收音频转文字
            if (payload.contains("\"start\"")) {
                log.info("🎙️ 启动新的语音识别会话");
                audioPipelineService.startSession(session);
            }
            // 前端发送end指令：停止接收音频输入，等待AI完整回复
            else if (payload.contains("\"end\"")) {
                log.info("🛑 结束当前音频流");
                audioPipelineService.endSessionInput(session);
            }
            // 前端发送session_end：完整结束本次语音对话，释放ASR/TTS资源
            else if (payload.contains("\"session_end\"")) {
                audioPipelineService.closeSession(sessionId);
                log.info("✅ 会话安全结束: {}", sessionId);
            }
        } catch (Exception e) {
            // 捕获指令处理异常，避免单条会话崩溃影响服务
            log.error("处理控制指令时发生错误: {}", e.getMessage(), e);
        }
    }

    /**
     * WebSocket连接断开回调（客户端关闭页面、网络中断、主动退出）
     * 作用：清理全局会话缓存，释放语音流水线资源，防止内存泄漏
     * @param session 断开的客户端会话
     * @param status 断开状态码，区分正常关闭/异常断开
     */
    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus status) {
        // 取出会话存储的对话标识
        String conversationId = session.getAttributes().get("conversationId").toString();
        // 全局会话管理器移除失效会话
        SessionManager.remove(session.getId());
        log.info("🔌 WebSocket 已关闭: {} (原因: {})", session.getId(), status);
    }

}