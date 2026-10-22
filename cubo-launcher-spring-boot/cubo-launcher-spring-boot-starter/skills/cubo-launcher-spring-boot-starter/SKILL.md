---
name: cubo-launcher-spring-boot-starter
description: 使用 Zeka Stack 统一启动与生命周期 编写业务功能、选择 starter、配置和排查装配时使用。
license: MIT
---

# cubo-launcher-spring-boot-starter

## 适用场景与组件定位

ZekaStack 应用需要统一启动、运行类型识别及生命周期扩展。

当前入口 `cubo-launcher-spring-boot-starter` 直接依赖：`cubo-launcher-spring-boot-autoconfigure`, `cubo-launcher-spring-boot-core`。底层实现通过 starter 传递引入，不重复手动拼装。

## 依赖方式

```xml
<dependency>
    <groupId>dev.dong4j</groupId>
    <artifactId>cubo-launcher-spring-boot-starter</artifactId>
</dependency>
```

## 选型

| 入口 | 适用场景 |
| --- | --- |
| `cubo-launcher-spring-boot-starter` | ZekaStack 应用需要统一启动、运行类型识别及生命周期扩展。 |
| `原生 Spring Boot` | 不需要 Zeka 工程约定 |

## 使用方式与最佳实践

依赖启动核心与自动配置；启动类继承 `dev.dong4j.zeka.starter.launcher.ZekaStarter`，加 `@SpringBootApplication`，默认不另写 main。可重写 `before()` / `run(String... args)` / `after()`，非 Web 用 `@RunningType(ApplicationType.SERVICE)`。

```java
package dev.dong4j.zeka.demo;
import dev.dong4j.zeka.starter.launcher.ZekaStarter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/** 进入框架统一生命周期。 */
@SpringBootApplication
public class DemoApplication extends ZekaStarter { }
```

扫描根包默认 `dev.dong4j.zeka`，自定义包须在启动 JVM 前设 `-DPARENT_PACKAGE_NAME=com.example`。显式 API 可用 `ZekaStarter.run(DemoApplication.class, args)`；不要臆造 ZekaApplication 的重载。

启动类入口与构建生成的 start.class 一起验证。

## 通用编码约束

使用构造器注入，统一错误码 `BaseCodes` / `LowestExceptionAssert`，不新建重复响应或工具类。新增类与关键方法说明业务约束，先查询真实方法签名再调用。配置以属性类和自动装配条件为准；只有带 `@ConditionalOnMissingBean` 的具体 Bean 才能按其条件覆盖，不能通过返回 null 禁用。

依赖版本由业务项目的 `cubo-boot-dependencies` BOM 管理，当前内容对应 3.0.0-SNAPSHOT；不同版本先核对 API。

## 配置规则

`zeka-stack.app.enable-banner` 默认 false；`app.group/config-group/discovery-group/custom` 在 `zeka-stack` 下。`zeka-stack.extend.enable-autowired-is-null` / `enable-resource-is-null` 默认 false，不以允许缺失注入替代正确依赖。

配置热更新受 RefreshScope 相关类路径和自动配置条件控制；当前条件使用 `LauncherProperties.PREFIX + "refresh"`（没有分隔点），不可声称配置 `zeka-stack.app.refresh` 一定生效，需在所用版本验证。

## 自动装配与自定义

LauncherAutoConfiguration 负责绑定启动属性。RefreshScopeRegistry、DynamicConfigLoader、RefreshScopeRefresher、ConfigFileWatcherRunner 在 RefreshScope 条件满足且缺少对应 Bean 时装配。配置监听器不是跨进程配置中心，不把本地文件变更视为分布式一致性。

## 不要这样做

不虚构配置键、注解属性、方法和 Bean 覆盖条件；不把选型对照中的另一种技术栈当成本 starter 的能力；不通过新建基础设施绕过框架已有封装。返回的成功与异常结构必须与业务项目协议一致。修改后通过实际应用上下文或接口测试确认行为，静态扫描不能证明运行时装配成功。
