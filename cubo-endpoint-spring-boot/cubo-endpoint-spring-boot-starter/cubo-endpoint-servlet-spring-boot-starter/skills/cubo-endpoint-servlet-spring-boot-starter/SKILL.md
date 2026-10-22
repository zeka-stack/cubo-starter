---
name: cubo-endpoint-servlet-spring-boot-starter
description: 使用 Zeka Stack 运行端点与预热 编写业务功能、选择 starter、配置和排查装配时使用。
license: MIT
---

# cubo-endpoint-servlet-spring-boot-starter

## 适用场景与组件定位

按 Servlet / Reactive 暴露启动信息、探测及初始化扩展。

当前入口 `cubo-endpoint-servlet-spring-boot-starter` 直接依赖：`cubo-endpoint-servlet`, `spring-boot-starter-web`, `spring-boot-starter-undertow`。底层实现通过 starter 传递引入，不重复手动拼装。

## 依赖方式

```xml
<dependency>
    <groupId>dev.dong4j</groupId>
    <artifactId>cubo-endpoint-servlet-spring-boot-starter</artifactId>
</dependency>
```

## 选型

| 入口 | 适用场景 |
| --- | --- |
| `cubo-endpoint-servlet-spring-boot-starter` | Servlet Web 服务 |
| `cubo-endpoint-reactive-spring-boot-starter` | WebFlux 服务，不混用 Servlet API |

## 使用方式与最佳实践

复用 `InitializationService` 扩展预热，按实际初始化流程设计幂等、超时和失败语义。Servlet 与 Reactive 启动信息实现不同，不把内部项目详情端点直接开放到公网。

此组件不是完整的监控或授权系统，健康探测、存活与业务就绪要按项目需求区分。

## 通用编码约束

使用构造器注入，统一错误码 `BaseCodes` / `LowestExceptionAssert`，不新建重复响应或工具类。新增类与关键方法说明业务约束，先查询真实方法签名再调用。配置以属性类和自动装配条件为准；只有带 `@ConditionalOnMissingBean` 的具体 Bean 才能按其条件覆盖，不能通过返回 null 禁用。

依赖版本由业务项目的 `cubo-boot-dependencies` BOM 管理，当前内容对应 3.0.0-SNAPSHOT；不同版本先核对 API。

## 配置规则

`zeka-stack.endpoint.enabled` 控制带相应 ConditionalOnEnabled 的自动配置。注意 ProjectInfoEndpointAutoConfiguration 仅检查 Servlet Web 条件，不受该开关统一关闭，不能把 enabled=false 当作关闭所有内部端点的安全策略。端点访问由应用网关/安全配置约束。

## 自动装配与自定义

EndpointAutoConfiguration 在 Web 环境、Endpoint 类与开关满足时装配 PreloadComponent（缺少相应 Bean）。ServletStartInfoAutoConfiguration / ReactiveStartInfoAutoConfiguration 按 Web 类型装配启动信息和 InitializationService（缺失时）。Reactive 的 routerFunction 提供 /start 与 /check；不能仅凭 @ReadOperation 推断任意方法已作为 Actuator 端点发布。

## 不要这样做

不虚构配置键、注解属性、方法和 Bean 覆盖条件；不把选型对照中的另一种技术栈当成本 starter 的能力；不通过新建基础设施绕过框架已有封装。返回的成功与异常结构必须与业务项目协议一致。修改后通过实际应用上下文或接口测试确认行为，静态扫描不能证明运行时装配成功。
