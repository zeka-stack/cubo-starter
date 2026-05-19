---
name: cubo-rest-servlet-spring-boot-starter
description: 在使用 Zeka Stack Cubo REST Servlet Starter 编写 Spring MVC REST API 时使用。
license: Apache-2.0
---

# Cubo REST Servlet Spring Boot Starter

## 适用场景

当项目依赖 `cubo-rest-servlet-spring-boot-starter`，并且需要编写或调整 Spring MVC / Servlet 技术栈下的 REST 接口、参数校验、统一响应、异常处理、HTTP
客户端配置时，使用本 Skill。

## 组件定位

`cubo-rest-servlet-spring-boot-starter` 是 Cubo REST 面向 Servlet 技术栈的独立 Starter。它面向传统 Spring MVC Web 应用，组合了 Cubo REST 的
Servlet 实现、自动配置和 Web 相关依赖，用于统一 REST API 的工程写法。

它主要提供以下能力：

- 基于 Spring MVC 的 REST Controller 编写方式
- 基于 Bean Validation 的请求参数校验
- Zeka Stack 统一响应模型 `Result<T>`
- Servlet 环境下的全局异常处理
- 用户认证与 Token 验证拦截器
- 链路追踪（traceId）支持
- XSS 防护、跨域支持、参数注入等过滤器
- Undertow、Log4j Web 等 Servlet Web 运行时依赖

## 依赖方式

Servlet Web 应用优先依赖该 Starter：

```xml
<dependency>
    <groupId>dev.dong4j</groupId>
    <artifactId>cubo-rest-servlet-spring-boot-starter</artifactId>
</dependency>
```

除非项目明确需要响应式 Web 栈，否则不要引入 `cubo-rest-reactive-spring-boot-starter`。

## 与同组件其他 Starter 的选型

| Starter                                  | 推荐场景                                                           | 不推荐场景                    |
|------------------------------------------|----------------------------------------------------------------|--------------------------|
| `cubo-rest-servlet-spring-boot-starter`  | Spring MVC / Servlet 应用、传统同步 Web 应用、需要 Undertow / Log4j Web 特性 | WebFlux / Reactive 应用    |
| `cubo-rest-reactive-spring-boot-starter` | WebFlux / Reactive 应用、响应式编程场景                                  | 传统 Servlet 应用、需要阻塞式数据库访问 |

## 统一响应模型

所有 REST 接口必须使用 Zeka Stack 统一响应模型 `Result<T>`。

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
Result<User> result = R.succeed(user);
Result<User> result = R.succeed(2000, "操作成功", user);

// 失败响应
Result<Void> result = R.failed("参数错误");
Result<Void> result = R.failed(4000, "业务异常");
Result<Void> result = R.failed(ResultCodeEnum.PARAM_ERROR);
```

## 核心注解

### @TokenRequired

标记需要 Token 认证的接口。配合 `AuthenticationInterceptor` 使用，从请求头中验证 Token 并解析用户信息。

```java
@TokenRequired
@GetMapping("/users/profile")
public Result<User> getUserProfile(@CurrentUser Long userId) {
    return R.succeed(userService.getById(userId));
}
```

### @CurrentUser

注入当前登录用户 ID。该注解配合 `CurrentUserArgumentResolver` 和 `CurrentUserInterceptor` 使用。

```java
@GetMapping("/users/profile")
public Result<User> getUserProfile(@CurrentUser Long userId) {
    // userId 从 Token 中自动解析
}
```

### @ApiVersion

API 版本控制注解，用于 URL 路径版本化管理。

```java
@ApiVersion({1, 2})
@RestController
@RequestMapping("/users")
public class UserController {

