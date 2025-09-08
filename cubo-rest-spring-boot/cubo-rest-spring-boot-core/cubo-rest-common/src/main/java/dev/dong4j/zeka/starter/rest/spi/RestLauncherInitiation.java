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
 * REST 模块启动器初始化类
 *
 * 该抽象类作为 REST 模块的启动器初始化组件，负责加载和配置
 * REST 相关的默认配置参数。通过 SPI 机制实现模块化的配置加载。
 *
 * 主要功能：
 * 1. 设置 Undertow 服务器的访问日志配置
 * 2. 配置 Spring MVC 的基础参数（编码、异常处理等）
 * 3. 设置 Jackson JSON 序列化的默认配置
 * 4. 为本地开发和生产环境配置不同的参数
 * 5. 在应用启动后打印访问日志路径信息
 *
 * 配置特点：
 * - 本地开发环境使用随机端口避免冲突
 * - 默认开启 Undertow 访问日志记录
 * - 设置 UTF-8 编码以支持国际化
 * - 配置 JSON 序列化时排除 null 值字段
 *
 * 作为抽象类，子类需要实现具体的模块名称和特定配置。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 11:17
 * @since 1.0.0
 */
@Slf4j
public abstract class RestLauncherInitiation implements LauncherInitiation {
    // 格式化器：关闭格式化
    /** Undertow 访问日志的默认格式模式，参考 io.undertow.server.handlers.accesslog.AccessLogHandler */
    @SuppressWarnings("checkstyle:LineLength")
    private static final String LOG_DEFAULT_PATTERN = "[%{time,yyyy-MM-dd HH:mm:ss.SSS}] \"%r\" %s (%D ms) (%b bytes) %{i,X-Trace-Id} %{i,X-Agent-Api}_%{i,X-Agent-Version},%{i,User-Agent} %l %u %v";
    // 格式化器：恢复格式化

    /**
     * 设置 REST 模块的默认配置属性
     *
     * 该方法作为模块初始化的核心方法，负责为 REST 模块设置各种默认配置。
     * 这些配置包括 Undertow 服务器参数、Spring MVC 参数、Jackson 参数等。
     *
     * 配置分类：
     * 1. Undertow 访问日志配置：日志格式、存储路径、文件命名等
     * 2. Spring MVC 配置：404 异常处理、编码设置等
     * 3. 服务器配置：端口选择策略（本地环境使用随机端口）
     * 4. JSON 序列化配置：包含策略设置
     *
     * 优先级：
     * 这些配置作为默认值，可以被用户自定义配置覆盖。
     *
     * @param env 环境变量信息，用于读取现有配置
     * @param appName 应用名称，用于构建特定的配置值
     * @param isLocalLaunch 是否为本地启动，影响部分配置的选择
     * @return 包含默认配置的 Map 对象，key 为配置名，value 为配置值
     * @see dev.dong4j.zeka.starter.launcher.listener.ZekaLauncherListener#onApplicationStartingEvent(ApplicationStartingEvent)
     * @see dev.dong4j.zeka.starter.launcher.env.RangeRandomValuePropertySource
     * @since 1.0.0
     */
    @Override
    @SuppressWarnings("checkstyle:Regexp")
    public Map<String, Object> setDefaultProperties(ConfigurableEnvironment env,
                                                    String appName,
                                                    boolean isLocalLaunch) {
        // 计算容器日志输出的默认路径（默认使用系统临时目录）
        String undertowLogDir = FileUtils.toTempDirPath(ConfigDefaultValue.DEFAULE_ACCESS_LOG_DIR);
        if (!isLocalLaunch) {
            // 非本地环境时，设置为生产环境的日志路径
            String logPath = System.getProperty(ConfigKey.LogSystemConfigKey.LOG_FILE_PATH, LogSystem.DEFAULT_LOGGING_LOCATION);
            undertowLogDir = FileUtils.appendPath(logPath, ConfigDefaultValue.DEFAULE_ACCESS_LOG_DIR);
        }

        // 默认服务器端口设置：生产环境使用 8080，本地环境使用随机端口
        Object port = 8080;
        if (StringUtils.isNotBlank(System.getProperty(App.START_APPLICATION))) {
            // 如果存在 START_APPLICATION 环境变量，则表示使用了 cubo-launcher-spring-boot-starter 依赖
            // 此时使用随机端口范围避免端口冲突
            port = "${range.random.int(18000, 18200)}";
        }

        return ChainMap.build(16)
            // Undertow 访问日志相关配置
            .put(ConfigKey.UndertowConfigKye.ENABLE_ACCESSLOG, ConfigDefaultValue.TRUE)
            .put(ConfigKey.UndertowConfigKye.ACCESSLOG_DIR, undertowLogDir)
            .put(ConfigKey.UndertowConfigKye.ACCESSLOG_PATTERN, LOG_DEFAULT_PATTERN)
            .put(ConfigKey.UndertowConfigKye.ACCESSLOG_PREFIX, "access.")
            .put(ConfigKey.UndertowConfigKye.ACCESSLOG_SUFFIX, "log")

            // Spring MVC 相关配置
            .put(ConfigKey.MvcConfigKey.NO_HANDLER_FOUND, ConfigDefaultValue.TRUE)
            .put(ConfigKey.MvcConfigKey.ENCODING_ENABLED, ConfigDefaultValue.TRUE)
            .put(ConfigKey.MvcConfigKey.ENCODING_FORCE, ConfigDefaultValue.TRUE)
            .put(ConfigKey.MvcConfigKey.ENCODING_CHARSET, StringPool.UTF_8)
            // 服务器端口配置
            .put(ConfigKey.SpringConfigKey.SERVER_PORT, port)
            // Jackson JSON 序列化配置：序列化时只包含不为空的字段
            .put(ConfigKey.SpringConfigKey.JACKSON_DEFAULT_PROPERTY_INCLUSION, ConfigDefaultValue.DEFAULT_PROPERTY_INCLUSION_VALUE);
    }

