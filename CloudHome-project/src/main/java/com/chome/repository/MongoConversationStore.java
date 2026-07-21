package com.chome.repository;

import cn.hutool.core.util.ObjectUtil;
import com.chome.domain.entity.AIChatMessage;
import com.chome.domain.entity.AIConversation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @Description MongoDB AI对话会话持久层仓库
 * 基于MongoTemplate实现AI聊天会话、对话消息的增删改查，统一管理用户会话历史记录
 * 每条会话存储多条问答消息，限制单会话最大消息条数防止文档过大
 * @Date 2025/4/16
 * @DAY_NAME_FULL: 星期三
 * @Version 1.0
 */
// lombok日志注解，用于打印操作异常、关键流程日志
@Slf4j
// Spring持久层标识，交由Spring容器管理
@Repository
// lombok构造器注入，自动注入所有final修饰的成员变量
@RequiredArgsConstructor
public class MongoConversationStore {

    /**
     * 单会话最大存储消息条数常量
     * 限制Mongo文档数组长度，避免单条文档数据量过大导致查询、写入性能下降
     */
    private static final int DEFAULT_MAX_MESSAGES = 200;

    // MongoDB操作模板，提供文档新增、查询、更新、删除能力
    private final MongoTemplate mongoTemplate;

    /**
     * 创建全新空白AI对话会话
     * @param userId 当前操作用户ID
     * @return 初始化完成的会话实体，含唯一会话ID、空消息列表、创建时间
     */
    public AIConversation createConversation(Integer userId) {
        // 使用Builder构建会话对象，自动生成唯一会话ID
        AIConversation conv = AIConversation.builder()
                .conversationId(UUID.randomUUID().toString())
                .userId(userId)
                .createTime(Instant.now())
                .updateTime(Instant.now())
                .messages(new ArrayList<>()).build();
        // 插入MongoDB会话文档
        mongoTemplate.save(conv);
        return conv;
    }

    /**
     * 新增单条对话消息至指定会话，会话不存在则自动新建会话
     * 写入消息后自动裁剪历史消息，保证不超过最大存储条数
     * @param message 单条问答消息实体（用户提问/AI回复）
     * @param conversationId 目标会话唯一标识
     * @param userId 当前操作用户ID，用于新建会话时绑定归属用户
     */
    public void updateMessage(AIChatMessage message, String conversationId, Integer userId) {
        // 根据会话ID查询已有会话文档
        AIConversation conv = mongoTemplate.findById(conversationId, AIConversation.class);
        // 判断会话不存在，初始化全新会话
        if (ObjectUtil.isNull(conv)) {
            conv = AIConversation.builder()
                    .conversationId(conversationId)
                    .userId(userId)
                    .createTime(Instant.now())
                    .messages(new ArrayList<>()).build();
        }
        // 将新消息追加至会话消息集合
        conv.addMessage(message);
        // 校验消息数量，超出阈值则截断最早的历史消息
        if(conv.getMessages().size() > DEFAULT_MAX_MESSAGES) {
            // 计算保留起始下标，只保留最新N条消息
            int start = conv.getMessages().size() - DEFAULT_MAX_MESSAGES;
            conv.setMessages(conv.getMessages().subList(start, conv.getMessages().size()));
        }
        // 覆盖更新MongoDB会话文档
        mongoTemplate.save(conv);
    }

    /**
     * 根据会话ID加载该会话全部历史对话消息
     * @param conversationId 会话唯一ID
     * @param userId 用户ID（预留校验归属权限）
     * @return 会话内所有消息集合；会话不存在/消息为空返回空集合
     */
    public List<AIChatMessage> loadHistory(String conversationId, Integer userId) {
        AIConversation conv = mongoTemplate.findById(conversationId, AIConversation.class);
        // 会话为空或消息列表为空，返回不可变空列表
        if (conv == null || conv.getMessages() == null) {
            return List.of();
        }
        return conv.getMessages();
    }

    /**
     * 查询指定用户名下全部AI对话会话
     * @param userId 用户唯一ID
     * @return 当前用户所有会话集合，按创建时间升序排列
     */
    public List<AIConversation> loadHistoryByUser(Integer userId) {
        // 构建查询条件：匹配userId字段
        Criteria criteria = Criteria.where("userId").is(userId);
        Query query = new Query(criteria)
                // 指定排序规则：按会话创建时间升序
                .with(Sort.by(Sort.Direction.ASC, "createTime"));
        // 查询匹配的全部会话文档
        return mongoTemplate.find(query, AIConversation.class);
    }

    /**
     * 删除指定会话，同时清空该会话下所有历史消息
     * 增加用户ID联合校验，防止越权删除他人会话
     * @param conversationId 待删除会话ID
     * @param userId 当前操作用户ID
     */
    public void deleteHistory(String conversationId, Integer userId) {
        // 多条件联合匹配：会话ID + 归属用户ID双重校验
        Criteria criteria = Criteria.where("_id").is(conversationId).and("userId").is(userId);
        Query query = new Query(criteria);
        // 删除匹配的会话整条文档
        mongoTemplate.remove(query, AIConversation.class);
    }
}