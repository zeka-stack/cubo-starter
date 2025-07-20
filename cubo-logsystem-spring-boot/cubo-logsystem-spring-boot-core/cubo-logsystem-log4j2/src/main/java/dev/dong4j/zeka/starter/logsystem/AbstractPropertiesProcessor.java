package dev.dong4j.zeka.starter.logsystem;

import dev.dong4j.zeka.kernel.common.util.StringUtils;
import java.util.Arrays;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertyResolver;
import org.springframework.core.env.PropertySourcesPropertyResolver;
import org.springframework.util.Assert;

/**
 * <p>Description: 抽象的参数处理器, 将配置文件中的配置(Environment) 写入到 JVM, logsystem 才能读取. </p>
 *
 * @author dong4j
 * @version 1.4.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.05.20 11:41
 * @since 1.4.0
 */
public abstract class AbstractPropertiesProcessor {

    /** Environment */
    protected final ConfigurableEnvironment environment;
    /** Resolver */
    protected final PropertyResolver resolver;

    /**
     * Abstract properties processor
     *
     * @param environment environment
     * @since 1.4.0
     */
    protected AbstractPropertiesProcessor(ConfigurableEnvironment environment) {
        Assert.notNull(environment, "Environment must not be null");
        this.environment = environment;
        this.resolver = this.getPropertyResolver();
    }

    /**
     * Gets property resolver *
     *
     * @return the property resolver
     * @since 1.0.0
     */
    protected PropertyResolver getPropertyResolver() {
        PropertySourcesPropertyResolver resolver = new PropertySourcesPropertyResolver(this.environment.getPropertySources());
        resolver.setIgnoreUnresolvableNestedPlaceholders(true);
        return resolver;
    }

    /**
     * Apply
     *
     * @since 1.4.0
     */
    public abstract void apply();

    /**
     * Gets log file property *
     *
     * @param propertyName           property 配置名
     * @param deprecatedPropertyName deprecated 配置别名
     * @param defaultValue           default 默认值
     * @return the log file property
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
     * 设置系统环境变量, 如果已存在就不设置, 因此优先级比启动脚本或 docker 设置的环境变量优先级低
     *
     * @param value value
     * @param names names
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
