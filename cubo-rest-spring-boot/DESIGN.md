# Cubo REST Spring Boot 组件设计文档

## 概述

`cubo-rest-spring-boot` 是 Zeka Stack 项目中的 REST API 模块，提供了完整的 RESTful API 开发支持。该模块采用分层架构设计，支持 Servlet 和
Reactive 两种 Web 技术栈，提供了统一的异常处理、参数验证、响应封装等功能，大大简化了 REST API 的开发工作。

## 1. 架构设计

### 1.1 分层架构

该模块采用了经典的三层架构设计，体现了**关注点分离**和**职责单一**的设计原则：

```
cubo-rest-spring-boot/
├── cubo-rest-spring-boot-core/             # 核心功能层
│   ├── cubo-rest-common/                   # 通用组件
│   ├── cubo-rest-servlet/                  # Servlet 实现
│   └── cubo-rest-reactive/                 # Reactive 实现
├── cubo-rest-spring-boot-autoconfigure/    # 自动配置层
└── cubo-rest-spring-boot-starter/          # 启动器层
    ├── cubo-rest-servlet-spring-boot-starter/
    └── cubo-rest-reactive-spring-boot-starter/
```

#### 各层职责

| 层级                 | 职责              | 特点                   |
|--------------------|-----------------|----------------------|
| **Core层**          | 纯业务逻辑实现         | 不依赖Spring Boot，可独立使用 |
| **Autoconfigure层** | Spring Boot集成逻辑 | 处理自动装配、条件装配          |
| **Starter层**       | 依赖管理            | 简化用户配置，只包含pom.xml    |

#### 设计优势

1. **职责分离清晰**：每层都有明确的职责边界
2. **复用性强**：Core层可在非Spring Boot环境中使用
3. **扩展性好**：支持用户自定义覆盖默认配置
4. **维护性强**：模块化设计便于维护和升级

### 1.2 双技术栈支持

#### Servlet 技术栈

- **基础框架**：Spring MVC + Servlet API
- **适用场景**：传统 Web 应用、REST API 服务
- **特点**：同步阻塞 I/O，简单易用

#### Reactive 技术栈

- **基础框架**：Spring WebFlux + Reactive Streams
- **适用场景**：高并发应用、微服务网关、流式数据处理
- **特点**：异步非阻塞 I/O，高性能

## 2. 核心特性

### 2.1 统一异常处理

#### 设计理念

采用**分层异常处理**策略，在不同层次提供统一的异常处理机制：

1. **Filter 层**：捕获 Servlet 容器异常
2. **Controller 层**：处理业务异常
3. **全局异常处理器**：兜底处理所有未捕获异常

#### Servlet 异常处理

```java
@RestControllerAdvice
@Slf4j
public class RestGlobalExceptionHandler extends ServletGlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public Result<Void> handleValidationException(ValidationException e) {
        log.error("参数验证失败", e);
        return Result.fail("VALIDATION_ERROR", "参数验证失败");
    }

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.error("业务异常", e);
        return Result.fail(e.getCode(), e.getMessage());
    }
}
```

#### Reactive 异常处理

```java
@Component
@Slf4j
public class JsonErrorWebExceptionHandler extends DefaultErrorWebExceptionHandler {

    @Override
    protected Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        // 统一 JSON 格式错误响应
        Map<String, Object> error = this.getErrorAttributes(request, includeStackTrace);
        return ServerResponse.status(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(error));
    }
}
```

#### 错误响应格式

```json
{
  "code": "错误码",
  "message": "错误信息",
  "success": false,
  "data": {
    "path": "请求路径",
    "method": "请求方法",
    "params": "请求参数",
    "headers": "请求头",
    "stackTrace": "堆栈跟踪（仅开发环境）",
    "traceId": "链路追踪ID"
  }
}
```

### 2.2 统一响应封装

#### 响应格式设计

