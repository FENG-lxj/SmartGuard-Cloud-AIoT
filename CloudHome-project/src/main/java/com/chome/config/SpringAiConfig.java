package com.chome.config;

import com.alibaba.cloud.ai.memory.redis.RedisChatMemoryRepository;
import com.chome.tool.DeviceControlTool;
import com.chome.tool.ExternalApiTool;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.chome.constants.CHomeConstants.MEMORY_LENGTH;

/**
 * @Description Spring AI 大模型统一配置类
 * 功能：1. 初始化OpenAI兼容大模型客户端，绑定自定义工具调用
 *      2. 基于Redis实现对话历史持久化存储，控制上下文窗口长度
 * @Date 2025/8/9
 * @DAY_NAME_FULL: 星期六
 * @Version 1.0
 */
@Configuration
public class SpringAiConfig {

    // 大模型密钥，配置文件读取 spring.ai.openai.api-key
    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    // 大模型代理/中转地址，兼容OpenAI接口格式
    @Value("${spring.ai.openai.base-url}")
    private String baseUrl;

    // 使用的大模型名称 gpt-3.5-turbo/gpt-4o等
    @Value("${spring.ai.openai.chat.options.model}")
    private String model;

    // Redis服务地址，用于存储对话记忆
    @Value("${spring.data.redis.host}")
    private String host;

    // Redis端口号
    @Value("${spring.data.redis.port}")
    private Integer port;

    /**
     * 构建OpenAI对话大模型Bean
     * 1. 初始化OpenAI Api客户端，携带密钥和中转地址
     * 2. 注入自定义工具：设备控制工具、第三方接口调用工具，支持大模型Function Call
     * 3. 设置全局默认对话参数：模型名称、温度、绑定工具集
     * @param deviceControlTool 智能家居设备控制工具（开关灯、调节设备等）
     * @param externalApiTool 外部第三方接口查询工具
     * @return 可直接注入使用的OpenAiChatModel大模型客户端
     */
    @Bean
    public OpenAiChatModel openAiChatModel(DeviceControlTool deviceControlTool,
                                           ExternalApiTool externalApiTool) {
        // 构建OpenAI接口请求客户端
        OpenAiApi openAiApi = OpenAiApi.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .build();

        // 将自定义工具封装为大模型可识别的Tool回调，实现工具调用能力
        ToolCallback[] callbacks = ToolCallbacks.from(deviceControlTool, externalApiTool);

        // 全局对话配置：指定模型、随机性、绑定工具集
        OpenAiChatOptions chatOptions = OpenAiChatOptions.builder()
                .model(model)
                .temperature(0.7) // 温度0~1，数值越高回答随机性越强
                .toolCallbacks(callbacks) // 注入可用工具，大模型可自主调用
                .build();

        // 组装并返回大模型实例
        return OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(chatOptions)
                .build();
    }

    /**
     * Redis对话记忆持久化仓库
     * 作用：将用户每轮对话历史存入Redis，重启服务后上下文不丢失
     * @return Redis存储对话记录的仓库实例
     */
    @Bean
    public RedisChatMemoryRepository redisChatMemoryRepository() {
        return RedisChatMemoryRepository.builder()
                .host(host)
                .port(port)
                .build();
    }

    /**
     * 对话上下文记忆管理器
     * 采用滑动窗口记忆：仅保留最近 N 条对话，防止上下文过长超限
     * @param redisChatMemoryRepository Redis持久化仓库
     * @return ChatMemory 对话记忆对象，供ChatClient使用
     */
    @Bean
    public ChatMemory chatMemory(RedisChatMemoryRepository redisChatMemoryRepository) {
        return MessageWindowChatMemory.builder()
                .maxMessages(MEMORY_LENGTH) // 最大保留消息条数，常量统一管控
                .chatMemoryRepository(redisChatMemoryRepository) // 绑定Redis持久化
                .build();
    }

}