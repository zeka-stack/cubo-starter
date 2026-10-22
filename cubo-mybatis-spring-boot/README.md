---
published: 2022.04.11
---

# 数据访问

当前内容对照 3.0.0-SNAPSHOT 源码维护。业务优先采用框架的基类与组件，不复制基础设施实现。

## 组件定位

关系数据库 CRUD、分页、字段填充和 MyBatis 增强。

当前入口 `cubo-mybatis-spring-boot-starter` 直接依赖：`cubo-mybatis-spring-boot-autoconfigure`, `cubo-mybatis-spring-boot-core`, `mybatis-plus-spring-boot3-starter`, `druid-spring-boot-starter`。底层实现通过 starter 传递引入，不重复手动拼装。


## Starter 选型

- [cubo-mybatis-spring-boot-starter](https://zekastack.dong4j.site/docs/skills/cubo-mybatis-spring-boot-starter/SKILL.md)：独立的依赖、选型和编码约定。

## 推荐业务写法

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


## 配置与真实默认值

`zeka-stack.mybatis.single-page-limit` 默认 -1（无限制，应按业务设上限），enable-log/sql-format 默认 false，`performmax-time` 默认 1000，max-length 默认 1000。注意真实拼写是 performmax-time，不是 perform-max-time。

`enable-illegal-sql-interceptor` 与 `enable-sql-explain-interceptor` 默认 false；其 Bean 还受非生产 Profile 约束，不能声称默认在生产阻止全表更新。`enable-sensitive` 是自动配置条件常量，默认启用敏感字段拦截；sensitive-key 通过受控配置提供，不把源码默认值当生产密钥。


## 自动配置与扩展

PaginationInnerInterceptor（缺少同类型 Bean）、MybatisPlusInterceptor、MybatisSqlInjector、敏感字段加解密拦截器、MetaHandlerChain 与时间填充链。非法 SQL / BlockAttack 拦截器需要非生产 Profile 和显式开关。

按具体 MissingBean 类型或名称定制，不重复注册多套 MyBatis Plus 拦截器。时间填充通过基类和 TimeMetaObjectHandler；租户字段填充不等于完整租户数据隔离。


## 验证要求

变更后验证实际应用上下文、接口与依赖版本。文档中的示例不是完整业务授权或运行可靠性保证；配置存在不代表所有环境均装配相同能力。框架工程规范维护在根目录 arch.md，可通过 zeka arch / zeka demo 离线查询。

## 深入阅读

- [detail-jdbc-driver-check](https://zekastack.dong4j.site/docs/cubo-starter/cubo-mybatis-spring-boot/detail-jdbc-driver-check.html)
- [detail-metadata-auto-fill](https://zekastack.dong4j.site/docs/cubo-starter/cubo-mybatis-spring-boot/detail-metadata-auto-fill.html)
- [detail-mybatis-plugin-jdk17-classcast-and-jpms-fix](https://zekastack.dong4j.site/docs/cubo-starter/cubo-mybatis-spring-boot/detail-mybatis-plugin-jdk17-classcast-and-jpms-fix.html)
- [detail-performance-monitoring](https://zekastack.dong4j.site/docs/cubo-starter/cubo-mybatis-spring-boot/detail-performance-monitoring.html)
- [detail-sensitive-field-encryption](https://zekastack.dong4j.site/docs/cubo-starter/cubo-mybatis-spring-boot/detail-sensitive-field-encryption.html)
- [detail-spring-boot3-compatibility](https://zekastack.dong4j.site/docs/cubo-starter/cubo-mybatis-spring-boot/detail-spring-boot3-compatibility.html)
- [detail-sql-interceptors](https://zekastack.dong4j.site/docs/cubo-starter/cubo-mybatis-spring-boot/detail-sql-interceptors.html)
