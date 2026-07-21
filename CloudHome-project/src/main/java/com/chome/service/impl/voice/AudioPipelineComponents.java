package com.chome.service.impl.voice;

import com.chome.service.AIEngineService;
import com.chome.service.IAsrService;
import com.chome.service.ITtsService;

/**
 * @Description 语音管道组件
 * @Date 2025/5/10
 * @DAY_NAME_FULL: 星期六
 * @Version 1.0
 */

public record AudioPipelineComponents(
        IAsrService asr,
        AIEngineService ai,
        ITtsService tts
) {}
