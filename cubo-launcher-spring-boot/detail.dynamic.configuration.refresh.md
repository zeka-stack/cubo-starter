---
published: 2022.03.21
---

# 🌟 Spring Boot 配置热更新框架（非 Spring Cloud 环境）

## **📌 项目目标**

在不引入 Spring Cloud 的前提下，实现类似 @RefreshScope 的配置热更新机制，支持：

- 修改 application.yml 或 application-{profile}.yml 后自动生效
- 无需重启服务，配置实时刷新到配置类
- 支持按配置类刷新（非全量刷新）
- 支持配置变更差异比对（diff）
- 与 Spring Cloud 原生 @RefreshScope 完全兼容

------

## **✅ 实现功能**

| **功能点**              | **描述**                                                       |
|----------------------|--------------------------------------------------------------|
| 🔁 实时监听配置文件变更        | 支持 application.yml 和 profile 文件（如 application-prod.yml）的自动监听 |
| 🧠 配置变更差异分析          | 精确对比新旧配置，避免无效刷新                                              |
| 🎯 精准配置类刷新           | 仅刷新受影响的 @ConfigurationProperties 配置类                         |
| 💡 自动绑定新配置           | 使用 Spring Boot 原生 Binder 将新值绑定到现有 Bean                       |
| 🌐 环境感知              | 支持按激活的 profile 选择性刷新                                         |
| 🧩 与 Spring Cloud 兼容 | 如果引入 Spring Cloud，会优先使用其原生 @RefreshScope                     |
| 🧼 线程安全 + 高可用        | 所有刷新操作都基于线程安全的 Map 和 Bean 实例引用                               |

------

## **🔨 实现思路**

### **核心流程：**

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

### **核心模块：**

| **模块类**               | **作用**                                               |
|-----------------------|------------------------------------------------------|
| @RefreshScope         | 标记可热刷新的配置类（与 Spring Cloud 同名兼容）                      |
| DynamicConfigLoader   | 负责加载并解析 YAML 配置为扁平 Map                               |
| ConfigFileWatcher     | 监听配置目录中文件变动（修改、保存）                                   |
| ConfigDiffer          | 执行配置差异对比，输出变更字段                                      |
| RefreshScopeRegistry  | 注册所有带有 @RefreshScope + @ConfigurationProperties 的配置类 |
| RefreshScopeRefresher | 按 prefix 匹配并刷新配置类，使用 Spring Binder 动态绑定              |

------

## **📘 使用方式**

### **1. 标记配置类**

```
@AutoConfiguration
@RefreshScope
@ConfigurationProperties(prefix = "custom")
public class CustomProperties {
    private String message;
    private Integer limit;

    // Getter / Setter
}
```

------

### **2. 在主类或配置类中初始化监听器**

```
@Bean
public CommandLineRunner initWatcher(
        DynamicConfigLoader loader,
        RefreshScopeRegistry registry,
        RefreshScopeRefresher refresher,
        Environment env) {

    return args -> {
        AtomicReference<Map<String, Object>> current = new AtomicReference<>(loader.loadCurrentEnvironmentConfig());

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

------

### **3. 获取配置值（无需 @Value）**

```
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

------

## **🧪 示例行为说明**

假设你的配置如下：

```
# application-prod.yml
custom:
  message: "Hello"
  limit: 10
```

运行环境为 prod，当你在部署目录下修改 application-prod.yml 为：

```
custom:
  message: "Hi"
  limit: 20
```

并保存文件时：

```
[Watcher] 配置文件发生变更：application-prod.yml
[DIFF] 变更字段: [custom.message, custom.limit]
[Refresher] 刷新配置类 CustomProperties（前缀：custom.）
```

你不需要重启服务，/hello 接口返回的配置就已更新为最新值。



------

## **📦 与 Spring Cloud 兼容说明**

- @RefreshScope 注解使用与 Spring Cloud 相同包名 org.springframework.cloud.context.config.annotation
- 使用 @ConditionalOnMissingClass 可以避免在 Spring Cloud 存在时加载自定义实现
- 后续可封装为 Spring Boot Starter，自动识别是否接入 Spring Cloud 并切换实现

------

## **🛠️ 可选扩展建议**

| **扩展点**       | **描述**                       |
|---------------|------------------------------|
| ⏱️ 配置变更事件节流   | 增加 debounce 机制，避免频繁刷写        |
| 📊 变更日志持久化    | 记录每次变更的旧值、新值、时间、用户           |
| 🔄 自定义刷新钩子    | 支持配置类刷新后回调方法，例如 @PostRefresh |
| 📦 封装 Starter | 抽出为开源通用依赖，支持一键接入             |

------

## **📁 项目目录结构建议**

```
com.example.refresh
├── annotations
│   └── @RefreshScope.java
├── core
│   ├── DynamicConfigLoader.java
│   ├── ConfigFileWatcher.java
│   ├── ConfigDiffer.java
│   ├── RefreshScopeRegistry.java
│   └── RefreshScopeRefresher.java
├── autoconfig (可选 Starter 扩展)
│   └── RefreshAutoConfiguration.java
└── META-INF
    └── spring.factories / spring.autoconfigure.imports
```

------

## **✅ 结语**

该组件是一个轻量级、无侵入、Spring 原生机制驱动的配置热更新实现，适合：

- 不接入 Spring Cloud 的服务
- 需要配置灵活变更但又不希望重启的场景
- 对配置改动有追踪、可控、分模块刷新需求的架构

------


