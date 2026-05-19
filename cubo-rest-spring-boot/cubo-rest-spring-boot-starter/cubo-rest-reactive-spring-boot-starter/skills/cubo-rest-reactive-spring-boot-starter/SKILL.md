---
name: cubo-rest-reactive-spring-boot-starter
description: 在使用 Zeka Stack Cubo REST Reactive Starter 编写 Spring WebFlux REST API 时使用。
license: Apache-2.0
---

# Cubo REST Reactive Spring Boot Starter

## 适用场景

当项目依赖 `cubo-rest-reactive-spring-boot-starter`，并且需要编写或调整 Spring WebFlux / Reactive 技术栈下的 REST 接口、响应式参数处理、统一响应、异常处理时，使用本
Skill。

## 组件定位

`cubo-rest-reactive-spring-boot-starter` 是 Cubo REST 面向 Reactive 技术栈的独立 Starter。它面向 Spring WebFlux 应用，组合了 Cubo REST 的
Reactive 实现、自动配置和 WebFlux 相关依赖，用于在响应式应用中保持 Zeka Stack 的 REST 工程规范。

它主要提供以下能力：

- 基于 Spring WebFlux 的 REST Controller 编写方式
- 响应式返回类型 `Mono<Result<T>>` 与统一响应模型的组合
- Reactive 环境下的全局异常处理
- WebFlux 自动配置与 Reactive Web 运行时依赖
- 链路追踪（traceId）支持
- 请求参数校验和错误响应格式

## 依赖方式

Reactive Web 应用优先依赖该 Starter：

```xml
<dependency>
    <groupId>dev.dong4j</groupId>
    <artifactId>cubo-rest-reactive-spring-boot-starter</artifactId>
</dependency>
```

除非项目明确需要 Servlet Web 栈，否则不要引入 `cubo-rest-servlet-spring-boot-starter`。

## 与同组件其他 Starter 的选型

| Starter                                  | 推荐场景                                                           | 不推荐场景                    |
|------------------------------------------|----------------------------------------------------------------|--------------------------|
| `cubo-rest-reactive-spring-boot-starter` | WebFlux / Reactive 应用、响应式编程场景                                  | 传统 Servlet 应用、需要阻塞式数据库访问 |
| `cubo-rest-servlet-spring-boot-starter`  | Spring MVC / Servlet 应用、传统同步 Web 应用、需要 Undertow / Log4j Web 特性 | WebFlux / Reactive 应用    |

## 统一响应模型

所有 REST 接口必须使用 Zeka Stack 统一响应模型 `Result<T>`。在 Reactive 环境下，返回类型为 `Mono<Result<T>>`。

### 响应结构

| 字段        | 类型      | 说明                      |
|-----------|---------|-------------------------|
| `code`    | Integer | 状态码，2000 表示成功，4000 表示失败 |
| `success` | boolean | 请求是否成功                  |
| `data`    | T       | 响应数据                    |
| `message` | String  | 响应消息                    |
| `traceId` | String  | 链路追踪标识                  |
| `extend`  | Object  | 扩展字段（非生产环境异常信息会写入此字段）   |

### 使用方式

```java
// 成功响应
@GetMapping("/users/{id}")
public Mono<Result<User>> getUser(@PathVariable Long id) {
    return userService.findById(id)
        .map(R::succeed);
}

// 失败响应
@GetMapping("/users/{id}")
public Mono<Result<User>> getUser(@PathVariable Long id) {
    return userService.findById(id)
        .map(R::succeed)
        .onErrorResume(e -> Mono.just(R.failed(e.getMessage())));
}

// 使用 switchIfEmpty 处理空结果
@GetMapping("/users/{id}")
public Mono<Result<User>> getUser(@PathVariable Long id) {
    return userService.findById(id)
        .map(R::succeed)
        .switchIfEmpty(Mono.just(R.failed("用户不存在")));
}
```

## 核心注解

### @CurrentUser（Reactive 环境）

Reactive 环境下通过 `ServerWebExchange` 获取当前用户信息：

```java
@GetMapping("/users/profile")
public Mono<Result<User>> getUserProfile(@RequestHeader("Authorization") String token) {
    Long userId = extractUserIdFromToken(token);
    return userService.findById(userId)
        .map(R::succeed);
}
```

### @RequestSingleParam

从 JSON 请求体中解析单个字段值：

```java
@PostMapping("/search")
public Mono<Result<List<User>>> search(@RequestBody Map<String, Object> body) {
    String keyword = (String) body.get("keyword");
    return userService.search(keyword)
        .map(R::succeed);
}
```

## 编码规则

- REST 接口使用 `@RestController`，按 WebFlux 方式组织请求映射
- Controller 返回类型应保持响应式，使用 `Mono<Result<T>>` 或 `Flux<Result<T>>`
- 不要在 Reactive 调用链中使用阻塞式数据库、文件或远程调用；如果必须接入阻塞逻辑，需要明确隔离调度器并使用
  `subscribeOn(Schedulers.boundedElastic())`
