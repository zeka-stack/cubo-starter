package dev.dong4j.zeka.starter.mybatis.check;

import java.util.Optional;

/**
 * 数据库类型解析器接口
 * <p>
 * 用于根据提供的 JDBC URL 解析并返回对应的数据库类型 (DbType). 该接口定义了统一的方法规范, 供不同实现类根据具体的 JDBC URL 格式识别数据库类型.
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.12.14
 * @since 2.0.0
 */
public interface DbTypeResolver {

    /**
     * 根据提供的 JDBC URL 解析对应的数据库类型
     * <p>
     * 该方法尝试从 JDBC URL 中识别出数据库类型, 并返回对应的 DbType 枚举值.
     *
     * @param jdbcUrl JDBC 连接字符串
     * @return 匹配的数据库类型, 如果无法识别则返回空的 Optional
     */
    Optional<DbType> resolve(String jdbcUrl);
}
