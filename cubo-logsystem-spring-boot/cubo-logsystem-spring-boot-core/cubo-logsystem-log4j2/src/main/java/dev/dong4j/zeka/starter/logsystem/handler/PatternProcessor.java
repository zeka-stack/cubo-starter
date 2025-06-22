package dev.dong4j.zeka.starter.logsystem.handler;

import dev.dong4j.zeka.kernel.common.constant.ConfigKey;
import dev.dong4j.zeka.starter.logsystem.AbstractPropertiesProcessor;
import dev.dong4j.zeka.starter.logsystem.Constants;
import dev.dong4j.zeka.starter.logsystem.entity.Pattern;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * <p>Description:  zeka-stack.logging.pattern 配置处理</p>
 *
 * @author dong4j
 * @version 1.4.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.05.20 10:47
 * @since 1.4.0
 */
public class PatternProcessor extends AbstractPropertiesProcessor {
    /**
     * Pattern processor
     *
     * @param environment environment
     * @since 1.4.0
     */
    public PatternProcessor(ConfigurableEnvironment environment) {
        super(environment);
    }

    /**
     * Apply
     *
     * @since 1.4.0
     */
    @Override
    public void apply() {
        // 提前将配置绑定到配置类
        Binder binder = Binder.get(this.environment);
        Pattern pattern = binder.bind(ConfigKey.LogSystemConfigKey.LOG_PATTERN, Pattern.class).orElse(new Pattern());

        // zeka-stack.logging.pattern.console
        this.setSystemProperty(pattern.getConsole(), Constants.CONSOLE_LOG_PATTERN,
            ConfigKey.LogSystemConfigKey.LOG_PATTERN_CONSOLE);
        // zeka-stack.logging.pattern.file
        this.setSystemProperty(pattern.getFile(), Constants.FILE_LOG_PATTERN, ConfigKey.LogSystemConfigKey.LOG_PATTERN_FILE);
        // zeka-stack.logging.pattern.level
        this.setSystemProperty(pattern.getLevel(), Constants.LOG_LEVEL_PATTERN, ConfigKey.LogSystemConfigKey.LOG_PATTERN_LEVEL);
        // zeka-stack.logging.pattern.dateformat
        this.setSystemProperty(pattern.getDateformat(), Constants.LOG_DATEFORMAT_PATTERN,
            ConfigKey.LogSystemConfigKey.LOG_PATTERN_DATEFORMAT);
        // zeka-stack.logging.pattern.rolling-file-name
        this.setSystemProperty(pattern.getRollingFileName(), Constants.ROLLING_FILE_NAME_PATTERN,
            ConfigKey.LogSystemConfigKey.ROLLING_FILE_NAME);
        // zeka-stack.logging.pattern.marker
        this.setSystemProperty(pattern.getMarker(), Constants.MARKER_PATTERN, ConfigKey.LogSystemConfigKey.MARKER_PATTERN);

    }
}
