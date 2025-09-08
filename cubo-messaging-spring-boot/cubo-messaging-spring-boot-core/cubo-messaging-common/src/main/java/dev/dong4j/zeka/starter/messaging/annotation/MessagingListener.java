package dev.dong4j.zeka.starter.messaging.annotation;

import dev.dong4j.zeka.starter.messaging.enums.MessagingType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 消息监听注解，用于标记消息处理方法
 * <p>
 * 该注解用于定义消息监听的相关配置，包括：
 * 1. 监听的 topic
 * 2. 消费者组 ID
 * 3. 消息中间件类型
 * <p>
 * 使用场景：
 * 1. Kafka 消息监听
 * 2. RocketMQ 消息监听
 * 3. 其他消息中间件的监听
 * <p>
 * 示例：
 * {@code
 *
 * @author dong4j
 * @version 1.0.0
 * @MessagingListener(topic = "test-topic", groupId = "test-group")
 * public void handleMessage(UnifiedMessage message) {
 * // 处理消息
 * }
 * }
 * @email "mailto:dong4j@gmail.com"
 * @date 2023.05.15
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MessagingListener {
    /**
     * 监听的 topic
     *
     * @return topic 名称
     */
    String topic();

    /**
     * 消费者组 ID
     *
     * @return 消费者组 ID
     */
    String groupId();

    /**
     * 消息中间件类型
     *
     * @return 消息中间件类型枚举
     */
    MessagingType type() default MessagingType.DEFAULT;
}
