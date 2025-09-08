package dev.dong4j.zeka.starter.openapi.autoconfigure;

import dev.dong4j.zeka.kernel.autoconfigure.ZekaProperties;
import dev.dong4j.zeka.kernel.common.constant.ConfigKey;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * OpenAPI配置属性类
 *
 * 该类用于配置OpenAPI相关的属性，通过Spring Boot的配置属性机制进行绑定。
 * 继承自ZekaProperties，提供OpenAPI功能的统一配置管理。
 *
 * 主要功能包括：
 * 1. 提供OpenAPI配置属性的绑定
 * 2. 支持配置属性的自动注入
 * 3. 提供配置属性的默认值
 * 4. 支持配置属性的验证和转换
 * 5. 继承Zeka框架的基础配置能力
 *
 * 使用场景：
 * - OpenAPI功能的配置管理
 * - API文档的个性化配置
 * - 开发环境的调试配置
 * - 生产环境的安全配置
 *
 * 设计意图：
 * 通过配置属性类提供OpenAPI的灵活配置能力，支持不同环境下的个性化配置，
 * 简化OpenAPI的集成和使用，提供标准化的配置管理。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.06.29 16:54
 * @since 1.0.0
 */
@Getter
@Setter
@ConfigurationProperties(prefix = OpenAPIProperties.PREFIX)
public class OpenAPIProperties extends ZekaProperties {
    /** OpenAPI配置前缀常量 */
    public static final String PREFIX = ConfigKey.PREFIX + "openapi";
}
