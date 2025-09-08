package dev.dong4j.zeka.starter.messaging.handler;

import dev.dong4j.zeka.starter.messaging.context.MessagingContext;
import dev.dong4j.zeka.starter.messaging.model.UnifiedMessage;

/**
 * 消息处理器接口
 * <p>
 * 该接口定义了消息处理的核心方法，用于：
 * 1. 处理接收到的消息
 * 2. 访问消息上下文
 * <p>
 * 使用场景：
 * 1. 消息监听适配器中调用
 * 2. 业务逻辑处理消息
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.06.27
 * @since 1.0.0
 */
@FunctionalInterface
public interface MessagingMessageHandler {
    /**
     * 处理消息
     *
     * @param message 统一消息对象
     * @param context 消息上下文
     */
    void handleMessage(UnifiedMessage message, MessagingContext context);
}
