package dev.dong4j.zeka.starter.launcher;

import dev.dong4j.zeka.kernel.common.asserts.Assertions;
import dev.dong4j.zeka.kernel.common.constant.App;
import dev.dong4j.zeka.kernel.common.constant.ConfigKey;
import dev.dong4j.zeka.kernel.common.env.DefaultEnvironment;
import dev.dong4j.zeka.kernel.common.exception.InstanceException;
import dev.dong4j.zeka.kernel.common.start.LauncherInitiation;
import dev.dong4j.zeka.kernel.common.util.CollectionUtils;
import dev.dong4j.zeka.kernel.common.util.ConfigKit;
import dev.dong4j.zeka.kernel.common.util.GsonUtils;
import dev.dong4j.zeka.kernel.common.util.ObjectUtils;
import dev.dong4j.zeka.kernel.common.util.StartUtils;
import dev.dong4j.zeka.kernel.common.util.StringPool;
import dev.dong4j.zeka.kernel.common.util.StringUtils;
import dev.dong4j.zeka.kernel.common.util.Tools;
import dev.dong4j.zeka.starter.launcher.enums.ApplicationType;
import dev.dong4j.zeka.starter.launcher.enums.SpringApplicationType;
import java.io.File;
import java.io.FileInputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.SimpleCommandLinePropertySource;

/**
 * Spring Boot 应用启动类封装，提供统一的应用启动流程和配置管理
 *
 * 主要功能：
 * 1. 自动加载默认配置，简化应用启动过程
 * 2. 通过 SPI 机制加载其他包的处理类，实现模块化扩展
 * 3. 统一管理应用名称和环境配置
 * 4. 支持多种应用类型（Web、Service等）的自动识别和配置
 *
 * 应用名称(appName)读取优先级：
 * 1. JVM 环境变量
 * 2. 配置文件中的设置
 * 3. run() 方法显式指定
 * 4. 应用所在目录名（默认值）
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.26 19:23
 * @since 1.0.0
 */
@Slf4j
public final class ZekaApplication {
    /** 保存最主要的配置 */
    private static final Properties MAIN_PROPERTIES;
    private static List<LauncherInitiation> launcherInitiations;

    static {
        MAIN_PROPERTIES = loadMainProperties();
    }

    /**
     * 私有构造方法，防止实例化
     *
     * 该类设计为工具类，不应被直接实例化使用
     *
     * @since 1.0.0
     */
    @Contract(pure = true)
    private ZekaApplication() {
        throw new InstanceException("不可实例化");
    }

    /**
     * 运行 Spring Boot 应用并返回应用上下文
     *
     * 此方法会自动推断应用类型，并使用默认的应用名称
     *
     * @param source 包含 @SpringBootApplication 注解的主类
     * @param args   命令行参数
     * @return Spring 应用上下文
     * @throws Exception 启动过程中可能抛出的异常
     * @since 1.0.0
     */
    public static ConfigurableApplicationContext run(Class<?> source,
                                                     String... args) throws Exception {
        return run(source, ApplicationType.deduceFromClasspath(), args);
    }

    /**
     * 使用指定的应用名称运行 Spring Boot 应用
     *
     * 此方法允许显式指定应用名称，并自动推断应用类型
     *
     * @param appName 应用名称，将用作 spring.application.name
     * @param source  包含 @SpringBootApplication 注解的主类
     * @param args    命令行参数
     * @return Spring 应用上下文
     * @since 1.0.0
     */
    public static ConfigurableApplicationContext run(String appName,
                                                     Class<?> source,
                                                     String... args) {
        return run(appName, source, ApplicationType.deduceFromClasspath(), args);
    }

    /**
     * 使用默认应用名称和指定应用类型运行 Spring Boot 应用
     *
     * 如果未显式设置应用名称，则使用从配置中读取的默认应用名
     *
     * @param source          包含 @SpringBootApplication 注解的主类
     * @param applicationType 应用类型（SERVLET、REACTIVE、SERVICE、NONE）
     * @param args            命令行参数
     * @return Spring 应用上下文
     * @since 1.0.0
     */
    public static ConfigurableApplicationContext run(Class<?> source, ApplicationType applicationType, String... args) {
        // 没有设置 application name 时, 使用默认应用名
        return run(MAIN_PROPERTIES.getProperty(ConfigKey.SpringConfigKey.APPLICATION_NAME), source, applicationType, args);
    }

