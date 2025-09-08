package dev.dong4j.zeka.starter.messaging.context;

import dev.dong4j.zeka.starter.messaging.enums.MessagingType;
import dev.dong4j.zeka.starter.messaging.model.UnifiedMessage;
import lombok.Getter;
import lombok.Setter;

/**
 * 消息上下文类
 * <p>
 * 该类封装了消息处理过程中的上下文信息，包括：
 * 1. 消息中间件类型
 * 2. 消息主题
 * 3. 消费者组ID
 * 4. 消息对象
 * <p>
 * 使用场景：
 * 1. 消息监听器处理消息时获取上下文
 * 2. 消息处理器中访问消息相关信息
 * 3. 异常处理时获取消息上下文
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.06.27
 * @since 1.0.0
 */
@Setter
@Getter
public class MessagingContext {
    /**
     * 消息中间件类型
     */
    private MessagingType messagingType;

    /**
     * 消息主题
     */
    private String topic;

    /**
     * 消费者组ID
     */
    private String groupId;

    /**
     * 统一消息对象
     */
    private UnifiedMessage message;

    /**
     * 默认构造方法
     */
    public MessagingContext() {
    }

    /**
     * 带消息类型的构造方法
     *
     * @param messagingType 消息中间件类型
     */
    public MessagingContext(MessagingType messagingType) {
        this.messagingType = messagingType;
    }
}
