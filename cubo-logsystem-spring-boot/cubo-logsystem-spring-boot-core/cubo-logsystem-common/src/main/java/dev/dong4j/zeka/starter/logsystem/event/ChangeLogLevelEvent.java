package dev.dong4j.zeka.starter.logsystem.event;

import dev.dong4j.zeka.kernel.common.event.BaseEvent;
import java.util.Map;

/**
 * 日志级别变更事件
 *
 * 该事件用于通知日志级别发生变更，支持动态调整日志输出级别。
 * 主要功能包括：
 * 1. 封装日志级别变更的相关信息
 * 2. 支持Spring事件机制进行事件发布和监听
 * 3. 提供日志级别变更的上下文信息
 * 4. 支持手动和自动触发的日志级别调整
 *
 * 使用场景：
 * - 运行时动态调整日志级别
 * - 配置中心推送的日志级别变更
 * - 运维人员手动调整日志级别
 * - 监控系统触发的日志级别调整
 *
 * 设计意图：
 * 通过事件机制实现日志级别的解耦和动态调整，
 * 提升系统的灵活性和可维护性。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.09.06 13:56
 * @since 1.0.0
 */
public class ChangeLogLevelEvent extends BaseEvent<Map<String, String>> {
    /** 序列化版本号 */
    private static final long serialVersionUID = -7263310380223565799L;

    /**
     * 构造函数
     *
     * 创建日志级别变更事件实例，封装日志级别变更的相关信息。
     *
     * @param source 日志级别变更的源数据，包含Logger名称和对应的新级别
     * @since 1.0.0
     */
    public ChangeLogLevelEvent(Map<String, String> source) {
        super(source);
    }
}
