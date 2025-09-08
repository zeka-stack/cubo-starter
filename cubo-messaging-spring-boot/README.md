# Cubo Messaging Spring Boot

## 概述

`cubo-messaging-spring-boot` 是 Cubo Starter 项目的消息处理模块，提供了统一的消息队列抽象和多种消息中间件的支持。该模块支持 Kafka、RocketMQ
等主流消息队列，并提供了消息发送、接收、事务处理等完整的消息处理能力。

## 主要功能

### 1. 多消息中间件支持

- **Kafka**: Apache 分布式流处理平台
- **RocketMQ**: 阿里巴巴分布式消息中间件
- 统一的 API 接口，便于切换和扩展

### 2. 消息发送和接收

- 支持同步和异步消息发送
- 提供消息监听器注解
- 支持消息的批量处理

### 3. 消息事务处理

- 支持分布式事务消息
- 提供消息的可靠投递保证
- 支持消息的幂等性处理

### 4. 消息模板抽象

- 提供统一的消息发送模板
- 支持消息的序列化和反序列化
- 提供消息的元数据管理

## 模块结构

```
cubo-messaging-spring-boot/
├── cubo-messaging-spring-boot-autoconfigure/    # 自动配置模块
├── cubo-messaging-spring-boot-core/             # 核心功能模块
│   ├── cubo-messaging-common/                   # 通用消息组件
│   ├── cubo-messaging-kafka/                    # Kafka 实现
│   └── cubo-messaging-rocketmq/                 # RocketMQ 实现
└── cubo-messaging-spring-boot-starter/          # Starter 模块
    ├── cubo-messaging-kafka-spring-boot-starter/
    └── cubo-messaging-rocketmq-spring-boot-starter/
```

### 子模块说明

#### cubo-messaging-spring-boot-autoconfigure

- **MessagingTemplateAutoConfiguration**: 消息模板自动配置
- **KafkaAutoConfiguration**: Kafka 自动配置
- **RocketMQAutoConfiguration**: RocketMQ 自动配置

#### cubo-messaging-spring-boot-core

##### cubo-messaging-common

- 提供通用的消息接口和抽象类
- 定义消息处理的核心规范
- 提供消息的序列化工具

##### cubo-messaging-kafka

- 基于 Kafka 的消息实现
- 支持 Kafka 的高级特性
- 提供 Kafka 的配置管理

##### cubo-messaging-rocketmq

- 基于 RocketMQ 的消息实现
- 支持 RocketMQ 的事务消息
- 提供 RocketMQ 的配置管理

## 核心特性

### 1. 统一消息接口

#### MessagingTemplate

```java
@Service
public class MessageService {

    @Autowired
    private MessagingTemplate messagingTemplate;

    public void sendMessage(String topic, Object message) {
        messagingTemplate.send(topic, message);
    }

    public void sendMessage(String topic, String key, Object message) {
        messagingTemplate.send(topic, key, message);
    }
}
```

### 2. 消息监听器

#### 注解方式

```java
@Component
public class MessageListener {

    @MessagingListener(topics = "user-topic")
    public void handleUserMessage(UserMessage message) {
        // 处理用户消息
        System.out.println("收到用户消息: " + message);
    }

    @MessagingListener(topics = "order-topic", groupId = "order-group")
    public void handleOrderMessage(OrderMessage message) {
        // 处理订单消息
        System.out.println("收到订单消息: " + message);
    }
}
```

### 3. 消息序列化

#### 自定义序列化器

```java
@Component
public class CustomMessageSerializer implements MessageSerializer {

    @Override
    public byte[] serialize(Object message) {
        // 自定义序列化逻辑
        return JSON.toJSONBytes(message);
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        // 自定义反序列化逻辑
        return JSON.parseObject(data, clazz);
    }
}
```

## 配置属性

### MessagingProperties

| 属性名                                     | 类型      | 默认值  | 说明            |
|-----------------------------------------|---------|------|---------------|
| `zeka-stack.messaging.enabled`          | boolean | true | 是否启用消息处理功能    |
| `zeka-stack.messaging.template.enabled` | boolean | true | 是否启用消息模板      |
| `zeka-stack.messaging.kafka.enabled`    | boolean | true | 是否启用 Kafka    |
| `zeka-stack.messaging.rocketmq.enabled` | boolean | true | 是否启用 RocketMQ |

### Kafka 配置

| 属性名                              | 类型     | 默认值            | 说明          |
|----------------------------------|--------|----------------|-------------|
| `spring.kafka.bootstrap-servers` | String | localhost:9092 | Kafka 服务器地址 |
| `spring.kafka.consumer.group-id` | String | default-group  | 消费者组 ID     |
| `spring.kafka.producer.acks`     | String | all            | 生产者确认机制     |

