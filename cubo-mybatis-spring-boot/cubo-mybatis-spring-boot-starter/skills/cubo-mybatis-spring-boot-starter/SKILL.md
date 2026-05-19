---
name: cubo-mybatis-spring-boot-starter
description: 在使用 Zeka Stack Cubo MyBatis Starter 进行数据库访问时使用。
license: Apache-2.0
---

# Cubo MyBatis Spring Boot Starter

## 适用场景

项目需要进行数据库访问时使用该 Starter。它基于 MyBatis Plus 提供 CRUD 操作增强、SQL 拦截、敏感字段加解密、元数据自动填充、分页查询等能力。

## 组件定位

该 Starter 是使用方直接依赖的入口，屏蔽了以下底层模块的复杂性：

- `cubo-mybatis-spring-boot-autoconfigure`：自动配置类、配置属性类
- `cubo-mybatis-spring-boot-core`：SQL 拦截器、元数据处理器、敏感字段处理、性能监控、类型处理器
- MyBatis Plus、Druid 连接池

## 依赖方式

```xml
<dependency>
    <groupId>dev.dong4j</groupId>
    <artifactId>cubo-mybatis-spring-boot-starter</artifactId>
</dependency>
```

该 Starter 已包含 MyBatis Plus 和 Druid，无需额外引入。

## 编码规则

### 实体类定义

使用 `@TableName` 标记表名，`@TableId` 标记主键，`@TableField` 标记字段映射，`@SensitiveField` 标记敏感字段：

```java
@Data
@TableName("user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_name")
    private String userName;

    @TableField("email")
    @SensitiveField  // 写入时加密，查询时自动解密
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

### Mapper 接口

继承 `BaseMapper<User>` 获取基础 CRUD 能力：

```java
@Mapper
public interface UserMapper extends BaseMapper<User> {

    // 自定义查询使用 @Select 注解
    @Select("SELECT * FROM user WHERE user_name = #{userName}")
    User findByUserName(@Param("userName") String userName);
}
```

### 服务层使用

使用 MyBatis Plus 提供的分页和查询方法：

```java
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }

    public Page<User> getUsersByPage(int page, int size) {
        Page<User> pageParam = new Page<>(page, size);
        return userMapper.selectPage(pageParam, null);
    }

    public boolean saveUser(User user) {
        return userMapper.insert(user) > 0;
    }
}
```

## 配置规则

### 基础配置

```yaml
zeka-stack:
  mybatis:
    enabled: true
    single-page-limit: 500
    enable-log: true
    sql-format: true
    perform-max-time: 1000
    max-length: 1000
    enable-sensitive: true
    sensitive-key: "your-secret-key"
    enable-illegal-sql-interceptor: true
    enable-sql-explain-interceptor: true
```

### 配置属性说明

| 属性名                                                 | 类型      | 默认值                                | 说明                           |
|-----------------------------------------------------|---------|------------------------------------|------------------------------|
| `zeka-stack.mybatis.enabled`                        | boolean | true                               | 是否启用 MyBatis 功能              |
| `zeka-stack.mybatis.single-page-limit`              | long    | -1                                 | 单页最大查询数量，-1 表示不限制            |
| `zeka-stack.mybatis.enable-log`                     | boolean | false                              | 是否启用 SQL 日志                  |
| `zeka-stack.mybatis.sql-format`                     | boolean | false                              | 是否格式化 SQL 输出                 |
| `zeka-stack.mybatis.perform-max-time`               | long    | 1000                               | SQL 执行最大时间（毫秒），超过此时间记录日志     |
| `zeka-stack.mybatis.max-length`                     | int     | 1000                               | SQL 输出最大长度                   |
| `zeka-stack.mybatis.enable-sensitive`               | boolean | true                               | 是否启用敏感字段加解密                  |
| `zeka-stack.mybatis.sensitive-key`                  | String  | `rFsHHirtsGuST7HtBzebLge1uVYCg2ZS` | 敏感字段加密密钥（Base64 编码的 16 字符密钥） |
| `zeka-stack.mybatis.enable-illegal-sql-interceptor` | boolean | false                              | 是否启用非法 SQL 拦截器（检测危险语法）       |
| `zeka-stack.mybatis.enable-sql-explain-interceptor` | boolean | false                              | 是否启用 SQL 攻击拦截器（防止全表操作）       |

### 自动配置的拦截器

该 Starter 自动配置以下拦截器（可通过覆盖 Bean 禁用或替换）：

| Bean 类型                            | 条件                                            | 说明                               |
|------------------------------------|-----------------------------------------------|----------------------------------|
| `IllegalSQLInnerInterceptor`       | 非生产环境 + `enable-illegal-sql-interceptor=true` | 拦截危险 SQL 语法（!=、not、or、子查询等）      |
| `BlockAttackInnerInterceptor`      | 非生产环境 + `enable-sql-explain-interceptor=true` | 拦截全表 update/delete 操作            |
| `PaginationInnerInterceptor`       | 始终                                            | 分页查询支持，设置 `single-page-limit` 限制 |
| `PerformanceInterceptor`           | 非生产环境 + 未使用 P6spy                             | SQL 执行时间和格式化日志                   |
| `SensitiveFieldEncryptIntercepter` | `enable-sensitive=true`                       | 写入时加密敏感字段                        |
| `SensitiveFieldDecryptIntercepter` | `enable-sensitive=true`                       | 查询时解密敏感字段                        |
| `MetaHandlerChain`                 | 始终                                            | 元数据自动填充处理器链                      |

### 元数据自动填充

以下字段在插入/更新时自动填充：

| 处理器                         | 填充字段          | 填充时机          | 来源       |
|-----------------------------|---------------|---------------|----------|
| `TimeMetaObjectHandler`     | `create_time` | INSERT        | 当前系统时间   |
| `TimeMetaObjectHandler`     | `update_time` | INSERT_UPDATE | 当前系统时间   |
| `TenantIdMetaObjectHandler` | `tenant_id`   | INSERT        | 当前租户上下文  |
| `ClientIdMetaObjectHandler` | `client_id`   | INSERT        | 当前客户端上下文 |

### 覆盖默认行为

如需禁用或自定义某个拦截器，定义同类型的 Bean 即可覆盖：

```java
@Configuration
public class MyMybatisConfig {

    @Bean
    public IllegalSQLInnerInterceptor illegalSqlInterceptor() {
        // 自定义配置或返回 null 禁用
        return new IllegalSQLInnerInterceptor();
    }
}
```

## 不要这样做

- **禁用生产环境安全拦截器**：生产环境不应禁用 `enable-sql-explain-interceptor`，全表操作拦截是数据安全底线
- **硬编码 sensitive-key**：使用配置中心或环境变量管理加密密钥，不要明文写在配置文件中
- **在生产环境启用性能日志**：`enable-log` 和 `sql-format` 会在日志中输出完整 SQL，影响性能和日志存储
- **使用危险 SQL 语法**：!=、not、or、子查询等语法会被 `IllegalSQLInnerInterceptor` 拦截
- **省略 where 条件的更新/删除**：会被 `BlockAttackInnerInterceptor` 拦截，防止意外全表操作
- **绕过 Starter 的自动配置**：不要重复定义 Starter 已配置的 Bean，如需定制则覆盖对应 Bean
