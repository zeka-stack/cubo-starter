package dev.dong4j.zeka.starter.dict.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import dev.dong4j.zeka.kernel.autoconfigure.ZekaProperties;
import dev.dong4j.zeka.kernel.common.constant.ConfigKey;
import dev.dong4j.zeka.starter.dict.enums.DictionaryCacheType;
import lombok.Getter;
import lombok.Setter;

/**
 * 字典配置属性类
 * <p> 用于配置字典相关的功能参数, 包括缓存启用状态, 预加载设置, 缓存类型, 缓存过期时间及刷新延迟等
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.12.22
 * @since 1.0.0
 */
@Getter
@Setter
@ConfigurationProperties(prefix = DictProperties.PREFIX)
public class DictProperties extends ZekaProperties {
    /** 组件配置前缀 */
    public static final String PREFIX = ConfigKey.PREFIX + "dict";

    /** 是否启用缓存 */
    private boolean enableCache = true;

    /** 是否在启动时预热缓存 */
    private boolean preloadCache = true;

    /** 缓存类型, 默认为内存缓存 */
    private DictionaryCacheType cacheType = DictionaryCacheType.MEMORY;

    /** 缓存过期时间 (秒) */
    private long cacheExpireTime = 3600;

    /** 缓存刷新延迟时间 (毫秒) */
    private long cacheRefreshDelay = 100;
}
