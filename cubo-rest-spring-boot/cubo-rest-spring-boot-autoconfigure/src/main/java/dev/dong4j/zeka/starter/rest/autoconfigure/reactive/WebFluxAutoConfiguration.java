package dev.dong4j.zeka.starter.rest.autoconfigure.reactive;

import dev.dong4j.zeka.kernel.autoconfigure.condition.ConditionalOnEnabled;
import dev.dong4j.zeka.kernel.common.start.ZekaAutoConfiguration;
import dev.dong4j.zeka.starter.rest.autoconfigure.RestProperties;
import dev.dong4j.zeka.starter.rest.handler.ZekaWebfluxExceptionErrorAttributes;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.reactive.context.ReactiveWebApplicationContext;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * WebFlux 反应式 Web 自动配置类
 *
 * 该自动配置类专门为基于 Spring WebFlux 的反应式 Web 应用提供配置支持。
 * 它负责初始化和配置 WebFlux 环境下所需的各种组件，包括路由配置、
 * 异常处理、请求处理等核心功能。
 *
 * 主要功能：
 * 1. WebFlux 基础配置：为反应式 Web 应用提供基础的配置支持
 * 2. 异常处理集成：集成框架的自定义异常处理组件
 * 3. 环境检测：自动检测并适配 WebFlux 环境
 * 4. 配置管理：集成框架的配置属性管理
 * 5. 组件注册：为后续的组件注册提供基础支持
 *
 * 条件化加载：
 * 1. @ConditionalOnWebApplication(REACTIVE)：仅在反应式 Web 环境下生效
 * 2. @ConditionalOnClass：检测 WebFlux 相关类的存在
 * 3. @ConditionalOnEnabled：支持通过配置文件开关功能
 *
 * 配置依赖：
 * - WebFluxConfigurer：Spring WebFlux 配置接口
 * - ZekaWebfluxExceptionErrorAttributes：框架自定义的异常属性处理器
 * - ReactiveWebApplicationContext：反应式 Web 应用上下文
 *
 * 设计特点：
 * - 实现 ZekaAutoConfiguration 接口，遵循框架规范
 * - 支持条件化加载，只在适当的环境下生效
 * - 集成框架的配置管理机制
 * - 提供明确的日志输出，便于问题排查
 *
 * 使用场景：
 * - Spring Boot + WebFlux 反应式 Web 应用
 * - 微服务网关应用
 * - 高并发的 REST API 服务
 * - 流式数据处理应用
 *
 * 注意事项：
 * - 该配置类与 Servlet 环境下的配置类互斥
 * - 需要确保项目中包含 WebFlux 相关依赖
 * - 建议与框架其他反应式组件配合使用
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 14:47
 * @since 1.0.0
 */
@Slf4j
@AutoConfiguration
@RestController
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@ConditionalOnClass(value = {WebFluxConfigurer.class, ZekaWebfluxExceptionErrorAttributes.class})
@ConditionalOnEnabled(value = RestProperties.PREFIX)
public class WebFluxAutoConfiguration implements ZekaAutoConfiguration {

    /**
     * 构造函数，初始化 WebFlux 自动配置
     *
     * 在配置类实例化时被调用，用于记录配置的加载信息。
     * 这有助于在应用启动时跟踪哪些自动配置被激活，
     * 便于问题排查和系统监控。
     */

    /** 反应式 Web 应用上下文，用于访问 WebFlux 环境下的 Spring 容器和组件 */
    @Resource
    private ReactiveWebApplicationContext applicationContext;

}
