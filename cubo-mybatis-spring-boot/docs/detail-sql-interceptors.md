---
published: 2022.04.11
---

# SQL 拦截器

## 概述

SQL 拦截器是 `cubo-mybatis-spring-boot` 模块的核心特性之一，它通过 MyBatis 的拦截器机制实现了对 SQL 语句的拦截和处理。该模块提供了多种 SQL
拦截器，包括非法 SQL 拦截、SQL 攻击拦截、分页拦截等，有效保护数据库安全和提升 SQL 性能。

## 设计目标

### 1. 数据库安全

- **防止 SQL 攻击**：拦截全表更新、全表删除等危险操作
- **防止性能问题**：检测和拦截可能存在性能问题的 SQL
- **防止数据泄露**：通过 SQL 拦截保护数据安全

### 2. 性能优化

- **索引使用检查**：确保 SQL 使用索引，避免全表扫描
- **SQL 复杂度控制**：限制 SQL 的复杂度，提高执行效率
- **分页限制**：防止大数据量查询影响性能

### 3. 开发规范

- **SQL 规范检查**：检测不符合规范的 SQL 语句
- **危险语法检测**：禁止使用危险的 SQL 语法
- **开发环境提示**：在开发环境提供详细的 SQL 检查信息

## 核心组件

### 1. IllegalSQLInnerInterceptor（非法 SQL 拦截器）

`IllegalSQLInnerInterceptor` 用于检测和拦截可能存在性能问题的 SQL 语句。

#### 检测规则

1. **索引使用检查**
    - 必须使用到索引，包含 left join 连接字段
    - 符合索引最左原则
    - 防止因动态 SQL bug 导致全表更新等危险操作

2. **SQL 复杂度控制**
    - 推荐单表执行，减少复杂的 join 操作
    - 提高查询条件的简单性和可维护性
    - 为分库分表等扩展性需求做准备

3. **危险语法检测**
    - 在字段上使用函数
    - where 条件为空
    - 使用 `!=` 操作符
    - 使用 `not` 关键字
    - 使用 `or` 关键字
    - 使用子查询

#### 配置

```java
@Bean
@ConditionalOnMissingBean(IllegalSQLInnerInterceptor.class)
@Profile(value = {App.ENV_NOT_PROD})
@ConditionalOnProperty(
    value = "zeka-stack.mybatis.enable-illegal-sql-interceptor",
    havingValue = "true"
)
public IllegalSQLInnerInterceptor illegalSqlInterceptor() {
    return new IllegalSQLInnerInterceptor();
}
```

#### 拦截示例

**会被拦截的 SQL**：

```sql
-- 全表更新（无 where 条件）
UPDATE user SET status = 1;

-- 在字段上使用函数
SELECT * FROM user WHERE YEAR(create_time) = 2024;

-- 使用 != 操作符
SELECT * FROM user WHERE status != 1;

-- 使用 or 关键字
SELECT * FROM user WHERE id = 1 OR id = 2;
```

**不会被拦截的 SQL**：

```sql
-- 有 where 条件的更新
UPDATE user SET status = 1 WHERE id = 1;

-- 使用 = 操作符
SELECT * FROM user WHERE status = 1;

-- 使用 and 关键字
SELECT * FROM user WHERE id = 1 AND status = 1;
```

### 2. BlockAttackInnerInterceptor（SQL 攻击拦截器）

`BlockAttackInnerInterceptor` 用于防止恶意的 SQL 攻击操作。

#### 防护功能

1. **拦截全表 update 操作**
    - 检测无 where 条件的 UPDATE 语句
    - 防止误操作导致全表数据被修改

2. **拦截全表 delete 操作**
    - 检测无 where 条件的 DELETE 语句
    - 防止误操作导致全表数据被删除

3. **防止无 where 条件的危险操作**
    - 确保所有更新和删除操作都有明确的 where 条件
    - 保护数据安全

#### 配置

```java
@Bean
@ConditionalOnMissingBean(BlockAttackInnerInterceptor.class)
@Profile(value = {App.ENV_NOT_PROD})
@ConditionalOnProperty(
    value = "zeka-stack.mybatis.enable-sql-explain-interceptor",
    havingValue = "true",
    matchIfMissing = true
)
public BlockAttackInnerInterceptor sqlExplainInterceptor() {
    return new BlockAttackInnerInterceptor();
}
```

#### 拦截示例

**会被拦截的 SQL**：

```sql
-- 全表更新
UPDATE user SET status = 1;

-- 全表删除
DELETE FROM user;

-- 无 where 条件的更新
UPDATE order SET amount = 0;
```

**不会被拦截的 SQL**：

```sql
-- 有 where 条件的更新
UPDATE user SET status = 1 WHERE id = 1;

-- 有 where 条件的删除
DELETE FROM user WHERE id = 1;

-- 使用主键更新
UPDATE user SET status = 1 WHERE id IN (1, 2, 3);
```

### 3. PaginationInnerInterceptor（分页拦截器）

`PaginationInnerInterceptor` 提供自动分页查询功能，支持多种数据库。

#### 核心功能

1. **自动分页处理**
    - 自动识别分页查询
    - 自动生成 count 查询
    - 自动优化分页 SQL

2. **多数据库支持**
    - MySQL：使用 `LIMIT` 语法
    - PostgreSQL：使用 `LIMIT` 和 `OFFSET` 语法
    - Oracle：使用 `ROWNUM` 语法

3. **分页限制**
    - 可配置单页最大查询数量
    - 防止大数据量查询影响性能
    - 默认限制为 500 条

