package dev.dong4j.zeka.starter.logsystem.handler;

import dev.dong4j.zeka.kernel.common.constant.ConfigKey;
import dev.dong4j.zeka.starter.logsystem.AbstractPropertiesProcessor;
import dev.dong4j.zeka.starter.logsystem.Constants;
import dev.dong4j.zeka.starter.logsystem.entity.Pattern;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * 日志格式模式处理器
 *
 * 该类负责处理 zeka-stack.logging.pattern 相关的配置，将配置属性转换为系统属性。
 * 主要功能包括：
 * 1. 处理日志格式模式的配置绑定
 * 2. 将配置属性转换为系统属性供Log4j2使用
 * 3. 支持控制台和文件的不同格式配置
 * 4. 提供日志级别、日期格式等模式配置
 *
 * 处理的配置项：
 * - 控制台日志格式模式
 * - 文件日志格式模式
 * - 日志级别格式模式
 * - 日期时间格式模式
 * - 滚动文件名格式模式
 * - 标记格式模式
 *
 * 使用场景：
 * - 日志系统初始化时的格式配置
 * - 多环境下的日志格式切换
 * - 自定义日志输出格式
 *
 * 设计意图：
 * 通过统一的配置处理器，简化日志格式模式的管理和配置，
 * 提供灵活的日志输出格式定制能力。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.05.20 10:47
 * @since 1.0.0
 */
public class PatternProcessor extends AbstractPropertiesProcessor {
    /**
     * 构造函数
     *
     * 创建日志格式模式处理器实例，初始化环境配置。
     *
     * @param environment 可配置的环境对象，用于获取配置属性
     * @since 1.0.0
     */
    public PatternProcessor(ConfigurableEnvironment environment) {
        super(environment);
    }

    /**
     * 应用日志格式模式配置
     *
     * 将 zeka-stack.logging.pattern 相关的配置属性转换为系统属性，
     * 供Log4j2配置文件使用。处理各种日志格式模式的配置项。
     *
     * 处理流程：
     * 1. 绑定日志格式模式配置到Pattern对象
     * 2. 将各个格式模式配置转换为系统属性
     * 3. 设置Log4j2配置文件可用的属性值
     *
     * @since 1.0.0
     */
    @Override
    public void apply() {
        // 绑定日志格式模式配置到Pattern对象
        Binder binder = Binder.get(this.environment);
        Pattern pattern = binder.bind(ConfigKey.LogSystemConfigKey.LOG_PATTERN, Pattern.class).orElse(new Pattern());

        // 设置控制台日志格式模式
        this.setSystemProperty(pattern.getConsole(), Constants.CONSOLE_LOG_PATTERN,
            ConfigKey.LogSystemConfigKey.LOG_PATTERN_CONSOLE);
        // 设置文件日志格式模式
        this.setSystemProperty(pattern.getFile(), Constants.FILE_LOG_PATTERN, ConfigKey.LogSystemConfigKey.LOG_PATTERN_FILE);
        // 设置日志级别格式模式
        this.setSystemProperty(pattern.getLevel(), Constants.LOG_LEVEL_PATTERN, ConfigKey.LogSystemConfigKey.LOG_PATTERN_LEVEL);
        // 设置日期时间格式模式
        this.setSystemProperty(pattern.getDateformat(), Constants.LOG_DATEFORMAT_PATTERN,
            ConfigKey.LogSystemConfigKey.LOG_PATTERN_DATEFORMAT);
        // 设置滚动文件名格式模式
        this.setSystemProperty(pattern.getRollingFileName(), Constants.ROLLING_FILE_NAME_PATTERN,
            ConfigKey.LogSystemConfigKey.ROLLING_FILE_NAME);
        // 设置标记格式模式
        this.setSystemProperty(pattern.getMarker(), Constants.MARKER_PATTERN, ConfigKey.LogSystemConfigKey.MARKER_PATTERN);

    }
}
