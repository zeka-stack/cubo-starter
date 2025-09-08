# Cubo MyBatis Spring Boot

## 概述

`cubo-mybatis-spring-boot` 是 Cubo Starter 项目的 MyBatis 增强模块，基于 MyBatis Plus 提供了丰富的数据库操作功能和增强特性。该模块包含了 SQL
拦截器、性能监控、敏感字段加解密、元数据自动填充等高级功能，大大简化了数据库操作的复杂度。

## 主要功能

### 1. MyBatis Plus 增强

- 基于 MyBatis Plus 的 CRUD 操作增强
- 支持代码生成和自动映射
- 提供丰富的查询条件和分页功能

### 2. SQL 拦截器

- **非法 SQL 拦截**: 检测和拦截可能存在性能问题的 SQL
- **SQL 攻击拦截**: 防止恶意的全表更新和删除操作
- **分页拦截器**: 自动处理分页查询，支持多种数据库

### 3. 性能监控

- SQL 执行时间监控
- 慢查询检测和记录
- 性能统计和分析

### 4. 敏感字段加解密

- 自动加密敏感字段
- 查询时自动解密
- 支持多种加密算法

### 5. 元数据自动填充

- 自动填充创建时间、更新时间
- 支持租户 ID 和客户端 ID 自动填充
- 可扩展的元数据处理器链

## 模块结构

```
cubo-mybatis-spring-boot/
├── cubo-mybatis-spring-boot-autoconfigure/    # 自动配置模块
├── cubo-mybatis-spring-boot-core/             # 核心功能模块
└── cubo-mybatis-spring-boot-starter/          # Starter 模块
```

### 子模块说明

#### cubo-mybatis-spring-boot-autoconfigure

- **MybatisAutoConfiguration**: MyBatis 主自动配置类
- **DruidAutoConfiguration**: Druid 数据源自动配置
- **P6spyAutoConfiguration**: P6spy SQL 监控配置

#### cubo-mybatis-spring-boot-core

- **SQL 拦截器**: 各种 SQL 拦截器实现
- **性能监控**: SQL 性能监控组件
- **敏感字段处理**: 加解密拦截器
- **元数据处理器**: 自动填充处理器
- **类型处理器**: 自定义类型转换器
- **SQL 注入器**: 扩展 SQL 方法

## 核心特性

### 1. SQL 拦截器

#### 非法 SQL 拦截器

```java
@Bean
@ConditionalOnMissingBean(IllegalSQLInnerInterceptor.class)
@Profile(value = {App.ENV_NOT_PROD})
@ConditionalOnProperty(value = "zeka-stack.mybatis.enable-illegal-sql-interceptor",
                      havingValue = "true")
public IllegalSQLInnerInterceptor illegalSqlInterceptor() {
    return new IllegalSQLInnerInterceptor();
}
```

**检测规则**:

- 必须使用到索引，包含 left join 连接字段
- 防止因动态 SQL bug 导致全表更新等危险操作
- 检测在字段上使用函数、where 条件为空等危险语法
- 禁止使用 !=、not、or 关键字和子查询

#### SQL 攻击拦截器

```java
@Bean
@ConditionalOnMissingBean(BlockAttackInnerInterceptor.class)
@Profile(value = {App.ENV_NOT_PROD})
public BlockAttackInnerInterceptor sqlExplainInterceptor() {
    return new BlockAttackInnerInterceptor();
}
```

**防护功能**:

- 拦截全表 update 操作
- 拦截全表 delete 操作
- 防止无 where 条件的危险操作

#### 分页拦截器

```java
@Bean
@ConditionalOnMissingBean(PaginationInnerInterceptor.class)
public PaginationInnerInterceptor paginationInterceptor(MybatisProperties mybatisProperties) {
    PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor();
    paginationInterceptor.setMaxLimit(mybatisProperties.getSinglePageLimit());
    return paginationInterceptor;
}
```

### 2. 性能监控

#### 性能拦截器

