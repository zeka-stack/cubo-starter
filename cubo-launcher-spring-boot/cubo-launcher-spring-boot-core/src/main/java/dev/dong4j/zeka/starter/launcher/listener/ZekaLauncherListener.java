package dev.dong4j.zeka.starter.launcher.listener;

import dev.dong4j.zeka.kernel.common.ZekaApplicationListener;
import dev.dong4j.zeka.kernel.common.constant.App;
import dev.dong4j.zeka.kernel.common.constant.BasicConstant;
import dev.dong4j.zeka.kernel.common.constant.ConfigKey;
import dev.dong4j.zeka.kernel.common.context.SpringContext;
import dev.dong4j.zeka.kernel.common.util.ConfigKit;
import dev.dong4j.zeka.kernel.common.util.StringUtils;
import dev.dong4j.zeka.kernel.common.util.ThreadUtils;
import dev.dong4j.zeka.processor.annotation.AutoListener;
import dev.dong4j.zeka.starter.launcher.banner.BannerPrinter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.Banner;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

/**
 * Zeka 框架核心启动监听器，负责应用启动全生命周期的处理
 *
 * 该监听器处理应用启动的各个阶段，包括：
 * 1. 应用启动前的检查和准备
 * 2. 环境配置加载和 Banner 显示
 * 3. Web 服务器初始化后的端口配置
 * 4. 应用启动完成后的资源初始化
 * 5. 应用关闭时的清理工作
 *
 * 通过 @AutoListener 注解自动注册到 Spring 容器中。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.20 19:06
 * @since 1.0.0
 */
@Slf4j
@AutoListener
public class ZekaLauncherListener implements ZekaApplicationListener {

    /**
     * 获取监听器执行优先级
     *
     * 设置为较高优先级（仅次于最高优先级21个位置），确保在核心配置加载后、
     * 但在大多数应用组件初始化前执行，以便正确设置环境和显示 Banner。
     *
     * @return 监听器的执行优先级
     * @since 1.0.0
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 21;
    }

    /**
     * 处理应用启动事件
     *
     * 在应用启动最早阶段执行，主要完成：
     * 1. 关闭 Spring Boot 默认的 Banner 显示
     * 2. 检查应用是否通过 ZekaApplication 启动
     * 3. 如果不是，输出警告信息和正确的使用示例
     *
     * @param event 应用启动事件
     * @since 1.0.0
     */
    @Override
    public void onApplicationStartingEvent(@NotNull ApplicationStartingEvent event) {
        ZekaApplicationListener.Runner.executeAtFirst(this.key(event, this.getClass()), () -> {
            // 关闭默认的 banner
            event.getSpringApplication().setBannerMode(Banner.Mode.OFF);
            if (StringUtils.isBlank(System.getProperty(App.START_APPLICATION))
                && !App.START_JUNIT.equals(System.getProperty(App.START_TYPE))) {
                log.warn("请使用 ZekaApplication 启动或者继承 ZekaStarter, 用于加载组件默认配置\n\n"
                    + "Sample: \n"
                    + "@SpringBootApplication\n"
                    + "public class DemoApplication {\n"
                    + "    public static void main(String[] args) {\n"
                    + "        ZekaApplication.run(DemoApplication.class);\n"
                    + "        // ZekaApplication.run(\"applicationName\",DemoApplication.class);\n"
                    + "    }\n"
                    + "}\n"
                    + "or simple: \n"
                    + "@SpringBootApplication\n"
                    + "public class DemoApplication extends ZekaStarter {\n"
                    + "    \n"
                    + "}");
            }
        });
    }

