package dev.dong4j.zeka.starter.messaging.util;

import dev.dong4j.zeka.starter.messaging.model.UnifiedMessage;

/**
 * Topic 和 Tag 工具类
 * <p>
 * 该类提供了 Topic 和 Tag 相关的工具方法，包括：
 * 1. 组合 Topic 和 Tag
 * 2. 从字符串中提取 Topic 和 Tag
 * 3. 从消息对象中提取 Topic 和 Tag
 * <p>
 * 核心功能：
 * 1. 支持 RocketMQ 的 Tag 处理
 * 2. 统一的分隔符处理
 * <p>
 * 使用场景：
 * 1. 消息发送时组合 Topic 和 Tag
 * 2. 消息接收时提取 Topic 和 Tag
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.06.27
 * @since 1.0.0
 */
public class TopicAndTagUtils {
    /**
     * Topic 和 Tag 的分隔符
     */
    private static final String TOPIC_TAG_DELIMITER = ":";

    /**
     * 组合 Topic 和 Tag
     *
     * @param topic 主题
     * @param tag 标签
     * @return 组合后的字符串
     */
    public static String withTopicAndTag(String topic, String tag) {
        if (tag == null || tag.isEmpty()) {
            return topic;
        }
        return topic + TOPIC_TAG_DELIMITER + tag;
    }

    /**
     * 从目标字符串中提取 Topic
     *
     * @param destination 目标字符串
     * @return 提取的 Topic
     */
    public static String extractTopic(String destination) {
        int index = destination.indexOf(TOPIC_TAG_DELIMITER);
        if (index == -1) {
            return destination;
        }
        return destination.substring(0, index);
    }

    /**
     * 从目标字符串中提取 Tag
     *
     * @param destination 目标字符串
     * @return 提取的 Tag
     */
    public static String extractTag(String destination) {
        int index = destination.indexOf(TOPIC_TAG_DELIMITER);
        if (index == -1) {
            return "";
        }
        return destination.substring(index + 1);
    }

    /**
     * 从消息对象中提取 Tag
     *
     * @param message 统一消息对象
     * @return 提取的 Tag
     */
    public static String extractTag(UnifiedMessage message) {
        Object tagObj = message.getHeaders().get("rocketmq_tag");
        if (tagObj != null) {
            return tagObj.toString();
        }
        return extractTag(message.getDestination());
    }

    /**
     * 从消息对象中提取 Topic
     *
     * @param message 统一消息对象
     * @return 提取的 Topic
     */
    public static String extractTopic(UnifiedMessage message) {
        return extractTopic(message.getDestination());
    }
}
