# Cubo Combiner Spring Boot

## 概述

`cubo-combiner-spring-boot` 是 Cubo Starter 项目的组合器模块，用于聚合多个 Starter 组件，简化使用者引入依赖的数量。该模块提供了预定义的 Starter
组合，让开发者可以快速引入常用的技术栈组合，而不需要逐个引入各个模块的依赖。

## 主要功能

### 1. 依赖聚合

- 将多个相关的 Starter 模块组合在一起
- 减少项目中的依赖声明数量
- 提供开箱即用的技术栈组合

### 2. 预定义组合

- **Framework Starter**: 基础框架组合
- **SSM Starter**: Spring + Spring MVC + MyBatis 组合
- 可根据需要扩展更多组合

### 3. 版本管理

- 统一管理组合中各个模块的版本
- 确保组合内模块的兼容性
- 简化版本升级和维护

### 4. 配置简化

- 提供组合的默认配置
- 减少重复的配置工作
- 支持组合级别的配置覆盖

## 模块结构

```
cubo-combiner-spring-boot/
├── cubo-framework-spring-boot-starter/    # 基础框架组合
└── cubo-ssm-spring-boot-starter/          # SSM 技术栈组合
```

### 子模块说明

#### cubo-framework-spring-boot-starter

基础框架组合，包含以下模块：

- **cubo-launcher-spring-boot-starter**: 应用启动器
- **cubo-logsystem-spring-boot-starter**: 日志系统
- **cubo-rest-spring-boot-starter**: REST API 支持
- **cubo-endpoint-spring-boot-starter**: 端点管理

#### cubo-ssm-spring-boot-starter

SSM 技术栈组合，包含以下模块：

- **cubo-framework-spring-boot-starter**: 基础框架（传递依赖）
- **cubo-mybatis-spring-boot-starter**: MyBatis 数据访问
- **cubo-openapi-spring-boot-starter**: API 文档
- **cubo-messaging-spring-boot-starter**: 消息处理

## 核心特性

### 1. 依赖传递

#### Framework Starter 依赖

```xml
<dependencies>
    <!-- 启动器 -->
    <dependency>
        <groupId>dev.dong4j</groupId>
        <artifactId>cubo-launcher-spring-boot-starter</artifactId>
    </dependency>

    <!-- 日志系统 -->
    <dependency>
        <groupId>dev.dong4j</groupId>
        <artifactId>cubo-logsystem-log4j2-spring-boot-starter</artifactId>
    </dependency>

    <!-- REST API -->
    <dependency>
        <groupId>dev.dong4j</groupId>
        <artifactId>cubo-rest-servlet-spring-boot-starter</artifactId>
    </dependency>

    <!-- 端点管理 -->
    <dependency>
        <groupId>dev.dong4j</groupId>
        <artifactId>cubo-endpoint-servlet-spring-boot-starter</artifactId>
    </dependency>
</dependencies>
```

#### SSM Starter 依赖

```xml
<dependencies>
    <!-- 基础框架 -->
    <dependency>
        <groupId>dev.dong4j</groupId>
        <artifactId>cubo-framework-spring-boot-starter</artifactId>
    </dependency>

    <!-- MyBatis 数据访问 -->
    <dependency>
        <groupId>dev.dong4j</groupId>
        <artifactId>cubo-mybatis-spring-boot-starter</artifactId>
    </dependency>

    <!-- API 文档 -->
    <dependency>
        <groupId>dev.dong4j</groupId>
        <artifactId>cubo-openapi-knife4j-spring-boot-starter</artifactId>
    </dependency>

    <!-- 消息处理 -->
    <dependency>
        <groupId>dev.dong4j</groupId>
        <artifactId>cubo-messaging-kafka-spring-boot-starter</artifactId>
    </dependency>
</dependencies>
```

### 2. 自动配置

#### 组合自动配置

```java
@AutoConfiguration
@Import({
    LauncherAutoConfiguration.class,
    LogSystemAutoConfiguration.class,
    RestAutoConfiguration.class,
    EndpointAutoConfiguration.class
})
public class FrameworkAutoConfiguration {
    // 框架组合的自动配置
}
```

### 3. 配置管理

#### 组合配置属性

```yaml
# 框架组合配置
zeka-stack:
  framework:
    enabled: true
    launcher:
      enabled: true
    logsystem:
      enabled: true
    rest:
      enabled: true
    endpoint:
      enabled: true

# SSM 组合配置
zeka-stack:
  ssm:
    enabled: true
    framework:
      enabled: true
    mybatis:
      enabled: true
    openapi:
      enabled: true
    messaging:
      enabled: true
```

## 使用方式

### 1. 引入依赖

#### 使用基础框架组合

```xml
<dependency>
    <groupId>dev.dong4j</groupId>
    <artifactId>cubo-framework-spring-boot-starter</artifactId>
</dependency>
```

#### 使用 SSM 技术栈组合

```xml
<dependency>
    <groupId>dev.dong4j</groupId>
    <artifactId>cubo-ssm-spring-boot-starter</artifactId>
</dependency>
```

### 2. 配置启用

#### 基础框架配置

