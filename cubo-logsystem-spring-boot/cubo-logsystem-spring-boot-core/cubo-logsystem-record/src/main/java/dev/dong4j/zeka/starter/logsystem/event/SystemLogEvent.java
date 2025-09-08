package dev.dong4j.zeka.starter.logsystem.event;

import dev.dong4j.zeka.kernel.common.event.BaseEvent;
import java.util.Map;

/**
 * 系统日志事件
 *
 * 该类是系统日志记录的事件类，继承自BaseEvent基类。
 * 用于在系统操作时发布系统日志记录事件，由事件处理器异步处理。
 *
 * 主要功能包括：
 * 1. 封装系统日志记录的事件数据
 * 2. 支持Spring事件机制异步处理
 * 3. 提供系统日志的完整信息传递
 * 4. 支持事件监听器的解耦处理
 *
 * 使用场景：
 * - 系统敏感操作的审计日志记录事件
 * - 异步系统日志处理
 * - 系统日志事件的发布和订阅
 * - 系统日志记录的解耦处理
 *
 * 设计意图：
 * 通过事件机制实现系统日志记录的异步处理，提高系统性能，
 * 支持系统日志处理的解耦和扩展。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.21 10:07
 * @since 1.0.0
 */
public class SystemLogEvent extends BaseEvent<Map<String, Object>> {

    /** serialVersionUID */
    private static final long serialVersionUID = -3789524764345821072L;

    /**
     * 构造系统日志事件
     *
     * 创建一个包含系统日志信息的事件对象，用于在系统中传播系统日志数据。
     * 事件数据包含系统日志实体和相关的请求信息。
     *
     * @param source 事件源数据，包含系统日志实体和请求信息的Map对象
     * @since 1.0.0
     */
    public SystemLogEvent(Map<String, Object> source) {
        super(source);
    }

}
