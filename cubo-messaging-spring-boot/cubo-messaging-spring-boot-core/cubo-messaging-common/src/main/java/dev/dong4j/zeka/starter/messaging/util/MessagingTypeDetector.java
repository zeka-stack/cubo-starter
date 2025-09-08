package dev.dong4j.zeka.starter.messaging.util;

import dev.dong4j.zeka.kernel.common.constant.ConfigKey;
import dev.dong4j.zeka.starter.messaging.annotation.MessagingListener;
import dev.dong4j.zeka.starter.messaging.enums.MessagingType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;

/**
 * 消息类型检测器
 * <p>
 * 该类负责检测项目中使用的消息中间件类型，包括：
 * 1. 自动检测类路径中的消息中间件实现
 * 2. 处理用户配置的禁用类型
 * 3. 确定默认消息类型
 * <p>
 * 核心功能：
 * 1. 延迟初始化检测
 * 2. 自定义检测类支持
 * 3. 类型可用性验证
 * <p>
 * 使用场景：
 * 1. 消息监听器注册时验证类型
 * 2. 消息模板初始化时确定类型
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.06.27
 * @since 1.0.0
 */
@Slf4j
public class MessagingTypeDetector {
    /**
     * 类存在性缓存
     */
    private static final Map<String, Boolean> classPresenceCache = new ConcurrentHashMap<>();

    /**
     * 消息类型与检测类的映射
     */
    private static final Map<MessagingType, String> DETECTION_CLASS_MAP = new HashMap<>();

    static {
        DETECTION_CLASS_MAP.put(MessagingType.KAFKA, "org.apache.kafka.clients.consumer.KafkaConsumer");
        DETECTION_CLASS_MAP.put(MessagingType.ROCKETMQ, "org.apache.rocketmq.client.consumer.DefaultMQPushConsumer");
    }

    /**
     * 默认配置属性前缀
     */
    public static final String DEFAULT_CONFIG_PREFIX = ConfigKey.PREFIX + "messaging";

    private final Environment environment;
    private final List<MessagingType> availableTypes = new ArrayList<>();
    private final Map<MessagingType, String> customDetectionClasses = new HashMap<>();
    private MessagingType defaultType = null;
    private final Set<MessagingType> userDisabledTypes = new HashSet<>();
    private boolean detectionCompleted = false;

    /**
     * 构造方法
     *
     * @param environment Spring环境对象
     */
    public MessagingTypeDetector(Environment environment) {
        this.environment = environment;
        this.userDisabledTypes.addAll(parseDisabledTypes());
    }

    /**
     * 执行消息类型检测（延迟初始化）
     */
    public synchronized void detectAvailableTypes() {
        if (detectionCompleted) return;

        // 加载自定义检测类配置
        loadCustomDetectionClasses();

        // 检测所有支持的MQ类型
        for (Map.Entry<MessagingType, String> entry : DETECTION_CLASS_MAP.entrySet()) {
            MessagingType type = entry.getKey();
            String className = customDetectionClasses.getOrDefault(type, entry.getValue());

            // 如果用户禁用了该类型，跳过检测
            if (userDisabledTypes.contains(type)) {
                log.debug("Skipping detection for disabled MQ type: {}", type);
                continue;
            }

            if (isClassPresent(className)) {
                availableTypes.add(type);
                log.info("Detected MQ implementation: {}", type);
            }
        }

        // 处理默认类型
        handleDefaultType();
        detectionCompleted = true;
    }

    private void handleDefaultType() {
        if (availableTypes.isEmpty()) {
            log.warn("No MQ implementations detected in classpath.");
        } else if (availableTypes.size() == 1) {
            defaultType = availableTypes.get(0);
            log.info("Auto-selected default MQ type: {}", defaultType);
        } else {
            // 尝试从配置中获取默认类型
            String defaultTypeConfig = environment.getProperty(DEFAULT_CONFIG_PREFIX + ".default-type");
            if (defaultTypeConfig != null) {
                try {
                    defaultType = MessagingType.valueOf(defaultTypeConfig.toUpperCase());
                    if (!availableTypes.contains(defaultType)) {
                        log.warn("Configured default MQ type {} not available. Available types: {}",
                            defaultType, availableTypes);
                        defaultType = null;
                    } else {
                        log.info("Using configured default MQ type: {}", defaultType);
                    }
                } catch (IllegalArgumentException e) {
                    log.error("Invalid default MQ type configured: {}", defaultTypeConfig);
                }
            }

            if (defaultType == null) {
                log.info("Multiple MQ implementations detected. Users must explicitly specify type.");
            }
        }
    }

