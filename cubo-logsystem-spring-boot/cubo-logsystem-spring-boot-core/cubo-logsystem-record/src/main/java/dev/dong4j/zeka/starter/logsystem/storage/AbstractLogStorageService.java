package dev.dong4j.zeka.starter.logsystem.storage;

import dev.dong4j.zeka.starter.logsystem.entity.ApiLog;
import dev.dong4j.zeka.starter.logsystem.entity.ErrorLog;
import dev.dong4j.zeka.starter.logsystem.entity.SystemLog;

/**
 * 抽象日志存储服务适配器
 *
 * 该类是日志存储服务的抽象适配器，实现了LogStorageService接口。
 * 为具体的日志存储实现提供默认的空实现，子类可以根据需要重写相应方法。
 *
 * 主要功能包括：
 * 1. 提供日志存储服务接口的默认实现
 * 2. 支持系统日志、API日志、错误日志的存储
 * 3. 为子类提供扩展基础
 * 4. 简化日志存储服务的开发
 *
 * 使用场景：
 * - 作为日志存储服务的基类
 * - 提供默认的空实现
 * - 简化具体存储服务的开发
 * - 支持日志存储的标准化处理
 *
 * 设计意图：
 * 通过抽象适配器提供日志存储服务的默认实现，简化具体存储服务的开发，
 * 支持日志存储的标准化和扩展。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.20 21:17
 * @since 1.0.0
 */
public abstract class AbstractLogStorageService implements LogStorageService {
    /**
     * 保存系统日志
     *
     * 默认实现返回true，表示日志保存成功。
     * 子类应该重写此方法来实现具体的系统日志存储逻辑。
     *
     * @param log 系统日志实体对象
     * @return 保存结果，true表示成功，false表示失败
     * @since 1.0.0
     */
    @Override
    public Boolean save(SystemLog log) {
        return true;
    }

    /**
     * 保存API日志
     *
     * 默认实现返回true，表示日志保存成功。
     * 子类应该重写此方法来实现具体的API日志存储逻辑。
     *
     * @param log API日志实体对象
     * @return 保存结果，true表示成功，false表示失败
     * @since 1.0.0
     */
    @Override
    public Boolean save(ApiLog log) {
        return true;
    }

    /**
     * 保存错误日志
     *
     * 默认实现返回true，表示日志保存成功。
     * 子类应该重写此方法来实现具体的错误日志存储逻辑。
     *
     * @param log 错误日志实体对象
     * @return 保存结果，true表示成功，false表示失败
     * @since 1.0.0
     */
    @Override
    public Boolean save(ErrorLog log) {
        return true;
    }
}
