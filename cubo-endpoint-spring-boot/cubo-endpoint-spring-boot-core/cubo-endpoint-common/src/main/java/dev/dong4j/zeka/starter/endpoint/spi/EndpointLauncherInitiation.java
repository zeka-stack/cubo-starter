package dev.dong4j.zeka.starter.endpoint.spi;

import dev.dong4j.zeka.kernel.common.constant.ConfigDefaultValue;
import dev.dong4j.zeka.kernel.common.constant.ConfigKey;
import dev.dong4j.zeka.kernel.common.enums.ZekaEnv;
import dev.dong4j.zeka.kernel.common.start.LauncherInitiation;
import dev.dong4j.zeka.kernel.common.support.ChainMap;
import dev.dong4j.zeka.kernel.common.util.ConfigKit;
import java.util.Map;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Endpoint 模块启动初始化抽象类
 *
 * 该抽象类通过 SPI 机制自动加载 Endpoint 模块的默认配置，
 * 主要负责配置 Spring Boot Actuator 的相关参数，包括：
 *
 * 1. 启用 Actuator 管理端点
 * 2. 配置健康检查详细信息显示
 * 3. 设置管理基础 URL 路径
 * 4. 配置 Git 信息显示模式
 * 5. 根据环境决定是否暴露所有端点
 *
 * 不同的 Web 技术栈（Servlet/Reactive）通过继承该类来实现具体的配置。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 11:12
 * @since 1.0.0
 */
public abstract class EndpointLauncherInitiation implements LauncherInitiation {

    /**
     * 设置默认属性配置
     *
     * 为 Endpoint 模块设置 Spring Boot Actuator 的默认配置参数，
     * 包括启用管理端点、配置健康检查等。
     * 在非生产环境下会暴露所有管理端点。
     *
     * @param env 配置环境对象
     * @param appName 应用名称
     * @param isLocalLaunch 是否本地启动
     * @return 默认配置属性映射
     * @since 1.0.0
     */
    @Override
    public Map<String, Object> setDefaultProperties(ConfigurableEnvironment env,
                                                    String appName,
                                                    boolean isLocalLaunch) {
        // 创建链式配置映射，初始容量为 4
        ChainMap map = ChainMap.build(4)
            // 启用 Actuator 管理端点
            .put(ConfigKey.ManagementConfigKey.ENABLED, ConfigDefaultValue.TRUE)
            // 设置健康检查显示详细信息
            .put(ConfigKey.ManagementConfigKey.HEALTH_DETAILS, "always")
            // 设置管理端点的基础 URL 路径
            .put(ConfigKey.ManagementConfigKey.BASE_URL, "/actuator")
            // 设置 Git 信息显示模式为完整模式
            .put(ConfigKey.ManagementConfigKey.GIT_MODE, "full");

        // 在非生产环境下暴露所有管理端点
        if (!ZekaEnv.PROD.getName().equals(ConfigKit.getProfile(env))) {
            map.put(ConfigKey.ManagementConfigKey.EXPOSURE_INCLUDE, "*");
        }

        return map;
    }

    /**
     * 获取初始化器的优先级
     *
     * 返回一个相对较高的优先级值，确保 Endpoint 配置
     * 在其他组件初始化之前被加载。
     *
     * @return 优先级值，数值越小优先级越高
     * @since 1.0.0
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 200;
    }

}
