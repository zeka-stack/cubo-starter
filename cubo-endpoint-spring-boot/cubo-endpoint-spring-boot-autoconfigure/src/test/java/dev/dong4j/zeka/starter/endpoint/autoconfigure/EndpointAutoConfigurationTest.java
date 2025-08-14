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
 * 自动装配测试
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

    @Resource
    private ApplicationContext context;

    @Test
    void test() {
        log.info("hello tester");

    }

    @Test
    void testWebApplicationType() {
        // 判断当前是否为Web环境以及具体类型
        String webApplicationType = "None";
        if (context instanceof WebApplicationContext) {
            // 传统Servlet环境
            webApplicationType = "Servlet";
        } else if (context instanceof ReactiveWebApplicationContext) {
            // Reactive环境
            webApplicationType = "Reactive";
        } else {
            webApplicationType = "Non-Web";
        }
        log.info("Current web application type: {}", webApplicationType);
    }

}