```java
@Bean
@ConditionalOnMissingBean(PerformanceInterceptor.class)
@ConditionalOnMissingClass("com.p6spy.engine.spy.P6SpyDriver")
@Profile(value = {App.ENV_NOT_PROD})
@ConditionalOnProperty(value = "zeka-stack.mybatis.enable-log", havingValue = "true")
public PerformanceInterceptor performanceInterceptor(MybatisProperties mybatisProperties) {
    PerformanceInterceptor performanceInterceptor = new PerformanceInterceptor();
    performanceInterceptor.setFormat(mybatisProperties.isSqlFormat());
    performanceInterceptor.setMaxTime(mybatisProperties.getPerformmaxTime());
    performanceInterceptor.setMaxLength(mybatisProperties.getMaxLength());
    return performanceInterceptor;
}
```

#### SQL 执行超时处理

```java
@Bean
@ConditionalOnProperty(value = "zeka-stack.mybatis.append-sql-file", havingValue = "true")
@ConditionalOnMissingBean
public SqlExecuteTimeoutHandler sqlExecuteTimeoutHandler() {
    return new SqlExecuteTimeoutHandler();
}
```

### 3. 敏感字段加解密

#### 加密拦截器

```java
@Bean
@ConditionalOnMissingBean(SensitiveFieldEncryptIntercepter.class)
@ConditionalOnProperty(value = "zeka-stack.mybatis.enable-sensitive",
                      havingValue = "true", matchIfMissing = true)
public SensitiveFieldEncryptIntercepter sensitiveFieldEncryptIntercepter(
        MybatisProperties mybatisProperties) {
    SqlUtils.setSensitiveKey(mybatisProperties.getSensitiveKey());
    return new SensitiveFieldEncryptIntercepter(mybatisProperties.getSensitiveKey());
}
```

#### 解密拦截器

```java
@Bean
@ConditionalOnMissingBean(SensitiveFieldDecryptIntercepter.class)
@ConditionalOnProperty(value = "zeka-stack.mybatis.enable-sensitive",
                      havingValue = "true", matchIfMissing = true)
public SensitiveFieldDecryptIntercepter sensitiveFieldDecryptIntercepter(
        MybatisProperties mybatisProperties) {
    return new SensitiveFieldDecryptIntercepter(mybatisProperties.getSensitiveKey());
}
```

### 4. 元数据自动填充

#### 时间字段处理器

```java
@Bean
@ConditionalOnMissingBean(name = "timeMetaObjectHandler")
public MetaObjectChain timeMetaObjectHandler() {
    return new TimeMetaObjectHandler();
}
```

#### 租户 ID 处理器

```java
@Bean
@ConditionalOnMissingBean(name = "tenantMetaObjectHandler")
public MetaObjectChain tenantMetaObjectHandler() {
    return new TenantIdMetaObjectHandler();
}
```

#### 客户端 ID 处理器

```java
@Bean
@ConditionalOnMissingBean(name = "clientMetaObjectHandler")
public MetaObjectChain clientMetaObjectHandler() {
    return new ClientIdMetIdaObjectHandler();
}
```

## 配置属性

### MybatisProperties

| 属性名                                                 | 类型      | 默认值   | 说明              |
|-----------------------------------------------------|---------|-------|-----------------|
| `zeka-stack.mybatis.enabled`                        | boolean | true  | 是否启用 MyBatis 功能 |
| `zeka-stack.mybatis.single-page-limit`              | int     | 500   | 单页最大查询数量        |
| `zeka-stack.mybatis.enable-illegal-sql-interceptor` | boolean | true  | 是否启用非法 SQL 拦截器  |
| `zeka-stack.mybatis.enable-sql-explain-interceptor` | boolean | true  | 是否启用 SQL 攻击拦截器  |
| `zeka-stack.mybatis.enable-log`                     | boolean | true  | 是否启用 SQL 日志     |
| `zeka-stack.mybatis.sql-format`                     | boolean | true  | 是否格式化 SQL       |
| `zeka-stack.mybatis.perform-max-time`               | long    | 1000  | SQL 执行最大时间（毫秒）  |
| `zeka-stack.mybatis.max-length`                     | int     | 1000  | SQL 输出最大长度      |
| `zeka-stack.mybatis.append-sql-file`                | boolean | false | 是否追加 SQL 到文件    |
| `zeka-stack.mybatis.enable-sensitive`               | boolean | true  | 是否启用敏感字段加解密     |
| `zeka-stack.mybatis.sensitive-key`                  | String  | -     | 敏感字段加密密钥        |

