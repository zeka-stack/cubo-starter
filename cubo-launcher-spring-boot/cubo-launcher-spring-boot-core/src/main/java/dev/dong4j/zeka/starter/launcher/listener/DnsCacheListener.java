package dev.dong4j.zeka.starter.launcher.listener;

import dev.dong4j.zeka.kernel.common.ZekaApplicationListener;
import dev.dong4j.zeka.kernel.common.constant.ConfigKey;
import dev.dong4j.zeka.kernel.common.dns.internal.InetAddressCacheUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * <p>Description: 在配置初始化完成后加载 dns 配置, 优先级必须设置为最高, 否则连接不了 Nacos </p>
 * todo-dong4j : (2025.06.22 18:37) [不使用反射, 兼容 JDK8+]
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.06.09 18:14
 * @since 1.5.0
 */
public class DnsCacheListener implements ZekaApplicationListener {
    /**
     * 优先级最高
     *
     * @return the order
     * @since 1.5.0
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    /**
     * On application environment prepared event
     *
     * @param event event
     * @since 1.5.0
     */
    @Override
    public void onApplicationEnvironmentPreparedEvent(@NotNull ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment environment = event.getEnvironment();
        ZekaApplicationListener.Runner.executeAtFirst(
            this.key(event, this.getClass()),
            () -> InetAddressCacheUtils.loadDnsProperties(environment.getProperty(ConfigKey.SpringConfigKey.PROFILE_ACTIVE)));
    }

}