```yaml
zeka-stack:
  framework:
    enabled: true
    launcher:
      enabled: true
      refresh-scope:
        enabled: true
    logsystem:
      enabled: true
      refresh-log-level:
        enabled: true
    rest:
      enabled: true
      servlet:
        enabled: true
    endpoint:
      enabled: true
      info:
        enabled: true
      health:
        enabled: true

spring:
  application:
    name: "my-application"
    version: "1.0.0"
```

#### SSM 技术栈配置

```yaml
zeka-stack:
  ssm:
    enabled: true
    framework:
      enabled: true
    mybatis:
      enabled: true
      single-page-limit: 500
      enable-log: true
    openapi:
      enabled: true
      title: "SSM API 文档"
      version: "1.0.0"
    messaging:
      enabled: true
      kafka:
        enabled: true

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/test
    username: root
    password: password
    driver-class-name: com.mysql.cj.jdbc.Driver

kafka:
  bootstrap-servers: localhost:9092
  consumer:
    group-id: my-group
```

### 3. 快速开始

#### 创建 Spring Boot 应用

```java
@SpringBootApplication
public class SsmApplication {

    public static void main(String[] args) {
        SpringApplication.run(SsmApplication.class, args);
    }
}
```

#### 创建 Controller

```java
@RestController
@RequestMapping("/api/users")
@Api(tags = "用户管理")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    @ApiOperation(value = "根据ID获取用户")
    public ApiResponse<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ApiResponse.success(user);
    }

    @PostMapping
    @ApiOperation(value = "创建用户")
    public ApiResponse<User> createUser(@RequestBody @Valid UserCreateRequest request) {
        User user = userService.createUser(request);
        return ApiResponse.success(user);
    }
}
```

#### 创建 Service

```java
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }

    public User createUser(UserCreateRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        userMapper.insert(user);
        return user;
    }
}
```

#### 创建 Mapper

```java
@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(@Param("username") String username);
}
```

## 高级功能

### 1. 自定义组合

#### 创建自定义 Starter

```xml
<dependencies>
    <!-- 基础框架 -->
    <dependency>
        <groupId>dev.dong4j</groupId>
        <artifactId>cubo-framework-spring-boot-starter</artifactId>
    </dependency>

    <!-- 自定义模块 -->
    <dependency>
        <groupId>com.example</groupId>
        <artifactId>custom-module-spring-boot-starter</artifactId>
    </dependency>
</dependencies>
```

#### 自定义自动配置

```java
@AutoConfiguration
@Import({
    FrameworkAutoConfiguration.class,
    CustomModuleAutoConfiguration.class
})
public class CustomCombinerAutoConfiguration {
    // 自定义组合的自动配置
}
```

### 2. 条件配置

#### 基于环境的配置

```java
@Configuration
@ConditionalOnProperty(name = "zeka-stack.ssm.messaging.enabled", havingValue = "true")
public class MessagingConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MessagingTemplate messagingTemplate() {
        return new MessagingTemplate();
    }
}
```

### 3. 配置覆盖

#### 组合级别配置

```yaml
# 覆盖组合中特定模块的配置
zeka-stack:
  ssm:
    enabled: true
    mybatis:
      single-page-limit: 1000  # 覆盖默认值 500
    openapi:
      title: "我的 API 文档"    # 覆盖默认标题
```

## 最佳实践

### 1. 组合设计

- 选择相关的模块进行组合
- 避免组合过于庞大，影响灵活性
- 提供清晰的组合说明和配置指南

### 2. 版本管理

- 确保组合内模块版本的兼容性
- 定期更新组合中的模块版本
- 提供版本升级指南

### 3. 配置管理

- 提供合理的默认配置
- 支持配置的灵活覆盖
- 提供配置验证和错误提示

### 4. 文档维护

- 及时更新组合的文档
- 提供使用示例和最佳实践
- 记录版本变更和升级说明

## 注意事项

1. **依赖冲突**: 组合中可能存在依赖冲突，需要仔细检查
2. **版本兼容**: 确保组合内模块的版本兼容性
3. **配置冲突**: 避免组合配置与单独模块配置冲突
4. **性能影响**: 组合可能引入不必要的依赖，影响应用性能

## 扩展指南

### 1. 添加新模块到组合

```xml
<dependencies>
    <!-- 现有模块 -->
    <dependency>
        <groupId>dev.dong4j</groupId>
        <artifactId>cubo-framework-spring-boot-starter</artifactId>
    </dependency>

    <!-- 新模块 -->
    <dependency>
        <groupId>dev.dong4j</groupId>
        <artifactId>cubo-new-module-spring-boot-starter</artifactId>
    </dependency>
</dependencies>
```

### 2. 创建新的组合

```xml
<dependencies>
    <!-- 基础依赖 -->
    <dependency>
        <groupId>dev.dong4j</groupId>
        <artifactId>cubo-boot-dependencies</artifactId>
        <type>pom</type>
        <scope>import</scope>
    </dependency>

    <!-- 组合模块 -->
    <dependency>
        <groupId>dev.dong4j</groupId>
        <artifactId>cubo-module1-spring-boot-starter</artifactId>
    </dependency>
    <dependency>
        <groupId>dev.dong4j</groupId>
        <artifactId>cubo-module2-spring-boot-starter</artifactId>
    </dependency>
</dependencies>
```

## 相关链接

- [Spring Boot Starter 开发指南](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-developing-auto-configuration)
- [Maven 依赖管理](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html)
- [Spring Boot 自动配置](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-developing-auto-configuration)
