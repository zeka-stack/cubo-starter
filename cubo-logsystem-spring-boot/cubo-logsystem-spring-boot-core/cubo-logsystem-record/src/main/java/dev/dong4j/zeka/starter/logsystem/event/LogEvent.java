package dev.dong4j.zeka.starter.logsystem.event;

import dev.dong4j.zeka.kernel.common.event.BaseEvent;

/**
 * 抽象日志事件基类
 *
 * 该类是所有日志事件的抽象基类，继承自BaseEvent基类。
 * 为具体的日志事件类型（如ApiLogEvent、ErrorLogEvent、SystemLogEvent等）提供统一的基础结构。
 *
 * 主要功能包括：
 * 1. 定义日志事件的基础结构
 * 2. 提供统一的序列化支持
 * 3. 支持Spring事件机制
 * 4. 为具体日志事件类型提供扩展基础
 *
 * 使用场景：
 * - 作为所有日志事件的基类
 * - 提供统一的日志事件结构
 * - 支持日志事件的标准化处理
 * - 简化具体日志事件类的开发
 *
 * 设计意图：
 * 通过抽象基类提供统一的日志事件结构，确保所有日志事件类型的一致性，
 * 简化日志事件类的开发和维护。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.21 10:26
 * @since 1.0.0
 */
public abstract class LogEvent extends BaseEvent<Object> {

    /** serialVersionUID */
    private static final long serialVersionUID = -4449996576925041248L;

    /**
     * 构造日志事件
     *
     * 创建一个日志事件对象，用于在系统中传播日志数据。
     * 子类应该调用此构造函数来初始化事件对象。
     *
     * @param source 事件源数据，包含日志相关信息的对象
     * @since 1.0.0
     */
    public LogEvent(Object source) {
        super(source);
    }
}
