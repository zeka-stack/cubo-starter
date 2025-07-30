package dev.dong4j.zeka.starter.launcher.refresh;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;

/**
 * 配置变更监听执行器
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.07.29
 * @since 1.0.0
 */
@Slf4j
public class ConfigFileWatcherRunner {

    private final DynamicConfigLoader loader;
    private final RefreshScopeRefresher refresher;
    private final ConfigDiffer differ;
    private final Environment environment;

    public ConfigFileWatcherRunner(DynamicConfigLoader loader,
                                   RefreshScopeRefresher refresher,
                                   ConfigDiffer differ,
                                   Environment environment) {
        this.loader = loader;
        this.refresher = refresher;
        this.differ = differ;
        this.environment = environment;
    }

    @PostConstruct
    public void start() {
        AtomicReference<Map<String, Object>> current = new AtomicReference<>(loader.loadCurrentEnvironmentConfig());

        new Thread(new ConfigFileWatcher(environment,
            changedFile -> {
                Map<String, Object> latest = loader.loadCurrentEnvironmentConfig();
                ConfigDiffer.DiffResult diff = differ.diff(current.get(), latest);

                if (diff.hasDiff) {
                    log.warn("检测到配置文件发生变更: {}", diff.changedKeys);
                    current.set(latest);
                    refresher.refreshByChangedKeys(diff.changedKeys);
                }
            }), "refresh-scope-watcher").start();
    }
}
