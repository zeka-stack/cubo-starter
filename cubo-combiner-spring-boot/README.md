---
published: 2022.05.16
---

# 组合器模块

组合器只聚合依赖，不定义新的配置或 Bean。下面的内容以当前 POM 为准，不能把规划能力当成已经提供的功能。

| 入口 | 实际依赖 |
| --- | --- |
| cubo-framework-spring-boot-starter | cubo-launcher-spring-boot-starter、cubo-logsystem-log4j2-spring-boot-starter |
| cubo-ssm-spring-boot-starter | framework、rest-servlet、openapi-knife4j、endpoint-servlet、mybatis、spring-boot-starter-actuator |

framework 不包含 REST 或数据库；SSM 不包含 messaging。按需要额外选择 Kafka/RocketMQ starter。版本通过业务 BOM 管理，避免重复引入 framework 或混入 Reactive。

## 使用方式

```xml
<dependency>
    <groupId>dev.dong4j</groupId>
    <artifactId>cubo-ssm-spring-boot-starter</artifactId>
</dependency>
```

启动类 extends ZekaStarter；Servlet Controller 使用 RestControllerWrapper / ServletController，返回 DTO 或 void 交由统一 Advice 包装。业务错误使用 BaseCodes 或 LowestExceptionAssert，DAO / Service 复用 BaseDao / BaseService / BaseServiceImpl，不创建自定义 ApiResponse。

## 配置与扩展

配置归各组件所有：zeka-stack.app、logging、rest、mybatis、openapi、endpoint。组合器没有额外的“统一覆盖”开关，也不承诺所有 Bean 都能直接替换。根据各自动配置的实际条件定制并验证应用上下文。

## 自包含 Skills

- [framework Skill](https://zekastack.dong4j.site/docs/skills/cubo-framework-spring-boot-starter/SKILL.md)
- [SSM Skill](https://zekastack.dong4j.site/docs/skills/cubo-ssm-spring-boot-starter/SKILL.md)

业务完整范例可使用 zeka demo，规范使用 zeka arch。
