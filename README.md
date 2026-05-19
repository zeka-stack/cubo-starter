---
published: 2022.03.07
---

# Starter 组件

[![License](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://openjdk.java.net/)

## 概述

Cubo Starter 是基于 Spring Boot 3.x 的企业级 Starter 组件集合，为 Java 开发者提供开箱即用的技术栈解决方案。该项目采用模块化设计，每个模块都包含
core、autoconfigure 和 starter 子模块，并集成了 SPI 逻辑用于组件初始化配置。

## 项目特点

- 🚀 **开箱即用**: 基于 Spring Boot 自动配置，零配置即可使用
- 🧩 **模块化设计**: 每个模块独立，可按需引入
- 🔧 **高度可配置**: 丰富的配置选项，满足不同场景需求
- 📚 **完整文档**: 详细的文档和使用示例
- 🔄 **配置热更新**: 支持非 Spring Cloud 环境下的配置热更新
- 🛡️ **企业级特性**: 包含日志、监控、安全等企业级功能
- 🌐 **多技术栈支持**: 支持 Servlet 和 Reactive 两种 Web 技术栈

## 技术栈

- **Spring Boot**: 3.x
- **Java**: 17+
- **构建工具**: Maven
- **数据库**: MySQL、PostgreSQL、Oracle
- **ORM**: MyBatis Plus
- **消息队列**: Kafka、RocketMQ
- **缓存**: Redis
- **文档**: OpenAPI 3.0、Knife4j
- **监控**: Micrometer、Spring Boot Actuator

## 模块架构

```
cubo-starter/
├── cubo-boot-dependencies/              # 依赖管理模块
├── cubo-launcher-spring-boot/           # 应用启动器
├── cubo-logsystem-spring-boot/          # 日志系统
├── cubo-messaging-spring-boot/          # 消息处理
├── cubo-mybatis-spring-boot/            # 数据访问
├── cubo-openapi-spring-boot/            # API 文档
├── cubo-rest-spring-boot/               # REST API
├── cubo-endpoint-spring-boot/           # 端点管理
└── cubo-combiner-spring-boot/           # 组合器模块
```

## 核心模块

### 🚀 [cubo-launcher-spring-boot](cubo-launcher-spring-boot/)

应用启动器模块，提供应用启动时的核心组件初始化和配置管理。

**主要功能**:

- 应用启动管理
- 配置热更新（非 Spring Cloud 环境）
- 扩展点管理（SPI 机制）

**核心特性**:

- ✅ 实时监听配置文件变更
- ✅ 配置变更差异分析
- ✅ 精准配置类刷新
- ✅ 与 Spring Cloud 兼容

### 📝 [cubo-logsystem-spring-boot](cubo-logsystem-spring-boot/)

日志系统模块，提供完整的日志解决方案。

**主要功能**:

- 多日志框架支持（Log4j2、Simple Logger、Record Logger）
- 日志级别动态刷新
- 日志记录和追踪

**核心特性**:

- ✅ 运行时动态调整日志级别
- ✅ 支持按包、类、方法等不同粒度控制
- ✅ 异步日志和性能优化
- ✅ 日志持久化存储

### 📨 [cubo-messaging-spring-boot](cubo-messaging-spring-boot/)

消息处理模块，提供统一的消息队列抽象。

**主要功能**:

- 多消息中间件支持（Kafka、RocketMQ）
- 消息发送和接收
- 消息事务处理

**核心特性**:

- ✅ 统一的 API 接口
- ✅ 支持同步和异步消息发送
- ✅ 分布式事务消息
- ✅ 消息的可靠投递保证

### 🗄️ [cubo-mybatis-spring-boot](cubo-mybatis-spring-boot/)

数据访问模块，基于 MyBatis Plus 提供增强的数据库操作功能。

**主要功能**:

- MyBatis Plus 增强
- SQL 拦截器（非法 SQL、攻击拦截、分页）
- 性能监控
- 敏感字段加解密
- 元数据自动填充

**核心特性**:

- ✅ SQL 性能监控和慢查询检测
- ✅ 敏感字段自动加解密
- ✅ 自动填充创建时间、更新时间等字段
- ✅ 支持多数据源

### 📖 [cubo-openapi-spring-boot](cubo-openapi-spring-boot/)

API 文档模块，提供完整的 API 文档生成和管理功能。

**主要功能**:

- API 文档生成（基于 OpenAPI 3.0）
- 多文档工具支持（Knife4j、Dubbo）
- 文档增强功能

**核心特性**:

- ✅ 自动生成 API 文档
- ✅ 支持 API 分组和标签管理
- ✅ 提供接口测试和调试功能
- ✅ 支持多种认证方式

### 🌐 [cubo-rest-spring-boot](cubo-rest-spring-boot/)

REST API 模块，提供完整的 RESTful API 开发支持。

**主要功能**:

- 多 Web 技术栈支持（Servlet、Reactive）
- 统一异常处理
- 参数验证
- 响应封装
- HTTP 客户端支持

**核心特性**:

- ✅ 全局异常处理器
- ✅ 统一的 API 响应格式
- ✅ 生产环境下快速失败模式
- ✅ 支持多种 HTTP 客户端

### 📊 [cubo-endpoint-spring-boot](cubo-endpoint-spring-boot/)

端点管理模块，提供应用监控和管理端点功能。

**主要功能**:

- 应用信息端点
- 健康检查端点
- 性能监控端点
- 管理端点

**核心特性**:

- ✅ 应用基本信息展示
- ✅ 数据库、消息队列等组件健康检查
- ✅ 系统资源监控
- ✅ 支持自定义监控指标

### 🔗 [cubo-combiner-spring-boot](cubo-combiner-spring-boot/)

组合器模块，用于聚合多个 Starter 组件。

**主要功能**:

- 依赖聚合
- 预定义组合
- 版本管理
- 配置简化

**预定义组合**:

- **Framework Starter**: 基础框架组合
- **SSM Starter**: Spring + Spring MVC + MyBatis 组合

## 快速开始

### 1. 引入依赖

#### 使用 SSM 技术栈组合（推荐）

```xml
<dependency>
    <groupId>dev.dong4j</groupId>
    <artifactId>cubo-ssm-spring-boot-starter</artifactId>
    <version>3.0.0-SNAPSHOT</version>
</dependency>
```

#### 使用基础框架组合

```xml
<dependency>
    <groupId>dev.dong4j</groupId>
    <artifactId>cubo-framework-spring-boot-starter</artifactId>
    <version>3.0.0-SNAPSHOT</version>
</dependency>
```

#### 单独引入模块

```xml
<dependency>
    <groupId>dev.dong4j</groupId>
    <artifactId>cubo-mybatis-spring-boot-starter</artifactId>
    <version>3.0.0-SNAPSHOT</version>
</dependency>
```

### 2. 基础配置

```yaml
# 应用配置
spring:
  application:
    name: "my-application"
    version: "1.0.0"

# 数据库配置
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/test
    username: root
    password: password
    driver-class-name: com.mysql.cj.jdbc.Driver

# Cubo 配置
zeka-stack:
  launcher:
    enabled: true
    refresh-scope:
      enabled: true
  mybatis:
    enabled: true
    single-page-limit: 500
  rest:
    enabled: true
  openapi:
    enabled: true
    title: "API 文档"
```

### 3. 创建应用

```java
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

### 4. 创建 Controller

```java
@RestController
@RequestMapping("/api/users")
@Api(tags = "用户管理")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    @ApiOperation(value = "根据ID获取用户")
    public ApiResponse<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ApiResponse.success(user);
    }
}
```

## 配置说明

### 全局配置

| 配置项                           | 默认值  | 说明          |
|-------------------------------|------|-------------|
| `zeka-stack.launcher.enabled` | true | 启用启动器       |
| `zeka-stack.mybatis.enabled`  | true | 启用 MyBatis  |
| `zeka-stack.rest.enabled`     | true | 启用 REST API |
| `zeka-stack.openapi.enabled`  | true | 启用 API 文档   |
| `zeka-stack.endpoint.enabled` | true | 启用端点管理      |

### 模块配置

每个模块都有详细的配置选项，请参考各模块的 README 文档：

- [[cubo-starter/cubo-launcher-spring-boot/index#配置属性|启动器配置]]
- [[cubo-starter/cubo-logsystem-spring-boot/index#配置属性|日志系统配置]]
- [[cubo-starter/cubo-messaging-spring-boot/index#配置属性|消息处理配置]]
- [[cubo-starter/cubo-mybatis-spring-boot/index#配置属性|数据访问配置]]
- [[cubo-starter/cubo-openapi-spring-boot/index#配置属性|API 文档配置]]
- [[cubo-starter/cubo-rest-spring-boot/index#配置属性|REST API 配置]]
- [[cubo-starter/cubo-endpoint-spring-boot/index#配置属性|端点管理配置]]

## 示例项目

我们提供了完整的示例项目，展示如何使用各个模块：

- [cubo-starter-examples](https://github.com/zeka-stack/cubo-starter-examples): 包含所有模块的使用示例

## 最佳实践

### 1. 模块选择

- 根据项目需求选择合适的模块组合
- 优先使用预定义的组合（如 SSM Starter）
- 避免引入不必要的模块

### 2. 配置管理

- 使用配置文件管理不同环境的配置
- 合理使用配置热更新功能
- 注意配置的安全性和敏感性

### 3. 性能优化

- 合理配置连接池参数
- 使用异步处理提高性能
- 监控系统资源使用情况

### 4. 安全考虑

- 配置适当的认证和授权
- 保护敏感配置信息
- 定期更新依赖版本

## 版本说明

### 3.0.0-SNAPSHOT

- AI Native 集成与开发

### 2.0.0-SNAPSHOT

**主要特性**:

- 升级到 Spring Boot 3.x
- 支持 Java 17+
- 优化模块结构和依赖管理
- 增强配置热更新功能
- 改进文档和示例

**版本兼容性**:

- Spring Boot: 3.x
- Java: 17+
- Maven: 3.6+

## 贡献指南

我们欢迎社区贡献！请遵循以下步骤：

1. Fork 本项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](https://github.com/zeka-stack/zeka-stack/blob/main/LICENSE) 文件了解详情。

## 联系方式

- **作者**: dong4j
- **邮箱**: dong4j@gmail.com
- **项目地址**: https://github.com/zeka-stack/cubo-starter
- **问题反馈**: https://github.com/zeka-stack/cubo-starter/issues

## 致谢

感谢以下开源项目的支持：

- [Spring Boot](https://spring.io/projects/spring-boot)
- [MyBatis Plus](https://baomidou.com/)
- [Knife4j](https://doc.xiaominfo.com/)
- [Druid](https://github.com/alibaba/druid)
- [RocketMQ](https://rocketmq.apache.org/)
- [Kafka](https://kafka.apache.org/)

---

**Cubo Starter** - 让 Spring Boot 开发更简单！ 🚀
