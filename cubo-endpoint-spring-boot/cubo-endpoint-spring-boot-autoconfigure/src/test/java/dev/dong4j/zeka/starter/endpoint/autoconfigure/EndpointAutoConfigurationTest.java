package dev.dong4j.zeka.starter.endpoint.autoconfigure;

import dev.dong4j.zeka.kernel.test.ZekaTest;
import dev.dong4j.zeka.starter.endpoint.autoconfigure.reactive.ReactiveStartInfoAutoConfiguration;
import dev.dong4j.zeka.starter.endpoint.autoconfigure.servlet.ServletStartInfoAutoConfiguration;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.reactive.context.ReactiveWebApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;

/**
 * Endpoint 模块自动配置测试类
 *
 * 该测试类用于验证 cubo-endpoint-spring-boot 模块的自动配置功能。
 * 主要测试内容包括：
 *
 * 1. 自动配置类的正常加载
 * 2. Servlet 和 Reactive 环境下的配置兼容性
 * 3. Web 应用类型的正确识别
 * 4. 相关 Bean 的创建和注入
 *
 * 使用 @ZekaTest 注解来加载测试所需的配置类。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.06.28 18:09
 * @since 1.0.0
 */
@Slf4j
@ZekaTest(classes = {EndpointAutoConfiguration.class, ServletStartInfoAutoConfiguration.class, ReactiveStartInfoAutoConfiguration.class})
class EndpointAutoConfigurationTest {

    /** Spring 应用上下文 */
    @Resource
    private ApplicationContext context;

    /**
     * 基本测试方法
     * <p>
     * 简单的测试方法，用于验证测试环境的正常启动。
     */
    @Test
    void test() {
        log.info("hello tester");
    }

    /**
     * 测试 Web 应用类型识别
     *
     * 检测当前应用上下文的类型，判断是否为 Web 环境以及具体类型。
     * 支持三种类型：
     * - Servlet：传统的 Spring MVC Web 环境
     * - Reactive：Spring WebFlux 环境
     * - Non-Web：非 Web 环境
     */
    @Test
    void testWebApplicationType() {
        // 初始化默认值
        String webApplicationType = "None";
        // 判断 Web 应用类型
        if (context instanceof WebApplicationContext) {
            // 传统 Servlet 环境
            webApplicationType = "Servlet";
        } else if (context instanceof ReactiveWebApplicationContext) {
            // Reactive 环境
            webApplicationType = "Reactive";
        } else {
            // 非 Web 环境
            webApplicationType = "Non-Web";
        }
        log.info("Current web application type: {}", webApplicationType);
    }

}
