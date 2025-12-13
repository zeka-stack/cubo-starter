package dev.dong4j.zeka.starter.mybatis.check;

import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

/**
 * 提供 P6Spy 数据源的 JDBC URL 信息
 * <p>
 * 该类实现了 JdbcUrlProvider 接口, 用于从 P6Spy 数据源中获取真实的 JDBC URL, 并将其以键值对形式返回.
 * 主要用于在使用 P6Spy 代理数据源时, 获取底层实际连接的数据库 URL.
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.12.14
 * @since 2.0.0
 */
public class P6SpyJdbcUrlProvider implements JdbcUrlProvider {

    /**
     * 获取与给定数据源关联的 JDBC URL
     * <p>
     * 如果数据源是 P6DataSource 类型, 则获取其实际连接的 JDBC URL 并返回, 否则返回空 Map.
     *
     * @param environment Spring 环境, 用于获取数据源配置
     * @return 包含 JDBC URL 的 Map, 键为 "p6spy", 值为实际 URL; 如果数据源不是 P6DataSource 或获取失败, 则返回空 Map
     */
    @Override
    public Map<String, String> getJdbcUrls(Environment environment) {
        Map<String, String> result = new HashMap<>();

        String url = environment.getProperty("spring.datasource.url");
        if (url != null && url.startsWith("jdbc:p6spy:")) {
            result.put(
                "p6spy",
                url.replaceFirst("jdbc:p6spy:", "jdbc:")
                      );
        }

        return result;
    }
}
