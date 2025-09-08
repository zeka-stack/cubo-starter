package dev.dong4j.zeka.starter.launcher.listener;

import dev.dong4j.zeka.kernel.common.ZekaApplicationListener;
import dev.dong4j.zeka.kernel.common.constant.ConfigKey;
import dev.dong4j.zeka.kernel.common.dns.internal.InetAddressCacheUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * DNS 缓存配置监听器，负责在应用启动时加载 DNS 配置
 *
 * 该监听器在配置初始化完成后立即加载 DNS 缓存配置，优先级设置为最高，
 * 确保在连接远程服务（如 Nacos）前 DNS 配置已正确加载。
 *
 * 注意：该类已被标记为过时，计划在未来版本中使用不依赖反射的方式重构，
 * 以提高与 JDK8+ 的兼容性。
 *
 * todo-dong4j : (2025.06.22 18:37) [不使用反射, 兼容 JDK8+]
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.06.09 18:14
 * @since 1.0.0
 */
@Deprecated
public class DnsCacheListener implements ZekaApplicationListener {
    /**
     * 获取监听器执行优先级
     *
     * 返回最高优先级，确保 DNS 配置在所有其他组件初始化前完成加载，
     * 避免网络连接问题。
     *
     * @return 监听器的执行优先级
     * @since 1.0.0
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    /**
     * 处理应用环境准备事件
     *
     * 当 Spring 环境准备完成后，从环境中获取当前激活的配置文件(profile)，
     * 并根据该配置加载对应的 DNS 缓存配置。
     * 使用 executeAtFirst 确保该操作只执行一次。
     *
     * @param event Spring 环境准备完成事件
     * @since 1.0.0
     */
    @Override
    public void onApplicationEnvironmentPreparedEvent(@NotNull ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment environment = event.getEnvironment();
        ZekaApplicationListener.Runner.executeAtFirst(
            this.key(event, this.getClass()),
            () -> InetAddressCacheUtils.loadDnsProperties(environment.getProperty(ConfigKey.SpringConfigKey.PROFILE_ACTIVE)));
    }

}
