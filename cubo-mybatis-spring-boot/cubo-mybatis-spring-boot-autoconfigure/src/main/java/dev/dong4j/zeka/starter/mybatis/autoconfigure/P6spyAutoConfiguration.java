package dev.dong4j.zeka.starter.mybatis.autoconfigure;

import com.p6spy.engine.spy.P6ModuleManager;
import com.p6spy.engine.spy.P6SpyDriver;
import com.p6spy.engine.spy.P6SpyOptions;
import com.p6spy.engine.spy.option.SystemProperties;
import dev.dong4j.zeka.kernel.common.constant.ConfigKey;
import dev.dong4j.zeka.kernel.common.exception.StarterException;
import dev.dong4j.zeka.kernel.common.start.ZekaAutoConfiguration;
import dev.dong4j.zeka.starter.mybatis.plugins.PerformanceInterceptor;
import java.lang.reflect.Field;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.Environment;

/**
 * P6spy SQL 监控自动配置类
 *
 * 该配置类用于自动配置 P6spy SQL 监控功能，主要功能包括：
 * 1. 验证数据源 URL 配置的正确性（必须包含 jdbc:p6spy: 前缀）
 * 2. 动态加载和配置 P6spy 相关参数
 * 3. 支持通过配置文件自定义 P6spy 行为
 * 4. 与 MyBatis Plus 集成，提供更好的 SQL 日志输出
 *
 * 注意：
 * - 该配置类仅在类路径中存在 P6SpyDriver 时生效
 * - 需要正确配置数据源驱动为 P6spy 驱动
 * - 与 PerformanceInterceptor 互斥，避免重复功能
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.12.15 10:44
 * @since 1.0.0
 */
@Slf4j
@SuppressWarnings("all")
@AutoConfiguration
@ConditionalOnClass(P6SpyDriver.class)
@EnableConfigurationProperties(P6spyProperties.class)
@ConditionalOnProperty(name = ConfigKey.DruidConfigKey.DRIVER_CLASS, havingValue = "com.p6spy.engine.spy.P6SpyDriver")
@ConditionalOnMissingBean(PerformanceInterceptor.class)
public class P6spyAutoConfiguration implements ZekaAutoConfiguration {

    /**
     * P6spy 自动配置构造方法
     *
     * 该构造方法在 Spring 容器初始化时执行，负责：
     * 1. 验证数据源 URL 配置是否正确（必须包含 jdbc:p6spy: 前缀）
     * 2. 通过反射获取 P6spyProperties 中的所有配置属性
     * 3. 优先使用系统属性中的自定义配置
     * 4. 将配置加载到 P6spy 的活动实例中
     * 5. 重新加载 P6spy 模块管理器以应用新配置
     *
     * 配置优先级：系统属性 > P6spyProperties 默认值 > P6spy 内置默认值
     *
     * @param environment Spring 环境对象，用于获取配置属性
     * @param properties P6spy 配置属性对象
     * @throws StarterException 当数据源 URL 配置错误时抛出异常
     * @since 1.0.0
     */
    public P6spyAutoConfiguration(Environment environment, P6spyProperties properties) {
        final String property = environment.getProperty(ConfigKey.SpringConfigKey.DATASOURCE_URL);
        if (!property.contains("jdbc:p6spy:")) {
            throw new StarterException("[{}] 配置错误, 正确配置: [jdbc:p6spy:db-type]", ConfigKey.SpringConfigKey.DATASOURCE_URL);
        }

        // 获取 P6spy 默认配置
        Map<String, String> defaults = P6SpyOptions.getActiveInstance().getDefaults();
        Field[] fields = P6spyProperties.class.getDeclaredFields();

        // 遍历 P6spyProperties 中的所有字段，动态设置配置
        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            try {
                Object value = field.get(properties);
                String propertiesName = SystemProperties.P6SPY_PREFIX.concat(fieldName);

                // 优先使用系统属性中的自定义配置
                if (environment.containsProperty(propertiesName)) {
                    String systemPropertyValue = environment.getProperty(propertiesName, defaults.get(fieldName));
                    defaults.put(fieldName, systemPropertyValue);
                    continue;
                }

                // 若系统属性中没有自定义配置，则使用 P6spyProperties 中的默认值
                if (value != null) {
                    defaults.put(fieldName, String.valueOf(value));
                }

            } catch (IllegalAccessException e) {
                // 反射访问异常不影响启动，记录警告日志
                log.warn("初始化 p6spy 参数异常: [{}]", e.getMessage());
                return;
            }
        }
        // 加载配置到 P6spy 活动实例
        P6SpyOptions.getActiveInstance().load(defaults);
        // 重新加载 P6spy 模块管理器以应用新配置
        P6ModuleManager.getInstance().reload();
    }

}

