package com.chome.service;

import java.util.function.Consumer;

/**
 * @Description TODO
 * @Date 2025/9/28
 * @DAY_NAME_FULL: 星期日
 * @Version 1.0
 */
public interface ITtsService {

    /**
     * 启动 TTS 流
     */
    void startStream();

    /**
     * 追加文本到 TTS 流中
     *
     * @param text 要追加的文本片段
     */
    void appendText(String text);

    /**
     * 结束 TTS 流（可选）
     */
    void markInputEnd();

}
