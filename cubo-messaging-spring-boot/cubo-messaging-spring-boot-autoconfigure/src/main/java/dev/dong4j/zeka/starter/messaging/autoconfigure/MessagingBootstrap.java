// UnifiedMQBootstrap.java
package dev.dong4j.zeka.starter.messaging.autoconfigure;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(MessagingConfiguration.class)
public @interface MessagingBootstrap {
}
