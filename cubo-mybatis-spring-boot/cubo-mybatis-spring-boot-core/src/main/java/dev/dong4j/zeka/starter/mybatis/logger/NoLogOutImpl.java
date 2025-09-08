package dev.dong4j.zeka.starter.mybatis.logger;

import dev.dong4j.zeka.starter.mybatis.plugins.PerformanceInterceptor;
import dev.dong4j.zeka.starter.mybatis.spi.MybatisLauncherInitiation;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;

/**
 * 无日志输出实现类
 *
 * 该类继承自 MyBatis 的 Slf4jImpl，用于禁用 MyBatis 的原生 SQL 日志输出。
 * 主要目的是减少冗余的日志输出，使用自定义的性能拦截器代替。
 *
 * 设计背景：
 * 当将 mapper 目录的日志等级设置为 debug/trace 时，MyBatis 会输出详细的
 * SQL 日志，但一个 SQL 语句会产生多行日志输出，造成日志冗余。
 *
 * 主要功能：
 * 1. 禁用 MyBatis 的 debug 和 trace 日志输出
 * 2. 禁用错误日志的输出（避免重复记录）
 * 3. 保持日志接口的兼容性
 * 4. 配合 PerformanceInterceptor 提供更好的 SQL 监控
 *
 * 使用方式：
 * 通过 MybatisLauncherInitiation 自动配置：
 * ```
 * mybatis-plus.configuration.log-impl=dev.dong4j.zeka.starter.mybatis.logger.NoLogOutImpl
 * ```
 *
 * 恢复原生日志：
 * 如果需要输出 MyBatis 的原生 SQL 日志，可以覆盖配置：
 * ```
 * mybatis-plus.configuration.log-impl=org.apache.ibatis.logging.slf4j.Slf4jImpl
 * ```
 *
 * 替代方案：
 * - 使用 PerformanceInterceptor 进行 SQL 监控
 * - 使用 P6spy 进行更详细的 SQL 分析
 * - 根据需要选择合适的日志输出方式
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.04.29 15:18
 * @see PerformanceInterceptor
 * @see MybatisLauncherInitiation
 * @since 1.0.0
 */
public class NoLogOutImpl extends Slf4jImpl {
    /**
     * Mybatis plus out
     *
     * @param clazz clazz
     * @since 1.0.0
     */
    public NoLogOutImpl(String clazz) {
        super(clazz);
    }

    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    @Override
    public boolean isTraceEnabled() {
        return false;
    }

    @Override
    public void error(String s, Throwable e) {
        // Do Nothing
    }

    @Override
    public void error(String s) {
        // Do Nothing
    }

    @Override
    public void debug(String s) {
        // Do Nothing
    }

    @Override
    public void trace(String s) {
        // Do Nothing
    }

    @Override
    public void warn(String s) {
        // Do Nothing
    }

}
