package dev.dong4j.zeka.starter.launcher.event;

import dev.dong4j.zeka.kernel.common.event.BaseEvent;

/**
 * 启动器事件类，用于封装启动过程中触发的事件
 *
 * 该类继承自 BaseEvent，提供了启动相关事件的基类实现。
 * 主要用于在应用启动过程中发布和监听各种状态变更事件。
 *
 * 典型使用场景：
 * 1. 启动前准备事件
 * 2. 启动完成事件
 * 3. 配置刷新事件
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 18:25
 * @since 1.0.0
 */
public class LauncherEvent extends BaseEvent<Object> {
    /** serialVersionUID */
    private static final long serialVersionUID = -8490953221049981401L;

    /**
     * 构造方法，创建启动器事件实例
     *
     * @param source 事件源对象，通常是触发事件的组件
     * @since 1.0.0
     */
    public LauncherEvent(Object source) {
        super(source);
    }
}
