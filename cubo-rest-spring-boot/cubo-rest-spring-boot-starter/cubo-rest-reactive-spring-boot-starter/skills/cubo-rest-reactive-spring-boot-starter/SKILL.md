---
name: cubo-rest-reactive-spring-boot-starter
description: 使用 Zeka Stack 统一 Web 接口 编写业务功能、选择 starter、配置和排查装配时使用。
license: MIT
---

# cubo-rest-reactive-spring-boot-starter

## 适用场景与组件定位

按应用类型选择 Servlet 或 Reactive，提供 REST 配置与异常处理。

当前入口 `cubo-rest-reactive-spring-boot-starter` 直接依赖：`cubo-rest-reactive`, `spring-boot-starter-webflux`。底层实现通过 starter 传递引入，不重复手动拼装。

## 依赖方式

```xml
<dependency>
    <groupId>dev.dong4j</groupId>
    <artifactId>cubo-rest-reactive-spring-boot-starter</artifactId>
</dependency>
```

## 选型

| 入口 | 适用场景 |
| --- | --- |
| `cubo-rest-servlet-spring-boot-starter` | Servlet Web 服务 |
| `cubo-rest-reactive-spring-boot-starter` | WebFlux 服务，不混用 Servlet API |

## 使用方式与最佳实践

本 starter 用于 WebFlux，依赖 cubo-rest-reactive 与 spring-boot-starter-webflux。Controller 按项目既有响应式契约返回 Mono/Flux，使用对应错误处理器；本文 Servlet 说明仅用于选型对照，不能套用 ServletController、@RestControllerWrapper 或声称已有相同自动包装。数据库等阻塞任务必须明确调度边界。

Servlet 业务 Controller 使用 `@RestControllerWrapper("/accounts")`，继承 `ServletController`，构造注入 Service，返回 DTO、集合、IPage 或 void，由 Advice 包装成 `Result<T>`。已有显式 `R.succeed()` 写法保持一致。失败使用 `BaseCodes.PARAM_VERIFY_ERROR` 等真实错误码，业务枚举实现 LowestExceptionAssert；不得引用不存在的 ResultCodeEnum。

自动包装要求包名符合 RestUtils 判定：默认包含 `dev.dong4j.zeka`，自定义根包通过 JVM `-DPARENT_PACKAGE_NAME=...` 并验证接口。原始文件下载用 `@OriginalResponse`。

Query/Form/DTO 继承 BaseQuery/BaseForm/BaseDTO；使用 Jakarta Validation 和 OpenAPI 注解，Controller 不直连 Mapper。

## 通用编码约束

使用构造器注入，统一错误码 `BaseCodes` / `LowestExceptionAssert`，不新建重复响应或工具类。新增类与关键方法说明业务约束，先查询真实方法签名再调用。配置以属性类和自动装配条件为准；只有带 `@ConditionalOnMissingBean` 的具体 Bean 才能按其条件覆盖，不能通过返回 null 禁用。

依赖版本由业务项目的 `cubo-boot-dependencies` BOM 管理，当前内容对应 3.0.0-SNAPSHOT；不同版本先核对 API。

## 配置规则

`zeka-stack.rest.enabled` 控制相应自动配置；read-timeout / write-timeout 默认 5000ms，connect-timeout 默认 3000ms；json.date-format 默认 yyyy-MM-dd HH:mm:ss，json.time-zone 来自 DEFAULT_TIME_ZONE（GMT+8），default-property-inclusion 为 non_null。

Servlet 的 `zeka-stack.rest.xss.enable-xss-filter` 默认 true；enable-global-cache-filter、enable-exception-filter 默认 true；enable-global-parameter-filter 默认 false。这些 Servlet 配置不应当作 WebFlux 的同等能力。

## 自动装配与自定义

ServletGlobalExceptionAutoConfiguration 在 Servlet 类路径和 Web 类型满足时装配 ErrorAttributes、错误 Controller / Handler；对应 Bean 有缺失条件。RestTemplate 的默认 Bean 有 MissingBean 条件；HTTP 工厂和其他辅助 Bean 不都可直接替换。

Reactive 使用 WebfluxGlobalExceptionAutoConfiguration / WebFluxAutoConfiguration，装配对应 ErrorAttributes 与 ErrorWebExceptionHandler；不得注册 Servlet Advice / Filter 代替。

## 不要这样做

不虚构配置键、注解属性、方法和 Bean 覆盖条件；不把选型对照中的另一种技术栈当成本 starter 的能力；不通过新建基础设施绕过框架已有封装。返回的成功与异常结构必须与业务项目协议一致。修改后通过实际应用上下文或接口测试确认行为，静态扫描不能证明运行时装配成功。
