---
published: 2022.04.11
---

# SQL 性能监控

## 概述

SQL 性能监控是 `cubo-mybatis-spring-boot` 模块的重要特性之一，它通过 `PerformanceInterceptor` 和 `SqlExecuteTimeoutHandler` 实现了对 SQL
执行性能的监控和分析。该模块能够记录 SQL 执行时间、检测慢查询、发布超时事件等，帮助开发者快速识别和优化性能问题。

## 设计目标

### 1. 性能监控

- **执行时间记录**：精确记录每条 SQL 的执行时间
- **慢查询检测**：自动检测超过阈值的慢查询
- **性能统计**：提供 SQL 性能统计分析

### 2. 问题定位

- **详细日志**：记录 SQL 语句、执行时间、参数等信息
- **超时事件**：发布 SQL 执行超时事件，支持异步处理
- **问题追踪**：通过 TraceId 关联请求和 SQL 执行

### 3. 开发辅助

- **SQL 格式化**：可配置的 SQL 格式化输出
- **参数显示**：显示 SQL 参数值，便于调试
- **执行统计**：提供 SQL 执行统计信息

## 核心组件

### 1. PerformanceInterceptor（性能拦截器）

`PerformanceInterceptor` 是 SQL 性能监控的核心实现，它拦截 SQL 执行，记录执行时间并检测慢查询。

#### 工作原理

```java
@Intercepts({
    @Signature(type = StatementHandler.class, method = "query",
               args = {Statement.class, ResultHandler.class}),
    @Signature(type = StatementHandler.class, method = "update",
               args = {Statement.class}),
    @Signature(type = StatementHandler.class, method = "batch",
               args = {Statement.class})
})
public class PerformanceInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        long startTime = System.currentTimeMillis();

        try {
            // 执行 SQL
            Object result = invocation.proceed();

            // 计算执行时间
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            // 记录 SQL 执行信息
            logSql(duration, sql);

            // 检测慢查询
            if (duration > maxTime) {
                handleSlowQuery(duration, sql);
            }

            return result;
        } catch (Throwable e) {
            // 记录异常信息
            logError(e, sql);
            throw e;
        }
    }
}
```

#### 核心功能

1. **执行时间记录**
    - 精确到毫秒的执行时间记录
    - 支持查询、更新、批量操作
    - 自动计算执行耗时

2. **SQL 格式化**
    - 可配置的 SQL 格式化输出
    - 美化 SQL 语句，便于阅读
    - 支持参数替换显示

3. **慢查询检测**
    - 可配置的执行时间阈值
    - 自动检测超过阈值的 SQL
    - 发布超时事件支持异步处理

4. **日志输出**
    - 可配置的日志级别
    - 限制 SQL 输出长度，避免日志过长
    - 支持输出到文件

#### 配置

```java
@Bean
@ConditionalOnMissingBean(PerformanceInterceptor.class)
@ConditionalOnMissingClass("com.p6spy.engine.spy.P6SpyDriver")
@Profile(value = {App.ENV_NOT_PROD})
@ConditionalOnProperty(
    value = "zeka-stack.mybatis.enable-log",
    havingValue = "true"
)
public PerformanceInterceptor performanceInterceptor(MybatisProperties mybatisProperties) {
    PerformanceInterceptor interceptor = new PerformanceInterceptor();
    interceptor.setFormat(mybatisProperties.isSqlFormat());
    interceptor.setMaxTime(mybatisProperties.getPerformmaxTime());
    interceptor.setMaxLength(mybatisProperties.getMaxLength());
    return interceptor;
}
```

#### 配置属性

```yaml
zeka-stack:
  mybatis:
    enable-log: true              # 是否启用 SQL 日志
    sql-format: true              # 是否格式化 SQL
    perform-max-time: 1000       # SQL 执行最大时间（毫秒）
    max-length: 1000              # SQL 输出最大长度
```

### 2. SqlExecuteTimeoutHandler（SQL 执行超时处理器）

`SqlExecuteTimeoutHandler` 用于监听和处理 SQL 执行超时事件。

#### 工作原理

```java
@Component
public class SqlExecuteTimeoutHandler {

    @EventListener
    @Async
    public void handleSqlExecuteTimeoutEvent(SqlExecuteTimeoutEvent event) {
        // 记录超时的 SQL 到文件
        logSlowSqlToFile(event);

        // 可以扩展其他处理逻辑
        // 如：发送告警、记录到监控系统等
    }
}
```

#### 核心功能

1. **事件监听**
    - 异步监听 SQL 执行超时事件
    - 不阻塞主业务流程
    - 支持多个监听器

2. **日志记录**
    - 将超时的 SQL 记录到单独的日志文件
    - 便于后续分析和优化
    - 支持日志滚动和归档

3. **扩展支持**
    - 支持自定义处理逻辑
    - 可以集成监控系统
    - 可以发送告警通知

#### 配置

```java
@Bean
@ConditionalOnProperty(
    value = "zeka-stack.mybatis.append-sql-file",
    havingValue = "true"
)
@ConditionalOnMissingBean
public SqlExecuteTimeoutHandler sqlExecuteTimeoutHandler() {
    return new SqlExecuteTimeoutHandler();
}
```

