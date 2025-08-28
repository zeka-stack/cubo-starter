package dev.dong4j.zeka.starter.launcher.listener;

import dev.dong4j.zeka.kernel.common.ZekaApplicationListener;
import dev.dong4j.zeka.processor.annotation.AutoListener;
import dev.dong4j.zeka.starter.launcher.env.RangeRandomValuePropertySource;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.core.Ordered;

/**
 * <p>Description: range.random 配置监听器 </p>
 * 注意: 每次通过 key 去获取随机端口 ${range.random.int(1111, 2222)}
 *
 * @author dongj4
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.23 14:18
 * @since 1.0.0
 */
@AutoListener
public class RangeRandomPortListener implements ZekaApplicationListener {

    /** DEFAULT_ORDER */
    private static final int DEFAULT_ORDER = Ordered.HIGHEST_PRECEDENCE + 9;

    /**
     * Gets order *
     *
     * @return the order
     * @since 1.0.0
     */
    @Override
    public int getOrder() {
        return DEFAULT_ORDER;
    }

    /**
     * 在应用读取完所有配置之后处理
     *
     * @param event event
     * @since 1.0.0
     */
    @Override
    public void onApplicationEnvironmentPreparedEvent(@NotNull ApplicationEnvironmentPreparedEvent event) {
        ZekaApplicationListener.Runner.executeAtFirst(this.key(event, this.getClass()),
            () -> RangeRandomValuePropertySource.addToEnvironment(event.getEnvironment()));
    }
}
