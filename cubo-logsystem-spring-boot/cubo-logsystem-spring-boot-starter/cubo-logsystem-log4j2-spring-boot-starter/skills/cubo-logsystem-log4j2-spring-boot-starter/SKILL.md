---
name: cubo-logsystem-log4j2-spring-boot-starter
description: 使用 Zeka Stack 日志系统 编写业务功能、选择 starter、配置和排查装配时使用。
license: MIT
---

# cubo-logsystem-log4j2-spring-boot-starter

## 适用场景与组件定位

统一 Log4j2 日志、动态级别；record 变体增加日志存储适配。

当前入口 `cubo-logsystem-log4j2-spring-boot-starter` 直接依赖：`cubo-logsystem-log4j2`, `snakeyaml`。底层实现通过 starter 传递引入，不重复手动拼装。

## 依赖方式

```xml
<dependency>
    <groupId>dev.dong4j</groupId>
    <artifactId>cubo-logsystem-log4j2-spring-boot-starter</artifactId>
</dependency>
```

## 选型

| 入口 | 适用场景 |
| --- | --- |
| `cubo-logsystem-log4j2-spring-boot-starter` | 统一日志后端 |
| `cubo-logsystem-record-spring-boot-starter` | 需要日志存储扩展 |

## 使用方式与最佳实践

业务使用 SLF4J / Lombok @Slf4j，参数化日志，复用 traceId。不要另引入冲突日志后端，不输出凭据与完整隐私对象。

record 变体在 Log4j2 基础上提供 LogStorageFactory / LogStorageFactoryAdapter 扩展；业务选择实际存储实现，不能假定引入 starter 后日志已经可靠持久化。

## 通用编码约束

使用构造器注入，统一错误码 `BaseCodes` / `LowestExceptionAssert`，不新建重复响应或工具类。新增类与关键方法说明业务约束，先查询真实方法签名再调用。配置以属性类和自动装配条件为准；只有带 `@ConditionalOnMissingBean` 的具体 Bean 才能按其条件覆盖，不能通过返回 null 禁用。

依赖版本由业务项目的 `cubo-boot-dependencies` BOM 管理，当前内容对应 3.0.0-SNAPSHOT；不同版本先核对 API。

## 配置规则

配置前缀 `zeka-stack.logging`，包括 app-name、config、file、level、group、pattern；record 开关为 `zeka-stack.logging.record.enabled`，由 ConditionalOnEnabled 控制。生产路径、轮转、级别按部署策略明确配置，不硬编码机器目录。

## 自动装配与自定义

LogSystemAutoConfiguration 在存在 LogPrintStream 时绑定 LogSystemProperties；RefreshLogLevelAutoConfiguration 提供级别刷新。record 的 LogSystemRecordAutoConfiguration 在存在 LogStorageFactory 且条件启用时装配 LogStorageFactoryAdapter，带 ConditionalOnMissingBean，可由实际存储工厂覆盖。

## 不要这样做

不虚构配置键、注解属性、方法和 Bean 覆盖条件；不把选型对照中的另一种技术栈当成本 starter 的能力；不通过新建基础设施绕过框架已有封装。返回的成功与异常结构必须与业务项目协议一致。修改后通过实际应用上下文或接口测试确认行为，静态扫描不能证明运行时装配成功。
