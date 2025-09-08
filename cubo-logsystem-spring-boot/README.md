# Cubo LogSystem Spring Boot

## 概述

`cubo-logsystem-spring-boot` 是 Cubo Starter 项目的日志系统模块，提供了完整的日志解决方案。该模块支持多种日志框架，包括
Log4j2、简单日志和记录日志，并提供了日志级别动态刷新、日志记录等高级功能。

## 主要功能

### 1. 多日志框架支持

- **Log4j2**: 高性能的日志框架，支持异步日志
- **Simple Logger**: 轻量级简单日志实现
- **Record Logger**: 专门用于记录和追踪的日志系统

### 2. 日志级别动态刷新

- 支持运行时动态调整日志级别
- 无需重启应用即可生效
- 支持按包、类、方法等不同粒度的日志控制

### 3. 日志记录和追踪

- 提供专门的日志记录功能
- 支持日志的持久化存储
- 便于日志分析和问题排查

### 4. 日志输出流管理

- 自定义日志输出流
- 支持日志的格式化输出
- 提供统一的日志接口

## 模块结构

```
cubo-logsystem-spring-boot/
├── cubo-logsystem-spring-boot-autoconfigure/    # 自动配置模块
├── cubo-logsystem-spring-boot-core/             # 核心功能模块
│   ├── cubo-logsystem-common/                   # 通用日志组件
│   ├── cubo-logsystem-log4j2/                   # Log4j2 实现
│   ├── cubo-logsystem-record/                   # 记录日志实现
│   └── cubo-logsystem-simple/                   # 简单日志实现
└── cubo-logsystem-spring-boot-starter/          # Starter 模块
    ├── cubo-logsystem-log4j2-spring-boot-starter/
    └── cubo-logsystem-record-spring-boot-starter/
```

### 子模块说明

#### cubo-logsystem-spring-boot-autoconfigure

- **LogSystemAutoConfiguration**: 日志系统主自动配置类
- **RefreshLogLevelAutoConfiguration**: 日志级别动态刷新配置
- **LogSystemRecordAutoConfiguration**: 日志记录自动配置

#### cubo-logsystem-spring-boot-core

##### cubo-logsystem-common

- 提供通用的日志接口和工具类
- 定义日志系统的核心抽象

##### cubo-logsystem-log4j2

- 基于 Log4j2 的日志实现
- 支持异步日志和性能优化
- 提供丰富的配置选项

##### cubo-logsystem-record

- 专门用于记录和追踪的日志系统
- 支持日志的持久化存储
- 提供日志分析功能

##### cubo-logsystem-simple

- 轻量级的简单日志实现
- 适合简单的日志需求
- 减少依赖和复杂度

## 核心特性

### 1. 日志级别动态刷新

#### 功能特点

- ✅ 运行时动态调整日志级别
- ✅ 支持按包、类、方法等不同粒度控制
- ✅ 无需重启应用即可生效
- ✅ 支持配置文件的实时监听
- ✅ 提供 REST API 接口进行日志级别调整

#### 使用方式

**1. 通过配置文件**

```yaml
zeka-stack:
  logsystem:
    refresh-log-level:
      enabled: true
      # 动态调整日志级别
      levels:
        com.example: DEBUG
        org.springframework: INFO
```

**2. 通过 REST API**

```bash
# 调整特定包的日志级别
curl -X POST "http://localhost:8080/actuator/loggers/com.example" \
  -H "Content-Type: application/json" \
  -d '{"configuredLevel": "DEBUG"}'
```

### 2. 多日志框架支持

#### Log4j2 支持

```xml
<dependency>
    <groupId>dev.dong4j</groupId>
    <artifactId>cubo-logsystem-log4j2-spring-boot-starter</artifactId>
</dependency>
```

**配置示例**

```yaml
logging:
  config: classpath:log4j2.xml
  level:
    root: INFO
    com.example: DEBUG
```

#### 简单日志支持

```xml
<dependency>
    <groupId>dev.dong4j</groupId>
    <artifactId>cubo-logsystem-simple</artifactId>
    <scope>provided</scope>
    <optional>true</optional>
</dependency>
```

### 3. 日志记录功能

#### 记录日志支持

```xml
<dependency>
    <groupId>dev.dong4j</groupId>
    <artifactId>cubo-logsystem-record-spring-boot-starter</artifactId>
</dependency>
```

**使用示例**