    @ApiVersion(3)
    @GetMapping("/users")
    public Result<List<User>> getUsersV3() {
        // 仅在 /v3/users 路径下生效
    }
}
```

访问路径：`/v1/users`、`/v2/users`、`/v3/users`

### @RequestSingleParam

从 JSON 请求体中解析单个字段值。

```java
@PostMapping("/search")
public Result<List<User>> search(@RequestSingleParam("keyword") String keyword) {
    return R.succeed(userService.search(keyword));
}
```

### @FormDataBody

解析 FormData 格式的请求体。

```java
@PostMapping("/submit")
public Result<Void> submit(@FormDataBody UserForm form) {
    return R.succeed(userService.save(form));
}
```

## 编码规则

- REST 接口使用 `@RestController`，按 Spring MVC 方式组织 `@GetMapping`、`@PostMapping`、`@PutMapping`、`@DeleteMapping`
- Controller 只负责请求映射、参数校验和调用业务服务，不在 Controller 中写复杂业务逻辑或持久化逻辑
- 请求体参数使用 `@Valid`，Controller 或方法级参数校验使用 `@Validated`
- 优先使用 Zeka Stack 统一响应类型 `Result<T>`
- 业务异常、参数异常和系统异常交给 Cubo REST / Blen Kernel 提供的全局异常处理模型处理
- 新增异常处理时应扩展既有异常处理链路，不要绕过统一响应格式自行返回临时错误结构
- 生成代码应体现 Servlet 技术栈特征，不要混入 Reactive 返回类型或 WebFlux API

## 自动配置的 Bean

该 Starter 自动配置以下 Bean（可通过覆盖同类型 Bean 来替换默认行为）：

### 核心组件

| Bean 类型                       | 方法名                             | 说明                     | 覆盖方式                                         |
|-------------------------------|---------------------------------|------------------------|----------------------------------------------|
| `CurrentUserArgumentResolver` | `currentUserArgumentResolver()` | 解析 `@CurrentUser` 注解参数 | 定义同类型 Bean                                   |
| `CurrentUserService`          | `currentUserService()`          | 提供当前用户信息服务             | 定义同类型 Bean                                   |
| `CurrentUserInterceptor`      | `currentUserInterceptor()`      | 解析并传递当前用户上下文           | 定义同类型 Bean                                   |
| `AuthenticationInterceptor`   | `authenticationInterceptor()`   | Token 认证拦截器            | 定义同类型 Bean                                   |
| `TraceInterceptor`            | `restTraceInterceptor()`        | 链路追踪 ID 生成             | 定义同类型 Bean                                   |
| `ZekaRestComponent`           | `restComponent()`               | REST 模块组件标识            | 覆盖 Bean 名称 `App.Components.REST_SPRING_BOOT` |
| `Validator`                   | `validator()`                   | 生产环境快速失败验证器            | 仅生产环境生效                                      |

### 过滤器

| Bean 类型                    | 方法名                                                | 说明                  | 覆盖方式       |
|----------------------------|----------------------------------------------------|---------------------|------------|
| `CharacterEncodingFilter`  | `characterEncodingFilterFilterRegistrationBean()`  | 强制 UTF-8 编码         | 定义同类型 Bean |
| `ServletGlobalCacheFilter` | `servletGlobalCacheFilterFilterRegistrationBean()` | Request/Response 缓存 | 定义同类型 Bean |
| `CorsFilter`               | `corsFilterFilterRegistrationBean()`               | 跨域支持（非生产环境）         | 定义同类型 Bean |
| `ExceptionFilter`          | `exceptionFilterFilterRegistrationBean()`          | Filter 层异常处理        | 定义同类型 Bean |
| `GlobalParameterFilter`    | `globalParameterFilterFilterRegistrationBean()`    | 全局参数注入              | 定义同类型 Bean |
| `XssFilter`                | `xssFilterFilterRegistrationBean()`                | XSS 防护              | 定义同类型 Bean |

### 消息转换器与格式化

| Bean 类型                                  | 方法名                            | 说明               |
|------------------------------------------|--------------------------------|------------------|
| `MappingApiJackson2HttpMessageConverter` | `configureMessageConverters()` | JSON 序列化/反序列化    |
| `StringToDateConverter`                  | `addFormatters()`              | String → Date 转换 |
| `GlobalEnumConverterFactory`             | `addFormatters()`              | 枚举类型转换           |
| `StringToMapConverter`                   | `addFormatters()`              | String → Map 转换  |

## 配置规则

### 基础配置

```yaml
zeka-stack:
  rest:
    enabled: true
    read-timeout: 5000
    write-timeout: 5000
    connect-timeout: 3000
    enable-browser: false
    enable-container-log: false
    enable-http2: false
    enable-global-cache-filter: true
    enable-exception-filter: true
    enable-global-parameter-filter: false
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
| `zeka-stack.rest.enable-browser`                         | boolean | false                 | 启动后自动打开浏览器    |
| `zeka-stack.rest.enable-container-log`                   | boolean | false                 | Undertow 请求日志 |
| `zeka-stack.rest.enable-http2`                           | boolean | false                 | 启用 HTTP/2     |
| `zeka-stack.rest.enable-global-cache-filter`             | boolean | true                  | 全局缓存过滤器       |
| `zeka-stack.rest.enable-exception-filter`                | boolean | true                  | 全局异常过滤器       |
| `zeka-stack.rest.enable-global-parameter-filter`         | boolean | false                 | 全局参数注入过滤器     |
| `zeka-stack.rest.enable-entity-enum-all-field-serialize` | boolean | true                  | 枚举全字段序列化      |
| `zeka-stack.rest.json.date-format`                       | String  | `yyyy-MM-dd HH:mm:ss` | JSON 日期格式     |
| `zeka-stack.rest.json.time-zone`                         | String  | `Asia/Shanghai`       | JSON 时区       |
| `zeka-stack.rest.json.default-property-inclusion`        | String  | `non_null`            | 空值字段处理        |
| `zeka-stack.rest.multipart.location`                     | String  | 系统临时目录                | 文件上传临时目录      |

### 覆盖默认行为

如需禁用某个自动配置的组件，定义同类型 Bean 并返回 `null` 或使用 `@ConditionalOnProperty` 控制：

```java
@Configuration
public class MyRestConfig {

    // 禁用 XSS 防护
    @Bean
    public FilterRegistrationBean<XssFilter> xssFilterFilterRegistrationBean() {
        FilterRegistrationBean<XssFilter> bean = new FilterRegistrationBean<>();
        bean.setEnabled(false);
        return bean;
    }

    // 自定义当前用户服务
    @Bean
    @Override
    public CurrentUserService currentUserService() {
        return new CustomCurrentUserService();
    }
}
```

## 生产环境注意事项

1. **CORS 跨域**：仅在非生产环境自动开启，生产环境需自行配置
2. **参数验证**：生产环境使用 Hibernate Validator 快速失败模式（failFast=true）
3. **异常信息**：生产环境的详细异常信息仅写入 `extend` 字段，不直接返回给客户端
4. **日志输出**：生产环境应关闭不必要的容器日志和 SQL 日志

## 不要这样做

- 不要为单个接口临时创建与 Zeka Stack 不一致的响应包装类
- 不要直接返回杂乱的 `Map`、字符串或非统一错误结构作为异常响应
- 不要把 WebFlux 的 `Mono`、`Flux` 等响应式类型用于 Servlet Starter 代码
- 不要绕开 Cubo REST 的自动配置，手工拼装与 Starter 重复的基础设施
- 不要照搬通用 Spring Boot 写法而忽略 Zeka Stack 的统一响应、异常和校验约定
- 不要禁用生产环境的异常过滤器（`enable-exception-filter`）
