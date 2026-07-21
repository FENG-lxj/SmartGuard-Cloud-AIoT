package com.chome.service;


import java.util.function.Consumer;

/**
 * @Description 语音识别服务接口
 * @Date 2025/9/28
 * @DAY_NAME_FULL: 星期日
 * @Version 1.0
 */

public interface IAsrService {

    /**
     * 启动流式识别（前端发送 "start" 时调用）
     *
     * @param textCallback 每次识别到文本（partial/final）时的回调
     */
    void startAsrStream(Consumer<String> textCallback);

    /**
     * 发送音频数据（音频块 PCM）
     *
     * @param audio PCM 二进制数据
     */
    void sendAudio(byte[] audio);

    /**
     * 通知 ASR 输入流结束（前端发送 "end" 时调用）
     * Coze 内部会生成最终识别结果。
     */
    void stopAsr();

    /**
     * 完全关闭 ASR 服务（会话结束时调用）
     */
    default void close() {
        stopAsr();
    }
}
