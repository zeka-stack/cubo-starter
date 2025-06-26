package dev.dong4j.zeka.starter.messaging.factory;


import dev.dong4j.zeka.starter.messaging.adapter.AbstractMessagingListenerAdapter;
import dev.dong4j.zeka.starter.messaging.annotation.MessagingListener;

public interface MessagingListenerContainerFactory {
    void registerContainer(AbstractMessagingListenerAdapter adapter, MessagingListener annotation);
}
