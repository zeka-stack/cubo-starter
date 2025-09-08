package dev.dong4j.zeka.starter.logsystem.handler;

import dev.dong4j.zeka.kernel.common.constant.ConfigKey;
import dev.dong4j.zeka.starter.logsystem.AbstractLoggingLevelConfiguration;
import java.util.Collections;
import java.util.Map;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;

/**
 * 自动日志级别变更事件处理器
 *
 * 该类监听 {@link EnvironmentChangeEvent} 事件，当环境配置发生变化时，
 * 自动重新绑定Logger级别。主要用于Spring Cloud环境下的动态日志级别调整。
 *
 * 主要功能包括：
 * 1. 监听环境配置变更事件
 * 2. 自动检测日志级别配置变化
 * 3. 重新绑定Logger级别配置
 * 4. 支持动态调整日志输出级别
 *
 * 使用场景：
 * - Spring Cloud环境下的配置中心变更
 * - 动态调整应用日志级别
 * - 运行时日志级别热更新
 * - 配置刷新时的日志级别同步
 *
 * 设计意图：
 * 通过监听环境变更事件，实现日志级别的动态调整，
 * 提供灵活的日志级别管理能力。
 *
 * @author Dave Syer
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.09.06 19:16
 * @since 1.0.0
 */
public class AutoChangeLogLevelEventHandler extends AbstractLoggingLevelConfiguration<EnvironmentChangeEvent> {

    /** 字符串到字符串映射的绑定器 */
    private static final Bindable<Map<String, String>> STRING_STRING_MAP = Bindable.mapOf(String.class, String.class);

    /**
     * 获取变更的日志级别
     *
     * 从环境配置中获取变更的日志级别配置，返回Logger名称到日志级别的映射。
     * 该方法会绑定 zeka-stack.logging.level 配置项。
     *
     * @param event 环境变更事件
     * @return Logger名称到日志级别的映射，如果没有配置则返回空映射
     * @since 1.0.0
     */
    @Override
    protected Map<String, String> changedLevels(EnvironmentChangeEvent event) {
        return Binder.get(this.environment)
            .bind(ConfigKey.PREFIX + "logging.level", STRING_STRING_MAP)
            .orElseGet(Collections::emptyMap);
    }

}
