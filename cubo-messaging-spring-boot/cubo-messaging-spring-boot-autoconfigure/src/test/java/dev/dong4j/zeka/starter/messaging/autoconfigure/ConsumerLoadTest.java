package dev.dong4j.zeka.starter.messaging.autoconfigure;

import dev.dong4j.zeka.starter.messaging.model.UnifiedMessage;
import dev.dong4j.zeka.starter.messaging.template.MessagingTemplate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@SpringBootTest
@ActiveProfiles("perf")
class ConsumerLoadTest {

    @Resource
    private MessagingTemplate messageTemplate;

    @Resource
    private OrderService orderService;

    @Test
    void testConsumerPerformance() throws InterruptedException {
        int messageCount = 10_000;
        CountDownLatch latch = new CountDownLatch(messageCount);

        // 设置回调计数
        doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(orderService).processOrder(any(Order.class));

        // 发送测试消息
        for (int i = 0; i < messageCount; i++) {
            Order order = new Order("order-" + i, i * 10.0);
            UnifiedMessage message = new UnifiedMessage("orders", order);
            messageTemplate.sendSync(message);
        }

        // 等待所有消息处理完成
        boolean completed = latch.await(30, TimeUnit.SECONDS);
        assertTrue("Not all messages processed in time", completed);

        // 验证处理调用次数
        verify(orderService, times(messageCount)).processOrder(any(Order.class));

        // 计算处理速率
        long startTime = System.currentTimeMillis();
        latch.await(); // 确保所有处理完成
        long endTime = System.currentTimeMillis();

        double rate = (double) messageCount / ((endTime - startTime) / 1000.0);
        System.out.printf("Processed %d messages at %.2f msg/s%n", messageCount, rate);
    }
}
