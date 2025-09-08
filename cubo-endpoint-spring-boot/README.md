# Cubo Endpoint Spring Boot

## 概述

`cubo-endpoint-spring-boot` 是 Cubo Starter 项目的端点管理模块，提供了应用监控和管理端点的功能。该模块支持 Servlet 和 Reactive 两种 Web
技术栈，提供了应用信息、健康检查、性能监控等端点，便于应用的运维和监控。

## 主要功能

### 1. 应用信息端点

- 提供应用的基本信息（名称、版本、描述等）
- 显示系统环境信息（Java 版本、操作系统等）
- 支持自定义应用信息

### 2. 健康检查端点

- 提供应用健康状态检查
- 支持数据库、消息队列等组件的健康检查
- 提供详细的健康状态信息

### 3. 性能监控端点

- 提供应用性能指标
- 监控内存、CPU、线程等系统资源
- 支持自定义监控指标

### 4. 管理端点

- 提供应用管理功能
- 支持配置刷新、日志级别调整等操作
- 提供安全的管理接口

## 模块结构

```
cubo-endpoint-spring-boot/
├── cubo-endpoint-spring-boot-autoconfigure/    # 自动配置模块
├── cubo-endpoint-spring-boot-core/             # 核心功能模块
│   ├── cubo-endpoint-common/                   # 通用组件
│   ├── cubo-endpoint-servlet/                  # Servlet 实现
│   └── cubo-endpoint-reactive/                 # Reactive 实现
└── cubo-endpoint-spring-boot-starter/          # Starter 模块
    ├── cubo-endpoint-servlet-spring-boot-starter/
    └── cubo-endpoint-reactive-spring-boot-starter/
```

### 子模块说明

#### cubo-endpoint-spring-boot-autoconfigure

- **EndpointAutoConfiguration**: 端点主自动配置类
- **ServletStartInfoAutoConfiguration**: Servlet 启动信息配置
- **ReactiveStartInfoAutoConfiguration**: Reactive 启动信息配置
- **ProjectInfoEndpointAutoConfiguration**: 项目信息端点配置

#### cubo-endpoint-spring-boot-core

##### cubo-endpoint-common

- 提供通用的端点组件和工具类
- 定义端点响应的标准格式
- 提供端点安全配置

##### cubo-endpoint-servlet

- 基于 Spring MVC 的 Servlet 端点实现
- 提供传统的 Web 端点支持
- 支持 Spring Boot Actuator 集成

##### cubo-endpoint-reactive

- 基于 Spring WebFlux 的 Reactive 端点实现
- 提供响应式端点支持
- 支持非阻塞 I/O 操作

## 核心特性

### 1. 应用信息端点

#### 项目信息端点

```java
@RestController
@RequestMapping("/actuator")
public class ProjectInfoEndpoint {

    @Autowired
    private ProjectInfoService projectInfoService;

    @GetMapping("/info")
    public ResponseEntity<ProjectInfo> getProjectInfo() {
        ProjectInfo info = projectInfoService.getProjectInfo();
        return ResponseEntity.ok(info);
    }

    @GetMapping("/health")
    public ResponseEntity<HealthInfo> getHealthInfo() {
        HealthInfo health = projectInfoService.getHealthInfo();
        return ResponseEntity.ok(health);
    }
}
```

#### 项目信息模型

```java
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectInfo {

    private String name;
    private String version;
    private String description;
    private String buildTime;
    private String gitCommit;
    private String gitBranch;
    private String javaVersion;
    private String osName;
    private String osVersion;
    private String springBootVersion;
    private Map<String, Object> customInfo;
}
```

### 2. 启动信息端点

#### Servlet 启动信息

```java
@Component
@Slf4j
public class ServletStartInfoService {

    @EventListener
    public void onApplicationReady(ApplicationReadyEvent event) {
        ApplicationContext context = event.getApplicationContext();
        ConfigurableEnvironment env = context.getEnvironment();

        log.info("应用启动完成");
        log.info("应用名称: {}", env.getProperty("spring.application.name"));
        log.info("应用端口: {}", env.getProperty("server.port"));
        log.info("激活环境: {}", Arrays.toString(env.getActiveProfiles()));
        log.info("启动时间: {}", new Date());
    }
}
```

