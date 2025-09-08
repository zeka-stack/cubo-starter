package dev.dong4j.zeka.starter.rest.support;


import dev.dong4j.zeka.kernel.common.constant.App;
import dev.dong4j.zeka.kernel.common.start.ZekaComponentBean;

/**
 * Zeka REST 模块组件标识类
 *
 * 该类实现了 ZekaComponentBean 接口，用于标识和注册 REST 模块组件。
 * 作为组件标识类，它在 Spring 容器中被注册为 Bean，
 * 用于记录和管理模块的加载状态。
 *
 * 主要作用：
 * 1. 模块标识：标识 REST 模块已经被正确加载
 * 2. 依赖检查：其他模块可以检查此组件是否存在
 * 3. 状态监控：可用于监控模块的加载和运行状态
 * 4. 组件管理：统一管理所有 Zeka 组件的注册信息
 *
 * 配置位置：
 * 通常在模块的自动配置类中被声明为 Bean，
 * 确保在模块加载时能够被正确识别和注册。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.01.16 02:39
 * @since 1.0.0
 */
public class ZekaRestComponent implements ZekaComponentBean {
    /**
     * 获取组件名称
     *
     * 返回当前 REST 模块组件的唯一标识名称。
     * 该名称由 App.Components 常量统一定义，
     * 确保在整个框架中的一致性和唯一性。
     *
     * 组件名称用途：
     * - 组件注册和查找
     * - 日志记录和监控
     * - 依赖检查和管理
     * - 系统诊断和状态检查
     *
     * @return REST 模块组件的标识名称
     * @since 1.0.0
     */
    @Override
    public String componentName() {
        return App.Components.REST_SPRING_BOOT;
    }
}