    /**
     * 创建并运行 Spring Boot 应用上下文
     *
     * 完整的应用启动方法，支持指定应用名称和应用类型
     * 示例：java -jar app.jar --spring.profiles.active=prod --server.port=2333
     *
     * @param appName         应用名称，将用作 spring.application.name
     * @param source          包含 @SpringBootApplication 注解的主类
     * @param applicationType 应用类型（SERVLET、REACTIVE、SERVICE、NONE）
     * @param args            命令行参数
     * @return 创建的 Spring 应用上下文
     * @since 1.0.0
     */
    public static ConfigurableApplicationContext run(String appName,
                                                     Class<?> source,
                                                     ApplicationType applicationType,
                                                     String... args) {
        // 设置是否使用 ZekaApplication 启动标识
        System.setProperty(App.START_APPLICATION, App.START_APPLICATION);
        ConfigurableApplicationContext context;
        // 优先使用启动类中设置的 application name
        MAIN_PROPERTIES.setProperty(ConfigKey.SpringConfigKey.APPLICATION_NAME, appName);
        SpringApplicationBuilder builder = createSpringApplicationBuilder(appName, source, applicationType, args);
        builder.registerShutdownHook(true);
        context = builder.run(args);
        // 使用 SPI 在应用启动后注入自定义逻辑
        launcherInitiations.forEach(launcherService -> launcherService.after(context, ConfigKit.isLocalLaunch()));
        launcherInitiations = null;
        return context;
    }

    /**
     * 创建 SpringApplicationBuilder 并进行配置
     *
     * 该方法负责：
     * 1. 设置默认配置和环境变量
     * 2. 通过 SPI 机制加载其他包的组件
     * 3. 配置应用类型和环境
     *
     * @param appName         应用名称
     * @param source          包含 @SpringBootApplication 注解的主类
     * @param applicationType 应用类型
     * @param args            命令行参数
     * @return 配置好的 SpringApplicationBuilder 实例
     * @since 1.0.0
     */
    @NotNull
    private static SpringApplicationBuilder createSpringApplicationBuilder(String appName,
                                                                           Class<?> source,
                                                                           @NotNull ApplicationType applicationType,
                                                                           String... args) {
        Assertions.notBlank(appName, "[appName] 服务名不能为空");
        StartUtils.setFrameworkVersion();

        // 生成默认的配置
        Properties defaultProperties = buildDefaultProperties(appName);

        // 读取环境变量,使用 spring boot 的规则 (获取系统参数和 JVM 参数)
        ConfigurableEnvironment environment = new DefaultEnvironment();
        MutablePropertySources propertySources = environment.getPropertySources();
        propertySources.addFirst(new SimpleCommandLinePropertySource(args));

        // 加载自定义 SPI 组件, 用于在容器启动前注入自定义逻辑, 比如设置组件的默认配置以减少业务上的配置
        ServiceLoader<LauncherInitiation> loader = ServiceLoader.load(LauncherInitiation.class);
        launcherInitiations = CollectionUtils.toList(loader)
            .stream()
            .sorted(Comparator.comparingInt(LauncherInitiation::getOrder))
            .collect(Collectors.toList());

        launcherInitiations.forEach(launcherService -> launcherService.launcherWrapper(environment,
            defaultProperties,
            appName,
            ConfigKit.isLocalLaunch()));

        log.debug("应用类型: ApplicationType = {}", applicationType.name());
        ConfigKit.setSystemProperties(App.APPLICATION_TYPE, applicationType.name());

        // 转换类型
        if (applicationType == ApplicationType.SERVICE) {
            applicationType = ApplicationType.NONE;
        }

        SpringApplicationBuilder builder = new SpringApplicationBuilder(source)
            .web(Tools.convert(applicationType.name(), org.springframework.boot.WebApplicationType.class))
            .main(source);

        builder.properties(defaultProperties);

        if (ConfigKit.isDebugModel()) {
            log.debug("全部的默认配置:\n{}", GsonUtils.toJson(defaultProperties, true));
        }

        propertySources.addLast(new MapPropertySource(DefaultEnvironment.DEFAULT_PROPERTIES_PROPERTY_SOURCE_NAME,
            Tools.getMapFromProperties(defaultProperties)));

        return builder;
    }

