---
published: 2025.01.15
---

# Spring Boot 3 兼容性问题修复

## 概述

在使用 Spring Boot 3 时，如果使用了不兼容的 MyBatis Plus 版本，可能会遇到
`Invalid value type for attribute 'factoryBeanObjectType': java.lang.String` 错误。本文档详细说明了该问题的原因、影响和解决方案。

## 问题描述

### 错误信息

```
Invalid value type for attribute 'factoryBeanObjectType': java.lang.String
```

### 触发场景

- 使用 Spring Boot 3.x 版本
- 使用 `mybatis-plus-boot-starter`（适用于 Spring Boot 2.x）
- 应用启动时出现 Bean 定义错误

### 错误原因

Spring Boot 3 对 Bean 定义的要求更加严格，`factoryBeanObjectType` 属性期望的是 `Class<?>` 类型，而不是 `String` 类型。旧版本的
`mybatis-plus-boot-starter` 使用了字符串类型的 `factoryBeanObjectType`，导致与 Spring Boot 3 不兼容。

## 技术背景

### Spring Boot 3 的变化

Spring Boot 3 基于 Spring Framework 6，引入了以下重要变化：

1. **Java 版本要求**：最低要求 Java 17
2. **Jakarta EE**：从 `javax.*` 迁移到 `jakarta.*`
3. **Bean 定义增强**：对 Bean 定义的验证更加严格
4. **类型安全**：更严格的类型检查

### MyBatis Plus 版本兼容性

| MyBatis Plus 版本 | Spring Boot 2.x | Spring Boot 3.x                |
|-----------------|-----------------|--------------------------------|
| 3.4.x - 3.5.x   | ✅ 支持            | ❌ 不支持                          |
| 3.5.3+          | ✅ 支持            | ✅ 支持（需使用 spring-boot3-starter） |

## 解决方案

### 方案一：使用 Spring Boot 3 专用 Starter（推荐）

将 `mybatis-plus-boot-starter` 替换为 `mybatis-plus-spring-boot3-starter`。

#### 修改步骤

**1. 修改依赖管理配置**

在 `cubo-boot-dependencies/pom.xml` 中：

```xml
<!-- 修改前 -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>${mybatis-plus.version}</version>
</dependency>

<!-- 修改后 -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
    <version>${mybatis-plus.version}</version>
</dependency>
```

**2. 修改模块依赖配置**

在 `cubo-mybatis-spring-boot-starter/pom.xml` 中：

```xml
<!-- 修改前 -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
</dependency>

<!-- 修改后 -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
</dependency>
```

在 `cubo-mybatis-spring-boot-autoconfigure/pom.xml` 中：

```xml
<!-- 修改前 -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <optional>true</optional>
</dependency>

<!-- 修改后 -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
    <optional>true</optional>
</dependency>
```

**3. 更新文档说明**

在 `cubo-boot-dependencies/README.md` 中更新依赖示例：

```xml
<!-- 修改前 -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
</dependency>

<!-- 修改后 -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
</dependency>
```

### 方案二：版本要求

确保使用兼容的 MyBatis Plus 版本：

- **MyBatis Plus 3.5.3+**：支持 Spring Boot 3
- **推荐版本**：3.5.15（当前项目使用的版本）

### 验证修复

修复后，应用应该能够正常启动，不再出现 `Invalid value type for attribute 'factoryBeanObjectType'` 错误。

## 影响范围

### 修改的文件

1. `cubo-boot-dependencies/pom.xml`
    - 依赖管理中的 `mybatis-plus-boot-starter` → `mybatis-plus-spring-boot3-starter`

2. `cubo-mybatis-spring-boot-starter/pom.xml`
    - Starter 模块的依赖声明

3. `cubo-mybatis-spring-boot-autoconfigure/pom.xml`
    - 自动配置模块的依赖声明

4. `cubo-boot-dependencies/README.md`
    - 文档中的依赖示例

### 兼容性说明

- ✅ **向后兼容**：对于使用 Spring Boot 2.x 的项目，需要继续使用 `mybatis-plus-boot-starter`
- ✅ **向前兼容**：对于使用 Spring Boot 3.x 的项目，必须使用 `mybatis-plus-spring-boot3-starter`
- ⚠️ **版本要求**：MyBatis Plus 3.5.3+ 才支持 Spring Boot 3

## 相关依赖

### 当前项目配置

```xml
<properties>
    <spring-boot-dependencies.version>3.5.4</spring-boot-dependencies.version>
    <mybatis-plus.version>3.5.15</mybatis-plus.version>
</properties>
```

### 依赖关系

```
cubo-mybatis-spring-boot-starter
    └── mybatis-plus-spring-boot3-starter (3.5.15)
        └── mybatis-plus-extension (3.5.15)
        └── mybatis-plus-jsqlparser (3.5.15)
        └── mybatis-plus-spring (3.5.15)
```

## 注意事项

### 1. 版本一致性

确保所有 MyBatis Plus 相关依赖使用相同的版本：

- `mybatis-plus-spring-boot3-starter`
- `mybatis-plus-extension`
- `mybatis-plus-jsqlparser`
- `mybatis-plus-spring`

### 2. Spring Boot 版本

- Spring Boot 2.x：使用 `mybatis-plus-boot-starter`
- Spring Boot 3.x：使用 `mybatis-plus-spring-boot3-starter`

### 3. 功能差异

`mybatis-plus-spring-boot3-starter` 与 `mybatis-plus-boot-starter` 在功能上完全一致，只是针对 Spring Boot 3 进行了适配。

### 4. 迁移建议

如果项目需要同时支持 Spring Boot 2.x 和 3.x，可以考虑：

- 使用条件依赖（Maven Profile）
- 或者创建两个不同的 Starter 模块

## 最佳实践

### 1. 版本管理

- 统一在 `cubo-boot-dependencies` 中管理 MyBatis Plus 版本
- 使用 `${mybatis-plus.version}` 属性统一版本号

### 2. 依赖声明

- 在依赖管理模块中明确声明 Spring Boot 3 专用 Starter
- 在文档中明确说明版本要求

### 3. 测试验证

- 在 Spring Boot 3 环境下进行完整测试
- 验证所有 MyBatis Plus 功能正常工作

## 总结

`Invalid value type for attribute 'factoryBeanObjectType': java.lang.String` 错误是由于在 Spring Boot 3 环境下使用了不兼容的 MyBatis Plus
Starter 导致的。通过将 `mybatis-plus-boot-starter` 替换为 `mybatis-plus-spring-boot3-starter`，可以完全解决
