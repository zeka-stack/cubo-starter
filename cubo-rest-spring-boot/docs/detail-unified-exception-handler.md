---
published: 2022.04.04
---

# 统一异常处理

## 概述

统一异常处理是 `cubo-rest-spring-boot` 模块的核心特性之一，它通过全局异常处理器实现了对所有未捕获异常的统一处理和响应。该设计确保了所有异常都能被优雅地处理，并返回统一的错误响应格式，提高了系统的稳定性和用户体验。

## 设计目标

### 1. 统一错误响应

所有异常都返回统一的错误响应格式：

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
  },
  "timestamp": 1234567890,
  "traceId": "链路追踪ID"
}
```

### 2. 分层异常处理

采用分层异常处理策略，在不同层次提供统一的异常处理机制：

1. **Filter 层**：捕获 Servlet 容器异常
2. **Controller 层**：处理业务异常
3. **全局异常处理器**：兜底处理所有未捕获异常

### 3. 环境适配

- **开发环境**：返回详细的异常信息，包括堆栈跟踪
- **生产环境**：返回简化的错误信息，保护系统内部信息

## 核心组件

### 1. RestGlobalExceptionHandler

`RestGlobalExceptionHandler` 是 REST 模块的全局异常处理器，它继承自 `ServletGlobalExceptionHandler`，专门用于处理 REST API 中发生的各种异常。

#### 类结构

```java
@Slf4j
public class RestGlobalExceptionHandler extends ServletGlobalExceptionHandler {

    public RestGlobalExceptionHandler() {
        log.info("加载全局异常处理器: [{}]", RestGlobalExceptionHandler.class);
    }
}
```

#### 继承关系

```
ServletGlobalExceptionHandler (blen-kernel-web)
    ↓
RestGlobalExceptionHandler (cubo-rest-spring-boot)
```

#### 处理范围

- Spring MVC 框架异常（参数验证、类型转换等）
- 业务逻辑异常（自定义异常类）
- 系统级异常（数据库异常、网络异常等）
- HTTP 协议相关异常（404、5xx 等）

### 2. ServletGlobalExceptionHandler

`ServletGlobalExceptionHandler` 是基础异常处理器，提供了通用的异常处理能力：

#### 核心方法

```java
@RestControllerAdvice
public class ServletGlobalExceptionHandler {

    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {
        // 提取验证错误信息
        // 返回统一的错误响应
    }

    /**
     * 处理类型转换异常
     */
    @ExceptionHandler(TypeMismatchException.class)
    public Result<Void> handleTypeMismatchException(TypeMismatchException e) {
        // 处理类型转换错误
    }

    /**
     * 处理业务异常
     */
    @ExceptionHandler(LowestException.class)
    public Result<Void> handleLowestException(LowestException e) {
        // 处理框架自定义异常
    }

    /**
     * 处理所有未捕获的异常
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        // 兜底处理所有异常
    }
}
```

### 3. ServletExtendExceptionHandler

`ServletExtendExceptionHandler` 提供了扩展的异常处理能力，处理特定场景的异常：

```java
@RestControllerAdvice
public class ServletExtendExceptionHandler implements ZekaAutoConfiguration {

    /**
     * 处理数据库锁异常
     */
    @ExceptionHandler(value = {
        CannotAcquireLockException.class,
        PessimisticLockingFailureException.class,
        OptimisticLockingFailureException.class
    })
    public Result<Void> handleLockException(Exception e) {
        // 处理数据库锁相关异常
    }
}
```

### 4. JsonErrorWebExceptionHandler (Reactive)

`JsonErrorWebExceptionHandler` 是 WebFlux 环境下的全局异常处理器：

```java
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

## 异常处理流程

### 1. Servlet 环境异常处理流程

```
请求进入
    ↓
Filter 层异常捕获
    ↓
Controller 层异常捕获
    ↓
全局异常处理器处理
    ↓
返回统一错误响应
```

### 2. Reactive 环境异常处理流程

```
请求进入
    ↓
WebFilter 异常捕获
    ↓
Controller 层异常捕获
    ↓
ErrorWebExceptionHandler 处理
    ↓
返回统一错误响应
```

## 异常分类处理

### 1. 参数验证异常

#### MethodArgumentNotValidException

```java
@ExceptionHandler(MethodArgumentNotValidException.class)
public Result<Void> handleMethodArgumentNotValidException(
        MethodArgumentNotValidException e) {
    BindingResult bindingResult = e.getBindingResult();
    List<FieldError> fieldErrors = bindingResult.getFieldErrors();

    // 提取所有验证错误
    Map<String, String> errors = new HashMap<>();
    for (FieldError fieldError : fieldErrors) {
        errors.put(fieldError.getField(), fieldError.getDefaultMessage());
    }

    return Result.fail("VALIDATION_ERROR", "参数验证失败", errors);
}
```

#### 响应格式

```json
{
  "code": "VALIDATION_ERROR",
  "message": "参数验证失败",
  "success": false,
  "data": {
    "username": "用户名不能为空",
    "email": "邮箱格式不正确"
  }
}
```

### 2. 业务异常

#### LowestException

```java
@ExceptionHandler(LowestException.class)
public Result<Void> handleLowestException(LowestException e) {
    log.error("业务异常: [{}]", e.getMessage(), e);
    return Result.fail(e.getCode(), e.getMessage());
}
```

#### 响应格式

```json
{
  "code": "USER_NOT_FOUND",
  "message": "用户不存在",
  "success": false
}
```

### 3. 系统异常

#### Exception

