package dev.dong4j.zeka.starter.openapi.autoconfigure.knife4j;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.github.xiaoymin.knife4j.spring.extension.Knife4jOpenApiCustomizer;
import com.github.xiaoymin.knife4j.spring.filter.JakartaProductionSecurityFilter;
import dev.dong4j.zeka.kernel.autoconfigure.condition.ConditionalOnEnabled;
import dev.dong4j.zeka.kernel.common.constant.App;
import dev.dong4j.zeka.kernel.common.enums.LibraryEnum;
import dev.dong4j.zeka.kernel.common.start.ZekaAutoConfiguration;
import dev.dong4j.zeka.kernel.common.util.ConfigKit;
import dev.dong4j.zeka.starter.openapi.MyKnife4jOpenApiCustomizer;
import dev.dong4j.zeka.starter.openapi.autoconfigure.OpenAPIProperties;
import jakarta.servlet.Servlet;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * Knife4j自动配置类
 *
 * 该类负责配置Knife4j相关的Bean和功能，提供API文档的增强功能。
 * 通过Spring Boot自动配置机制，在非生产环境下启用Knife4j功能。
 *
 * 主要功能包括：
 * 1. 配置Knife4jOpenApiCustomizer自定义器
 * 2. 配置生产环境安全过滤器
 * 3. 提供Knife4j UI自动配置
 * 4. 支持OpenAPI文档的增强显示
 *
 * 使用场景：
 * - API文档的自动生成和展示
 * - Swagger UI的增强功能
 * - OpenAPI规范的扩展支持
 * - 开发环境的API调试工具
 *
 * 设计意图：
 * 通过自动配置简化Knife4j的集成，提供开箱即用的API文档功能，
 * 支持开发人员快速构建和调试API接口。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.05.08 16:14
 * @since 1.0.0
 */
@AutoConfiguration(before = com.github.xiaoymin.knife4j.spring.configuration.Knife4jAutoConfiguration.class)
@Profile(value = {App.ENV_NOT_PROD})
@ConditionalOnClass(value = {
    EnableKnife4j.class,
    Servlet.class,
    DispatcherServlet.class,
})
@ConditionalOnEnabled(value = OpenAPIProperties.PREFIX)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(value = {Knife4jProperties.class, com.github.xiaoymin.knife4j.spring.configuration.Knife4jProperties.class})
@Slf4j
public class Knife4jAutoConfiguration implements ZekaAutoConfiguration {

    /**
     * 构造Knife4j自动配置对象
     * <p>
     * 初始化Knife4j自动配置，记录启动日志。
     *
     * @since 1.0.0
     */
    public Knife4jAutoConfiguration() {
        log.info("启动自动配置: [{}]", this.getClass());
    }

    /**
     * 创建Knife4j OpenAPI自定义器
     *
     * 创建自定义的Knife4j OpenAPI自定义器，用于增强API文档的显示和功能。
     * 该自定义器支持标签排序、文档扩展等高级功能。
     *
     * @param properties Knife4j配置属性
     * @param docProperties SpringDoc配置属性
     * @return Knife4jOpenApiCustomizer实例
     * @since 1.0.0
     */
    @Bean
    public Knife4jOpenApiCustomizer knife4jOpenApiCustomizer(com.github.xiaoymin.knife4j.spring.configuration.Knife4jProperties properties, SpringDocConfigProperties docProperties) {
        log.debug("Register Knife4jOpenApiCustomizer");
        return new MyKnife4jOpenApiCustomizer(properties, docProperties);
    }

    /**
     * 创建生产环境安全过滤器
     *
     * 创建生产环境安全过滤器，用于在生产环境中保护API文档的访问。
     * 只有在非本地启动且为生产环境时才启用安全过滤。
     *
     * @return JakartaProductionSecurityFilter实例
     * @since 1.0.0
     */
    @Bean
    public JakartaProductionSecurityFilter productionSecurityFilter() {
        return new JakartaProductionSecurityFilter(!ConfigKit.isLocalLaunch() && ConfigKit.isProd());
    }

    /**
     * Knife4j UI自动配置类
     *
     * 该类负责配置Knife4j UI相关的功能，仅在存在doc.html资源时启用。
     * 提供Swagger REST Bootstrap类型的库支持。
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.05.08 16:52
     * @since 1.0.0
     */
    @AutoConfiguration
    @ConditionalOnResource(resources = "classpath:META-INF/resources/doc.html")
    static class Knife4jUiAutoConfiguration implements ZekaAutoConfiguration {

        /**
         * 获取库类型
         *
         * 返回Swagger REST Bootstrap库类型，用于标识当前使用的API文档库。
         *
         * @return SWAGGER_REST_BOOTSTRAP库类型
         * @since 1.0.0
         */
        @Override
        public LibraryEnum getLibraryType() {
            return LibraryEnum.SWAGGER_REST_BOOTSTRAP;
        }
    }

}
