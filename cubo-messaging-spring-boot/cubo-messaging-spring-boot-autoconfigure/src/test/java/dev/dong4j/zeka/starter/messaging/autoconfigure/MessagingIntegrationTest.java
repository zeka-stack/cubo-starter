package dev.dong4j.zeka.starter.messaging.autoconfigure;

import dev.dong4j.zeka.kernel.common.util.JsonUtils;
import dev.dong4j.zeka.kernel.test.ZekaTest;
import dev.dong4j.zeka.starter.messaging.annotation.MessagingListener;
import dev.dong4j.zeka.starter.messaging.enums.MessagingType;
import dev.dong4j.zeka.starter.messaging.model.UnifiedMessage;
import dev.dong4j.zeka.starter.messaging.template.MessagingTemplate;
import dev.dong4j.zeka.starter.messaging.template.model.MessageKey;
import javax.annotation.Resource;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ZekaTest(classes = MessagingContainerConfiguration.class)
@ActiveProfiles("test")
class MessagingIntegrationTest {

    @Resource
    private MessagingTemplate messageTemplate;

    @Resource
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @SpyBean
    private OrderService orderService;

    @SpyBean
    private PaymentService paymentService;

    @Test
    void shouldSendAndReceiveKafkaMessage() {
        // 准备测试订单
        Order order = new Order("order-123", 99.99);

        // 发送消息
        UnifiedMessage message = new UnifiedMessage("orders", order)
            .withKey(MessageKey.of(order.getId()));

        messageTemplate.sendSync(message);

        // 验证Kafka发送被调用
        ArgumentCaptor<ProducerRecord> kafkaCaptor = ArgumentCaptor.forClass(ProducerRecord.class);
        verify(kafkaTemplate).send(kafkaCaptor.capture());

        ProducerRecord sentRecord = kafkaCaptor.getValue();
        assertEquals("orders", sentRecord.topic());
        assertEquals("order-123", sentRecord.key());
        assertEquals(order, sentRecord.value());

        // 模拟消息接收
        ConsumerRecord<String, Order> record =
            new ConsumerRecord<>("orders", 0, 0, "order-123", order);

        // 调用监听器适配器
        orderService.processKafkaOrder(record);

        // 验证业务方法被调用
        verify(orderService).processOrder(any(Order.class));
    }

    @Test
    void shouldSendRocketMQMessage() {
// 准备测试支付
        Payment payment = new Payment("pay-456", 49.99);
        // 发送消息
        UnifiedMessage message = new UnifiedMessage("payments", "111111")
            .addHeader("rocketmq_tag", "PAYMENT");

        messageTemplate.forType(MessagingType.ROCKETMQ).sendSync(message);

        // 验证RocketMQ发送被调用
        ArgumentCaptor<Message> rocketCaptor = ArgumentCaptor.forClass(Message.class);
        ArgumentCaptor<String> destinationCaptor = ArgumentCaptor.forClass(String.class);

        verify(rocketMQTemplate).syncSend(destinationCaptor.capture(), rocketCaptor.capture());

        assertEquals("payments:PAYMENT", destinationCaptor.getValue());
        Message sentMessage = rocketCaptor.getValue();
        assertEquals(payment, JsonUtils.parse(sentMessage.getBody(), Payment.class));
        assertEquals("PAYMENT", sentMessage.getProperty(("rocketmq_tag")));
    }
}

// 测试业务服务
@Service
class OrderService {

    @MessagingListener(
        topic = "orders",
        groupId = "order-group",
        type = MessagingType.KAFKA
    )
    public void processKafkaOrder(ConsumerRecord<String, Order> record) {
        processOrder(record.value());
    }

    public void processOrder(Order order) {
        // 实际业务逻辑
    }
}

@Service
class PaymentService {

    @MessagingListener(
        topic = "payments",
        groupId = "payment-group",
        type = MessagingType.ROCKETMQ
    )
    public void processRocketMQPayment(MessageExt message) {
        Payment payment = JsonUtils.parse(message.getBody(), Payment.class);
        processPayment(payment);
    }

    public void processPayment(Payment payment) {
        // 实际业务逻辑
    }

}


