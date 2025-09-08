package dev.dong4j.zeka.starter.openapi.autoconfigure.knife4j;

import dev.dong4j.zeka.kernel.common.constant.ConfigKey;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Knife4j配置属性类
 *
 * 该类用于配置Knife4j相关的属性，通过Spring Boot的配置属性机制进行绑定。
 * 支持通过配置文件或环境变量来配置Knife4j的各种功能。
 *
 * 主要功能包括：
 * 1. 提供Knife4j配置属性的绑定
 * 2. 支持配置属性的自动注入
 * 3. 提供配置属性的默认值
 * 4. 支持配置属性的验证和转换
 *
 * 使用场景：
 * - Knife4j功能的配置管理
 * - API文档的个性化配置
 * - 开发环境的调试配置
 * - 生产环境的安全配置
 *
 * 设计意图：
 * 通过配置属性类提供Knife4j的灵活配置能力，支持不同环境下的个性化配置，
 * 简化Knife4j的集成和使用。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.05.08 04:14
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = Knife4jProperties.PREFIX)
public class Knife4jProperties {
    /** 配置前缀常量 */
    public static final String PREFIX = ConfigKey.PREFIX + "openapi.knife4j";

}
