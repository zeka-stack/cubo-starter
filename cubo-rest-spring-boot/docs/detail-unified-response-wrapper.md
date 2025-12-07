---
published: 2022.04.04
---

# 统一响应封装

## 概述

统一响应封装是 `cubo-rest-spring-boot` 模块的核心特性之一，它通过 `ResponseWrapperAdvice` 实现了对 Controller 方法返回值的自动包装，确保所有
API 接口都返回一致的响应结构。这种设计大大简化了前端对 API 响应的处理，提高了开发效率和代码质量。

## 设计目标

### 1. 统一响应格式

所有 API 接口返回统一的响应结构，包含以下字段：

```json
{
  "code": "响应码",
  "message": "响应消息",
  "data": "响应数据",
  "success": true/false,
  "timestamp": 1234567890,
  "traceId": "链路追踪ID"
}
```

### 2. 自动化包装

- **零侵入**：无需在每个 Controller 方法中手动包装返回值
- **智能识别**：自动识别需要包装的方法，避免重复包装
- **灵活控制**：支持通过注解跳过包装，满足特殊场景需求

### 3. 性能优化

- **早期过滤**：在 `supports` 方法中快速判断是否需要包装
- **避免重复**：智能检测已包装的结果，避免重复包装
- **条件执行**：只在需要时执行包装逻辑，减少性能开销

## 核心组件

### 1. ResponseWrapperAdvice

`ResponseWrapperAdvice` 是统一响应封装的核心实现类，它实现了 Spring MVC 的 `ResponseBodyAdvice` 接口。

#### 工作原理

```java
@RestControllerAdvice
@ConditionalOnClass(Servlet.class)
public class ResponseWrapperAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType,
                           Class<? extends HttpMessageConverter<?>> converterType) {
        // 判断是否需要包装
        return RestUtils.zekaClass(returnType) && RestUtils.supportsAdvice(returnType);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType,
                                MediaType selectedContentType,
                                Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                ServerHttpRequest request, ServerHttpResponse response) {
        // 执行包装逻辑
        if (RestUtils.isOriginalResponse(returnType) ||
            !MediaType.APPLICATION_JSON.isCompatibleWith(selectedContentType)) {
            return body;
        }
        return wrapper(body);
    }
}
```

#### 判断逻辑

**需要包装的条件**：

1. **类路径检查**：Controller 类必须在框架的包路径下（`dev.dong4j.zeka`）
2. **注解检查**：Controller 或方法使用了以下注解之一：
    - `@RestController`
    - `@RestControllerWrapper`
    - `@ResponseWrapper`
    - `@ResponseBody`

**跳过包装的条件**：

1. **@OriginalResponse 注解**：方法使用了 `@OriginalResponse` 注解
2. **非 JSON 响应**：Content-Type 不是 `application/json`（如文件下载、图片返回等）
3. **已包装结果**：返回值已经是 `Result` 类型

### 2. RestUtils 工具类

`RestUtils` 提供了判断是否需要包装的工具方法：

```java
public class RestUtils {
    /**
     * 判断是否为框架内的类
     */
    public static boolean zekaClass(MethodParameter returnType) {
        // 检查类路径是否在框架包下
    }

    /**
     * 判断是否支持响应包装
     */
    public static boolean supportsAdvice(MethodParameter returnType) {
        // 检查是否使用了支持的注解
    }

    /**
     * 判断是否跳过包装
     */
    public static boolean isOriginalResponse(MethodParameter returnType) {
        // 检查是否使用了 @OriginalResponse 注解
    }
}
```

### 3. 响应包装注解

#### @ResponseWrapper

显式指定需要包装的注解：

```java
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResponseWrapper {
    /**
     * 是否启用包装，默认为 true
     */
    boolean value() default true;
}
```

#### @OriginalResponse

跳过自动包装的注解：

```java
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OriginalResponse {
    /**
     * 跳过自动包装的原因说明
     */
    String value() default "";
}
```

#### @RestControllerWrapper

