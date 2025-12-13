package dev.dong4j.zeka.starter.mybatis.check;

import lombok.Getter;

/**
 * 数据库驱动元数据枚举
 * <p>
 * 该枚举用于表示不同数据库系统的 JDBC 驱动名称及其对应的驱动类全限定名, 便于在程序中根据数据库类型加载相应的 JDBC 驱动.
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.12.14
 * @since 2.0.0
 */
@Getter
public enum JdbcDriverMeta {

    /**
     * MySQL 数据库类型常量
     * <p>
     * 用于标识数据库类型为 MySQL, 并指定对应的 JDBC 驱动类名
     */
    MYSQL("MySQL", "com.mysql.cj.jdbc.Driver"),
    /** PostgreSQL 数据库类型及其对应的 JDBC 驱动类名 */
    POSTGRESQL("PostgreSQL", "org.postgresql.Driver"),
    /**
     * Oracle 数据库类型常量
     * <p>
     * 用于标识 Oracle 数据库, 并指定其 JDBC 驱动类名
     */
    ORACLE("Oracle", "oracle.jdbc.OracleDriver"),
    /** SQL Server 数据库类型及其对应的 JDBC 驱动类名 */
    SQLSERVER("SQL Server", "com.microsoft.sqlserver.jdbc.SQLServerDriver");

    /** 数据库名称 */
    private final String name;
    /** 数据库驱动类名称 */
    private final String driverClass;

    /**
     * 构造一个新的 JdbcDriverMeta 实例
     * <p>
     * 用于存储 JDBC 驱动程序的名称和对应的驱动类名
     *
     * @param name        驱动程序的名称
     * @param driverClass 对应的 JDBC 驱动类的全限定类名
     */
    JdbcDriverMeta(String name, String driverClass) {
        this.name = name;
        this.driverClass = driverClass;
    }

}
