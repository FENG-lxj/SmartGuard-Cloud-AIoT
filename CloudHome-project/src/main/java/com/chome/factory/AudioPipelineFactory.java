package com.chome.factory;

import com.chome.service.AIEngineService;
import com.chome.service.IAsrService;
import com.chome.service.ITtsService;
import com.chome.service.impl.voice.AudioPipelineComponents;
import com.chome.service.impl.voice.coze.CozeAsrServiceImpl;
import com.chome.service.impl.voice.coze.CozeTtsServiceImpl;
import com.chome.service.impl.voice.sparkAI.SparkAsrServiceImpl;
import com.chome.service.impl.voice.sparkAI.SparkTtsServiceImpl;
import com.chome.utils.SparkRequestUtil;
import com.coze.openapi.service.service.CozeAPI;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

/**
 * @Description 音频语音流水线工厂
 * 根据传入的语音厂商标识provider，动态组装一套完整语音处理组件：
 * ASR语音转文字 + AI大模型对话引擎 + TTS文字转语音
 * 目前支持两套语音方案：星火SparkAI、扣子Coze
 * @Date 2025/9/29
 * @DAY_NAME_FULL: 星期一
 * @Version 1.0
 */
// 注册为Spring容器组件，业务可直接注入使用
@Component
// lombok自动构造注入所有final成员变量
@RequiredArgsConstructor
public class AudioPipelineFactory {

    // Coze扣子平台API客户端，用于构建扣子语音识别、语音合成实例
    private final CozeAPI coze;

    // AI引擎工厂，根据provider获取对应大模型对话服务
    private final AiEngineFactory aiEngineFactory;

    // 配置文件读取：讯飞星火ASR接口地址
    @Value("${thirdparty.spark.ai.hosturlasr}")
    private String hostUrlAsr;

    /**
     * 创建一套完整语音处理流水线组件包
     * @param provider 语音服务商标识：spark=讯飞星火，其他=默认扣子Coze
     * @param ws 当前音频WebSocket会话对象，TTS合成流式音频需要通过ws推送给前端
     * @return 封装ASR、AI对话、TTS的完整语音流水线组件
     */
    public AudioPipelineComponents create(String provider, WebSocketSession ws) {
        // 1. 根据厂商标识获取对应大模型对话引擎
        AIEngineService ai = aiEngineFactory.get(provider);

        // 2. 判断是否为讯飞星火语音方案
        if ("spark".equalsIgnoreCase(provider)) {
            // 实例化星火语音识别ASR
            IAsrService asr = new SparkAsrServiceImpl(new SparkRequestUtil(), hostUrlAsr);
            // 实例化星火语音合成TTS，传入WebSocket会话用于推送音频流
            ITtsService tts = new SparkTtsServiceImpl(ws, new SparkRequestUtil(), hostUrlAsr);
            // 组装整套语音组件返回
            return new AudioPipelineComponents(asr, ai, tts);
        } else {
            // 默认使用扣子Coze语音方案
            // 扣子语音识别ASR
            IAsrService asr = new CozeAsrServiceImpl(coze);
            // 扣子语音合成TTS，绑定当前WebSocket会话推送音频
            ITtsService tts = new CozeTtsServiceImpl(ws, coze);
            return new AudioPipelineComponents(asr, ai, tts);
        }
    }
}