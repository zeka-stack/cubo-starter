package dev.dong4j.zeka.starter.messaging.enums;

/**
 * 消息中间件类型枚举
 * <p>
 * 定义系统支持的消息中间件类型，包括：
 * 1. Kafka
 * 2. RocketMQ
 * 3. 其他自定义消息中间件
 * <p>
 * 使用场景：
 * 1. 消息监听器配置
 * 2. 消息发送器配置
 * 3. 消息适配器选择
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2023.05.15
 * @since 1.0.0
 */
public enum MessagingType {
    /**
     * Kafka 消息中间件
     */
    KAFKA,

    /**
     * RocketMQ 消息中间件
     */
    ROCKETMQ,

    /**
     * 通HTTP 消息中间件
     */
    TONGHTP,

    /**
     * 默认消息中间件类型
     */
    DEFAULT
}
