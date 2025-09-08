
package org.slf4j.impl;

import org.jetbrains.annotations.Contract;
import org.slf4j.helpers.NOPMDCAdapter;
import org.slf4j.spi.MDCAdapter;

/**
 * 静态MDC绑定器
 *
 * 该类绑定到NOPMDCAdapter实现，提供SLF4J MDC（Mapped Diagnostic Context）功能的实现。
 * 使用空操作适配器，不提供实际的MDC功能。
 *
 * 主要功能包括：
 * 1. 提供MDC适配器的单例实例
 * 2. 绑定到NOPMDCAdapter实现
 * 3. 提供MDC适配器的类名信息
 * 4. 支持SLF4J MDC功能的静态绑定
 *
 * 使用场景：
 * - SLF4J MDC功能的静态绑定
 * - MDC适配器的实例管理
 * - 日志上下文功能的实现
 * - SLF4J框架的集成
 *
 * 设计意图：
 * 通过静态绑定器提供MDC适配器的统一管理，使用空操作适配器简化实现，
 * 支持SLF4J MDC功能的集成。
 *
 * @author Ceki G&uuml;lc&uuml;
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.08 11:49
 * @since 1.0.0
 */
@SuppressWarnings("all")
public final class StaticMDCBinder {

    /**
     * The unique instance of this class.
     */
    private static final StaticMDCBinder SINGLETON = new StaticMDCBinder();

    /**
     * 构造静态MDC绑定器
     *
     * 私有构造函数，确保单例模式。
     *
     * @since 1.0.0
     */
    @Contract(pure = true)
    private StaticMDCBinder() {
    }

    /**
     * 获取单例实例
     *
     * 返回StaticMDCBinder的单例实例。
     *
     * @return StaticMDCBinder的单例实例
     * @since 1.0.0
     */
    @Contract(pure = true)
    public static StaticMDCBinder getSingleton() {
        return SINGLETON;
    }

    /**
     * 获取MDC适配器实例
     *
     * 当前实现总是返回NOPMDCAdapter的实例。
     * 提供SLF4J MDC功能的空操作实现。
     *
     * @return MDC适配器实例
     * @since 1.0.0
     */
    public MDCAdapter getMDCA() {
        return new NOPMDCAdapter();
    }

    /**
     * 获取MDC适配器类名
     *
     * 返回NOPMDCAdapter的类名。
     * 用于标识MDC适配器的具体实现类。
     *
     * @return MDC适配器的类名字符串
     * @since 1.0.0
     */
    public String getMDCAdapterClassStr() {
        return NOPMDCAdapter.class.getName();
    }
}
