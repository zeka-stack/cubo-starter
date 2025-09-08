package dev.dong4j.zeka.starter.logsystem.handler;

import dev.dong4j.zeka.kernel.common.constant.ConfigKey;
import dev.dong4j.zeka.kernel.common.util.ConfigKit;
import dev.dong4j.zeka.kernel.common.util.FileUtils;
import dev.dong4j.zeka.kernel.common.util.JustOnceLogger;
import dev.dong4j.zeka.kernel.common.util.StringUtils;
import dev.dong4j.zeka.starter.logsystem.AbstractPropertiesProcessor;
import dev.dong4j.zeka.starter.logsystem.Constants;
import dev.dong4j.zeka.starter.logsystem.constant.LogSystem;
import dev.dong4j.zeka.starter.logsystem.entity.LogFile;
import java.io.File;
import lombok.Getter;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * 日志文件配置处理器
 *
 * 该类负责处理 zeka-stack.logging.file 相关的配置，管理日志文件的创建、路径设置和属性配置。
 * 主要功能包括：
 * 1. 处理日志文件的名称和路径配置
 * 2. 管理日志文件的滚动和清理策略
 * 3. 支持本地开发和生产环境的不同配置
 * 4. 提供日志文件路径的自动创建和管理
 *
 * 处理的配置项：
 * - 日志文件名称和路径
 * - 日志文件滚动策略（最大历史、最大大小、总大小限制）
 * - 启动时清理历史日志
 * - 应用名称设置
 *
 * 使用场景：
 * - 日志系统初始化时的文件配置
 * - 多环境下的日志文件管理
 * - 本地开发和生产环境的差异化配置
 * - 日志文件的自动创建和清理
 *
 * 设计意图：
 * 通过统一的文件配置处理器，简化日志文件的管理和配置，
 * 提供灵活的日志文件策略和环境适配能力。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.02.26 12:18
 * @since 1.0.0
 */
public final class LogFileProcessor extends AbstractPropertiesProcessor {
    /** 日志文件名称 */
    @Getter
    private String name;

    /** 日志文件路径 */
    @Getter
    private String path;

    /** 应用名称 */
    private String appName;

    /**
     * 构造函数
     *
     * 初始化日志文件处理器，第一步需要处理文件名称和路径。
     *
     * @param environment 可配置的环境对象，用于获取配置属性
     * @since 1.0.0
     */
    public LogFileProcessor(ConfigurableEnvironment environment) {
        super(environment);
        this.setLogAppName();
        this.processorNameAndPath(environment);
    }

    /**
     * 处理日志文件名称和路径
     *
     * 处理日志文件的名称和路径配置，需要返回给日志系统用于初始化。
     * 根据不同的启动方式和配置优先级确定最终的日志文件路径。
     *
     * 路径获取优先级：
     * 1. 脚本启动时设置的JVM参数：-Dzeka-stack.logging.file.path=${FINAL_LOG_PATH}
     * 2. 默认配置路径：/mnt/syslogs/zeka.stack
     * 3. application.yml中显式配置的路径
     *
     * 本地开发特殊处理：
     * - 本地开发且使用默认路径时，输出到临时目录
     * - 应用退出时自动删除临时日志文件
     *
     * @param environment 可配置的环境对象，用于获取配置属性
     * @since 1.0.0
     */
    private void processorNameAndPath(ConfigurableEnvironment environment) {
        // 获取日志文件名称，支持废弃属性兼容
        String tempName = this.getProperty(ConfigKey.LogSystemConfigKey.LOG_FILE_NAME,
            Constants.DEPRECATED_FILE_NAME_PROPERTY,
            Constants.DEFAULT_FILE_NAME);

        // 获取日志目录路径，支持多种配置方式
        String tempPath = this.getProperty(ConfigKey.LogSystemConfigKey.LOG_FILE_PATH,
            Constants.DEPRECATED_FILE_PATH_PROPERTY,
            LogSystem.DEFAULT_LOGGING_LOCATION);

        // 本地开发环境的特殊处理
        if (ConfigKit.isLocalLaunch() && LogSystem.DEFAULT_LOGGING_LOCATION.equals(tempPath)) {
            tempPath = FileUtils.getTempDirPath();
            // 设置应用退出时删除临时日志文件
            File file = new File(tempPath, tempName);
            file.deleteOnExit();
        }

        this.name = tempName;
        // 确保路径存在，如果不存在则创建
        this.path = FileUtils.toPath(tempPath);

        // 获取应用名称，优先使用日志配置中的应用名称
        this.appName = ConfigKit.getProperty(environment, ConfigKey.LogSystemConfigKey.LOG_APP_NAME);
        if (StringUtils.isBlank(this.appName)) {
            this.appName = ConfigKit.getProperty(environment, ConfigKey.SpringConfigKey.APPLICATION_NAME);
        }
    }

