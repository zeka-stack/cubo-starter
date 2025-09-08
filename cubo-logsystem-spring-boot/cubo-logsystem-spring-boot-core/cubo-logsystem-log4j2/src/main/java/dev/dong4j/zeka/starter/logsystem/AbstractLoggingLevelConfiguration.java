package dev.dong4j.zeka.starter.logsystem;

import dev.dong4j.zeka.kernel.common.constant.ConfigDefaultValue;
import dev.dong4j.zeka.kernel.common.util.Jsons;
import dev.dong4j.zeka.starter.logsystem.listener.ZekaLoggingListener;
import java.util.Locale;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggerGroup;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

/**
 * 抽象日志级别配置基类
 *
 * 该类是日志级别配置的抽象基类，提供统一的日志级别变更处理框架。
 * 通过监听应用事件，实现日志级别的动态调整和配置。
 *
 * 主要功能包括：
 * 1. 监听应用事件，触发日志级别变更
 * 2. 提供统一的日志级别配置处理框架
 * 3. 支持日志级别分组的管理
 * 4. 处理日志级别字符串到枚举的转换
 * 5. 提供环境感知的配置能力
 *
 * 使用场景：
 * - 动态调整应用日志级别
 * - 配置中心变更时的日志级别同步
 * - 运行时日志级别管理
 * - 多环境下的日志级别配置
 *
 * 设计意图：
 * 通过抽象基类提供统一的日志级别配置处理框架，简化具体实现类的开发，
 * 确保日志级别变更的一致性和可扩展性。
 *
 * @param <T> 应用事件类型参数
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.09.06 19:51
 * @since 1.0.0
 */
@Slf4j
public abstract class AbstractLoggingLevelConfiguration<T extends ApplicationEvent> implements ApplicationListener<T>, EnvironmentAware {
    /** Environment */
    protected Environment environment;

    /**
     * Sets environment *
     *
     * @param environment environment
     * @since 1.0.0
     */
    @Override
    public void setEnvironment(@NotNull Environment environment) {
        this.environment = environment;
    }

    /**
     * On application event
     *
     * @param event event
     * @since 1.0.0
     */
    @Override
    public void onApplicationEvent(@NotNull T event) {
        if (this.environment == null) {
            return;
        }
        LoggingSystem system = LoggingSystem.get(LoggingSystem.class.getClassLoader());
        this.configureLogLevel(system, this.changedLevels(event));
    }

    /**
     * Changed levels
     *
     * @param event event
     * @return the map
     * @since 1.0.0
     */
    protected abstract Map<String, String> changedLevels(T event);

    /**
     * Configure log level
     *
     * @param loggingSystem logging system
     * @param levels        levels
     * @since 1.0.0
     */
    public void configureLogLevel(LoggingSystem loggingSystem, @NotNull Map<String, String> levels) {
        log.info("日志等级修改事件处理: {}", Jsons.toJson(levels));

        levels.forEach((name, level) -> {
            level = this.environment.resolvePlaceholders(level);

            LoggerGroup group = ZekaLoggingListener.getLoggerGroups().get(name);
            if (group != null && group.hasMembers()) {
                group.configureLogLevel(this.resolveLogLevel(level), loggingSystem::setLogLevel);
                return;
            }

            if (name.equalsIgnoreCase(LoggingSystem.ROOT_LOGGER_NAME)) {
                name = null;
            }
            loggingSystem.setLogLevel(name, this.resolveLogLevel(level));
        });
    }

    /**
     * Resolve log level
     *
     * @param level level
     * @return the log level
     * @since 1.0.0
     */
    private LogLevel resolveLogLevel(@NotNull String level) {
        String trimmedLevel = level.trim();
        if (ConfigDefaultValue.FALSE_STRING.equalsIgnoreCase(trimmedLevel)) {
            return LogLevel.OFF;
        }
        return LogLevel.valueOf(trimmedLevel.toUpperCase(Locale.ENGLISH));
    }
}
