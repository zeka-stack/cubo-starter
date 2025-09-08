package dev.dong4j.zeka.starter.launcher.autoconfigure.extend;

import dev.dong4j.zeka.kernel.common.constant.ConfigDefaultValue;
import dev.dong4j.zeka.kernel.common.constant.ConfigKey;
import dev.dong4j.zeka.kernel.common.start.ZekaAutoConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;

/**
 * 扩展自动配置类，提供对 Spring 注解的扩展支持
 *
 * 该类实现了以下功能：
 * 1. 允许 @Autowired 和 @Resource 注解的依赖注入为 null
 * 2. 通过配置属性控制扩展功能的启用/禁用
 * 3. 提供统一的扩展配置管理
 *
 * @todo 需要修复可能存在的问题 (2020年02月28日 18:19)
 *
 * 使用场景：
 * 1. 在开发环境中简化依赖管理
 * 2. 在可选依赖场景下避免强制注入
 *
 * 注意：生产环境应谨慎使用，可能导致 NPE 风险。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.01.28 01:26
 * @since 1.0.0
 */
@Slf4j
@EnableConfigurationProperties(ExtendProperties.class)
public class ExtendAutoConfiguration implements ZekaAutoConfiguration {

    /**
     * 创建 @Autowired 注解扩展处理器
     *
     * 该处理器允许 @Autowired 注解的依赖注入为 null，
     * 通过配置 "zeka-stack.extend.enable-autowired-is-null" 控制是否启用。
     * 默认值为 true，表示默认启用。
     *
     * @return @Autowired 注解扩展处理器实例
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnProperty(
        value = ConfigKey.EXTEND_ENABLE_AUTOWIRED_IS_NULL,
        havingValue = ConfigDefaultValue.TRUE_STRING,
        matchIfMissing = true
    )
    public AutowiredAnnotationBeanPostProcessor autowiredAnnotationExtend() {
        log.info("全局设置允许 @Autowired 允许注入的 bean 为 null");
        return new AutowiredAnnotationExtend();
    }

    /**
     * 创建 @Resource 注解扩展处理器
     *
     * 该处理器允许 @Resource 注解的依赖注入为 null，
     * 通过配置 "zeka-stack.extend.enable-resource-is-null" 控制是否启用。
     * 默认值为 true，表示默认启用。
     *
     * @return @Resource 注解扩展处理器实例
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnProperty(
        value = ConfigKey.EXTEND_ENABLE_RESOURCE_IS_NULL,
        havingValue = ConfigDefaultValue.TRUE_STRING,
        matchIfMissing = true
    )
    public CommonAnnotationBeanPostProcessor resourceAnnotationExtend() {
        log.info("设置允许 @Resource 允许注入的 bean 为 null");
        return new ResourceAnnotationExtend();
    }
}
