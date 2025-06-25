package dev.dong4j.zeka.starter.messaging.factory;


import dev.dong4j.zeka.starter.messaging.adapter.AbstractListenerAdapter;
import dev.dong4j.zeka.starter.messaging.annotation.MessagingListener;

public interface MessagingListenerContainerFactory {
    void registerContainer(AbstractListenerAdapter adapter, MessagingListener annotation);
}
