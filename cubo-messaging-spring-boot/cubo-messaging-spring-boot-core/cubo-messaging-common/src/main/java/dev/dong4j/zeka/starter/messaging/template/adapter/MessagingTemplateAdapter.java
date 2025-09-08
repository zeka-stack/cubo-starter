package dev.dong4j.zeka.starter.messaging.template.adapter;

import dev.dong4j.zeka.starter.messaging.model.UnifiedMessage;
import dev.dong4j.zeka.starter.messaging.template.model.SendResult;
import java.util.concurrent.CompletableFuture;

/**
 * 消息模板适配器接口
 * <p>
 * 该接口定义了消息发送的统一操作，用于：
 * 1. 同步发送消息
 * 2. 异步发送消息
 * 3. 单向发送消息
 * 4. 获取原生模板
 * <p>
 * 实现类需要针对不同的消息中间件提供具体实现
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.06.27
 * @since 1.0.0
 */
public interface MessagingTemplateAdapter {
    /**
     * 同步发送消息
     *
     * @param message 统一消息对象
     * @return 发送结果
     */
    SendResult sendSync(UnifiedMessage message);

    /**
     * 异步发送消息
     *
     * @param message 统一消息对象
     * @return CompletableFuture 包装的发送结果
     */
    CompletableFuture<SendResult> sendAsync(UnifiedMessage message);

    /**
     * 单向发送消息（不关心发送结果）
     *
     * @param message 统一消息对象
     */
    void sendOneWay(UnifiedMessage message);

    /**
     * 获取原生消息模板
     *
     * @param <T> 模板类型
     * @return 原生模板实例
     */
    <T> T getNativeTemplate();
}