#### Reactive 启动信息

```java
@Component
@Slf4j
public class ReactiveStartInfoService {

    @EventListener
    public void onApplicationReady(ApplicationReadyEvent event) {
        ApplicationContext context = event.getApplicationContext();
        ConfigurableEnvironment env = context.getEnvironment();

        log.info("响应式应用启动完成");
        log.info("应用名称: {}", env.getProperty("spring.application.name"));
        log.info("应用端口: {}", env.getProperty("server.port"));
        log.info("激活环境: {}", Arrays.toString(env.getActiveProfiles()));
        log.info("启动时间: {}", new Date());
    }
}
```

### 3. 健康检查端点

#### 健康检查服务

```java
@Service
public class HealthCheckService {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public HealthInfo getHealthInfo() {
        HealthInfo.HealthInfoBuilder builder = HealthInfo.builder();

        // 检查数据库连接
        boolean dbHealthy = checkDatabaseHealth();
        builder.databaseStatus(dbHealthy ? "UP" : "DOWN");

        // 检查 Redis 连接
        boolean redisHealthy = checkRedisHealth();
        builder.redisStatus(redisHealthy ? "UP" : "DOWN");

        // 检查系统资源
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        builder.heapUsed(heapUsage.getUsed());
        builder.heapMax(heapUsage.getMax());

        // 计算整体健康状态
        boolean overallHealthy = dbHealthy && redisHealthy;
        builder.status(overallHealthy ? "UP" : "DOWN");

        return builder.build();
    }

    private boolean checkDatabaseHealth() {
        try (Connection connection = dataSource.getConnection()) {
            return connection.isValid(5);
        } catch (SQLException e) {
            log.error("数据库健康检查失败", e);
            return false;
        }
    }

    private boolean checkRedisHealth() {
        try {
            redisTemplate.opsForValue().get("health-check");
            return true;
        } catch (Exception e) {
            log.error("Redis 健康检查失败", e);
            return false;
        }
    }
}
```

### 4. 预热组件

#### 预热服务

```java
@Component
@Slf4j
public class PreloadComponent {

    @Autowired(required = false)
    private InitializationService initializationService;

    @EventListener
    public void onApplicationReady(ApplicationReadyEvent event) {
        log.info("开始执行应用预热...");

        if (initializationService != null) {
            try {
                initializationService.initialize();
                log.info("应用预热完成");
            } catch (Exception e) {
                log.error("应用预热失败", e);
            }
        } else {
            log.info("未配置初始化服务，跳过预热");
        }
    }
}
```

## 配置属性

### EndpointProperties

| 属性名                                    | 类型      | 默认值  | 说明               |
|----------------------------------------|---------|------|------------------|
| `zeka-stack.endpoint.enabled`          | boolean | true | 是否启用端点功能         |
| `zeka-stack.endpoint.servlet.enabled`  | boolean | true | 是否启用 Servlet 端点  |
| `zeka-stack.endpoint.reactive.enabled` | boolean | true | 是否启用 Reactive 端点 |
| `zeka-stack.endpoint.info.enabled`     | boolean | true | 是否启用信息端点         |
| `zeka-stack.endpoint.health.enabled`   | boolean | true | 是否启用健康检查端点       |

### 应用信息配置

| 属性名                              | 类型     | 默认值         | 说明        |
|----------------------------------|--------|-------------|-----------|
| `spring.application.name`        | String | application | 应用名称      |
| `spring.application.version`     | String | 1.0.0       | 应用版本      |
| `spring.application.description` | String | -           | 应用描述      |
| `info.build.time`                | String | -           | 构建时间      |
| `info.git.commit`                | String | -           | Git 提交 ID |
| `info.git.branch`                | String | -           | Git 分支    |

## 使用方式

### 1. 引入依赖

**使用 Servlet 端点**

```xml
<dependency>
    <groupId>dev.dong4j</groupId>
    <artifactId>cubo-endpoint-servlet-spring-boot-starter</artifactId>
</dependency>
```

