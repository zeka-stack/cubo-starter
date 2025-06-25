package dev.dong4j.zeka.starter.messaging.annotation;// UnifiedMessageListener.java

import dev.dong4j.zeka.starter.messaging.enums.MessagingType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MessagingListener {
    String topic();

    String groupId();

    MessagingType type() default MessagingType.DEFAULT;
}
