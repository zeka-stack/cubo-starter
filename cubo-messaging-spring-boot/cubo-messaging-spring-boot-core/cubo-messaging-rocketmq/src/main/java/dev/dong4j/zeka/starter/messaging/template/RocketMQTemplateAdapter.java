package dev.dong4j.zeka.starter.messaging.template;

import dev.dong4j.zeka.starter.messaging.model.UnifiedMessage;
import dev.dong4j.zeka.starter.messaging.template.adapter.TemplateAdapter;
import dev.dong4j.zeka.starter.messaging.template.model.SendResult;
import dev.dong4j.zeka.starter.messaging.util.TopicAndTagUtils;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

public class RocketMQTemplateAdapter implements TemplateAdapter {

    private final RocketMQTemplate rocketMQTemplate;

    public RocketMQTemplateAdapter(RocketMQTemplate rocketMQTemplate) {
        this.rocketMQTemplate = rocketMQTemplate;
    }

    @Override
    public SendResult sendSync(UnifiedMessage unifiedMessage) {
        String destination = TopicAndTagUtils.withTopicAndTag(
            unifiedMessage.getDestination(),
            TopicAndTagUtils.extractTag(unifiedMessage)
        );

        Message<?> springMessage = createSpringMessage(unifiedMessage);
        org.apache.rocketmq.client.producer.SendResult rocketResult = rocketMQTemplate.syncSend(destination, springMessage);
        return convertToSendResult(rocketResult);
    }

    @Override
    public CompletableFuture<SendResult> sendAsync(UnifiedMessage unifiedMessage) {
        CompletableFuture<SendResult> future = new CompletableFuture<>();

        try {
            String destination = TopicAndTagUtils.withTopicAndTag(
                unifiedMessage.getDestination(),
                TopicAndTagUtils.extractTag(unifiedMessage)
            );

            Message<?> springMessage = createSpringMessage(unifiedMessage);
            rocketMQTemplate.asyncSend(destination, springMessage, new SendCallback() {

                @Override
                public void onSuccess(org.apache.rocketmq.client.producer.SendResult sendResult) {
                    SendResult result = convertToSendResult(sendResult);
                    future.complete(result);
                }

                @Override
                public void onException(Throwable throwable) {
                    String payloadStr = springMessage.getPayload().toString();
                    future.completeExceptionally(new RuntimeException("Async send failed: " + payloadStr, throwable));
                }
            });
        } catch (Exception e) {
            future.completeExceptionally(new RuntimeException("Unexpected error during async send", e));
        }

        return future;
    }

    @Override
    public void sendOneWay(UnifiedMessage unifiedMessage) {
        String destination = TopicAndTagUtils.withTopicAndTag(
            unifiedMessage.getDestination(),
            TopicAndTagUtils.extractTag(unifiedMessage)
        );

        Message<?> springMessage = createSpringMessage(unifiedMessage);
        rocketMQTemplate.sendOneWay(destination, springMessage);
    }

    @Override
    public <T> T getNativeTemplate() {
        // noinspection unchecked
        return (T) rocketMQTemplate;
    }

    private Message<?> createSpringMessage(UnifiedMessage unifiedMessage) {
        MessageBuilder<?> builder = MessageBuilder.withPayload(unifiedMessage.getPayload());

        // 添加消息头
        for (Map.Entry<String, Object> entry : unifiedMessage.getHeaders().entrySet()) {
            builder.setHeader(entry.getKey(), entry.getValue());
        }

        // 添加消息键
        if (unifiedMessage.getMessageKey() != null) {
            builder.setHeader("KEYS", unifiedMessage.getMessageKey().getKey());
        }

        return builder.build();
    }

    private SendResult convertToSendResult(org.apache.rocketmq.client.producer.SendResult rocketResult) {
        return new SendResult(
            rocketResult.getMessageQueue().getTopic(),
            rocketResult.getMessageQueue().getQueueId(),
            -1, // RocketMQ没有offset概念
            rocketResult.getMsgId()
        );
    }
}

