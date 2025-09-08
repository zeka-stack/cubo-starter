package dev.dong4j.zeka.starter.launcher.enums;

import dev.dong4j.zeka.kernel.common.exception.StarterException;
import org.springframework.util.ClassUtils;

/**
 * Spring 应用类型枚举，用于区分 Spring Boot 和 Spring Cloud 应用
 *
 * 该枚举定义了两种 Spring 应用类型：
 * 1. BOOT - 标准 Spring Boot 应用
 * 2. CLOUD - Spring Cloud 应用（包含 Spring Boot）
 *
 * 提供了自动检测应用类型的方法，通过分析类路径中是否存在特定的标识类来确定应用类型。
 * 这种区分对于配置加载顺序和特定功能的启用非常重要。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.02.14 16:52
 * @since 1.0.0
 */
public enum SpringApplicationType {

    /** Boot spring application type */
    BOOT,
    /** Cloud spring application type */
    CLOUD;

    /** spring-cloud-context 中的 class */
    private static final String CLOUD_CLASS = "org.springframework.cloud.bootstrap.BootstrapImportSelectorConfiguration";
    /** spring-bbot 中的 class */
    private static final String BOOT_CLASS = "org.springframework.boot.SpringBootVersion";

    /**
     * 从类路径推断 Spring 应用类型
     *
     * 通过检查类路径中是否存在特定的指示器类来确定应用类型：
     * 1. 如果存在 Spring Cloud 的引导配置类，则为 CLOUD 类型
     * 2. 如果存在 Spring Boot 版本类，则为 BOOT 类型
     * 3. 如果两者都不存在，则抛出异常，表示不是 Spring Boot/Cloud 应用
     *
     * 这种检测方式确保了框架能够正确识别应用类型，并加载相应的配置。
     *
     * @return 推断出的 Spring 应用类型
     * @throws StarterException 如果既不是 Spring Boot 也不是 Spring Cloud 应用
     * @since 1.0.0
     */
    public static SpringApplicationType deduceFromClasspath() {
        if (ClassUtils.isPresent(CLOUD_CLASS, null)) {
            return SpringApplicationType.CLOUD;
        }
        if (ClassUtils.isPresent(BOOT_CLASS, null)) {
            return SpringApplicationType.BOOT;
        }

        throw new StarterException("不是 Spring Boot/Cloud 应用.");
    }

}