```java
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {
    private String code;
    private String message;
    private T data;
    private Boolean success;
    private Long timestamp;
    private String traceId;
}
```

#### 自动响应包装

通过 `ResponseWrapperAdvice` 实现自动响应包装：

```java
@RestControllerAdvice
public class ResponseWrapperAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 判断是否需要包装响应
        return !RestUtils.zekaClass(returnType) && RestUtils.supportsAdvice(returnType);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType,
                                MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                ServerHttpRequest request, ServerHttpResponse response) {
        // 自动包装为统一响应格式
        return Result.success(body);
    }
}
```

### 2.3 参数验证机制

#### 生产环境快速失败

```java
@Bean
@Profile(value = {App.ENV_PROD})
public Validator validator() {
    log.info("参数验证开启快速失败模式");
    try (ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
        .configure()
        .failFast(true)  // 快速失败模式
        .messageInterpolator(new ParameterMessageInterpolator())
        .buildValidatorFactory()) {
        return validatorFactory.getValidator();
    }
}
```

#### 自定义参数解析器

支持多种自定义注解的参数解析：

- `@CurrentUser`：当前用户信息注入
- `@RequestSingleParam`：单个参数提取
- `@RequestAbstractForm`：抽象表单绑定
- `@FormDataBody`：表单数据绑定

### 2.4 HTTP 客户端配置

#### OkHttp 连接池配置

```java
@Bean
@ConditionalOnClass(OkHttpClient.class)
public ClientHttpRequestFactory clientHttpRequestFactory(RestProperties restProperties) {
    OkHttpClient okHttpClient = this.getUnsafeOkHttpClient()
        .newBuilder()
        .connectTimeout(restProperties.getConnectTimeout(), TimeUnit.MILLISECONDS)
        .readTimeout(restProperties.getReadTimeout(), TimeUnit.MILLISECONDS)
        .writeTimeout(restProperties.getWriteTimeout(), TimeUnit.MILLISECONDS)
        .connectionPool(new ConnectionPool(
            restProperties.getConnectionPool().getMaxIdleConnections(),
            restProperties.getConnectionPool().getKeepAliveDuration(),
            TimeUnit.MINUTES
        ))
        .build();

    return new OkHttpClientHttpRequestFactory(okHttpClient);
}
```

#### 连接池配置属性

```yaml
zeka-stack:
  rest:
    connection-pool:
      max-idle-connections: 5      # 最大空闲连接数
      keep-alive-duration: 5      # 连接保活时间（分钟）
    connect-timeout: 3000         # 连接超时（毫秒）
    read-timeout: 5000            # 读取超时（毫秒）
    write-timeout: 5000           # 写入超时（毫秒）
```

## 3. 高级特性

### 3.1 XSS 防护

#### 多层防护机制

1. **Filter 层防护**：`XssFilter` 在请求入口进行过滤
2. **Request Wrapper**：`XssHttpServletRequestWrapper` 包装请求对象
3. **HTML 过滤器**：`HtmlFilter` 提供专业的 HTML 过滤功能

#### XSS 过滤实现

```java
public class XssHttpServletRequestWrapper extends CacheRequestEnhanceWrapper {
    private static final HtmlFilter HTML_FILTER = new HtmlFilter();

    @Override
    public String getParameter(String name) {
        String value = super.getParameter(xssEncode(name));
        if (StringUtils.isNotBlank(value)) {
            value = xssEncode(value);
        }
        return value;
    }

    private static String xssEncode(String input) {
        return HTML_FILTER.filter(cleanSqlKeyWords(input));
    }
}
```

### 3.2 API 版本控制

#### 版本控制注解

```java
@ApiVersion({1, 2})
@RestController
public class UserController {

    @ApiVersion(3)
    @GetMapping("/users")
    public List<User> getUsers() {
        // 该方法只对 v3 版本生效
    }
}
```

#### 版本路由映射

