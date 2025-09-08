package dev.dong4j.zeka.starter.rest.spi;

import dev.dong4j.zeka.kernel.common.start.LauncherInitiation;
import dev.dong4j.zeka.processor.annotation.AutoService;

/**
 * 反应式 REST 模块启动器初始化类
 *
 * 该类是反应式 REST 模块的启动器初始化实现，负责在应用启动过程中
 * 加载和配置反应式 Web 环境所需的默认配置和参数。它继承自
 * RestLauncherInitiation，在通用 REST 配置的基础上添加了反应式特有的配置。
 *
 * 主要功能：
 * 1. 为反应式 Web 应用提供专用的启动初始化逻辑
 * 2. 继承通用 REST 模块的所有配置和初始化行为
 * 3. 提供可识别的模块名称，用于日志和监控
 * 4. 支持 WebFlux 环境下的特殊配置需求
 * 5. 与 SPI 机制集成，实现自动注册和加载
 *
 * SPI 机制：
 * 通过 @AutoService 注解自动注册为 LauncherInitiation 的实现，
 * 在应用启动时会被 SPI 机制自动发现和加载。
 *
 * 继承关系：
 * ReactiveLauncherInitiation → RestLauncherInitiation → LauncherInitiation
 *
 * 这种设计保证了：
 * - 代码的可复用性：继承通用 REST 配置
 * - 特化的灵活性：可以针对反应式环境进行定制
 * - 配置的一致性：与框架其他模块保持一致
 *
 * 使用场景：
 * - WebFlux 反应式 Web 应用的启动初始化
 * - 网关服务的配置加载
 * - 反应式微服务的默认参数设置
 * - Spring Boot 自动配置的補充和增强
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 11:17
 * @since 1.0.0
 */
@AutoService(LauncherInitiation.class)
public class ReactiveLauncherInitiation extends RestLauncherInitiation {
    /**
     * 获取启动器的模块名称
     *
     * 重写父类方法，返回反应式 REST 模块的具体名称。该名称用于：
     *
     * 1. 模块识别：在日志和监控中标识当前加载的模块
     * 2. 配置隔离：区分不同模块的配置和初始化行为
     * 3. 问题排查：帮助开发人员定位模块相关问题
     * 4. 版本管理：跟踪和管理不同模块的版本信息
     *
     * 返回的名称对应于反应式 REST 模块的 starter 包名，
     * 保持了与 Maven 坐标的一致性。
     *
     * @return 模块名称 "cubo-rest-reactive-spring-boot-starter"
     * @since 1.0.0
     */
    @Override
    public String getName() {
        return "cubo-rest-reactive-spring-boot-starter";
    }
}