### 3. P6spy 集成

框架支持使用 P6spy 进行 SQL 监控，当检测到 P6spy 存在时，会自动禁用 `PerformanceInterceptor`，避免重复监控。

#### 配置

```xml
<dependency>
    <groupId>p6spy</groupId>
    <artifactId>p6spy</artifactId>
</dependency>
```

```yaml
spring:
  datasource:
    driver-class-name: com.p6spy.engine.spy.P6SpyDriver
    url: jdbc:p6spy:mysql://localhost:3306/test
```

## 监控功能

### 1. SQL 执行时间监控

#### 日志输出示例

```
2024-01-01 10:00:00.123 INFO  - SQL 执行耗时: 45ms
SELECT * FROM user WHERE id = 1
```

#### 格式化 SQL 输出

```
2024-01-01 10:00:00.123 INFO  - SQL 执行耗时: 45ms
SELECT
    id,
    username,
    email
FROM
    user
WHERE
    id = 1
```

### 2. 慢查询检测

#### 慢查询日志

```
2024-01-01 10:00:00.123 WARN  - SQL 执行超时: 1500ms (阈值: 1000ms)
SELECT * FROM user WHERE status = 1 ORDER BY create_time DESC
```

#### 超时事件

```java
@EventListener
@Async
public void handleSqlExecuteTimeoutEvent(SqlExecuteTimeoutEvent event) {
    log.warn("SQL 执行超时: {}ms, SQL: {}",
        event.getDuration(),
        event.getSql());

    // 可以扩展其他处理逻辑
    // 如：发送告警、记录到监控系统等
}
```

### 3. 性能统计

#### 统计信息

- SQL 执行次数
- 平均执行时间
- 最大执行时间
- 最小执行时间
- 慢查询数量

## 使用示例

### 1. 基本使用

引入依赖后，性能监控会自动启用（开发环境）：

```java
@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(Long id);
}

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User getUser(Long id) {
        // SQL 执行会被自动监控
        // 执行时间、SQL 语句等信息会被记录
        return userMapper.findById(id);
    }
}
```

### 2. 自定义超时处理

```java
@Component
@Slf4j
public class CustomSqlTimeoutHandler {

    @EventListener
    @Async
    public void handleSqlExecuteTimeoutEvent(SqlExecuteTimeoutEvent event) {
        // 记录到数据库
        saveSlowSqlToDatabase(event);

        // 发送告警
        sendAlert(event);

        // 记录到监控系统
        recordToMonitoringSystem(event);
    }

    private void saveSlowSqlToDatabase(SqlExecuteTimeoutEvent event) {
        SlowSqlLog log = new SlowSqlLog();
        log.setSql(event.getSql());
        log.setDuration(event.getDuration());
        log.setTraceId(event.getTraceId());
        log.setCreateTime(new Date());
        slowSqlLogMapper.insert(log);
    }
}
```

### 3. 配置 SQL 日志输出

```yaml
zeka-stack:
  mybatis:
    enable-log: true              # 启用 SQL 日志
    sql-format: true              # 格式化 SQL
    perform-max-time: 1000        # 慢查询阈值（毫秒）
    max-length: 2000              # SQL 输出最大长度
    append-sql-file: true         # 将超时 SQL 写入文件
```

## 设计优势

### 1. 性能监控

- **精确记录**：精确到毫秒的执行时间记录
- **自动检测**：自动检测慢查询
- **统计分析**：提供性能统计分析

### 2. 问题定位

- **详细日志**：记录 SQL 语句、执行时间、参数等信息
- **事件驱动**：通过事件机制支持异步处理
- **链路追踪**：通过 TraceId 关联请求和 SQL 执行

### 3. 开发体验

- **零配置**：引入依赖即可使用
- **灵活配置**：支持通过配置调整监控行为
- **易于扩展**：支持自定义处理逻辑

## 注意事项

### 1. 性能考虑

- SQL 监控会带来一定的性能开销
- 生产环境建议关闭或使用 P6spy
- 合理设置慢查询阈值

### 2. 日志管理

- SQL 日志可能产生大量数据
- 需要定期清理和归档
- 注意日志文件大小

### 3. 安全性

- SQL 日志可能包含敏感信息
- 生产环境需要谨慎处理
- 避免在日志中输出敏感数据

## 最佳实践

### 1. 环境配置

- **开发环境**：启用所有监控功能
- **测试环境**：启用监控，便于性能测试
- **生产环境**：使用 P6spy 或关闭监控

### 2. 阈值设置

- 根据业务需求设置合理的慢查询阈值
- 定期分析慢查询日志
- 优化慢查询 SQL

### 3. 监控集成

- 集成监控系统（如 Prometheus、Grafana）
- 设置告警规则
- 定期分析性能数据

## 总结

SQL 性能监控通过拦截器机制实现了对 SQL 执行性能的监控和分析，帮助开发者快速识别和优化性能问题。这种设计不仅提供了强大的性能监控能力，还保持了良好的性能和易用性，是构建高质量数据访问层的重要基础。

