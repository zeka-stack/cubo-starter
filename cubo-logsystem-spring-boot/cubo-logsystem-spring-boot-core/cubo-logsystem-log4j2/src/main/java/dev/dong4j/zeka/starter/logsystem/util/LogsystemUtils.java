package dev.dong4j.zeka.starter.logsystem.util;

import dev.dong4j.zeka.starter.logsystem.listener.ZekaLoggingListener;
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

import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * <p>Description: 日志工具类, 提供直接修改日志等级工具方法, 此类只适用于单元测试(非容器启动时)动态修改日志等级,
 * 如果在容器中动态修改日志等级, 请使用 {@link LoggingSystem#get(java.lang.ClassLoader)}, 可参考 {@link ZekaLoggingListener}</p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.16 16:06
 * @since 1.0.0
 */
@Slf4j
@UtilityClass
public class LogsystemUtils {

    /** LEVELS */
    private static final LogLevels<Level> LEVELS = new LogLevels<>();

    static {
        LEVELS.map(LogLevel.TRACE, Level.TRACE);
        LEVELS.map(LogLevel.DEBUG, Level.DEBUG);
        LEVELS.map(LogLevel.INFO, Level.INFO);
        LEVELS.map(LogLevel.WARN, Level.WARN);
        LEVELS.map(LogLevel.ERROR, Level.ERROR);
        LEVELS.map(LogLevel.FATAL, Level.FATAL);
        LEVELS.map(LogLevel.OFF, Level.OFF);
    }

    /**
     * Sets log level *
     *
     * @param loggerName logger name
     * @param logLevel   log level
     * @since 1.0.0
     */
    public void setLogLevel(String loggerName, LogLevel logLevel) {
        Level level = LEVELS.convertSystemToNative(logLevel);
        LoggerConfig loggerConfig = getLoggerConfig(loggerName);
        if (loggerConfig == null) {
            loggerConfig = new LoggerConfig(loggerName, level, true);
            getLoggerContext().getConfiguration().addLogger(loggerName, loggerConfig);
        } else {
            loggerConfig.setLevel(level);
        }
        getLoggerContext().updateLoggers();
    }

    /**
     * Gets logger config *
     *
     * @param name name
     * @return the logger config
     * @since 1.0.0
     */
    private LoggerConfig getLoggerConfig(String name) {
        if (!StringUtils.hasLength(name) || LoggingSystem.ROOT_LOGGER_NAME.equals(name)) {
            name = LogManager.ROOT_LOGGER_NAME;
        }
        return getLoggerContext().getConfiguration().getLoggers().get(name);
    }

    /**
     * Gets logger context *
     *
     * @return the logger context
     * @since 1.0.0
     */
    private LoggerContext getLoggerContext() {
        return (LoggerContext) LogManager.getContext(false);
    }

    /**
     * <p>Description: </p>
     *
     * @param <T> parameter
     * @author dong4j
     * @version 1.3.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.03.16 15:51
     * @since 1.0.0
     */
    private static class LogLevels<T> {

        /** System to native */
        private final Map<LogLevel, T> systemToNative;

        /** Native to system */
        private final Map<T, LogLevel> nativeToSystem;

        /**
         * Log levels
         *
         * @since 1.0.0
         */
        LogLevels() {
            this.systemToNative = new EnumMap<>(LogLevel.class);
            this.nativeToSystem = new HashMap<>(8);
        }

        /**
         * Map *
         *
         * @param system      system
         * @param nativeLevel native level
         * @since 1.0.0
         */
        void map(LogLevel system, T nativeLevel) {
            this.systemToNative.putIfAbsent(system, nativeLevel);
            this.nativeToSystem.putIfAbsent(nativeLevel, system);
        }

        /**
         * Convert native to system log level
         *
         * @param level level
         * @return the log level
         * @since 1.0.0
         */
        public LogLevel convertNativeToSystem(T level) {
            return this.nativeToSystem.get(level);
        }

        /**
         * Convert system to native t
         *
         * @param level level
         * @return the t
         * @since 1.0.0
         */
        T convertSystemToNative(LogLevel level) {
            return this.systemToNative.get(level);
        }

        /**
         * Gets supported *
         *
         * @return the supported
         * @since 1.0.0
         */
        @Contract(" -> new")
        public @NotNull Set<LogLevel> getSupported() {
            return new LinkedHashSet<>(this.nativeToSystem.values());
        }

    }
}
