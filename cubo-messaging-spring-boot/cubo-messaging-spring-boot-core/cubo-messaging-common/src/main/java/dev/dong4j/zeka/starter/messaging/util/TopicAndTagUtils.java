package dev.dong4j.zeka.starter.messaging.util;

import dev.dong4j.zeka.starter.messaging.model.UnifiedMessage;

public class TopicAndTagUtils {
    private static final String TOPIC_TAG_DELIMITER = ":";

    public static String withTopicAndTag(String topic, String tag) {
        if (tag == null || tag.isEmpty()) {
            return topic;
        }
        return topic + TOPIC_TAG_DELIMITER + tag;
    }

    public static String extractTopic(String destination) {
        int index = destination.indexOf(TOPIC_TAG_DELIMITER);
        if (index == -1) {
            return destination;
        }
        return destination.substring(0, index);
    }

    public static String extractTag(String destination) {
        int index = destination.indexOf(TOPIC_TAG_DELIMITER);
        if (index == -1) {
            return "";
        }
        return destination.substring(index + 1);
    }

    public static String extractTag(UnifiedMessage message) {
        Object tagObj = message.getHeaders().get("rocketmq_tag");
        if (tagObj != null) {
            return tagObj.toString();
        }
        return extractTag(message.getDestination());
    }

    public static String extractTopic(UnifiedMessage message) {
        return extractTopic(message.getDestination());
    }
}
