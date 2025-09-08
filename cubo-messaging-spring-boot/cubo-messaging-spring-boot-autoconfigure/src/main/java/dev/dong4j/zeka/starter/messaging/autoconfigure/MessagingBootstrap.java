package dev.dong4j.zeka.starter.messaging.autoconfigure;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

/**
 * 消息启动引导注解
 *
 * 该注解用于启用消息中间件的自动配置功能，包括：
 * 1. 消息监听容器的初始化
 * 2. 消息模板的自动配置
 * 3. 消息中间件相关组件的注册
 *
 * 使用场景：
 * 1. 主应用类上添加此注解启用消息功能
 * 2. 配置类上添加此注解进行定制化配置
 *
 * 示例：
 * {@code
 * @MessagingBootstrap
 * @SpringBootApplication
 * public class Application {
 *     public static void main(String[] args) {
 *         SpringApplication.run(Application.class, args);
 *     }
 * }
 * }
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.06.27
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(MessagingContainerConfiguration.class)
public @interface MessagingBootstrap {
}
