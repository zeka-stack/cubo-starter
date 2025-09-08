# Cubo Boot Dependencies

## 概述

`cubo-boot-dependencies` 是 Cubo Starter 项目的依赖管理模块，负责统一管理整个项目中所有模块的依赖版本和配置。该模块作为 BOM（Bill of
Materials）使用，为其他模块提供统一的依赖版本控制。

## 主要功能

### 1. 依赖版本管理

- 统一管理所有第三方依赖的版本号
- 避免版本冲突和兼容性问题
- 提供稳定的依赖版本策略

### 2. 模块依赖声明

- 声明所有 Cubo Starter 子模块的依赖关系
- 提供模块间的依赖传递机制
- 简化模块间的依赖配置

### 3. 第三方库集成

- 集成常用的 Spring Boot 生态组件
- 管理数据库、消息队列、日志等中间件依赖
- 提供开箱即用的技术栈支持

## 包含的依赖

### 核心框架

- **Spring Boot Dependencies**: Spring Boot 核心依赖管理
- **Blen Kernel Dependencies**: Zeka.Stack 核心框架依赖

### 数据库相关

- **MyBatis Plus**: 增强的 MyBatis 框架
- **Druid**: 阿里巴巴数据库连接池
- **P6spy**: SQL 监控和日志记录

### 消息队列

- **RocketMQ Spring Boot Starter**: 阿里巴巴消息队列
- **Kafka**: Apache 分布式流处理平台

### 其他组件

- **OpenAPI**: API 文档生成
- **REST**: RESTful API 支持
- **Endpoint**: 应用端点管理
- **LogSystem**: 日志系统
- **Launcher**: 应用启动器
- **Messaging**: 消息处理

## 使用方式

### 1. 作为 BOM 引入

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>dev.dong4j</groupId>
            <artifactId>cubo-boot-dependencies</artifactId>
            <version>${cubo-boot-dependencies.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### 2. 直接引入依赖

```xml
<dependencies>
    <!-- 引入具体的 Cubo Starter 模块 -->
    <dependency>
        <groupId>dev.dong4j</groupId>
        <artifactId>cubo-rest-spring-boot-starter</artifactId>
    </dependency>

    <!-- 引入第三方依赖（版本由 BOM 管理） -->
    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-boot-starter</artifactId>
    </dependency>
</dependencies>
```

## 配置说明

### 版本属性

| 属性名                                    | 默认值                 | 说明                              |
|----------------------------------------|---------------------|---------------------------------|
| `cubo-boot-dependencies.version`       | `${global.version}` | Cubo Boot 依赖版本                  |
| `p6spy.version`                        | `3.9.1`             | P6spy 版本                        |
| `druid.version`                        | `1.2.4`             | Druid 连接池版本                     |
| `rocketmq-spring-boot-starter.version` | `2.2.3`             | RocketMQ Spring Boot Starter 版本 |

### 依赖范围

- **provided**: 可选依赖，如 `cubo-logsystem-simple`
- **默认**: 传递依赖，会被其他模块自动引入

## 最佳实践

### 1. 版本管理

- 定期更新依赖版本，保持技术栈的先进性
- 在升级前进行充分的兼容性测试
- 使用版本范围时注意边界情况

### 2. 依赖选择

- 优先使用 BOM 中已管理的依赖
- 避免重复声明版本号
- 注意依赖的传递性和冲突解决

### 3. 模块集成

- 通过 BOM 引入模块依赖
- 利用自动配置减少手动配置
- 遵循模块间的依赖关系

## 注意事项

1. **版本兼容性**: 确保所有依赖版本之间的兼容性
2. **传递依赖**: 注意依赖的传递性，避免引入不必要的依赖
3. **冲突解决**: 当出现依赖冲突时，优先使用 BOM 中定义的版本
4. **性能影响**: 合理选择依赖范围，避免引入过重的依赖

## 更新日志

### v2.0.0-SNAPSHOT

- 升级到 Spring Boot 3.x
- 更新 MyBatis Plus 到最新版本
- 优化依赖管理结构
- 新增 RocketMQ 支持

## 相关链接

- [Spring Boot Dependencies](https://docs.spring.io/spring-boot/docs/current/reference/html/dependency-versions.html)
- [MyBatis Plus](https://baomidou.com/)
- [Druid 连接池](https://github.com/alibaba/druid)
- [RocketMQ](https://rocketmq.apache.org/)
