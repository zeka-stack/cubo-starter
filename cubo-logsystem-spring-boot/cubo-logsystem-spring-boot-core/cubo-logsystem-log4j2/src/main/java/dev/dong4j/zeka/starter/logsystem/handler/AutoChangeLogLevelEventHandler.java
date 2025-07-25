package dev.dong4j.zeka.starter.logsystem.handler;

import dev.dong4j.zeka.kernel.common.constant.ConfigKey;
import dev.dong4j.zeka.starter.logsystem.AbstractLoggingLevelConfiguration;
import java.util.Collections;
import java.util.Map;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;

/**
 * Listener that looks for {@link EnvironmentChangeEvent} and rebinds logger levels if any
 * changed.
 *
 * @author Dave Syer
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.09.06 19:16
 * @since 1.6.0
 */
public class AutoChangeLogLevelEventHandler extends AbstractLoggingLevelConfiguration<EnvironmentChangeEvent> {

    /** STRING_STRING_MAP */
    private static final Bindable<Map<String, String>> STRING_STRING_MAP = Bindable.mapOf(String.class, String.class);

    /**
     * Changed levels
     *
     * @param event event
     * @return the map
     * @since 1.6.0
     */
    @Override
    protected Map<String, String> changedLevels(EnvironmentChangeEvent event) {
        return Binder.get(this.environment)
            .bind(ConfigKey.PREFIX + "logging.level", STRING_STRING_MAP)
            .orElseGet(Collections::emptyMap);
    }

}
