package dev.dong4j.zeka.starter.messaging.template.model;

/**
 * 消息键类
 * <p>
 * 该类封装了消息的键，用于：
 * 1. 消息分区
 * 2. 消息去重
 * 3. 消息追踪
 * <p>
 * 使用场景：
 * 1. Kafka 消息键
 * 2. RocketMQ 消息键
 * 3. 其他消息中间件的消息键封装
 * <p>
 * 示例：
 * {@code
 * MessageKey key = MessageKey.of("order-123");
 * }
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2023.05.15
 * @since 1.0.0
 */
public class MessageKey {
    /**
     * 消息键值
     */
    private final Object key;

    /**
     * 私有构造方法
     *
     * @param key 消息键值
     */
    private MessageKey(Object key) {
        this.key = key;
    }

    /**
     * 静态工厂方法，创建 MessageKey 实例
     *
     * @param key 消息键值
     * @return MessageKey 实例
     */
    public static MessageKey of(Object key) {
        return new MessageKey(key);
    }

    /**
     * 获取消息键值
     *
     * @return 消息键值
     */
    public Object getKey() {
        return key;
    }
}
