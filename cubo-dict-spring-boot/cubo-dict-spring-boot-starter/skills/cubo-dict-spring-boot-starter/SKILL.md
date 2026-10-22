---
name: cubo-dict-spring-boot-starter
description: 使用 Zeka Stack 字典服务 编写业务功能、选择 starter、配置和排查装配时使用。
license: MIT
---

# cubo-dict-spring-boot-starter

## 适用场景与组件定位

基于 MyBatis 与 Servlet 提供字典选项查询与缓存管理。

当前入口 `cubo-dict-spring-boot-starter` 直接依赖：`cubo-dict-spring-boot-autoconfigure`, `cubo-dict-spring-boot-core`。底层实现通过 starter 传递引入，不重复手动拼装。

## 依赖方式

```xml
<dependency>
    <groupId>dev.dong4j</groupId>
    <artifactId>cubo-dict-spring-boot-starter</artifactId>
</dependency>
```

## 选型

| 入口 | 适用场景 |
| --- | --- |
| `cubo-dict-spring-boot-starter` | 基于 MyBatis 与 Servlet 提供字典选项查询与缓存管理。 |
| `原生 Spring Boot` | 不需要 Zeka 工程约定 |

## 使用方式与最佳实践

构造注入 `dev.dong4j.zeka.starter.dict.service.DictionaryService`，调用 getDictionaryOptions(typeCode) / getAllDictionaryOptions()，无需另写一套字典 Mapper 和接口。

持久表为 sys_dict_type / sys_dict_value。GET /dicts/options/{typeCode}、GET /dicts/options；POST /dicts/refresh/{typeCode}、/dicts/refresh、/dicts/clear/{typeCode}、/dicts/clear。写端点由业务安全层保护。返回 Result，成功 code=2000，不是字符串 success。

组件依赖 Servlet 与 MyBatis，不用于无数据库或纯 Reactive 服务。

## 通用编码约束

使用构造器注入，统一错误码 `BaseCodes` / `LowestExceptionAssert`，不新建重复响应或工具类。新增类与关键方法说明业务约束，先查询真实方法签名再调用。配置以属性类和自动装配条件为准；只有带 `@ConditionalOnMissingBean` 的具体 Bean 才能按其条件覆盖，不能通过返回 null 禁用。

依赖版本由业务项目的 `cubo-boot-dependencies` BOM 管理，当前内容对应 3.0.0-SNAPSHOT；不同版本先核对 API。

## 配置规则

`zeka-stack.dict.enabled` 默认条件启用。属性类默认 enable-cache=true、preload-cache=true、cache-type=memory、cache-expire-time=3600 秒、cache-refresh-delay=100ms。

**当前装配条件差异：** cache-type 与 preload-cache 的 Bean 条件没有 matchIfMissing，因此必须在 YAML 中显式写 cache-type: memory 和 preload-cache: true 才能依赖对应默认装配。enable-cache 虽有字段，不是切换缓存 Bean 的条件；禁用缓存选择 cache-type: none。没有 table-prefix / consistency-check 等属性。

```yaml
zeka-stack:
  dict:
    enabled: true
    cache-type: memory
    preload-cache: true
    cache-expire-time: 3600
```


## 自动装配与自定义

DictAutoConfiguration：DictionaryService（缺失时）、DictionaryCache（cache-type=memory/none 且缺失时）、DictionaryEventListener（缺失时）、DictionaryCachePreloader（preload-cache=true 且缺失时）。可按这些条件提供自定义服务或缓存，不重复注册组件 Controller，不通过虚构表前缀修改实际表名。

## 不要这样做

不虚构配置键、注解属性、方法和 Bean 覆盖条件；不把选型对照中的另一种技术栈当成本 starter 的能力；不通过新建基础设施绕过框架已有封装。返回的成功与异常结构必须与业务项目协议一致。修改后通过实际应用上下文或接口测试确认行为，静态扫描不能证明运行时装配成功。
