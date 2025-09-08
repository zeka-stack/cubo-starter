package dev.dong4j.zeka.starter.logsystem.event;

import dev.dong4j.zeka.kernel.common.event.BaseEvent;
import dev.dong4j.zeka.starter.logsystem.entity.AbstractLog;
import java.util.Map;

/**
 * API日志事件
 *
 * 该类是API日志记录的事件类，继承自BaseEvent基类。
 * 用于在API接口调用时发布日志记录事件，由事件处理器异步处理。
 *
 * 主要功能包括：
 * 1. 封装API日志记录的事件数据
 * 2. 支持Spring事件机制异步处理
 * 3. 提供API日志的完整信息传递
 * 4. 支持事件监听器的解耦处理
 *
 * 使用场景：
 * - API接口调用的日志记录事件
 * - 异步日志处理
 * - 日志事件的发布和订阅
 * - 日志记录的解耦处理
 *
 * 设计意图：
 * 通过事件机制实现日志记录的异步处理，提高系统性能，
 * 支持日志处理的解耦和扩展。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.20 04:59
 * @since 1.0.0
 */
public class ApiLogEvent extends BaseEvent<Map<String, AbstractLog>> {

    /** serialVersionUID */
    private static final long serialVersionUID = -4183669564491640060L;

    /**
     * Api log event
     *
     * @param source source
     * @since 1.0.0
     */
    public ApiLogEvent(Map<String, AbstractLog> source) {
        super(source);
    }

}
