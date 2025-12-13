package dev.dong4j.zeka.starter.mybatis.check;

/**
 * 数据库类型枚举
 * <p>
 * 该枚举定义了支持的数据库类型, 包括每种数据库的名称,JDBC 驱动类以及对应的 Maven 依赖配置片段, 用于在项目中集成相应的数据库驱动.
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.12.14
 * @since 2.0.0
 */
public enum DbType {

    /**
     * MYSQL 数据库类型配置
     * <p>
     * 包含数据库类型名称,JDBC 驱动类名以及对应的 Maven 依赖配置
     */
    MYSQL(
        "MySQL",
        "com.mysql.cj.jdbc.Driver",
        """
            <dependency>
                <groupId>com.mysql</groupId>
                <artifactId>mysql-connector-j</artifactId>
                <scope>runtime</scope>
            </dependency>
            """
    ),

    /**
     * PostgreSQL 数据库类型配置信息
     * <p>
     * 包含数据库类型名称, 驱动类名以及对应的 Maven 依赖配置
     */
    POSTGRESQL(
        "PostgreSQL",
        "org.postgresql.Driver",
        """
            <dependency>
                <groupId>org.postgresql</groupId>
                <artifactId>postgresql</artifactId>
                <scope>runtime</scope>
            </dependency>
            """
    );

    /** 用户名称 */
    private final String name;
    /** 数据库驱动类名称 */
    private final String driverClass;
    /** Maven 构建代码片段 */
    private final String mavenSnippet;

    /**
     * 构造一个新的 DbType 实例
     * <p>
     * 用于初始化数据库类型对象, 包含数据库名称, 驱动类和 Maven 依赖片段
     *
     * @param name         数据库类型名称
     * @param driverClass  数据库驱动类的全限定名
     * @param mavenSnippet 用于 Maven 项目中的依赖配置片段
     */
    DbType(String name, String driverClass, String mavenSnippet) {
        this.name = name;
        this.driverClass = driverClass;
        this.mavenSnippet = mavenSnippet;
    }

    /**
     * 获取名称
     * <p>
     * 返回当前对象的名称属性值
     *
     * @return 名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取驱动类名称
     * <p>
     * 返回当前配置的驱动类名称
     *
     * @return 驱动类名称
     */
    public String getDriverClass() {
        return driverClass;
    }

    /**
     * 获取 Maven 代码片段
     * <p>
     * 返回当前存储的 Maven 代码片段内容
     *
     * @return Maven 代码片段字符串
     */
    public String getMavenSnippet() {
        return mavenSnippet;
    }
}
