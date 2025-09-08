package dev.dong4j.zeka.starter.logsystem;

import dev.dong4j.zeka.kernel.common.util.StringUtils;
import java.util.Arrays;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertyResolver;
import org.springframework.core.env.PropertySourcesPropertyResolver;
import org.springframework.util.Assert;

/**
 * 抽象属性处理器基类
 *
 * 该类是日志系统属性处理器的抽象基类，负责将配置文件中的配置（Environment）
 * 写入到JVM系统属性中，供日志系统读取使用。
 *
 * 主要功能包括：
 * 1. 提供统一的属性处理框架
 * 2. 支持配置属性的获取和转换
 * 3. 管理JVM系统属性的设置
 * 4. 支持废弃属性的兼容处理
 * 5. 提供属性解析器的统一管理
 *
 * 使用场景：
 * - 日志系统配置的预处理
 * - 配置属性到系统属性的转换
 * - 多环境配置的统一处理
 * - 废弃属性的兼容性处理
 *
 * 设计意图：
 * 通过抽象基类提供统一的属性处理框架，简化具体处理器的实现，
 * 确保配置属性能够正确传递给日志系统。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.05.20 11:41
 * @since 1.0.0
 */
public abstract class AbstractPropertiesProcessor {

    /** 可配置的环境对象 */
    protected final ConfigurableEnvironment environment;

    /** 属性解析器 */
    protected final PropertyResolver resolver;

    /**
     * 构造函数
     *
     * 创建抽象属性处理器实例，初始化环境配置和属性解析器。
     *
     * @param environment 可配置的环境对象，不能为null
     * @since 1.0.0
     */
    protected AbstractPropertiesProcessor(ConfigurableEnvironment environment) {
        Assert.notNull(environment, "Environment must not be null");
        this.environment = environment;
        this.resolver = this.getPropertyResolver();
    }

    /**
     * 获取属性解析器
     *
     * 创建并配置属性解析器，支持忽略无法解析的嵌套占位符。
     *
     * @return 配置好的属性解析器
     * @since 1.0.0
     */
    protected PropertyResolver getPropertyResolver() {
        PropertySourcesPropertyResolver resolver = new PropertySourcesPropertyResolver(this.environment.getPropertySources());
        resolver.setIgnoreUnresolvableNestedPlaceholders(true);
        return resolver;
    }

    /**
     * 应用属性处理
     *
     * 子类需要实现此方法，执行具体的属性处理逻辑。
     *
     * @since 1.0.0
     */
    public abstract void apply();

    /**
     * 获取日志文件属性
     *
     * 获取指定的配置属性值，支持废弃属性的兼容处理。
     * 优先级：新属性名 > 废弃属性名 > 默认值
     *
     * @param propertyName 主要配置属性名
     * @param deprecatedPropertyName 废弃的配置属性名（可选）
     * @param defaultValue 默认值
     * @return 配置属性值
     * @since 1.0.0
     */
    public String getProperty(String propertyName,
                              String deprecatedPropertyName,
                              String defaultValue) {
        String property = this.resolver.getProperty(propertyName);
        if (StringUtils.isNotBlank(property)) {
            return property;
        }

        if (StringUtils.isNotBlank(deprecatedPropertyName)) {
            property = this.resolver.getProperty(deprecatedPropertyName);
        }

        return StringUtils.isBlank(property) ? defaultValue : property;
    }

    /**
     * 设置系统属性
     *
     * 设置JVM系统属性，如果属性已存在则不设置。
     * 因此优先级比启动脚本或Docker设置的环境变量优先级低。
     *
     * @param value 属性值
     * @param names 属性名称数组
     * @since 1.0.0
     */
    protected void setSystemProperty(String value, String... names) {
        Arrays.stream(names).forEach(name -> {
            if (StringUtils.isBlank(System.getProperty(name)) && StringUtils.isNotBlank(value)) {
                System.setProperty(name, value);
            }
        });
    }
}
