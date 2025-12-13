package dev.dong4j.zeka.starter.mybatis.check;

import org.springframework.core.env.Environment;

import java.util.Map;

/**
 * JDBC URL 提供者接口
 * <p>
 * 定义了一个用于获取 JDBC 连接信息的接口, 实现类需提供根据数据源获取 JDBC URL 映射的方法, 通常用于动态获取数据库连接配置.
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.12.14
 * @since 2.0.0
 */
public interface JdbcUrlProvider {

    /**
     * 获取与数据源关联的 JDBC URL 映射
     * <p>
     * 从提供的 Spring 环境中提取 JDBC URL 信息, 并以键值对形式返回.
     *
     * @param environment Spring 环境, 用于获取数据源配置
     * @return 包含 JDBC URL 的映射, 键为连接名称, 值为对应的 URL
     */
    Map<String, String> getJdbcUrls(Environment environment);
}