    /**
     * 构建默认的应用属性配置
     *
     * 设置应用的基本信息，包括：
     * - 版本信息
     * - 组ID
     * - 构件ID
     * - 应用名称
     * - 包名等
     *
     * @param appName 应用名称
     * @return 包含默认配置的 Properties 对象
     * @since 1.0.0
     */
    @NotNull
    private static Properties buildDefaultProperties(String appName) {
        Properties defaultProperties = new Properties();
        defaultProperties.setProperty(ConfigKey.POM_INFO_VERSION,
            MAIN_PROPERTIES.getProperty("version",
                System.getProperty(ConfigKey.SERVICE_VERSION)));
        defaultProperties.setProperty(ConfigKey.POM_INFO_GROUPID,
            MAIN_PROPERTIES.getProperty("groupId", App.BASE_PACKAGES));
        defaultProperties.setProperty(ConfigKey.POM_INFO_ARTIFACTID,
            appName);
        defaultProperties.setProperty(ConfigKey.SERVICE_VERSION,
            MAIN_PROPERTIES.getProperty("version",
                System.getProperty(ConfigKey.SERVICE_VERSION)));

        // 设置默认应用名, 可以通过环境变量修改或者是配置文件修改
        defaultProperties.setProperty(ConfigKey.SpringConfigKey.APPLICATION_NAME, appName);
        // 设置 package.name, 用于替换配置文件中的 ${package.name}
        defaultProperties.setProperty(ConfigKey.SpringConfigKey.PACKAGE_NAME, appName);
        defaultProperties.putAll(MAIN_PROPERTIES);
        return defaultProperties;
    }