    /**
     * 处理应用环境准备事件
     *
     * 当 Spring 环境准备完成后执行，主要完成：
     * 1. 设置应用类型到系统属性
     * 2. 初始化配置工具类
     * 3. 根据配置决定是否显示自定义 Banner
     * 4. 输出应用启动信息
     *
     * @param event Spring 环境准备完成事件
     * @since 1.0.0
     */
    @Override
    public void onApplicationEnvironmentPreparedEvent(@NotNull ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment environment = event.getEnvironment();
        System.setProperty(App.APP_TYPE, event.getSpringApplication().getWebApplicationType().name());
        // 只有初始化之后, 才能使用此类(在 Spring Boot 的环境未准备好之前, 只能使用此类从 Java 环境变量中获取信息)
        ConfigKit.init(environment);
        if (!Boolean.parseBoolean(environment.getProperty(ConfigKey.ENABLE_BANNER))) {
            // 打印 banner
            ZekaApplicationListener.Runner.executeAtFirst(this.key(event, this.getClass()), () -> {
                log.info("application starting.... see more information: {}", ConfigKit.getProperty(ConfigKey.WIKI));
                new BannerPrinter().print();
            });
        }
    }

    /**
     * 处理 Web 服务器初始化事件
     *
     * 当 Web 服务器初始化完成后执行，主要完成：
     * 1. 获取 Web 应用的实际端口号
     * 2. 将端口号设置到系统环境变量中，便于其他组件获取
     * 3. 记录应用名称、端口、上下文路径和环境信息
     *
     * 特别适用于随机端口场景，确保能获取到实际分配的端口号。
     *
     * @param event Web 服务器初始化事件
     * @see dev.dong4j.zeka.kernel.common.util.StartUtils
     * @since 1.0.0
     */
    @Override
    public void onWebServerInitializedEvent(@NotNull WebServerInitializedEvent event) {
        ZekaApplicationListener.Runner.executeAtLast(this.key(event, this.getClass()), () -> {
            Environment environment = event.getApplicationContext().getEnvironment();
            String appName = ConfigKit.getAppName();
            int localPort = event.getWebServer().getPort();
            // 将当前端口设置到环境变量, 便于其他地方获取真实的端口 (junit 或者端口随机时) todo-dong4j : (2021.12.5 17:40) [Nacos 环境下失效]
            System.setProperty(ConfigKey.SpringConfigKey.SERVER_PORT, localPort + "");
            String profile = StringUtils.arrayToCommaDelimitedString(environment.getActiveProfiles());
            log.debug("appName = [{}] localPort = [{}] context-path = [{}] profile = [{}]",
                appName, localPort, ConfigKit.getContextPath(),
                profile);
        });
    }

    /**
     * 处理应用上下文关闭事件
     *
     * 当应用关闭时执行，记录应用关闭的日志信息，
     * 使用 executeAtLast 确保该操作在所有关闭处理的最后执行。
     *
     * @param event 上下文关闭事件
     * @since 1.0.0
     */
    @Override
    @SuppressWarnings("PMD.UndefineMagicConstantRule")
    public void onContextClosedEvent(@NotNull ContextClosedEvent event) {
        ZekaApplicationListener.Runner.executeAtLast(this.key(event, this.getClass()),
            () -> log.info("[{}] is closed", event.getApplicationContext()));
    }

    /**
     * 处理应用启动完成事件
     *
     * 当应用完全启动后执行，主要完成：
     * 1. 记录应用上下文的详细信息（用于调试）
     * 2. 设置线程执行器，用于后续的异步任务处理
     *
     * 使用 executeAtLast 确保该操作在所有启动处理的最后执行。
     *
     * @param event 应用启动完成事件
     * @since 1.0.0
     */
    @Override
    public void onApplicationStartedEvent(@NotNull ApplicationStartedEvent event) {
        ZekaApplicationListener.Runner.executeAtLast(this.key(event, this.getClass()), () -> {
            ConfigurableApplicationContext applicationContext = event.getApplicationContext();
            log.trace("[{}]", applicationContext);
            log.trace("[{}]", applicationContext.getParent());
            log.trace("[{}]", applicationContext.getApplicationName());
            log.trace("[{}]", applicationContext.getId());
            try {
                ThreadUtils.setExecutor(SpringContext.getInstance(BasicConstant.BOOST_EXECUTORSERVICE));
            } catch (Throwable ignored) {
            }

        });
    }
}
