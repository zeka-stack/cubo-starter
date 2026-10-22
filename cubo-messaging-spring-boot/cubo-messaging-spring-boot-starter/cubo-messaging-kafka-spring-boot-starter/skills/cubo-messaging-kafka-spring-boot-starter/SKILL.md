---
name: cubo-messaging-kafka-spring-boot-starter
description: 使用 Zeka Stack 统一消息发送与监听 编写业务功能、选择 starter、配置和排查装配时使用。
license: MIT
---

# cubo-messaging-kafka-spring-boot-starter

## 适用场景与组件定位

使用 Kafka 或 RocketMQ，复用统一消息模型，业务负责消费幂等和失败策略。

当前入口 `cubo-messaging-kafka-spring-boot-starter` 直接依赖：`cubo-messaging-kafka`。底层实现通过 starter 传递引入，不重复手动拼装。

## 依赖方式

```xml
<dependency>
    <groupId>dev.dong4j</groupId>
    <artifactId>cubo-messaging-kafka-spring-boot-starter</artifactId>
</dependency>
```

## 选型

| 入口 | 适用场景 |
| --- | --- |
| `cubo-messaging-kafka-spring-boot-starter` | Kafka 集群 |
| `cubo-messaging-rocketmq-spring-boot-starter` | RocketMQ 集群 |

## 使用方式与最佳实践

构造注入 `dev.dong4j.zeka.starter.messaging.template.MessagingTemplate`；使用 `dev.dong4j.zeka.starter.messaging.model.UnifiedMessage`。

```java
// payload 应为约定的消息内容，destination 是真实主题。
var message = new UnifiedMessage("account-events", "created");
var result = messagingTemplate.sendSync(message);
```

公开发送 API 是 sendSync / sendAsync / sendOneWay，不存在 send(topic, payload)。withKey 接受 MessageKey，不是任意字符串；多实现用 forType(MessagingType.KAFKA/ROCKETMQ) 选择。

监听方法使用 `@MessagingListener(topic = "account-events", groupId = "account-service")`，可指定 type。当前注册器按 payload 解析参数，优先使用与发送方契约一致的简单负载并做真实 broker 测试；不默认假定 DTO 自动转换。注解没有 topics、filter、retryAttempts、batchSize 等属性。

本地数据库事务不自动覆盖发送，框架不会自动保证分布式事务、可靠投递和业务幂等。

## 通用编码约束

使用构造器注入，统一错误码 `BaseCodes` / `LowestExceptionAssert`，不新建重复响应或工具类。新增类与关键方法说明业务约束，先查询真实方法签名再调用。配置以属性类和自动装配条件为准；只有带 `@ConditionalOnMissingBean` 的具体 Bean 才能按其条件覆盖，不能通过返回 null 禁用。

依赖版本由业务项目的 `cubo-boot-dependencies` BOM 管理，当前内容对应 3.0.0-SNAPSHOT；不同版本先核对 API。

## 配置规则

前缀 `zeka-stack.messaging`。auto-detect 默认 true，disabled-types 默认空，default-type 可显式选择，class-detect-timeout=500ms，enable-cache=true，cache-expiration=300 秒。kafka.enabled / rocketmq.enabled 默认 true；检测开关不等于底层容器和适配器全部被禁用。

Kafka broker 使用 spring.kafka.*，RocketMQ 使用 rocketmq.*。没有 messaging.template.enabled。TongHTP 有配置字段不代表已有可依赖 starter 和实现。

## 自动装配与自定义

MessagingTemplateAutoConfiguration 根据 KafkaTemplate / RocketMQTemplate Bean 注册对应适配器，并提供 DefaultMessagingTemplate。MessagingContainerConfiguration 提供 MessagingTypeDetector、MessagingListenerRegistry、注册处理器和中间件容器注册器。

只有 KafkaListenerEndpointRegistry 等特定 Bean 有 MissingBean 条件；不要重复创建整个模板系统。单一中间件缺少另一个实现时的类路径隔离需按所用版本做上下文验证，不能仅靠属性假定所有可选类都会隔离。

## 不要这样做

不虚构配置键、注解属性、方法和 Bean 覆盖条件；不把选型对照中的另一种技术栈当成本 starter 的能力；不通过新建基础设施绕过框架已有封装。返回的成功与异常结构必须与业务项目协议一致。修改后通过实际应用上下文或接口测试确认行为，静态扫描不能证明运行时装配成功。
