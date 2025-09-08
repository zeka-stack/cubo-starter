# Cubo REST Spring Boot

## 概述

`cubo-rest-spring-boot` 是 Cubo Starter 项目的 REST API 模块，提供了完整的 RESTful API 开发支持。该模块支持 Servlet 和 Reactive 两种 Web
技术栈，提供了统一的异常处理、参数验证、响应封装等功能，大大简化了 REST API 的开发工作。

## 主要功能

### 1. 多 Web 技术栈支持

- **Servlet**: 基于 Spring MVC 的传统 Web 开发
- **Reactive**: 基于 Spring WebFlux 的响应式 Web 开发
- 统一的 API 接口和配置管理

### 2. 统一异常处理

- 全局异常处理器
- 统一的错误响应格式
- 支持自定义异常类型和错误码

### 3. 参数验证

- 基于 Bean Validation 的参数验证
- 生产环境下开启快速失败模式
- 统一的验证错误响应

### 4. 响应封装

- 统一的 API 响应格式
- 支持分页响应封装
- 提供响应状态码管理

### 5. HTTP 客户端支持

- RestTemplate 自动配置
- 支持多种 HTTP 客户端（OkHttp、Apache HttpClient）
- 连接池和超时配置

## 模块结构

```
cubo-rest-spring-boot/
├── cubo-rest-spring-boot-autoconfigure/    # 自动配置模块
├── cubo-rest-spring-boot-core/             # 核心功能模块
│   ├── cubo-rest-common/                   # 通用组件
│   ├── cubo-rest-servlet/                  # Servlet 实现
│   └── cubo-rest-reactive/                 # Reactive 实现
└── cubo-rest-spring-boot-starter/          # Starter 模块
    ├── cubo-rest-servlet-spring-boot-starter/
    └── cubo-rest-reactive-spring-boot-starter/
```

### 子模块说明

#### cubo-rest-spring-boot-autoconfigure

- **RestAutoConfiguration**: REST 主自动配置类
- **ServletAutoConfiguration**: Servlet Web 自动配置
- **WebFluxAutoConfiguration**: Reactive Web 自动配置
- **RestTemplateAutoConfiguration**: HTTP 客户端自动配置

#### cubo-rest-spring-boot-core

##### cubo-rest-common

- 提供通用的 REST 组件和工具类
- 定义统一的响应格式和异常类型
- 提供参数验证和序列化工具

##### cubo-rest-servlet

- 基于 Spring MVC 的 Servlet 实现
- 提供全局异常处理器
- 支持 Undertow 服务器配置

##### cubo-rest-reactive

- 基于 Spring WebFlux 的 Reactive 实现
- 提供响应式异常处理
- 支持非阻塞 I/O 操作

## 核心特性

### 1. 统一异常处理

#### Servlet 全局异常处理

```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException e) {
        log.error("参数验证失败", e);
        return ResponseEntity.badRequest()
            .body(ErrorResponse.builder()
                .code("VALIDATION_ERROR")
                .message("参数验证失败")
                .details(e.getMessage())
                .build());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        log.error("业务异常", e);
        return ResponseEntity.status(e.getHttpStatus())
            .body(ErrorResponse.builder()
                .code(e.getCode())
                .message(e.getMessage())
                .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        log.error("系统异常", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse.builder()
                .code("INTERNAL_ERROR")
                .message("系统内部错误")
                .build());
    }
}
```

#### Reactive 异常处理

```java
@Component
@Slf4j
public class ReactiveGlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleValidationException(ValidationException e) {
        log.error("参数验证失败", e);
        return Mono.just(ResponseEntity.badRequest()
            .body(ErrorResponse.builder()
                .code("VALIDATION_ERROR")
                .message("参数验证失败")
                .details(e.getMessage())
                .build()));
    }
}
```

### 2. 统一响应格式

#### 响应封装类

