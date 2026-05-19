---
name: cubo-launcher-spring-boot-starter
description: 在使用 Zeka Stack 应用启动器编写 Spring Boot 应用时使用。
license: Apache-2.0
---

# Cubo Launcher Spring Boot Starter

## 适用场景

当项目需要编写 Spring Boot 应用，并希望使用 Zeka Stack 统一的应用启动方式、生命周期钩子、应用类型管理和配置热更新时，使用本 Skill。

## 组件定位

`cubo-launcher-spring-boot-starter` 是 Zeka Stack 的应用启动器模块。它封装了 Spring Boot 应用的启动流程，提供了：

- 统一的 `ZekaStarter` 抽象基类，替代传统的 `SpringApplication.run()`
- 应用类型自动检测和生命周期管理
- 配置热更新能力（不依赖 Spring Cloud）
- 启动时 SPI 扩展机制
- Banner 显示和版本信息管理

## 功能能力

### 1. 统一启动基类 ZekaStarter

继承 `ZekaStarter` 代替直接使用 `SpringApplication.run()`，自动获得：

- 启动类自动检测和验证（确保只有一个 ZekaStarter 子类）
- 应用类型自动推断（SERVLET / REACTIVE / NONE / SERVICE）
- 生命周期钩子：`before()`、`run()`、`after()`
- 服务类型应用的守护线程管理（防止启动完成后退出）
- 启动参数处理（支持 `--start.class=` 指定启动类）

### 2. 应用类型管理

通过 `@RunningType` 注解或自动检测确定应用类型：

| ApplicationType | 说明                    | 行为              |
|-----------------|-----------------------|-----------------|
| `SERVLET`       | 传统 Servlet Web 应用     | 启动 Web 容器       |
| `REACTIVE`      | WebFlux 响应式应用         | 启动响应式 Web 容器    |
| `NONE`          | 非 Web 应用              | 启动完成后自动退出       |
| `SERVICE`       | 非 Web 服务（如 Dubbo 提供者） | 启动完成后不退出，守护线程阻塞 |

### 3. 配置热更新

通过 `RefreshScopeAutoConfiguration` 提供不依赖 Spring Cloud 的配置热更新：

- 监控 `application.yml` 及环境特定配置文件
- 配置变更时精准刷新相关 Bean
- 通过 `zeka-stack.app.refresh=false` 可禁用

### 4. 注解扩展

通过 `ExtendAutoConfiguration` 提供注解行为扩展：

- `@Autowired` 注入的 Bean 可以为 null（默认启用）
- `@Resource` 注入的 Bean 可以为 null（默认启用）

## 依赖方式

```xml
<dependency>
    <groupId>dev.dong4j</groupId>
    <artifactId>cubo-launcher-spring-boot-starter</artifactId>
</dependency>
```

## 使用方式与最佳实践

### 启动类写法

继承 `ZekaStarter` 作为应用入口，不需要编写 `main()` 方法：

```java
@SpringBootApplication
public class DemoApplication extends ZekaStarter {

    // 不需要写 main() 方法

}
```

### 启动应用

使用 `ZekaApplication.run()` 方法启动：

```java
// 方式1：通过继承 ZekaStarter 启动
// 直接运行 DemoApplication 即可

// 方式2：在测试中启动内嵌应用
ConfigurableApplicationContext context = ZekaApplication.run(DemoApplication.class, args);

// 方式3：指定应用名称
ConfigurableApplicationContext context = ZekaApplication.run("my-app", DemoApplication.class, args);
```

### 生命周期钩子

在启动类的子类中重写生命周期方法：

```java
@SpringBootApplication
public class DemoApplication extends ZekaStarter {

    // Spring 容器启动前调用
    @Override
    protected void before() {
        // 执行环境检查、资源预加载等
    }

    // 容器刷新完成后、after() 之前调用（实现 CommandLineRunner）
    @Override
    public void run(String... args) {
        // 执行启动后的一键式初始化任务
    }

    // 应用完全启动后调用
    @Override
    protected void after() {
        // 执行需要应用完全就绪后的操作
    }
}
```

### 指定应用类型

通过 `@RunningType` 注解显式指定应用类型：

```java
@RunningType(ApplicationType.SERVICE)
@SpringBootApplication
public class DubboProviderApplication extends ZekaStarter {
    // 显式指定为 SERVICE 类型
}
```

## 编码规则

