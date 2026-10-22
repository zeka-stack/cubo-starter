---
name: cubo-mybatis-spring-boot-starter
description: 使用 Zeka Stack 统一持久层 编写业务功能、选择 starter、配置和排查装配时使用。
license: MIT
---

# cubo-mybatis-spring-boot-starter

## 适用场景与组件定位

关系数据库 CRUD、分页、字段填充和 MyBatis 增强。

当前入口 `cubo-mybatis-spring-boot-starter` 直接依赖：`cubo-mybatis-spring-boot-autoconfigure`, `cubo-mybatis-spring-boot-core`, `mybatis-plus-spring-boot3-starter`, `druid-spring-boot-starter`。底层实现通过 starter 传递引入，不重复手动拼装。

## 依赖方式

```xml
<dependency>
    <groupId>dev.dong4j</groupId>
    <artifactId>cubo-mybatis-spring-boot-starter</artifactId>
</dependency>
```

## 选型

| 入口 | 适用场景 |
| --- | --- |
| `cubo-mybatis-spring-boot-starter` | 关系数据库 CRUD、分页、字段填充和 MyBatis 增强。 |
| `原生 Spring Boot` | 不需要 Zeka 工程约定 |

## 使用方式与最佳实践

依赖 MyBatis Plus、Druid 与框架自动配置。DAO 用 `dev.dong4j.zeka.starter.mybatis.base.BaseDao<PO>`，Service 接口用 `BaseService<PO>`，实现用 `BaseServiceImpl<Mapper, PO>`。不要在新业务另建裸 BaseMapper / ServiceImpl 体系。

```java
import dev.dong4j.zeka.starter.mybatis.base.BaseDao;
import org.apache.ibatis.annotations.Mapper;
/** Account 是本业务 PO，复用框架增强 DAO。 */
@Mapper
public interface AccountMapper extends BaseDao<Account> { }
```

PO 依表结构选 BasePO（id）、BaseWithTimePO（id + 时间）、BaseWithLogicPO（id + deleted）、BaseExtendPO（id + 时间 + deleted）。BaseWithTimePO 不含 deleted；不要重复定义继承字段，不给 PO 加 SuperBuilder。Form/DTO/Query 使用 kernel.common.base 基类。

转换用 MapStruct ExtendConverter 的 f2p/p2d；事务放在 Service public 方法，必要时 rollbackFor=Exception.class。普通新增用 insert，业务明确忽略冲突才用 insertIgnore（MySQL 风格）。分页参数和排序白名单由服务端控制，XML 用 #{} 参数绑定。

## 通用编码约束

使用构造器注入，统一错误码 `BaseCodes` / `LowestExceptionAssert`，不新建重复响应或工具类。新增类与关键方法说明业务约束，先查询真实方法签名再调用。配置以属性类和自动装配条件为准；只有带 `@ConditionalOnMissingBean` 的具体 Bean 才能按其条件覆盖，不能通过返回 null 禁用。

依赖版本由业务项目的 `cubo-boot-dependencies` BOM 管理，当前内容对应 3.0.0-SNAPSHOT；不同版本先核对 API。

## 配置规则

`zeka-stack.mybatis.single-page-limit` 默认 -1（无限制，应按业务设上限），enable-log/sql-format 默认 false，`performmax-time` 默认 1000，max-length 默认 1000。注意真实拼写是 performmax-time，不是 perform-max-time。

`enable-illegal-sql-interceptor` 与 `enable-sql-explain-interceptor` 默认 false；其 Bean 还受非生产 Profile 约束，不能声称默认在生产阻止全表更新。`enable-sensitive` 是自动配置条件常量，默认启用敏感字段拦截；sensitive-key 通过受控配置提供，不把源码默认值当生产密钥。

## 自动装配与自定义

PaginationInnerInterceptor（缺少同类型 Bean）、MybatisPlusInterceptor、MybatisSqlInjector、敏感字段加解密拦截器、MetaHandlerChain 与时间填充链。非法 SQL / BlockAttack 拦截器需要非生产 Profile 和显式开关。

按具体 MissingBean 类型或名称定制，不重复注册多套 MyBatis Plus 拦截器。时间填充通过基类和 TimeMetaObjectHandler；租户字段填充不等于完整租户数据隔离。

## 不要这样做

不虚构配置键、注解属性、方法和 Bean 覆盖条件；不把选型对照中的另一种技术栈当成本 starter 的能力；不通过新建基础设施绕过框架已有封装。返回的成功与异常结构必须与业务项目协议一致。修改后通过实际应用上下文或接口测试确认行为，静态扫描不能证明运行时装配成功。