```java
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {

    private String code;
    private String message;
    private T data;
    private Long timestamp;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
            .code("SUCCESS")
            .message("操作成功")
            .data(data)
            .timestamp(System.currentTimeMillis())
            .build();
    }

    public static <T> ApiResponse<T> error(String code, String message) {
        return ApiResponse.<T>builder()
            .code(code)
            .message(message)
            .timestamp(System.currentTimeMillis())
            .build();
    }
}
```

#### 分页响应

```java
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse<T> {

    private List<T> records;
    private Long total;
    private Long current;
    private Long size;
    private Long pages;

    public static <T> PageResponse<T> of(Page<T> page) {
        return PageResponse.<T>builder()
            .records(page.getRecords())
            .total(page.getTotal())
            .current(page.getCurrent())
            .size(page.getSize())
            .pages(page.getPages())
            .build();
    }
}
```

### 3. 参数验证

#### 验证配置

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

#### 验证注解使用

```java
@Data
public class UserCreateRequest {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20个字符之间")
    private String username;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @NotNull(message = "年龄不能为空")
    @Min(value = 18, message = "年龄不能小于18岁")
    @Max(value = 100, message = "年龄不能大于100岁")
    private Integer age;
}
```

### 4. HTTP 客户端配置

#### RestTemplate 配置

```java
@Bean
@ConditionalOnClass(OkHttpClient.class)
public ClientHttpRequestFactory clientHttpRequestFactory(RestProperties restProperties) {
    OkHttpClient okHttpClient = this.getUnsafeOkHttpClient()
        .newBuilder()
        .connectTimeout(restProperties.getConnectTimeout(), TimeUnit.MILLISECONDS)
        .readTimeout(restProperties.getReadTimeout(), TimeUnit.MILLISECONDS)
        .writeTimeout(restProperties.getWriteTimeout(), TimeUnit.MILLISECONDS)
        .build();

    return new OkHttpClientHttpRequestFactory(okHttpClient);
}

@Bean
@ConditionalOnMissingBean
public RestTemplate restTemplate(ClientHttpRequestFactory requestFactory) {
    RestTemplate restTemplate = new RestTemplate(requestFactory);
    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
    return restTemplate;
}
```

## 配置属性

### RestProperties

| 属性名                                | 类型      | 默认值   | 说明               |
|------------------------------------|---------|-------|------------------|
| `zeka-stack.rest.enabled`          | boolean | true  | 是否启用 REST 功能     |
| `zeka-stack.rest.servlet.enabled`  | boolean | true  | 是否启用 Servlet 支持  |
| `zeka-stack.rest.reactive.enabled` | boolean | true  | 是否启用 Reactive 支持 |
| `zeka-stack.rest.enable-browser`   | boolean | false | 是否启用浏览器自动打开      |
| `zeka-stack.rest.connect-timeout`  | long    | 5000  | 连接超时时间（毫秒）       |
| `zeka-stack.rest.read-timeout`     | long    | 10000 | 读取超时时间（毫秒）       |
| `zeka-stack.rest.write-timeout`    | long    | 10000 | 写入超时时间（毫秒）       |

### Undertow 配置

| 属性名                              | 类型      | 默认值  | 说明        |
|----------------------------------|---------|------|-----------|
| `server.undertow.threads.worker` | int     | 200  | 工作线程数     |
| `server.undertow.threads.io`     | int     | 8    | IO 线程数    |
| `server.undertow.buffer-size`    | int     | 1024 | 缓冲区大小     |
| `server.undertow.direct-buffers` | boolean | true | 是否使用直接缓冲区 |

## 使用方式

### 1. 引入依赖

**使用 Servlet**

```xml
<dependency>
    <groupId>dev.dong4j</groupId>
    <artifactId>cubo-rest-servlet-spring-boot-starter</artifactId>
</dependency>
```

**使用 Reactive**

```xml
<dependency>
    <groupId>dev.dong4j</groupId>
    <artifactId>cubo-rest-reactive-spring-boot-starter</artifactId>
</dependency>
```

### 2. 配置启用

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