    /**
     * 设置日志应用名称
     *
     * 设置日志配置文件中的应用名称变量。从配置文件中读取应用名称，
     * 如果未配置则使用默认值，将使用日志配置文件中的默认配置。
     *
     * @since 1.0.0
     */
    private void setLogAppName() {
        this.setSystemProperty(StringUtils.isBlank(this.appName) ? "Please-Inherit-ZekaStarter" : this.appName, Constants.APP_NAME);
    }

    /**
     * 应用日志文件配置
     *
     * 将自定义配置注入到日志系统中。首先读取应用环境的日志配置，
     * 然后注入到系统环境中，日志系统在读取日志配置文件时，
     * 会从系统环境中替换日志配置中的变量。
     *
     * 处理流程：
     * 1. 绑定日志文件配置到LogFile对象
     * 2. 处理日志文件路径和名称
     * 3. 设置各种日志文件相关的系统属性
     * 4. 配置日志文件滚动和清理策略
     *
     * @since 1.0.0
     */
    @Override
    public void apply() {
        // 绑定日志文件配置到LogFile对象
        Binder binder = Binder.get(this.environment);

        // 绑定 zeka-stack.logging.file 配置，如果没有显式配置则使用默认配置
        LogFile logFile = binder.bind(ConfigKey.LogSystemConfigKey.LOG_FILE, LogFile.class)
            .orElse(new LogFile(this.toString(), this.path));
        if (StringUtils.isBlank(logFile.getPath())) {
            logFile.setPath(this.path);
        }
        if (StringUtils.isBlank(logFile.getName())) {
            logFile.setName(this.name);
        }

        // 获取绝对路径表示
        String rawPath = logFile.getPath();
        File pathFile = new File(rawPath);
        String absolutePath = pathFile.getAbsolutePath();

        // 使用绝对路径确保路径正确性
        String finalLogPath = FileUtils.toPath(absolutePath);
        String message = "logging file: " + FileUtils.appendPath(finalLogPath, this.name);
        JustOnceLogger.printOnce(LogFileProcessor.class.getName(), message);

        // 设置日志文件路径系统属性
        this.setSystemProperty(finalLogPath, Constants.LOG_BASE_FOLDER,
            ConfigKey.LogSystemConfigKey.LOG_FILE_PATH);

        // 设置日志文件名称系统属性
        this.setSystemProperty(logFile.getName(), "LOG_FILE", ConfigKey.LogSystemConfigKey.LOG_FILE_NAME);
        // 设置启动时清理历史日志系统属性
        this.setSystemProperty(String.valueOf(logFile.isCleanHistoryOnStart()), ConfigKey.LogSystemConfigKey.LOG_FILE_CLEAN_HISTORY,
            Constants.FILE_CLEAN_HISTORY_ON_START);
        // 设置最大历史文件数量系统属性
        this.setSystemProperty(String.valueOf(logFile.getMaxHistory()), ConfigKey.LogSystemConfigKey.LOG_FILE_MAX_HISTORY,
            Constants.FILE_MAX_HISTORY);
        // 设置单个文件最大大小系统属性
        this.setSystemProperty(logFile.getMaxSize(), ConfigKey.LogSystemConfigKey.LOG_FILE_MAX_SIZE, Constants.FILE_MAX_SIZE);
        // 设置总大小限制系统属性
        this.setSystemProperty(String.valueOf(logFile.getTotalSizeCap()), ConfigKey.LogSystemConfigKey.LOG_FILE_TOTAL_SIZE_CAP,
            Constants.FILE_TOTAL_SIZE_CAP);
    }

    /**
     * 生成完整的日志文件路径
     *
     * 根据路径和名称生成完整的日志文件路径。如果名称为空，
     * 则使用默认文件名。
     *
     * @return 完整的日志文件路径字符串
     * @since 1.0.0
     */
    @Override
    public String toString() {
        if (StringUtils.hasLength(this.name)) {
            return this.name;
        }
        return new File(this.path, Constants.DEFAULT_FILE_NAME).getPath();
    }

}
