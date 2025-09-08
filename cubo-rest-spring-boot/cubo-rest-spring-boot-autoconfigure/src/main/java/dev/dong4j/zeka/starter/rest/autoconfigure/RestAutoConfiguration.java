package dev.dong4j.zeka.starter.rest.autoconfigure;

import dev.dong4j.zeka.kernel.autoconfigure.condition.ConditionalOnEnabled;
import dev.dong4j.zeka.kernel.common.constant.App;
import dev.dong4j.zeka.kernel.common.enums.LibraryEnum;
import dev.dong4j.zeka.kernel.common.start.ZekaAutoConfiguration;
import dev.dong4j.zeka.starter.rest.autoconfigure.reactive.WebFluxAutoConfiguration;
import dev.dong4j.zeka.starter.rest.autoconfigure.servlet.ServletAutoConfiguration;
import dev.dong4j.zeka.starter.rest.runner.OpenBrowserRunner;
import dev.dong4j.zeka.starter.rest.spi.RestLauncherInitiation;
import dev.dong4j.zeka.starter.rest.support.ZekaRestComponent;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * REST 模块自动配置类
 *
 * 该类作为 REST 模块的主要自动配置类，负责初始化和配置
 * 所有 REST 相关的核心组件和功能。
 *
 * 主要功能：
 * 1. 注册 REST 模块组件标识
 * 2. 配置参数验证器（生产环境下开启快速失败模式）
 * 3. 初始化浏览器自动打开功能
 * 4. 与 Servlet 和 WebFlux 模块进行集成
 *
 * 触发条件：
 * - 配置属性 zeka-stack.rest.enabled=true
 * - 应用为 Web 应用（Servlet 或 Reactive）
 * - 类路径中存在 RestLauncherInitiation 类
 *
 * 加载顺序：
 * 在 ServletAutoConfiguration 和 WebFluxAutoConfiguration 之后加载，
 * 确保 Web 相关的配置已经完成。
 *
 * 环境适配：
 * 自动适配不同的 Web 技术栈（Servlet 或 Reactive），
 * 提供统一的 REST API 开发体验。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.06.29 10:44
 * @since 1.0.0
 */
@Slf4j
@AutoConfiguration(after = {
    ServletAutoConfiguration.class,
    WebFluxAutoConfiguration.class
})
@ConditionalOnEnabled(value = RestProperties.PREFIX)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.ANY)
@EnableConfigurationProperties(RestProperties.class)
@ConditionalOnClass(RestLauncherInitiation.class)
public class RestAutoConfiguration implements ZekaAutoConfiguration {

    /**
     * 构造方法，记录自动配置类的加载信息
     *
     * 在类实例化时记录日志，便于跟踪和调试模块的加载过程。
     * 可以帮助开发者了解哪些自动配置已经生效。
     *
     * @since 1.0.0
     */

    /**
     * 获取当前模块的库类型标识
     *
     * 返回 REST 模块的类型标识，用于模块分类和管理。
     * 该标识由框架用于区分不同类型的组件和功能模块。
     *
     * 作用：
     * - 模块分类和标识
     * - 依赖关系管理
     * - 组件扫描和加载
     * - 日志和监控信息分类
     *
     * @return REST 模块的库类型枚举值
     * @since 1.0.0
     */
    @Override
    public LibraryEnum getLibraryType() {
        return LibraryEnum.REST;
    }

    /**
     * 定义 REST 模块组件标识 Bean
     *
     * 该方法创建并注册 REST 模块的组件标识 Bean，
     * 用于标识模块已经被正确加载和配置。
     *
     * Bean 特性：
     * - 使用 @Primary 注解设为主要 Bean
     * - Bean 名称由 App.Components.REST_SPRING_BOOT 常量定义
     * - 在整个应用中保持唯一性
     *
     * 作用：
     * - 模块状态监控和管理
     * - 其他模块的依赖检查
     * - 系统诊断和健康检查
     *
     * @return REST 模块组件标识实例
     * @since 1.0.0
     */
    @Primary
    @Bean(App.Components.REST_SPRING_BOOT)
    public ZekaRestComponent restComponent() {
        return new ZekaRestComponent();
    }

    /**
     * 配置参数验证器（生产环境下开启快速失败模式）
     *
     * 在生产环境下，为了提高验证效率，将参数验证模式设置为快速失败。
     * 即遇到第一个验证错误时立即返回，而不是验证完所有参数后再返回所有错误。
     *
     * 配置特点：
     * - 仅在生产环境（prod profile）下生效
     * - 使用 Hibernate Validator 作为验证提供者
     * - 开启快速失败模式（failFast=true）
     * - 使用参数消息插值器替代默认的 EL 表达式
     *
     * 性能优势：
     * - 减少验证耗时，提高响应速度
     * - 降低 CPU 和内存开销
     * - 提高系统并发处理能力
     *
     * @return 配置为快速失败模式的验证器实例
     * @since 1.0.0
     */
    @Bean
    @Profile(value = {App.ENV_PROD})
    public Validator validator() {
        log.info("参数验证开启快速失败模式");
        try (ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
            .configure()
            // 快速失败模式：遇到第一个验证错误时立即返回
            .failFast(true)
            // 代替默认的 EL 表达式，提高性能和安全性
            .messageInterpolator(new ParameterMessageInterpolator())
            .buildValidatorFactory()) {
            return validatorFactory.getValidator();
        }
    }

    /**
     * 创建浏览器自动打开运行器
     *
     * 该 Bean 用于在应用启动完成后自动打开浏览器访问应用首页。
     * 主要用于开发环境下提高开发者的体验。
     *
     * 创建条件：
     * - 使用 @ConditionalOnMissingBean 确保只创建一个实例
     * - 允许用户自定义实现覆盖默认行为
     *
     * 配置源：
     * 从 RestProperties 中获取 enableBrowser 配置，
     * 决定是否在应用启动后自动打开浏览器。
     *
     * 功能特点：
     * - 仅在本地开发环境下生效
     * - 支持多平台（Windows、macOS、Linux）
     * - 延迟打开，确保服务完全就绪
     *
     * @param restProperties REST 模块的配置属性，不能为 null
     * @return 浏览器自动打开运行器实例
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean
    public OpenBrowserRunner openBrowserRunner(@NotNull RestProperties restProperties) {
        return new OpenBrowserRunner(restProperties.isEnableBrowser());
    }
}
