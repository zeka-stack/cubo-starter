package dev.dong4j.zeka.starter.rest.spi;

import dev.dong4j.zeka.kernel.common.constant.App;
import dev.dong4j.zeka.kernel.common.constant.ConfigDefaultValue;
import dev.dong4j.zeka.kernel.common.constant.ConfigKey;
import dev.dong4j.zeka.kernel.common.start.LauncherInitiation;
import dev.dong4j.zeka.kernel.common.support.ChainMap;
import dev.dong4j.zeka.kernel.common.util.FileUtils;
import dev.dong4j.zeka.kernel.common.util.StringPool;
import dev.dong4j.zeka.kernel.common.util.StringUtils;
import dev.dong4j.zeka.starter.logsystem.constant.LogSystem;
import java.io.File;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * <p>Description: rest 加载默认配置 </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 11:17
 * @since 1.0.0
 */
@Slf4j
public abstract class RestLauncherInitiation implements LauncherInitiation {
    // formatter:off
    /** NOT_LOCALLAUNCHER_LOG_DEFAULT_PATTERN @see io.undertow.server.handlers.accesslog.AccessLogHandler */
    @SuppressWarnings("checkstyle:LineLength")
    private static final String LOG_DEFAULT_PATTERN = "[%{time,yyyy-MM-dd HH:mm:ss.SSS}] \"%r\" %s (%D ms) (%b bytes) %{i,X-Trace-Id} %{i,X-Agent-Api}_%{i,X-Agent-Version},%{i,User-Agent} %l %u %v";
    // formatter:on

    /**
     * Launcher *
     * {@link dev.dong4j.zeka.starter.launcher.env.RangeRandomValuePropertySource}
     *
     * @param env           env
     * @param appName       app name
     * @param isLocalLaunch is local launch
     * @return the map
     * @see dev.dong4j.zeka.starter.launcher.listener.ZekaLauncherListener#onApplicationStartingEvent(ApplicationStartingEvent)
     * @since 1.0.0
     */
    @Override
    @SuppressWarnings("checkstyle:Regexp")
    public Map<String, Object> setDefaultProperties(ConfigurableEnvironment env,
                                                    String appName,
                                                    boolean isLocalLaunch) {
        // 容器日志输出的默认路径(临时目录)
        String undertowLogDir = FileUtils.toTempDirPath(ConfigDefaultValue.DEFAULE_ACCESS_LOG_DIR);
        if (!isLocalLaunch) {
            // 非本地环境, 设置默认路径
            String logPath = System.getProperty(ConfigKey.LogSystemConfigKey.LOG_FILE_PATH, LogSystem.DEFAULT_LOGGING_LOCATION);
            undertowLogDir = FileUtils.appendPath(logPath, ConfigDefaultValue.DEFAULE_ACCESS_LOG_DIR);
        }

        // 如果存在 START_APPLICATION 环境变量, 则表示使用了 cubo-launcher-spring-boot-starter 依赖
        Object port = 8080;
        if (StringUtils.isNotBlank(System.getProperty(App.START_APPLICATION))) {
            port = "${range.random.int(18000, 18200)}";
        }

        return ChainMap.build(16)
            .put(ConfigKey.UndertowConfigKye.ENABLE_ACCESSLOG, ConfigDefaultValue.TRUE)
            .put(ConfigKey.UndertowConfigKye.ACCESSLOG_DIR, undertowLogDir)
            .put(ConfigKey.UndertowConfigKye.ACCESSLOG_PATTERN, LOG_DEFAULT_PATTERN)
            .put(ConfigKey.UndertowConfigKye.ACCESSLOG_PREFIX, "access.")
            .put(ConfigKey.UndertowConfigKye.ACCESSLOG_SUFFIX, "log")

            .put(ConfigKey.MvcConfigKey.NO_HANDLER_FOUND, ConfigDefaultValue.TRUE)
            .put(ConfigKey.MvcConfigKey.ENCODING_ENABLED, ConfigDefaultValue.TRUE)
            .put(ConfigKey.MvcConfigKey.ENCODING_FORCE, ConfigDefaultValue.TRUE)
            .put(ConfigKey.MvcConfigKey.ENCODING_CHARSET, StringPool.UTF_8)
            .put(ConfigKey.SpringConfigKey.SERVER_PORT, port)
            // 序列化时只包含不为空的字段
            .put(ConfigKey.SpringConfigKey.JACKSON_DEFAULT_PROPERTY_INCLUSION, ConfigDefaultValue.DEFAULT_PROPERTY_INCLUSION_VALUE);
    }

    /**
     * 后
     *
     * @param context     语境
     * @param localLaunch 本地发布
     */
    @Override
    public void after(ConfigurableApplicationContext context, @NotNull Boolean localLaunch) {
        log.debug("[{}] 容器启动完成, 开始注入自定义逻辑", getName());
        final ConfigurableEnvironment environment = context.getEnvironment();
        final String logPrefix = environment.getProperty(ConfigKey.UndertowConfigKye.ACCESSLOG_PREFIX);
        final String logSuffix = environment.getProperty(ConfigKey.UndertowConfigKye.ACCESSLOG_SUFFIX);

        final String undertowLogDir = environment.getProperty(ConfigKey.UndertowConfigKye.ACCESSLOG_DIR);
        if (StringUtils.isNotBlank(undertowLogDir)) {
            File pathFile = new File(undertowLogDir);
            String absolutePath = pathFile.getAbsolutePath();
            // 使用绝对路径
            String finalLogPath = FileUtils.toPath(absolutePath);
            System.out.println("access log: " + FileUtils.appendPath(finalLogPath, logPrefix + logSuffix));
        }
    }

    /**
     * Gets order *
     *
     * @return the order
     * @since 1.0.0
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 101;
    }

}
