package dev.dong4j.zeka.starter.launcher.listener;

import dev.dong4j.zeka.kernel.common.ZekaApplicationListener;
import dev.dong4j.zeka.processor.annotation.AutoListener;
import dev.dong4j.zeka.starter.launcher.env.RangeRandomValuePropertySource;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.core.Ordered;

/**
 * 范围随机值配置监听器，支持在配置中使用范围随机值
 *
 * 该监听器允许在配置文件中使用范围随机值表达式，特别适用于在开发环境中
 * 为应用分配随机端口，避免端口冲突问题。
 *
 * 使用方式：
 * 在配置文件中通过 ${range.random.int(min, max)} 语法获取指定范围内的随机整数
 * 例如：server.port=${range.random.int(1111, 2222)}
 *
 * 注意：每次通过表达式获取值时都会生成新的随机数，如需在多处使用相同的随机值，
 * 应先将其赋值给一个属性，然后引用该属性。
 *
 * @author dongj4
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.23 14:18
 * @since 1.0.0
 */
@AutoListener
public class RangeRandomPortListener implements ZekaApplicationListener {

    /** DEFAULT_ORDER */
    private static final int DEFAULT_ORDER = Ordered.HIGHEST_PRECEDENCE + 9;

    /**
     * 获取监听器执行优先级
     *
     * 设置为较高优先级（仅次于最高优先级9个位置），确保在大多数配置处理前
     * 随机值属性源已被添加到环境中，使其他配置能够引用这些随机值。
     *
     * @return 监听器的执行优先级
     * @since 1.0.0
     */
    @Override
    public int getOrder() {
        return DEFAULT_ORDER;
    }

    /**
     * 处理应用环境准备事件
     *
     * 当 Spring 环境准备完成后，向环境中添加范围随机值属性源，
     * 使配置文件中的 ${range.random.xxx} 表达式能够被正确解析。
     * 使用 executeAtFirst 确保该操作只执行一次。
     *
     * @param event Spring 环境准备完成事件
     * @since 1.0.0
     */
    @Override
    public void onApplicationEnvironmentPreparedEvent(@NotNull ApplicationEnvironmentPreparedEvent event) {
        ZekaApplicationListener.Runner.executeAtFirst(this.key(event, this.getClass()),
            () -> RangeRandomValuePropertySource.addToEnvironment(event.getEnvironment()));
    }
}
