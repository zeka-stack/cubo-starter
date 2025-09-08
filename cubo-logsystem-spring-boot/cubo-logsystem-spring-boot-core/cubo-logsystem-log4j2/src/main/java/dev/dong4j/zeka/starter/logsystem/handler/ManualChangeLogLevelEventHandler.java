package dev.dong4j.zeka.starter.logsystem.handler;

import dev.dong4j.zeka.starter.logsystem.AbstractLoggingLevelConfiguration;
import dev.dong4j.zeka.starter.logsystem.event.ChangeLogLevelEvent;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

/**
 * 手动日志级别变更事件处理器
 *
 * 该类处理手动触发的日志级别变更事件，用于动态调整日志输出级别。
 * 主要功能包括：
 * 1. 处理手动触发的日志级别变更事件
 * 2. 支持运行时动态调整日志级别
 * 3. 提供灵活的日志级别管理接口
 * 4. 支持批量日志级别调整
 *
 * 使用场景：
 * - 运行时动态调整日志级别
 * - 调试时的临时日志级别修改
 * - 批量日志级别调整
 * - 日志级别管理接口调用
 *
 * 设计意图：
 * 通过事件驱动的方式，提供灵活的日志级别管理能力，
 * 支持运行时动态调整日志输出级别。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.09.06 14:00
 * @since 1.0.0
 */
public class ManualChangeLogLevelEventHandler extends AbstractLoggingLevelConfiguration<ChangeLogLevelEvent> {

    /**
     * 获取变更的日志级别
     *
     * 从日志级别变更事件中获取变更的日志级别配置。
     * 直接返回事件源数据中的Logger名称到日志级别的映射。
     *
     * @param event 日志级别变更事件
     * @return Logger名称到日志级别的映射
     * @since 1.0.0
     */
    @Override
    protected Map<String, String> changedLevels(@NotNull ChangeLogLevelEvent event) {
        return event.getSource();
    }

}