    /**
     * 在应用容器启动完成后执行的后置处理逻辑
     *
     * 该方法在 Spring 应用上下文完全初始化后被调用，
     * 主要用于打印访问日志的存储位置信息，方便开发者查看。
     *
     * 处理流程：
     * 1. 从环境配置中获取访问日志的相关参数
     * 2. 构建完整的日志文件路径
     * 3. 在控制台输出日志文件的绝对路径信息
     *
     * 信息输出：
     * 在控制台直接输出日志文件路径，方便开发者快速定位日志文件。
     *
     * @param context 应用上下文，用于获取环境配置信息
     * @param localLaunch 是否为本地启动，影响日志输出的详细程度
     * @since 1.0.0
     */
    @Override
    public void after(ConfigurableApplicationContext context, @NotNull Boolean localLaunch) {
        log.debug("[{}] 容器启动完成, 开始注入自定义逻辑", getName());
        final ConfigurableEnvironment environment = context.getEnvironment();
        // 从环境配置中获取访问日志的文件名前缀和后缀
        final String logPrefix = environment.getProperty(ConfigKey.UndertowConfigKye.ACCESSLOG_PREFIX);
        final String logSuffix = environment.getProperty(ConfigKey.UndertowConfigKye.ACCESSLOG_SUFFIX);

        // 获取访问日志的目录路径
        final String undertowLogDir = environment.getProperty(ConfigKey.UndertowConfigKye.ACCESSLOG_DIR);
        if (StringUtils.isNotBlank(undertowLogDir)) {
            File pathFile = new File(undertowLogDir);
            String absolutePath = pathFile.getAbsolutePath();
            // 使用绝对路径确保路径的准确性
            String finalLogPath = FileUtils.toPath(absolutePath);
            // 在控制台输出完整的访问日志文件路径，方便开发者查看
            System.out.println("access log: " + FileUtils.appendPath(finalLogPath, logPrefix + logSuffix));
        }
    }

    /**
     * 获取初始化器的执行优先级
     *
     * 该方法返回初始化器在 Spring 启动过程中的执行顺序。
     * 数值越小表示优先级越高，越早被执行。
     *
     * 当前设置：
     * HIGHEST_PRECEDENCE + 101，表示在高优先级组件之后执行，
     * 但仍具有较高的优先级，确保基础配置能够及时生效。
     *
     * @return 优先级数值，越小优先级越高
     * @since 1.0.0
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 101;
    }

}