- 请求体参数使用 `@Valid`，Controller 或方法级参数校验使用 `@Validated`
- 优先使用 Zeka Stack 统一响应类型 `Result<T>`
- 异常处理应复用 Cubo REST 在 Reactive 环境下提供的错误处理模型，不要手写临时 JSON 错误结构
- 生成代码应体现 Reactive 技术栈特征，不要混入 Servlet 专用类型或阻塞式调用模式

## 自动配置的 Bean

该 Starter 自动配置以下 Bean（可通过覆盖同类型 Bean 来替换默认行为）：

### 核心组件

| Bean 类型             | 方法名               | 说明          | 覆盖方式                                         |
|---------------------|-------------------|-------------|----------------------------------------------|
| `ZekaRestComponent` | `restComponent()` | REST 模块组件标识 | 覆盖 Bean 名称 `App.Components.REST_SPRING_BOOT` |
| `Validator`         | `validator()`     | 生产环境快速失败验证器 | 仅生产环境生效                                      |

### 异常处理

| Bean 类型                    | 方法名                          | 说明              | 覆盖方式       |
|----------------------------|------------------------------|-----------------|------------|
| `ErrorWebExceptionHandler` | `errorWebExceptionHandler()` | 全局异常处理器（优先级 -2） | 定义同类型 Bean |
| `ErrorAttributes`          | `errorAttributes()`          | 错误属性提取器         | 定义同类型 Bean |
| `ServerCodecConfigurer`    | `serverCodecConfigurer()`    | HTTP 消息编解码器     | 定义同类型 Bean |

## 配置规则

### 基础配置

```yaml
zeka-stack:
  rest:
    enabled: true
    read-timeout: 5000
    write-timeout: 5000
    connect-timeout: 3000
    enable-global-cache-filter: true
    enable-exception-filter: true
    enable-entity-enum-all-field-serialize: true
```

### 配置属性说明

| 属性名                                                      | 类型      | 默认值                   | 说明            |
|----------------------------------------------------------|---------|-----------------------|---------------|
| `zeka-stack.rest.enabled`                                | boolean | true                  | 是否启用 REST 功能  |
| `zeka-stack.rest.read-timeout`                           | int     | 5000                  | HTTP 读取超时（毫秒） |
| `zeka-stack.rest.write-timeout`                          | int     | 5000                  | HTTP 写入超时（毫秒） |
| `zeka-stack.rest.connect-timeout`                        | int     | 3000                  | HTTP 连接超时（毫秒） |
| `zeka-stack.rest.connection-pool.max-idle-connections`   | int     | 5                     | 最大空闲连接数       |
| `zeka-stack.rest.connection-pool.keep-alive-duration`    | int     | 5                     | 连接保活时间（分钟）    |
| `zeka-stack.rest.enable-global-cache-filter`             | boolean | true                  | 全局缓存过滤器       |
| `zeka-stack.rest.enable-exception-filter`                | boolean | true                  | 全局异常过滤器       |
| `zeka-stack.rest.enable-entity-enum-all-field-serialize` | boolean | true                  | 枚举全字段序列化      |
| `zeka-stack.rest.json.date-format`                       | String  | `yyyy-MM-dd HH:mm:ss` | JSON 日期格式     |
| `zeka-stack.rest.json.time-zone`                         | String  | `Asia/Shanghai`       | JSON 时区       |
| `zeka-stack.rest.json.default-property-inclusion`        | String  | `non_null`            | 空值字段处理        |

### 覆盖默认行为

```java
@Configuration
public class MyReactiveRestConfig {

    // 自定义错误属性提取器
    @Bean
    @Override
    public ErrorAttributes errorAttributes() {
        return new CustomErrorAttributes();
    }
}
```

## Reactive 注意事项

### 调度器隔离

如果必须在 Reactive 链路中调用阻塞式操作，必须使用 `subscribeOn` 隔离：

```java
@GetMapping("/users")
public Mono<Result<List<User>>> getUsers() {
    return userService.findAll()
        .publishOn(Schedulers.boundedElastic())  // 对于阻塞操作
        .map(R::succeed);
}
```

### 背压处理

WebFlux 基于响应式流，需要正确处理背压：

```java
@GetMapping("/users")
public Flux<User> getUsers(@RequestParam(defaultValue = "0") int offset,
                           @RequestParam(defaultValue = "100") int limit) {
    return userService.findAll()
        .skip(offset)
        .take(limit);
}
```

## 不要这样做

- 不要在 Reactive Controller 中返回 Servlet 专用类型（如 `HttpServletRequest`）
- 不要在响应式链路中直接调用阻塞 API 而不说明隔离方式
- 不要为单个接口临时创建与 Zeka Stack 不一致的响应包装类
- 不要绕过 Cubo REST 的 Reactive 异常处理模型自行拼接错误响应
- 不要照搬 Servlet 写法而忽略 WebFlux 的响应式边界
- 不要在 `Mono` 或 `Flux` 内部使用 `block()` 方法，这会破坏响应式流
