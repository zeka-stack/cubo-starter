package dev.dong4j.zeka.starter.messaging.model;

import dev.dong4j.zeka.starter.messaging.template.model.MessageKey;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

/**
 * 统一消息模型类
 * <p>
 * 该类封装了消息中间件的通用消息结构，包括：
 * 1. 消息目的地 (destination)
 * 2. 消息负载 (payload)
 * 3. 消息键 (messageKey)
 * 4. 消息头 (headers)
 * <p>
 * 使用场景：
 * 1. Kafka 消息传递
 * 2. RocketMQ 消息传递
 * 3. 其他消息中间件的消息封装
 * <p>
 * 示例：
 * {@code
 * UnifiedMessage message = new UnifiedMessage("test-topic", "message content")
 * .addHeader("traceId", "123456")
 * .withKey(new MessageKey("key1"));
 * }
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2023.05.15
 * @since 1.0.0
 */
@Getter
public class UnifiedMessage {
    /**
     * 消息目的地，通常是 topic 或 queue 名称
     */
    @Setter
    private String destination;

    /**
     * 消息负载，存储实际的消息内容
     */
    private final Object payload;

    /**
     * 消息键，用于消息分区或去重
     */
    private MessageKey messageKey;

    /**
     * 消息头，存储额外的元数据信息
     */
    private final Map<String, Object> headers = new HashMap<>();

    /**
     * 构造方法
     *
     * @param destination 消息目的地
     * @param payload 消息负载
     */
    public UnifiedMessage(String destination, Object payload) {
        this.destination = destination;
        this.payload = payload;
    }

    /**
     * 添加消息头
     *
     * @param key 消息头键
     * @param value 消息头值
     * @return 当前消息对象，支持链式调用
     */
    public UnifiedMessage addHeader(String key, Object value) {
        this.headers.put(key, value);
        return this;
    }

    /**
     * 设置消息键
     *
     * @param key 消息键对象
     * @return 当前消息对象，支持链式调用
     */
    public UnifiedMessage withKey(MessageKey key) {
        this.messageKey = key;
        return this;
    }
}
