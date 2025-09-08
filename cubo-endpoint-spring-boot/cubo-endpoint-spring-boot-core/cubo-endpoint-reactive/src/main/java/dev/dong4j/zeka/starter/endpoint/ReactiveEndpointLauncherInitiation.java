package dev.dong4j.zeka.starter.endpoint;

import dev.dong4j.zeka.kernel.common.start.LauncherInitiation;
import dev.dong4j.zeka.processor.annotation.AutoService;
import dev.dong4j.zeka.starter.endpoint.constant.Endpoint;
import dev.dong4j.zeka.starter.endpoint.spi.EndpointLauncherInitiation;

/**
 * Reactive 环境下的 Endpoint 模块启动初始化器
 *
 * 通过 SPI 机制自动加载 Reactive 环境下的 Endpoint 模块默认配置。
 * 继承自 EndpointLauncherInitiation，为 Spring WebFlux Reactive Web 应用
 * 提供 Actuator 和管理端点的配置支持。
 *
 * 使用 @AutoService 注解自动注册到 SPI 机制中。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 11:12
 * @since 1.0.0
 */
@AutoService(LauncherInitiation.class)
public class ReactiveEndpointLauncherInitiation extends EndpointLauncherInitiation {

    /**
     * 获取模块名称
     *
     * 返回 Reactive 环境下的 Endpoint 模块名称，
     * 用于在日志和监控中标识该初始化器。
     *
     * @return Reactive Endpoint 模块的名称
     * @since 1.0.0
     */
    @Override
    public String getName() {
        return Endpoint.REACTIVE_MODULE_NAME;
    }
}
