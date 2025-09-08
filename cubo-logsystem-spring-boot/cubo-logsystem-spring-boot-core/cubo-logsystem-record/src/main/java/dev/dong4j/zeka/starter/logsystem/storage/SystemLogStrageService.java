package dev.dong4j.zeka.starter.logsystem.storage;

import dev.dong4j.zeka.starter.logsystem.entity.AbstractLog;
import dev.dong4j.zeka.starter.logsystem.entity.SystemLog;

/**
 * 系统日志存储服务实现
 *
 * 该类是系统日志存储服务的具体实现，实现了ILogStorage接口。
 * 通过委托给LogStorageService来处理系统日志的存储操作。
 *
 * 主要功能包括：
 * 1. 实现系统日志的存储操作
 * 2. 委托给LogStorageService处理具体存储逻辑
 * 3. 提供系统日志存储的统一接口
 * 4. 支持系统日志的标准化存储
 *
 * 使用场景：
 * - 系统日志的存储操作
 * - 系统操作审计日志的记录
 * - 系统日志存储的统一管理
 * - 系统日志存储的标准化处理
 *
 * 设计意图：
 * 通过具体的实现类提供系统日志存储功能，委托给LogStorageService处理具体逻辑，
 * 提供系统日志存储的标准化和统一管理。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.21 10:39
 * @since 1.0.0
 */
public class SystemLogStrageService implements ILogStorage {

    /** Log storage service */
    private LogStorageService logStorageService;

    /**
     * 保存系统日志
     *
     * 将系统日志实体保存到存储介质中，委托给LogStorageService处理具体存储逻辑。
     *
     * @param log 系统日志实体对象
     * @return 保存结果，true表示成功，false表示失败
     * @since 1.0.0
     */
    public boolean save(SystemLog log) {
        return this.logStorageService.save(log);
    }

    /**
     * 保存抽象日志实体
     *
     * 将抽象日志实体保存到存储介质中，先转换为SystemLog类型再委托给LogStorageService处理。
     * 此方法实现了ILogStorage接口的save方法。
     *
     * @param log 抽象日志实体对象
     * @return 保存结果，true表示成功，false表示失败
     * @since 1.0.0
     */
    @Override
    public boolean save(AbstractLog log) {
        return this.logStorageService.save((SystemLog) log);
    }
}
