package dev.dong4j.zeka.starter.launcher;

import dev.dong4j.zeka.starter.launcher.app.ApplicationTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * ZekaStarter 测试类
 *
 * 该类用于测试 ZekaStarter 的核心功能，包括：
 * 1. 应用启动流程
 * 2. 配置加载
 * 3. 自动装配
 *
 * 测试场景：
 * 1. 直接运行带有 @SpringBootApplication 注解的应用类
 *
 * 注意：测试类应保持简单，专注于验证核心功能
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.07 21:24
 * @since 1.0.0
 */
@Slf4j
class ZekaStarterTest {

    /**
     * 测试直接运行带有 @SpringBootApplication 注解的应用
     *
     * 该方法验证 ZekaStarter 能否正确启动一个 Spring Boot 应用，
     * 并完成基本的自动配置和上下文初始化。
     *
     * @throws Exception 如果启动过程中发生错误
     * @since 1.0.0
     */
    @Test
    void test_run() throws Exception {
        ZekaStarter.run(ApplicationTest.class);
    }

}
