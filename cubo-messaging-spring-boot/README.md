---
published: 2022.04.18
---

# 消息处理

提供 Kafka / RocketMQ 的统一发送、监听与消息模型。使用对应具体 starter；消息一致性、重试、幂等和事务保障仍需结合业务与中间件设计，不能仅凭引入依赖认为已获得这些保证。

## 依赖选型

| 中间件 | artifactId（groupId 为 dev.dong4j） |
| --- | --- |
| Kafka | cubo-messaging-kafka-spring-boot-starter |
| RocketMQ | cubo-messaging-rocketmq-spring-boot-starter |

版本由项目 BOM 管理。不要依赖 packaging 为 pom 的聚合 starter，不为单一中间件同时引入两个实现。

## 消息发送

```java
import dev.dong4j.zeka.starter.messaging.model.UnifiedMessage;
import dev.dong4j.zeka.starter.messaging.template.MessagingTemplate;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/** 消息契约由业务统一维护，调用方负责失败补偿。 */
@Service
@AllArgsConstructor
public class AccountEventService {
    private final MessagingTemplate messagingTemplate;

    /** 使用真实发送 API；发送成功不等同于消费成功。 */
    public void publishCreated(String event) {
        this.messagingTemplate.sendSync(new UnifiedMessage("account-events", event));
    }
}
```

API 为 sendSync(UnifiedMessage)、sendAsync(UnifiedMessage)、sendOneWay(UnifiedMessage)。withKey 接收 MessageKey，多实现选择用 forType(MessagingType)。异步返回 CompletableFuture，调用方必须处理失败。

## 消息监听

```java
import dev.dong4j.zeka.starter.messaging.annotation.MessagingListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/** 示例使用简单 payload；真实业务补充幂等与失败处理。 */
@Slf4j
@Component
public class AccountEventListener {
    /** 消费组由业务分配，不记录完整消息中的敏感数据。 */
    @MessagingListener(topic = "account-events", groupId = "account-service")
    public void onCreated(String event) {
        log.info("收到账户事件");
    }
}
```

注解实际属性只有 topic、groupId、type。注册器按 payload 解析方法参数，复杂 DTO 转换需要实测；不能编写不存在的 topics、filter、retryAttempts 或 batchSize 属性。组件没有名为 MessageSerializer 的业务扩展接口，序列化按实际适配器与中间件配置验证。

## 配置与装配

```yaml
zeka-stack:
  messaging:
    auto-detect: true
    default-type: KAFKA
    enable-cache: true
    cache-expiration: 300
spring:
  kafka:
    bootstrap-servers: localhost:9092
```

前缀是 zeka-stack.messaging。auto-detect 默认 true；disabled-types 默认为空；class-detect-timeout 为 500ms；enable-cache 为 true；cache-expiration 为 300 秒。kafka.enabled / rocketmq.enabled 默认 true，用于类型检测配置；不要据此假定所有底层 Bean 都被独立开关隔离。不存在 template.enabled。RocketMQ broker 参数来自 rocketmq.*，Kafka 来自 spring.kafka.*，不能把示例地址当生产默认值。

MessagingTemplateAutoConfiguration 根据 KafkaTemplate / RocketMQTemplate Bean 创建适配器并提供 DefaultMessagingTemplate；MessagingContainerConfiguration 装配监听注册器、检测器与容器注册组件。单一实现的类路径隔离和实际 broker 连接需要上下文测试；部分 Bean 无 MissingBean 条件，不重复注册整套基础设施。

## 扩展边界

Kafka、RocketMQ 原生特性通过对应原生模板及业务方案接入，统一 MessagingTemplate 不声明事务消息 API。本地 @Transactional 不自动包含消息发送。业务明确消费幂等键、顺序要求、重试和失败归档，再使用该组件。当前配置中的 TongHTP 字段不等于已有可用 starter。
