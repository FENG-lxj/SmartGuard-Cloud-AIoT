package com.chome.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description AI对话会话Mongo实体
 * 对应MongoDB集合 CHome_chat_memory，存储用户与大模型的完整对话会话数据
 * 内置消息列表，封装新增消息方法，自动维护会话更新时间
 * @Date 2025/4/30
 * @DAY_NAME_FULL: 星期三
 * @Version 1.0
 */
// lombok：自动生成get/set/toString/equals/hashCode
@Data
// MongoDB文档注解，绑定集合名称
@Document("CHome_chat_memory")
// lombok构建者模式，快速构建会话对象
@Builder
public class AIConversation implements Serializable {

    /**
     * 会话唯一主键ID，MongoDB文档主键
     */
    @Id
    private String conversationId;

    /**
     * 当前会话归属用户ID，用于区分不同用户会话
     */
    private Integer userId;

    /**
     * 对话会话标题，可由AI自动生成或用户自定义
     */
    private String title;

    /**
     * 会话内全部问答消息集合，初始化空集合，存储用户提问、AI回复记录
     */
    private List<AIChatMessage> messages = new ArrayList<>();

    /**
     * 会话创建时间戳，Instant标准UTC时间格式
     */
    private Instant createTime;

    /**
     * 会话最后更新时间，新增消息时自动刷新
     */
    private Instant updateTime;

    /**
     * 序列化版本号
     * 1. 实现Serializable接口，用于Redis缓存、网络传输对象序列化
     * 2. @Serial Java16+标准注解，标识序列化版本常量，编译器校验合法性
     * 3. @TableField(exist = false) MyBatis-Plus注解，该字段不存在MySQL表，持久化时忽略映射
     * 4. static final静态常量，不属于文档业务字段，不会存入MongoDB
     * 5. 作用：反序列化时校验类结构一致性，版本不一致直接抛出序列化异常，防止数据错乱
     */
    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 向当前会话追加一条对话消息
     * @param message 单条聊天消息实体（用户提问/AI应答）
     */
    public void addMessage(AIChatMessage message) {
        // 判空兜底，防止消息列表未初始化空指针
        if (messages == null) {
            messages = new ArrayList<>();
        }
        // 将新消息加入集合尾部
        messages.add(message);
        // 新增消息同步刷新会话最后更新时间
        updateTime = Instant.now();
    }

    /**
     * 安全获取会话消息列表
     * @return 消息集合；无消息时返回不可变空列表，避免上层空指针
     */
    public List<AIChatMessage> getMessages() {
        // 使用MyBatis-Plus工具类判断集合是否为空
        if (CollectionUtils.isEmpty(messages)) {
            return List.of();
        }
        return messages;
    }
}