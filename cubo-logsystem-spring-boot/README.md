---
published: 2022.03.28
---

# 日志系统

当前内容对照 3.0.0-SNAPSHOT 源码维护。业务优先采用框架的基类与组件，不复制基础设施实现。

## 组件定位

统一 Log4j2 日志、动态级别；record 变体增加日志存储适配。

当前入口 `cubo-logsystem-log4j2-spring-boot-starter` 直接依赖：`cubo-logsystem-log4j2`, `snakeyaml`。底层实现通过 starter 传递引入，不重复手动拼装。


## Starter 选型

- [cubo-logsystem-log4j2-spring-boot-starter](https://zekastack.dong4j.site/docs/skills/cubo-logsystem-log4j2-spring-boot-starter/SKILL.md)：独立的依赖、选型和编码约定。
- [cubo-logsystem-record-spring-boot-starter](https://zekastack.dong4j.site/docs/skills/cubo-logsystem-record-spring-boot-starter/SKILL.md)：独立的依赖、选型和编码约定。

## 推荐业务写法

业务使用 SLF4J / Lombok @Slf4j，参数化日志，复用 traceId。不要另引入冲突日志后端，不输出凭据与完整隐私对象。

record 变体在 Log4j2 基础上提供 LogStorageFactory / LogStorageFactoryAdapter 扩展；业务选择实际存储实现，不能假定引入 starter 后日志已经可靠持久化。


## 配置与真实默认值

配置前缀 `zeka-stack.logging`，包括 app-name、config、file、level、group、pattern；record 开关为 `zeka-stack.logging.record.enabled`，由 ConditionalOnEnabled 控制。生产路径、轮转、级别按部署策略明确配置，不硬编码机器目录。


## 自动配置与扩展

LogSystemAutoConfiguration 在存在 LogPrintStream 时绑定 LogSystemProperties；RefreshLogLevelAutoConfiguration 提供级别刷新。record 的 LogSystemRecordAutoConfiguration 在存在 LogStorageFactory 且条件启用时装配 LogStorageFactoryAdapter，带 ConditionalOnMissingBean，可由实际存储工厂覆盖。


## 验证要求

变更后验证实际应用上下文、接口与依赖版本。文档中的示例不是完整业务授权或运行可靠性保证；配置存在不代表所有环境均装配相同能力。框架工程规范维护在根目录 arch.md，可通过 zeka arch / zeka demo 离线查询。

## 深入阅读

- [detail-design](https://zekastack.dong4j.site/docs/cubo-starter/cubo-logsystem-spring-boot/detail-design.html)
