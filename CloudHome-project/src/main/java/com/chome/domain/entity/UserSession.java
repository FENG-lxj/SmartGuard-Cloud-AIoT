package com.chome.domain.entity;

import com.chome.service.AIEngineService;
import com.chome.service.IAsrService;
import com.chome.service.ITtsService;
import com.chome.service.impl.voice.AudioPipelineSession;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.socket.WebSocketSession;

/**
 * @Description 用户会话
 * @Date 2025/9/28
 * @DAY_NAME_FULL: 星期日
 * @Version 1.0
 */

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserSession {

    /**
     * 用户会话
     */
    public WebSocketSession clientSession;
    /**
     * 语音识别服务
     */
    public IAsrService asrService;
    /***
     * 聊天服务
     */
    public AIEngineService chatService;
    /**
     * 语音合成服务
     */
    public ITtsService ttsService;
    /**
     * 音频会话管道
     */
    public AudioPipelineSession pipelineSession;

    public UserSession(WebSocketSession session) {
        this.clientSession = session;
    }
}
