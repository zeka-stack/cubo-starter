# Cubo Launcher Spring Boot

## 概述

`cubo-launcher-spring-boot` 是 Cubo Starter 项目的启动器模块，负责应用启动时的核心组件初始化和配置管理。该模块提供了应用启动的基础设施，包括配置热更新、扩展点管理等高级功能。

## 主要功能

### 1. 应用启动管理

- 提供统一的应用启动入口
- 管理启动过程中的组件初始化顺序
- 支持启动时的自定义逻辑扩展

### 2. 配置热更新

- 支持非 Spring Cloud 环境下的配置热更新
- 实现类似 `@RefreshScope` 的配置刷新机制
- 支持配置文件的实时监听和自动刷新

### 3. 扩展点管理

- 提供 SPI（Service Provider Interface）机制
- 支持插件化的功能扩展
- 便于第三方组件的集成

## 模块结构

```
cubo-launcher-spring-boot/
├── cubo-launcher-spring-boot-autoconfigure/    # 自动配置模块
├── cubo-launcher-spring-boot-core/             # 核心功能模块
└── cubo-launcher-spring-boot-starter/          # Starter 模块
```

### 子模块说明

#### cubo-launcher-spring-boot-autoconfigure

- **LauncherAutoConfiguration**: 启动器主自动配置类
- **RefreshScopeAutoConfiguration**: 配置热更新自动配置
- **ExtendAutoConfiguration**: 扩展点自动配置

#### cubo-launcher-spring-boot-core

- **ZekaStarter**: 启动器核心类
- **配置热更新相关组件**:
    - `DynamicConfigLoader`: 动态配置加载器
    - `ConfigFileWatcher`: 配置文件监听器
    - `ConfigDiffer`: 配置差异比较器
    - `RefreshScopeRegistry`: 刷新作用域注册器
    - `RefreshScopeRefresher`: 配置刷新器

#### cubo-launcher-spring-boot-starter

- 提供开箱即用的 Starter 依赖

## 核心特性

### 1. 配置热更新

#### 功能特点

- ✅ 实时监听配置文件变更（application.yml 和 profile 文件）
- ✅ 配置变更差异分析，避免无效刷新
- ✅ 精准配置类刷新，仅刷新受影响的配置
- ✅ 自动绑定新配置到现有 Bean
- ✅ 环境感知，支持按 profile 选择性刷新
- ✅ 与 Spring Cloud 兼容，优先使用原生 `@RefreshScope`
- ✅ 线程安全和高可用性

#### 使用方式

**1. 标记配置类**

```java
@AutoConfiguration
@RefreshScope
@ConfigurationProperties(prefix = "custom")
public class CustomProperties {
    private String message;
    private Integer limit;

    // Getter / Setter
}
```

**2. 初始化监听器**

```java
@Bean
public CommandLineRunner initWatcher(
        DynamicConfigLoader loader,
        RefreshScopeRegistry registry,
        RefreshScopeRefresher refresher,
        Environment env) {

    return args -> {
        AtomicReference<Map<String, Object>> current =
            new AtomicReference<>(loader.loadCurrentEnvironmentConfig());

        new Thread(new ConfigFileWatcher(env, changedFile -> {
            Map<String, Object> latest = loader.loadCurrentEnvironmentConfig();
            ConfigDiffer.DiffResult diff = new ConfigDiffer().diff(current.get(), latest);

            if (diff.hasDiff) {
                System.out.println("[DIFF] 变更字段: " + diff.changedKeys);
                current.set(latest);
                refresher.refreshByChangedKeys(diff.changedKeys);
            }
        })).start();
    };
}
```

**3. 使用配置**

```java
@RestController
public class HelloController {
    private final CustomProperties props;

    public HelloController(CustomProperties props) {
        this.props = props;
    }

    @GetMapping("/hello")
    public String hello() {
        return "Message: " + props.getMessage() + ", Limit: " + props.getLimit();
    }
}
```

### 2. 启动器核心功能

#### ZekaStarter

- 提供应用启动的统一入口
- 管理启动过程中的组件初始化
- 支持启动时的自定义逻辑

#### 扩展点机制

- 支持 SPI 扩展
- 提供插件化架构
- 便于功能模块的集成

## 配置属性

### LauncherProperties

| 属性名                                         | 类型      | 默认值  | 说明        |
|---------------------------------------------|---------|------|-----------|
| `zeka-stack.launcher.enabled`               | boolean | true | 是否启用启动器功能 |
| `zeka-stack.launcher.refresh-scope.enabled` | boolean | true | 是否启用配置热更新 |

## 使用方式

### 1. 引入依赖

```xml
<dependency>
    <groupId>dev.dong4j</groupId>
    <artifactId>cubo-launcher-spring-boot-starter</artifactId>
</dependency>
```

### 2. 配置启用

```yaml
zeka-stack:
  launcher:
    enabled: true
    refresh-scope:
      enabled: true
```

### 3. 自定义启动逻辑

```java
@Component
public class CustomStartupComponent {

    @EventListener
    public void onApplicationReady(ApplicationReadyEvent event) {
        // 自定义启动逻辑
        System.out.println("应用启动完成，执行自定义逻辑");
    }
}
```

## 最佳实践

### 1. 配置热更新

- 合理使用 `@RefreshScope` 注解
- 避免在配置类中执行重量级操作
- 注意配置变更对业务逻辑的影响

### 2. 启动器使用

- 遵循启动顺序，避免循环依赖
- 合理使用扩展点机制
- 注意启动时的性能影响

### 3. 配置管理

- 使用有意义的配置前缀
- 提供合理的默认值
- 添加必要的配置验证

## 注意事项

1. **配置热更新**: 仅在非生产环境或特殊场景下使用
2. **性能影响**: 配置监听会消耗一定的系统资源
3. **兼容性**: 与 Spring Cloud 的 `@RefreshScope` 完全兼容
4. **线程安全**: 所有刷新操作都是线程安全的

## 技术实现

### 配置热更新流程

```
监听配置文件变更
        ↓
重新加载并扁平化配置
        ↓
与旧配置进行 diff 比较
        ↓
识别受影响的配置类
        ↓
使用 Spring Binder 绑定新值到现有 Bean
```

### 核心组件

- **@RefreshScope**: 标记可热刷新的配置类
- **DynamicConfigLoader**: 负责加载并解析 YAML 配置
- **ConfigFileWatcher**: 监听配置目录中文件变动
- **ConfigDiffer**: 执行配置差异对比
- **RefreshScopeRegistry**: 注册所有可刷新的配置类
- **RefreshScopeRefresher**: 按 prefix 匹配并刷新配置类

## 相关链接

- [Spring Boot 配置属性](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)
- [Spring Cloud RefreshScope](https://docs.spring.io/spring-cloud/docs/current/reference/html/#refresh-scope)
- [配置文件监听最佳实践](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config.files.watching)
