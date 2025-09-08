package dev.dong4j.zeka.starter.messaging.autoconfigure;

import dev.dong4j.zeka.kernel.autoconfigure.ZekaProperties;
import dev.dong4j.zeka.starter.messaging.enums.MessagingType;
import dev.dong4j.zeka.starter.messaging.util.MessagingTypeDetector;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 消息中间件配置属性类
 * <p>
 * 该类定义了消息中间件的全局配置属性，包括：
 * 1. 自动检测配置
 * 2. 禁用类型配置
 * 3. 默认消息类型
 * 4. 检测超时设置
 * 5. 缓存配置
 * 6. 各消息中间件(Kafka/RocketMQ/TongHTP)的特定配置
 * <p>
 * 配置前缀：zeka.messaging
 * <p>
 * 使用示例：
 * {@code
 * zeka.messaging:
 * auto-detect: true
 * disabled-types: ROCKETMQ
 * default-type: KAFKA
 * kafka:
 * enabled: true
 * detection-class: com.example.CustomKafkaDetector
 * }
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.06.27
 * @since 1.0.0
 */
@Getter
@Setter
@ConfigurationProperties(prefix = MessagingTypeDetector.DEFAULT_CONFIG_PREFIX)
public class MessagingProperties extends ZekaProperties {

    public static final String PREFIX = MessagingTypeDetector.DEFAULT_CONFIG_PREFIX;

    /**
     * 是否启用自动检测功能
     * 默认值: true
     * 当设置为 true 时，系统会自动检测项目中使用的消息中间件类型
     */
    private boolean autoDetect = true;

    /**
     * 禁用的消息中间件类型集合
     * 格式: Set 集合，包含 MessagingType 枚举值
     * 示例: [KAFKA, ROCKETMQ]
     */
    private Set<MessagingType> disabledTypes = new HashSet<>();

    /**
     * 默认的消息中间件类型
     * 当自动检测到多个消息中间件类型时使用
     * 如果未设置，则使用检测到的第一个类型
     */
    private MessagingType defaultType;

    /**
     * 类检测超时时间(毫秒)
     * 默认值: 500
     * 设置检测消息中间件相关类时的超时时间
     */
    private long classDetectTimeout = 500;

    /**
     * 是否启用类检测缓存
     * 默认值: true
     * 启用缓存可提高性能，但可能在类路径变更后需要重启应用
     */
    private boolean enableCache = true;

    /**
     * 缓存过期时间(秒)
     * 默认值: 300 (5分钟)
     * 设置检测结果的缓存时间，过期后重新检测
     */
    private long cacheExpiration = TimeUnit.MINUTES.toSeconds(5);

    /**
     * Kafka 特定配置
     */
    private KafkaConfig kafka = new KafkaConfig();

    /**
     * RocketMQ 特定配置
     */
    private RocketMQConfig rocketmq = new RocketMQConfig();

    /**
     * TongHTP 特定配置
     */
    private TonghtpConfig tonghtp = new TonghtpConfig();


    @Data
    // 嵌套配置类
    public static class KafkaConfig {
        /**
         * 是否启用Kafka支持
         * 默认值: true
         */
        private boolean enabled = true;

        /**
         * 自定义检测类
         * 用于覆盖默认的Kafka检测逻辑
         */
        private String detectionClass;

    }

    @Data
    public static class RocketMQConfig {
        /**
         * 是否启用RocketMQ支持
         * 默认值: true
         */
        private boolean enabled = true;

        /**
         * 自定义检测类
         * 用于覆盖默认的RocketMQ检测逻辑
         */
        private String detectionClass;

    }

    @Data
    public static class TonghtpConfig {
        /**
         * 是否启用TongHTP支持
         * 默认值: true
         */
        private boolean enabled = true;

        /**
         * 自定义检测类
         * 用于覆盖默认的TongHTP检测逻辑
         */
        private String detectionClass;

    }

    /**
     * 获取指定MQ类型的自定义检测类
     */
    public String getDetectionClass(MessagingType type) {
        switch (type) {
            case KAFKA:
                return kafka.getDetectionClass();
            case ROCKETMQ:
                return rocketmq.getDetectionClass();
            case TONGHTP:
                return tonghtp.getDetectionClass();
            default:
                return null;
        }
    }

    /**
     * 检查指定MQ类型是否启用
     */
    public boolean isEnabled(MessagingType type) {
        switch (type) {
            case KAFKA:
                return kafka.isEnabled();
            case ROCKETMQ:
                return rocketmq.isEnabled();
            case TONGHTP:
                return tonghtp.isEnabled();
            default:
                return true;
        }
    }
}
