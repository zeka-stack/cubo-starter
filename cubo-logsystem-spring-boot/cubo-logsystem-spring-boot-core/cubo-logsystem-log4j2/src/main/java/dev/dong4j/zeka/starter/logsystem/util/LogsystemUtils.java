package dev.dong4j.zeka.starter.logsystem.util;

import dev.dong4j.zeka.starter.logsystem.listener.ZekaLoggingListener;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.util.StringUtils;

/**
 * 日志系统工具类
 *
 * 该类提供直接修改日志级别的工具方法，主要用于单元测试环境下的日志级别动态调整。
 * 主要功能包括：
 * 1. 提供日志级别的动态设置方法
 * 2. 支持Spring Boot日志级别与Log4j2级别的转换
 * 3. 提供Logger配置的获取和更新功能
 * 4. 支持日志级别的双向转换映射
 *
 * 使用场景：
 * - 单元测试中的日志级别动态调整
 * - 非容器环境下的日志级别修改
 * - 调试时的临时日志级别调整
 * - 测试用例中的日志输出控制
 *
 * 注意事项：
 * - 此类仅适用于单元测试（非容器启动时）动态修改日志级别
 * - 如果在容器中动态修改日志级别，请使用 {@link LoggingSystem#get(java.lang.ClassLoader)}
 * - 可参考 {@link ZekaLoggingListener} 了解容器环境下的日志级别管理
 *
 * 设计意图：
 * 通过提供简单易用的工具方法，简化单元测试中的日志级别管理，
 * 同时避免在容器环境下的复杂配置。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.16 16:06
 * @since 1.0.0
 */
@Slf4j
@UtilityClass
public class LogsystemUtils {

    /** 日志级别转换映射表 */
    private static final LogLevels<Level> LEVELS = new LogLevels<>();

    static {
        // 初始化Spring Boot日志级别与Log4j2级别的映射关系
        LEVELS.map(LogLevel.TRACE, Level.TRACE);
        LEVELS.map(LogLevel.DEBUG, Level.DEBUG);
        LEVELS.map(LogLevel.INFO, Level.INFO);
        LEVELS.map(LogLevel.WARN, Level.WARN);
        LEVELS.map(LogLevel.ERROR, Level.ERROR);
        LEVELS.map(LogLevel.FATAL, Level.FATAL);
        LEVELS.map(LogLevel.OFF, Level.OFF);
    }

    /**
     * 设置日志级别
     *
     * 动态设置指定Logger的日志级别，如果Logger不存在则创建新的Logger配置。
     * 该方法会将Spring Boot的日志级别转换为Log4j2的日志级别，然后应用到Logger配置中。
     *
     * 使用场景：
     * - 单元测试中的日志级别动态调整
     * - 调试时的临时日志级别修改
     * - 测试用例中的日志输出控制
     *
     * 注意事项：
     * - 此方法仅适用于非容器环境
     * - 修改后会立即生效，无需重启应用
     * - 如果Logger不存在，会自动创建新的Logger配置
     *
     * @param loggerName Logger名称，null或空字符串表示根Logger
     * @param logLevel 要设置的日志级别
     * @since 1.0.0
     */
    public void setLogLevel(String loggerName, LogLevel logLevel) {
        Level level = LEVELS.convertSystemToNative(logLevel);
        LoggerConfig loggerConfig = getLoggerConfig(loggerName);
        if (loggerConfig == null) {
            // 如果Logger不存在，创建新的Logger配置
            loggerConfig = new LoggerConfig(loggerName, level, true);
            getLoggerContext().getConfiguration().addLogger(loggerName, loggerConfig);
        } else {
            // 如果Logger已存在，更新其日志级别
            loggerConfig.setLevel(level);
        }
        // 更新Logger上下文，使配置生效
        getLoggerContext().updateLoggers();
    }

    /**
     * 获取Logger配置
     *
     * 根据Logger名称获取对应的Logger配置对象。如果名称为空或为根Logger名称，
     * 则返回根Logger的配置。
     *
     * @param name Logger名称
     * @return Logger配置对象，如果不存在则返回null
     * @since 1.0.0
     */
    private LoggerConfig getLoggerConfig(String name) {
        // 处理根Logger的特殊情况
        if (!StringUtils.hasLength(name) || LoggingSystem.ROOT_LOGGER_NAME.equals(name)) {
            name = LogManager.ROOT_LOGGER_NAME;
        }
        return getLoggerContext().getConfiguration().getLoggers().get(name);
    }

    /**
     * 获取Logger上下文
     *
     * 获取当前Log4j2的Logger上下文，用于访问Logger配置和进行配置更新。
     *
     * @return Log4j2的Logger上下文对象
     * @since 1.0.0
     */
    private LoggerContext getLoggerContext() {
        return (LoggerContext) LogManager.getContext(false);
    }

    /**
     * 日志级别转换器内部类
     *
     * 该类负责Spring Boot日志级别与Log4j2日志级别之间的双向转换。
     * 主要功能包括：
     * 1. 维护两种日志级别之间的映射关系
     * 2. 提供双向转换方法
     * 3. 支持获取所有支持的日志级别
     *
     * 使用场景：
     * - Spring Boot日志级别转换为Log4j2级别
     * - Log4j2级别转换为Spring Boot级别
     * - 获取所有支持的日志级别列表
     *
     * @param <T> 原生日志级别类型（如Log4j2的Level）
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.03.16 15:51
     * @since 1.0.0
     */
    private static class LogLevels<T> {

        /** Spring Boot日志级别到原生日志级别的映射 */
        private final Map<LogLevel, T> systemToNative;

        /** 原生日志级别到Spring Boot日志级别的映射 */
        private final Map<T, LogLevel> nativeToSystem;

        /**
         * 构造函数
         *
         * 初始化日志级别转换器，创建双向映射表。
         *
         * @since 1.0.0
         */
        LogLevels() {
            this.systemToNative = new EnumMap<>(LogLevel.class);
            this.nativeToSystem = new HashMap<>(8);
        }

        /**
         * 添加日志级别映射
         *
         * 在Spring Boot日志级别和原生日志级别之间建立映射关系。
         * 如果映射已存在，则不会覆盖现有映射。
         *
         * @param system Spring Boot日志级别
         * @param nativeLevel 原生日志级别
         * @since 1.0.0
         */
        void map(LogLevel system, T nativeLevel) {
            this.systemToNative.putIfAbsent(system, nativeLevel);
            this.nativeToSystem.putIfAbsent(nativeLevel, system);
        }

        /**
         * 将原生日志级别转换为Spring Boot日志级别
         *
         * @param level 原生日志级别
         * @return 对应的Spring Boot日志级别，如果未找到则返回null
         * @since 1.0.0
         */
        public LogLevel convertNativeToSystem(T level) {
            return this.nativeToSystem.get(level);
        }

        /**
         * 将Spring Boot日志级别转换为原生日志级别
         *
         * @param level Spring Boot日志级别
         * @return 对应的原生日志级别，如果未找到则返回null
         * @since 1.0.0
         */
        T convertSystemToNative(LogLevel level) {
            return this.systemToNative.get(level);
        }

        /**
         * 获取所有支持的Spring Boot日志级别
         *
         * @return 支持的日志级别集合
         * @since 1.0.0
         */
        @Contract(" -> new")
        public @NotNull Set<LogLevel> getSupported() {
            return new LinkedHashSet<>(this.nativeToSystem.values());
        }

    }
}
