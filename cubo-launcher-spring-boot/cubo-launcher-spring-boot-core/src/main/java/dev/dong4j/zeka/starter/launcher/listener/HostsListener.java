package dev.dong4j.zeka.starter.launcher.listener;

import dev.dong4j.zeka.kernel.common.ZekaApplicationListener;
import dev.dong4j.zeka.kernel.common.constant.ConfigKey;
import dev.dong4j.zeka.kernel.common.util.ConfigKit;
import dev.dong4j.zeka.kernel.common.util.HostUtils;
import dev.dong4j.zeka.kernel.common.util.ThreadUtils;
import dev.dong4j.zeka.starter.launcher.exception.LauncherException;
import java.io.InputStream;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.Resource;

/**
 * Hosts 配置监听器，用于本地开发环境自动更新 hosts 文件
 *
 * 该监听器在本地开发环境启动时，自动读取预定义的 hosts 配置文件，
 * 并将其中的配置项写入/更新到开发者本地的 hosts 文件中，简化开发环境配置。
 *
 * 配置文件查找顺序：
 * 1. 优先查找 includes/hosts.properties
 * 2. 如果未找到，则使用 dns-default/hosts.properties
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.11.12 16:05
 * @since 1.0.0
 * @deprecated 从 2022.1.1 开始不再更新开发者本地的 hosts 文件，开发者需自行配置
 */
@Slf4j
@Deprecated
public class HostsListener implements ZekaApplicationListener {

    /**
     * 获取监听器执行优先级
     *
     * 返回最低优先级，确保 hosts 配置在其他所有组件初始化后才进行更新，
     * 避免影响其他组件的初始化过程。
     *
     * @return 监听器的执行优先级
     * @since 1.0.0
     */
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    /**
     * 处理应用环境准备事件
     *
     * 当 Spring 环境准备完成后，从环境中获取当前激活的配置文件(profile)，
     * 并根据该配置加载对应的 hosts 配置。
     * 使用 executeAtFirst 确保该操作只执行一次。
     *
     * @param event Spring 环境准备完成事件
     * @since 1.0.0
     */
    @Override
    public void onApplicationEnvironmentPreparedEvent(@NotNull ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment environment = event.getEnvironment();
        Runner.executeAtFirst(this.key(event, this.getClass()),
            () -> this.load(environment.getProperty(ConfigKey.SpringConfigKey.PROFILE_ACTIVE)));
    }


    /**
     * 加载 hosts 配置
     *
     * 根据当前环境配置加载对应的 hosts 文件。
     * 只在本地开发环境下执行，生产环境不会修改 hosts 文件。
     *
     * @param profile 当前激活的环境配置
     * @since 1.0.0
     */
    private void load(String profile) {
        if (ConfigKit.isLocalLaunch()) {
            try {
                this.loadHosts("includes/hosts.properties");
            } catch (LauncherException e) {
                this.loadHosts("dns-default/hosts.properties");
            }
        }
    }

    /**
     * 加载指定的 hosts 配置文件
     *
     * 查找顺序：
     * 1. 先尝试从本地文件系统加载（开发环境在 target 目录下，部署环境在 config 目录下）
     * 2. 如果本地文件不存在，则从 jar 包中加载
     * 3. 如果都找不到，则抛出异常
     *
     * @param propertiesFileName hosts 配置文件名
     * @since 1.0.0
     */
    private void loadHosts(String propertiesFileName) {
        InputStream inputStream;
        try {
            // 先加载本地文件, 开发环境在 target 目录下, 服务器部署环境在 config 目录下
            Resource resource = ConfigKit.getResource(propertiesFileName);
            inputStream = resource.getInputStream();
        } catch (Exception e) {
            // 从 jar 中加载
            inputStream = HostsListener.class.getClassLoader().getResourceAsStream(propertiesFileName);
        }

        if (inputStream == null) {
            throw new LauncherException("Fail to find " + propertiesFileName + " on classpath!");
        }

        try {
            Properties properties = new Properties();
            properties.load(inputStream);
            inputStream.close();
            ThreadUtils.submit(() -> writeToHosts(properties));
        } catch (Exception e) {
            throw new LauncherException(String.format("Fail to loadDnsCacheConfig from %s, cause: %s", propertiesFileName, e), e);
        }
    }

    /**
     * 将 hosts 配置写入到本地 hosts 文件
     *
     * 遍历配置项，将每一项写入或更新到本地 hosts 文件中。
     * 如果本地 hosts 文件中已存在相同域名的记录，则会更新为新的 IP 地址。
     * 该操作在后台线程中异步执行，不会阻塞应用启动。
     *
     * @param cacheProperties 包含 hosts 配置的 Properties 对象，键为域名，值为 IP 地址
     * @since 1.0.0
     */
    private static void writeToHosts(Properties cacheProperties) {
        cacheProperties.forEach((k, v) -> HostUtils.updateHost(String.valueOf(v), String.valueOf(k)));
    }

}
