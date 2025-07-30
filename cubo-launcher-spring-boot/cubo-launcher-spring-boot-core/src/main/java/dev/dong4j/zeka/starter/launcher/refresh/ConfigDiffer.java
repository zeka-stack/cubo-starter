package dev.dong4j.zeka.starter.launcher.refresh;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 5: 比较新旧配置 Map，判断哪些配置项发生了变化，并映射到对应的配置类。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.07.29
 * @since 1.0.0
 */
public class ConfigDiffer {

    public static class DiffResult {
        public final Set<String> changedKeys;
        public final boolean hasDiff;

        public DiffResult(Set<String> changedKeys) {
            this.changedKeys = changedKeys;
            this.hasDiff = !changedKeys.isEmpty();
        }
    }

    public DiffResult diff(Map<String, Object> oldConfig, Map<String, Object> newConfig) {
        Set<String> changedKeys = new HashSet<>();

        Set<String> allKeys = new HashSet<>();
        allKeys.addAll(oldConfig.keySet());
        allKeys.addAll(newConfig.keySet());

        for (String key : allKeys) {
            Object oldValue = oldConfig.get(key);
            Object newValue = newConfig.get(key);

            if (!Objects.equals(oldValue, newValue)) {
                changedKeys.add(key);
            }
        }

        return new DiffResult(changedKeys);
    }
}
