package dev.dong4j.zeka.starter.mybatis.check;

import java.util.Optional;

/**
 * 默认数据库类型解析器
 * <p>
 * 用于根据 JDBC URL 解析对应的数据库类型, 支持 MySQL 和 PostgreSQL 数据库的识别.
 * 若无法识别数据库类型, 则返回空的 Optional.
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.12.14
 * @since 2.0.0
 */
public class DefaultDbTypeResolver implements DbTypeResolver {

    /**
     * 根据 JDBC URL 解析对应的数据库类型
     * <p>
     * 通过检查 JDBC URL 的前缀来确定数据库类型, 若 URL 为 null 或无法识别则返回空的 Optional
     *
     * @param jdbcUrl JDBC 连接字符串
     * @return 包含数据库类型的 Optional 对象, 若无法识别则返回空
     */
    @Override
    public Optional<DbType> resolve(String jdbcUrl) {
        if (jdbcUrl == null) {
            return Optional.empty();
        }

        if (jdbcUrl.startsWith("jdbc:mysql:")) {
            return Optional.of(DbType.MYSQL);
        }

        if (jdbcUrl.startsWith("jdbc:postgresql:")) {
            return Optional.of(DbType.POSTGRESQL);
        }

        return Optional.empty();
    }
}
