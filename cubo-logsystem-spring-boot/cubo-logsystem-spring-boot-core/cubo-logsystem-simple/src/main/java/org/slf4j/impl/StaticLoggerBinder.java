
package org.slf4j.impl;

import dev.dong4j.zeka.starter.logsystem.SimpleLoggerFactory;
import org.jetbrains.annotations.Contract;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LoggerFactoryBinder;

/**
 * SLF4J静态Logger绑定器
 *
 * 该类是SLF4J的静态Logger绑定器实现，负责绑定SimpleLoggerFactory到SLF4J系统。
 * 提供SLF4J与SimpleLogger之间的桥接功能。
 *
 * 主要功能包括：
 * 1. 实现SLF4J的LoggerFactoryBinder接口
 * 2. 绑定SimpleLoggerFactory到SLF4J系统
 * 3. 提供Logger工厂的单例访问
 * 4. 支持SLF4J的静态绑定机制
 * 5. 确保Logger工厂的唯一性
 *
 * 特性包括：
 * - 单例模式的Logger绑定器
 * - 线程安全的工厂访问
 * - 支持SLF4J的静态绑定
 * - 提供完整的SLF4J兼容性
 *
 * 使用场景：
 * - SLF4J与SimpleLogger的桥接
 * - 简单日志系统的初始化
 * - 测试环境的日志绑定
 * - 轻量级日志需求
 *
 * 设计意图：
 * 提供SLF4J与SimpleLogger之间的桥接功能，确保SLF4J能够
 * 正确使用SimpleLogger作为底层日志实现。
 *
 * @author Ceki G&uuml;lc&uuml;
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.08 11:48
 * @since 1.0.0
 */
public final class StaticLoggerBinder implements LoggerFactoryBinder {

    /**
     * The unique instance of this class.
     */
    private static final StaticLoggerBinder SINGLETON = new StaticLoggerBinder();

    /**
     * Return the singleton of this class.
     *
     * @return the StaticLoggerBinder singleton
     * @since 1.0.0
     */
    @Contract(pure = true)
    public static StaticLoggerBinder getSingleton() {
        return SINGLETON;
    }

    /** LOGGER_FACTORY_CLASS_STR */
    private static final String LOGGER_FACTORY_CLASS_STR = SimpleLoggerFactory.class.getName();

    /**
     * The ILoggerFactory instance returned by the {@link #getLoggerFactory}
     * method should always be the same object
     */
    private final ILoggerFactory loggerFactory;

    /**
     * Static logger binder
     *
     * @since 1.0.0
     */
    private StaticLoggerBinder() {
        this.loggerFactory = new SimpleLoggerFactory();
    }

    /**
     * Gets logger factory *
     *
     * @return the logger factory
     * @since 1.0.0
     */
    @Override
    public ILoggerFactory getLoggerFactory() {
        return this.loggerFactory;
    }

    /**
     * Gets logger factory class str *
     *
     * @return the logger factory class str
     * @since 1.0.0
     */
    @Override
    public String getLoggerFactoryClassStr() {
        return LOGGER_FACTORY_CLASS_STR;
    }
}
