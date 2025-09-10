package dev.dong4j.zeka.starter.messaging.template.model;

import org.jetbrains.annotations.NotNull;

/**
 * 消息发送结果类
 * <p>
 * 该类封装了消息发送的结果信息，包括：
 * 1. 消息主题
 * 2. 分区信息
 * 3. 偏移量
 * 4. 消息ID
 * <p>
 * 使用场景：
 * 1. Kafka 消息发送结果
 * 2. RocketMQ 消息发送结果
 * 3. 其他消息中间件的发送结果封装
 * <p>
 * 示例：
 * {@code
 * SendResult result = new SendResult("test-topic", 0, 12345L, "msg-123456");
 * }
 *
 * @param topic     消息主题
 * @param partition 分区编号
 * @param offset    消息偏移量
 * @param messageId 消息ID
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2023.05.15
 * @since 1.0.0
 */
public record SendResult(String topic, int partition, long offset, String messageId) {
    /**
     * 构造方法
     *
     * @param topic     消息主题
     * @param partition 分区编号
     * @param offset    消息偏移量
     * @param messageId 消息ID
     */
    public SendResult {
    }

    /**
     * 重写 toString 方法
     *
     * @return 发送结果的字符串表示
     */
    @Override
    public @NotNull String toString() {
        return "SendResult{" +
            "topic='" + topic + '\'' +
            ", partition=" + partition +
            ", offset=" + offset +
            ", messageId='" + messageId + '\'' +
            '}';
    }
}
