package dev.dong4j.zeka.starter.mybatis.check;

import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

/**
 * Spring 单数据源 JDBC URL 提供类
 * <p>
 * 实现 JdbcUrlProvider 接口, 用于从 Spring 数据源中获取实际的 JDBC 连接 URL.
 * 该类通过获取数据源的连接元数据, 提取真实的数据库连接地址, 并将其以键值对形式返回.
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.12.14
 * @since 2.0.0
 */
public class SpringSingleJdbcUrlProvider implements JdbcUrlProvider {

    /**
     * 获取数据源的 JDBC 连接 URL
     * <p>
     * 通过提供的 Spring 环境获取数据库连接的 URL, 并将其以 "primary" 为键存入 Map 中返回.
     * 如果获取连接过程中发生异常, 则忽略异常并返回空的 Map.
     *
     * @param environment Spring 环境, 用于获取数据源配置
     * @return 包含 "primary" 键值对的 Map, 值为实际的 JDBC URL; 若发生异常则返回空 Map
     */
    @Override
    public Map<String, String> getJdbcUrls(Environment environment) {

        Map<String, String> result = new HashMap<>();

        // 标准单数据源
        String url = environment.getProperty("spring.datasource.url");
        if (url != null) {
            result.put("primary", url);
        }

        // 常见多数据源前缀（约定优于配置）
        extractByPrefix(environment, "spring.datasource.", result);
        extractByPrefix(environment, "spring.datasources.", result);

        return result;
    }

    private void extractByPrefix(
        Environment env,
        String prefix,
        Map<String, String> result
                                ) {
        // best-effort：只扫描常见形式
        for (int i = 0; i < 10; i++) {
            String name = prefix + i + ".url";
            String url = env.getProperty(name);
            if (url != null) {
                result.put(name, url);
            }
        }
    }
}
