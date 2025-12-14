---
published: 2025.12.14
---

# MyBatis 插件在 JDK 17 下的双重异常排查与修复

## 概述

项目中基于 MyBatis Interceptor 实现了一个结果集字典绑定插件（`DataBindInterceptor`），用于在 SQL 查询完成后，对返回的对象列表进行字段级别的字典翻译。

该插件在 JDK 8 环境下长期稳定运行，但在升级至 JDK 17 后，启动及运行过程中接连出现异常，且错误信息与以往完全不同。本文档详细记录了问题的排查过程、根本原因分析以及最终的修复方案。

## 问题背景

### 插件功能

`DataBindInterceptor` 是一个 MyBatis 拦截器，主要功能包括：

- **结果集拦截**：拦截 `ResultSetHandler.handleResultSets` 方法
- **字典绑定**：对查询结果中的字段进行字典值翻译
- **自动处理**：通过注解驱动，自动识别需要翻译的字段

### 核心实现

```java
@Intercepts( {@Signature(
    type = ResultSetHandler.class,
    method = "handleResultSets",
    args = {Statement.class}
)})
@SuppressWarnings("all")
public class DataBindInterceptor implements Interceptor {
```

### 升级影响

- **JDK 8**：插件运行正常，无异常
- **JDK 17**：启动及运行时出现双重异常
    - 第一次异常：`ClassCastException`
    - 第二次异常：模块系统禁止反射访问

## 第一次异常：ClassCastException（代理类型误用）

### 异常现象

```
java.lang.ClassCastException:
jdk.proxy2.$Proxy185 cannot be cast to
org.apache.ibatis.executor.resultset.DefaultResultSetHandler
```

详细错误信息：

```
jdk.proxy2.$Proxy185 is in module jdk.proxy2 of loader 'app';
org.apache.ibatis.executor.resultset.DefaultResultSetHandler
is in unnamed module of loader 'app'
```

### 出错代码

原始的错误实现：

```java
DefaultResultSetHandler resultSetHandler =
    (DefaultResultSetHandler) invocation.getTarget();
```

### 根因分析

#### MyBatis 插件的真实对象结构

MyBatis 插件并不是直接作用在原始对象上，而是通过 JDK 动态代理进行增强：

```
$ProxyXXX (JDK Proxy, module jdk.proxy2)
 └── Plugin
     └── DefaultResultSetHandler
```

因此：

- `invocation.getTarget()` 返回的是 JDK Proxy
- 而不是 `DefaultResultSetHandler` 本体

#### JDK 动态代理的关键特性

- 继承自 `java.lang.reflect.Proxy`
- 只实现接口，不继承具体类
- 永远不是具体实现类

因此，将其强转为具体实现类在任何 JDK 版本下都是错误设计，只是在 JDK 9+ 模块系统（JPMS）中被更明确地暴露出来。

### 第一阶段修复思路（初步）

- 不再直接强转实现类
- 改为使用 `MetaObject` 解代理，获取真实对象

## 第二次异常：模块系统禁止反射 JDK 内部结构

在修复第一次异常后，又出现了新的异常。

### 异常现象

```
Unable to make field
protected java.lang.reflect.InvocationHandler java.lang.reflect.Proxy.h
accessible:

module java.base does not "opens java.lang.reflect"
to unnamed module
```

出错位置：

```java
Object h = metaObject.getValue("h");
```

### 问题本质

这是 JDK 9+ 模块系统（JPMS）引入的强约束。

#### h 字段说明

`h` 是 JDK Proxy 的内部字段：

```java
java.lang.reflect.Proxy {
    protected InvocationHandler h;
}
```

- `h` 是 JDK Proxy 的内部字段
- 位于 `java.base` 模块
- 包路径为 `java.lang.reflect`

### 为什么 JDK 17 直接失败

在 JDK 9 之后：

**业务代码禁止通过反射访问 `java.base` 中的非 public 成员**

即使使用 `setAccessible(true)`，也会被模块系统直接拒绝。

### 这是"新问题"吗？

不是。这是一个历史技术债：

- **JDK 8 之前**：允许但不安全
- **JDK 9+**：明确禁止
- **JDK 17**：彻底执行

这是一次被 JDK 升级"强制暴露"的历史技术债。

