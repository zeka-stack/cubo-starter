package dev.dong4j.zeka.starter.mybatis.check;

import com.google.common.collect.Maps;

import org.springframework.core.env.Environment;

import java.util.Map;

/**
 * ShardingSphere JDBC URL 提供者
 * <p>
 * 用于根据数据源生成对应的 JDBC URL, 主要针对 ShardingSphere 数据源进行处理.
 * 当传入的 DataSource 不是 ShardingSphereDataSource 类型时, 返回空的 URL 映射.
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.12.14
 * @since 2.0.0
 */
@SuppressWarnings("PMD.UndefineMagicConstantRule")
public class ShardingSphereJdbcUrlProvider implements JdbcUrlProvider {

    /**
     * 获取与数据源关联的 JDBC URL 映射
     * <p>
     * 如果数据源不是 ShardingSphereDataSource 类型, 则返回空映射. 否则, 返回与数据源关联的 JDBC URL 映射.
     *
     * @param environment Spring 环境, 用于获取数据源配置
     * @return 包含 JDBC URL 的映射, 键为数据源名称, 值为对应的 JDBC URL
     */
    @Override
    public Map<String, String> getJdbcUrls(Environment environment) {
        Map<String, String> result = Maps.newHashMapWithExpectedSize(2);

        String url = environment.getProperty("spring.datasource.url");
        if (url == null || !url.startsWith("jdbc:shardingsphere:")) {
            return result;
        }

        // 只能给出“提示级”告警
        result.put("shardingsphere", "jdbc:shardingsphere:(real datasource defined in config file)");

        return result;
    }

}