## 使用方式

### 1. 引入依赖

```xml
<dependency>
    <groupId>dev.dong4j</groupId>
    <artifactId>cubo-mybatis-spring-boot-starter</artifactId>
</dependency>
```

### 2. 配置启用

```yaml
zeka-stack:
  mybatis:
    enabled: true
    single-page-limit: 500
    enable-illegal-sql-interceptor: true
    enable-sql-explain-interceptor: true
    enable-log: true
    sql-format: true
    perform-max-time: 1000
    max-length: 1000
    append-sql-file: false
    enable-sensitive: true
    sensitive-key: "your-secret-key"

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/test
    username: root
    password: password
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### 3. 实体类定义

```java
@Data
@TableName("user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_name")
    private String userName;

    @TableField("email")
    @SensitiveField  // 标记为敏感字段
    private String email;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableField(value = "tenant_id", fill = FieldFill.INSERT)
    private String tenantId;

    @TableField(value = "client_id", fill = FieldFill.INSERT)
    private String clientId;
}
```

### 4. Mapper 接口

```java
@Mapper
public interface UserMapper extends BaseMapper<User> {

    // 自定义查询方法
    @Select("SELECT * FROM user WHERE user_name = #{userName}")
    User findByUserName(@Param("userName") String userName);

    // 使用 MyBatis Plus 的扩展方法
    List<User> selectByCondition(@Param("condition") UserCondition condition);
}
```

### 5. 服务层使用

```java
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }

    public List<User> getUsersByPage(int page, int size) {
        Page<User> pageParam = new Page<>(page, size);
        return userMapper.selectPage(pageParam, null).getRecords();
    }

    public boolean saveUser(User user) {
        return userMapper.insert(user) > 0;
    }

    public boolean updateUser(User user) {
        return userMapper.updateById(user) > 0;
    }
}
```

## 高级功能

### 1. 自定义 SQL 注入器

```java
@Component
public class CustomSqlInjector extends DefaultSqlInjector {

    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass) {
        List<AbstractMethod> methodList = super.getMethodList(mapperClass);
        // 添加自定义方法
        methodList.add(new InsertIgnore());
        methodList.add(new Replace());
        return methodList;
    }
}
```

### 2. 自定义类型处理器

```java
@Component
public class CustomTypeHandler extends BaseTypeHandler<CustomType> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i,
                                  CustomType parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.getValue());
    }

    @Override
    public CustomType getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value != null ? new CustomType(value) : null;
    }

    // 其他方法实现...
}
```

### 3. 多数据源支持

```java
@Configuration
public class MultiDataSourceConfig {

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.primary")
    public DataSource primaryDataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.secondary")
    public DataSource secondaryDataSource() {
        return DruidDataSourceBuilder.create().build();
    }
}
```

## 最佳实践

### 1. SQL 优化

- 合理使用索引，避免全表扫描
- 避免在字段上使用函数
- 使用合适的查询条件，避免空条件
- 控制单页查询数量，避免大数据量查询

### 2. 性能监控

- 定期检查慢查询日志
- 设置合理的 SQL 执行超时时间
- 监控数据库连接池状态
- 优化频繁执行的 SQL 语句

### 3. 安全考虑

- 启用 SQL 攻击拦截器
- 对敏感字段进行加密存储
- 使用参数化查询，避免 SQL 注入
- 定期更新数据库密码和加密密钥

### 4. 数据一致性

- 合理使用事务
- 实现数据版本控制
- 处理并发更新冲突
- 确保元数据自动填充的准确性

## 注意事项

1. **性能影响**: SQL 拦截器会影响性能，生产环境需要谨慎使用
2. **兼容性**: 确保与数据库版本的兼容性
3. **内存使用**: 分页查询时注意内存使用情况
4. **事务管理**: 注意事务的边界和回滚机制

## 相关链接

- [MyBatis Plus 官方文档](https://baomidou.com/)
- [Druid 连接池文档](https://github.com/alibaba/druid)
- [P6spy 文档](https://p6spy.readthedocs.io/)
- [Spring Boot 数据访问](https://docs.spring.io/spring-boot/docs/current/reference/html/data.html)