## 错误路线总结

两次踩坑的共同点：

| 错误做法                     | 本质问题   |
|--------------------------|--------|
| 强转 Proxy 为实现类            | 违反类型系统 |
| 反射 Proxy.h 字段            | 违反模块系统 |
| 依赖 `setAccessible(true)` | 不可持续   |

## 最终正确解法（官方认可、长期可用）

### 核心设计原则

**MyBatis 插件只应该解 MyBatis 自己的代理，永远不应该触碰 JDK Proxy 的内部结构。**

### 正确解代理方式

使用 MyBatis Plus 官方工具类 `PluginUtils.realTarget()`：

```java
Object target = PluginUtils.realTarget(invocation.getTarget());
```

该方法的特点：

- 只解 Plugin 包装
- 不反射 Proxy.h
- 不依赖 JVM 启动参数
- JDK 8 ~ 21 完全兼容

### 最终修复后的完整代码

```java
@Override
public Object intercept(Invocation invocation) throws Throwable {
    Object result = invocation.proceed();

    if (!(result instanceof List<?> list) || list.isEmpty()) {
        return result;
    }

    // 交给 MyBatis 解代理
    Object target = PluginUtils.realTarget(invocation.getTarget());

    if (!(target instanceof DefaultResultSetHandler handler)) {
        return result;
    }

    MetaObject metaObject = SystemMetaObject.forObject(handler);
    MappedStatement ms =
        (MappedStatement) metaObject.getValue("mappedStatement");
    Configuration configuration = ms.getConfiguration();

    for (Object row : list) {
        if (row == null) {
            continue;
        }

        // 检查是否需要翻译，是否需要翻译的标准是，检查目标对象的Class是否有自定义的注解，
        // 有的话，调用字典数据绑定，取修改对象的target属性
        boolean needTranslate = DataBindUtil.needTranslate(
            configuration,
            row,
            (m, f) -> {
                // 得到自定义注解
                FieldBind fieldBind = f.getFieldBind();
                // 得到具体属性的值
                Object value = m.getValue(f.getName());
                dictBind.setMetaObject(fieldBind, value, m);
            });

        if (!needTranslate) {
            continue;
        }
    }

    return result;
}
```

### 关键修复点

1. **使用 `PluginUtils.realTarget()`**：正确解代理，获取真实对象
2. **类型检查**：使用 `instanceof` 进行安全的类型检查
3. **避免反射 JDK 内部结构**：不再访问 `Proxy.h` 字段

## 为什么不使用 --add-opens

虽然可以通过 JVM 启动参数绕过问题：

```bash
--add-opens java.base/java.lang.reflect=ALL-UNNAMED
```

但这意味着：

- 插件强依赖 JVM 启动参数
- 云原生环境不可控
- JDK 升级风险极高
- 框架设计不合格

**框架级代码不应该要求该参数**

## 经验总结（可复用）

### MyBatis 插件开发准则

1. **只面向接口编程**：不要强转具体实现类
2. **永远假设目标对象是代理**：使用工具类解代理
3. **使用 MetaObject / PluginUtils**：官方提供的安全工具
4. **不反射 JDK 内部类**：避免模块系统限制
5. **不依赖 JVM 绕过参数**：保证框架的可移植性

### 关于 JDK 升级的正确认知

**JDK 升级不是引入问题，而是让错误的设计"无法再被容忍"。**

## 技术要点

### PluginUtils.realTarget() 方法说明

`PluginUtils.realTarget()` 是 MyBatis Plus 提供的工具方法，用于安全地获取被代理对象的真实实例：

- **作用范围**：只解 MyBatis Plugin 的包装
- **安全性**：不涉及 JDK Proxy 内部结构
- **兼容性**：支持 JDK 8 到 JDK 21+

### 模块系统（JPMS）的影响

JDK 9+ 引入的模块系统对反射访问进行了严格限制：

- `java.base` 模块中的非 public 成员禁止反射访问
- 即使使用 `setAccessible(true)` 也无法绕过
- 这是为了增强安全性和封装性

## 结语

这次问题的本质并非 MyBatis 或 JDK 的不兼容，而是：

- 错误使用代理类型
- 越权访问 JDK 内部结构
- 历史代码在新平台下被彻底揭示