```java
@ExceptionHandler(Exception.class)
public Result<Void> handleException(Exception e) {
    log.error("系统异常", e);

    // 开发环境返回详细错误信息
    if (!ConfigKit.isProd()) {
        return Result.fail("INTERNAL_ERROR", e.getMessage(), getStackTrace(e));
    }

    // 生产环境返回通用错误信息
    return Result.fail("INTERNAL_ERROR", "系统内部错误");
}
```

#### 响应格式（开发环境）

```json
{
  "code": "INTERNAL_ERROR",
  "message": "NullPointerException: ...",
  "success": false,
  "data": {
    "stackTrace": "java.lang.NullPointerException\n  at ..."
  }
}
```

#### 响应格式（生产环境）

```json
{
  "code": "INTERNAL_ERROR",
  "message": "系统内部错误",
  "success": false
}
```

## 环境适配

### 1. 开发环境

**特点**：

- 返回详细的异常信息
- 包含堆栈跟踪
- 包含请求参数和请求头
- 便于问题排查

**配置**：

```yaml
spring:
  profiles:
    active: dev
```

### 2. 生产环境

**特点**：

- 返回简化的错误信息
- 不包含堆栈跟踪
- 保护系统内部信息
- 避免信息泄露

**配置**：

```yaml
spring:
  profiles:
    active: prod
```

## 错误响应格式

### 1. 标准错误响应

```json
{
  "code": "错误码",
  "message": "错误信息",
  "success": false,
  "data": {
    "path": "/api/users/123",
    "method": "GET",
    "params": {},
    "headers": {
      "Content-Type": "application/json"
    },
    "traceId": "abc123def456"
  },
  "timestamp": 1704067200000,
  "traceId": "abc123def456"
}
```

### 2. 验证错误响应

```json
{
  "code": "VALIDATION_ERROR",
  "message": "参数验证失败",
  "success": false,
  "data": {
    "username": "用户名不能为空",
    "email": "邮箱格式不正确",
    "age": "年龄必须在18-100之间"
  }
}
```

### 3. 业务错误响应

```json
{
  "code": "USER_NOT_FOUND",
  "message": "用户不存在",
  "success": false,
  "data": null
}
```

## 自定义异常处理

### 1. 创建自定义异常

```java
public class BusinessException extends LowestException {

    public BusinessException(String code, String message) {
        super(code, message);
    }

    public BusinessException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
```

### 2. 使用自定义异常

```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            throw new BusinessException("USER_NOT_FOUND", "用户不存在");
        }
        return user;
    }
}
```

### 3. 扩展异常处理

```java
@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.error("业务异常: [{}]", e.getMessage(), e);
        return Result.fail(e.getCode(), e.getMessage());
    }
}
```

## 链路追踪集成

### 1. 自动添加 TraceId

所有错误响应都会自动包含 TraceId，便于问题排查：

```java
@Override
public Result<Void> handleException(Exception e) {
    String traceId = Trace.getTraceId();
    log.error("系统异常, traceId: [{}]", traceId, e);

    Result<Void> result = Result.fail("INTERNAL_ERROR", "系统内部错误");
    result.setTraceId(traceId);
    return result;
}
```

### 2. 日志关联

异常日志会自动关联 TraceId，便于在日志系统中追踪：

```
2024-01-01 10:00:00 ERROR [traceId: abc123def456] 系统异常
java.lang.NullPointerException
  at com.example.service.UserService.getUserById(UserService.java:123)
  ...
```

## 设计优势

### 1. 统一性

- **格式统一**：所有异常都返回统一的错误响应格式
- **处理统一**：前端可以使用统一的错误处理逻辑
- **日志统一**：所有异常都按照统一格式记录日志

### 2. 安全性

- **信息保护**：生产环境不暴露系统内部信息
- **敏感信息过滤**：自动过滤敏感信息
- **错误码管理**：统一的错误码管理机制

### 3. 可维护性

- **集中管理**：所有异常处理逻辑集中管理
- **易于扩展**：支持自定义异常处理
- **清晰明确**：异常处理流程清晰明确

### 4. 可观测性

- **链路追踪**：自动关联 TraceId
- **详细日志**：记录详细的异常信息
- **环境适配**：根据环境提供不同详细程度的信息

## 最佳实践

### 1. 异常分类

- **参数验证异常**：使用 `@Valid` 和 Bean Validation
- **业务异常**：使用自定义业务异常类
- **系统异常**：由全局异常处理器统一处理

### 2. 错误码管理

- 使用统一的错误码规范
- 错误码要有明确的含义
- 错误码要便于前端处理

### 3. 日志记录

- 记录详细的异常信息
- 关联 TraceId 便于追踪
- 区分不同级别的异常

### 4. 错误信息

- 提供有意义的错误信息
- 避免暴露敏感信息
- 根据环境提供不同详细程度的信息

## 注意事项

### 1. 性能考虑

- 异常处理会带来一定的性能开销
- 避免在异常处理中执行重量级操作
- 合理使用异常，不要滥用

### 2. 安全性

- 生产环境不要暴露堆栈跟踪
- 过滤敏感信息
- 避免信息泄露

### 3. 兼容性

- 与 Spring MVC 完全兼容
- 支持所有标准的异常类型
- 不影响正常的业务逻辑

## 总结

统一异常处理通过全局异常处理器实现了对所有异常的统一处理和响应，确保了所有异常都能被优雅地处理，并返回统一的错误响应格式。这种设计不仅提高了系统的稳定性，还简化了前端的错误处理逻辑，是构建高质量
REST API 的重要基础。