**使用 Reactive 端点**

```xml
<dependency>
    <groupId>dev.dong4j</groupId>
    <artifactId>cubo-endpoint-reactive-spring-boot-starter</artifactId>
</dependency>
```

### 2. 配置启用

```yaml
zeka-stack:
  endpoint:
    enabled: true
    servlet:
      enabled: true
    reactive:
      enabled: true
    info:
      enabled: true
    health:
      enabled: true

spring:
  application:
    name: "my-application"
    version: "1.0.0"
    description: "我的应用程序"

info:
  build:
    time: "@build.time@"
  git:
    commit: "@git.commit.id@"
    branch: "@git.branch@"
```

### 3. 访问端点

- **应用信息**: http://localhost:8080/actuator/info
- **健康检查**: http://localhost:8080/actuator/health
- **系统信息**: http://localhost:8080/actuator/system
- **性能指标**: http://localhost:8080/actuator/metrics

### 4. 自定义端点

```java
@RestController
@RequestMapping("/actuator/custom")
public class CustomEndpoint {

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getCustomStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("custom", "value");
        status.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(status);
    }
}
```

## 高级功能

### 1. 自定义健康检查

```java
@Component
public class CustomHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        try {
            // 执行自定义健康检查逻辑
            boolean isHealthy = performCustomHealthCheck();

            if (isHealthy) {
                return Health.up()
                    .withDetail("custom", "服务正常")
                    .build();
            } else {
                return Health.down()
                    .withDetail("custom", "服务异常")
                    .build();
            }
        } catch (Exception e) {
            return Health.down()
                .withDetail("custom", "检查失败")
                .withException(e)
                .build();
        }
    }

    private boolean performCustomHealthCheck() {
        // 自定义健康检查逻辑
        return true;
    }
}
```

### 2. 端点安全配置

```java
@Configuration
@EnableWebSecurity
public class EndpointSecurityConfig {

    @Bean
    public SecurityFilterChain endpointSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                .requestMatchers("/actuator/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
```

### 3. 端点监控

```java
@Component
@Slf4j
public class EndpointMonitor {

    @EventListener
    public void onEndpointAccess(EndpointAccessEvent event) {
        log.info("端点访问: {} 来自: {}",
            event.getEndpoint(),
            event.getSource());
    }
}
```

### 4. 自定义指标

```java
@Component
public class CustomMetrics {

    private final MeterRegistry meterRegistry;
    private final Counter customCounter;
    private final Timer customTimer;

    public CustomMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.customCounter = Counter.builder("custom.operations")
            .description("自定义操作计数")
            .register(meterRegistry);
        this.customTimer = Timer.builder("custom.duration")
            .description("自定义操作耗时")
            .register(meterRegistry);
    }

    public void incrementCounter() {
        customCounter.increment();
    }

    public void recordDuration(Duration duration) {
        customTimer.record(duration);
    }
}
```

## 最佳实践

### 1. 端点设计

- 提供清晰的端点命名和描述
- 使用标准的 HTTP 状态码
- 提供详细的错误信息
- 考虑端点的安全性

### 2. 健康检查

- 实现全面的健康检查逻辑
- 提供详细的健康状态信息
- 考虑依赖服务的健康状态
- 设置合理的超时时间

### 3. 性能监控

- 监控关键性能指标
- 设置合理的告警阈值
- 定期分析性能数据
- 优化性能瓶颈

### 4. 安全考虑

- 限制敏感端点的访问权限
- 使用适当的认证和授权
- 避免暴露敏感信息
- 定期审查端点安全配置

## 注意事项

1. **性能影响**: 端点访问会影响应用性能，需要合理配置
2. **安全风险**: 敏感端点需要适当的访问控制
3. **资源消耗**: 监控端点可能消耗系统资源
4. **版本兼容**: 注意与 Spring Boot Actuator 的兼容性

## 相关链接

- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Micrometer 监控](https://micrometer.io/)
- [Spring Boot 健康检查](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.health)
- [应用监控最佳实践](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.monitoring)
