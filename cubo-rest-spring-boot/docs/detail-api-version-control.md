---
published: 2022.04.04
---

# API 版本控制

## 概述

API 版本控制是 `cubo-rest-spring-boot` 模块的重要特性之一，它通过 `@ApiVersion` 注解和 `ApiVersionRequestMappingHandlerMapping` 实现了 REST
API 的版本化管理和路由。这种设计使得同一个 API 的多个版本可以并存，方便 API 的向下兼容和灰度升级。

## 设计目标

### 1. URL 路径版本化

- 支持在 URL 路径中加入版本号（如 `/v1/users`、`/v2/users`）
- 支持多个版本号的批量指定
- 支持类级别和方法级别的版本控制

### 2. 向下兼容

- 支持多个版本并存
- 新版本不影响旧版本
- 方便灰度升级和逐步迁移

### 3. 灵活配置

- 支持在类和方法级别使用
- 方法级别优先级高于类级别
- 支持版本范围映射

## 核心组件

### 1. @ApiVersion 注解

`@ApiVersion` 注解用于标记 REST API 的版本信息：

```java
@Mapping
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface ApiVersion {
    /**
     * 指定 API 的版本号数组
     *
     * 可以指定一个或多个版本号，表示当前 API 支持的版本范围。
     * 当指定多个版本时，该 API 会在所有指定的版本下都可以访问。
     *
     * @return API 支持的版本号数组
     */
    int[] value() default 1;
}
```

#### 使用方式

**类级别版本控制**：

```java
@ApiVersion({1, 2})
@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping
    public List<User> getUsers() {
        // 该方法在 v1 和 v2 版本下都可以访问
        // URL: /v1/users 或 /v2/users
        return userService.getUsers();
    }
}
```

**方法级别版本控制**：

```java
@RestController
@RequestMapping("/users")
public class UserController {

    @ApiVersion(1)
    @GetMapping
    public List<User> getUsersV1() {
        // 该方法只在 v1 版本下可以访问
        // URL: /v1/users
        return userService.getUsersV1();
    }

    @ApiVersion(2)
    @GetMapping
    public List<User> getUsersV2() {
        // 该方法只在 v2 版本下可以访问
        // URL: /v2/users
        return userService.getUsersV2();
    }
}
```

**混合使用**：

```java
@ApiVersion({1, 2})
@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping
    public List<User> getUsers() {
        // 继承类级别的版本，在 v1 和 v2 版本下都可以访问
        return userService.getUsers();
    }

    @ApiVersion(3)
    @GetMapping
    public List<User> getUsersV3() {
        // 方法级别覆盖类级别，只在 v3 版本下可以访问
        // URL: /v3/users
        return userService.getUsersV3();
    }
}
```

### 2. ApiVersionRequestMappingHandlerMapping

`ApiVersionRequestMappingHandlerMapping` 是自定义的请求映射处理器，它扩展了 Spring MVC 的 `RequestMappingHandlerMapping`，实现了版本化的 URL
路径映射。

#### 核心逻辑

```java
@Slf4j
public class ApiVersionRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

    private static final String VERSION_PATTERN = "/v%d";

    @Override
    protected void registerHandlerMethod(Object handler, Method method, RequestMappingInfo mapping) {
        Class<?> clazz = handler instanceof String ?
            EarlySpringContext.getInstance(handler.toString()).getClass() :
            handler.getClass();

        // 查找 @ApiVersion 注解
        ApiVersion apiVersion = Optional
            .ofNullable(AnnotationUtils.findAnnotation(method, ApiVersion.class))
            .orElse(AnnotationUtils.findAnnotation(clazz, ApiVersion.class));

        if (null != apiVersion && 0 != apiVersion.value().length) {
            // 为每个版本号生成对应的 URL 路径
            String[] patterns = mapping.getPatternsCondition().getPatterns()
                .stream()
                .flatMap(pattern -> Arrays.stream(apiVersion.value())
                    .mapToObj(version -> String.format(VERSION_PATTERN, version) + pattern))
                .toArray(String[]::new);

            // 创建新的映射信息
            RequestMappingInfo newMappingInfo = RequestMappingInfo
                .paths(patterns)
                .methods(mapping.getMethodsCondition().getMethods())
                .params(mapping.getParamsCondition())
                .headers(mapping.getHeadersCondition())
                .consumes(mapping.getConsumesCondition())
                .produces(mapping.getProducesCondition())
                .build();

            // 注册新的映射
            super.registerHandlerMethod(handler, method, newMappingInfo);
        } else {
            // 没有 @ApiVersion 注解，使用原始映射
            super.registerHandlerMethod(handler, method, mapping);
        }
    }
}
```

### 3. ApiVersionCondition

`ApiVersionCondition` 实现了 `RequestCondition` 接口，用于在请求匹配时进行版本条件判断：

```java
public class ApiVersionCondition implements RequestCondition<ApiVersionCondition> {

    private final Pattern versionPrefixPattern = Pattern.compile("v(\\d+)/");
    private final int apiVersion;

    @Override
    public ApiVersionCondition getMatchingCondition(HttpServletRequest request) {
        String path = request.getRequestURI();
        Matcher matcher = versionPrefixPattern.matcher(path);

        if (matcher.find()) {
            int version = Integer.parseInt(matcher.group(1));
            if (version == this.apiVersion) {
                return this;
            }
        }
        return null;
    }

    @Override
    public ApiVersionCondition combine(ApiVersionCondition other) {
        // 方法级别覆盖类级别
        return new ApiVersionCondition(other.getApiVersion());
    }
}
```

## 版本控制策略

### 1. 版本号规则

