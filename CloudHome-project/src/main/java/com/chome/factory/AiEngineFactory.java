package com.chome.factory;

import com.chome.service.AIEngineService;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Description AI引擎工厂类
 * 基于Spring自动注入所有AIEngineService接口实现类，根据providerId动态匹配对应AI大模型服务
 * 支持多AI厂商切换（如Coze、OpenAI、通义千问等），找不到指定实现时自动降级默认AI服务
 * @Date 2025/11/7
 * @DAY_NAME_FULL: 星期五
 * @Version 1.0
 */
// 注册为Spring组件，启动时自动实例化
@Component
public class AiEngineFactory {
    /**
     * Spring自动注入容器中所有实现AIEngineService的Bean
     * key = Bean名称，value = 对应AI引擎服务实例
     */
    private final Map<String, AIEngineService> services;

    /**
     * 构造注入：Spring自动收集全部AIEngineService实现类存入Map
     * @param services 所有AI引擎实现类集合
     */
    public AiEngineFactory(Map<String, AIEngineService> services) {
        this.services = services;
    }

    /**
     * 根据服务商标识获取对应AI引擎服务实例
     * 匹配规则：
     * 1. providerId为空，默认使用CozeChatServiceImpl，无则取第一个存在的AI服务
     * 2. 优先直接用providerId匹配Bean名称
     * 3. 匹配失败自动拼接后缀ChatServiceImpl二次匹配
     * 4. 两次匹配都失败，降级返回容器中第一个可用AI服务
     * @param providerId AI服务商标识（如coze、openai等）
     * @return 匹配到的AI引擎服务，无任何实现返回null
     */
    public AIEngineService get(String providerId) {
        // 未传入服务商标识，使用默认扣子AI
        if (providerId == null) {
            return services.getOrDefault("CozeChatServiceImpl", services.values().stream().findFirst().orElse(null));
        }
        // 第一次匹配：直接用providerId作为Bean名称查找
        AIEngineService svc = services.get(providerId);
        // 第一次匹配失败，拼接后缀重新匹配
        if (svc == null) {
            svc = services.get(providerId + "ChatServiceImpl");
        }
        // 匹配成功返回对应AI服务；匹配失败取容器内第一个AI实现兜底
        return svc != null ? svc : services.values().stream().findFirst().orElse(null);
    }
}