- `/v1/users` - 访问版本 1 的 API
- `/v2/users` - 访问版本 2 的 API
- `/v3/users` - 访问版本 3 的 API

### 3.3 链路追踪

#### 自动链路追踪

```java
@Component
public class TraceInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 生成或获取链路追踪ID
        String traceId = TracerUtils.getOrGenerateTraceId(request);
        MDC.put("traceId", traceId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                              Object handler, Exception ex) {
        // 清理链路追踪上下文
        MDC.clear();
    }
}
```

### 3.4 用户认证

#### Token 认证拦截器

```java
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            TokenRequired tokenRequired = handlerMethod.getMethodAnnotation(TokenRequired.class);

            if (tokenRequired != null) {
                // 验证 Token 并解析用户信息
                CurrentUser currentUser = currentUserService.getCurrentUser(request);
                if (currentUser == null) {
                    throw new UnauthorizedException("Token 验证失败");
                }
            }
        }
        return true;
    }
}
```

#### 当前用户注入

```java
@GetMapping("/profile")
public Result<User> getProfile(@CurrentUser CurrentUser currentUser) {
    // 自动注入当前用户信息
    return Result.success(userService.getUserById(currentUser.getUserId()));
}
```

## 4. 配置属性

### 4.1 基础配置

| 属性名                                | 类型      | 默认值   | 说明               |
|------------------------------------|---------|-------|------------------|
| `zeka-stack.rest.enabled`          | boolean | true  | 是否启用 REST 功能     |
| `zeka-stack.rest.servlet.enabled`  | boolean | true  | 是否启用 Servlet 支持  |
| `zeka-stack.rest.reactive.enabled` | boolean | true  | 是否启用 Reactive 支持 |
| `zeka-stack.rest.enable-browser`   | boolean | false | 是否启用浏览器自动打开      |

### 4.2 超时配置

| 属性名                               | 类型   | 默认值  | 说明         |
|-----------------------------------|------|------|------------|
| `zeka-stack.rest.connect-timeout` | long | 3000 | 连接超时时间（毫秒） |
| `zeka-stack.rest.read-timeout`    | long | 5000 | 读取超时时间（毫秒） |
| `zeka-stack.rest.write-timeout`   | long | 5000 | 写入超时时间（毫秒） |

### 4.3 连接池配置

| 属性名                                                    | 类型  | 默认值 | 说明         |
|--------------------------------------------------------|-----|-----|------------|
| `zeka-stack.rest.connection-pool.max-idle-connections` | int | 5   | 最大空闲连接数    |
| `zeka-stack.rest.connection-pool.keep-alive-duration`  | int | 5   | 连接保活时间（分钟） |

### 4.4 功能开关

| 属性名                                              | 类型      | 默认值   | 说明            |
|--------------------------------------------------|---------|-------|---------------|
| `zeka-stack.rest.enable-global-cache-filter`     | boolean | true  | 是否启用全局缓存过滤器   |
| `zeka-stack.rest.enable-exception-filter`        | boolean | true  | 是否启用全局异常过滤器   |
| `zeka-stack.rest.enable-global-parameter-filter` | boolean | false | 是否启用全局参数注入过滤器 |
| `zeka-stack.rest.xss.enable-xss-filter`          | boolean | true  | 是否启用 XSS 过滤器  |

## 5. 使用指南

### 5.1 引入依赖

#### Servlet 项目

```xml
<dependency>
    <groupId>dev.dong4j</groupId>
    <artifactId>cubo-rest-servlet-spring-boot-starter</artifactId>
</dependency>
```

#### Reactive 项目

```xml
<dependency>
    <groupId>dev.dong4j</groupId>
    <artifactId>cubo-rest-reactive-spring-boot-starter</artifactId>
</dependency>
```

### 5.2 基础配置

