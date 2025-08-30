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
 * <p>Description: zeka-stack.logging.file 配置处理</p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.02.26 12:18
 * @since 1.0.0
 */
public final class LogFileProcessor extends AbstractPropertiesProcessor {
    /** File */
    @Getter
    private String name;
    /** Path */
    @Getter
    private String path;
    /** 应用名称 */
    private String appName;

    /**
     * 初始化处理器, 第一步需要处理 name 和 path
     *
     * @param environment environment
     * @since 1.0.0
     */
    public LogFileProcessor(ConfigurableEnvironment environment) {
        super(environment);
        this.setLogAppName();
        this.processorNameAndPath(environment);
    }

    /**
     * 处理 file name 和 path, 需要返回给日志系统用于初始化
     *
     * @param environment environment
     * @since 1.4.0
     */
    private void processorNameAndPath(ConfigurableEnvironment environment) {
        String tempName = this.getProperty(ConfigKey.LogSystemConfigKey.LOG_FILE_NAME,
            Constants.DEPRECATED_FILE_NAME_PROPERTY,
            Constants.DEFAULT_FILE_NAME);

        // 获取日志目录:
        // 1. 如果是脚本启动, 会设置: -Dzeka-stack.logging.file.path=${FINAL_LOG_PATH}
        // 2. 如果非脚本启动, 或者说没有在任何地方配置 zeka-stack.logging.file.path, 则会使用默认配置: /mnt/syslogs/zeka.stack
        // 3. 如果在 application.yml 显式配置了 zeka-stack.logging.file.path 或 logging.file.path 则会直接使用
        String tempPath = this.getProperty(ConfigKey.LogSystemConfigKey.LOG_FILE_PATH,
            Constants.DEPRECATED_FILE_PATH_PROPERTY,
            LogSystem.DEFAULT_LOGGING_LOCATION);

        // 本地开发且没有配置 path 时输出到临时文件
        if (ConfigKit.isLocalLaunch() && LogSystem.DEFAULT_LOGGING_LOCATION.equals(tempPath)) {
            tempPath = FileUtils.getTempDirPath();
            // 应用退出时删除临时日志文件
            File file = new File(tempPath, tempName);
            file.deleteOnExit();
        }

        this.name = tempName;
        // 如果路径不存在, 则创建, 返回的就是 tempPath, 比如 ./logs
        this.path = FileUtils.toPath(tempPath);

        this.appName = ConfigKit.getProperty(environment, ConfigKey.LogSystemConfigKey.LOG_APP_NAME);
        if (StringUtils.isBlank(this.appName)) {
            this.appName = ConfigKit.getProperty(environment, ConfigKey.SpringConfigKey.APPLICATION_NAME);
        }
    }

    /**
     * 设置日志配置文件中的变量, 从配置文件中读取, 有则设置, 没有则不设置, 将使用日志配置文件中的默认配置
     *
     * @since 1.0.0
     */
    private void setLogAppName() {
        this.setSystemProperty(StringUtils.isBlank(this.appName) ? "Please-Inherit-ZekaStarter" : this.appName, Constants.APP_NAME);
    }

    /**
     * 将自定义配置注入到日志系统, 这里首先读取出应用环境的日志配置, 然后注入到系统环境中, 日志系统在读取日志配置文件时, 会从系统环境中替换日志配置中的变量
     *
     * @since 1.0.0
     */
    @Override
    public void apply() {
        // 提前将配置绑定到配置类
        Binder binder = Binder.get(this.environment);

        // 绑定 zeka-stack.logging.file 到 LogFile, 如果没有显式配置 zeka-stack.logging.file, 则使用默认配置
        LogFile logFile = binder.bind(ConfigKey.LogSystemConfigKey.LOG_FILE, LogFile.class)
            .orElse(new LogFile(this.toString(), this.path));
        if (StringUtils.isBlank(logFile.getPath())) {
            logFile.setPath(this.path);
        }
        if (StringUtils.isBlank(logFile.getName())) {
            logFile.setName(this.name);
        }

        // 优先使用显式配置的 zeka-stack.logging.file.path(-Dzeka-stack.logging.file.path 优先级最高), 没有的话则使用默认配置
        // 获取绝对路径表示
        String rawPath = logFile.getPath();
        File pathFile = new File(rawPath);
        String absolutePath = pathFile.getAbsolutePath();

        // 使用绝对路径
        String finalLogPath = FileUtils.toPath(absolutePath);
        String message = "logging file: " + FileUtils.appendPath(finalLogPath, this.name);
        JustOnceLogger.printOnce(LogFileProcessor.class.getName(), message);

        // zeka-stack.logging.file.path
        this.setSystemProperty(finalLogPath, Constants.LOG_BASE_FOLDER,
            ConfigKey.LogSystemConfigKey.LOG_FILE_PATH);

        // zeka-stack.logging.file.name
        this.setSystemProperty(logFile.getName(), "LOG_FILE", ConfigKey.LogSystemConfigKey.LOG_FILE_NAME);
        // zeka-stack.logging.file.clean-history-on-start
        this.setSystemProperty(String.valueOf(logFile.isCleanHistoryOnStart()), ConfigKey.LogSystemConfigKey.LOG_FILE_CLEAN_HISTORY,
            Constants.FILE_CLEAN_HISTORY_ON_START);
        // zeka-stack.logging.file.max-history
        this.setSystemProperty(String.valueOf(logFile.getMaxHistory()), ConfigKey.LogSystemConfigKey.LOG_FILE_MAX_HISTORY,
            Constants.FILE_MAX_HISTORY);
        // zeka-stack.logging.file.max-size
        this.setSystemProperty(logFile.getMaxSize(), ConfigKey.LogSystemConfigKey.LOG_FILE_MAX_SIZE, Constants.FILE_MAX_SIZE);
        // zeka-stack.logging.file.total-size-cap
        this.setSystemProperty(String.valueOf(logFile.getTotalSizeCap()), ConfigKey.LogSystemConfigKey.LOG_FILE_TOTAL_SIZE_CAP,
            Constants.FILE_TOTAL_SIZE_CAP);
    }

    /**
     * 根据 path 和 name 生成完整的日志文件路径
     *
     * @return the string
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
