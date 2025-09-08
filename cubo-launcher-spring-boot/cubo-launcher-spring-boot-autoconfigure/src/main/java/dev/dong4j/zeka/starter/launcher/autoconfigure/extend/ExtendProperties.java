package dev.dong4j.zeka.starter.launcher.autoconfigure.extend;

import dev.dong4j.zeka.kernel.common.constant.ConfigKey;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 扩展功能配置属性类，定义与注解扩展相关的配置项
 *
 * 该类提供了以下配置项：
 * 1. 控制 @Resource 注解是否允许注入为 null
 * 2. 控制 @Autowired 注解是否允许注入为 null
 *
 * 配置前缀为 "zeka-stack.extend"，可以通过配置文件动态调整。
 * 这些配置会影响 ExtendAutoConfiguration 的行为。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.01.28 01:27
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = ExtendProperties.PREFIX)
public class ExtendProperties {

    /**
     * 配置前缀常量，用于绑定所有扩展相关的配置项
     * <p>
     * 实际配置前缀为 "zeka-stack.extend"，所有相关配置都应以此前缀开头。
     */
    public static final String PREFIX = ConfigKey.PREFIX + "extend";
    /**
     * 是否允许 @Resource 注解注入的依赖为 null
     *
     * 默认值为 false，表示不允许注入为 null。
     * 设置为 true 可以允许 @Resource 注解的依赖注入为 null。
     */
    private boolean enableResourceIsNull = Boolean.FALSE;
    /**
     * 是否允许 @Autowired 注解注入的依赖为 null
     *
     * 默认值为 false，表示不允许注入为 null。
     * 设置为 true 可以允许 @Autowired 注解的依赖注入为 null。
     */
    private boolean enableAutowiredIsNull = Boolean.FALSE;
}
