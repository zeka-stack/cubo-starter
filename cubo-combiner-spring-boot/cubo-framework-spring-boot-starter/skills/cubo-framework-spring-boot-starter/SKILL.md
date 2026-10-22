---
name: cubo-framework-spring-boot-starter
description: 使用 Zeka Stack 组合业务能力 编写业务功能、选择 starter、配置和排查装配时使用。
license: MIT
---

# cubo-framework-spring-boot-starter

## 适用场景与组件定位

按项目形态选 framework 或 SSM，减少重复依赖配置。

当前入口 `cubo-framework-spring-boot-starter` 直接依赖：`cubo-logsystem-log4j2-spring-boot-starter`, `cubo-launcher-spring-boot-starter`。底层实现通过 starter 传递引入，不重复手动拼装。

## 依赖方式

```xml
<dependency>
    <groupId>dev.dong4j</groupId>
    <artifactId>cubo-framework-spring-boot-starter</artifactId>
</dependency>
```

## 选型

| 入口 | 适用场景 |
| --- | --- |
| `cubo-framework-spring-boot-starter` | 统一启动与日志 |
| `cubo-ssm-spring-boot-starter` | Servlet + MyBatis 完整业务栈 |

## 使用方式与最佳实践

framework = launcher + logsystem-log4j2；SSM = framework + rest-servlet + openapi-knife4j + endpoint-servlet + mybatis + actuator。

Servlet SSM 业务采用 @RestControllerWrapper / ServletController，直接返回 DTO/void，统一 Advice 包装；DAO extends BaseDao，Service extends BaseService / BaseServiceImpl，PO 按真实列选择 BaseWithTimePO 或 BaseExtendPO；MapStruct ExtendConverter 负责 f2p/p2d。错误码用 BaseCodes / LowestExceptionAssert，构造器注入。

启动类 extends ZekaStarter，默认根包 dev.dong4j.zeka，自定义包用 JVM -DPARENT_PACKAGE_NAME=...。不要重复引入组合器已包含的组件，也不要额外混入 Reactive。

## 通用编码约束

使用构造器注入，统一错误码 `BaseCodes` / `LowestExceptionAssert`，不新建重复响应或工具类。新增类与关键方法说明业务约束，先查询真实方法签名再调用。配置以属性类和自动装配条件为准；只有带 `@ConditionalOnMissingBean` 的具体 Bean 才能按其条件覆盖，不能通过返回 null 禁用。

依赖版本由业务项目的 `cubo-boot-dependencies` BOM 管理，当前内容对应 3.0.0-SNAPSHOT；不同版本先核对 API。

## 配置规则

组合器不定义新配置前缀。使用各依赖组件的 zeka-stack.app / logging / rest / mybatis / endpoint / openapi 配置。SSM 的数据库配置来自 spring.datasource 等实际原生属性。配置存在不代表生产默认开启所有安全拦截或文档端点。

## 自动装配与自定义

组合器本身不声明 Bean，装配来自依赖 starter。framework 只提供启动/日志，不能假定有 REST 或数据库。SSM 的 Bean 和覆盖方式分别遵守底层自动配置，不能笼统声明所有 Bean 都可通过同类型覆盖。

## 不要这样做

不虚构配置键、注解属性、方法和 Bean 覆盖条件；不把选型对照中的另一种技术栈当成本 starter 的能力；不通过新建基础设施绕过框架已有封装。返回的成功与异常结构必须与业务项目协议一致。修改后通过实际应用上下文或接口测试确认行为，静态扫描不能证明运行时装配成功。
