package dev.dong4j.zeka.starter.logsystem;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 简单日志工厂类
 *
 * 该类是SLF4J的简单日志工厂实现，负责创建和管理SimpleLogger实例。
 * 提供线程安全的Logger创建和缓存机制。
 *
 * 主要功能包括：
 * 1. 实现SLF4J的ILoggerFactory接口
 * 2. 创建和管理SimpleLogger实例
 * 3. 提供Logger的缓存机制
 * 4. 支持Logger的重置和重新初始化
 * 5. 确保线程安全的Logger创建
 *
 * 特性包括：
 * - 线程安全的Logger创建
 * - 高效的Logger缓存机制
 * - 支持Logger的动态重置
 * - 提供完整的SLF4J兼容性
 *
 * 使用场景：
 * - 作为SLF4J的Logger工厂
 * - 简单日志系统的Logger管理
 * - 测试环境的日志工厂
 * - 轻量级日志需求
 *
 * 设计意图：
 * 提供简单、高效的Logger工厂实现，确保Logger的创建和管理
 * 符合SLF4J标准，支持线程安全和性能优化。
 *
 * @author Ceki G&uuml;lc&uuml;
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.08 11:46
 * @since 1.0.0
 */
public class SimpleLoggerFactory implements ILoggerFactory {

    /** Logger map */
    private final ConcurrentMap<String, Logger> loggerMap;

    /**
     * Simple logger factory
     *
     * @since 1.0.0
     */
    public SimpleLoggerFactory() {
        this.loggerMap = new ConcurrentHashMap<>();
        SimpleLogger.lazyInit();
    }

    /**
     * Return an appropriate {@link SimpleLogger} instance by name.
     *
     * @param name name
     * @return the logger
     * @since 1.0.0
     */
    @Override
    public Logger getLogger(String name) {
        Logger simpleLogger = this.loggerMap.get(name);
        if (simpleLogger != null) {
            return simpleLogger;
        } else {
            Logger newInstance = new SimpleLogger(name);
            Logger oldInstance = this.loggerMap.putIfAbsent(name, newInstance);
            return oldInstance == null ? newInstance : oldInstance;
        }
    }

    /**
     * Clear the internal logger cache.
     * <p>
     * This method is intended to be called by classes (in the same package) for
     * testing purposes. This method is internal. It can be modified, renamed or
     * removed at any time without notice.
     * <p>
     * You are strongly discouraged from calling this method in production code.
     *
     * @since 1.0.0
     */
    void reset() {
        this.loggerMap.clear();
    }
}