server:
  port: 8080
  undertow:
    threads:
      worker: 200
      io: 8
    buffer-size: 1024
    direct-buffers: true
```

### 3. 编写 Controller

#### Servlet Controller

```java
@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ApiResponse<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ApiResponse.success(user);
    }

    @PostMapping
    public ApiResponse<User> createUser(@RequestBody @Valid UserCreateRequest request) {
        User user = userService.createUser(request);
        return ApiResponse.success(user);
    }

    @GetMapping
    public ApiResponse<PageResponse<User>> getUsers(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size) {
        Page<User> page = userService.getUsers(current, size);
        return ApiResponse.success(PageResponse.of(page));
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
    public Mono<ApiResponse<User>> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
            .map(ApiResponse::success);
    }

    @PostMapping
    public Mono<ApiResponse<User>> createUser(@RequestBody @Valid UserCreateRequest request) {
        return userService.createUser(request)
            .map(ApiResponse::success);
    }

    @GetMapping
    public Mono<ApiResponse<PageResponse<User>>> getUsers(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size) {
        return userService.getUsers(current, size)
            .map(PageResponse::of)
            .map(ApiResponse::success);
    }
}
```

### 4. 使用 HTTP 客户端

```java
@Service
public class ExternalApiService {

    @Autowired
    private RestTemplate restTemplate;

    public User getUserFromExternalApi(Long id) {
        String url = "https://api.example.com/users/" + id;
        ResponseEntity<ApiResponse<User>> response = restTemplate.getForEntity(
            url,
            new ParameterizedTypeReference<ApiResponse<User>>() {}
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody().getData();
        }
        throw new RuntimeException("获取用户信息失败");
    }
}
```

## 高级功能

### 1. 自定义异常处理

```java
@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(CustomBusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomBusinessException(CustomBusinessException e) {
        return ResponseEntity.status(e.getHttpStatus())
            .body(ApiResponse.error(e.getCode(), e.getMessage()));
    }
}
```

### 2. 请求日志记录

```java
@Component
@Slf4j
public class RequestLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                        FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        long startTime = System.currentTimeMillis();

        try {
            chain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            log.info("请求: {} {} 耗时: {}ms",
                httpRequest.getMethod(),
                httpRequest.getRequestURI(),
                duration);
        }
    }
}
```

### 3. 响应压缩

```java
@Configuration
public class CompressionConfig {

    @Bean
    public FilterRegistrationBean<CompressionFilter> compressionFilter() {
        FilterRegistrationBean<CompressionFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new CompressionFilter());
        registration.addUrlPatterns("/*");
        registration.setName("compressionFilter");
        registration.setOrder(1);
        return registration;
    }
}
```

### 4. CORS 配置

```java
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
```

## 最佳实践

### 1. API 设计

- 使用 RESTful 风格的 URL 设计
- 合理使用 HTTP 状态码
- 提供清晰的错误信息
- 保持 API 的向后兼容性

### 2. 异常处理

- 定义清晰的异常层次结构
- 提供有意义的错误码和消息
- 记录详细的错误日志
- 避免暴露敏感信息

### 3. 性能优化

- 合理使用缓存
- 优化数据库查询
- 使用异步处理提高响应速度
- 配置合适的连接池参数

### 4. 安全考虑

- 实现适当的认证和授权
- 验证所有输入参数
- 防止 SQL 注入和 XSS 攻击
- 使用 HTTPS 传输敏感数据

## 注意事项

1. **性能影响**: 全局异常处理会影响性能，需要合理使用
2. **内存使用**: 大量请求可能导致内存压力，需要监控
3. **线程安全**: 确保共享资源的线程安全
4. **版本兼容**: 注意与 Spring Boot 版本的兼容性

## 相关链接

- [Spring Boot Web 开发](https://docs.spring.io/spring-boot/docs/current/reference/html/web.html)
- [Spring WebFlux 官方文档](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html)
- [Bean Validation 规范](https://beanvalidation.org/)
- [Undertow 官方文档](https://undertow.io/)
