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

@Getter
@Setter
@ConfigurationProperties(prefix = MessagingTypeDetector.DEFAULT_CONFIG_PREFIX)
public class MessagingProperties extends ZekaProperties {

    public static final String PREFIX = MessagingTypeDetector.DEFAULT_CONFIG_PREFIX;

    /**
     * 是否启用自动检测功能
     * 默认值: true
     */
    private boolean autoDetect = true;

    /**
     * 禁用的MQ类型列表
     * 格式: 逗号分隔的MQ类型名称 (KAFKA, ROCKETMQ, TONGHTP)
     */
    private Set<MessagingType> disabledTypes = new HashSet<>();

    /**
     * 默认的MQ类型
     * 当自动检测到多个MQ类型时使用
     */
    private MessagingType defaultType;

    /**
     * 类检测超时时间(毫秒)
     * 默认值: 500
     */
    private long classDetectTimeout = 500;

    /**
     * 是否启用类检测缓存
     * 默认值: true
     */
    private boolean enableCache = true;

    /**
     * 缓存过期时间(秒)
     * 默认值: 300 (5分钟)
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