- 启动类必须继承 `ZekaStarter`，不能直接实例化或调用 `ZekaStarter.main()`
- 一个应用只能有一个 `ZekaStarter` 子类
- 启动类必须使用 `@SpringBootApplication` 或 `@EnableAutoConfiguration` 注解
- 生命周期钩子方法应避免长时间阻塞操作
- 服务类型应用不需要守护线程管理，已由框架自动处理

## 配置规则

### 基础配置

```yaml
zeka-stack:
  app:
    enabled: true
    enable-banner: false
    group: default
    config-group: default
    discovery-group: default
    refresh: true
    custom:
      key: value
```

### 配置属性说明

| 属性名                                          | 类型      | 默认值   | 说明                      |
|----------------------------------------------|---------|-------|-------------------------|
| `zeka-stack.app.enabled`                     | boolean | true  | 是否启用启动器功能               |
| `zeka-stack.app.enable-banner`               | boolean | false | 是否禁用启动 Banner 显示        |
| `zeka-stack.app.group`                       | String  | -     | 应用分组名称                  |
| `zeka-stack.app.config-group`                | String  | -     | 配置中心分组名称                |
| `zeka-stack.app.discovery-group`             | String  | -     | 服务发现分组名称                |
| `zeka-stack.app.refresh`                     | boolean | true  | 是否启用配置热更新               |
| `zeka-stack.app.custom`                      | Map     | -     | 自定义配置映射                 |
| `zeka-stack.extend.enable-autowired-is-null` | boolean | true  | 是否允许 @Autowired 注入 null |
| `zeka-stack.extend.enable-resource-is-null`  | boolean | true  | 是否允许 @Resource 注入 null  |

## 自动装配的 Bean

| Bean / 类型                              | 作用                    | 生效条件                            | 自定义方式         |
|----------------------------------------|-----------------------|---------------------------------|---------------|
| `RefreshScopeRegistry`                 | 配置刷新范围注册表             | `refresh=true` 且无 Spring Cloud  | 声明同类型 Bean 覆盖 |
| `DynamicConfigLoader`                  | 动态配置加载器               | `refresh=true` 且无 Spring Cloud  | 声明同类型 Bean 覆盖 |
| `RefreshScopeRefresher`                | 精准刷新受影响的 Bean         | `refresh=true` 且无 Spring Cloud  | 声明同类型 Bean 覆盖 |
| `ConfigFileWatcherRunner`              | 配置文件监听线程              | `refresh=true` 且无 Spring Cloud  | 声明同类型 Bean 覆盖 |
| `AutowiredAnnotationBeanPostProcessor` | 允许 @Autowired 注入 null | `enable-autowired-is-null=true` | 配置属性开关        |
| `CommonAnnotationBeanPostProcessor`    | 允许 @Resource 注入 null  | `enable-resource-is-null=true`  | 配置属性开关        |

## 自定义与覆盖方式

### 禁用配置热更新

```yaml
zeka-stack:
  app:
    refresh: false
```

### 禁用 null 注入扩展

```yaml
zeka-stack:
  extend:
    enable-autowired-is-null: false
    enable-resource-is-null: false
```

### 自定义启动行为

通过重写生命周期方法扩展：

```java
@SpringBootApplication
public class CustomApplication extends ZekaStarter {

    @Override
    protected void before() {
        // 自定义启动前逻辑
    }

    @Override
    protected void after() {
        // 自定义启动后逻辑
    }
}
```

### SPI 扩展

实现 `LauncherInitiation` 接口，在应用启动的不同阶段插入自定义逻辑：

```java
public class CustomLauncherInitiation implements LauncherInitiation {

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void launcherWrapper(ConfigurableEnvironment environment,
                               Properties defaultProperties,
                               String appName,
                               boolean isLocalLaunch) {
        // 容器创建前执行
    }

    @Override
    public void beforeWrapper(boolean isLocalLaunch) {
        // main() 方法执行前、ZekaStarter 初始化前执行
    }

    @Override
    public void after(ConfigurableApplicationContext context, boolean isLocalLaunch) {
        // 容器启动完成后执行
    }
}
```

在 `META-INF/services/dev.dong4j.zeka.kernel.common.start.LauncherInitiation` 文件中注册：

```
com.example.CustomLauncherInitiation
```

## 不要这样做

- 不要直接调用 `ZekaStarter.main()`，必须通过子类启动
- 不要在一个应用中创建多个 `ZekaStarter` 子类
- 不要在 `before()` 或 `after()` 中执行长时间阻塞操作，这会影响应用启动/关闭
- 不要禁用生产环境的配置热更新功能，除非有特殊原因
- 不要在非必要情况下禁用 `@Autowired` null 注入扩展，这可能导致现有代码行为变更
