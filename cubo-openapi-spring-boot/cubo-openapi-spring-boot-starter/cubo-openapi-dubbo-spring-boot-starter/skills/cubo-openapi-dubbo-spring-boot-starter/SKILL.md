---
name: cubo-openapi-dubbo-spring-boot-starter
description: 使用 Zeka Stack 接口文档 编写业务功能、选择 starter、配置和排查装配时使用。
license: MIT
---

# cubo-openapi-dubbo-spring-boot-starter

## 适用场景与组件定位

HTTP Knife4j 或 Dubbo 元数据文档，按技术栈选择。

当前入口 `cubo-openapi-dubbo-spring-boot-starter` 直接依赖：`cubo-openapi-dubbo`, `cubo-endpoint-servlet-spring-boot-starter`。底层实现通过 starter 传递引入，不重复手动拼装。

## 依赖方式

```xml
<dependency>
    <groupId>dev.dong4j</groupId>
    <artifactId>cubo-openapi-dubbo-spring-boot-starter</artifactId>
</dependency>
```

## 选型

| 入口 | 适用场景 |
| --- | --- |
| `cubo-openapi-knife4j-spring-boot-starter` | Servlet HTTP 接口文档 |
| `cubo-openapi-dubbo-spring-boot-starter` | Dubbo 接口文档，检查 Servlet 传递依赖 |

## 使用方式与最佳实践

HTTP API 使用 @Tag / @Operation / @Schema，查询对象使用 @ParameterObject；文档必须与真实路径、校验和返回模型一致，统一响应以 Result 契约为准。

Dubbo 变体为 Dubbo 接口文档能力，当前 starter 传递引入 endpoint-servlet，不能视为纯 RPC 或纯 Reactive 无 Web 依赖。不要将两个变体视为同一套自动装配。

## 通用编码约束

使用构造器注入，统一错误码 `BaseCodes` / `LowestExceptionAssert`，不新建重复响应或工具类。新增类与关键方法说明业务约束，先查询真实方法签名再调用。配置以属性类和自动装配条件为准；只有带 `@ConditionalOnMissingBean` 的具体 Bean 才能按其条件覆盖，不能通过返回 null 禁用。

依赖版本由业务项目的 `cubo-boot-dependencies` BOM 管理，当前内容对应 3.0.0-SNAPSHOT；不同版本先核对 API。

## 配置规则

`zeka-stack.openapi.enabled` 为自动配置开关。Knife4j 自动配置受 Servlet、类路径与非生产 Profile 约束；不能假定生产会开放 doc.html。原生 springdoc / knife4j 参数遵循项目实际依赖版本。

## 自动装配与自定义

OpenAPIAutoConfiguration 标记组件，Knife4jAutoConfiguration 提供 Knife4jOpenApiCustomizer 和 JakartaProductionSecurityFilter（非生产 Servlet 条件）。这些 Bean 没有统一的 MissingBean 覆盖保证，不盲目重复注册。

Dubbo 能力由 cubo-openapi-dubbo 引入，具体可用端点以部署时实际接口元数据和环境验证。

## 不要这样做

不虚构配置键、注解属性、方法和 Bean 覆盖条件；不把选型对照中的另一种技术栈当成本 starter 的能力；不通过新建基础设施绕过框架已有封装。返回的成功与异常结构必须与业务项目协议一致。修改后通过实际应用上下文或接口测试确认行为，静态扫描不能证明运行时装配成功。
