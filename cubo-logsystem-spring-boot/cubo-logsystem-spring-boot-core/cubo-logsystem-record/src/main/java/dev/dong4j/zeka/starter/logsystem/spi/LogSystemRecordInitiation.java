package dev.dong4j.zeka.starter.logsystem.spi;

import dev.dong4j.zeka.kernel.common.constant.ConfigDefaultValue;
import dev.dong4j.zeka.kernel.common.constant.ConfigKey;
import dev.dong4j.zeka.kernel.common.start.LauncherInitiation;
import dev.dong4j.zeka.kernel.common.support.ChainMap;
import dev.dong4j.zeka.processor.annotation.AutoService;
import dev.dong4j.zeka.starter.logsystem.constant.LogSystem;
import java.util.Map;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * 日志系统记录初始化器
 *
 * 该类是日志系统记录的初始化器，实现了LauncherInitiation接口。
 * 负责在系统启动时设置日志系统记录的默认配置和属性。
 *
 * 主要功能包括：
 * 1. 设置日志系统记录的默认配置
 * 2. 配置Spring Bean定义覆盖策略
 * 3. 提供系统启动时的初始化逻辑
 * 4. 支持日志系统的标准化配置
 *
 * 使用场景：
 * - 系统启动时的日志系统初始化
 * - 日志系统默认配置的设置
 * - Spring配置的标准化处理
 * - 日志系统模块的启动管理
 *
 * 设计意图：
 * 通过初始化器在系统启动时设置日志系统的默认配置，确保日志系统能够正常工作，
 * 提供日志系统模块的标准化启动流程。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.01.27 12:23
 * @since 1.0.0
 */
@AutoService(LauncherInitiation.class)
public class LogSystemRecordInitiation implements LauncherInitiation {

    /**
     * 设置默认属性
     *
     * 设置日志系统记录的默认配置属性。日志系统配置需要在配置文件被读取后才能设置，
     * 默认配置已通过ZekaLoggingListener进行设置，此处主要配置Spring相关属性。
     *
     * @param env           可配置的环境对象
     * @param appName       应用程序名称
     * @param isLocalLaunch 是否为本地启动
     * @return 默认属性映射表
     * @since 1.0.0
     */
    @Override
    public Map<String, Object> setDefaultProperties(ConfigurableEnvironment env, String appName, boolean isLocalLaunch) {
        return ChainMap.build(1)
            .put(ConfigKey.SpringConfigKey.MAIN_ALLOW_BEAN_DEFINITION_OVERRIDING, ConfigDefaultValue.TRUE);
    }

    /**
     * 获取初始化顺序
     *
     * 返回初始化器的执行顺序，使用最高优先级确保日志系统配置优先执行。
     *
     * @return 初始化顺序，返回最高优先级
     * @since 1.0.0
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    /**
     * 获取初始化器名称
     *
     * 返回初始化器的名称，用于标识日志系统记录初始化器。
     *
     * @return 初始化器名称
     * @since 1.0.0
     */
    @Override
    public String getName() {
        return LogSystem.MODULE_NAME;
    }
}