#### 配置

```java
@Bean
@ConditionalOnMissingBean(PaginationInnerInterceptor.class)
public PaginationInnerInterceptor paginationInterceptor(MybatisProperties mybatisProperties) {
    PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor();
    paginationInterceptor.setMaxLimit(mybatisProperties.getSinglePageLimit());
    return paginationInterceptor;
}
```

#### 使用示例

```java
@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 使用 MyBatis Plus 的分页功能
}

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public Page<User> getUsers(int page, int size) {
        Page<User> pageParam = new Page<>(page, size);
        return userMapper.selectPage(pageParam, null);
    }
}
```

#### 生成的 SQL

**原始 SQL**：

```sql
SELECT * FROM user WHERE status = 1
```

**分页 SQL（MySQL）**：

```sql
-- 数据查询
SELECT * FROM user WHERE status = 1 LIMIT 10 OFFSET 0

-- Count 查询
SELECT COUNT(*) FROM user WHERE status = 1
```

## 拦截器链

### 1. 拦截器执行顺序

所有拦截器通过 `MybatisPlusInterceptor` 统一管理，按添加顺序执行：

```java
@Bean
public MybatisPlusInterceptor mybatisPlusInterceptor(List<InnerInterceptor> interceptors) {
    MybatisPlusInterceptor plusInterceptor = new MybatisPlusInterceptor();
    interceptors.forEach(plusInterceptor::addInnerInterceptor);
    return plusInterceptor;
}
```

### 2. 执行流程

```
SQL 执行请求
    ↓
MybatisPlusInterceptor 拦截
    ↓
分页拦截器处理
    ↓
SQL 攻击拦截器检查
    ↓
非法 SQL 拦截器检查
    ↓
性能监控拦截器记录
    ↓
执行 SQL
    ↓
返回结果
```

## 配置说明

### 1. 基本配置

```yaml
zeka-stack:
  mybatis:
    enabled: true
    # 非法 SQL 拦截器
    enable-illegal-sql-interceptor: true
    # SQL 攻击拦截器
    enable-sql-explain-interceptor: true
    # 单页最大查询数量
    single-page-limit: 500
```

### 2. 环境配置

**开发环境**：

- 默认启用所有拦截器
- 提供详细的错误信息
- 便于发现和修复问题

**生产环境**：

- 建议关闭非法 SQL 拦截器（性能考虑）
- 保留 SQL 攻击拦截器（安全考虑）
- 保留分页拦截器（性能考虑）

## 使用示例

### 1. 基本使用

```java
@Mapper
public interface UserMapper extends BaseMapper<User> {

    // 使用 MyBatis Plus 的自动分页
    // 拦截器会自动处理分页逻辑
}

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public Page<User> getUsers(int page, int size) {
        Page<User> pageParam = new Page<>(page, size);
        // 分页拦截器会自动处理
        return userMapper.selectPage(pageParam, null);
    }

    public void updateUser(Long id, User user) {
        // SQL 攻击拦截器会检查是否有 where 条件
        userMapper.updateById(user);
    }
}
```

### 2. 自定义拦截器

```java
@Component
public class CustomSqlInterceptor implements InnerInterceptor {

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms,
                           Object parameter, RowBounds rowBounds,
                           ResultHandler resultHandler, BoundSql boundSql) {
        // 自定义 SQL 拦截逻辑
        String sql = boundSql.getSql();
        if (sql.contains("危险关键字")) {
            throw new RuntimeException("检测到危险的 SQL 语句");
        }
    }
}
```

## 设计优势

### 1. 安全性

- **多层防护**：通过多个拦截器提供多层安全防护
- **早期检测**：在 SQL 执行前进行检测，避免数据损坏
- **详细日志**：记录被拦截的 SQL，便于问题排查

### 2. 性能

- **索引检查**：确保 SQL 使用索引，提高查询性能
- **分页限制**：防止大数据量查询影响性能
- **SQL 优化**：自动优化分页 SQL，提高执行效率

### 3. 开发体验

- **自动处理**：分页等功能自动处理，无需手动编写 SQL
- **错误提示**：提供详细的错误信息，便于问题定位
- **灵活配置**：支持通过配置启用/禁用功能

## 注意事项

### 1. 性能考虑

- 拦截器会增加一定的性能开销
- 生产环境建议关闭部分拦截器
- 合理配置拦截器，避免过度拦截

### 2. 兼容性

- 与 MyBatis Plus 完全兼容
- 支持所有标准的 MyBatis Plus 功能
- 不影响正常的业务逻辑

### 3. 安全性

- SQL 拦截是安全的重要防线，但不是唯一防线
- 还需要配合其他安全措施（如参数验证、权限控制等）
- 定期审查被拦截的 SQL，发现潜在问题

## 最佳实践

### 1. 合理使用拦截器

- **开发环境**：启用所有拦截器，及早发现问题
- **生产环境**：只启用必要的拦截器，平衡安全和性能
- **测试环境**：启用所有拦截器，确保代码质量

### 2. 配置优化

- 根据实际需求配置拦截器
- 合理设置分页限制
- 定期审查拦截器配置

### 3. 监控和日志

- 记录被拦截的 SQL
- 监控拦截统计信息
- 定期分析拦截日志

## 总结

SQL 拦截器通过 MyBatis 的拦截器机制实现了对 SQL 语句的拦截和处理，有效保护了数据库安全和提升了 SQL
性能。这种设计不仅提供了强大的安全防护能力，还保持了良好的性能和易用性，是构建高质量数据访问层的重要基础。

