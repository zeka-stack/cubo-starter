package dev.dong4j.zeka.starter.logsystem.factory;

import dev.dong4j.zeka.starter.logsystem.entity.ApiLog;
import dev.dong4j.zeka.starter.logsystem.entity.ErrorLog;
import dev.dong4j.zeka.starter.logsystem.entity.SystemLog;
import dev.dong4j.zeka.starter.logsystem.storage.ILogStorage;
import dev.dong4j.zeka.starter.logsystem.storage.LogStorageService;

/**
 * 日志存储工厂适配器
 *
 * 该类是日志存储工厂的抽象适配器，实现了LogStorageFactory接口。
 * 为具体的日志存储工厂实现提供默认的空实现，子类可以根据需要重写相应方法。
 *
 * 主要功能包括：
 * 1. 提供日志存储工厂接口的默认实现
 * 2. 支持获取不同类型的日志存储服务
 * 3. 为子类提供扩展基础
 * 4. 简化日志存储工厂的开发
 *
 * 使用场景：
 * - 作为日志存储工厂的基类
 * - 提供默认的空实现
 * - 简化具体工厂类的开发
 * - 支持日志存储工厂的标准化处理
 *
 * 设计意图：
 * 通过抽象适配器提供日志存储工厂的默认实现，简化具体工厂类的开发，
 * 支持日志存储工厂的标准化和扩展。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.20 17:36
 * @since 1.0.0
 */
public abstract class LogStorageFactoryAdapter implements LogStorageFactory {

    /**
     * 获取日志存储服务
     *
     * 默认实现返回null，子类应该重写此方法来实现具体的日志存储服务获取逻辑。
     *
     * @return 日志存储服务实例，如果未实现则返回null
     * @since 1.0.0
     */
    @Override
    public LogStorageService getLogStorageService() {
        return null;
    }

    /**
     * 获取系统日志存储服务
     *
     * 默认实现返回null，子类应该重写此方法来实现具体的系统日志存储服务获取逻辑。
     *
     * @return 系统日志存储服务实例，如果未实现则返回null
     * @since 1.0.0
     */
    @Override
    public ILogStorage<SystemLog> getSystemLogStorage() {
        return null;
    }

    /**
     * 获取错误日志存储服务
     *
     * 默认实现返回null，子类应该重写此方法来实现具体的错误日志存储服务获取逻辑。
     *
     * @return 错误日志存储服务实例，如果未实现则返回null
     * @since 1.0.0
     */
    @Override
    public ILogStorage<ErrorLog> getErrorLogStorage() {
        return null;
    }

    /**
     * 获取API日志存储服务
     *
     * 默认实现返回null，子类应该重写此方法来实现具体的API日志存储服务获取逻辑。
     *
     * @return API日志存储服务实例，如果未实现则返回null
     * @since 1.0.0
     */
    @Override
    public ILogStorage<ApiLog> getApiLogStorage() {
        return null;
    }
}