组合注解，同时包含 `@RestController` 和 `@ResponseWrapper`：

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RestController
@ResponseWrapper
public @interface RestControllerWrapper {
    /**
     * Controller 名称
     */
    String value() default "";
}
```

## 包装策略

### 1. 包装规则

| 返回值类型            | 包装结果              | 说明                |
|------------------|-------------------|-------------------|
| `null`           | `R.succeed()`     | 成功但无数据            |
| 非 null 普通对象      | `R.succeed(data)` | 成功并返回数据           |
| `Result` 类型      | 直接返回              | 避免重复包装            |
| `ResponseEntity` | 保持原样              | 需要自定义 HTTP 状态码时使用 |

### 2. 包装实现

```java
private Object wrapper(Object body) {
    if (body instanceof Result) {
        // 已经是 Result 类型，直接返回
        log.trace("请求响应已使用 Result 包装, 原始响应: [{}]", body);
        return body;
    } else {
        if (body == null) {
            // null 值包装为成功但无数据的结果
            return R.succeed();
        }
        // 非 null 值包装为成功并返回数据的结果
        log.trace("重写请求响应, 使用 Result 包装, 原始响应: [{}]", body);
        return R.succeed(body);
    }
}
```

## 使用示例

### 1. 基本使用

```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        // 返回值会自动包装为 Result<User>
        return userService.getUserById(id);
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        // 返回值会自动包装为 Result<User>
        return userService.createUser(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        // void 返回值会包装为 Result<Void>
        userService.deleteUser(id);
    }
}
```

### 2. 跳过包装

```java
@RestController
@RequestMapping("/api/files")
public class FileController {

    @GetMapping("/download")
    @OriginalResponse("文件下载需要返回原始响应")
    public ResponseEntity<Resource> downloadFile(@RequestParam String fileName) {
        // 使用 @OriginalResponse 跳过自动包装
        Resource resource = fileService.getFile(fileName);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
            .body(resource);
    }
}
```

### 3. 手动包装

```java
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @GetMapping("/{id}")
    public Result<Order> getOrderById(@PathVariable Long id) {
        // 手动返回 Result，不会被重复包装
        Order order = orderService.getOrderById(id);
        return Result.success(order);
    }

    @PostMapping
    public Result<Order> createOrder(@RequestBody OrderCreateRequest request) {
        // 可以自定义响应码和消息
        Order order = orderService.createOrder(request);
        return Result.success(order, "订单创建成功");
    }
}
```

## 响应格式说明

### 1. 成功响应

```json
{
  "code": "2000",
  "message": "操作成功",
  "data": {
    "id": 1,
    "name": "张三",
    "email": "zhangsan@example.com"
  },
  "success": true,
  "timestamp": 1704067200000,
  "traceId": "abc123def456"
}
```

### 2. 空数据响应

```json
{
  "code": "2000",
  "message": "操作成功",
  "data": null,
  "success": true,
  "timestamp": 1704067200000,
  "traceId": "abc123def456"
}
```

### 3. 分页响应

```json
{
  "code": "2000",
  "message": "操作成功",
  "data": {
    "records": [
      {"id": 1, "name": "张三"},
      {"id": 2, "name": "李四"}
    ],
    "total": 100,
    "current": 1,
    "size": 10,
    "pages": 10
  },
  "success": true,
  "timestamp": 1704067200000,
  "traceId": "abc123def456"
}
```

## 设计优势

### 1. 统一性

- **格式统一**：所有 API 返回相同的响应结构
- **处理统一**：前端可以使用统一的响应处理逻辑
- **错误处理统一**：异常和正常响应都遵循相同格式

### 2. 易用性

- **零配置**：引入依赖即可使用，无需额外配置
- **零侵入**：不需要修改现有 Controller 代码
- **灵活控制**：支持通过注解精确控制包装行为

### 3. 性能

- **早期过滤**：在 `supports` 方法中快速判断，减少不必要的处理
- **避免重复**：智能检测已包装结果，避免重复包装
- **条件执行**：只在需要时执行包装逻辑

### 4. 可维护性

- **集中管理**：所有包装逻辑集中在一个类中
- **易于扩展**：支持自定义包装策略
- **清晰明确**：通过注解明确表达意图

## 注意事项

### 1. 性能考虑

- 包装操作会带来一定的性能开销，但影响很小
- 对于高频接口，可以考虑使用 `@OriginalResponse` 跳过包装
- 生产环境建议关闭 TRACE 级别日志

### 2. 兼容性

- 与 Spring MVC 完全兼容
- 支持所有标准的 Spring MVC 注解
- 不影响文件下载、流式响应等特殊场景

### 3. 扩展性

- 可以通过继承 `ResponseWrapperAdvice` 自定义包装逻辑
- 支持自定义 `Result` 类型
- 支持自定义响应字段

## 最佳实践

### 1. 统一使用

- 建议所有 REST API 都使用统一响应格式
- 避免在部分接口中手动包装，部分接口自动包装

### 2. 合理使用注解

- 普通接口：使用默认的自动包装
- 文件下载：使用 `@OriginalResponse` 跳过包装
- 特殊场景：手动返回 `Result` 类型

### 3. 错误处理

- 异常响应也会被统一处理，确保格式一致
- 配合全局异常处理器使用，效果更佳

## 总结

统一响应封装通过 `ResponseWrapperAdvice` 实现了对 API 响应的自动包装，确保了所有接口返回一致的响应格式。这种设计不仅提高了开发效率，还简化了前端的处理逻辑，是构建高质量
REST API 的重要基础。

