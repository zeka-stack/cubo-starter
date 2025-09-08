
package org.slf4j.impl;

import org.jetbrains.annotations.Contract;
import org.slf4j.IMarkerFactory;
import org.slf4j.MarkerFactory;
import org.slf4j.helpers.BasicMarkerFactory;
import org.slf4j.spi.MarkerFactoryBinder;

/**
 * 静态标记工厂绑定器
 *
 * 该类负责将MarkerFactory类与实际IMarkerFactory实例进行绑定。
 * 通过该类返回的信息执行绑定操作，提供SLF4J标记功能的实现。
 *
 * 主要功能包括：
 * 1. 提供MarkerFactory的单例实例
 * 2. 绑定MarkerFactory与IMarkerFactory实现
 * 3. 提供标记工厂的类名信息
 * 4. 支持SLF4J标记功能的静态绑定
 *
 * 使用场景：
 * - SLF4J标记功能的静态绑定
 * - 标记工厂的实例管理
 * - 日志标记功能的实现
 * - SLF4J框架的集成
 *
 * 设计意图：
 * 通过静态绑定器提供MarkerFactory的统一管理，支持SLF4J标记功能的实现，
 * 简化标记工厂的创建和使用。
 *
 * @author Ceki G&uuml;lc&uuml;
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.08 11:49
 * @since 1.0.0
 */
public final class StaticMarkerBinder implements MarkerFactoryBinder {

    /**
     * The unique instance of this class.
     */
    private static final StaticMarkerBinder SINGLETON = new StaticMarkerBinder();

    /** Marker factory */
    private final IMarkerFactory markerFactory = new BasicMarkerFactory();

    /**
     * 构造静态标记绑定器
     *
     * 私有构造函数，确保单例模式。
     *
     * @since 1.0.0
     */
    private StaticMarkerBinder() {
    }

    /**
     * 获取单例实例
     *
     * 返回StaticMarkerBinder的单例实例。
     *
     * @return StaticMarkerBinder的单例实例
     * @since 1.0.0
     */
    @Contract(pure = true)
    public static StaticMarkerBinder getSingleton() {
        return SINGLETON;
    }

    /**
     * 获取标记工厂实例
     *
     * 当前实现总是返回BasicMarkerFactory的实例。
     * 提供SLF4J标记功能的实现。
     *
     * @return 标记工厂实例
     * @since 1.0.0
     */
    @Override
    public IMarkerFactory getMarkerFactory() {
        return this.markerFactory;
    }

    /**
     * 获取标记工厂类名
     *
     * 当前实现返回BasicMarkerFactory的类名。
     * 用于标识标记工厂的具体实现类。
     *
     * @return 标记工厂的类名字符串
     * @since 1.0.0
     */
    @Override
    public String getMarkerFactoryClassStr() {
        return BasicMarkerFactory.class.getName();
    }

}
