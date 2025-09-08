package dev.dong4j.zeka.starter.launcher.autoconfigure;

import dev.dong4j.zeka.kernel.autoconfigure.ZekaProperties;
import dev.dong4j.zeka.kernel.common.constant.ConfigKey;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * 启动器配置属性类，定义与启动相关的配置项
 *
 * 该类继承自 ZekaProperties，提供了以下配置项：
 * 1. 自定义配置映射 (custom)
 * 2. 应用分组配置 (group, configGroup, discoveryGroup)
 * 3. 启动相关开关 (enableBanner, refresh)
 *
 * 配置前缀为 "zeka-stack.app"，支持配置热更新 (@RefreshScope)。
 * 该类是启动器模块的核心配置类，所有启动相关的配置都应在此定义。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 14:54
 * @since 1.0.0
 */
@Getter
@Setter
@RefreshScope
@ConfigurationProperties(prefix = LauncherProperties.PREFIX)
public class LauncherProperties extends ZekaProperties {

    /**
     * 配置前缀常量，用于绑定所有启动相关的配置项
     * <p>
     * 实际配置前缀为 "zeka-stack.app"，所有相关配置都应以此前缀开头。
     */
    public static final String PREFIX = ConfigKey.PREFIX + "app";
    /**
     * 自定义配置映射，用于装载任意自定义配置项
     *
     * 通过 "zeka-stack.app.custom.xxx" 格式的配置项可以动态扩展配置，
     * 这些配置可以在运行时通过 @Value 注解或 Environment 接口获取。
     */
    private Map<String, Object> custom;
    /**
     * 应用配置映射 (已废弃)
     *
     * @deprecated 使用 custom 属性替代，以保持配置层级一致性
     */
    @Deprecated
    private Map<String, Object> app;
    /**
     * 是否启用启动 banner 显示
     *
     * 默认值为 false，表示显示 banner。
     * 设置为 true 可以禁用启动时的 banner 显示。
     */
    private boolean enableBanner = Boolean.FALSE;
    /**
     * 应用分组名称
     *
     * 用于在分布式环境中标识应用所属的逻辑分组，
     * 通常用于配置中心、服务发现等场景。
     */
    private String group;
    /**
     * 配置中心分组名称
     *
     * 用于从配置中心获取特定分组的配置，
     * 如果未指定，则使用默认分组。
     */
    private String configGroup;
    /**
     * 服务发现分组名称
     *
     * 用于在服务注册中心中标识服务所属的逻辑分组，
     * 如果未指定，则使用默认分组。
     */
    private String discoveryGroup;
    /**
     * 是否启用配置动态刷新功能
     *
     * 默认值为 true，表示启用配置刷新。
     * 在 Spring Cloud 环境中会自动禁用此功能，
     * 因为 Spring Cloud 提供了自己的配置刷新机制。
     */
    private boolean refresh;

    /**
     * 设置应用配置映射 (已废弃)
     *
     * @param app 应用配置映射
     * @deprecated 使用 custom 属性替代，以保持配置层级一致性
     * @since 1.0.0
     */
    @DeprecatedConfigurationProperty(replacement = ConfigKey.PREFIX + "app.custom", reason = "添加 app 配置层级, 与其他项目区分")
    public void setApp(Map<String, Object> app) {
        this.app = app;
    }

    /**
     * 配置类型枚举，定义不同的配置来源类型
     *
     * 1. config - 表示配置中心来源
     * 2. discovery - 表示服务发现来源
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.04.19 16:14
     * @since 1.0.0
     */
    public enum Type {
        /** Config type */
        config,
        /** Discovery type */
        discovery
    }
}