```java
@Service
public class BusinessService {

    @Autowired
    private LogRecordService logRecordService;

    public void processData(String data) {
        // 记录业务操作日志
        logRecordService.record("DATA_PROCESS", "处理数据: " + data);

        // 业务逻辑
        // ...
    }
}
```

## 配置属性

### LogSystemProperties

| 属性名                                              | 类型      | 默认值  | 说明           |
|--------------------------------------------------|---------|------|--------------|
| `zeka-stack.logsystem.enabled`                   | boolean | true | 是否启用日志系统     |
| `zeka-stack.logsystem.refresh-log-level.enabled` | boolean | true | 是否启用日志级别动态刷新 |
| `zeka-stack.logsystem.record.enabled`            | boolean | true | 是否启用日志记录功能   |

### 日志级别配置

| 属性名                         | 类型     | 默认值   | 说明       |
|-----------------------------|--------|-------|----------|
| `logging.level.root`        | String | INFO  | 根日志级别    |
| `logging.level.com.example` | String | DEBUG | 特定包的日志级别 |

## 使用方式

### 1. 引入依赖

**使用 Log4j2**

```xml
<dependency>
    <groupId>dev.dong4j</groupId>
    <artifactId>cubo-logsystem-log4j2-spring-boot-starter</artifactId>
</dependency>
```

**使用记录日志**

```xml
<dependency>
    <groupId>dev.dong4j</groupId>
    <artifactId>cubo-logsystem-record-spring-boot-starter</artifactId>
</dependency>
```

### 2. 配置启用

```yaml
zeka-stack:
  logsystem:
    enabled: true
    refresh-log-level:
      enabled: true
    record:
      enabled: true

logging:
  level:
    root: INFO
    com.example: DEBUG
```

### 3. 使用日志

```java
@RestController
public class ExampleController {

    private static final Logger log = LoggerFactory.getLogger(ExampleController.class);

    @GetMapping("/example")
    public String example() {
        log.debug("处理请求开始");
        log.info("处理业务逻辑");
        log.warn("警告信息");
        log.error("错误信息");
        return "success";
    }
}
```

## 高级功能

### 1. 异步日志

**Log4j2 异步配置**

```xml
<Configuration>
    <Appenders>
        <Console name="Console" target="SYSTEM_OUT">
            <PatternLayout pattern="%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
        </Console>

        <AsyncAppender name="AsyncAppender" bufferSize="1024">
            <AppenderRef ref="Console"/>
        </AsyncAppender>
    </Appenders>

    <Loggers>
        <Root level="INFO">
            <AppenderRef ref="AsyncAppender"/>
        </Root>
    </Loggers>
</Configuration>
```

### 2. 日志文件滚动

```yaml
logging:
  file:
    name: logs/application.log
  logback:
    rollingpolicy:
      max-file-size: 100MB
      max-history: 30
      total-size-cap: 1GB
```

### 3. 日志过滤

```java
@Component
public class CustomLogFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                        FilterChain chain) throws IOException, ServletException {

        // 记录请求日志
        log.info("请求: {} {}",
            ((HttpServletRequest) request).getMethod(),
            ((HttpServletRequest) request).getRequestURI());

        chain.doFilter(request, response);
    }
}
```

## 最佳实践

### 1. 日志级别使用

- **ERROR**: 系统错误，需要立即处理
- **WARN**: 警告信息，可能影响系统运行
- **INFO**: 重要的业务信息
- **DEBUG**: 调试信息，仅在开发环境使用
- **TRACE**: 详细的跟踪信息

### 2. 性能优化

- 使用异步日志提高性能
- 合理设置日志级别，避免过多日志输出
- 使用合适的日志格式，避免复杂的字符串拼接

### 3. 日志管理

- 定期清理日志文件
- 使用日志聚合工具进行日志分析
- 设置合适的日志滚动策略

## 注意事项

1. **性能影响**: 日志输出会影响应用性能，生产环境需要合理配置
2. **存储空间**: 日志文件会占用磁盘空间，需要定期清理
3. **安全考虑**: 避免在日志中输出敏感信息
4. **异步日志**: 使用异步日志时注意内存使用情况

## 相关链接

- [Log4j2 官方文档](https://logging.apache.org/log4j/2.x/)
- [Spring Boot 日志配置](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.logging)
- [SLF4J 用户手册](http://www.slf4j.org/manual.html)
