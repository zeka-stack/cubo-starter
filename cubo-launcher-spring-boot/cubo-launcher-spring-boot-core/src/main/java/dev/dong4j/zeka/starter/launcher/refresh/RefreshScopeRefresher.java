package dev.dong4j.zeka.starter.launcher.refresh;

import dev.dong4j.zeka.kernel.common.util.JsonUtils;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.origin.OriginTrackedValue;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

/**
 * 6: 根据配置变更项（扁平化 key 集合）精准刷新受影响的 {@code @ConfigurationProperties} Bean。
 *
 * <p>刷新逻辑：</p>
 * <ol>
 *   <li>从 {@link RefreshScopeRegistry} 获取所有被 {@code @RefreshScope} 标记且
 *       同时带 {@code @ConfigurationProperties} 的 Bean。</li>
 *   <li>读取其 {@code prefix}，判断变更的 key 是否以该前缀开头。</li>
 *   <li>若受影响则使用 Spring Boot {@link Binder} 将最新
 *       {@link Environment} 值重新绑定到现有 Bean 实例。</li>
 * </ol>
 *
 * <p>注意：此实现<strong>不会</strong>重新创建 Bean，只是把新值绑定到已有实例，
 * 因此要求属性通过标准的 setter 或可变字段暴露。</p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.07.29
 * @since 1.0.0
 */
@Slf4j
public class RefreshScopeRefresher {

    /** Spring 的环境对象，包含最新 PropertySource */
    private final Environment environment;
    private final DynamicConfigLoader dynamicConfigLoader;

    /** 可刷新 Bean 注册表 */
    private final RefreshScopeRegistry registry;

    public RefreshScopeRefresher(Environment environment,
                                 RefreshScopeRegistry registry,
                                 DynamicConfigLoader dynamicConfigLoader) {
        this.environment = environment;
        this.registry = registry;
        this.dynamicConfigLoader = dynamicConfigLoader;
    }

    /**
     * 根据发生变化的配置 key 刷新对应的配置类。
     *
     * @param changedKeys 以点号扁平化的配置 key，如 {@code spring.datasource.url}
     */
    public void refreshByChangedKeys(Set<String> changedKeys) {
        if (changedKeys == null || changedKeys.isEmpty()) {
            return;
        }

        // 遍历所有注册的可刷新 Bean
        for (Map.Entry<String, Object> entry : registry.getAll().entrySet()) {
            Object bean = entry.getValue();
            Class<?> beanClass = bean.getClass();

            // 必须带 @ConfigurationProperties，否则跳过
            ConfigurationProperties propertiesAnno =
                beanClass.getAnnotation(ConfigurationProperties.class);
            if (propertiesAnno == null) {
                continue;
            }

            String prefix = normalizePrefix(propertiesAnno.prefix());

            // 判断是否有 key 以 prefix 开头，若无则此 Bean 不受影响
            boolean affected = changedKeys.stream()
                .anyMatch(k -> k.startsWith(prefix));
            if (!affected) {
                continue;
            }

            log.info("[Refresher] 刷新配置类 {}（前缀：{})", beanClass.getSimpleName(), prefix);

            Map<String, Object> rawConfig = dynamicConfigLoader.loadCurrentEnvironmentConfig();
            Map<String, Object> cleaned = flattenAndClean(rawConfig);
            updateEnvironmentWithLatestConfig(cleaned);

            // 重新绑定：将 Environment 中 prefix 下的属性绑定到现有 bean
            Binder.get(environment)
                .bind(prefix.substring(0, prefix.length() - 1),   // 去掉末尾的 '.'
                    Bindable.ofInstance(bean))
                .ifBound(rebound -> {
                    log.info("[{}] 刷新完成", bean.getClass());
                });

            log.debug("{}", JsonUtils.toJson(bean, true));
        }
    }

    /**
     * 使用最新配置更新环境
     *
     * @param latestConfig 最新配置
     */
    private void updateEnvironmentWithLatestConfig(Map<String, Object> latestConfig) {
        ConfigurableEnvironment configurableEnvironment =
            (ConfigurableEnvironment) this.environment;

        MutablePropertySources sources = configurableEnvironment.getPropertySources();

        // 移除旧的我们添加的临时 PropertySource（如有）
        if (sources.contains("refresh-overrides")) {
            sources.remove("refresh-overrides");
        }

        // 加入新的临时 PropertySource
        MapPropertySource override = new MapPropertySource("refresh-overrides", latestConfig);
        sources.addFirst(override); // 确保优先级最高
    }

    /**
     * 平坦而干净
     *
     * @param original 原来
     * @return 地图<字符串 ， 对象>
     */
    private Map<String, Object> flattenAndClean(Map<String, Object> original) {
        Map<String, Object> result = new LinkedHashMap<>();
        original.forEach((key, value) -> {
            if (value instanceof OriginTrackedValue) {
                result.put(key, ((OriginTrackedValue) value).getValue());
            } else if (value instanceof Map) {
                // 递归处理嵌套 Map
                @SuppressWarnings("unchecked")
                Map<String, Object> nested = flattenAndClean((Map<String, Object>) value);
                nested.forEach((k, v) -> result.put(key + "." + k, v));
            } else {
                result.put(key, value);
            }
        });
        return result;
    }

    /**
     * 规范化前缀：确保以 "xxx." 结尾，便于后续 startsWith 判断。
     */
    private String normalizePrefix(String raw) {
        if (Objects.isNull(raw) || raw.isEmpty()) {
            return "";
        }
        return raw.endsWith(".") ? raw : raw + ".";
    }
}
