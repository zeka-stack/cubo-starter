package dev.dong4j.zeka.starter.openapi.autoconfigure;

import dev.dong4j.zeka.kernel.autoconfigure.condition.ConditionalOnEnabled;
import dev.dong4j.zeka.kernel.common.start.ZekaAutoConfiguration;
import dev.dong4j.zeka.starter.openapi.autoconfigure.knife4j.Knife4jAutoConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * OpenAPI自动配置类
 *
 * 该类负责OpenAPI相关功能的自动配置，作为OpenAPI模块的主入口配置类。
 * 通过Spring Boot自动配置机制，在启用OpenAPI功能时自动配置相关组件。
 *
 * 主要功能包括：
 * 1. 作为OpenAPI模块的自动配置入口
 * 2. 启用OpenAPI配置属性绑定
 * 3. 配置Knife4j自动配置的执行顺序
 * 4. 提供OpenAPI功能的统一管理
 *
 * 使用场景：
 * - OpenAPI功能的自动启用
 * - API文档的自动配置
 * - 开发环境的文档工具配置
 * - 生产环境的API文档管理
 *
 * 设计意图：
 * 通过自动配置类提供OpenAPI功能的开箱即用体验，
 * 简化开发人员的配置工作，提供标准化的API文档解决方案。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.06.29 16:54
 * @since 1.0.0
 */
@Slf4j
@AutoConfiguration
@ConditionalOnEnabled(value = OpenAPIProperties.PREFIX)
@AutoConfigureAfter({
    Knife4jAutoConfiguration.class,
})
@EnableConfigurationProperties(OpenAPIProperties.class)
public class OpenAPIAutoConfiguration implements ZekaAutoConfiguration {

    /**
     * 构造OpenAPI自动配置对象
     * <p>
     * 初始化OpenAPI自动配置，记录启动日志。
     * 当OpenAPI功能被启用时，该配置类会被Spring Boot自动加载。
     *
     * @since 1.0.0
     */
    public OpenAPIAutoConfiguration() {
        log.info("启动自动配置: [{}]", this.getClass());
    }
}