    /**
     * 加载自定义检测类配置
     */
    private void loadCustomDetectionClasses() {
        for (MessagingType type : MessagingType.values()) {
            String classKey = DEFAULT_CONFIG_PREFIX + "." + type.name().toLowerCase() + ".detection-class";
            String customClass = environment.getProperty(classKey);
            if (customClass != null && !customClass.isEmpty()) {
                customDetectionClasses.put(type, customClass);
                log.debug("Using custom detection class for {}: {}", type, customClass);
            }
        }
    }

    /**
     * 解析用户禁用的消息类型
     *
     * @return 禁用的消息类型集合
     */
    private Set<MessagingType> parseDisabledTypes() {
        Set<MessagingType> disabled = new HashSet<>();
        String disabledTypes = environment.getProperty(DEFAULT_CONFIG_PREFIX + ".disabled-types");
        if (disabledTypes != null) {
            for (String typeStr : disabledTypes.split(",")) {
                try {
                    MessagingType type = MessagingType.valueOf(typeStr.trim().toUpperCase());
                    disabled.add(type);
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid MQ type in disabled-types: {}", typeStr);
                }
            }
        }
        return disabled;
    }

    private boolean isClassPresent(String className) {
        return classPresenceCache.computeIfAbsent(className, key -> {
            try {
                Class.forName(key);
                return true;
            } catch (ClassNotFoundException e) {
                return false;
            }
        });
    }

    /**
     * 验证消息监听器注解配置
     *
     * @param annotation MessagingListener注解实例
     * @throws IllegalStateException 如果类型配置无效
     */
    public void validate(MessagingListener annotation) {
        if (!detectionCompleted) detectAvailableTypes();

        MessagingType configuredType = annotation.type();
        if (configuredType == MessagingType.DEFAULT) {
            if (defaultType == null) {
                throw new IllegalStateException(
                    "Multiple MQ implementations detected. Please explicitly specify 'type' in " +
                        "@UnifiedMessageListener. Available types: " + availableTypes
                );
            }
        } else if (!availableTypes.contains(configuredType)) {
            throw new IllegalStateException(
                "Specified MQ type " + configuredType + " not available. Available types: " + availableTypes
            );
        }
    }


    /**
     * 解析实际使用的消息类型
     *
     * @param configuredType 配置的类型
     * @return 实际使用的消息类型
     * @throws IllegalStateException 如果类型解析失败
     */
    public MessagingType resolveType(MessagingType configuredType) {
        if (!detectionCompleted) {
            detectAvailableTypes();
        }

        if (configuredType == MessagingType.DEFAULT) {
            if (defaultType == null) {
                throw new IllegalStateException(
                    "No default MQ type available. Please configure one explicitly."
                );
            }
            return defaultType;
        }
        return configuredType;
    }

    /**
     * 获取默认的消息类型
     *
     * @return 默认消息类型
     */
    public MessagingType getDefaultType() {
        if (!detectionCompleted) {
            detectAvailableTypes();
        }
        return defaultType;
    }

    /**
     * 检查指定消息类型是否可用
     *
     * @param type 消息类型
     * @return 是否可用
     */
    public boolean isTypeAvailable(MessagingType type) {
        if (!detectionCompleted) {
            detectAvailableTypes();
        }
        return availableTypes.contains(type);
    }

    /**
     * 检查是否只有一个可用的消息类型
     *
     * @return 是否只有一个可用类型
     */
    public boolean hasSingleType() {
        if (!detectionCompleted) {
            detectAvailableTypes();
        }
        return availableTypes.size() == 1;
    }

    /**
     * 获取所有可用的消息类型
     *
     * @return 可用消息类型列表
     */
    public List<MessagingType> getAvailableTypes() {
        if (!detectionCompleted) {
            detectAvailableTypes();
        }
        return Collections.unmodifiableList(availableTypes);
    }

    /**
     * 检查是否启用了自动检测
     *
     * @return 是否启用自动检测
     */
    public boolean isAutoDetectEnabled() {
        return environment.getProperty(
            DEFAULT_CONFIG_PREFIX + ".auto-detect", Boolean.class, true
        );
    }

    /**
     * 根据条件筛选检测结果
     *
     * @param filter 筛选条件
     */
    public void filterDetectedTypes(Predicate<MessagingType> filter) {
        if (!detectionCompleted) {
            detectAvailableTypes();
        }
        availableTypes.removeIf(type -> !filter.test(type));
        handleDefaultType(); // 重新计算默认类型
    }
}