### RocketMQ 配置

| 属性名                       | 类型     | 默认值                    | 说明             |
|---------------------------|--------|------------------------|----------------|
| `rocketmq.name-server`    | String | localhost:9876         | RocketMQ 名称服务器 |
| `rocketmq.producer.group` | String | default-producer-group | 生产者组           |
| `rocketmq.consumer.group` | String | default-consumer-group | 消费者组           |

## 使用方式

### 1. 引入依赖

**使用 Kafka**

```xml
<dependency>
    <groupId>dev.dong4j</groupId>
    <artifactId>cubo-messaging-kafka-spring-boot-starter</artifactId>
</dependency>
```

**使用 RocketMQ**

```xml
<dependency>
    <groupId>dev.dong4j</groupId>
    <artifactId>cubo-messaging-rocketmq-spring-boot-starter</artifactId>
</dependency>
```

### 2. 配置启用

```yaml
zeka-stack:
  messaging:
    enabled: true
    template:
      enabled: true
    kafka:
      enabled: true
    rocketmq:
      enabled: true

# Kafka 配置
spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: my-group
      auto-offset-reset: earliest
    producer:
      acks: all
      retries: 3

# RocketMQ 配置
rocketmq:
  name-server: localhost:9876
  producer:
    group: my-producer-group
  consumer:
    group: my-consumer-group
```

### 3. 发送消息

```java
@RestController
public class MessageController {

    @Autowired
    private MessagingTemplate messagingTemplate;

    @PostMapping("/send")
    public String sendMessage(@RequestBody String message) {
        messagingTemplate.send("test-topic", message);
        return "消息发送成功";
    }
}
```

### 4. 接收消息

```java
@Component
public class MessageConsumer {

    @MessagingListener(topics = "test-topic")
    public void handleMessage(String message) {
        System.out.println("收到消息: " + message);
    }
}
```

## 高级功能

### 1. 事务消息

#### RocketMQ 事务消息

```java
@Service
public class TransactionalMessageService {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Transactional
    public void sendTransactionalMessage(String topic, Object message) {
        // 发送事务消息
        rocketMQTemplate.sendMessageInTransaction(topic, message, null);
    }
}
```

### 2. 消息过滤

#### Kafka 消息过滤

```java
@Component
public class FilteredMessageConsumer {

    @MessagingListener(topics = "user-topic",
                      filter = "message.userId > 1000")
    public void handleFilteredMessage(UserMessage message) {
        // 只处理 userId > 1000 的消息
        System.out.println("处理过滤后的消息: " + message);
    }
}
```

### 3. 消息重试

```java
@Component
public class RetryableMessageConsumer {

    @MessagingListener(topics = "retry-topic",
                      retryAttempts = 3,
                      retryDelay = 1000)
    public void handleRetryableMessage(String message) {
        try {
            // 处理消息
            processMessage(message);
        } catch (Exception e) {
            // 抛出异常会触发重试
            throw new RuntimeException("处理失败，将重试", e);
        }
    }
}
```

### 4. 消息批量处理

```java
@Component
public class BatchMessageConsumer {

    @MessagingListener(topics = "batch-topic",
                      batchSize = 100,
                      batchTimeout = 5000)
    public void handleBatchMessages(List<String> messages) {
        // 批量处理消息
        messages.forEach(this::processMessage);
    }
}
```

## 最佳实践

### 1. 消息设计

- 使用有意义的主题名称
- 设计合理的消息结构
- 考虑消息的版本兼容性

### 2. 错误处理

- 实现消息的幂等性
- 合理设置重试策略
- 记录失败消息用于后续处理

### 3. 性能优化

- 使用批量处理提高吞吐量
- 合理设置消费者并发数
- 优化消息序列化性能

### 4. 监控和运维

- 监控消息队列的积压情况
- 设置合理的告警机制
- 定期检查消息处理性能

## 注意事项

1. **消息顺序**: 某些场景下需要保证消息的顺序性
2. **消息重复**: 网络问题可能导致消息重复，需要实现幂等性
3. **消息丢失**: 合理配置确认机制，避免消息丢失
4. **性能影响**: 消息处理会影响应用性能，需要合理配置

## 相关链接

- [Apache Kafka 官方文档](https://kafka.apache.org/documentation/)
- [RocketMQ 官方文档](https://rocketmq.apache.org/docs/quick-start/)
- [Spring Boot 消息处理](https://docs.spring.io/spring-boot/docs/current/reference/html/messaging.html)
