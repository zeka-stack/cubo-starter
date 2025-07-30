package dev.dong4j.zeka.starter.launcher.refresh;

import dev.dong4j.zeka.kernel.common.yml.YmlPropertyLoaderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

/**
 * 3: 配置加载器
 * 1. 加载主配置文件 application.yml
 * 2. 加载当前激活 Profile 的配置文件（如 application-prod.yml）
 * 3. 返回扁平化配置 Map，格式形如：
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.07.29
 * @since 1.0.0
 */
@Slf4j
public class DynamicConfigLoader {

    private static final String BASE_CONFIG = "application.yml";
    private static final String PROFILE_CONFIG_FORMAT = "application-%s.yml";

    private final Environment environment;

    public DynamicConfigLoader(Environment environment) {
        this.environment = environment;
    }

    /**
     * 读取当前环境对应的所有配置（主配置 + profile）
     */
    public Map<String, Object> loadCurrentEnvironmentConfig() {
        // 1. 读取 application.yml
        Map<String, Object> base = load(BASE_CONFIG);
        Map<String, Object> merged = new LinkedHashMap<>(flattenMap(base));

        // 2. 获取当前激活的 profile（如 dev、prod）
        String[] profiles = environment.getActiveProfiles();
        for (String profile : profiles) {
            String profilePath = String.format(PROFILE_CONFIG_FORMAT, profile);
            Map<String, Object> profileData = load(profilePath);
            merged.putAll(flattenMap(profileData));
        }

        return merged;
    }

    /**
     * 加载指定 YAML 文件
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> loadYaml(String path) {
        try (InputStream input = Files.newInputStream(Paths.get(path).toFile().toPath())) {
            Yaml yaml = new Yaml(new SafeConstructor());
            Object data = yaml.load(input);
            if (data instanceof Map) {
                return (Map<String, Object>) data;
            }
        } catch (Exception e) {
            // 文件不存在或格式异常不阻塞
        }
        return Collections.emptyMap();
    }

    /**
     * 将嵌套 map 扁平化，生成 "a.b.c" 形式的键
     */
    private Map<String, Object> flattenMap(Map<String, Object> source) {
        Map<String, Object> result = new LinkedHashMap<>();
        doFlatten("", source, result);
        return result;
    }

    @SuppressWarnings("unchecked")
    private void doFlatten(String prefix, Map<String, Object> source, Map<String, Object> target) {
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Map<?, ?>) {
                doFlatten(key, (Map<String, Object>) value, target);
            } else {
                target.put(key, value);
            }
        }
    }

    private static Map<String, Object> load(String configName) {
        YmlPropertyLoaderFactory ymlPropertyLoaderFactory = new YmlPropertyLoaderFactory();
        // 从 classpath 加载资源文件
        Resource resource = new ClassPathResource(configName);
        try {
            final PropertySource<?> propertySource = ymlPropertyLoaderFactory.createPropertySource(
                "DynamicProperties", new EncodedResource(resource, StandardCharsets.UTF_8)
            );
            // propertySource 转 Map<String, Object>
            Map<String, Object> result = new LinkedHashMap<>();
            if (propertySource.getSource() instanceof Map) {
                // 如果 source 本身就是 Map，则直接转换
                Map<?, ?> sourceMap = (Map<?, ?>) propertySource.getSource();
                for (Map.Entry<?, ?> entry : sourceMap.entrySet()) {
                    if (entry.getKey() instanceof String) {
                        result.put((String) entry.getKey(), entry.getValue());
                    }
                }
            }
            return result;

        } catch (IOException e) {
            log.error("[{}] 解析失败: {}", configName, e.getMessage());
            return Collections.emptyMap();
        }
    }

}
