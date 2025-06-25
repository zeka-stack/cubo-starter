// TemplateAdapter.java
package dev.dong4j.zeka.starter.messaging.template.adapter;

import dev.dong4j.zeka.starter.messaging.model.UnifiedMessage;
import dev.dong4j.zeka.starter.messaging.template.model.SendResult;
import java.util.concurrent.CompletableFuture;

public interface TemplateAdapter {
    SendResult sendSync(UnifiedMessage message);

    CompletableFuture<SendResult> sendAsync(UnifiedMessage message);

    void sendOneWay(UnifiedMessage message);

    <T> T getNativeTemplate();
}
