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
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.20 19:06
 * @since 1.0.0
 */
@Slf4j
@AutoListener
public class ZekaLauncherListener implements ZekaApplicationListener {

    /**
     * Gets order *
     *
     * @return the order
     * @since 1.0.0
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 21;
    }

    /**
     * 启动类检查
     *
     * @param event the event
     * @since 1.0.0
     */
    @Override
    public void onApplicationStartingEvent(@NotNull ApplicationStartingEvent event) {
        ZekaApplicationListener.Runner.executeAtFirst(this.key(event, this.getClass()), () -> {
            // 关闭默认的 banner
            event.getSpringApplication().setBannerMode(Banner.Mode.OFF);
            if (StringUtils.isBlank(System.getProperty(App.START_ZEKA_APPLICATION))
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
     * On application environment prepared event *
     *
     * @param event event
     * @since 1.0.0
     */
    @Override
    public void onApplicationEnvironmentPreparedEvent(@NotNull ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment environment = event.getEnvironment();
        System.setProperty(App.APP_TYPE, event.getSpringApplication().getWebApplicationType().name());
        // 只有初始化之后, 才能使用此类(在 Spring Boot 的环境未准备好之前, 只能使用此类从 Java 环境变量中获取信息)
        ConfigKit.init(environment);
        if (!Boolean.parseBoolean(environment.getProperty(ConfigKey.ZEKA_ENABLE_BANNER))) {
            // 打印 banner
            ZekaApplicationListener.Runner.executeAtFirst(this.key(event, this.getClass()), () -> {
                log.info("application starting.... see more information: https://zeka.dong4j.dev");
                new BannerPrinter().print();
            });
        }
    }

    /**
     * 应用 web 相关初始化完成后, 获取 web 应用的端口, 设置到环境变量中, 用于在启动完成后输出端口信息
     *
     * @param event event
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
     * On context closed event *
     *
     * @param event event
     * @since 1.0.0
     */
    @Override
    @SuppressWarnings("PMD.UndefineMagicConstantRule")
    public void onContextClosedEvent(@NotNull ContextClosedEvent event) {
        ZekaApplicationListener.Runner.executeAtLast(this.key(event, this.getClass()),
            () -> log.info("[{}] is closed", event.getApplicationContext()));
    }

    /**
     * On context started event *
     *
     * @param event event
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