```yaml
zeka-stack:
  rest:
    enabled: true
    servlet:
      enabled: true
    reactive:
      enabled: true
    enable-browser: false
    connect-timeout: 5000
    read-timeout: 10000
    write-timeout: 10000
    connection-pool:
      max-idle-connections: 20
      keep-alive-duration: 10
```

### 5.3 编写 Controller

#### Servlet Controller

```java
@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return Result.success(user);
    }

    @PostMapping
    public Result<User> createUser(@RequestBody @Valid UserCreateRequest request) {
        User user = userService.createUser(request);
        return Result.success(user);
    }

    @GetMapping
    @TokenRequired
    public Result<PageResponse<User>> getUsers(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @CurrentUser CurrentUser currentUser) {
        Page<User> page = userService.getUsers(current, size);
        return Result.success(PageResponse.of(page));
    }
}
```

#### Reactive Controller

```java
@RestController
@RequestMapping("/api/users")
@Validated
public class ReactiveUserController {

    @Autowired
    private ReactiveUserService userService;

    @GetMapping("/{id}")
    public Mono<Result<User>> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
            .map(Result::success);
    }

    @PostMapping
    public Mono<Result<User>> createUser(@RequestBody @Valid UserCreateRequest request) {
        return userService.createUser(request)
            .map(Result::success);
    }
}
```

### 5.4 使用 HTTP 客户端

```java
@Service
public class ExternalApiService {

    @Autowired
    private RestTemplate restTemplate;

    public User getUserFromExternalApi(Long id) {
        String url = "https://api.example.com/users/" + id;
        ResponseEntity<Result<User>> response = restTemplate.getForEntity(
            url,
            new ParameterizedTypeReference<Result<User>>() {}
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody().getData();
        }
        throw new RuntimeException("获取用户信息失败");
    }
}
```

## 6. 最佳实践

### 6.1 API 设计

- 使用 RESTful 风格的 URL 设计
- 合理使用 HTTP 状态码
- 提供清晰的错误信息
- 保持 API 的向后兼容性

### 6.2 异常处理

- 定义清晰的异常层次结构
- 提供有意义的错误码和消息
- 记录详细的错误日志
- 避免暴露敏感信息

### 6.3 性能优化

- 合理使用缓存
- 优化数据库查询
- 使用异步处理提高响应速度
- 配置合适的连接池参数

### 6.4 安全考虑

- 实现适当的认证和授权
- 验证所有输入参数
- 防止 SQL 注入和 XSS 攻击
- 使用 HTTPS 传输敏感数据

## 7. 技术亮点

### 7.1 双技术栈支持

- **统一 API**：Servlet 和 Reactive 使用相同的注解和配置
- **自动适配**：根据项目依赖自动选择合适的技术栈
- **无缝切换**：可以轻松在不同技术栈间切换

### 7.2 智能异常处理

- **分层处理**：Filter、Controller、全局三层异常处理
- **环境适配**：开发环境详细错误，生产环境简化错误
- **链路追踪**：自动关联请求上下文，便于问题排查

### 7.3 高性能 HTTP 客户端

- **连接池优化**：支持连接复用和保活机制
- **SSL 支持**：内置 SSL 证书忽略功能
- **超时配置**：细粒度的超时控制

### 7.4 安全防护

- **XSS 防护**：多层 XSS 攻击防护机制
- **参数验证**：生产环境快速失败模式
- **Token 认证**：基于注解的 Token 认证机制

## 8. 总结

`cubo-rest-spring-boot` 模块通过精心设计的架构和丰富的功能特性，为 Spring Boot 应用提供了完整的 REST API 开发解决方案。其双技术栈支持、统一异常处理、智能参数验证、高性能
HTTP 客户端等特性，大大简化了 REST API 的开发工作，提高了开发效率和代码质量。

该模块不仅解决了 REST API 开发中的常见问题，还提供了许多高级特性，如 XSS 防护、API 版本控制、链路追踪等，为构建高质量的企业级应用提供了强有力的支持。
