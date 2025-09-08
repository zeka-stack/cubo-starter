package dev.dong4j.zeka.starter.logsystem.handler;

import dev.dong4j.zeka.kernel.common.constant.App;
import dev.dong4j.zeka.kernel.common.constant.ConfigDefaultValue;
import dev.dong4j.zeka.kernel.common.constant.ConfigKey;
import dev.dong4j.zeka.kernel.common.util.ConfigKit;
import dev.dong4j.zeka.kernel.common.util.JustOnceLogger;
import dev.dong4j.zeka.kernel.common.util.StringUtils;
import dev.dong4j.zeka.starter.logsystem.AbstractPropertiesProcessor;
import dev.dong4j.zeka.starter.logsystem.Constants;
import dev.dong4j.zeka.starter.logsystem.enums.LogAppenderType;
import dev.dong4j.zeka.starter.logsystem.listener.ZekaLoggingListener;
import org.slf4j.MDC;
import org.springframework.boot.context.logging.LoggingApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;


/**
 * 附加配置处理器
 *
 * 该类负责处理日志系统的附加配置，将自定义配置注入到日志系统中。
 * 主要功能包括：
 * 1. 根据环境设置合适的日志配置文件
 * 2. 配置日志位置显示功能
 * 3. 设置环境标识和启动类型
 * 4. 管理日志配置的系统属性
 *
 * 处理的配置项：
 * - 日志配置文件选择（console/file/docker）
 * - 日志位置显示开关
 * - 环境标识设置
 * - 启动类型判断
 *
 * 使用场景：
 * - 日志系统初始化时的环境配置
 * - 多环境下的日志配置切换
 * - 开发和生产环境的差异化配置
 * - Docker容器环境的特殊配置
 *
 * 设计意图：
 * 通过统一的配置处理器，简化日志系统的环境配置管理，
 * 提供灵活的日志配置和环境切换能力。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.02.25 21:36
 * @since 1.0.0
 */
public class AdditionalProcessor extends AbstractPropertiesProcessor {
    /**
     * 构造函数
     *
     * 创建附加配置处理器实例，初始化环境配置。
     *
     * @param environment 可配置的环境对象，用于获取配置属性
     * @since 1.0.0
     */
    public AdditionalProcessor(ConfigurableEnvironment environment) {
        super(environment);
    }

    /**
     * 应用附加配置
     *
     * 将自定义配置注入到日志系统中。首先读取应用环境的日志配置，
     * 然后注入到系统环境中，日志系统在读取日志配置文件时，
     * 会从系统环境中替换日志配置中的变量。
     *
     * 处理流程：
     * 1. 判断是否为本地启动环境
     * 2. 设置日志配置文件
     * 3. 设置日志位置显示功能
     *
     * @since 1.0.0
     */
    @Override
    public void apply() {
        boolean isLocalLaunch = ConfigKit.isLocalLaunch();

        // 设置日志配置文件
        this.setLogConfigFile(isLocalLaunch);
        // 设置日志位置显示功能
        this.setShowLogLocation(isLocalLaunch);
    }

    /**
     * 设置日志配置文件
     *
     * 根据当前环境选择合适的日志配置文件。优先使用业务端设置的日志配置文件名，
     * 如果未配置则会根据当前环境使用 log4j2-console.xml 或者 log4j2-file.xml 配置文件。
     *
     * 配置策略：
     * 1. 本地开发环境：使用控制台输出配置
     * 2. Docker环境：使用Docker输出配置
     * 3. 服务器部署：使用文件输出配置
     *
     * @param isLocalLaunch 是否为本地启动环境
     * @see ZekaLoggingListener#initializeSystem
     * @see LoggingApplicationListener#initializeSystem
     * @since 1.0.0
     */
    @SuppressWarnings("all")
    private void setLogConfigFile(boolean isLocalLaunch) {
        // 设置当前环境标识，通过 ${ctx:env} 读取
        MDC.put("env", "本地开发");
        LogAppenderType appenderType = LogAppenderType.CONSOLE;

        // 判断Docker启动方式
        final String startType = System.getProperty(App.START_TYPE);
        if (App.START_DOCKER.equals(startType)) {
            MDC.put("env", "Docker 启动");
            appenderType = LogAppenderType.DOCKER;
        } else if (!isLocalLaunch) {
            // 非Docker启动且非本地环境时，将日志输出到文件
            MDC.put("env", "服务器部署");
            appenderType = LogAppenderType.FILE;
        }

        // 获取日志配置文件名称
        String logConfig = this.environment.getProperty(ConfigKey.LogSystemConfigKey.LOG_CONFIG, appenderType.getConfig());

        // 设置日志配置文件系统属性
        System.setProperty(LoggingApplicationListener.CONFIG_PROPERTY, StringUtils.format("classpath:{}", logConfig));
        JustOnceLogger.printOnce(AdditionalProcessor.class.getName(), ConfigKey.PREFIX + "logging.config=" + logConfig);
    }

    /**
     * 设置日志位置显示功能
     *
     * 配置是否显示日志输出位置信息，用于本地开发时快速跳转到日志输出位置。
     * 通过 zeka-stack.logging.enable-show-location 配置项控制。
     *
     * 配置策略：
     * 1. 本地开发环境且未配置时，默认开启
     * 2. 其他环境根据业务端配置为准
     * 3. 关闭时替换布局格式，移除位置信息
     *
     * @param isLocalLaunch 是否为本地启动环境
     * @since 1.0.0
     */
    private void setShowLogLocation(boolean isLocalLaunch) {

        String showLogLocation = this.resolver.getProperty(ConfigKey.LogSystemConfigKey.SHOW_LOG_LOCATION);

        // 本地开发环境且未配置时，默认开启位置显示
        if (isLocalLaunch && StringUtils.isBlank(showLogLocation)) {
            showLogLocation = ConfigDefaultValue.TRUE_STRING;
        }

        // 解析位置显示配置
        boolean enableShowLocation = Boolean.parseBoolean(showLogLocation);
        if (!enableShowLocation) {
            // 关闭位置显示时，替换布局格式为简化版本
            System.setProperty(Constants.SHOW_LOG_LOCATION_LAYOUT, "%clr{%c{1.}}{cyan}");
        }

        // 设置位置显示系统属性
        System.setProperty(Constants.SHOW_LOG_LOCATION, String.valueOf(enableShowLocation));
    }

}