    /**
     * 加载主要配置属性，并确定应用名称
     *
     * 应用名称获取逻辑：
     * 1. 如果是通过 jar 启动应用，从 MANIFEST.MF 或 build-info.properties 中获取 artifactId
     * 2. 如果是在 IDE 中运行，从配置文件中读取 spring.application.name
     * 3. 如果以上都为空，则解析 classpath 路径，使用当前目录名
     *
     * 注意: applicationName 规定使用 maven 中的 artifactId，日志文件保存路径也会使用到 artifactId
     * 配置链: maven.artifactId --> spring.application.name --> 日志路径
     *
     * @return 包含应用主要配置的 Properties 对象
     * @since 1.0.0
     */
    @SuppressWarnings("D")
    @NotNull
    private static Properties loadMainProperties() {
        log.info("App NameSpace: [{}], 如果不正确请设置 JVM 变量/系统环境变量: 「user.namespace」或: [ZEKA_NAME_SPACE]", App.ZEKA_NAME_SPACE);
        log.info("本地开发时, 默认为 local 环境, 如果需要连接 dev 或 test 环境, 请修改 [arco-maven-plugin/profile/spring.profiles.active] 配置");
        // 获取配置文件路径, 有多种情况 (本地运行, junit 运行, jar 运行)
        String configFilePath = ConfigKit.getConfigPath();
        String startType = System.getProperty(App.START_TYPE);
        String applicationName;
        String version = StringPool.NULL_STRING;
        Properties properties = new Properties();
        // shell 脚本启动, 优先从 jar 的 MANIFEST.MF 读取
        if (StringUtils.isNotBlank(startType) && (startType.equals(App.START_SHELL) || startType.equals(App.START_DOCKER))) {
            // 优先解析 jar 文件中的 MANIFEST.MF 文件, jar.file 环境变量通过 launcher 启动脚本设置
            try (JarFile jarFile = new JarFile(System.getProperty("jar.file"))) {
                Manifest manifest = jarFile.getManifest();
                log.info("MANIFEST.MF Info:");
                manifest.getMainAttributes().forEach((k, v) -> log.info("[{}:{}]", k, v));
                applicationName = manifest.getMainAttributes().getValue("Project-Name");
                version = manifest.getMainAttributes().getValue("Implementation-Version");
            } catch (Exception e) {
                // 如果在 IDE 中指定 start.type=shell 时(本地模拟部署时使用), 则会抛出 NPE 异常, 这里就会解析 build-info.properties 文件
                try (val fileInputStream = new FileInputStream(configFilePath + App.APP_BULID_INFO_FILE_NAME)) {
                    properties.load(fileInputStream);
                    applicationName = properties.getProperty("build.project.name");
                    version = properties.getProperty("build.version");
                } catch (Exception ex) {
                    log.warn("Launcher the app via shell, but not find build-info.properties. Analysis from jar");
                    // 抛异常是因为没有 build-info.properties 文件, 这时则通过解析 jar 来获取默认应用名
                    String startPath = Objects.requireNonNull(ZekaApplication.class.getResource(StringPool.SLASH))
                        .getPath()
                        .split("!")[0];
                    // 非 jar 启动
                    File file = new File(startPath);
                    applicationName = file.getParentFile().getParentFile().getName();
                }
            }
        } else {
            Object name = null;
            try {
                // 检查配置文件是否配置相关属性
                PropertySource<?> propertySource;
                SpringApplicationType type = SpringApplicationType.deduceFromClasspath();
                if (type == SpringApplicationType.CLOUD) {
                    // 如果是 cloud 应用, 解析 bootstrap.yml
                    propertySource = ConfigKit.getPropertySource(ConfigKit.CLOUD_CONFIG_FILE_NAME);
                } else {
                    // 如果是 boot 应用, 解析 application.yml
                    propertySource = ConfigKit.getPropertySource(ConfigKit.BOOT_CONFIG_FILE_NAME);
                }
                name = propertySource.getProperty(ConfigKey.SpringConfigKey.APPLICATION_NAME);

                deprecatedPropertiesCheck(propertySource);
            } catch (Exception ignored) {
                // nothing to do.
            }

            if (name != null && !name.equals(StringPool.DOLLAR_LEFT_BRACE
                + ConfigKey.SpringConfigKey.PACKAGE_NAME
                + StringPool.RIGHT_BRACE)) {
                applicationName = name.toString();
            } else {
                File file = new File(configFilePath);
                // 直接解析文件目录, 使用当前目录名作为应用名 (target 上一级目录)
                applicationName = file.getParentFile().getParentFile().getName();
                log.debug("未显式设置 application name 或者未正确解析 ${package.name} (可能需要重新编译项目), 读取当前模块名作为应用名: [{}]",
                    applicationName);
                if (StringUtils.isBlank(startType)) {
                    // 如果 startType 为 null, 则是从 IDE 中启动
                    System.setProperty(App.START_TYPE, App.START_IDEA);
                }

                File buildInfoFile = new File(file, "META-INF/" + App.APP_BULID_INFO_FILE_NAME);
                if (buildInfoFile.exists()) {
                    try (val fileInputStream = new FileInputStream(buildInfoFile)) {
                        properties.load(fileInputStream);
                        version = properties.getProperty("build.version");
                    } catch (Exception ignore) {
                        // nothing to do.
                    }
                }
            }
        }

        // 环境变量是最高级别
        applicationName = System.getProperty(ConfigKey.SpringConfigKey.APPLICATION_NAME, applicationName);
        properties.put(ConfigKey.SpringConfigKey.APPLICATION_NAME, applicationName);
        // 设置当前的应用版本, 在 banner 中输出
        System.setProperty(ConfigKey.SERVICE_VERSION, App.FRAMEWORK_VERSION_PREFIX + version);
        return properties;
    }

    /**
     * 检查配置中是否存在已过时的配置项
     *
     * 当检测到过时配置时，会输出警告日志提示用户更新配置
     *
     * @param propertySource 配置源对象
     * @since 1.0.0
     */
    private static void deprecatedPropertiesCheck(@NotNull PropertySource<?> propertySource) {
        Object property = propertySource.getProperty(ConfigKey.SpringConfigKey.PROFILE_ACTIVE);
        if (ObjectUtils.isNotNull(property)) {
            log.debug("1.4.0+ 开始不需要在配置文件 (bootstrap.yml/application.yml) 中配置 spring.profiles.active 属性, 请删除此配置以修复错误.");
        }
    }
}
