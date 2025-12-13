package dev.dong4j.zeka.starter.mybatis.autoconfigure;

import com.p6spy.engine.spy.P6DataSource;

import org.apache.shardingsphere.driver.jdbc.core.datasource.ShardingSphereDataSource;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

import dev.dong4j.zeka.kernel.common.constant.ConfigDefaultValue;
import dev.dong4j.zeka.kernel.common.constant.ConfigKey;
import dev.dong4j.zeka.kernel.common.start.ZekaAutoConfiguration;
import dev.dong4j.zeka.starter.mybatis.check.JdbcDriverChecker;
import dev.dong4j.zeka.starter.mybatis.check.JdbcUrlProvider;
import dev.dong4j.zeka.starter.mybatis.check.P6SpyJdbcUrlProvider;
import dev.dong4j.zeka.starter.mybatis.check.ShardingSphereJdbcUrlProvider;
import dev.dong4j.zeka.starter.mybatis.check.SpringSingleJdbcUrlProvider;

/**
 * JDBC 检查自动配置类
 * <p>
 * 提供 JDBC 驱动和连接 URL 的检查功能, 确保数据源配置正确可用. 该类通过自动配置机制在应用启动时检查 JDBC 驱动是否可用, 并根据不同的数据源类型 (如 ShardingSphere,P6Spy 等) 提供对应的 URL 提供器.
 * 支持在应用启动完成后自动执行检查逻辑, 确保数据库连接配置的正确性.
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.12.14
 * @since 2.0.0
 */
@AutoConfiguration(
    after = {
        DataSourceAutoConfiguration.class
    }
)
@ConditionalOnClass(javax.sql.DataSource.class)
@ConditionalOnProperty(value = ConfigKey.MYBATIS_JDBC_CHECK_ENABLED,
                       havingValue = ConfigDefaultValue.TRUE_STRING,
                       matchIfMissing = true)
@EnableConfigurationProperties( {DataSourceProperties.class})
public class JdbcCheckAutoConfiguration implements ZekaAutoConfiguration {
    /** 用于检查 JDBC 驱动程序的工具 */
    private final JdbcDriverChecker checker;
    /**
     * 提供 JDBC URL 的服务
     * <p>
     * 用于动态获取或生成数据库连接 URL
     *
     * @see JdbcUrlProvider
     */
    private final JdbcUrlProvider urlProvider;
    /**
     * 应用程序的环境配置
     * <p>
     * 用于获取和管理应用程序运行时的环境变量和配置信息
     */
    private final Environment environment;

    /**
     * 构造一个 JdbcCheckAutoConfiguration 实例
     * <p>
     * 用于初始化 JDBC 检查自动配置, 依赖于 JDBC 驱动检查器,URL 提供器和环境对象
     *
     * @param checker     JDBC 驱动检查器, 用于验证 JDBC 驱动
     * @param urlProvider JDBC URL 提供器, 用于获取数据库连接 URL
     * @param environment 环境对象, 用于获取配置信息
     */
    public JdbcCheckAutoConfiguration(JdbcDriverChecker checker, JdbcUrlProvider urlProvider, Environment environment) {
        this.checker = checker;
        this.urlProvider = urlProvider;
        this.environment = environment;
    }

    /**
     * JDBC 检查核心自动配置类
     * <p>
     * 提供 JDBC 驱动检查和 JDBC URL 提供器的自动配置功能, 用于在 Spring 应用启动时自动注册相关 Bean, 确保 JDBC 连接的正确性和可用性.
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2025.12.14
     * @since 2.0.0
     */
    @AutoConfiguration
    static class JdbcCheckCoreAutoConfiguration {

        /**
         * 创建并返回一个 JdbcDriverChecker 实例
         * <p>
         * 该方法用于初始化一个 JdbcDriverChecker 对象, 通常用于检查 JDBC 驱动是否可用.
         *
         * @return JdbcDriverChecker 实例
         */
        @Bean
        public JdbcDriverChecker jdbcDriverChecker() {
            return new JdbcDriverChecker();
        }

        /**
         * 创建并返回一个 JdbcUrlProvider 实例
         * <p>
         * 仅当上下文中未存在 JdbcUrlProvider 类型的 Bean 时, 才会创建该 Bean.
         * 默认使用 SpringSingleJdbcUrlProvider 作为实现类.
         *
         * @return JdbcUrlProvider 实例
         */
        @Bean
        @ConditionalOnMissingBean(JdbcUrlProvider.class)
        public JdbcUrlProvider jdbcUrlProvider() {
            return new SpringSingleJdbcUrlProvider();
        }
    }

    /**
     * 分片 Sphere 适配器支持类
     * <p>
     * 提供对 ShardingSphere 数据源的适配支持, 用于在 Spring Boot 自动配置过程中注册相关的 Bean, 如 JdbcUrlProvider, 以确保 ShardingSphere 能够正确集成到应用中.
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2025.12.14
     * @since 2.0.0
     */
    @AutoConfiguration(
        after = {
            DataSourceAutoConfiguration.class
        }
    )
    @ConditionalOnClass(ShardingSphereDataSource.class)
    static class ShardingSphereAdapterSupport {
        /**
         * 创建并返回 ShardingSphereJdbcUrlProvider 的 Bean 实例
         * <p>
         * 该方法用于在 Spring 应用上下文中注册一个 ShardingSphereJdbcUrlProvider 的 Bean, 用于提供分片 JDBC URL
         *
         * @return ShardingSphereJdbcUrlProvider 的实例
         */
        @Bean
        public JdbcUrlProvider shardingSphereJdbcUrlProvider() {
            return new ShardingSphereJdbcUrlProvider();
        }
    }

    /**
     * P6Spy 适配器支持类
     * <p>
     * 用于在 Spring 自动配置中提供对 P6Spy 数据源的适配支持, 确保在使用 P6Spy 时能够正确初始化 JDBC URL 提供器
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2025.12.14
     * @since 2.0.0
     */
    @AutoConfiguration(
        after = {
            DataSourceAutoConfiguration.class
        }
    )
    @ConditionalOnClass(P6DataSource.class)
    static class P6SpyAdapterSupport {
        /**
         * 创建并返回一个 P6SpyJdbcUrlProvider 实例
         * <p>
         * 该方法用于生成一个用于 P6Spy 的 JDBC URL 提供程序, 通常用于拦截和监控 JDBC 操作.
         *
         * @return 返回一个配置好的 P6SpyJdbcUrlProvider 实例
         */
        @Bean
        public JdbcUrlProvider p6SpyJdbcUrlProvider() {
            return new P6SpyJdbcUrlProvider();
        }
    }

    /**
     * 在应用启动完成后执行检查操作
     * <p>
     * 当应用程序准备好后, 触发检查逻辑, 使用提供的 URL 提供器和环境信息进行检查.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        checker.check(urlProvider, environment);
    }

}