- **版本号格式**：正整数，从 1 开始递增
- **URL 格式**：`/v{version}/{path}`
- **默认版本**：如果不指定，默认为版本 1

### 2. 版本选择策略

- **精确匹配**：URL 中的版本号必须与注解中的版本号完全匹配
- **多版本支持**：一个方法可以支持多个版本
- **优先级**：方法级别版本覆盖类级别版本

### 3. 版本映射示例

| 注解配置                     | URL 路径                                | 说明           |
|--------------------------|---------------------------------------|--------------|
| `@ApiVersion(1)`         | `/v1/users`                           | 仅支持版本 1      |
| `@ApiVersion({1, 2})`    | `/v1/users`, `/v2/users`              | 同时支持版本 1 和 2 |
| `@ApiVersion({1, 2, 3})` | `/v1/users`, `/v2/users`, `/v3/users` | 支持版本 1、2、3   |

## 使用示例

### 1. 基本版本控制

```java
@RestController
@RequestMapping("/users")
public class UserController {

    @ApiVersion(1)
    @GetMapping
    public List<User> getUsersV1() {
        // 访问 /v1/users
        return userService.getUsersV1();
    }

    @ApiVersion(2)
    @GetMapping
    public List<User> getUsersV2() {
        // 访问 /v2/users
        return userService.getUsersV2();
    }
}
```

### 2. 多版本并存

```java
@ApiVersion({1, 2})
@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping
    public List<User> getUsers() {
        // 同时支持 /v1/users 和 /v2/users
        // 内部可以根据版本号进行不同处理
        return userService.getUsers();
    }
}
```

### 3. 版本升级场景

```java
@RestController
@RequestMapping("/orders")
public class OrderController {

    // 旧版本 API（保留用于兼容）
    @ApiVersion(1)
    @GetMapping("/{id}")
    public Order getOrderV1(@PathVariable Long id) {
        // 返回旧版本的数据结构
        return orderService.getOrderV1(id);
    }

    // 新版本 API（新功能）
    @ApiVersion(2)
    @GetMapping("/{id}")
    public OrderV2 getOrderV2(@PathVariable Long id) {
        // 返回新版本的数据结构，包含更多字段
        return orderService.getOrderV2(id);
    }
}
```

### 4. 灰度升级场景

```java
@RestController
@RequestMapping("/products")
public class ProductController {

    // 稳定版本（大部分用户使用）
    @ApiVersion({1, 2})
    @GetMapping
    public List<Product> getProducts() {
        return productService.getProducts();
    }

    // 新版本（小部分用户使用，用于测试）
    @ApiVersion(3)
    @GetMapping
    public List<ProductV3> getProductsV3() {
        return productService.getProductsV3();
    }
}
```

## 版本路由机制

### 1. URL 路径生成

当使用 `@ApiVersion` 注解时，框架会自动为每个版本号生成对应的 URL 路径：

**原始映射**：`@GetMapping("/users")`

**版本 1**：`/v1/users`
**版本 2**：`/v2/users`
**版本 3**：`/v3/users`

### 2. 请求匹配流程

```
请求进入：/v2/users
    ↓
提取版本号：2
    ↓
查找匹配的 Controller 方法
    ↓
检查 @ApiVersion 注解
    ↓
版本号匹配？
    ↓
是 → 执行对应方法
    ↓
否 → 返回 404
```

### 3. 版本条件判断

```java
@Override
public ApiVersionCondition getMatchingCondition(HttpServletRequest request) {
    String path = request.getRequestURI();
    Matcher matcher = versionPrefixPattern.matcher(path);

    if (matcher.find()) {
        int version = Integer.parseInt(matcher.group(1));
        if (version == this.apiVersion) {
            return this; // 版本匹配
        }
    }
    return null; // 版本不匹配
}
```

## 最佳实践

### 1. 版本号管理

- **递增策略**：版本号从 1 开始，每次重大变更递增
- **语义化版本**：可以考虑使用语义化版本号（如 v1.0.0）
- **版本文档**：为每个版本维护详细的变更文档

### 2. 兼容性策略

- **向下兼容**：新版本尽量保持与旧版本的兼容性
- **逐步迁移**：给用户足够的时间迁移到新版本
- **版本废弃**：明确标记废弃的版本，并提供迁移指南

### 3. 版本共存

- **合理共存**：不要保留过多旧版本，建议最多保留 2-3 个版本
- **定期清理**：定期清理不再使用的旧版本
- **版本监控**：监控各版本的使用情况，决定何时废弃

### 4. API 设计

- **清晰的变更**：版本变更应该有明确的理由和文档
- **向后兼容**：尽量保持 API 的向后兼容性
- **变更通知**：提前通知用户版本变更计划

## 注意事项

### 1. 性能考虑

- 版本路由会增加一定的路由匹配开销，但影响很小
- 建议不要创建过多版本，避免路由表过大

### 2. 兼容性

- 与 Spring MVC 完全兼容
- 支持所有标准的 Spring MVC 注解
- 不影响没有使用版本控制的接口

### 3. URL 设计

- 版本号应该放在 URL 路径的最前面
- 建议使用 `/v{version}/` 格式
- 避免在查询参数中使用版本号

### 4. 文档维护

- 为每个版本维护详细的 API 文档
- 明确标注版本变更内容
- 提供版本迁移指南

## 总结

API 版本控制通过 `@ApiVersion` 注解和自定义的请求映射处理器实现了 REST API 的版本化管理和路由。这种设计使得同一个 API 的多个版本可以并存，方便
API 的向下兼容和灰度升级，是构建高质量 REST API 的重要基础。

