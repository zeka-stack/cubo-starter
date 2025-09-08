package dev.dong4j.zeka.starter.messaging.template;

import dev.dong4j.zeka.starter.messaging.enums.MessagingType;
import dev.dong4j.zeka.starter.messaging.model.UnifiedMessage;
import dev.dong4j.zeka.starter.messaging.template.model.SendResult;
import java.util.concurrent.CompletableFuture;

/**
 * 消息模板接口
 * <p>
 * 该接口定义了消息发送的统一操作，包括：
 * 1. 同步发送
 * 2. 异步发送
 * 3. 单向发送
 * 4. 获取原生模板
 * 5. 按类型获取模板
 * <p>
 * 使用场景：
 * 1. Kafka 消息发送
 * 2. RocketMQ 消息发送
 * 3. 其他消息中间件的统一操作
 * <p>
 * 示例：
 * {@code
 * messagingTemplate.sendSync(new UnifiedMessage("test-topic", "message content"));
 * }
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2023.05.15
 * @since 1.0.0
 */
public interface MessagingTemplate {
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
     * @param type 消息中间件类型
     * @param templateClass 模板类
     * @return 原生模板实例
     */
    <T> T getNativeTemplate(MessagingType type, Class<T> templateClass);

    /**
     * 按消息中间件类型获取模板
     *
     * @param type 消息中间件类型
     * @return 消息模板实例
     */
    MessagingTemplate forType(MessagingType type);
}
